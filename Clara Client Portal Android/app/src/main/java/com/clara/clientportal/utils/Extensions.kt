package com.clara.clientportal.utils

import java.text.DecimalFormat

fun Number?.toDollar(): String {
    val formattedString = DecimalFormat("0.00").format(this)
    if (this == null) return ""
    return "\u0024$formattedString"
}