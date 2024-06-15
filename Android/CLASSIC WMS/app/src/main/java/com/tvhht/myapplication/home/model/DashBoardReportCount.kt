package com.tvhht.myapplication.home.model

import com.google.gson.annotations.SerializedName


data class DashBoardReportCount (

  @SerializedName("inboundCount"  ) var inboundCount  : InboundCount?  = InboundCount(),
  @SerializedName("outboundCount" ) var outboundCount : OutboundCount? = OutboundCount(),
  @SerializedName("stockCount"    ) var stockCount    : String?        = null

)