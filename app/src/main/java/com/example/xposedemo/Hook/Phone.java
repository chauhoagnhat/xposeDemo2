package com.example.xposedemo.Hook;

import android.content.res.Configuration;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.example.xposedemo.utils.Common;
import com.example.xposedemo.utils.SharedPref;
import com.example.xposedemo.utils.Utils;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Administrator on 2017/4/17 0017.
 */

public class Phone  {

    private String TAG="Phone";
    private ArrayList<String> methodNamelist;
    public String jsonStr;
    JSONObject jsonObjectPara;

    public Phone( XC_LoadPackage.LoadPackageParam sharePkgParam ) {
        methodNamelist=new ArrayList<>();
        Telephony( sharePkgParam );
    }

    // 联网方式
    public void getType(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        XposedHelpers.findAndHookMethod("android.net.NetworkInfo", loadPackageParam.classLoader, "getType",
                new XC_MethodHook() {
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        param.setResult(SharedPref.getintXValue("getType"));
                    }
                    ;
                });

    }

    public void query(){
        //获取内容解析者
        Log.d(TAG, "query: query");
    }

    // ------- MAC 蓝牙-----------------------------------------------------------
    public void Bluetooth(XC_LoadPackage.LoadPackageParam loadPkgParam) {
        try {

            // 双层 MAC
            XposedHelpers.findAndHookMethod(
                    "android.bluetooth.BluetoothAdapter",
                    loadPkgParam.classLoader, "getAddress",
                    new XC_MethodHook() {

                        @Override
                        protected void afterHookedMethod(MethodHookParam param)
                                throws Throwable {
                            // TODO Auto-generated method stub
                            super.afterHookedMethod(param);
                            param.setResult(SharedPref.getXValue("LYMAC"));
                        }

                    });
            // 双层MAC
            XposedHelpers.findAndHookMethod(
                    "android.bluetooth.BluetoothDevice",
                    loadPkgParam.classLoader, "getAddress",
                    new XC_MethodHook() {

                        @Override
                        protected void afterHookedMethod(MethodHookParam param)
                                throws Throwable {
                            // TODO Auto-generated method stub
                            // super.afterHookedMethod(param);
                            param.setResult(SharedPref.getXValue("LYMAC"));
                        }

                    });
        } catch (Exception e) {
            XposedBridge.log("phone MAC HOOK 失败 " + e.getMessage());
        }
    }

    // -----------------------------------------------------------------------------

    // WIF MAC
    public void Wifi(XC_LoadPackage.LoadPackageParam loadPkgParam) {
        try {

            XposedHelpers.findAndHookMethod("android.net.wifi.WifiInfo",
                    loadPkgParam.classLoader, "getMacAddress",
                    new XC_MethodHook() {

                        @Override
                        protected void afterHookedMethod(MethodHookParam param)
                                throws Throwable {
                            // TODO Auto-generated method stub
                            super.afterHookedMethod(param);
                            param.setResult(SharedPref.getXValue("WifiMAC"));
                        }

                    });

            // 内网IP
            XposedHelpers.findAndHookMethod("android.net.wifi.WifiInfo",
                    loadPkgParam.classLoader, "getIpAddress",
                    new XC_MethodHook() {

                        @Override
                        protected void afterHookedMethod(MethodHookParam param)
                                throws Throwable {
                            // TODO Auto-generated method stub
                            super.afterHookedMethod(param);
                            param.setResult(SharedPref.getintXValue("getIP"));
                            // param.setResult(tryParseInt(SharedPref.getXValue("getIP")));

                        }

                    });

            XposedHelpers.findAndHookMethod("android.net.wifi.WifiInfo",
                    loadPkgParam.classLoader, "getSSID", new XC_MethodHook() {

                        @Override
                        protected void afterHookedMethod(MethodHookParam param)
                                throws Throwable {
                            // TODO Auto-generated method stub
                            super.afterHookedMethod(param);
                            param.setResult(SharedPref.getXValue("WifiName"));
                        }

                    });


        } catch (Exception e) {

        }

        // ------------------------基站信息


        // 基站的信号强度
        XposedHelpers.findAndHookMethod("android.net.wifi.WifiInfo",
                loadPkgParam.classLoader, "getBSSID", new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        // TODO Auto-generated method stub
                        super.afterHookedMethod(param);
                        param.setResult(SharedPref.getXValue("BSSID"));
                    }

                });
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

    public void Telephony(XC_LoadPackage.LoadPackageParam loadPkgParam) {

        String TelePhone = "android.telephony.TelephonyManager";
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
        jsonStr=Utils.readFileToString(Common.DEVICE_PATH);
        jsonObjectPara = JSONObject.parseObject( jsonStr );
        //hookBuild();
        Log.d(TAG, "Telephony: json="+jsonStr );


        // 修改手机系统信息 此处是手机的基本信息 包括厂商 信号 ROM版本 安卓版本 主板 设备名 指纹名称等信息
        XposedHelpers.findAndHookMethod(TelePhone,
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

                });


        // 修改为指定的运营商mnc mcc信息
        XposedHelpers.findAndHookMethod(android.content.res.Resources.class.getName(),loadPkgParam.classLoader,  "getConfiguration" , new XC_MethodHook()
        {

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                super.afterHookedMethod(param);
                Configuration configuration=(Configuration) param.getResult();
                configuration.mcc= Integer.parseInt( jsonObjectPara.getString("mcc") );
                configuration.mnc=Integer.parseInt( jsonObjectPara.getString("mnc") );
//                configuration.densityDpi=560;
   //                     configuration.fontScale=1;
    //                    configuration.hardKeyboardHidden=2;
    //                    configuration.keyboard=1;
//                        configuration.orientation=1;
//
//                        configuration.uiMode=17;                        configuration.screenHeightDp=659;
        //             configuration.screenLayout=268435794;
////                        configuration.screenWidthDp=411;
////                        configuration.smallestScreenWidthDp=411;
//                        configuration.keyboardHidden=1;
//                        configuration.navigation=1;
//                        configuration.navigationHidden=2;
//                        configuration.touchscreen=3;
              //  Log.d(TAG, "HookTelephony="+methodNamelist.toString() );
                Log.d(TAG, "setHookValue: "+"getConfiguration"+"-result="+param.getResult().toString()  );
                param.setResult( configuration );

            }

        });

        String fucName="getDeviceSoftwareVersion";
        fucName="getDeviceId";
        HookTelephony(TelePhone, loadPkgParam, fucName,
                jsonObjectPara.getString( fucName )  );  //返系统版本
        HookTelephony(TelePhone, loadPkgParam, fucName,
                jsonObjectPara.getString( fucName )  );  //返系统版本
        fucName="getSubscriberId";          //460017932859596 imsi
        HookTelephony(TelePhone, loadPkgParam, fucName,
                jsonObjectPara.getString( fucName )  );
        fucName="getLine1Number";          //13117511178 返系统版本
        HookTelephony(TelePhone, loadPkgParam, fucName,
                jsonObjectPara.getString( fucName )  );
        fucName="getSimSerialNumber";          //89860179328595969501 序列号
        HookTelephony( TelePhone, loadPkgParam, fucName,
                jsonObjectPara.getString( fucName )  );
        fucName="getNetworkOperator";          //45413 运营商网络类型
        HookTelephony( TelePhone, loadPkgParam, fucName,
                jsonObjectPara.getString( fucName )  );
        fucName="getNetworkOperatorName";       //45413 网络类型名
        HookTelephony( TelePhone, loadPkgParam, fucName,
                jsonObjectPara.getString( fucName )  );
        fucName="getSimOperator";       //45413 运营商
        HookTelephony( TelePhone, loadPkgParam, fucName,
                jsonObjectPara.getString( fucName )  );
        fucName="getSimOperatorName";       //中国联通 运营商名字
        HookTelephony( TelePhone, loadPkgParam, fucName,
                jsonObjectPara.getString( fucName )  );
        fucName="getNetworkCountryIso";       //中国联通 国家iso代码
        HookTelephony( TelePhone, loadPkgParam, fucName,
                jsonObjectPara.getString( fucName )  );
        fucName="getSimCountryIso";       //hk 手机卡国家
        HookTelephony( TelePhone, loadPkgParam, fucName,
                jsonObjectPara.getString( fucName )  );

