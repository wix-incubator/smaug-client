/*      __ __ _____  __                                              *\
**     / // // /_/ |/ /          Wix                                 **
**    / // // / /|   /           (c) 2006-2015, Wix LTD.             **
**   / // // / //   |            http://www.wix.com/                 **
**   \__/|__/_//_/| |                                                **
\*                |/                                                 */
package com.wix.pay.creditcard.tokenizer.model


import com.wix.pay.creditcard.PublicCreditCard

import scala.beans.BeanProperty


/** Container for a token and its associated Public Credit Card.
  *
  * @author <a href="mailto:ohadr@wix.com">Raz, Ohad</a>
  */
case class CreditCardToken(@BeanProperty token: String,
                           @BeanProperty creditCard: PublicCreditCard)
