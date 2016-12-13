package interview

class MainSpec extends UnitSpec {
  "Addition" should {
    "be commutative" in forAll { (a: Int, b: Int) ⇒
      b + a shouldEqual a + b
    }
  }
}
