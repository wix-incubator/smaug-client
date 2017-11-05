/*      __ __ _____  __                                              *\
**     / // // /_/ |/ /          Wix                                 **
**    / // // / /|   /           (c) 2006-2017, Wix LTD.             **
**   / // // / //   |            http://www.wix.com/                 **
**   \__/|__/_//_/| |                                                **
\*                |/                                                 */
package com.wix.pay.smaug.client.model


import com.wix.pay.creditcard.CreditCard


/** Encapsulates the data for a Tokenize request.
  *
  * @author <a href="mailto:ohadr@wix.com">Raz, Ohad</a>
  */
case class TokenizeRequest(card: CreditCard, tenantId: String)
