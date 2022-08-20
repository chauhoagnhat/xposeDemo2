package com.example.xposedemo;

import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.xposedemo.Hook.BaseHook;
import com.example.xposedemo.Hook.CPUHook;
import com.example.xposedemo.Hook.ContextGet;
import com.example.xposedemo.Hook.HookShare;
import com.example.xposedemo.Hook.PackagesHook;
import com.example.xposedemo.Hook.Phone;
import com.example.xposedemo.Hook.SimpleBaseHook;
import com.example.xposedemo.Hook.WIFIHook;
import com.example.xposedemo.Hook.webViewHook;
import com.example.xposedemo.utils.MyFile;
import com.example.xposedemo.utils.Ut;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class HookMain  implements IXposedHookLoadPackage   {
    public String TAG="hook";
    /**
     * XposedBridge.log()：以原生logcat的形式写入到/data/user_de/0/de.robv.android.xposed.installer/log/error.log
     */
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam)  throws Throwable {

        //loadPackageParam.packageName.equals( BuildConfig.APPLICATION_ID
        //String json= MyFile.readFileToString(HookShare.pathSelectedPackages);

//        Log.d(TAG, "handleLoadPackage: boolSelectedPackages"+ HookShare.boolSelectedPackages( loadPackageParam ) );

        Log.d(TAG, "handleLoadPackage: "+HookShare.boolSelectedPackages(loadPackageParam) );
        if ( HookShare.boolSelectedPackages( loadPackageParam ) ){
            //new ContextGet(loadPackageParam);
            new webViewHook( loadPackageParam );
            //new Phone( loadPackageParam  ) ;

            //new CPUHook( loadPackageParam );
            Log.d(TAG, "handleLoadPackage: ");

            if ( getUiChecked( "sw_enable_para" ) ){
                Log.d(TAG, "handleLoadPackage: enable BaseHook" );
                new BaseHook( loadPackageParam );
            }

            //new SimpleBaseHook( loadPackageParam );
            //new WIFIHook( loadPackageParam );
            //new PackagesHook( loadPackageParam );

        }

/*        if (loadPackageParam.packageName.equals( BuildConfig.APPLICATION_ID )) {
            Class clazz = loadPackageParam.classLoader.loadClass("com.example.xposedemo.MainActivity");
            XposedHelpers.findAndHookMethod(clazz, "toastMessage", new XC_MethodHook() {
                protected void beforeHookedMethod( MethodHookParam param ) throws Throwable {
                    super.beforeHookedMethod( param );
                    XposedBridge.log("Hook成功");
                }

                protected void afterHookedMethod( MethodHookParam param ) throws Throwable {
                    param.setResult("按钮已被劫持");
                }
            });
        }*/

    }

    public boolean getUiChecked( String key ){
        String uiJson=MyFile.readFileToString( HookShare.pathUiSettingData );
        Log.d(TAG, "getUiChecked: "+uiJson);
        JSONObject jsonObject = null;

        if (uiJson!=""){
            jsonObject= JSON.parseObject( uiJson );
            return jsonObject.getBoolean( key  );
        }else{
            return false;
        }

    }

    public void  hookBuild(){

        // 修改手机系统信息 此处是手机的基本信息 包括厂商 信号 ROM版本 安卓版本 主板 设备名 指纹名称等信息
        XposedHelpers.setStaticObjectField(android.os.Build.class,  "MODEL" ,"Nexus 6P" );
        XposedHelpers.setStaticObjectField(android.os.Build.class,  "MANUFACTURER" ,"Huawei");
        XposedHelpers.setStaticObjectField(android.os.Build.class,  "BRAND" , "google");
        XposedHelpers.setStaticObjectField(android.os.Build.class,  "HARDWARE" ,"angler");
        XposedHelpers.setStaticObjectField(android.os.Build.class,  "BOARD" ,"angler");
        XposedHelpers.setStaticObjectField(android.os.Build.class,  "SERIAL" ,"ENU7N15A28004256" );
        XposedHelpers.setStaticObjectField(android.os.Build.class,  "DEVICE" ,"angler");
        //XposedHelpers.setStaticObjectField(android.os.Build.class,  "ID" , );
        XposedHelpers.setStaticObjectField(android.os.Build.class,  "PRODUCT" , "angler");
        XposedHelpers.setStaticObjectField(android.os.Build.class,  "DISPLAY" ,"MTC20L" );
        XposedHelpers.setStaticObjectField(android.os.Build.class,  "FINGERPRINT","google/angler/angler:6.0.1/MTC20L/3230295:user/release-keys" );
        Log.d(TAG, "hookBuild: hookBuild");

    }


    public class  MyXc_MethodHook extends XC_MethodHook {
        private Object ObjSetParam;
        public MyXc_MethodHook( Object ObjSetParam ){
            this.ObjSetParam=ObjSetParam;
        }

        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
            super.beforeHookedMethod(param);
        }

        @Override
        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
            super.afterHookedMethod( param );
            param.setResult( ObjSetParam );
        }
        
    }

}
