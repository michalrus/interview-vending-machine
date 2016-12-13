package interview

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import spray.json.DefaultJsonProtocol._

import JsonFormats._
import Model._

object Routing {

  def route(persistence: Persistence): Route =
    pathPrefix("products") {
      pathEnd {
        get(complete(persistence.availableProducts))
      } ~
        pathPrefix(JavaUUID) { uuid ⇒
          rejectEmptyResponse {
            path("select")(post(complete(persistence.select(uuid)))) ~
              path("deselect")(post(complete(persistence.deselect(uuid))))
          }
        }
    } ~
      path("current-selection") {
        get(complete(persistence.currentSelection))
      } ~
      post {
        path("cancel")(complete(persistence.cancel)) ~
          path("pay") {
            entity(as[Payment]) { payment ⇒
              onSuccess(persistence.pay(payment)) {
                case PayedTooLittle(expectedAmount) ⇒
                  complete(
                    (StatusCodes.PaymentRequired, expectedAmount.v.toString))
                case UnableToMakeChange ⇒
                  complete(
                    (StatusCodes.RetryWith,
                     "Unable to make change. Try providing the exact amount."))
                case PaymentSucceeded(change) ⇒
                  complete(change.v.toString)
              }
            }
          }
      }

}
