package com.wix.pay.creditcard.tokenizer


import java.net.{URLEncoder, URL}

import com.google.api.client.http.{ByteArrayContent, GenericUrl, HttpRequestFactory}
import com.twitter.util.{Return, Throw, Try}
import com.wix.pay.creditcard.tokenizer.model.{CreditCardToken, InTransitRequest, TokenizeRequest}
import com.wix.pay.creditcard.{CreditCard, CreditCardOptionalFields}
import com.wix.restaurants.common.protocol.api.{Error, Response}

import scala.concurrent.duration.Duration


object Endpoints {
  val production = "https://pay.wix.com/cards"
}

class DefaultTokenizerClient(requestFactory: HttpRequestFactory,
                             connectTimeout: Option[Duration] = None,
                             readTimeout: Option[Duration] = None,
                             numberOfRetries: Int = 0,
                             endpointUrl: String = Endpoints.production) extends TokenizerClient {
  private val tokenizeRequestParser = new TokenizeRequestParser
  private val responseForTokenizeRequestParser = new ResponseForTokenizeRequestParser
  private val inTransitRequestParser = new InTransitRequestParser
  private val responseForInTransitRequestParser = new ResponseForInTransitRequestParser
  private val exceptionsTranslator = new ExceptionsTranslator

  override def formUrl(params: String): Try[URL] = {
    Try {
      val resource = s"/form?params=${URLEncoder.encode(params, "UTF-8")}"
      Option(getAndExtractLocationHeader(resource)) match {
        case Some(url) => new URL(url)
        case None => throw TokenizerInternalException("Form endpoint did not return location header")
      }
    }
  }

  override def tokenize(card: CreditCard): Try[CreditCardToken] = {
    Try {
      val request = TokenizeRequest(
        card = card
      )
      val requestJson = tokenizeRequestParser.stringify(request)

      val responseJson = doJsonRequest("/tokenize", requestJson)

      responseForTokenizeRequestParser.parse(responseJson)
    } match {
      case Return(response) => response match {
        case ResponseForTokenizeRequestHasError(error) => Throw(exceptionsTranslator.translateError(error))
        case ResponseForTokenizeRequestHasValue(value) => Return(value)
      }
      case Throw(e) => Throw(new TokenizerInternalException(e.getMessage, e))
    }
  }

  override def inTransit(permanentToken: CreditCardToken,
                         additionalInfo: Option[CreditCardOptionalFields] = None): Try[CreditCardToken] = {
    Try {
      val request = InTransitRequest(
        permanentToken = permanentToken,
        additionalInfo = additionalInfo
      )
      val requestJson = inTransitRequestParser.stringify(request)

      val responseJson = doJsonRequest("/intransit", requestJson)

      responseForInTransitRequestParser.parse(responseJson)
    } match {
      case Return(response) => response match {
        case ResponseForInTransitRequestHasError(error) => Throw(exceptionsTranslator.translateError(error))
        case ResponseForInTransitRequestHasValue(value) => Return(value)
      }
      case Throw(e) => Throw(new TokenizerInternalException(e.getMessage, e))
    }
  }

  private def getAndExtractLocationHeader(resource: String): String = {
    val httpRequest = requestFactory.buildGetRequest(new GenericUrl(endpointUrl + resource))

    connectTimeout foreach (connectTimeout => httpRequest.setConnectTimeout(connectTimeout.toMillis.toInt))
    readTimeout foreach (readTimeout => httpRequest.setReadTimeout(readTimeout.toMillis.toInt))
    httpRequest.setNumberOfRetries(numberOfRetries)

    httpRequest.setFollowRedirects(false)
    httpRequest.setThrowExceptionOnExecuteError(false)

    val httpResponse = httpRequest.execute()
    try {
      httpResponse.getHeaders.getLocation
    } finally {
      httpResponse.ignore()
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

object ResponseForTokenizeRequestHasValue {
  def unapply(response: Response[CreditCardToken]): Option[CreditCardToken] = Option(response.value)
}
object ResponseForTokenizeRequestHasError {
  def unapply(response: Response[CreditCardToken]): Option[Error] = Option(response.error)
}

object ResponseForInTransitRequestHasValue {
  def unapply(response: Response[CreditCardToken]): Option[CreditCardToken] = Option(response.value)
}
object ResponseForInTransitRequestHasError {
  def unapply(response: Response[CreditCardToken]): Option[Error] = Option(response.error)
}
