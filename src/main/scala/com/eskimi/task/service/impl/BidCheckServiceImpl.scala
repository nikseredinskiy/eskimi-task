package com.eskimi.task.service.impl

import com.eskimi.task.actor.{ BidRequest, Campaign, Impression }
import com.eskimi.task.service.BidCheckService

class BidCheckServiceImpl extends BidCheckService {

  override def check(campaigns: Seq[Campaign], request: BidRequest): Map[Impression, Seq[Campaign]] = {
    filterByImpression(
      campaigns
        .filter(filterByCountry(_, request))
        .filter(filterByTargetSiteId(_, request)),
      request
    )
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
    value
      .map(height => height == bannerValue)
      .orElse(Some(minValue.exists(_ <= bannerValue) && maxValue.exists(_ >= bannerValue)))
      .orElse(Some(maxValue.exists(_ >= bannerValue)))
      .orElse(Some(minValue.exists(_ <= bannerValue)))
      .getOrElse(false)
  }

}
