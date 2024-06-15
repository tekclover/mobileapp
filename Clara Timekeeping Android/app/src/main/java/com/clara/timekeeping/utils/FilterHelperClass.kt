package com.clara.timekeeping.utils

import com.clara.timekeeping.model.SearchResult

object FilterHelperClass {
    var selectedFilterList: ArrayList<Map<String, ArrayList<SearchResult>>> = arrayListOf()
    var singleSelectionSearchList: ArrayList<Map<String, ArrayList<SearchResult>>> = arrayListOf()
    fun removeSearchSelection(
        removeItem: String,
        selectedSearch: String,
        selectedSearchList: ArrayList<Map<String, ArrayList<SearchResult>>>
    ) {
        if (selectedSearchList.size > 0) {
            val parentIterator = selectedSearchList.iterator()
            while (parentIterator.hasNext()) {
                val entryItem = parentIterator.next()
                for (key in entryItem.keys) {
                    val childIterator = entryItem[key]?.iterator()
                    while (childIterator?.hasNext() == true) {
                        val childItem = childIterator.next()
                        if (childItem.id == removeItem) childIterator.remove()
                    }
                }
                if (entryItem.containsKey(selectedSearch) && entryItem.getValue(selectedSearch)
                        .isNullOrEmpty()
                ) {
                    parentIterator.remove()
                }
            }
        }
    }

    fun addSelectedSearch(selectedItem: SearchResult, key: String) {
        if (selectedFilterList.size > 0) {
            var isKeyExists = false
            for (entryItem in selectedFilterList) {
                if (entryItem.containsKey(key)) {
                    isKeyExists = true
                    val filterItemList = entryItem[key]
                    filterItemList?.add(selectedItem)
                    break
                }
            }
            if (!isKeyExists) {
                saveSelectedSearchOption(selectedItem, key)
            }
        } else {
            saveSelectedSearchOption(selectedItem, key)
        }

    }

    private fun saveSelectedSearchOption(selectedItem: SearchResult, key: String) {
        val map: MutableMap<String, ArrayList<SearchResult>> = HashMap()
        val optionList = ArrayList<SearchResult>()
        optionList.add(selectedItem)
        map[key] = optionList
        selectedFilterList.add(map)
    }

    fun clearSearch() {
/*
        if (selectedFilterList.size > 0) {
            val parentIterator = selectedFilterList.iterator()
            while (parentIterator.hasNext()) {
                parentIterator.remove()
            }
        }
*/
        selectedFilterList = arrayListOf()
    }

    fun addSingleSearchOption(selectedItem: SearchResult, key: String) {
        if (singleSelectionSearchList.size > 0) {
            var isKeyExists = false
            for (entryItem in singleSelectionSearchList) {
                if (entryItem.containsKey(key)) {
                    isKeyExists = true
                    val filterItemList = entryItem[key]
                    filterItemList?.clear()
                    filterItemList?.add(selectedItem)
                    break
                }
            }
            if (!isKeyExists) {
                saveSingleSelectionSearchOption(selectedItem, key)
            }
        } else {
            saveSingleSelectionSearchOption(selectedItem, key)
        }
    }

    private fun saveSingleSelectionSearchOption(selectedItem: SearchResult, key: String) {
        val map: MutableMap<String, ArrayList<SearchResult>> = HashMap()
        val optionList = ArrayList<SearchResult>()
        optionList.add(selectedItem)
        map[key] = optionList
        singleSelectionSearchList.add(map)
    }

    fun clearSingleSelectionSearch() {
        singleSelectionSearchList = arrayListOf()
    }

    fun removeDate(key: String) {
        if (selectedFilterList.size > 0) {
            val parentIterator = selectedFilterList.iterator()
            while (parentIterator.hasNext()) {
                val entryItem = parentIterator.next()
                if (entryItem.containsKey(key)) {
                    parentIterator.remove()
                }
            }
        }
    }
}