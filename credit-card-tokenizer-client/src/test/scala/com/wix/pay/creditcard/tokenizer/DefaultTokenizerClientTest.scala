package com.wix.pay.creditcard.tokenizer

import com.google.api.client.http.javanet.NetHttpTransport
import com.twitter.util.{Throw, Try}
import com.wix.pay.creditcard.tokenizer.model.{CreditCardToken, Error, ErrorCodes, TokenizeRequest}
import com.wix.pay.creditcard.tokenizer.testkit.TokenizerDriver
import com.wix.pay.creditcard.{CreditCard, PublicCreditCard, YearMonth}
import org.specs2.matcher._
import org.specs2.mutable.SpecWithJUnit
import org.specs2.specification.Scope

import scala.reflect.ClassTag

class DefaultTokenizerClientTest extends SpecWithJUnit {
  val tokenizerPort = 10001

  val driver = new TokenizerDriver(port = tokenizerPort)

  step {
    driver.start()
  }

  sequential

  trait Ctx extends Scope {
    val someAccessToken = "some access token"

    val someCard = CreditCard(
      number = "4111111111111111",
      expiration = YearMonth(
        year = 2020,
        month = 12
      )
    )

    val someCardToken = CreditCardToken(
      token = "some token",
      creditCard = PublicCreditCard(someCard)
    )

    val aTokenizeRequest = TokenizeRequest(
      creditCard = someCard
    )

    def anInternalError(message: String): Error = {
      Error(
        code = ErrorCodes.internal,
        description = message
      )
    }

    val tokenizer = new DefaultTokenizerClient(
      requestFactory = new NetHttpTransport().createRequestFactory(),
      endpointUrl = s"http://localhost:$tokenizerPort"
    )

    val tokenizerWithAccess = new DefaultTokenizerClient(
      requestFactory = new NetHttpTransport().createRequestFactory(),
      endpointUrl = s"http://localhost:$tokenizerPort",
      accessToken = Some(someAccessToken)
    )

    driver.reset()
  }


  def beSuccessful(cardToken: Matcher[CreditCardToken] = AlwaysMatcher()): Matcher[Try[CreditCardToken]] =
    TwitterTrySuccessMatcher[CreditCardToken]() and
      cardToken ^^ { (_: Try[CreditCardToken]).get() }


  def beFailure[T <: Throwable : ClassTag](msg: Matcher[String] = AlwaysMatcher(),
                                           cause: Matcher[Throwable] = AlwaysMatcher()): Matcher[Try[CreditCardToken]] = {
    TwitterTryFailureMatcher[CreditCardToken]() and
      beAnInstanceOf[T] ^^ { (_: Try[CreditCardToken]) match {
        case Throw(e) => e
        case _ => failure("Expected a failure (exception), but was successful")
      }} and
      new Matcher[Try[CreditCardToken]] {
        override def apply[S <: Try[CreditCardToken]](expectable: Expectable[S]): MatchResult[S] = {
          expectable.value match {
            case Throw(e) => createExpectable(e.getMessage).applyMatcher(msg).asInstanceOf[MatchResult[S]]
            case _ => failure("Expected a failure (exception), but was successful", expectable)
          }
        }
      } and
      new Matcher[Try[CreditCardToken]] {
        override def apply[S <: Try[CreditCardToken]](expectable: Expectable[S]): MatchResult[S] = {
          expectable.value match {
            case Throw(e) => createExpectable(e.getCause).applyMatcher(cause).asInstanceOf[MatchResult[S]]
            case _ => failure("Expected a failure (exception), but was successful", expectable)
          }
        }
      }
  }

  "tokenizing a card" should {
    "return the document ID on success" in new Ctx {
      driver.aTokenizeFor(aTokenizeRequest) returns someCardToken

      tokenizer.tokenize(
        card = someCard
      ) must beSuccessful(
        cardToken = ===(someCardToken)
      )
    }

    "gracefully fail on error" in new Ctx {
      val someErrorMessage = "some error message"
      driver.aTokenizeFor(aTokenizeRequest) errors anInternalError(someErrorMessage)

      tokenizer.tokenize(
        card = someCard
      ) must beFailure[TokenizerInternalException](
        msg = ===(someErrorMessage)
      )
    }
  }

  step {
    driver.stop()
  }
}

case class TwitterTrySuccessMatcher[T]() extends OptionLikeMatcher[Try, T, T]("a Success", (_: Try[T]).toOption)
case class TwitterTryFailureMatcher[T]() extends OptionLikeMatcher[Try, T, Throwable](
  "a Failure",
  (_: Try[T]) match {
    case Throw(e) => Option(e)
    case _ => None
  })