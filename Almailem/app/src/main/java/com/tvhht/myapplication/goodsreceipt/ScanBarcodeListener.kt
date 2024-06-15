package com.tvhht.myapplication.goodsreceipt

import com.tvhht.myapplication.goodsreceipt.model.SelectedDocumentResponse

interface ScanBarcodeListener {
    fun onReload(document:SelectedDocumentResponse)
}