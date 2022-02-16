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
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookShare {
    
    public final static String pathDataRoot="/data/local/tmp/";
    public final static String sdcardRoot="/sdcard/";
    public final static String BootBroadcastReceiver="BootBroadcastReceiver";
    public final static String BootBroadcastReceiverState="state";
    public final static int BootBroadcastReceiverBooted=1;
    public final static int BootBroadcastReceiverDefault=0;

    public final static String configPhoneCountryCode="configPhoneCountryCode";
    //public final static String configCountry="configPhoneCountryCode";

    public final static String pathNkFolder=sdcardRoot+"nk";
    public final static String pathNewDeviceOrder=sdcardRoot+"nk/run.txt";
    public final static String pathDeviceJson=sdcardRoot+"nk/deviceJson.txt";
    public final static String  pathPackages =sdcardRoot+"nk/packages.txt";
    public final static String  pathSelectedPackages =sdcardRoot+"nk/selectedPackages.txt";

    public final static String pathNkFolderData=pathDataRoot+"nk";
    public final static String  pathUiSettingData =pathDataRoot+"nk/ui.txt";
    public final static String pathNewDeviceOrderData=pathDataRoot+"nk/run.txt";
    public final static String pathDeviceJsonData=pathDataRoot+"nk/deviceJson.txt";
    public final static String  pathPackagesData =pathDataRoot+"nk/packages.txt";
    public final static String  pathSelectedPackagesData =pathDataRoot+"nk/selectedPackages.txt";


    private static final String TAG ="HookShare" ;
    public static final String PATH_PHONE_DEVICE = sdcardRoot+"nk/devicePhone.txt";
    public static final String PATH_FUNCTION_PACKAGES=sdcardRoot+"nk/packagesFunction.txt";
    public static final String PATH_BACK_PATH=sdcardRoot+"nk/PATH_BACK_PATH.txt";
    public static final String PATH_DATABACK_JSON=sdcardRoot+"nk/PATH_DATABACK_JSON.txt";
    public static final String PATH_DATABACK_SELECTED_PATH=sdcardRoot+"nk/PATH_DATABACK_SELECTED_PATH.txt";
    public static final String PATH_DATA_USER="/data/user/0";
    public static final String PATH_DEVICE_PHONE=sdcardRoot+"nk/devicePhone.txt";
    public static final String PATH_UI_SETTING=sdcardRoot+"nk/ui.txt";
    public static final String PATH_SCRIPT_RUNNING=sdcardRoot+"nk/script.txt";

    public static final String PATH_PHONE_DEVICE_DATA = pathDataRoot+"nk/devicePhone.txt";

    public static final String PATH_FUNCTION_PACKAGES_DATA =pathDataRoot+"nk/packagesFunction.txt";
    public static final String PATH_BACK_PATH_DATA =pathDataRoot+"nk/PATH_BACK_PATH.txt";
    public static final String PATH_DATABACK_JSON_DATA =pathDataRoot+"nk/PATH_DATABACK_JSON.txt";
    public static final String PATH_DATABACK_SELECTED_PATH_DATA =pathDataRoot+"nk/PATH_DATABACK_SELECTED_PATH.txt";
    public static final String PATH_DEVICE_PHONE_DATA =pathDataRoot+"nk/devicePhone.txt";
    //public static final String PATH_DEVICE_PHONE_DATA =pathDataRoot+"devicePhone.txt";
    public static final String PATH_UI_SETTING_DATA =pathDataRoot+"nk/ui.txt";
    public static final String PATH_SCRIPT_RUNNING_DATA =pathDataRoot+"nk/script.txt";


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

        String json=MyFile.readFileToString( pathPackagesData );
        Log.d(TAG, "boolSelectedPackages: read="
        +json);

  /*      List aa=MyFile.execCmdsforResult( new String[]{
                "cat "+HookShare.pathPackagesData
        } );
        StringBuilder stringBuilder=new StringBuilder();
        for ( Object str :
                aa ) {
            Log.d(TAG, "Telephony: cat="+str );
            stringBuilder.append(str);
        }
        if ( aa==null){
            Log.d(TAG, "Telephony: cat= aa=null" );
            return false;
        }

        else
            json=stringBuilder.toString();*/

        Log.d(TAG, "boolSelectedPackages: "+json );

        //json= MyFile.readFileToString( HookShare.pathPackagesData );
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
                if ( entry.getKey().equals( loadPackageParam.packageName )||loadPackageParam.packageName.indexOf("jp.naver.line.android")>0   )
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
