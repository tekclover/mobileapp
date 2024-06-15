package com.tvhht.myapplication.utils;



public class AppInstance {
    private static WMSApplication context;

    public void init(WMSApplication appContext) { // only call from App!
        context = appContext;
    }

    public WMSApplication getContext() {
        return context;
    }

    private static AppInstance mInstance = new AppInstance();
    public static AppInstance getInstance() {
        synchronized (AppInstance.class) {
            if (mInstance == null) {
                mInstance = new AppInstance();
            }
        }
        return mInstance;
    }
}
