package com.tvhht.myapplication.utils

import com.tvhht.myapplication.quality.model.QualityListResponse

interface DatabaseHelper {
    suspend fun getQAList(heNo: String, orderNo: String): List<QualityListResponse>
    suspend fun insertAll(quality: List<QualityListResponse>)
    suspend fun deleteALL()
    suspend fun getQAOrderList(orderNo:String): List<QualityListResponse>
    suspend fun deleteHeNumber(heNumber: String): Int
    suspend fun deleteSingleHeNumber(heNumber: String, orderNo: String): Int
}
