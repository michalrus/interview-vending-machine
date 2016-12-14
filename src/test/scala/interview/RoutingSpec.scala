package interview

import org.scalatest.{Matchers, WordSpec}
import akka.actor.ActorSystem
import akka.http.scaladsl.testkit.ScalatestRouteTest
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.StatusCodes
import spray.json.DefaultJsonProtocol._

import Model._
import JsonFormats._
import Configuration.InitialState

class RoutingSpec extends WordSpec with Matchers with ScalatestRouteTest {

  "The service" should {

    "behave sanely" in {
      val route =
        Routing.route(new Persistence(InitialState, implicitly[ActorSystem]))

      val productA = InitialState.available(0).product
      val productB = InitialState.available(1).product

      Get("/products") ~> route ~> check {
        responseAs[List[ProductAvailability]] shouldEqual InitialState.available
      }

      Get("/current-selection") ~> route ~> check {
        responseAs[List[Product]] shouldEqual Nil
      }

      Post(s"/products/${productA.uuid}/select") ~> route
      Get("/current-selection") ~> route ~> check {
        responseAs[List[Product]] shouldEqual List(productA)
      }

      Post(s"/cancel") ~> route
      Get("/current-selection") ~> route ~> check {
        responseAs[List[Product]] shouldEqual Nil
      }

      Post(s"/products/${productA.uuid}/select") ~> route
      Post(s"/products/${productB.uuid}/select") ~> route
      Post("/pay", Payment(Money(5))) ~> route ~> check {
        status shouldEqual StatusCodes.PaymentRequired
        responseAs[String] shouldEqual (productA.price.v + productB.price.v).toString
      }

      /* Check if amounts were decreased. */
      Get("/products") ~> route ~> check {
        responseAs[List[ProductAvailability]] shouldEqual InitialState.available
          .map(
            av ⇒
              if (Set(productA.uuid, productB.uuid) contains av.product.uuid)
                av.copy(amount = av.amount - 1)
              else av)
      }

      Post(s"/products/${productB.uuid}/deselect") ~> route
      Post("/pay", Payment(Money(40))) ~> route ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual (40 - productA.price.v).toString
      }

      /* Test the ‘unable to make change’ error. */
      Post(s"/products/${productA.uuid}/select") ~> route
      Post("/pay", Payment(Money(999999))) ~> route ~> check {
        status shouldEqual StatusCodes.RetryWith
      }

      // Buy one more (third).
      Post(s"/products/${productA.uuid}/select") ~> route
      Post("/pay", Payment(Money(999))) ~> route

      Get("/products") ~> route ~> check {
        responseAs[List[ProductAvailability]].head.amount shouldEqual
          (InitialState.available.head.amount - 3)
      }
    }

  }

}
