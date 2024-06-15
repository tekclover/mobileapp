package com.clara.timekeeping.model

import java.io.Serializable

data class SearchResult(
    var id: String? = null,
    var name: String? = null,
    var isChecked: Boolean = false
) : Serializable

