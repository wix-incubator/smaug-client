package com.wix.pay.smaug.client


import com.wix.pay.smaug.client.model.ErrorCodes
import com.wix.restaurants.common.protocol.api.Error


class ExceptionsTranslator {
  def translateError(error: Error): Throwable = {
    error.code match {
      case ErrorCodes.unauthenticated => AuthenticationException(error.description)
      case ErrorCodes.unauthorized    => AuthorizationException(error.description)
      case ErrorCodes.internal        => SmaugInternalException(error.description)
      case _                          => SmaugInternalException(error.description)
    }
  }

  def translateException(e: Throwable): Error = {
    e match {
      case _: AuthenticationException    => Error(ErrorCodes.unauthenticated, "invalid/expired access token")
      case _: AuthorizationException     => Error(ErrorCodes.unauthorized, "unauthorized operation")
      case _: SmaugInternalException => Error(ErrorCodes.internal, e.getMessage)
      case _: Throwable                  => Error(ErrorCodes.internal, e.getMessage)
    }
  }
}
