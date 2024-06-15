package com.tvhht.myapplication.cases.model

import com.google.gson.annotations.SerializedName

data class CasesConfirmRequest(

    @SerializedName("caseCode") var caseCode: String? = null,
    @SerializedName("itemCode") var itemCode: String? = null,
    @SerializedName("lineNo") var lineNo: Int? = null,
    @SerializedName("palletCode") var palletCode: String? = null,
    @SerializedName("preInboundNo") var preInboundNo: String? = null,
    @SerializedName("refDocNumber") var refDocNumber: String? = null,
    @SerializedName("stagingNo") var stagingNo: String? = null,
    @SerializedName("warehouseId") var warehouseId: String? = null

)