//        fucName="GetNeighboringCellInfo";       //
//        HookTelephony( TelePhone, loadPkgParam, fucName,
//                jsonObjectPara.getString( fucName )  ); // 获取邻近的基站信息，返回的是List<NeighboringCellInfo>基站列表信息
        fucName="getCellLocation";       //
        HookTelephony( TelePhone, loadPkgParam, fucName,
                jsonObjectPara.getString( fucName )  ); // 获取当前基站信息
        fucName="getAllCellInfo";       //
        HookTelephony( TelePhone, loadPkgParam, fucName,
                jsonObjectPara.getString( fucName )  ); // List<CellInfo>基站列表信息


        HookTelephony(android.telephony.TelephonyManager.class.getName(),loadPkgParam,  "getPhoneType" , TelephonyManager.PHONE_TYPE_GSM );
        HookTelephony(android.telephony.TelephonyManager.class.getName(),loadPkgParam,  "getNetworkType" , TelephonyManager.NETWORK_TYPE_HSPAP);
        HookTelephony(android.telephony.TelephonyManager.class.getName(),loadPkgParam,  "getSimState" , TelephonyManager.SIM_STATE_READY);
        HookTelephony(android.telephony.TelephonyManager.class.getName(),loadPkgParam,  "hasIccCard" ,  true );


      /*  XposedBridge.hookAllMethods(findClass("pc.a.b.n", loadPkgParam.classLoader), "b", new XC_MethodHook() {
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
                            Log.d(TAG, "HookTelephony="+methodNamelist.toString() );
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
                                Log.d(TAG, "getRealValue:"+funcName+"-result="+param.getResult().toString());
                            }else
                                Log.d(TAG, "getRealValue:"+funcName+"-result=null"  );


                            if(null!=value&&""!=value){
                                param.setResult(value);
                                Log.d(TAG, "setHookValue:"+funcName+"-result="+param.getResult().toString()+"set-value="+value   );
                            }


                        }

                    });

        } catch (Exception e) {

        }

    }

}