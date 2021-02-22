package com.eskimi.task.entity

case class BidResponse(
    id:           Option[String],
    bidRequestId: String,
    price:        Double,
    adid:         Option[String],
    banner:       Option[Banner])
