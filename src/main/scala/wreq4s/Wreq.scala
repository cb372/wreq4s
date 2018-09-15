package wreq4s

import cats.effect.IO
import okhttp3.Request
import scala.collection.JavaConverters._

object Wreq {

  def get(url: String)(implicit session: Session = new OneOffSession): IO[Response[String]] =
    getWith(defaults)(url)

  def getWith(opts: Options)(url: String)(implicit session: Session = new OneOffSession): IO[Response[String]] = IO {
    // TODO proper URL building, escaping of params, etc.
    val queryString = opts.params.map(kv => s"${kv._1}=${kv._2}").mkString("&")
    val urlWithParams = s"$url?$queryString"
    val request = new Request.Builder().url(urlWithParams).build

    val okResp = session.client.newCall(request).execute
    val body = okResp.body.string
    val status = ResponseStatus(okResp.code, okResp.message)
    val headers =
      for {
        name <- okResp.headers.names.asScala
        value <- okResp.headers.values(name).asScala
      } yield (name.toLowerCase, value)
    Response(body, status, headers.toList)
  }

}
