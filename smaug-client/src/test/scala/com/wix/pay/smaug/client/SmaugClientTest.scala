/*      __ __ _____  __                                              *\
**     / // // /_/ |/ /          Wix                                 **
**    / // // / /|   /           (c) 2006-2017, Wix LTD.             **
**   / // // / //   |            http://www.wix.com/                 **
**   \__/|__/_//_/| |                                                **
\*                |/                                                 */
package com.wix.pay.smaug.client


import scala.util.{Failure, Success}
import java.net.URL
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope
import com.google.api.client.http.javanet.NetHttpTransport
import com.wix.pay.creditcard.{CreditCard, CreditCardOptionalFields, PublicCreditCard, YearMonth}
import com.wix.pay.smaug.client.model.{CreditCardToken, ErrorCodes, InTransitRequest, TokenizeRequest}
import com.wix.pay.smaug.client.testkit.SmaugDriver
import com.wix.restaurants.common.protocol.api.Error


class SmaugClientTest extends SpecWithJUnit {
  val cardsStoreBridgePort = 10001

  val someAccessToken = "some access token"
  val someTenantId = "some tenant ID"
  val someCard = CreditCard(
    number = "4111111111111111",
    expiration = YearMonth(
      year = 2020,
      month = 12))
  val someInTransitToken = CreditCardToken(
    token = "some in-transit token",
    creditCard = PublicCreditCard(someCard))
  val somePermanentCardToken = CreditCardToken(
    token = "some permanent token",
    creditCard = PublicCreditCard(someCard))

  val aTokenizeRequest: String => TokenizeRequest = tenantId => TokenizeRequest(someCard, tenantId)
  val someAdditionalCardInfo = Some(CreditCardOptionalFields.withFields(
    csc = Some("123")
  ))
  val anInTransitRequest: String => InTransitRequest = tenantId => InTransitRequest(
    permanentToken = somePermanentCardToken,
    additionalInfo = someAdditionalCardInfo,
    tenantId = tenantId)

  val cardsStoreBridge = new DefaultSmaugClient(
    requestFactory = new NetHttpTransport().createRequestFactory(),
    endpointUrl = s"http://localhost:$cardsStoreBridgePort")

  val driver = new SmaugDriver(port = cardsStoreBridgePort)

  val anInternalError: String => Error = message => Error(
      code = ErrorCodes.internal,
      description = message)


  step {
    driver.start()
  }


  sequential


  trait Ctx extends Scope {
    driver.reset()
  }

  "getting the card form URL" should {
    "return the URL for simple with no parameters" in new Ctx {
      val someUrl = new URL("https://www.example.org/someResource")
      driver.aFormUrl() redirectsTo someUrl

      cardsStoreBridge.formUrl() must be_===(Success(someUrl))
    }

    "return the URL for simple query" in new Ctx {
      val someSimpleParams = "someParams"
      val someUrl = new URL("https://www.example.org/someResource")
      driver.aFormUrl(Some(someSimpleParams)) redirectsTo someUrl

      cardsStoreBridge.formUrl(Some(someSimpleParams)) must be_===(Success(someUrl))
    }

    "return the URL for query with special characters" in new Ctx {
      val someParamsWithSpecialCharacters = "ab&wx=yz"
      val someUrl = new URL("https://www.example.org/someResource")
      driver.aFormUrl(Some(someParamsWithSpecialCharacters)) redirectsTo someUrl

      cardsStoreBridge.formUrl(Some(someParamsWithSpecialCharacters)) must be_===(Success(someUrl))
    }
  }

  "tokenizing a card" should {
    "return an in-transit card token on success" in new Ctx {
      driver.aTokenizeRequest(aTokenizeRequest(someTenantId)) returns someInTransitToken

      cardsStoreBridge.tokenize(card = someCard, tenantId = someTenantId) must be_===(Success(someInTransitToken))
    }

    "gracefully fail on error" in new Ctx {
      val someErrorMessage = "some error message"
      driver.aTokenizeRequest(aTokenizeRequest(someTenantId)) errors anInternalError(someErrorMessage)

      cardsStoreBridge.tokenize(card = someCard, tenantId = someTenantId) must be_===(Failure(
        SmaugInternalException(someErrorMessage)))
    }
  }


  "converting a permanent card token" should {
    "return an in-transit card token on success" in new Ctx {
      driver.anInTransitRequest(anInTransitRequest(someTenantId)) returns someInTransitToken

      cardsStoreBridge.inTransit(
        permanentToken = somePermanentCardToken,
        additionalInfo = someAdditionalCardInfo,
        tenantId = someTenantId) must be_===(Success(someInTransitToken))
    }

    "gracefully fail on error" in new Ctx {
      val someErrorMessage = "some error message"

      driver.anInTransitRequest(anInTransitRequest(someTenantId)) errors anInternalError(someErrorMessage)

      cardsStoreBridge.inTransit(
        permanentToken = somePermanentCardToken,
        additionalInfo = someAdditionalCardInfo,
        tenantId = someTenantId) must be_===(Failure(SmaugInternalException(someErrorMessage)))
    }
  }

  step {
    driver.stop()
  }
}
