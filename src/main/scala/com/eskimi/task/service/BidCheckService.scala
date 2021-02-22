package com.eskimi.task.service

import com.eskimi.task.entity.{ BidRequest, BidResponse, Campaign }

trait BidCheckService {

  def check(campaigns: Seq[Campaign], request: BidRequest): Option[BidResponse]

}
