package com.wix.pay.creditcard.tokenizer.testkit


import com.wix.hoopoe.http.testkit.EmbeddedHttpProbe
import com.wix.pay.creditcard.tokenizer._
import com.wix.pay.creditcard.tokenizer.model._
import com.wix.restaurants.common.protocol.api.{Error, Response}
import spray.http._


class TokenizerDriver(port: Int) {
  private val probe = new EmbeddedHttpProbe(port, EmbeddedHttpProbe.NotFoundHandler)
  private val tokenizeRequestParser = new TokenizeRequestParser
  private val responseForTokenizeRequestParser = new ResponseForTokenizeRequestParser
  private val inTransitRequestParser = new InTransitRequestParser
  private val responseForInTransitRequestParser = new ResponseForInTransitRequestParser
  private val saveRequestParser = new SaveRequestParser
  private val responseForSaveRequestParser = new ResponseForSaveRequestParser
  private val deleteRequestParser = new DeleteRequestParser
  private val responseForDeleteRequestParser = new ResponseForDeleteRequestParser

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

  def anInTransitFor(request: InTransitRequest): InTransitCtx = {
    new InTransitCtx(request)
  }

  def aSaveFor(request: SaveRequest): SaveCtx = {
    new SaveCtx(request)
  }

  def aDeleteFor(request: DeleteRequest): DeleteCtx = {
    new DeleteCtx(request)
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

  class InTransitCtx(request: InTransitRequest) extends Ctx("/inTransit") {
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

  class SaveCtx(request: SaveRequest) extends Ctx("/save") {
    def returns(value: CreditCardToken): Unit = {
      val response = Response[CreditCardToken](value = value)
      returnsJson(responseForSaveRequestParser.stringify(response))
    }

    def errors(error: Error): Unit = {
      val response = Response[CreditCardToken](error = error)
      returnsJson(responseForSaveRequestParser.stringify(response))
    }

    protected override def isStubbedRequestEntity(entity: HttpEntity): Boolean = {
      val parsedRequest = saveRequestParser.parse(entity.asString)
      parsedRequest == request
    }
  }

  class DeleteCtx(request: DeleteRequest) extends Ctx("/delete") {
    def returns(value: DeleteResponse): Unit = {
      val response = Response[DeleteResponse](value = value)
      returnsJson(responseForDeleteRequestParser.stringify(response))
    }

    def errors(error: Error): Unit = {
      val response = Response[DeleteResponse](error = error)
      returnsJson(responseForDeleteRequestParser.stringify(response))
    }

    protected override def isStubbedRequestEntity(entity: HttpEntity): Boolean = {
      val parsedRequest = deleteRequestParser.parse(entity.asString)
      parsedRequest == request
    }
  }
}