package com.wix.pay.creditcard.tokenizer


import com.wix.pay.creditcard.tokenizer.model.ErrorCodes
import com.wix.restaurants.common.protocol.api.Error


class ExceptionsTranslator {
  def translateError(error: Error): Throwable = {
    error.code match {
      case ErrorCodes.unauthorized => UnauthorizedException(error.description)
      case ErrorCodes.internal => TokenizerInternalException(error.description)
      case ErrorCodes.unauthenticated => AuthenticationException(error.description)
      case ErrorCodes.unauthorizedNG => AuthorizationException()
      case _ => TokenizerInternalException(error.description)
    }
  }

  def translateException(e: Throwable): Error = {
    e match {
      case e: UnauthorizedException => Error(ErrorCodes.unauthorized, e.getMessage)
      case e: TokenizerInternalException => Error(ErrorCodes.internal, e.getMessage)
      case e: Throwable => Error(ErrorCodes.internal, e.getMessage)
    }
  }
}