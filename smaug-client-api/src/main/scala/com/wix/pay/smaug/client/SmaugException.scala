package com.wix.pay.smaug.client


class SmaugException(message: String, cause: Throwable = null) extends RuntimeException(message, cause)


case class SmaugInternalException(message: String, cause: Throwable) extends SmaugException(message, cause)

object SmaugInternalException {
  def apply(message: String): SmaugInternalException = this(message, null)
  def apply(cause: Throwable): SmaugInternalException = this(Option(cause).map(_.toString).orNull, cause)
  def apply(): SmaugInternalException = this(null, null)
}
