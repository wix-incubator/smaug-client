package com.wix.pay.creditcard.tokenizer.testkit


import spray.http._
import com.wix.hoopoe.http.testkit.EmbeddedHttpProbe
import com.wix.pay.creditcard.tokenizer.model.{CreditCardToken, TokenizeRequest}
import com.wix.pay.creditcard.tokenizer.{ResponseForTokenizeRequestParser, TokenizeRequestParser}
import com.wix.restaurants.common.protocol.api.{Error, Response}


class TokenizerDriver(port: Int) {
  private val probe = new EmbeddedHttpProbe(port, EmbeddedHttpProbe.NotFoundHandler)
  private val tokenizeRequestParser = new TokenizeRequestParser
  private val responseForTokenizeRequestParser = new ResponseForTokenizeRequestParser

  def start() {
    probe.doStart()
  }

  def stop() {
    probe.doStop()
  }

  def reset() {
    probe.handlers.clear()
  }

  def aTokenizeFor(request: TokenizeRequest): TokenizeCtx = {
    new TokenizeCtx(request)
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
}
