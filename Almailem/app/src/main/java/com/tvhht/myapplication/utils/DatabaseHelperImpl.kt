package com.tvhht.myapplication.utils

import com.tvhht.myapplication.quality.model.QualityListResponse

class DatabaseHelperImpl(private val gfgDatabase: WMSRoomDatabase) : DatabaseHelper {
    override suspend fun getQAList(heNo: String, orderNo: String): List<QualityListResponse> = gfgDatabase.submitDao().getAll(heNo, orderNo)
    override suspend fun insertAll(quality: List<QualityListResponse>) = gfgDatabase.submitDao().insertAll(quality)
    override suspend fun deleteALL() =gfgDatabase.submitDao().delete()
    override suspend fun getQAOrderList(orderNo: String): List<QualityListResponse> = gfgDatabase.submitDao().getAllOrderNo(orderNo)
    override suspend fun deleteHeNumber(heNumber: String): Int = gfgDatabase.submitDao().deleteHeNumber(heNumber)
    override suspend fun deleteSingleHeNumber(heNumber: String, orderNo: String): Int = gfgDatabase.submitDao().deleteSingleHeNumber(heNumber, orderNo)
}