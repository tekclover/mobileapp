package com.tvhht.myapplication.utils;

public class SubmitLocalRepository {
    private static SubmitLocalRepository sInstance;
    private final WMSRoomDatabase mDatabase;



    private SubmitLocalRepository(final WMSRoomDatabase database) {
        mDatabase = database;


    }

    public static SubmitLocalRepository getInstance(final WMSRoomDatabase database) {
        synchronized (SubmitLocalRepository.class) {
            if (sInstance == null) {
                sInstance = new SubmitLocalRepository(database);
            }
        }
        return sInstance;
    }




}
