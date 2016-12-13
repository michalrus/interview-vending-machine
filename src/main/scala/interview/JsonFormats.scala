package interview

import java.util.UUID
import spray.json._
import DefaultJsonProtocol._

import Model._

@SuppressWarnings(Array("org.wartremover.warts.ExplicitImplicitTypes"))
object JsonFormats {
  implicit object UUIDFormat extends JsonFormat[UUID] {
    def write(x: UUID): JsValue = JsString(x.toString)
    def read(value: JsValue): UUID = value match {
      case JsString(x) ⇒ UUID.fromString(x)
      case x           ⇒ deserializationError(s"Expected UUID as JsString, but got ‘$x’")
    }
  }

  /* A shorthand for avoiding boilerplate when defining encodings for
   * value classes. */
  def tagFormat[T, V](in: T ⇒ V, out: V ⇒ T)(
      implicit fv: JsonFormat[V]): JsonFormat[T] =
    new JsonFormat[T] {
      def write(t: T): JsValue = fv.write(in(t))
      def read(v: JsValue): T  = out(fv.read(v))
    }

  implicit val moneyFormat   = tagFormat[Money, Int](_.v, Money)
  implicit val productFormat = jsonFormat4(Product)
  implicit val paymentFormat = jsonFormat1(Payment)
}
