package com.tvhht.myapplication.utils;

import android.content.Context;

import com.tvhht.myapplication.cases.dao.AsnListDao;
import com.tvhht.myapplication.cases.model.AsnList;
import com.tvhht.myapplication.picking.model.PickingSubmitRequest;
import com.tvhht.myapplication.putaway.model.PutAwaySubmit;
import com.tvhht.myapplication.quality.model.QualityListResponse;
import com.tvhht.myapplication.quality.model.QualityModel;

import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities =
        {AsnList.class, QualityListResponse.class, PutAwaySubmit.class, QualityModel.class, PickingSubmitRequest.class}
        , version = 1
        , exportSchema = false)

public abstract class WMSRoomDatabase extends RoomDatabase {
    @VisibleForTesting
    private static final String DATABASE_NAME = "WMSClassic-Database";
    private static WMSRoomDatabase sInstance;
    private final MutableLiveData<Boolean> mIsDatabaseCreated = new MutableLiveData<>();

    private static WMSRoomDatabase getDatabase(final Context context) {
        synchronized (WMSRoomDatabase.class) {
            if (sInstance == null) {
                sInstance = Room.databaseBuilder(context.getApplicationContext(),
                        WMSRoomDatabase.class, DATABASE_NAME)
                        .allowMainThreadQueries()
                        //  for version increased, fallback to destructive migration enabled — database is cleared
                        .fallbackToDestructiveMigration()
                        .build();

            }
        }

        return sInstance;
    }

    public static WMSRoomDatabase getInstance(final Context context) {
        synchronized (WMSRoomDatabase.class) {
            if (sInstance == null) {
                sInstance = getDatabase(context.getApplicationContext());
                       }
        }

        return sInstance;
    }


    public abstract AsnListDao asnListDao();
    public abstract SubmitDao submitDao();

    /**
     * Check whether the database already exists and expose it via {@link #getDatabaseCreated()}
     */
    private void updateDatabaseCreated(final Context context) {
        if (context.getDatabasePath(DATABASE_NAME).exists()) {
            setDatabaseCreated();
        }
    }

    private void setDatabaseCreated() {
        mIsDatabaseCreated.postValue(true);
    }

    public LiveData<Boolean> getDatabaseCreated() {
        return mIsDatabaseCreated;
    }



}
