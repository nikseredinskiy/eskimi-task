package com.eskimi.task.entity

case class Campaign(id: Int, country: String, targeting: Targeting, banners: List[Banner], bid: Double)
