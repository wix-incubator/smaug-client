/*      __ __ _____  __                                              *\
**     / // // /_/ |/ /          Wix                                 **
**    / // // / /|   /           (c) 2006-2015, Wix LTD.             **
**   / // // / //   |            http://www.wix.com/                 **
**   \__/|__/_//_/| |                                                **
\*                |/                                                 */
package com.wix.pay.creditcard.tokenizer


import com.wix.pay.creditcard.tokenizer.model.DeleteResponse
import com.wix.restaurants.common.protocol.api.Response
import org.json4s.DefaultFormats
import org.json4s.native.Serialization


class ResponseForDeleteRequestParser {
  implicit val formats = DefaultFormats

  def parse(str: String): Response[DeleteResponse] = {
    Serialization.read[Response[DeleteResponse]](str)
  }

  def stringify(obj: Response[DeleteResponse]): String = {
    Serialization.write(obj)
  }
}