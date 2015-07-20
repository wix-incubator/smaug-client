/*      __ __ _____  __                                              *\
**     / // // /_/ |/ /          Wix                                 **
**    / // // / /|   /           (c) 2006-2015, Wix LTD.             **
**   / // // / //   |            http://www.wix.com/                 **
**   \__/|__/_//_/| |                                                **
\*                |/                                                 */
package com.wix.pay.creditcard.tokenizer

import com.wix.pay.creditcard.tokenizer.model.{CreditCardToken, Response}
import org.json4s.DefaultFormats
import org.json4s.native.Serialization

class ResponseForTokenizeRequestParser {
  implicit val formats = DefaultFormats

  def parse(str: String): Response[CreditCardToken] = {
    Serialization.read[Response[CreditCardToken]](str)
  }

  def stringify(obj: Response[CreditCardToken]): String = {
    Serialization.write(obj)
  }
}