package com.example.xposedemo.Utis;

import android.app.Application;
import android.content.Context;

public class BaseContext extends Application {

    private static Context mContext;
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
    public static Context getContext(){
        return mContext;
    }


}
