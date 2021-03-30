package com.example.xposedemo.Utis;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {

    SharedPreferences sp;
    SharedPreferences.Editor edit;
    Context context;

    public SharedPreferencesHelper(Context context, String name){

        this.context=context;
        sp=context.getSharedPreferences(name,0);
        edit=sp.edit();
    }

    public void putValue(String key,String value){

        edit.putString(key,value);
        edit.commit();

    }
    public String getValue(String key){
        return sp.getString(key,null);
    }

}
