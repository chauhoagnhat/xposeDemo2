package com.example.xposedemo;

import android.util.Log;

import com.example.xposedemo.Hook.CPUHook;
import com.example.xposedemo.Hook.Phone;

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

        if (loadPackageParam.packageName.equals( "jp.naver.line.android" )
            |loadPackageParam.packageName.equals("com.google.android.gms")
            |loadPackageParam.packageName.equals("com.android.vending")){
            Log.d(TAG, "handleLoadPackage: new phone");
            //hookBuild();
            //hookBuild();
            new Phone( loadPackageParam  ) ;
            new CPUHook( loadPackageParam );

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
