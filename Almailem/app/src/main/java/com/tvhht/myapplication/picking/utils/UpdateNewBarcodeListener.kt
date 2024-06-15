package com.tvhht.myapplication.picking.utils

import com.tvhht.myapplication.picking.model.AddNewBarcodeResponse

interface UpdateNewBarcodeListener {
    fun onBarcodeUpdate(response:AddNewBarcodeResponse)
}