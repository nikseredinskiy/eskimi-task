package com.eskimi.task.controller

import java.util.concurrent.TimeUnit

import akka.actor.{ ActorRef, ActorSystem }
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{ Directives, Route }
import akka.pattern.ask
import akka.util.Timeout
import com.eskimi.task.actor.{ BidRequest, BidResponse }
import com.eskimi.task.util.JsonSupport._

import scala.concurrent.Future
import scala.concurrent.duration.FiniteDuration

class BidController(bidRequestActor: ActorRef)(implicit actorSystem: ActorSystem) extends Directives {

  private implicit val timeout: Timeout = Timeout(FiniteDuration(10, TimeUnit.SECONDS))

  def checkBidRoute: Route = {
    pathPrefix("check") {
      pathEnd {
        post {
          entity(as[BidRequest]) { body =>
            onSuccess(checkBid(body)) {
              case Some(v) => complete(v)
              case None    => complete(StatusCodes.NoContent)
            }
          }
        }
      }
    }
  }

  val routes: Route = checkBidRoute

  private def checkBid(body: BidRequest): Future[Option[BidResponse]] = {
    bidRequestActor.ask(body).mapTo[Option[BidResponse]]
  }

}
