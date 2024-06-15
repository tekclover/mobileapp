package com.tvhht.myapplication.utils;

import com.tvhht.myapplication.picking.model.PickingSubmitRequest;
import com.tvhht.myapplication.putaway.model.PutAwaySubmit;
import com.tvhht.myapplication.quality.model.QualityModel;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.room.Transaction;


public class SubmitLocalRepository {
    private static SubmitLocalRepository sInstance;
    private final WMSRoomDatabase mDatabase;
    private MediatorLiveData<List<PutAwaySubmit>> mObservableLists;
    private MediatorLiveData<List<QualityModel>> QualityModelList;


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


    public LiveData<List<QualityModel>> qualityList() {

        QualityModelList = new MediatorLiveData<>();
        QualityModelList.addSource(mDatabase.submitDao().getListQuality(),
                listVO -> {
                    QualityModelList.postValue(listVO);

                });

        return QualityModelList;
    }


    public MediatorLiveData<List<PutAwaySubmit>> fetchListData() {

        mObservableLists = new MediatorLiveData<>();
        mObservableLists.addSource(mDatabase.submitDao().getListPutAway(),
                listVO -> {
                    mObservableLists.postValue(listVO);

                });

        return mObservableLists;
    }


    @Transaction
    public void insertPutawayListVo(final List<PutAwaySubmit> listVO
    ) {

        WMSApplication.getInstance().getExecutors().diskIO().execute(() -> mDatabase.submitDao().insertPutAway(listVO));

    }

    @Transaction
    public void insertPickingListVo(final List<PickingSubmitRequest> listVO
    ) {

        WMSApplication.getInstance().getExecutors().diskIO().execute(() -> mDatabase.submitDao().insertPicking(listVO));

    }

    @Transaction
    public void insertQuality(final List<QualityModel> listVO
    ) {

        WMSApplication.getInstance().getExecutors().diskIO().execute(() -> mDatabase.submitDao().insertQuality(listVO));

    }

    @Transaction
    public void delete() {

        WMSApplication.getInstance().getExecutors().diskIO().execute(() -> mDatabase.submitDao().deletePutAway());

    }


}
