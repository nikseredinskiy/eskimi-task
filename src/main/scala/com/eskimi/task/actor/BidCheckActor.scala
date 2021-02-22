package com.eskimi.task.actor

import akka.actor.{ Actor, ActorLogging, Props }
import com.eskimi.task.entity.BidRequest
import com.eskimi.task.service.BidCheckService
import com.eskimi.task.service.impl.BidCheckServiceImpl
import com.eskimi.task.util.CampaignData

object BidCheckActor {

  def props(): Props = Props(new BidCheckActor(BidCheckServiceImpl))

}

class BidCheckActor(bidCheckService: BidCheckService) extends Actor with ActorLogging {

  var responseCounter = 0

  override def receive: Receive = {
    case request: BidRequest =>
      log.info("Receiving check bid command: {}", request)

      val response = bidCheckService.check(CampaignData.activeCampaigns, request)
      responseCounter += 1

      sender() ! response.map(_.copy(id = Some(s"response$responseCounter")))
  }
}
