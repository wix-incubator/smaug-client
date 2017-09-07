/*      __ __ _____  __                                              *\
**     / // // /_/ |/ /          Wix                                 **
**    / // // / /|   /           (c) 2006-2017, Wix LTD.             **
**   / // // / //   |            http://www.wix.com/                 **
**   \__/|__/_//_/| |                                                **
\*                |/                                                 */
package com.wix.pay.creditcard.tokenizer


import java.net.{URL, URLEncoder}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success, Try}
import com.google.api.client.http.{ByteArrayContent, GenericUrl, HttpRequestFactory}
import com.wix.pay.creditcard.tokenizer.model._
import com.wix.pay.creditcard.{CreditCard, CreditCardOptionalFields}
import com.wix.restaurants.common.protocol.api.{Error, Response}


object Endpoints {
  val production = "https://pay.wix.com/cards"
}


class DefaultTokenizerClient(requestFactory: HttpRequestFactory,
                             connectTimeout: Option[Duration] = None,
                             readTimeout: Option[Duration] = None,
                             numberOfRetries: Int = 0,
                             endpointUrl: String = Endpoints.production) extends TokenizerClient {
  private val tokenizeRequestParserNG = new TokenizeRequestParser
  private val responseForTokenizeRequestParser = new ResponseForTokenizeRequestParser
  private val inTransitRequestParserNG = new InTransitRequestParser
  private val responseForInTransitRequestParser = new ResponseForInTransitRequestParser
  private val exceptionsTranslator = new ExceptionsTranslator

  override def formUrl(params: Option[String] = None): Try[URL] = {
    Try {
      val resource = "/form" + params.map(params => s"?params=${URLEncoder.encode(params, "UTF-8")}").getOrElse("")

      Option(getAndExtractLocationHeader(resource)) match {
        case Some(url) => new URL(url)
        case None => throw TokenizerInternalException("Form endpoint did not return location header")
      }
    }
  }

  override def tokenize(card: CreditCard, tenantId: String): Try[CreditCardToken] = {
    Try {
      val request = TokenizeRequest(card = card, tenantId = tenantId)
      val requestJson = tokenizeRequestParserNG.stringify(request)

      val responseJson = doJsonRequest("/tokenize", requestJson)

      responseForTokenizeRequestParser.parse(responseJson)
    } match {
      case Success(response) => response match {
        case ResponseForTokenizeRequestHasError(error) => Failure(exceptionsTranslator.translateError(error))
        case ResponseForTokenizeRequestHasValue(value) => Success(value)
      }
      case Failure(e) => Failure(new TokenizerInternalException(e.getMessage, e))
    }
  }

  override def inTransit(permanentToken: CreditCardToken,
                         additionalInfo: Option[CreditCardOptionalFields] = None,
                         tenantId: String): Try[CreditCardToken] = {
    Try {
      val request = InTransitRequest(
        permanentToken = permanentToken,
        additionalInfo = additionalInfo,
        tenantId = tenantId)
      val requestJson = inTransitRequestParserNG.stringify(request)

      val responseJson = doJsonRequest("/intransit", requestJson)

      responseForInTransitRequestParser.parse(responseJson)
    } match {
      case Success(response) => response match {
        case ResponseForInTransitRequestHasError(error) => Failure(exceptionsTranslator.translateError(error))
        case ResponseForInTransitRequestHasValue(value) => Success(value)
      }
      case Failure(e) => Failure(new TokenizerInternalException(e.getMessage, e))
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
  def unapply(response: Response[CreditCardToken]): Option[CreditCardToken] = response.value
}
object ResponseForTokenizeRequestHasError {
  def unapply(response: Response[CreditCardToken]): Option[Error] = response.error
}

object ResponseForInTransitRequestHasValue {
  def unapply(response: Response[CreditCardToken]): Option[CreditCardToken] = response.value
}
object ResponseForInTransitRequestHasError {
  def unapply(response: Response[CreditCardToken]): Option[Error] = response.error
}
