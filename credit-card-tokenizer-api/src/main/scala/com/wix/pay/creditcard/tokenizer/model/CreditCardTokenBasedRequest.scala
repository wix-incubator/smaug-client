/*      __ __ _____  __                                              *\
**     / // // /_/ |/ /          Wix                                 **
**    / // // / /|   /           (c) 2006-2015, Wix LTD.             **
**   / // // / //   |            http://www.wix.com/                 **
**   \__/|__/_//_/| |                                                **
\*                |/                                                 */
package com.wix.pay.creditcard.tokenizer.model


/** Encapsulates the data for a tokenizer based request (e.g., delete or detokenize).
  *
  * @author <a href="mailto:ohadr@wix.com">Raz, Ohad</a>
  */
case class CreditCardTokenBasedRequest(accessToken: String, token: CreditCardToken)
