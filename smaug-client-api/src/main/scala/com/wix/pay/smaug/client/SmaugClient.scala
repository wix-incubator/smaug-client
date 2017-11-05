/*      __ __ _____  __                                              *\
**     / // // /_/ |/ /          Wix                                 **
**    / // // / /|   /           (c) 2006-2017, Wix LTD.             **
**   / // // / //   |            http://www.wix.com/                 **
**   \__/|__/_//_/| |                                                **
\*                |/                                                 */
package com.wix.pay.smaug.client


import scala.util.Try
import java.net.URL
import com.wix.pay.creditcard.{CreditCard, CreditCardOptionalFields}
import com.wix.pay.smaug.client.model.CreditCardToken


/** Defines the APIs and behaviour of any implementing Smaug's Client.
  * The Clients implementing this trait are clients that issue requests to Smaug APIs that do not required an access
  * token; these could be ''tokenize'' and ''in-transit'' (as opposed to ''save'', ''delete'' and ''clean expired'',
  * which require an access token).
  *
  * @author <a href="mailto:ohadr@wix.com">Raz, Ohad</a>
  */
trait SmaugClient {

  /** Returns the URL for the (secured) payment form, that contains the specified additional parameters.
    * Different merchants, or even gateways, may require additional information (additional to the credit card number
    * and expiration date) for performing payment. Such can be the CVV, or card holder ID. These additional fields
    * should be specified specifically.
    *
    * @param params
    *               Additional parameters to be included in the payment form. Optional.
    * @return
    *         The URL to the payment form.
    */
  def formUrl(params: Option[String] = None): Try[URL]

  /** Tokenize the given credit-card.
    * ''Tokenize'' means that the credit-card details will be saved in both the underlying storage and HSM, as
    * ''temporary'' (a.k.a., "''in-transit''") tokens. A ''temporary'' token holds all required data, including CVV
    * (or CSC), for a predefined period of time; once this period of time had elapsed, the token is deleted from both
    * the underlying storage and HSM.
    * A payment action can only be done using an ''in-transit'' (''temporary'') token.
    *
    * @param card
    *             The credit-card to be tokenized.
    * @return
    *         A credit-card token.
    */
  def tokenize(card: CreditCard, tenantId: String): Try[CreditCardToken]

  /** Creates a temporary ("''in-transit''") token from the given permanent (saved) token.
    * The ''temporary'' token holds all required data, including CVV (or CSC), for a predefined period of time; once
    * this period of time had elapsed, the token is deleted from both the underlying storage and HSM.
    * A payment action can only be done using an ''in-transit'' (''temporary'') token.
    *
    * @param permanentToken
    *                       The permanent token for which a temporary ("''in-transit''") token will be created.
    * @return
    *         A new credit-card token, representing the temporary token.
    */
  def inTransit(permanentToken: CreditCardToken,
                additionalInfo: Option[CreditCardOptionalFields] = None,
                tenantId: String): Try[CreditCardToken]
}
