package com.wix.pay.creditcard.tokenizer


import scala.concurrent.duration.Duration
import com.google.api.client.http.{ByteArrayContent, GenericUrl, HttpRequestFactory}
import com.twitter.util.{Return, Throw, Try}
import com.wix.pay.creditcard.tokenizer.model.{CreditCardToken, TokenizeRequest}
import com.wix.pay.creditcard.{CreditCard, CreditCardOptionalFields}
import com.wix.restaurants.common.protocol.api.{Error, Response}


object Endpoints {
  val production = "https://pay.wix.com/tokenizer"
}

class DefaultTokenizerClient(requestFactory: HttpRequestFactory,
                             connectTimeout: Option[Duration] = None,
                             readTimeout: Option[Duration] = None,
                             numberOfRetries: Int = 0,
                             endpointUrl: String = Endpoints.production,
                             accessToken: Option[String] = None) extends TokenizerClient {
  private val tokenizeRequestParser = new TokenizeRequestParser
  private val responseForTokenizeRequestParser = new ResponseForTokenizeRequestParser
  private val exceptionsTranslator = new ExceptionsTranslator

  override def tokenize(card: CreditCard): Try[CreditCardToken] = {
    Try {
      val request = TokenizeRequest(
        creditCard = card
      )
      val requestJson = tokenizeRequestParser.stringify(request)

      val responseJson = doJsonRequest("/tokenize", requestJson)

      responseForTokenizeRequestParser.parse(responseJson)
    } match {
      case Return(response) => response match {
        case ResponseHasError(error) => Throw(exceptionsTranslator.translateError(error))
        case ResponseHasValue(value) => Return(value)
      }
      case Throw(e) => Throw(new TokenizerInternalException(e.getMessage, e))
    }
  }

  override def inTransit(permanentToken: CreditCardToken,
                         additionalInfo: Option[CreditCardOptionalFields] = None): Try[CreditCardToken] = {
    Try {
      throw new UnsupportedOperationException("inTransit is not implemented")
    }
  }

  override def save(temporaryToken: CreditCardToken): Try[CreditCardToken] = {
    Try {
      throw new UnsupportedOperationException("save is not implemented")
    }
  }

  override def delete(permanentToken: CreditCardToken): Try[Boolean] = {
    Try {
      throw new UnsupportedOperationException("delete is not implemented")
    }
  }

  private def doJsonRequest(resource: String, requestJson: String): String = {
    val httpRequest = requestFactory.buildPostRequest(
      new GenericUrl(endpointUrl + resource),
      new ByteArrayContent("application/json; charset=utf-8", requestJson.getBytes("UTF-8")))

    connectTimeout foreach (connectTimeout => httpRequest.setConnectTimeout(connectTimeout.toMillis.toInt))
    readTimeout foreach (readTimeout => httpRequest.setReadTimeout(readTimeout.toMillis.toInt))
    httpRequest.setNumberOfRetries(numberOfRetries)

    httpRequest.setThrowExceptionOnExecuteError(false)

    val httpResponse = httpRequest.execute()
    try {
      httpResponse.parseAsString()
    } finally {
      httpResponse.ignore()
    }
  }
}

object ResponseHasValue {
  def unapply(response: Response[CreditCardToken]): Option[CreditCardToken] = Option(response.value)
}

object ResponseHasError {
  def unapply(response: Response[CreditCardToken]): Option[Error] = Option(response.error)
}
