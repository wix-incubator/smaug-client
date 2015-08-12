/*      __ __ _____  __                                              *\
**     / // // /_/ |/ /          Wix                                 **
**    / // // / /|   /           (c) 2006-2015, Wix LTD.             **
**   / // // / //   |            http://www.wix.com/                 **
**   \__/|__/_//_/| |                                                **
\*                |/                                                 */
package com.wix.pay.creditcard.tokenizer

import com.wix.pay.creditcard.tokenizer.model.SaveRequest
import org.json4s.DefaultFormats
import org.json4s.native.Serialization

class SaveRequestParser {
  implicit val formats = DefaultFormats

  def parse(str: String): SaveRequest = {
    Serialization.read[SaveRequest](str)
  }

  def stringify(obj: SaveRequest): String = {
    Serialization.write(obj)
  }
}