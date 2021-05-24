package com.example.xposedemo.Hook;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.util.Log;

import com.example.xposedemo.fake.FakePackage;
import com.example.xposedemo.utils.Ut;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class PackagesHook {

    public static final String TAG="PackagesHook";

    public PackagesHook(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        hookRun(loadPackageParam);
    }

    public  void hookRun(final XC_LoadPackage.LoadPackageParam loadPackageParam ){

        XposedHelpers.findAndHookMethod("android.app.ApplicationPackageManager", loadPackageParam.classLoader, "queryIntentActivities", Intent.class,
        int.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                        Log.d(TAG, "afterHookedMethod: queryIntentActivities");
                        return;
                    }
                }
        );

        XposedHelpers.findAndHookMethod("android.app.ApplicationPackageManager", loadPackageParam.classLoader, "getInstalledPackages", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                Log.d(TAG, "afterHookedMethod: packageName="+loadPackageParam.packageName   );
                if (loadPackageParam.packageName.equals("com.example.xposedemo") )
                {
                    Log.d(TAG, "afterHookedMethod: self return");
                    return;
                }

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

                long currenTime=rnd_time(60*60*24,60*60*24*7  );
                packageInfoLine.firstInstallTime =currenTime;
                packageInfoLine.lastUpdateTime=currenTime;

                List<String> packageNames=Ut.readLines( FakePackage.PathPackagesFinalResult );
                List<PackageInfo> listFinalRet=new ArrayList<>();

                //
                int tp;
                int min=60*60*24;
                int max=60*60*24*10 ;
                long time=0;

                listFinalRet.add( packageInfoLine );
                //遍历添加fakePackage
                PackageInfo packageInfoBase=null;
                for (int i=0;i<packageNames.size();i++  ){
                    if ( i>packageInfoList.size()  ){
                        tp=  Ut.r_ ( 0, packageInfoList.size()-1 );
                        packageInfoBase=packageInfoList.get( tp );
                    }else {
                        packageInfoBase=packageInfoList.get(i);
                    }

                    time=rnd_time( min,max );
                    packageInfoBase.firstInstallTime=time;
                    packageInfoBase.lastUpdateTime=time;
                    packageInfoBase.packageName=packageNames.get(i);
                    listFinalRet.add( packageInfoBase );
                }
                Log.d(TAG, "afterHookedMethod: package="   );
                param.setResult( listFinalRet );

            }
        });
    }

    public  static long rnd_time (int min, int max){

        int lineSecMin =min;
        int lineSecMax=max;
        long currenTime=System.currentTimeMillis();
        //Log.d(TAG, "rnd_time: currenTime="+currenTime );
        int rnd_time_down= Ut.r_( lineSecMin,lineSecMax )*1000;
        //Log.d(TAG, "rnd_time: rnd_time_down="+rnd_time_down );
        currenTime= currenTime-rnd_time_down ;
       // Log.d(TAG, "afterHookedMethod: "  );
        return currenTime;

    }

}
