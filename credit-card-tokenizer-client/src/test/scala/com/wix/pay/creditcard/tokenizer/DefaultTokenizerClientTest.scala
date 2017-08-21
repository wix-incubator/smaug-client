/*      __ __ _____  __                                              *\
**     / // // /_/ |/ /          Wix                                 **
**    / // // / /|   /           (c) 2006-2017, Wix LTD.             **
**   / // // / //   |            http://www.wix.com/                 **
**   \__/|__/_//_/| |                                                **
\*                |/                                                 */
package com.wix.pay.creditcard.tokenizer


import java.net.URL
import scala.util.{Failure, Success}
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope
import com.google.api.client.http.javanet.NetHttpTransport
import com.wix.pay.creditcard.tokenizer.model._
import com.wix.pay.creditcard.tokenizer.testkit.TokenizerDriver
import com.wix.pay.creditcard.{CreditCard, CreditCardOptionalFields, PublicCreditCard, YearMonth}
import com.wix.restaurants.common.protocol.api.Error


class DefaultTokenizerClientTest extends SpecWithJUnit {
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

  val aTokenizeRequest = TokenizeRequest(card = someCard)
  val aTokenizeRequestNG: String => TokenizeRequestNG = tenantId => TokenizeRequestNG(someCard, tenantId)
  val someAdditionalCardInfo = Some(CreditCardOptionalFields.withFields(
    csc = Some("123")
  ))
  val anInTransitRequest = InTransitRequest(
    permanentToken = somePermanentCardToken,
    additionalInfo = someAdditionalCardInfo)
  val anInTransitRequestNG: String => InTransitRequestNG = tenantId => InTransitRequestNG(
    permanentToken = somePermanentCardToken,
    additionalInfo = someAdditionalCardInfo,
    tenantId = tenantId)

  val cardsStoreBridge = new DefaultTokenizerClient(
    requestFactory = new NetHttpTransport().createRequestFactory(),
    endpointUrl = s"http://localhost:$cardsStoreBridgePort")

  val driver = new TokenizerDriver(port = cardsStoreBridgePort)

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
      driver.aTokenizeFor(aTokenizeRequest) returns someInTransitToken

      cardsStoreBridge.tokenize(
        card = someCard
      ) must be_===(Success(someInTransitToken))
    }

    "gracefully fail on error" in new Ctx {
      val someErrorMessage = "some error message"
      driver.aTokenizeFor(aTokenizeRequest) errors anInternalError(someErrorMessage)

      cardsStoreBridge.tokenize(
        card = someCard
      ) must be_===(Failure(TokenizerInternalException(someErrorMessage)))
    }
  }
  "tokenizing a card NG" should {
    "return an in-transit card token on success" in new Ctx {
      driver.aTokenizeRequest(aTokenizeRequestNG(someTenantId)) returns someInTransitToken

      cardsStoreBridge.tokenizeNG(card = someCard, tenantId = someTenantId) must be_===(Success(someInTransitToken))
    }

    "gracefully fail on error" in new Ctx {
      val someErrorMessage = "some error message"
      driver.aTokenizeRequest(aTokenizeRequestNG(someTenantId)) errors anInternalError(someErrorMessage)

      cardsStoreBridge.tokenizeNG(card = someCard, tenantId = someTenantId) must be_===(Failure(
        TokenizerInternalException(someErrorMessage)))
    }
  }

  "converting a permanent card token" should {
    "return an in-transit card token on success" in new Ctx {
      driver.anInTransitFor(anInTransitRequest) returns someInTransitToken

      cardsStoreBridge.inTransit(
        permanentToken = somePermanentCardToken,
        additionalInfo = someAdditionalCardInfo
      ) must be_===(Success(someInTransitToken))
    }

    "gracefully fail on error" in new Ctx {
      val someErrorMessage = "some error message"

      driver.anInTransitFor(anInTransitRequest) errors anInternalError(someErrorMessage)

      cardsStoreBridge.inTransit(
        permanentToken = somePermanentCardToken,
        additionalInfo = someAdditionalCardInfo
      ) must be_===(Failure(TokenizerInternalException(someErrorMessage)))
    }
  }
  "converting a permanent card token NG" should {
    "return an in-transit card token on success" in new Ctx {
      driver.anInTransitRequest(anInTransitRequestNG(someTenantId)) returns someInTransitToken

      cardsStoreBridge.inTransitNG(
        permanentToken = somePermanentCardToken,
        additionalInfo = someAdditionalCardInfo,
        tenantId = someTenantId) must be_===(Success(someInTransitToken))
    }

    "gracefully fail on error" in new Ctx {
      val someErrorMessage = "some error message"

      driver.anInTransitRequest(anInTransitRequestNG(someTenantId)) errors anInternalError(someErrorMessage)

      cardsStoreBridge.inTransitNG(
        permanentToken = somePermanentCardToken,
        additionalInfo = someAdditionalCardInfo,
        tenantId = someTenantId) must be_===(Failure(TokenizerInternalException(someErrorMessage)))
    }
  }

  step {
    driver.stop()
  }
}
