package com.example.xposedemo.Hook;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.xposedemo.utils.MyFile;
import com.example.xposedemo.utils.Ut;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookShare {

    public final static String pathNkFolder="/sdcard/nk";
    public final static String pathNewDeviceOrder="/sdcard/nk/run.txt";
    public final static String pathDeviceJson="/sdcard/nk/deviceJson.txt";
    public final static String  pathPackages ="/sdcard/nk/packages.txt";
    public final static String  pathSelectedPackages ="/sdcard/nk/selectedPackages.txt";

    private static final String TAG ="HookShare" ;
    public static final String PATH_PHONE_DEVICE = "/sdcard/nk/devicePhone.txt";
    public static final String PATH_FUNCTION_PACKAGES="/sdcard/nk/packagesFunction.txt";
    public static final String PATH_BACK_PATH="/sdcard/nk/PATH_BACK_PATH.txt";
    public static final String PATH_DATABACK_JSON="/sdcard/nk/PATH_DATABACK_JSON.txt";
    public static final String PATH_DATABACK_SELECTED_PATH="/sdcard/nk/PATH_DATABACK_SELECTED_PATH.txt";
    public static final String PATH_DATA_USER="/data/user/0";
    public static final String PATH_DEVICE_PHONE="/sdcard/nk/devicePhone.txt";
    public static final String PATH_UI_SETTING="/sdcard/nk/ui.txt";
    public static final String PATH_SCRIPT_RUNNING="/sdcard/nk/script.txt";
    public static Context context;


    static {
        Ut.crFolder( pathNkFolder );
    }

    public static String getDataBackPath( String packageName ){
        return "/nk/back/"+packageName+"/"+System.currentTimeMillis();
    }

    public static String getPathFunctionPackages(){
        return MyFile.readFileToString( PATH_FUNCTION_PACKAGES );
    }

    public static Context getCurAppContext(){
        return  AndroidAppHelper.currentApplication().getApplicationContext();
    }

    public static boolean boolSelectedPackages( XC_LoadPackage.LoadPackageParam loadPackageParam ){

        String json= MyFile.readFileToString( HookShare.pathPackages );
        JSONObject jsonObject= JSON.parseObject(json);
        JSONObject jobj2=new JSONObject();

        if( jsonObject ==null)
                return false;
        for ( Map.Entry<String,Object> entry :
                jsonObject.entrySet() ) {
            if( (boolean)entry.getValue() ){
                jobj2.put( entry.getKey(),true );
            }
        }

        if (jobj2==null)
            return false;

        boolean ret=false;
        if (jobj2.containsValue(true)){
            for ( Map.Entry<String,Object> entry :
            jobj2.entrySet() ) {
                if ( entry.getKey().equals( loadPackageParam.packageName )||loadPackageParam.packageName.indexOf("ugc.aweme")>0   )
                    ret=true;
            }
        }
        return  ret;

    }

    public static Context getContext(){
        return AndroidAppHelper.currentApplication();
    }

    /**
     * 返回已经选择的包名
     * @return
     */
    public static JSONObject returnSelectedPackages( ) {

        String json= Ut.readFileToString( HookShare.pathPackages );
        JSONObject jsonObject= JSON.parseObject(json);
        JSONObject jobj2=null;

        if( jsonObject ==null)
            return null;
        for ( Map.Entry<String,Object> entry :
                jsonObject.entrySet() ) {
            if( (boolean)entry.getValue() ){
                if( jobj2==null ){
                    jobj2=new JSONObject();
                }
                jobj2.put( entry.getKey(),true );
            }
        }
            return jobj2;
    }


    public static boolean boolIsPackageEmpty( ) {
        String json= MyFile.readFileToString( HookShare.pathPackages );
        JSONObject jsonObject= JSON.parseObject(json);
        JSONObject jobj2=new JSONObject();

        if( jsonObject ==null)
            return false;
        for ( Map.Entry<String,Object> entry :
                jsonObject.entrySet() ) {
            if( (boolean)entry.getValue() ){
                jobj2.put( entry.getKey(),true );
            }
        }

        return jobj2.containsValue(true);

    }

    public static boolean boolNew(){

        String ret= MyFile.readFileToString( pathNewDeviceOrder );
        String json=MyFile.readFileToString( pathDeviceJson );
        if (ret==""){
            return true;
        }else if (ret=="0") {
                return true;
        }else if( json=="" )
                return true;

        return false;
    }

  
    public static void WriteBean2Json(Object o, String pathJson ){

        Class c=o.getClass();
        Field [] fields=c.getDeclaredFields();
        Log.d(TAG, "WriteBean2Json: "+c.getName() );
        JSONObject jsonObject= (JSONObject) JSONObject.toJSON( o );
        Ut.fileWriterTxt (pathJson, jsonObject.toJSONString() );
        Log.d(TAG, "WriteBean2Json: json="+jsonObject.toJSONString()  );

    }

}
