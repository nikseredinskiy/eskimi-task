package com.eskimi.task.service.impl

import com.eskimi.task.entity.{ BidRequest, BidResponse, Campaign, Impression }
import com.eskimi.task.service.BidCheckService

object BidCheckServiceImpl extends BidCheckService {

  override def check(campaigns: Seq[Campaign], request: BidRequest): Option[BidResponse] = {
    toBidResponse(
      request,
      filterByImpression(
        campaigns
          .filter(filterByCountry(_, request))
          .filter(filterByTargetSiteId(_, request)),
        request
      )
    )
  }

  private def toBidResponse(request: BidRequest, result: Map[Impression, Seq[Campaign]]): Option[BidResponse] = {
    for {
      item <- result.headOption //can be changed in case of a need to return BidResponse for every Impression from request
      camp <- item._2.headOption
      banners <- Option(camp.banners).filter(_.nonEmpty) //to not return campaigns without proper banners
    } yield {
      BidResponse(
        id = None,
        bidRequestId = request.id,
        price = item._1.bidFloor.getOrElse(0),
        adid = Some(camp.id.toString),
        banner = banners.headOption
      )
    }
  }

  private def filterByCountry(campaign: Campaign, request: BidRequest): Boolean = {
    request.device.exists(_.geo.exists(_.country.contains(campaign.country))) ||
    request.user.exists(_.geo.exists(_.country.contains(campaign.country)))
  }

  private def filterByTargetSiteId(campaign: Campaign, request: BidRequest): Boolean = {
    campaign.targeting.targetedSiteIds.contains(request.site.id)
  }

  private def filterByImpression(campaigns: Seq[Campaign], request: BidRequest): Map[Impression, Seq[Campaign]] = {
    request.imp
      .map(impressions => {
        impressions
          .flatMap(impression => {
            Seq(
              impression -> campaigns
                .filter(campaign => impression.bidFloor.exists(_ < campaign.bid))
                .map(filterBannersByImageProps(_, impression))
            )
          })
          .toMap
      })
      .getOrElse(Map())
  }

  private def filterBannersByImageProps(campaign: Campaign, impression: Impression): Campaign = {
    campaign.copy(
      banners = campaign.banners.filter(
        banner =>
          filterByImageProps(impression.h, impression.hmin, impression.hmax, banner.height) &&
            filterByImageProps(impression.w, impression.wmin, impression.wmax, banner.width)
      )
    )
  }

  private def filterByImageProps(
      value:       Option[Int],
      minValue:    Option[Int],
      maxValue:    Option[Int],
      bannerValue: Int
    ): Boolean = {
    (value, minValue, maxValue) match {
      case (Some(v), _, _)           => v == bannerValue
      case (_, Some(min), Some(max)) => min <= bannerValue && bannerValue <= max
      case (_, Some(min), _)         => min <= bannerValue
      case (_, _, Some(max))         => max >= bannerValue
      case _                         => false
    }
  }

}
