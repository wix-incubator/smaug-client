package com.wix.pay.creditcard.tokenizer.model

case class SaveRequest(accessToken: String, inTransitToken: CreditCardToken)