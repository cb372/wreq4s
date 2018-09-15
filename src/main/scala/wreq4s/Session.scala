package wreq4s

import okhttp3.OkHttpClient

trait Session {

  def client: OkHttpClient

}

class OneOffSession extends Session {

  override def client: OkHttpClient = new OkHttpClient()

}

