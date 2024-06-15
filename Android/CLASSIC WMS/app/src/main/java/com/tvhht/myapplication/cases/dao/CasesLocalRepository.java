package com.tvhht.myapplication.cases.dao;

import com.tvhht.myapplication.cases.model.AsnList;
import com.tvhht.myapplication.utils.WMSApplication;
import com.tvhht.myapplication.utils.WMSRoomDatabase;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.room.Transaction;


public class CasesLocalRepository {
    private static CasesLocalRepository sInstance;
    private final WMSRoomDatabase mDatabase;
    private MediatorLiveData<List<AsnList>> mObservableLists;
    private MediatorLiveData<List<AsnList>> mObservableString;



    private CasesLocalRepository(final WMSRoomDatabase database) {
        mDatabase = database;


    }

    public static CasesLocalRepository getInstance(final WMSRoomDatabase database) {
        synchronized (CasesLocalRepository.class) {
            if (sInstance == null) {
                sInstance = new CasesLocalRepository(database);
            }
        }
        return sInstance;
    }


    public LiveData<List<AsnList>> fetchListData() {

        mObservableLists = new MediatorLiveData<>();
        mObservableLists.addSource(mDatabase.asnListDao().getList(),
                listVO -> {
                      mObservableLists.postValue(listVO);

                });

        return mObservableLists;
    }


    public LiveData<List<AsnList>> fetchData(String ids) {

        mObservableString = new MediatorLiveData<>();
        mObservableString.addSource(mDatabase.asnListDao().getCaseDetails(ids),
                listVO -> {
                    mObservableString.postValue(listVO);

                });

        return mObservableString;
    }


    @Transaction
    public void insertListVo(final List<AsnList> listVO
    ) {

        WMSApplication.getInstance().getExecutors().diskIO().execute(() -> mDatabase.asnListDao().insert(listVO));

    }

    @Transaction
    public void delete() {

        WMSApplication.getInstance().getExecutors().diskIO().execute(() -> mDatabase.asnListDao().deleteAll());

    }



}
