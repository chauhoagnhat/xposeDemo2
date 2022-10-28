package com.example.xposedemo.Hook;

import android.content.ContentResolver;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.xposedemo.bean.BaseInfo;
import com.example.xposedemo.utils.MyFile;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class SimpleBaseHook {
    BaseInfo baseInfo;
    public SimpleBaseHook(XC_LoadPackage.LoadPackageParam loadPackageParam){
       // hookAll(  baseInfo,loadPackageParam  );
        hookTestGetValue();
    }

    public void hookTestGetValue(){
        XposedHelpers.findAndHookMethod(Settings.Secure.class, "getString", ContentResolver.class, String.class, new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                //accessibility_captioning_locale
                Object obj=param.getResult();
                if ( obj==null )
                    Log.d(TAG, "afterHookedMethod: systemHook="+param.args[1]+"-result=null");
                else
                    Log.d(TAG, "afterHookedMethod: systemHook="+param.args[1]+"-result="+obj.toString() );
                //+"result="+param.getResult().toString() );
                if (param.args[1].equals(Settings.Secure.ANDROID_ID)){
                    Log.d(TAG, "afterHookedMethod: android_id get="+param.getResult() );
                    // Log.d(TAG, "afterHookedMethod: android_id set="+jsonObject.get("android_id")  );
                    // param.setResult( jsonObject.get("android_id") );
                }

            }

        });

    }

    private static final String TAG = "SimpleBaseHook" ;

    public void hookAll(BaseInfo baseInfo,XC_LoadPackage.LoadPackageParam loadPackageParam){
        String json= null;
//        try {
//            json = MyFile.readFileToString( HookShare.pathDeviceJsonData );
//        } catch (Exception e) {
//            Log.d(TAG, "exception SimpleBaseHook: readFileToString"+e.toString() );
//            e.printStackTrace();
        /*    List aa=MyFile.execCmdsforResult( new String[]{
                    "cat "+HookShare.pathDeviceJsonData
            } );
            StringBuilder stringBuilder=new StringBuilder();
            for ( Object str :
                    aa ) {
                stringBuilder.append(str);
            }
            if ( aa==null)
                return;
            else
                json=stringBuilder.toString();*/
        //}
        json=MyFile.readFileToString(HookShare.pathDeviceJsonData );

        Log.d(TAG, "hookAll: ="+json );

        final JSONObject jsonObject= JSON.parseObject  ( json );
        XposedHelpers.setStaticObjectField(android.os.Build.class,
                "FINGERPRINT",jsonObject.get("fingerprint") );

        XposedHelpers.findAndHookMethod("android.os.SystemProperties", loadPackageParam.classLoader, "get", String.class, new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                // String finger = SystemProperties.get("ro.build.fingerprint");
                //Build.FINGERPRINT
                Log.d(TAG, "afterHookedMethod: SystemProperties-run-"+param.args[0] );

                String para = (String) param.args[0];
                if("gsm.version.baseband".equals(para)||"no message".equals(para)){
                    Log.d(TAG, "afterHookedMethod: baseBand="+jsonObject.get("baseBand")  );
                    param.setResult( jsonObject.get("baseBand") );
                }
             /*   else if (para.equals("ro.serialno")){
                    Log.d(TAG,"afterHookedMethod: realvalue="+param.getResult().toString() );
                    Log.d(TAG, "setHook: afterHookedMethod: ro.serialno set="+jsonObject.get("serial")  );
                    param.setResult( jsonObject.get("serial") );
                }else if ( para.equals( "ro.build.fingerprint" ) ){
                    Log.d(TAG,"afterHookedMethod: ro.build.fingerprint realvalue="+param.getResult().toString() );
                    Log.d(TAG, "setHook: afterHookedMethod: ro.build.fingerprint set="+jsonObject.get("fingerprint")  );
                }*/
            }
        });

        XposedHelpers.findAndHookMethod(android.net.wifi.WifiInfo.class, "getMacAddress", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                String result=jsonObject.getString("wifimac");
                Log.d(TAG, "setHook: afterHookedMethod: wifi hook="+result );
                param.setResult(result);

            }
        });

        XposedHelpers.findAndHookMethod(Settings.Secure.class, "getString", ContentResolver.class, String.class, new XC_MethodHook() {
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                //accessibility_captioning_locale
                Object obj=param.getResult();
                if ( obj==null )
                    Log.d(TAG, "afterHookedMethod: systemHook="+param.args[1]+"-result=null");
                else
                    Log.d(TAG, "afterHookedMethod: systemHook="+param.args[1]+"-result="+obj.toString() );
                //+"result="+param.getResult().toString() );
                if (param.args[1].equals(Settings.Secure.ANDROID_ID)){
                    Log.d(TAG, "afterHookedMethod: android_id get="+param.getResult() );
                   // Log.d(TAG, "afterHookedMethod: android_id set="+jsonObject.get("android_id")  );
                   // param.setResult( jsonObject.get("android_id") );
                }

            }

        });

    }

}
