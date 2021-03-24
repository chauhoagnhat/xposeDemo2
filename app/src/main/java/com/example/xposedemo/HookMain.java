package com.example.xposedemo;

import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Log;
import com.example.xposedemo.Hook.Hook;
import com.example.xposedemo.Hook.Phone;
import com.example.xposedemo.Hook.XBuild;
import com.example.xposedemo.Utis.RootCloak;
import com.example.xposedemo.Utis.SharedPref;
import java.security.PublicKey;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookMain implements IXposedHookLoadPackage {

    public String TAG="hook";
    /**
     * XposedBridge.log()：以原生logcat的形式写入到/data/user_de/0/de.robv.android.xposed.installer/log/error.log
     */
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam)  throws Throwable {

        Log.d(TAG, "handleLoadPackage: hook run");
        if (loadPackageParam.packageName.equals( "jp.naver.line.android" )|loadPackageParam.packageName.equals( BuildConfig.APPLICATION_ID  ) ){
            new Phone( loadPackageParam ) ;
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
            super.afterHookedMethod(param);
            param.setResult( ObjSetParam );
        }
    }

}
