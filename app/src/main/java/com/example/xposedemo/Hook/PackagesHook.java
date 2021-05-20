package com.example.xposedemo.Hook;

import android.content.pm.PackageInfo;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PackagesHook {


    public static void hookRun(XC_LoadPackage.LoadPackageParam loadPackageParam ){

        XposedHelpers.findAndHookMethod("android.app.ApplicationPackageManager", loadPackageParam.classLoader, "getInstalledPackages", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                //Hook后的具体逻辑操作
                List<PackageInfo>  packageInfoList=(List<PackageInfo>) param.getResult();
                PackageInfo packageInfoLine=null;
                for ( PackageInfo info :
                    packageInfoList  ) {
                    if ( info.packageName.equals( "jp.naver.line.android" ) ){
                        packageInfoLine=info;
                        break;
                    }
                }

                int lineSecMin =60*60*24;
                int lineSecMax=60*60*7;

            }
        });



    }
}
