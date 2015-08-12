package com.wix.pay.creditcard.tokenizer


import com.google.api.client.http.javanet.NetHttpTransport
import com.twitter.util.{Return, Throw}
import com.wix.pay.creditcard.tokenizer.model.{CreditCardToken, ErrorCodes, TokenizeRequest}
import com.wix.pay.creditcard.tokenizer.testkit.TokenizerDriver
import com.wix.pay.creditcard.{CreditCard, PublicCreditCard, YearMonth}
import com.wix.restaurants.common.protocol.api.Error
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope


class DefaultTokenizerClientTest extends SpecWithJUnit {
  val tokenizerPort = 10001
  val someAccessToken = "some access token"
  val someCard = CreditCard(
    number = "4111111111111111",
    expiration = YearMonth(
      year = 2020,
      month = 12))
  val someCardToken = CreditCardToken(
    token = "some token",
    creditCard = PublicCreditCard(someCard))
  val aTokenizeRequest = TokenizeRequest(card = someCard)
  val tokenizer = new DefaultTokenizerClient(
    requestFactory = new NetHttpTransport().createRequestFactory(),
    endpointUrl = s"http://localhost:$tokenizerPort")
  val tokenizerWithAccess = new DefaultTokenizerClient(
    requestFactory = new NetHttpTransport().createRequestFactory(),
    endpointUrl = s"http://localhost:$tokenizerPort",
    accessToken = Some(someAccessToken))
  val driver = new TokenizerDriver(port = tokenizerPort)

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


  "tokenizing a card" should {
    "return the document ID on success" in new Ctx {
      driver.aTokenizeFor(aTokenizeRequest) returns someCardToken

      tokenizer.tokenize(card = someCard) must be_===(Return(someCardToken))
    }

    "gracefully fail on error" in new Ctx {
      val someErrorMessage = "some error message"

      driver.aTokenizeFor(aTokenizeRequest) errors anInternalError(someErrorMessage)

      tokenizer.tokenize(card = someCard) must be_===(Throw(TokenizerInternalException(someErrorMessage)))
    }
  }


  step {
    driver.stop()
  }
}
