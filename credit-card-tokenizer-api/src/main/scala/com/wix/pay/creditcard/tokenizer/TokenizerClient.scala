package com.wix.pay.creditcard.tokenizer

import java.net.URL

import com.wix.pay.creditcard.tokenizer.model.CreditCardToken
import com.wix.pay.creditcard.{CreditCard, CreditCardOptionalFields}

import scala.util.Try

trait TokenizerClient {
  def formUrl(params: Option[String] = None): Try[URL]

  def tokenize(card: CreditCard): Try[CreditCardToken]

  def inTransit(permanentToken: CreditCardToken,
                additionalInfo: Option[CreditCardOptionalFields] = None): Try[CreditCardToken]
}
