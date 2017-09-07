package com.wix.pay.creditcard.tokenizer


import com.wix.pay.creditcard.tokenizer.model.ErrorCodes
import com.wix.restaurants.common.protocol.api.Error


class ExceptionsTranslator {
  def translateError(error: Error): Throwable = {
    error.code match {
      case ErrorCodes.unauthenticated => AuthenticationException(error.description)
      case ErrorCodes.unauthorized    => AuthorizationException(error.description)
      case ErrorCodes.internal        => TokenizerInternalException(error.description)
      case _                          => TokenizerInternalException(error.description)
    }
  }

  def translateException(e: Throwable): Error = {
    e match {
      case _: AuthenticationException    => Error(ErrorCodes.unauthenticated, "invalid/expired access token")
      case _: AuthorizationException     => Error(ErrorCodes.unauthorized, "unauthorized operation")
      case _: TokenizerInternalException => Error(ErrorCodes.internal, e.getMessage)
      case _: Throwable                  => Error(ErrorCodes.internal, e.getMessage)
    }
  }
}
