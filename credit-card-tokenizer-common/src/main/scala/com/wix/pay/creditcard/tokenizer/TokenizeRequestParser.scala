/*      __ __ _____  __                                              *\
**     / // // /_/ |/ /          Wix                                 **
**    / // // / /|   /           (c) 2006-2015, Wix LTD.             **
**   / // // / //   |            http://www.wix.com/                 **
**   \__/|__/_//_/| |                                                **
\*                |/                                                 */
package com.wix.pay.creditcard.tokenizer

import com.wix.pay.creditcard.tokenizer.model.TokenizeRequest
import org.json4s.DefaultFormats
import org.json4s.native.Serialization

class TokenizeRequestParser {
  implicit val formats = DefaultFormats

  def parse(str: String): TokenizeRequest = {
    Serialization.read[TokenizeRequest](str)
  }

  def stringify(obj: TokenizeRequest): String = {
    Serialization.write(obj)
  }
}