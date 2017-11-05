/*      __ __ _____  __                                              *\
**     / // // /_/ |/ /          Wix                                 **
**    / // // / /|   /           (c) 2006-2015, Wix LTD.             **
**   / // // / //   |            http://www.wix.com/                 **
**   \__/|__/_//_/| |                                                **
\*                |/                                                 */
package com.wix.pay.smaug.client


import org.json4s.DefaultFormats
import org.json4s.native.Serialization
import com.wix.pay.smaug.client.model.CreditCardToken
import com.wix.restaurants.common.protocol.api.Response


class ResponseForInTransitRequestParser {
  implicit val formats = DefaultFormats

  def parse(str: String): Response[CreditCardToken] = {
    Serialization.read[Response[CreditCardToken]](str)
  }

  def stringify(obj: Response[CreditCardToken]): String = {
    Serialization.write(obj)
  }
}