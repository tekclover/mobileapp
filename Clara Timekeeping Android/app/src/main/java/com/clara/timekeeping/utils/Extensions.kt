package com.clara.timekeeping.utils

import android.view.View
import java.text.DecimalFormat

fun Number?.toDollar(): String {
    if (this == null) return ""
    val formattedString = DecimalFormat("0.00").format(this)
    return "\u0024$formattedString"
}

fun View.setEnable(isEnable: Boolean) {
    this.isEnabled = isEnable
    this.isClickable = isEnable
}
