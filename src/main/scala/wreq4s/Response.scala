package wreq4s

case class Response[A](
                        body: A,
                        status: ResponseStatus,
                        headers: List[(String, String)]
                      )

case class ResponseStatus(code: Int, message: String)
