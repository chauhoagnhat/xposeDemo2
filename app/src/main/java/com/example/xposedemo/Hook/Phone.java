package com.example.xposedemo.Hook;

import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.xposedemo.Utis.Date;
import com.example.xposedemo.Utis.SharedPref;
import com.example.xposedemo.Utis.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private ArrayList<String> methodNamelist;
    public Phone(XC_LoadPackage.LoadPackageParam sharePkgParam) {
        methodNamelist=new ArrayList<>();
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

        Log.d(TAG, "Telephony: run 1");
        String jsonStr= Utils.txt2String("/mnt/sdcard/device.txt" );
        //String jsonStr= Date.shareJson;
        Log.d(TAG, "Telephony:read jsonstr="+jsonStr );
        JSONObject jsonObjectPara;
        if (jsonStr==null|jsonStr==""){
            Map<String,String> mapJson=new HashMap<>();
            mapJson.put("getDeviceSoftwareVersion","");
            mapJson.put("getSubscriberId","");
            mapJson.put("getLine1Number","");
            mapJson.put("getSimSerialNumber","");
            mapJson.put("getNetworkOperator","");
            mapJson.put("getNetworkOperatorName","");
            mapJson.put("getSimOperator","");
            mapJson.put("getSimOperatorName","");
            mapJson.put("getNetworkCountryIso","");
            mapJson.put("getNetworkType","");
            mapJson.put("getPhoneType","");
            mapJson.put("GetNeighboringCellInfo","");
            mapJson.put("getCellLocation","");
            mapJson.put("getAllCellInfo","");
            mapJson.put("GetNeighboringCellInfo","");
            jsonStr= JSON.toJSONString(mapJson) ;
            jsonObjectPara=JSONObject.parseObject( jsonStr );
        }
            jsonObjectPara=JSONObject.parseObject( jsonStr );

        Log.d(TAG, "Telephony: json"+jsonObjectPara.getString("getSimCountryIso") );

        String fucName="getDeviceSoftwareVersion";
        fucName="getDeviceSoftwareVersion";
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
        fucName="getNetworkType";       //hk 网络类型
        HookTelephony( TelePhone, loadPkgParam, fucName,
                jsonObjectPara.getString( fucName )  );
        fucName="getPhoneType";       //
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


//        fucName="GetNeighboringCellInfo";       //
//        HookTelephony( TelePhone, loadPkgParam, fucName,
//                jsonObjectPara.getString( fucName )  ); //获取邻近的基站信息，返回的是List<NeighboringCellInfo>基站列表信息

//        HookTelephony( "java.util.Locale", loadPkgParam, "getLanguage",
//                ""  ); // 获取语言

    }

    private void HookTelephony(String hookClass, XC_LoadPackage.LoadPackageParam loadPkgParam,
                               final String funcName, final String value) {
        try {

                    XposedHelpers.findAndHookMethod(hookClass,
                    loadPkgParam.classLoader, funcName, new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                    super.beforeHookedMethod(param);
                                    if(!methodNamelist.contains(funcName))
                                        methodNamelist.add(funcName);
                                    Log.d(TAG, "HookTelephony="+methodNamelist.toString());
                                }

                                @Override
                        protected void afterHookedMethod(MethodHookParam param)
                                throws Throwable {
                            // TODO Auto-generated method stub
                            super.afterHookedMethod(param);
                            if(null!=value&&""!=value){
                                param.setResult(value);
                            }

                            Log.d(TAG, "afterHookedMethod:"+funcName+"-result="+param.getResult().toString()+"set-value="+value   );

                        }

                    });

        } catch (Exception e) {

        }

    }

}