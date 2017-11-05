/*      __ __ _____  __                                              *\
**     / // // /_/ |/ /          Wix                                 **
**    / // // / /|   /           (c) 2006-2017, Wix LTD.             **
**   / // // / //   |            http://www.wix.com/                 **
**   \__/|__/_//_/| |                                                **
\*                |/                                                 */
package com.wix.pay.smaug.client


/** An exception class indicates that invalid credentials were provided on the request.
  *
  * @author <a href="mailto:ohadr@wix.com">Raz, Ohad</a>
  */
case class AuthorizationException(message: String, cause: Throwable) extends RuntimeException(message, cause)


/** The Companion Object of the [[AuthorizationException]] class, which introduces means for instantiating an
  * exception object.
  *
  * @author <a href="mailto:ohadr@wix.com">Raz, Ohad</a>
  */
object AuthorizationException {
  def apply(message: String): AuthorizationException = this(message, null)
  def apply(cause: Throwable): AuthorizationException = this(Option(cause).map(_.toString).orNull, cause)
  def apply(): AuthorizationException = this(null, null)
}
