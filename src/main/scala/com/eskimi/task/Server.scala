package com.eskimi.task

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.handleRejections
import akka.http.scaladsl.server.RejectionHandler
import com.eskimi.task.actor.BidCheckActor
import com.eskimi.task.controller.BidController
import org.slf4j.{ Logger, LoggerFactory }

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.DurationInt

object Server {

  private val logger: Logger = LoggerFactory.getLogger(Server.getClass)

  def main(args: Array[String]): Unit = {
    implicit val system: ActorSystem = ActorSystem("eskimi-task")
    implicit val executor: ExecutionContext = system.dispatcher

    val bidRequestActor = system.actorOf(BidCheckActor.props())

    val routes =
      handleRejections(RejectionHandler.default) {
        new BidController(bidRequestActor).routes
      }

    val host = system.settings.config.getString("eskimi-task.host")
    val port = system.settings.config.getInt("eskimi-task.port")

    val bindingFuture = Http().newServerAt(host, port).bind(routes)
    logger.info(s"Server online at http://$host:$port/")

    sys.addShutdownHook({
      bindingFuture
        .flatMap(_.terminate(1.second))
        .flatMap(_ => Http().shutdownAllConnectionPools)
        .flatMap(_ => system.terminate())
    })
  }
}
