package com.example.xposedemo.Hook;

import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.example.xposedemo.utils.Ut;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class HookShare {
    public final static String pathNewDeviceOrder="/sdcard/run.txt";
    public final static String pathDeviceJson="/sdcard/deviceJson.txt";
    private static final String TAG ="HookShare" ;

    public static boolean boolNew(){

        String ret=Ut.readFileToString( pathNewDeviceOrder );
        String json=Ut.readFileToString( pathDeviceJson );
        if (ret==""){
            return true;
        }else if (ret=="0") {
                return true;
        }else if( json=="" )
                return true;

        return false;
    }

  
    public static void WriteBean2Json( Object o ){

        Class c=o.getClass();
        Field [] fields=c.getDeclaredFields();
        Log.d(TAG, "WriteBean2Json: "+c.getName() );
        JSONObject jsonObject= (JSONObject) JSONObject.toJSON( o );
        Ut.fileWriterTxt ( pathDeviceJson, jsonObject.toJSONString() );

        Log.d(TAG, "WriteBean2Json: json="+jsonObject.toJSONString()  );

    }

}
