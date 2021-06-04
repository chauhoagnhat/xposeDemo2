package com.example.xposedemo.Hook;


import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.example.xposedemo.utils.Common;
import com.example.xposedemo.utils.Utils;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CPUHook {

    public static final String CPUPATHORIG = "/proc/cpuinfo";
    public static final String CPUPATHNEW = "/sdcard/cpuinfo";
    private String TAG="CPUHook";
    private String jsonStr;

    public CPUHook(LoadPackageParam loadPackageParam ){
        XposedBridge.log("CPU hook is starting...");
        CPUHookMethod( loadPackageParam );
    }

    public void CPUHookMethod(final LoadPackageParam loadPackageParam) {

        jsonStr= Utils.readFileToString( HookShare.pathDeviceJson );
        final JSONObject jsonObjectPara = JSONObject.parseObject(jsonStr);


        XposedBridge.hookAllConstructors(File.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                replaceFileArgs(param);
            }
        });
        XposedBridge.log("hook all File constructors");


        XposedHelpers.findAndHookMethod( "android.os.SystemProperties",loadPackageParam.classLoader,"get", String.class ,new XC_MethodHook(){
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Log.d(TAG, "SystemProperties: realvalue="+param.getResult().toString()+"||"+param.args[0] );

                String v="";
                String tp="";
                switch( (String)param.args[0] ){
                    case "gsm.sim.operator.alpha":
                        tp=jsonObjectPara.getString( "getSimOperatorName" );
                        if(tp!=""){
                            v=tp;
                        } ;
                        break;
                    case "gsm.sim.operator.numeric":
                        tp=jsonObjectPara.getString( "getSimOperator" );
                        if(tp!=""){
                            v=tp;
                        } ;
                        v=tp ;break;
                    case "ro.product.cpu.abi":
                        v="arm64-v8a" ;break;
                    case "ro.product.cpu.abi2":
                        break;
                    case "gsm.sim.operator.iso-country":
                        tp=jsonObjectPara.getString( "getSimCountryIso" );
                        if(tp!=""){
                            v=tp;
                        } ;
                        v=tp ;break;
                    case "gsm.operator.alpha":
                        tp=jsonObjectPara.getString( "getNetworkOperatorName" );
                        if(tp!=""){
                            v=tp;
                        } ;
                        v=tp ;break;
                    default:
                        //System.out.println("default");break;
                }
//gsm.operator.alpha
                if (v!=""){
                    param.setResult(v);
                }

                Log.d(TAG, "SystemProperties: "+param.getResult().toString()+"||"+param.args[0]+"||set-value="+v );
            }
        })  ;

        XposedHelpers.findAndHookMethod("java.lang.Runtime", loadPackageParam.classLoader, "exec", String[].class, String[].class, File.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String p= param.args[0].toString();
                if ( p==null){
                    p="null";
                }
                Log.d(TAG, "beforeHookedMethod: Runtime---"+loadPackageParam.packageName+"---"+p );
                replaceFileArgs(param);
            }
        });
        XposedBridge.log("hook Runtime...");

        XposedBridge.hookMethod(XposedHelpers.findConstructorExact(ProcessBuilder.class, String[].class), new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Log.d(TAG, "beforeHookedMethod: ProcessBuilder---"+loadPackageParam.packageName );
                if(param.args[0] != null){
                    String[] strArr = (String[])param.args[0];
                    for(String str:strArr){
                        if(CPUPATHORIG.equals(str)){
                            strArr[1] = CPUPATHNEW;
                        }
                    }
                    param.args[0] = strArr;
                }
            }
        });

        XposedHelpers.findAndHookMethod("java.util.regex.Matcher", loadPackageParam.classLoader, "matches", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Log.d(TAG,"enter hook:matches"  );
                        Matcher matcher=(Matcher) param.thisObject;
                        Boolean result=(Boolean)param.getResult();
                        String string;
                        if (result)
                            string="true";
                        else
                            string="false";

                        Pattern pattern=matcher.pattern();
                        Log.d(TAG, "afterHookedMethod: Matcher.matches*pattern="+pattern
                        + "*result="+string );
                    }
            });


     XposedHelpers.findAndHookMethod("java.util.regex.Pattern", loadPackageParam.classLoader, "matcher", CharSequence.class, new XC_MethodHook() {

            String p="";
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                super.beforeHookedMethod(param);
                p = param.args[0].toString();
                if ( p==null){
                    p="null";
                }

                if(param.args.length == 1){
                    if(CPUPATHORIG.equals(param.args[0])){
                        //Log.d(TAG, "beforeHookedMethod: Pattern---getCpu");
                        param.args[0] = CPUPATHNEW;
                    }
                }
            }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                        super.afterHookedMethod(param);
                        String args2;
                        Object pa=param.getResult();
                        if (pa==null)
                            args2="null";
                        else
                        {
                            args2=pa.toString();
                        }

                        Log.d(TAG, "afterHookedMethod: Pattern---"+loadPackageParam.packageName+ "---\r\nargs---"+p+"\r\n"
                        +"arg2="+args2 );


            }
                }

        );

    }

    public static void replaceFileArgs(XC_MethodHook.MethodHookParam param){

        if(param.args.length == 1){
            if(CPUPATHORIG.equals(param.args[0])){
                param.args[0] = CPUPATHNEW;
            }
        }else if (param.args.length == 2 && !File.class.isInstance(param.args[0])){
            for(int i=0; i<2; i++){
                if(CPUPATHORIG.equals(param.args[i])){
                    Log.d("replaceFileArgs", "beforeHookedMethod: Pattern---getCpu");
                    param.args[i] = CPUPATHNEW;
                }
            }
        }

    }


}
