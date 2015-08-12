/*      __ __ _____  __                                              *\
**     / // // /_/ |/ /          Wix                                 **
**    / // // / /|   /           (c) 2006-2015, Wix LTD.             **
**   / // // / //   |            http://www.wix.com/                 **
**   \__/|__/_//_/| |                                                **
\*                |/                                                 */
package com.wix.pay.creditcard.tokenizer

import com.wix.pay.creditcard.tokenizer.model.InTransitRequest
import org.json4s.DefaultFormats
import org.json4s.native.Serialization

class InTransitRequestParser {
  implicit val formats = DefaultFormats

  def parse(str: String): InTransitRequest = {
    Serialization.read[InTransitRequest](str)
  }

  def stringify(obj: InTransitRequest): String = {
    Serialization.write(obj)
  }
}