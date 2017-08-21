/*      __ __ _____  __                                              *\
**     / // // /_/ |/ /          Wix                                 **
**    / // // / /|   /           (c) 2006-2017, Wix LTD.             **
**   / // // / //   |            http://www.wix.com/                 **
**   \__/|__/_//_/| |                                                **
\*                |/                                                 */
package com.wix.pay.creditcard.tokenizer.testkit


import java.net.URL
import spray.http.HttpHeaders.Location
import spray.http._
import com.wix.hoopoe.http.testkit.EmbeddedHttpProbe
import com.wix.pay.creditcard.tokenizer._
import com.wix.pay.creditcard.tokenizer.model._
import com.wix.restaurants.common.protocol.api.{Error, Response}


class TokenizerDriver(port: Int) {
  private val probe = new EmbeddedHttpProbe(port, EmbeddedHttpProbe.NotFoundHandler)
  private val tokenizeRequestParser = new TokenizeRequestParser
  private val tokenizeRequestParserNG = new TokenizeRequestParserNG
  private val responseForTokenizeRequestParser = new ResponseForTokenizeRequestParser
  private val inTransitRequestParser = new InTransitRequestParser
  private val inTransitRequestParserNG = new InTransitRequestParserNG
  private val responseForInTransitRequestParser = new ResponseForInTransitRequestParser

  def start() {
    probe.doStart()
  }

  def stop() {
    probe.doStop()
  }

  def reset() {
    probe.handlers.clear()
  }

  def aFormUrl(params: Option[String] = None): FormUrlCtx = {
    new FormUrlCtx(params)
  }

  def aTokenizeFor(request: TokenizeRequest): TokenizeCtx = {
    new TokenizeCtx(request)
  }
  def aTokenizeRequest(request: TokenizeRequestNG): TokenizeNGCtx = {
    new TokenizeNGCtx(request)
  }

  def anInTransitFor(request: InTransitRequest): InTransitCtx = {
    new InTransitCtx(request)
  }
  def anInTransitRequest(request: InTransitRequestNG): InTransitNGCtx = {
    new InTransitNGCtx(request)
  }

  abstract class Ctx(resource: String) {
    protected def returnsJson(responseJson: String): Unit = {
      probe.handlers += {
        case HttpRequest(
        HttpMethods.POST,
        Uri.Path(`resource`),
        _,
        entity,
        _) if isStubbedRequestEntity(entity) =>
          HttpResponse(
            status = StatusCodes.OK,
            entity = HttpEntity(ContentTypes.`application/json`, responseJson))
      }
    }

    protected def isStubbedRequestEntity(entity: HttpEntity): Boolean
  }

  class FormUrlCtx(params: Option[String]) {
    def redirectsTo(url: URL): Unit = {
      probe.handlers += {
        case HttpRequest(
        HttpMethods.GET,
        requestUri,
        _,
        entity,
        _) if requestUri.path == Uri.Path("/form") &&
          params.forall(params => requestUri.query == Uri.Query("params" -> params)) =>

          HttpResponse(
            status = StatusCodes.Found,
            headers = List(Location(Uri(url.toString)))
          )
      }
    }
  }

  class TokenizeCtx(request: TokenizeRequest) extends Ctx("/tokenize") {
    def returns(value: CreditCardToken): Unit = {
      val response = Response[CreditCardToken](value = value)
      returnsJson(responseForTokenizeRequestParser.stringify(response))
    }

    def errors(error: Error): Unit = {
      val response = Response[CreditCardToken](error = error)
      returnsJson(responseForTokenizeRequestParser.stringify(response))
    }

    protected override def isStubbedRequestEntity(entity: HttpEntity): Boolean = {
      val parsedRequest = tokenizeRequestParser.parse(entity.asString)
      parsedRequest == request
    }
  }
  class TokenizeNGCtx(request: TokenizeRequestNG) extends Ctx("/tokenizeNG") {
    def returns(value: CreditCardToken): Unit = {
      val response = Response[CreditCardToken](value = value)
      returnsJson(responseForTokenizeRequestParser.stringify(response))
    }

    def errors(error: Error): Unit = {
      val response = Response[CreditCardToken](error = error)
      returnsJson(responseForTokenizeRequestParser.stringify(response))
    }

    protected override def isStubbedRequestEntity(entity: HttpEntity): Boolean = {
      val parsedRequest = tokenizeRequestParserNG.parse(entity.asString)

      parsedRequest == request
    }
  }


  class InTransitCtx(request: InTransitRequest) extends Ctx("/intransit") {
    def returns(value: CreditCardToken): Unit = {
      val response = Response[CreditCardToken](value = value)
      returnsJson(responseForInTransitRequestParser.stringify(response))
    }

    def errors(error: Error): Unit = {
      val response = Response[CreditCardToken](error = error)
      returnsJson(responseForInTransitRequestParser.stringify(response))
    }

    protected override def isStubbedRequestEntity(entity: HttpEntity): Boolean = {
      val parsedRequest = inTransitRequestParser.parse(entity.asString)
      parsedRequest == request
    }
  }
  class InTransitNGCtx(request: InTransitRequestNG) extends Ctx("/intransitNG") {
    def returns(value: CreditCardToken): Unit = {
      val response = Response[CreditCardToken](value = value)
      returnsJson(responseForInTransitRequestParser.stringify(response))
    }

    def errors(error: Error): Unit = {
      val response = Response[CreditCardToken](error = error)
      returnsJson(responseForInTransitRequestParser.stringify(response))
    }

    protected override def isStubbedRequestEntity(entity: HttpEntity): Boolean = {
      val parsedRequest = inTransitRequestParserNG.parse(entity.asString)
      parsedRequest == request
    }
  }
}
