/*      __ __ _____  __                                              *\
**     / // // /_/ |/ /          Wix                                 **
**    / // // / /|   /           (c) 2006-2015, Wix LTD.             **
**   / // // / //   |            http://www.wix.com/                 **
**   \__/|__/_//_/| |                                                **
\*                |/                                                 */
package com.wix.pay.creditcard.tokenizer.model


import com.wix.pay.creditcard.CreditCardOptionalFields


/** Encapsulates the data for an In-Transit request.
  *
  * @author <a href="mailto:ohadr@wix.com">Raz, Ohad</a>
  */
case class InTransitRequest(permanentToken: CreditCardToken,
                            additionalInfo: Option[CreditCardOptionalFields] = None)
