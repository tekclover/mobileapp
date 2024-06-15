package com.clara.timekeeping.model

data class SearchEvent(
    var matterList: List<String> = mutableListOf(),
    var billTypeList: List<String> = mutableListOf(),
    var statusList: List<Int> = mutableListOf(),
    var timekeeperCodeList: List<String> = mutableListOf(),
    var startDateList: List<String> = mutableListOf(),
    var endDateList: List<String> = mutableListOf()
)
