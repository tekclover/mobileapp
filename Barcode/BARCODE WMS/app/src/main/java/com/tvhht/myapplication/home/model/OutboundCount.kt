package com.tvhht.myapplication.home.model

import com.google.gson.annotations.SerializedName


data class OutboundCount (

  @SerializedName("picking"   ) var picking   : Int? = null,
  @SerializedName("quality"   ) var quality   : Int? = null,
  @SerializedName("reversals" ) var reversals : Int? = null

)