package com.tvhht.myapplication.utils

import com.tvhht.myapplication.quality.model.QualityListResponse

class DatabaseHelperImpl(private val gfgDatabase: WMSRoomDatabase) : DatabaseHelper {
    override suspend fun getQAList(he_no : String): List<QualityListResponse> = gfgDatabase.submitDao().getAll(he_no)
    override suspend fun insertAll(Courses: List<QualityListResponse>) = gfgDatabase.submitDao().insertAll(Courses)
    override suspend fun deleteALL() =gfgDatabase.submitDao().delete()
}