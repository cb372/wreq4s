package wreq4s

case class Response[A](status: ResponseStatus, body: A)

case class ResponseStatus(code: Int, message: String)
