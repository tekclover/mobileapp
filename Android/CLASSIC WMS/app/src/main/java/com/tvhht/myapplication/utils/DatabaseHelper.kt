package com.tvhht.myapplication.utils

import com.tvhht.myapplication.quality.model.QualityListResponse

interface DatabaseHelper {
    suspend fun getQAList(he_no:String): List<QualityListResponse>
    suspend fun insertAll(Courses: List<QualityListResponse>)
    suspend fun deleteALL()
}
