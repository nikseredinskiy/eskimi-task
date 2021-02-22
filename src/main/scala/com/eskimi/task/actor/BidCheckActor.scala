package com.eskimi.task.actor

import akka.actor.{ Actor, ActorLogging, Props }
import akka.pattern.pipe
import com.eskimi.task.actor.BidCheckActor.activeCampaigns
import com.eskimi.task.service.BidCheckService
import com.eskimi.task.service.impl.BidCheckServiceImpl

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Campaign(id: Int, country: String, targeting: Targeting, banners: List[Banner], bid: Double)

case class Targeting(targetedSiteIds: Seq[String])

case class Banner(id: Int, src: String, width: Int, height: Int)

case class BidRequest(id: String, imp: Option[List[Impression]], site: Site, user: Option[User], device: Option[Device])

case class BidResponse(id: String, bidRequestId: String, price: Double, adid: Option[String], banner: Option[Banner])

case class Impression(
    id:       String,
    wmin:     Option[Int],
    wmax:     Option[Int],
    w:        Option[Int],
    hmin:     Option[Int],
    hmax:     Option[Int],
    h:        Option[Int],
    bidFloor: Option[Double])

case class Site(id: String, domain: String)

case class User(id: String, geo: Option[Geo])

case class Device(id: String, geo: Option[Geo])

case class Geo(country: Option[String])

object BidCheckActor {

  def props(): Props = Props(new BidCheckActor(new BidCheckServiceImpl()))

  val activeCampaigns = Seq(
    Campaign(
      id = 1,
      country = "LT",
      targeting = Targeting(
        targetedSiteIds = Seq("0006a522ce0f4bbbbaa6b3c38cafaa0f")
      ),
      banners = List(
        Banner(
          id = 1,
          src = "https://business.eskimi.com/wp-content/uploads/2020/06/openGraph.jpeg",
          width = 300,
          height = 250
        )
      ),
      bid = 5d
    )
  )

}

class BidCheckActor(bidCheckService: BidCheckService) extends Actor with ActorLogging {

  var responseCounter = 0

  override def receive: Receive = {
    case request: BidRequest =>
      log.info("Receiving check bid command: {}", request)

      val checkResult = bidCheckService.check(activeCampaigns, request)

      val response = for {
        item <- checkResult.headOption //can be changed in case of a need to return BidResponse for every Impression from request
        camp <- item._2.headOption
        banners <- Option(camp.banners).filter(_.nonEmpty) //to not return campaigns without proper banners
      } yield {
        responseCounter += 1
        BidResponse(
          id = s"response$responseCounter",
          bidRequestId = request.id,
          price = item._1.bidFloor.getOrElse(0),
          adid = Some(camp.id.toString),
          banner = banners.headOption
        )
      }

      Future.successful(response).pipeTo(sender())
  }
}
