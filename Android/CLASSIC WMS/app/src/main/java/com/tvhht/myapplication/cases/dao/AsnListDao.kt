package com.tvhht.myapplication.cases.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tvhht.myapplication.cases.model.AsnList

@Dao
interface AsnListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(asnListVO: List<AsnList?>?)

    @Query("SELECT * FROM AsnList WHERE refDocNumber = :refDocNum")
    fun getCaseDetails(refDocNum: String): LiveData<List<AsnList>>

    @Query("DELETE FROM AsnList")
    fun deleteAll()

    @get:Query("SELECT * from AsnList")
    val list: LiveData<List<AsnList?>?>?





}