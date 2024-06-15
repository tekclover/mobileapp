package com.clara.timekeeping.model

data class FilterData(
    val filterList: MutableList<SearchResult>? = null,
    val isSingleSelect: Boolean = false,
    val searchOption: String = ""
)
