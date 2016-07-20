package lab.tag

import com.twitter.finagle.Http
import com.twitter.util.{Await, Future}

class TagApp{
  val store = new Store

  val service = Endpoint.makeService(store)

  val server = Http.serve(":8080", service) //creates service

  Await.ready(server)

  def close(): Future[Unit] = {
    Await.ready(server.close())
  }
}

object TagApp extends TagApp with App {
  Await.ready(server)
}
