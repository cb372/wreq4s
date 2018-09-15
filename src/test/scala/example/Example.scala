package example

import io.circe.optics.JsonOptics
import wreq4s._

object Example extends App {

  {
    val io = for {
      response <- Wreq.get("https://jsonplaceholder.typicode.com/todos/1")
      status = response ^ (responseStatus composeLens statusCode)
      body = response ^ responseBody
    } yield (status, body)

    println(io.unsafeRunSync())
  }

  {
    val opts = defaults & param("foo") ~ List("bar", "quux")
    val io = for {
      response <- Wreq.getWith(opts)("http://httpbin.org/get")
      url = response ^? (responseBody composeOptional key("url") composePrism JsonOptics.jsonString)
    } yield url

    println(io.unsafeRunSync())
  }

}