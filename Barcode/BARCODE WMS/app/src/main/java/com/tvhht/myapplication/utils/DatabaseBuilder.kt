package com.tvhht.myapplication.utils

import android.content.Context
import androidx.room.Room

object DatabaseBuilder {
    private var INSTANCE: WMSRoomDatabase? = null
    fun getInstance(context: Context): WMSRoomDatabase {
        if (INSTANCE == null) {
            synchronized(WMSRoomDatabase::class) {
                INSTANCE = buildRoomDB(context)
            }
        }
        return INSTANCE!!
    }
    private fun buildRoomDB(context: Context) =
        Room.databaseBuilder(
            context.applicationContext,
            WMSRoomDatabase::class.java,
            "WMSClassic-Database"
        ).build()
}