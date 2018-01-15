/*      __ __ _____  __                                              *\
**     / // // /_/ |/ /          Wix                                 **
**    / // // / /|   /           (c) 2006-2017, Wix LTD.             **
**   / // // / //   |            http://www.wix.com/                 **
**   \__/|__/_//_/| |                                                **
\*                |/                                                 */
package com.wix.pay.smaug.client.testkit

import java.net.URL
import java.util.concurrent.atomic.AtomicReference

import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.Location
import com.wix.e2e.http.RequestHandler
import com.wix.e2e.http.client.extractors.HttpMessageExtractors._
import com.wix.e2e.http.server.WebServerFactory._
import com.wix.pay.smaug.client.model.{CreditCardToken, InTransitRequest, TokenizeRequest}
import com.wix.pay.smaug.client.{InTransitRequestParser, ResponseForInTransitRequestParser, ResponseForTokenizeRequestParser, TokenizeRequestParser}
import com.wix.restaurants.common.protocol.api.{Error, Response}

import scala.collection.mutable


class SmaugDriver(port: Int) {
  private val delegatingHandler: RequestHandler = { case r: HttpRequest => handler.get().apply(r) }
  private val notFoundHandler: RequestHandler = { case _: HttpRequest => HttpResponse(status = StatusCodes.NotFound) }
  private var handler: AtomicReference[RequestHandler] = new AtomicReference[RequestHandler](notFoundHandler)

  private val probe = aMockWebServerWith(delegatingHandler).onPort(port).build
  private val tokenizeRequestParser = new TokenizeRequestParser
  private val responseForTokenizeRequestParser = new ResponseForTokenizeRequestParser
  private val inTransitRequestParser = new InTransitRequestParser
  private val responseForInTransitRequestParser = new ResponseForInTransitRequestParser

  def start() {
    probe.start()
  }

  def stop() {
    probe.stop()
  }

  def reset(): Unit = {
    handler.set(notFoundHandler)
  }

  def aFormUrl(params: Option[String] = None): FormUrlCtx = {
    new FormUrlCtx(params)
  }

  def aTokenizeRequest(request: TokenizeRequest): TokenizeCtx = {
    new TokenizeCtx(request)
  }

  def anInTransitRequest(request: InTransitRequest): InTransitCtx = {
    new InTransitCtx(request)
  }

  private def addHandler(newHandler: RequestHandler): Unit = {
    handler.set(newHandler orElse handler.get())
  }

  abstract class Ctx(resource: String) {
    protected def returnsJson(responseJson: String): Unit = {
      addHandler({
        case HttpRequest(HttpMethods.POST, uri, _, entity, _) if uri.path.toString() == resource && isStubbedRequestEntity(entity) =>
          HttpResponse(status = StatusCodes.OK, entity = HttpEntity(ContentTypes.`application/json`, responseJson))
      })
    }

    protected def isStubbedRequestEntity(entity: HttpEntity): Boolean
  }

  class FormUrlCtx(params: Option[String]) {
    def redirectsTo(url: URL): Unit = {
      def hasRightParams(uri: Uri) = params.forall(p => uri.query() == Uri.Query("params" -> p))
      addHandler({
        case HttpRequest(HttpMethods.GET, uri, _, _, _) if uri.path.toString() == "/form" && hasRightParams(uri) =>
          HttpResponse(status = StatusCodes.Found, headers = List(Location(Uri(url.toString))))
      })
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
      val parsedRequest = tokenizeRequestParser.parse(entity.extractAsString)

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
      val parsedRequest = inTransitRequestParser.parse(entity.extractAsString)
      parsedRequest == request
    }
  }
}
