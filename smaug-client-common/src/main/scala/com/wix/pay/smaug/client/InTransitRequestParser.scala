/*      __ __ _____  __                                              *\
**     / // // /_/ |/ /          Wix                                 **
**    / // // / /|   /           (c) 2006-2017, Wix LTD.             **
**   / // // / //   |            http://www.wix.com/                 **
**   \__/|__/_//_/| |                                                **
\*                |/                                                 */
package com.wix.pay.smaug.client


import org.json4s.native.Serialization
import org.json4s.{DefaultFormats, Formats}
import com.wix.pay.smaug.client.model.InTransitRequest


class InTransitRequestParser {
  implicit val formats: Formats = DefaultFormats

  def parse(str: String): InTransitRequest = {
    Serialization.read[InTransitRequest](str)
  }

  def stringify(obj: InTransitRequest): String = {
    Serialization.write(obj)
  }
}
