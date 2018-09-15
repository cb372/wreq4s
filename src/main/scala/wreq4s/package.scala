import cats.effect.IO
import io.circe.{Decoder, Json, JsonObject}
import monocle.{Lens, Optional, Traversal}
import monocle.macros.GenLens
import io.circe.optics._
import monocle.function.At
import monocle.function.Index

import scala.language.higherKinds

package object wreq4s extends JsonOptics with JsonObjectOptics {

//  private implicit final lazy val jsonObjectAt: At[JsonObject, String, Option[Json]] =
//    new At[JsonObject, String, Option[Json]] {
//      final def at(field: String): Lens[JsonObject, Option[Json]] =
//        Lens[JsonObject, Option[Json]](_.apply(field))(optVal =>
//          obj => optVal.fold(obj.remove(field))(value => obj.add(field, value))
//        )
//    }
//
//  private implicit final lazy val jsonObjectIndex: Index[JsonObject, String, Json] = Index.fromAt[JsonObject, String, Json]

  implicit class Syntax[A](a: A) {
    // This is called ^. in Haskell but that's not a valid function name with . in Scala
    def ^[B](lens: Lens[A, B]): B = lens.get(a)

    def ^?[B](optional: Optional[A, B]): Option[B] = optional.getOption(a)

    // Lets us write a function in infix notation: operand & function
    def &[B](f: Function[A, B]) = f(a)
  }

  implicit class LensSyntax[S, A](lens: Lens[S, A]) {

    def ~(a: A): S => S = lens.set(a)

  }

  def responseStatus[A]: Lens[Response[A], ResponseStatus] = GenLens[Response[A]](_.status)

  def responseBody[A]: Lens[Response[A], A] = GenLens[Response[A]](_.body)

  val statusCode: Lens[ResponseStatus, Int] = GenLens[ResponseStatus](_.code)

  val statusMessage: Lens[ResponseStatus, String] = GenLens[ResponseStatus](_.message)

  val defaults: Options = Options.defaults

  def param(key: String): Lens[Options, List[String]] =
    Lens[Options, List[String]](
      opts => opts.params.collect { case (`key`, value) => value }
    )(values =>
      opts => {
        val ps = values.map(v => (key, v))
        val filtered = opts.params.filterNot(_._1 == key)
        opts.copy(params = ps ::: filtered)
      }
    )

  def key(k: String): Optional[String, Json] =
    json.composePrism(JsonOptics.jsonObject).composeOptional(Index.index[JsonObject, String, Json](k))

  val json: Optional[String, Json] = Optional[String, Json](
    string => io.circe.parser.parse(string).toOption
  )(_ => json => json)

  def asJSON[A: Decoder](response: Response[String]): IO[Response[A]] = {
    io.circe.parser.decode[A](response.body) match {
      case Left(e) => IO.raiseError(e)
      case Right(a) => IO.pure(response.copy(body = a))
    }
  }

}
