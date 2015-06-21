/*      __ __ _____  __                                              *\
**     / // // /_/ |/ /          Wix                                 **
**    / // // / /|   /           (c) 2006-2015, Wix LTD.             **
**   / // // / //   |            http://www.wix.com/                 **
**   \__/|__/_//_/| |                                                **
\*                |/                                                 */
package com.wix.pay.creditcard.tokenizer.model


/** Encapsulates the data for a Response.
  * It has mutual members - either a value, for successive response, or an Error for a failure response.
  *
  * @author <a href="mailto:ohadr@wix.com">Raz, Ohad</a>
  */
case class Response[V] private (value: V, error: Error)


/** The companion object of the [[Response]] case class, introduces the means to create a response, a successive one
  * or an error.
  *
  * @author <a href="mailto:ohadr@wix.com">Raz, Ohad</a>
  */
object Response {
  def apply[V](value: V): Response[V] = Response(value, null)

  def apply[V](error: Error): Response[V] = Response(null.asInstanceOf[V], error)
}