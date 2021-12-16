package com.example.xposedemo.Hook;

import android.app.Application;
import android.content.Context;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class ContextGet {

    public static Context context;
    public XC_LoadPackage.LoadPackageParam loadPackageParam;

    public ContextGet(XC_LoadPackage.LoadPackageParam loadPackageParam ){
        this.loadPackageParam=loadPackageParam;
        XposedBridge.log("ContextGet hook is starting...");
        getContext();
    }

    public void getContext() {
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                context = (Context) param.args[0];
            }
        });
    }


}
