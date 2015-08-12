package com.wix.pay.creditcard.tokenizer

import com.twitter.util.Try
import com.wix.pay.creditcard.tokenizer.model.CreditCardToken
import com.wix.pay.creditcard.{CreditCard, CreditCardOptionalFields}

trait TokenizerClient {
  def tokenize(card: CreditCard): Try[CreditCardToken]

  def inTransit(permanentToken: CreditCardToken,
                additionalInfo: Option[CreditCardOptionalFields] = None): Try[CreditCardToken]
}
