package com.example.xposedemo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Telephony;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.xposedemo.MyProvider.MyOpenHelper;
import com.example.xposedemo.Utis.Common;
import com.example.xposedemo.Utis.Date;
import com.example.xposedemo.Utis.SharedPref;
import com.example.xposedemo.Utis.SharedPreferencesHelper;
import com.example.xposedemo.Utis.Utils;

import java.io.File;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    TelephonyManager tm;
    TelecomManager tm2;

    private TextView viewById;
    private String string;
    private static Context context;


    @Override
        protected void onCreate(Bundle savedInstanceState) {

            super.onCreate( savedInstanceState );
            setContentView(R.layout.activity_main);
            String path=Environment.getExternalStorageDirectory().toString();
            Log.d(TAG, "onCreate: path="+path);

            viewById=findViewById(R.id.textView);
            viewById.setText("no data");

       // String jsonStr= Utils.readFileToString(Environment.getExternalStorageDirectory ()+"/device.txt");
        String jsonStr= Utils.readFileToString( Common.DEVICE_PATH );
        startPermissionManager1();
       // insert(jsonStr);

        if (jsonStr!=""&&jsonStr!=null){
            JSONObject jsonObjectPara;
           // jsonObjectPara=JSONObject.parseObject( jsonStr );
            Log.d(TAG, "onCreate: jsonStr="+jsonStr );
            Map<String,String> map=JSONObject.parseObject(jsonStr,
                    new TypeReference<Map<String, String>>(){});

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

    public void  startPermissionManager1(){
        Intent intent=new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData( Uri.parse("package:jp.naver.line.android") );
        intent.setFlags(0x10008000);
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
