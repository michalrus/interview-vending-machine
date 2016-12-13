package interview

import scala.concurrent.Future
import akka.Done
import java.util.UUID
import Model._

final class Persistence {

  def availableProducts: Future[List[Product]]     = ???
  def select(productUUID: UUID): Future[Done]      = ???
  def deselect(productUUID: UUID): Future[Done]    = ???
  def currentSelection: Future[List[Product]]      = ???
  def cancel: Future[Done]                         = ???
  def pay(payment: Payment): Future[PaymentResult] = ???

}
