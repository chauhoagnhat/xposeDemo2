package com.example.xposedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.xposedemo.utils.Common;
import com.example.xposedemo.utils.Ut;
import com.example.xposedemo.utils.SharedPref;
import com.example.xposedemo.utils.Utils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    TelephonyManager tm;
    TelecomManager tm2;

    private TextView viewById;
    private String string;
    private   Context context =  null ;

    @Override
        protected void onCreate( Bundle savedInstanceState ) {

        String arch = "";//cpu类型
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method get = clazz.getDeclaredMethod("get", new Class[] { String.class } );
            arch = (String)get.invoke(clazz, new Object[] {"ro.product.cpu.abi"} );
        } catch(Exception e) {
            e.printStackTrace();
        }

        Log.d(TAG, "arch----- "+arch);

         testHook();
            String deviceId= Ut.getUDID( getApplicationContext() );
            String id=  Settings.Secure.getString( getContentResolver(), Settings.Secure.ANDROID_ID );
            Ut.copyAssetsFile(context,"cpuinfo","/sdcard/cpuinfo" );
            Log.d(TAG, "onCreate: androidId="+id  );

            super.onCreate( savedInstanceState );
            setContentView( R.layout.activity_main );
            String path=Environment.getExternalStorageDirectory().toString();
            Log.d(TAG, "onCreate: path="+path);

            viewById=findViewById(R.id.textView);
            viewById.setText("no data");
            TelephonyManager  telephonyManager =( TelephonyManager )getApplicationContext(). getSystemService( Context.TELEPHONY_SERVICE );

        try {
            getApplication().getAssets().open("cpuinfo");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "onCreate:getSubscriberId"+telephonyManager.getSubscriberId() );
        Log.d(TAG, "onCreate:getDeviceId"+telephonyManager.getDeviceId  () );

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                
                Log.d(TAG, "onCreate: getDeviceId="+telephonyManager.getDeviceId(0) );
                Log.d(TAG, "onCreate: getSubscriberId="+telephonyManager. getSubscriberId () );
                Configuration configuration=getResources().getConfiguration();
                //Log.d(TAG,  "colorMode-"+configuration.colorMode );
                Log.d(TAG,  "configAll densityDpi-"+configuration.densityDpi );
                Log.d(TAG,  "configAll fontScale-"+configuration.fontScale );
                Log.d(TAG,  "configAll hardKeyboardHidden-"+configuration.hardKeyboardHidden );
                Log.d(TAG,  "configAll keyboard-"+configuration.keyboard );
                Log.d(TAG,  "configAll orientation-"+configuration.orientation );
                Log.d(TAG,  "configAll screenHeightDp-"+configuration.screenHeightDp );
                Log.d(TAG,  "configAll screenLayout-"+configuration.screenLayout );
                Log.d(TAG,  "configAll screenWidthDp-"+configuration.screenWidthDp );
                Log.d(TAG,  "configAll smallestScreenWidthDp-"+configuration.smallestScreenWidthDp );
                Log.d(TAG,  "configAll uiMode-"+configuration.uiMode );
                Log.d(TAG,  "configAll keyboardHidden-"+configuration.keyboardHidden );
                Log.d(TAG,  "configAll navigation-"+configuration.navigation );
                Log.d(TAG,  "configAll navigationHidden-"+configuration.navigationHidden );
                Log.d(TAG,  "configAll touchscreen-"+configuration.touchscreen );

                Log.d(TAG, "onCreate: getConfiguration mccmnc="+configuration.mcc+configuration.mnc );
                Log.d(TAG, "onCreate: getConfiguration="+configuration.toString()  );
                Log.d(TAG, "onCreate:"   ) ;

                Log.d(TAG, "onCreate: build model-"+Build.MODEL );
                Log.d(TAG, "onCreate: build MANUFACTURER-"+Build.MANUFACTURER );
                Log.d(TAG, "onCreate: build BRAND-"+Build.BRAND );
                Log.d(TAG, "onCreate: build HARDWARE-"+Build.HARDWARE );
                Log.d(TAG, "onCreate: build BOARD-"+Build.BOARD );
                Log.d(TAG, "onCreate: build SERIAL-"+Build.SERIAL );
                Log.d(TAG, "onCreate: build DEVICE-"+Build.DEVICE );
                Log.d(TAG, "onCreate: build FINGERPRINT-"+Build.FINGERPRINT );
                Log.d(TAG, "onCreate: build DISPLAY-"+Build.DISPLAY );

            }

   /*     Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String mAdID = DeviceUtils.getGoogleAdId(getApplicationContext());
                    Log.d(TAG, "mAdID: "+mAdID );
                    if (!TextUtils.isEmpty(mAdID)) {
                        //init();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/

       // String jsonStr= Utils.readFileToString(Environment.getExternalStorageDirectory ()+"/device.txt");
        String jsonStr= Utils.readFileToString( Common.DEVICE_PATH );
        startPermissionManager1();
       // insert(jsonStr);

        if (jsonStr!=""&&jsonStr!=null){

            JSONObject jsonObjectPara;
           // jsonObjectPara=JSONObject.parseObject( jsonStr );
            Log.d(TAG, "onCreate: jsonStr="+jsonStr );
            Map<String,String> map=JSONObject.parseObject(jsonStr,
                    new TypeReference< Map<String, String> >(){});
            string=Utils.join_map2str( map,"\r\n");
            Log.d(TAG, "onCreate: mapStr="+string );
            viewById.setText(string);

        }

        Log.d(TAG, "onCreate: run finish");
        //ALPermissionManager.RootCommand("777");
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(MainActivity.this, toastMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        //saveImsi();

    }

    public void testHook(){
        context=getApplicationContext();
        Pattern p = Pattern.compile("a*b");
        Matcher m = p.matcher("aaaaab");
        boolean b = m.matches();
        Log.d(TAG, "testHook: matches=true*patter=*"+b +m.pattern() );

    }

    public void  startPermissionManager1(){
        Intent intent=new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData( Uri.parse("package:jp.naver.line.android") );
        //intent.setFlags(0x10008000);
        intent.setComponent( new ComponentName("com.android.settings","com.android.settings.applications.InstalledAppDetailsTop" )   );
        startActivity(intent);
    }

    public void insert(String json){

        //①获取内容解析者
        ContentResolver resolver = getContentResolver();
        Uri url = Uri.parse( Common.URI+"insert" );
        Log.d(TAG, "insert: =" +Common.URI+"insert" );
        ContentValues values = new ContentValues();
        values.put("device",json  );
        Uri insert = resolver.insert(url, values);
        System.out.println(insert);

    }

    public void query(){
        //获取内容解析者
        ContentResolver contentResolver = getContentResolver();
        Uri uri =Uri.parse(Common.URI+"query");
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
//	Cursor cursor = database.rawQuery("select * from info", null);

        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex("device"));
            String phone = cursor.getString(cursor.getColumnIndex("json"));
            System.out.println("name="+name+"phone"+phone);
        }

    }

    public String toastMessage() {
        Log.d(TAG, "toastMessage: toast-run");
         return "按钮未被劫持";
    }

    public  void saveImsi(){

        Log.d(TAG, "saveImsi: ");
        SharedPref mySP = new SharedPref( getApplicationContext() );
//    mySP.setSharedPref("IMSI","460017932859596");
//    mySP.setSharedPref("PhoneNumber","13117511178"); // 手机号码
//    mySP.setSharedPref("SimSerial", "89860179328595969501"); // 手机卡序列号
//    mySP.setSharedPref("networktor","46001" ); // 网络运营商类型
//    mySP.setSharedPref("Carrier","中国联通" );// 网络类型名
//    mySP.setSharedPref("CarrierCode","46001" ); // 运营商
//    mySP.setSharedPref("simopename","中国联通" );// 运营商名字
//    mySP.setSharedPref("gjISO", "cn");// 国家iso代码
//    mySP.setSharedPref("CountryCode","cn" );// 手机卡国家

        mySP.setSharedPref("IMSI","250127932859596");
        mySP.setSharedPref("PhoneNumber","13117511178"); // 手机号码
        mySP.setSharedPref("SimSerial", "89860179328595969501"); // 手机卡序列号
        mySP.setSharedPref("networktor","25012" ); // 网络运营商类型
        mySP.setSharedPref("Carrier","China Mobile/Peoples" );// 网络类型名
        mySP.setSharedPref("CarrierCode","25012" ); // 运营商
        mySP.setSharedPref("simopename","Baykal Westcom" );// 运营商名字
        mySP.setSharedPref("gjISO", "ru");// 国家iso代码
        mySP.setSharedPref("CountryCode","ru" );// 手机卡国家
        System.out.println( "run-ok" );



    }


}
