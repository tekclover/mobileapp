package com.tvhht.myapplication.utils

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tvhht.myapplication.cases.model.AsnList
import com.tvhht.myapplication.picking.model.PickingSubmitRequest
import com.tvhht.myapplication.putaway.model.PutAwaySubmit
import com.tvhht.myapplication.quality.model.QualityListResponse
import com.tvhht.myapplication.quality.model.QualityModel

@Dao
interface SubmitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPutAway(submit: List<PutAwaySubmit?>?)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPicking(submit: List<PickingSubmitRequest?>?)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuality(submit: List<QualityModel?>?)

    @Query("DELETE FROM PutAwaySubmit")
    fun deletePutAway()

    @get:Query("SELECT * from PutAwaySubmit")
    val listPutAway: LiveData<List<PutAwaySubmit?>?>?

    @get:Query("SELECT * from QualityModel")
    val listQuality: LiveData<List<QualityModel?>?>?


    @Query("SELECT * FROM QualityListResponse where actualHeNo = :actualHeNo AND (refDocNumber = :orderNo OR salesOrderNumber = :orderNo)")
    suspend fun getAll(actualHeNo: String, orderNo: String): List<QualityListResponse>

    @Query("SELECT * FROM QualityListResponse where refDocNumber =:orderNo OR salesOrderNumber =:orderNo")
    suspend fun getAllOrderNo(orderNo: String): List<QualityListResponse>

    @Insert
    suspend fun insertAll(qualityListResponse: List<QualityListResponse>)

    @Query("DELETE FROM QualityListResponse")
    suspend fun delete()

    @Query("DELETE FROM QualityListResponse where actualHeNo  =:heNumber")
    suspend fun deleteHeNumber(heNumber: String): Int

    @Query("DELETE FROM QualityListResponse where actualHeNo  =:heNumber AND (refDocNumber = :orderNo OR salesOrderNumber = :orderNo)")
    suspend fun deleteSingleHeNumber(heNumber: String, orderNo: String): Int
}