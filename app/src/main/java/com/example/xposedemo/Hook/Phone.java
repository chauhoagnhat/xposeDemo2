package com.example.xposedemo.Hook;

import android.os.Build;
import android.util.Log;

import com.example.xposedemo.Utis.SharedPref;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Administrator on 2017/4/17 0017.
 */

public class Phone {
    private String TAG="Phone";

    public Phone(XC_LoadPackage.LoadPackageParam sharePkgParam) {
//          getType(sharePkgParam);
//         Bluetooth(sharePkgParam);
//        Wifi(sharePkgParam);
        Telephony(sharePkgParam);
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
    public void Telephony(XC_LoadPackage.LoadPackageParam loadPkgParam) {

        String TelePhone = "android.telephony.TelephonyManager";
        //TelePhone="android.telecom.TelephonyManager";

        try {
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
        Log.d(TAG, "Telephony: classname="+TelePhone );


//        mySP.setSharedPref("IMSI","250127932859596");
//        mySP.setSharedPref("PhoneNumber","13117511178"); // 手机号码
//        mySP.setSharedPref("SimSerial", "89860179328595969501"); // 手机卡序列号
//        mySP.setSharedPref("networktor","25012" ); // 网络运营商类型
//        mySP.setSharedPref("Carrier","China Mobile/Peoples" );// 网络类型名
//        mySP.setSharedPref("CarrierCode","25012" ); // 运营商
//        mySP.setSharedPref("simopename","Baykal Westcom" );// 运营商名字
//        mySP.setSharedPref("gjISO", "ru");// 国家iso代码
//        mySP.setSharedPref("CountryCode","ru" );// 手机卡国家

 /*
		HookTelephony(TelePhone, loadPkgParam, "getDeviceSoftwareVersion",
				SharedPref.getXValue("deviceversion"));// 返系统版本
      HookTelephony(TelePhone, loadPkgParam, "getSubscriberId",
                SharedPref.getXValue("IMSI"));
        HookTelephony(TelePhone, loadPkgParam, "getLine1Number",
                SharedPref.getXValue("PhoneNumber"));
        HookTelephony(TelePhone, loadPkgParam, "getSimSerialNumber",
                SharedPref.getXValue("SimSerial"));
        HookTelephony(TelePhone, loadPkgParam, "getNetworkOperator",
                SharedPref.getXValue("networktor")); // 网络运营商类型
        HookTelephony(TelePhone, loadPkgParam, "getNetworkOperatorName",
                SharedPref.getXValue("Carrier")); // 网络类型名
        HookTelephony(TelePhone, loadPkgParam, "getSimOperator",
                "25012");
                //SharedPref.getXValue("CarrierCode")); // 运营商
        HookTelephony(TelePhone, loadPkgParam, "getSimOperatorName",
                SharedPref.getXValue("simopename")); // 运营商名字
        HookTelephony(TelePhone, loadPkgParam, "getNetworkCountryIso",
                SharedPref.getXValue("gjISO")); // 国家iso代码
        HookTelephony(TelePhone, loadPkgParam, "getSimCountryIso",
                SharedPref.getXValue("CountryCode")); // 手机卡国家*/

        //        mySP.setSharedPref("IMSI","250127932859596");
//        mySP.setSharedPref("PhoneNumber","13117511178"); // 手机号码
//        mySP.setSharedPref("SimSerial", "89860179328595969501"); // 手机卡序列号
//        mySP.setSharedPref("networktor","25012" ); // 网络运营商类型
//        mySP.setSharedPref("Carrier","China Mobile/Peoples" );// 网络类型名
//        mySP.setSharedPref("CarrierCode","25012" ); // 运营商
//        mySP.setSharedPref("simopename","Baykal Westcom" );// 运营商名字
//        mySP.setSharedPref("gjISO", "ru");// 国家iso代码
//        mySP.setSharedPref("CountryCode","ru" );// 手机卡国家

        //    mySP.setSharedPref("IMSI","460017932859596");
//    mySP.setSharedPref("PhoneNumber","13117511178"); // 手机号码
//    mySP.setSharedPref("SimSerial", "89860179328595969501"); // 手机卡序列号
//    mySP.setSharedPref("networktor","46001" ); // 网络运营商类型
//    mySP.setSharedPref("Carrier","中国联通" );// 网络类型名
//    mySP.setSharedPref("CarrierCode","46001" ); // 运营商
//    mySP.setSharedPref("simopename","中国联通" );// 运营商名字
//    mySP.setSharedPref("gjISO", "cn");// 国家iso代码
//    mySP.setSharedPref("CountryCode","cn" );// 手机卡国家


//        HookTelephony(TelePhone, loadPkgParam, "getSubscriberId",
//           "460017932859596"  );
//        HookTelephony(TelePhone, loadPkgParam, "getLine1Number",
//              "13117511178" );
//        HookTelephony(TelePhone, loadPkgParam, "getSimSerialNumber",
//               "89860179328595969501"  );
        HookTelephony(TelePhone, loadPkgParam, "getNetworkOperator",
               "25012" ); // 网络运营商类型
        HookTelephony(TelePhone, loadPkgParam, "getNetworkOperatorName",
               "Baykal Westcom" ); // 网络类型名
        HookTelephony(TelePhone, loadPkgParam, "getSimOperator",
                "25012");
//                //SharedPref.getXValue("CarrierCode")); // 运营商
//        HookTelephony(TelePhone, loadPkgParam, "getSimOperatorName",
//              "中国联通"  ); // 运营商名字
        HookTelephony(TelePhone, loadPkgParam, "getNetworkCountryIso",
              "ru"  ); // 国家iso代码
      HookTelephony(TelePhone, loadPkgParam, "getSimCountryIso",
              "ru"  ); // 手机卡国家
//        Log.d(TAG, "Telephony: ok"+ SharedPref.getXValue("CarrierCode") );
/*

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", loadPkgParam.classLoader, "getNetworkType", new XC_MethodHook() {

            @Override
            protected void afterHookedMethod(MethodHookParam param)
                    throws Throwable {
                // TODO Auto-generated method stub
                super.afterHookedMethod(param);
                //      网络类型
                param.setResult( SharedPref.getintXValue("networkType"));

            }

        });


        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager",
                loadPkgParam.classLoader, "getPhoneType", new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        // TODO Auto-generated method stub
                        super.afterHookedMethod(param);
                        param.setResult(SharedPref.getintXValue("phonetype"));
                    }
                });

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager",
                loadPkgParam.classLoader, "getSimState", new XC_MethodHook() {

                    @Override
                    protected void afterHookedMethod(MethodHookParam param)
                            throws Throwable {
                        // TODO Auto-generated method stub
                        super.afterHookedMethod(param);
                        param.setResult(SharedPref.getintXValue("SimState"));
                    }
                });


*/



    }

    private void HookTelephony(String hookClass, XC_LoadPackage.LoadPackageParam loadPkgParam,
                               String funcName, final String value) {
        try {
            XposedHelpers.findAndHookMethod(hookClass,
                    loadPkgParam.classLoader, funcName, new XC_MethodHook() {

                        @Override
                        protected void afterHookedMethod(MethodHookParam param)
                                throws Throwable {
                            // TODO Auto-generated method stub
                            super.afterHookedMethod(param);
                            param.setResult(value);
                        }

                    });

        } catch (Exception e) {

        }
    }

}