package com.tvhht.myapplication.home.model

import com.google.gson.annotations.SerializedName


data class InboundCount (

  @SerializedName("cases"     ) var cases     : Int? = null,
  @SerializedName("putaway"   ) var putaway   : Int? = null,
  @SerializedName("reversals" ) var reversals : Int? = null

)