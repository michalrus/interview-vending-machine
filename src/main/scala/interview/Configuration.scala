package interview

import java.util.UUID
import Model._

object Configuration {

  val Port = 8080
  val Host = "localhost"

  // scalastyle:off magic.number
  val InitialState = MachineState(
    available = List(
      ProductAvailability(
        Product(UUID.fromString("653055c3-d101-4347-84e5-cd757f27232c"),
                Money(20),
                "Product A",
                "http://localhost-cdn/a.jpg"),
        amount = 20),
      ProductAvailability(
        Product(
          UUID.fromString("08af572c-346e-4f82-b698-27a8bc09005e"),
          Money(30),
          "Product B",
          "http://localhost-cdn/b.jpg"),
        amount = 5),
      ProductAvailability(
        Product(
          UUID.fromString("205eb62d-e3fd-4521-844a-851d4d8203a4"),
          Money(40),
          "Product C",
          "http://localhost-cdn/c.jpg"),
        amount = 31),
      ProductAvailability(
        Product(UUID.fromString("d1c24f6b-4e9f-47cc-a652-b6cc49bc4694"),
                Money(50),
                "Product D",
                "http://localhost-cdn/d.jpg"),
        amount = 17)
    ),
    currentlySelected = Nil,
    pastTransactions = Nil,
    balance = Money(100 * 100)
  )
  // scalastyle:on magic.number

}
