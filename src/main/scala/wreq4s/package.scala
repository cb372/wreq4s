import cats.effect.IO
import io.circe.{Decoder, Json, JsonObject}
import monocle.{Lens, Optional}
import monocle.macros.GenLens
import io.circe.optics._
import monocle.function.Index

import scala.language.higherKinds

package object wreq4s extends JsonOptics with JsonObjectOptics {

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

  def responseHeader[A](name: String): Optional[Response[A], String] =
    Optional[Response[A], String](resp =>
      resp.headers.collectFirst { case (n, value) if n == name.toLowerCase => value }
    )(value => response => response.copy(headers = (name, value) :: response.headers))

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

  def header(key: String): Lens[Options, List[String]] =
    Lens[Options, List[String]](
      opts => opts.headers.collect { case (`key`, value) => value }
    )(values =>
      opts => {
        val hs = values.map(v => (key, v))
        val filtered = opts.headers.filterNot(_._1 == key)
        opts.copy(headers = hs ::: filtered)
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
