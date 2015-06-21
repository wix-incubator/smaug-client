/*      __ __ _____  __                                              *\
**     / // // /_/ |/ /          Wix                                 **
**    / // // / /|   /           (c) 2006-2015, Wix LTD.             **
**   / // // / //   |            http://www.wix.com/                 **
**   \__/|__/_//_/| |                                                **
\*                |/                                                 */
package com.wix.pay.creditcard.tokenizer.model


import com.wix.pay.creditcard.CreditCard


/** Encapsulates the data for a Tokenize request.
  *
  * @author <a href="mailto:ohadr@wix.com">Raz, Ohad</a>
  */
case class TokenizeRequest(accessToken: String, creditCard: CreditCard)
