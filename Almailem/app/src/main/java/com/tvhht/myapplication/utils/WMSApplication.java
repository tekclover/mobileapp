package com.tvhht.myapplication.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.tvhht.myapplication.cases.dao.CasesLocalRepository;

import java.lang.ref.WeakReference;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.multidex.MultiDexApplication;


public class WMSApplication extends MultiDexApplication implements LifecycleObserver {
    private WeakReference<Activity> mForegroundActivity;
    private AppExecutors mAppExecutors;

    private boolean isAppInForeground;
    private boolean isAppInBackground;
    private boolean isAppUpgradeCheckNeeded = false;



    public  Context getContext() {
        return AppInstance.getInstance().getContext().getApplicationContext();
    }

    public static WMSApplication getInstance() {
        return AppInstance.getInstance().getContext();
    }

    public static Context getContextStatic() {
        return AppInstance.getInstance().getContext().getApplicationContext();
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //  initStrictMode();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        AppInstance worker = new AppInstance();
        worker.init(this);
        mAppExecutors = new AppExecutors();


        ProcessLifecycleOwner.get().getLifecycle().addObserver(this);


    }







    public WMSRoomDatabase getDatabase() {
        return WMSRoomDatabase.getInstance(AppInstance.getInstance().getContext().getApplicationContext());
    }



    public CasesLocalRepository getCasesLocalRepository() {
        return CasesLocalRepository.getInstance(getDatabase());
    }

    public SubmitLocalRepository getSubmitLocalRepository() {
        return SubmitLocalRepository.getInstance(getDatabase());
    }



    public AppExecutors getExecutors() {
        return mAppExecutors;
    }



    public boolean isInternetConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null)
            activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public boolean isAppInForeground() {
        return isAppInForeground;
    }
    public boolean isAppInBackground() {
        return isAppInBackground;
    }


    public boolean isAppUpgradeCheckNeeded() {
        return isAppUpgradeCheckNeeded;
    }

    public void setAppUpgradeCheckNeeded(boolean appUpgradeCheckNeeded) {
        isAppUpgradeCheckNeeded = appUpgradeCheckNeeded;
    }
}
