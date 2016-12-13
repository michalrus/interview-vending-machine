package interview

import java.util.UUID
import org.joda.time.DateTime

object Model {

  final case class MachineState(available: List[ProductAvailability],
                                currentlySelected: List[Product],
                                pastTransactions: List[Transaction],
                                balance: Money)

  final case class Transaction(timestamp: DateTime, products: List[Product])

  /* Note, that we’re using Strings for image URL, as the URL class in
   * Java’s stdlib has some uncanny properties. E.g. its hashCode
   * might open a network connection, when called… */
  final case class Product(uuid: UUID,
                           price: Money,
                           name: String,
                           imageUrl: String)

  /* The price is in cents. It’s forbidden to use doubles for prices
   * because of numerical/rounding off issues. It’s certainly safer to
   * ‘tag’ the price using a value class, so let’s do it. Though, we
   * could really make use of Haskell’s type tagging here… */
  final case class Money(v: Int) extends AnyVal

  /* TODO: Rename this to something more appropriate. */
  final case class ProductAvailability(product: Product, amount: Int)

  final case class Payment(amount: Money)

  sealed trait PaymentResult
  final case class PaymentSucceeded(change: Money)       extends PaymentResult
  final case class PayedTooLittle(expectedAmount: Money) extends PaymentResult
  case object UnableToMakeChange                         extends PaymentResult

}
