package emsgame.testing

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer

/**
  * Created by pappmar on 01/02/2017.
  */
object RunEmsGameServer {

  def main(args: Array[String]): Unit = {

    implicit val actorSystem = ActorSystem()
    implicit val materializer = ActorMaterializer()
    import actorSystem.dispatcher

    import akka.http.scaladsl.server.Directives._
    val route = {
      getFromDirectory(
        "js"
      )
    }

    Http()
      .bindAndHandle(
        route,
        "0.0.0.0",
        9665
      )
      .onComplete(println)








  }

}
