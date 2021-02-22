package com.eskimi.task.entity

case class Impression(
    id:       String,
    wmin:     Option[Int],
    wmax:     Option[Int],
    w:        Option[Int],
    hmin:     Option[Int],
    hmax:     Option[Int],
    h:        Option[Int],
    bidFloor: Option[Double])
