package interview

import scala.concurrent.Future
import akka.Done
import java.util.UUID
import org.joda.time.DateTime
import Model._

final class Persistence(initialState: MachineState) {

  def availableProducts: Future[List[ProductAvailability]] = withState { s ⇒
    Future.successful((s, s.available))
  }

  /* If such productUUID really is available, decrease its availability
   * by one and add it to current selection. The user can only
   * (de)select one unit of a product at a time. */
  def select(productUUID: UUID): Future[Option[Done]] = withState { s ⇒
    val newState: Option[MachineState] = for {
      availability ← s.available.find(_.product.uuid == productUUID)
      if (availability.amount > 0)
      decreasedAv = availability
        .copy(amount = availability.amount - 1) // use lens?
      newState = s.copy(
        available = s.available.map(av ⇒
          if (av.product.uuid == productUUID) decreasedAv else av),
        currentlySelected = availability.product :: s.currentlySelected
      )
    } yield newState

    Future.successful((newState getOrElse s, newState map (_ ⇒ Done)))
  }

  /* Remove the UUID from current selection, and if is available
   * (i.e. legitimate request), increase its availability by one. */
  def deselect(productUUID: UUID): Future[Done] = withState { s ⇒
    val newState = s.copy(
      currentlySelected = s.currentlySelected.filterNot(_.uuid == productUUID),
      available = s.available.map(
        av ⇒
          if (av.product.uuid == productUUID) av.copy(amount = av.amount + 1)
          else av)
    )
    Future.successful((newState, Done))
  }

  def currentSelection: Future[List[Product]] = withState { s ⇒
    Future.successful((s, s.currentlySelected))
  }

  /* Cancel the whole order/selection. */
  def cancel: Future[Done] = withState { s ⇒
    val newState = s.copy(
      currentlySelected = Nil,
      available = s.available.map(
        av ⇒
          av.copy(amount = av.amount + s.currentlySelected.count(
              _.uuid == av.product.uuid)))
    )
    Future.successful((newState, Done))
  }

  def pay(payment: Payment): Future[PaymentResult] = withState { s ⇒
    val total = s.currentlySelected.map(_.price.v).sum
    Future
      .successful(
        if (payment.amount.v < total)
          (s, PayedTooLittle(Money(total)))
        else {
          val change = payment.amount.v - total
          if (change > s.balance.v)
            (s, UnableToMakeChange)
          else
            (s.copy(
               balance = Money(s.balance.v + payment.amount.v),
               currentlySelected = Nil,
               pastTransactions = Transaction(
                   DateTime.now,
                   s.currentlySelected) :: s.pastTransactions
             ),
             PaymentSucceeded(Money(change)))
        }
      )
  }

  /* Ideal use case for the State monad? =) */
  private def withState[T](
      action: MachineState ⇒ Future[(MachineState, T)]): Future[T] = {
    ???
  }

}
