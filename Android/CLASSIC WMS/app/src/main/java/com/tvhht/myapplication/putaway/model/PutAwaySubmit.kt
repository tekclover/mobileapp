package com.tvhht.myapplication.putaway.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class PutAwaySubmit(@PrimaryKey(autoGenerate = true) val putAwayID: Int? = null,
                         @SerializedName("assignedUserId") var assignedUserId: String? = null,
                         @SerializedName("companyCodeId") var companyCodeId: String? = null,
                         @SerializedName("confirmedBy") var confirmedBy: String? = null,
                         @SerializedName("confirmedOn") var confirmedOn: String? = null,
                         @SerializedName("confirmedStorageBin") var confirmedStorageBin: String? = null,
                         @SerializedName("createdBy") var createdBy: String? = null,
                         @SerializedName("createdOn") var createdOn: String? = null,
                         @SerializedName("deletionIndicator") var deletionIndicator: Int? = null,
                         @SerializedName("expiryDate") var expiryDate: String? = null,
                         @SerializedName("goodsReceiptNo") var goodsReceiptNo: String? = null,
                         @SerializedName("itemCode") var itemCode: String? = null,
                         @SerializedName("languageId") var languageId: String? = null,
                         @SerializedName("lineNo") var lineNo: Int? = null,
                         @SerializedName("manufacturerDate") var manufacturerDate: String? = null,
                         @SerializedName("outboundOrderTypeId") var outboundOrderTypeId: Int? = null,
                         @SerializedName("packBarcodes") var packBarcodes: String? = null,
                         @SerializedName("plantId") var plantId: String? = null,
                         @SerializedName("preInboundNo") var preInboundNo: String? = null,
                         @SerializedName("proposedStorageBin") var proposedStorageBin: String? = null,
                         @SerializedName("putAwayNumber") var putAwayNumber: String? = null,
                         @SerializedName("putAwayQuantity") var putAwayQuantity: Int? = null,
                         @SerializedName("putAwayUom") var putAwayUom: String? = null,
                         @SerializedName("putawayConfirmedQty") var putawayConfirmedQty: Int? = null,
                         @SerializedName("quantityType") var quantityType: String? = null,
                         @SerializedName("refDocNumber") var refDocNumber: String? = null,
                         @SerializedName("specialStockIndicatorId") var specialStockIndicatorId: Int? = null,
                         @SerializedName("stockTypeId") var stockTypeId: Int? = null,
                         @SerializedName("warehouseId") var warehouseId: String? = null,
                         @SerializedName("itemDescription") var itemDescription: String? = null

)