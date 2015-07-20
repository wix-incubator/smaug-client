package com.wix.pay.creditcard.tokenizer

import com.wix.pay.creditcard.tokenizer.model.{Error, ErrorCodes}

class ExceptionsTranslator {
  def translateError(error: Error): Throwable = {
    error.code match {
      case ErrorCodes.unauthorized => UnauthorizedException(error.description)
      case ErrorCodes.internal => new TokenizerInternalException(error.description)
      case _ => new TokenizerInternalException(error.description)
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