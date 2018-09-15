package wreq4s

import cats.effect.IO
import okhttp3.Request
import okhttp3.{Response => OkResponse}

object Wreq {

  def get(url: String)(implicit session: Session = new OneOffSession): IO[Response[String]] = IO {
    val request = new Request.Builder().url(url).build

    val okResp = session.client.newCall(request).execute
    val status = ResponseStatus(okResp.code(), okResp.message())
    Response(status, okResp.body.string)
  }

  def getWith(opts: Options)(url: String)(implicit session: Session = new OneOffSession): IO[Response[String]] = IO {
    // TODO proper URL building, escaping of params, etc.
    val queryString = opts.params.map(kv => s"${kv._1}=${kv._2}").mkString("&")
    val urlWithParams = s"$url?$queryString"
    val request = new Request.Builder().url(urlWithParams).build

    val okResp = session.client.newCall(request).execute
    val status = ResponseStatus(okResp.code(), okResp.message())
    Response(status, okResp.body.string)
  }

}
