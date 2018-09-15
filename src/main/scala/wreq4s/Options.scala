package wreq4s

final case class Options(
                        auth: Option[Auth] = None,
                        headers: List[(String, String)] = Nil,
                        params: List[(String, String)] = Nil
                        )

object Options {
  val defaults = Options()
}

sealed trait Auth

object Auth {
  case class Basic(username: String, password: String) extends Auth
}

