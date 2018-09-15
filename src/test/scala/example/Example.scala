package example

import wreq4s._
import io.circe.generic.auto._

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
      url = response ^? (responseBody composeOptional key("url") composePrism jsonString)
    } yield url

    println(io.unsafeRunSync())
  }

  {
    case class User(id: Int, userId: Int, title: String, completed: Boolean)

    val io = for {
      response <- Wreq.get("https://jsonplaceholder.typicode.com/todos/1")
      decoded <- asJSON[User](response)
    } yield decoded ^ responseBody

    println(io.unsafeRunSync())
  }

  {
    val opts = defaults & header("Accept") ~ List("application/json")
    val io = for {
      response <- Wreq.getWith(opts)("http://httpbin.org/get")
    } yield response ^? responseHeader("content-type")

    println(io.unsafeRunSync())
  }

}
