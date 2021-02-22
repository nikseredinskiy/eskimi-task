package com.eskimi.task.service

import com.eskimi.task.actor.{ BidRequest, Campaign, Impression }

trait BidCheckService {

  def check(campaigns: Seq[Campaign], request: BidRequest): Map[Impression, Seq[Campaign]]

}
