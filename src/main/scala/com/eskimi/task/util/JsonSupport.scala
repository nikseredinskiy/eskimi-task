package com.eskimi.task.util

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.eskimi.task.entity._
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

object JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val bannerFormat: RootJsonFormat[Banner] = jsonFormat4(Banner)
  implicit val geoFormat: RootJsonFormat[Geo] = jsonFormat1(Geo)
  implicit val deviceFormat: RootJsonFormat[Device] = jsonFormat2(Device)
  implicit val userFormat: RootJsonFormat[User] = jsonFormat2(User)
  implicit val siteFormat: RootJsonFormat[Site] = jsonFormat2(Site)
  implicit val impressionFormat: RootJsonFormat[Impression] = jsonFormat8(Impression)

  implicit val bidRequestFormat: RootJsonFormat[BidRequest] = jsonFormat5(BidRequest)
  implicit val bidResponseFormat: RootJsonFormat[BidResponse] = jsonFormat5(BidResponse)
}
