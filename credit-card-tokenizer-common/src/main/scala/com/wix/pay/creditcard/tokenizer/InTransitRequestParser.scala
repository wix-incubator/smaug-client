/*      __ __ _____  __                                              *\
**     / // // /_/ |/ /          Wix                                 **
**    / // // / /|   /           (c) 2006-2017, Wix LTD.             **
**   / // // / //   |            http://www.wix.com/                 **
**   \__/|__/_//_/| |                                                **
\*                |/                                                 */
package com.wix.pay.creditcard.tokenizer


import org.json4s.{DefaultFormats, Formats}
import org.json4s.native.Serialization
import com.wix.pay.creditcard.tokenizer.model.InTransitRequest


class InTransitRequestParser {
  implicit val formats: Formats = DefaultFormats

  def parse(str: String): InTransitRequest = {
    Serialization.read[InTransitRequest](str)
  }

  def stringify(obj: InTransitRequest): String = {
    Serialization.write(obj)
  }
}
