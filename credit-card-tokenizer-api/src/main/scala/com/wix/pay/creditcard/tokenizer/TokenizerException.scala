package com.wix.pay.creditcard.tokenizer


class TokenizerException(message: String, cause: Throwable = null) extends RuntimeException(message, cause)


case class TokenizerInternalException(message: String, cause: Throwable) extends TokenizerException(message, cause)

object TokenizerInternalException {
  def apply(message: String): TokenizerInternalException = this(message, null)
  def apply(cause: Throwable): TokenizerInternalException = this(Option(cause).map(_.toString).orNull, cause)
  def apply(): TokenizerInternalException = this(null, null)
}


case class UnauthorizedException(message: String, cause: Throwable) extends TokenizerException(message, cause)

object UnauthorizedException {
  def apply(message: String): UnauthorizedException = this(message, null)
  def apply(cause: Throwable): UnauthorizedException = this(Option(cause).map(_.toString).orNull, cause)
  def apply(): UnauthorizedException = this(null, null)
}
