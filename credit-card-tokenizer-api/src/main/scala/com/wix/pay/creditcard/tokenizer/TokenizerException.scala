package com.wix.pay.creditcard.tokenizer

class TokenizerException(message: String, cause: Throwable = null) extends RuntimeException(message, cause)

case class TokenizerInternalException(message: String, cause: Throwable = null) extends TokenizerException(message, cause)

case class UnauthorizedException(message: String, cause: Throwable = null) extends TokenizerException(message, cause)
