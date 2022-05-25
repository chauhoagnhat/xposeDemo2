package com.example.xposedemo;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.JsonReader;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.xposedemo.Hook.HookShare;
import com.example.xposedemo.MyProvider.MultiprocessSharedPreferences;
import com.example.xposedemo.MyProvider.MyOpenHelper;
import com.example.xposedemo.bean.BaseInfo;
import com.example.xposedemo.bean.MainActivityData;
import com.example.xposedemo.bean.WIFIInfo;
import com.example.xposedemo.fake.FakeBase;
import com.example.xposedemo.fake.FakePackage;
import com.example.xposedemo.functionModule.DataBack;
import com.example.xposedemo.MyInterface.DialogCallBack;
import com.example.xposedemo.functionModule.ScriptControl;
import com.example.xposedemo.myBroadcast.VolumeChangeObserver;
import com.example.xposedemo.myService.AlarmService;
import com.example.xposedemo.utils.MyFile;
import com.example.xposedemo.utils.MyUi;
import com.example.xposedemo.utils.PhoneRndTools;
import com.example.xposedemo.utils.Ut;
import com.example.xposedemo.utils.SharedPref;
import com.example.xposedemo.utils.Utils;
import com.example.xposedemo.view.ActivityPhone;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.SecureRandom;
import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {


    private String TAG = "MainActivity";
    TelephonyManager tm;
    TelecomManager tm2;
    private TextView viewById;
    private String string;
    private Context context = null;
    private Button bt_new;
    private List<Object> listDevice;
    private Context appContext = null;
    private MainActivityData mainActivityData = null;
    public TextView logTextview;
    private EditText et_pkgName;
    private Switch sw_scriptBootedRun;
    private Switch sw_enable_para;
    public  static VolumeChangeObserver volumeChangeObserver;
    String[] permissions;
    List<String> mPermissionList = new ArrayList<>();
    private static final int MY_PERMISSIONS_REQUEST_CODE = 10000;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permissionInit();
        getPermissions();

        addContacts();

        logTextview = (TextView) findViewById(R.id.tv_log);
        logTextview.setText("version-20220308");
        init_findViewById();

        Ut.restartApp ( context,"com.rf.icon" );

        //testPhone();
        Log.d(TAG, "onCreate: finger"+Build.FINGERPRINT );
        loadUiSetting();

//        volumeChangeObserver=
//                new VolumeChangeObserver( getApplicationContext() ) ;
//        volumeChangeObserver.registerReceiver();
        mainActivityDataInit();
        //getLanguages();
        assertInit();

        viewById = findViewById( R.id.textView );
        viewById.setText("no data");
        //setSelectedPackges();
        //testHook();
        ui();
        MyFile.execCmdsforResult( new String[]{ "am force-stop com.tunnelworkshop.postern" } );

        if ( getIntent()
                .getBooleanExtra( HookShare.mainActivityExtra,false ) ){
            getIntent().putExtra(HookShare.mainActivityExtra,false);

            Ut.restartApp( getApplicationContext(),et_pkgName.getText().toString() );
            Log.d(TAG,"启动app，退出");

            return;
        }
        Log.d(TAG, "onCreate: aaa");

        startWatchService();
        //boolStartScript();

    }

    public void permissionInit() {
        permissions = new String[]{
                //Manifest.permission.READ_SMS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE

        };
    }


    private void getPermissions() {

        mPermissionList.clear();                                    //清空已经允许的没有通过的权限
        for (int i = 0; i < permissions.length; i++) {          //逐个判断是否还有未通过的权限
            if (ContextCompat.checkSelfPermission(MainActivity.this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "getPermissions: " + permissions[i]);
                if (permissions[i].equals("android.permission.PACKAGE_USAGE_STATS")) {
                    Log.d(TAG, "getPermissions: run PACKAGE_USAGE_STATS-need");
                }
                mPermissionList.add(permissions[i]);
            }
        }

        if (mPermissionList.size() > 0) {                           //有权限没有通过，需要申请
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST_CODE);
            //permissionAppState();
        } else {
            Log.e("getPermissions() >>>", "已经授权");     //权限已经都通过了
        }

    }



    public void addContacts(){

        Button buttonContactsAdd=(Button)findViewById( R.id.contact_adds );
        final TextView tvLog=(TextView)findViewById( R.id.contact_log );
        buttonContactsAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        String path="sdcard/tel.txt";
                        if ( !new File( path ).exists() ){

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvLog.setText( "没有找到tel.txt" );
                                }
                            });
                            return;
                        }

                        List<String> lineStr=Ut.readLines( path );
                        if (lineStr!=null){
                            Map<String,String> mapNameTel=new HashMap<>();
                            for ( String str :
                                    lineStr  ) {
                                mapNameTel.put( Ut.rnd_enName (),"+"+str );
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvLog.setText("正在导入");
                                }
                            });

                            Ut.contactAdd( getApplicationContext(), mapNameTel );
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tvLog.setText("通讯录添加完成");
                                }
                            });

                        }

                    }
                }).start();

            }
        });

    }



    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: run");
        //volumeChangeObserver.unregisterReceiver();
       // volumeChangeObserver.unregisterReceiver();
        super.finish();
        super.onDestroy();

    }

    public void init_findViewById(){
        et_pkgName=
                (EditText)findViewById( R.id.et_pkgName ) ;
     //   et_pkgName.setVisibility(View.VISIBLE);
       // et_pkgName.setEnabled(false);
        sw_scriptBootedRun=
                (Switch)findViewById( R.id.sw_scriptBootedRun );
        sw_enable_para=(Switch)findViewById( R.id.sw_enable_para );

    }


    private List<String> getLanguages(){
        List<String> list=new ArrayList<>();
        Locale[] lg = Locale.getAvailableLocales();
        for(Locale language:lg){
            String name=language.getDisplayLanguage();
            Log.d(TAG, "getLanguages: "+language.toString());
            //去掉重复的语言
            if (!list.contains(name)){
                list.add(name);
                Log.d(TAG, "getLanguages: " );
            }
        }
        return list;
    }


    public void boolStartScript(){
       if( sw_scriptBootedRun.isChecked() ){
           SharedPreferences sharedPreferences=context.getSharedPreferences(
                   HookShare.BootBroadcastReceiver,Context.MODE_PRIVATE);
           int state= sharedPreferences.getInt( HookShare.BootBroadcastReceiverState,0 );
           if (state == HookShare.BootBroadcastReceiverBooted ){
               SharedPreferences.Editor editor=sharedPreferences.edit();
               editor.putInt(HookShare.BootBroadcastReceiverState
                       ,HookShare.BootBroadcastReceiverDefault );
               editor.commit();
               scriptRun( this, et_pkgName.getText().toString() );
           }
       }

    }

    public void stopApp(){

//        ActivityManager am = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
//
//        List infos = am.getRunningAppProcesses();
//        for( ActivityManager.RunningAppProcessInfo info : infos ) {
//            Log.i("---","aa" );
//            if("com.test.br2".equals( info.processName )){
//                Log.i(TAG, info.processName+"---"+info.pid);
//              //  killProcess( info.pid );
//                break;
//            }

    }

    public void  scriptRun(final Context context, final String packageName ){

        Toast.makeText( context,"检测脚本是否运行",Toast.LENGTH_LONG );
        //这里模拟后台操作
        new Thread(new Runnable() {
            @Override
            public void run() {

                synchronized ( this ){

                    Looper.prepare();
                    Log.d(TAG, "run: loop");
                    MyFile.fileWriterTxt( HookShare.PATH_SCRIPT_RUNNING,"0" );
                    try {
                        Thread.sleep(10*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    String tp= MyFile.readFileToString(HookShare.PATH_SCRIPT_RUNNING);
                    if (Integer.parseInt(tp)!=1){
                        for (int i=0;i<4;i++) {

                            boolean boolSuc=false;
                            try {
                                Log.d(TAG, "run: stop-app"+i);

                                Ut.stopAppByCmd( HookShare.packagePostern );
                                Ut.stopAppByCmd( HookShare.packageSurboard );
                                Thread.sleep(1000);
                                //Ut.startApplicationWithPackageName("com.apple", context);

                                Ut.restartApp( context,packageName );

                                Thread.sleep(8000 );
                                for (int j=0;j<6;j++){

                                    ScriptControl.setVolDown();
                                    Log.d(TAG, "run: set vol down"+j);
                                    Thread.sleep(6000);
                                    tp = MyFile.readFileToString(HookShare.PATH_SCRIPT_RUNNING);

                                    if (Integer.parseInt(tp) == 1) {

                                        boolSuc=true;
                                        Toast.makeText(context, "启动成功", Toast.LENGTH_SHORT);
                                        Log.d(TAG, "run: 启动成功");

                                        //再次确认启动成功
                                        MyFile.fileWriterTxt( HookShare.PATH_SCRIPT_RUNNING,"0" );
                                        Thread.sleep(10000);
                                        tp = MyFile.readFileToString(HookShare.PATH_SCRIPT_RUNNING);
                                        if (Integer.parseInt(tp) == 1) {
                                            Log.d(TAG, "run: 再次确认启动成功");
                                            break;
                                        }else {
                                            Log.d(TAG, "run: 再次确认启动失败");
                                            boolSuc=false;
                                        }

                                    }else{
                                        Log.d(TAG, "run: 判断脚本失败 readtxt="+Integer.parseInt(tp) );
                                    }


                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (boolSuc)
                                break;
//                            if (!getJsonWatch()) {
//                                Log.d(TAG, "run: not need watch was set");
//                                break;
//                            }
                        }

                    }else {
                        Log.d(TAG, "run: 脚本已启动无需启动");
                    }

                    Looper.loop ();
                }

            }
        }).start();

    }

    public boolean getJsonWatch(){

        String uiJson=MyFile.readFileToString( HookShare.PATH_UI_SETTING );
        JSONObject jsonObject=null;
        Log.d(TAG, "getJsonWatch");

        if (uiJson!=""){
            jsonObject= JSON.parseObject(uiJson);
            if (jsonObject.containsKey( "sw_script_watch" ) )
                return jsonObject.getBoolean ("sw_script_watch") ;
        }
        return false;

    }

    public void assertInit(){

        context=getApplicationContext();
        File nkForder=new File(HookShare.pathNkFolder);
        if (! nkForder.exists()){
            Log.d(TAG, "assertInit: 创建nk文件夹"+ nkForder.mkdir()  );
        }
        nkForder=new File( HookShare.pathNkFolderData );
        if (! nkForder.exists()){
            Log.d(TAG, "assertInit: 创建data/local/tmp/nk" );
            MyFile.execCmdsforResult( new String[]{"cd /data/local/tmp"
            ,"mkdir nk"}
            );
        }


//        MyFile.execCmdsforResult(
//                new String[]{"chmod 666 /data"
//
//                }
//        );
//
//
//
/*List aa=MyFile.execCmdsforResult( new String[]{
        "cat /data/local/tmp/nk/mccmncJsonData"
} );
        for ( Object s :
           aa  ) {
            Log.d(TAG, "cat=: "+s  );
        }*/
        //Log.d(TAG, "cat="+aa.get(0)  );

//        MyFile.execCmdsforResult(
//                new String[]{"chmod 666 /data/local/tmp"
//
//                }
//        );

        Log.d(TAG, "assertInit: run");
        MyFile.execCmdsforResult(
                new String[]{"chmod 777 "
                        +HookShare.pathNkFolderData
                         }
        );


    Ut.copyAssetsFile(context,
                "cpuinfo", "/sdcard/cpuinfo");

    Ut.copyAssetsFile(context,
                "mccmncJsonData",
            HookShare.pathNkFolder+"/mccmncJsonData" );
/*    File file=new File( HookShare.pathNkFolderData
    +"mccmncJsonData" );*/

       String command = "cp -r -f "+
                HookShare.pathNkFolder+""
               + "/mccmncJsonData" + " "
               + HookShare.pathNkFolderData
               +"/mccmncJsonData" ;



        MyFile.execCmdsforResult(
                new String[]{ command,"chmod 777 "
                  +HookShare.pathNkFolderData
                        +"/mccmncJsonData"  }
                );

        File f=new File( HookShare.PATH_DEVICE_PHONE );
        if ( !f.exists() ) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        command = "cp -r -f "
                +HookShare.PATH_DEVICE_PHONE
                +" "+HookShare.PATH_DEVICE_PHONE_DATA;

        File fileData=new File(HookShare.PATH_DEVICE_PHONE_DATA);
        if (!fileData.exists()){
            MyFile.execCmdsforResult(
                    new String[]{ command,"chmod 666 "
                            +HookShare.PATH_DEVICE_PHONE_DATA
                    }
            );
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.P)
    public void testPhone() {

        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(TELEPHONY_SERVICE);
        int getSimCarrierId = telephonyManager.getSimCarrierId();
        String getSimOperator = telephonyManager.getSimOperator();
        String getSimOperatorName = telephonyManager.getSimOperatorName();
        String getNetworkOperatorName = telephonyManager.getNetworkOperatorName();
        String getNetworkOperator = telephonyManager.getNetworkOperator();
        String getNetworkCountryIso = telephonyManager.getNetworkCountryIso();
        int getSimState = telephonyManager.getSimState();
        int getPhoneType = telephonyManager.getPhoneType();
        String getSimCountryIso = telephonyManager.getSimCountryIso();

        Log.d(TAG, "testPhone: getSimOperator=" + getSimOperator);
        Log.d(TAG, "testPhone: getSimOperatorName=" + getSimOperatorName);
        Log.d(TAG, "testPhone: getNetworkOperatorName=" + getNetworkOperatorName);
        Log.d(TAG, "testPhone: getNetworkOperator=" + getNetworkOperator);
        Log.d(TAG, "testPhone: getNetworkCountryIso=" + getNetworkCountryIso);
        Log.d(TAG, "testPhone: getSimState=" + getSimState);
        Log.d(TAG, "testPhone: getPhoneType=" + getPhoneType);
        Log.d(TAG, "testPhone: getSimCountryIso=" + getSimCountryIso);

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(
                        this,
                Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "testPhone: phonenumber" + telephonyManager.getLine1Number());
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            int getSimSpecificCarrierId= telephonyManager.getSimSpecificCarrierId();
            String getNetworkSpecifier=telephonyManager.getManufacturerCode();
            int getCarrierIdFromSimMccMnc =telephonyManager.getCarrierIdFromSimMccMnc();
            int getCardIdForDefaultEuicc= telephonyManager.getCardIdForDefaultEuicc();
            Log.d(TAG, "testPhone: q-getSimSpecificCarrierId="+getSimSpecificCarrierId );
            Log.d(TAG, "testPhone: q-getNetworkSpecifier="+getNetworkSpecifier );
            Log.d(TAG, "testPhone: q-getCarrierIdFromSimMccMnc="+getCarrierIdFromSimMccMnc );
            Log.d(TAG, "testPhone: q-getCardIdForDefaultEuicc="+getCardIdForDefaultEuicc );
        }

        String getSimCarrierIdName= (String) telephonyManager.getSimCarrierIdName();
        int getCallState=  telephonyManager.getCallState();
        String getNetworkSpecifier=telephonyManager.getNetworkSpecifier();
        //String getNetworkSpecifier=telephonyManager.getGroupIdLevel1();
        //p int getDataNetworkType=telephonyManager.getDataNetworkType();
         int getDataState=  telephonyManager.getDataState();

        Log.d(TAG, "testPhone: getSimCarrierIdName="+ getSimCarrierIdName);
        Log.d(TAG, "testPhone: getCallState="+ getCallState);
        Log.d(TAG, "testPhone: getNetworkSpecifier="+ getNetworkSpecifier);
        Log.d(TAG, "testPhone: getDataState="+ getDataState);

       //p int getDataNetworkType=  telephonyManager.getDataNetworkType();
    }

    public void mainActivityDataInit(){

        mainActivityData=new MainActivityData();
        mainActivityData.setContext( getApplicationContext() );
        mainActivityData.setActivityContext( MainActivity.this );
        mainActivityData.setLogTextview( (TextView)findViewById( R.id.tv_log ) );
        mainActivityData.setEt_path( (EditText)findViewById(R.id.et_path ) );
        mainActivityData.setActivity( MainActivity.this );

    }

    public void setSelectedPackges() {

        String json= MyFile.readFileToString( HookShare.pathPackages );
        JSONObject jsonObject= JSON.parseObject(json);
        JSONObject jobj2=new JSONObject();
        for ( Map.Entry<String,Object> entry :
                jsonObject.entrySet() ) {
            if( (boolean)entry.getValue() ){
                jobj2.put( entry.getKey(),true );
            }
        }
        Ut.fileWriterTxt( HookShare.pathSelectedPackages,jobj2.toJSONString() );

    }

    public void Permission() {
        boolean isGranted = true;
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                //如果没有写sd卡权限
                isGranted = false;
            }
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                isGranted = false;
            }
            Log.i("cbs", "isGranted == " + isGranted);
            if (!isGranted) {
                this.requestPermissions(
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission
                                .ACCESS_FINE_LOCATION,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        102);
            }

        }

    }

    @Override
    protected void onStop() {
//        Log.d(TAG, "onStop: " );
//        //if ( HookShare.boolIsPackageEmpty()!=true )
//         if (HookShare.returnSelectedPackages()==null)
//            MyUi.dialogShow( new String[]{ "未指定app" },MainActivity.this );
           super.onStop();

    }

    @Override
    protected void onResume() {
                Log.d(TAG, "onResume: " );
        //if ( HookShare.boolIsPackageEmpty()!=true )
         if (HookShare.returnSelectedPackages( HookShare.pathPackages )==null)
            MyUi.dialogShow( new String[]{ "未指定app" },MainActivity.this );
        super.onResume();
    }

    private void deviceLog() {

        TelephonyManager  telephonyManager =( TelephonyManager )getApplicationContext(). getSystemService( Context.TELEPHONY_SERVICE );
        Log.d(TAG, "onCreate:getSubscriberId"+telephonyManager.getSubscriberId() );
        Log.d(TAG, "onCreate:getDeviceId"+telephonyManager.getDeviceId  () );
//            PackageManager packageManager=getApplication().getPackageManager();
//            List< PackageInfo > tp=packageManager.getInstalledPackages(0);
            Log.d("aaa","bbbb");
            listDevice=new ArrayList<>();
            String pa;

       // Log.d(TAG, "deviceLog: ip="+Ut.getIPAddress( getApplicationContext() ) );
            
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {

            pa="onCreate: getDeviceId="+telephonyManager.getDeviceId(0);
            Log.d(TAG, pa );
            listDevice.add(pa);
            pa="onCreate: getSubscriberId="+telephonyManager.getSubscriberId();
            Log.d(TAG, pa );
            listDevice.add(pa);
            pa="onCreate: build model-"+Build.MODEL;
            Log.d(TAG, pa );
            listDevice.add(pa);
            pa="onCreate: build MANUFACTURER-"+Build.MANUFACTURER;
            Log.d(TAG, pa);
            listDevice.add(pa);
            pa="onCreate: build BRAND-"+Build.BRAND;
            Log.d(TAG, pa );
            listDevice.add(pa);
            pa="onCreate: build HARDWARE-"+Build.HARDWARE ;
            Log.d(TAG,pa);
            listDevice.add(pa);
            pa="onCreate: build BOARD-"+Build.BOARD;
            Log.d(TAG,pa  );
            listDevice.add(pa);
            pa="onCreate: build SERIAL-"+Build.SERIAL;
            Log.d(TAG, pa );
            listDevice.add(pa);
            pa="onCreate: build DEVICE-"+Build.DEVICE;
            Log.d(TAG,  pa);
            listDevice.add(pa);
            pa="onCreate: build FINGERPRINT-"+Build.FINGERPRINT;
            Log.d(TAG,  pa);
            listDevice.add(pa);
            pa="onCreate: build DISPLAY-"+Build.DISPLAY;
            Log.d(TAG, pa );
            listDevice.add(pa);
            pa="onCreate: build ID-"+Build.ID;
            Log.d(TAG, pa  );
            listDevice.add(pa);
            pa="onCreate: build HOST-"+Build.HOST ;
            Log.d(TAG,pa  );
            listDevice.add(pa);
            pa="onCreate: build TAGS-"+Build.TAGS  ;
            Log.d(TAG,pa );
            listDevice.add(pa);
            pa= "onCreate: build TIME-"+Build.TIME ;
            Log.d(TAG, pa);
            listDevice.add(pa);
            pa="onCreate: build .VERSION.INCREMENTAL-"+Build.VERSION.INCREMENTAL;
            Log.d(TAG,  pa );
            listDevice.add(pa);
            pa="onCreate: build .VERSION.RELEASE-"+Build.VERSION.RELEASE;
            Log.d(TAG,  pa );
            listDevice.add(pa);
            pa="onCreate: build .VERSION.CODENAME-"+Build.VERSION.CODENAME;
            Log.d(TAG,  pa );
            listDevice.add(pa);

            Configuration configuration=getResources().getConfiguration();
            listDevice.add(pa);
            //Log.d(TAG,  "colorMode-"+configuration.colorMode );
            Log.d(TAG,  "configAll densityDpi-"+configuration.densityDpi );
            listDevice.add(pa);
            Log.d(TAG,  "configAll fontScale-"+configuration.fontScale );
            listDevice.add(pa);
            Log.d(TAG,  "configAll hardKeyboardHidden-"+configuration.hardKeyboardHidden );
            listDevice.add(pa);
            Log.d(TAG,  "configAll keyboard-"+configuration.keyboard );
            listDevice.add(pa);
            Log.d(TAG,  "configAll orientation-"+configuration.orientation );
            listDevice.add(pa);
            Log.d(TAG,  "configAll screenHeightDp-"+configuration.screenHeightDp );
            listDevice.add(pa);
            Log.d(TAG,  "configAll screenLayout-"+configuration.screenLayout );
            listDevice.add(pa);
            Log.d(TAG,  "configAll screenWidthDp-"+configuration.screenWidthDp );
            listDevice.add(pa);
            Log.d(TAG,  "configAll smallestScreenWidthDp-"+configuration.smallestScreenWidthDp );
            listDevice.add(pa);
            Log.d(TAG,  "configAll uiMode-"+configuration.uiMode );
            listDevice.add(pa);
            Log.d(TAG,  "configAll keyboardHidden-"+configuration.keyboardHidden );
            listDevice.add(pa);
            Log.d(TAG,  "configAll navigation-"+configuration.navigation );
            listDevice.add(pa);
            Log.d(TAG,  "configAll navigationHidden-"+configuration.navigationHidden );
            listDevice.add(pa);
            Log.d(TAG,  "configAll touchscreen-"+configuration.touchscreen );
            listDevice.add(pa);
            Log.d(TAG, "onCreate: getConfiguration mccmnc="+configuration.mcc+configuration.mnc );
            listDevice.add(pa);
            Log.d(TAG, "onCreate: getConfiguration="+configuration.toString()  );
            listDevice.add(pa);
            Log.d(TAG, "onCreate: getSystemProperties-ro.product.cpu.abi-"+ Ut.getSystemProperties("ro.product.cpu.abi")  );
            listDevice.add(pa);
            Log.d(TAG, "onCreate: getSystemProperties-ro.build.description-"+ Ut.getSystemProperties("ro.build.description")  );
            listDevice.add(pa);
            Log.d(TAG, "onCreate: getSystemProperties-gsm.version.baseband-"+Ut.getSystemProperties("gsm.version.baseband")  );
            listDevice.add(pa);

            // Log.d(TAG, "onCreate: build TAGS-"+Build.   );
            Log.d(TAG, "onCreate: build getRadioVersion-"+Build.getRadioVersion()  );
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d(TAG, "onCreate: build getSerial-"+Build.getSerial() );
            }
            Log.d(TAG, "onCreate: build BOOTLOADER-"+Build.BOOTLOADER  );
            Log.d(TAG, "onCreate: build HOST-"+Build.HOST  );
            Log.d(TAG, "onCreate: build TAGS-"+Build.TAGS  );
            Log.d(TAG, "onCreate: build TYPE-"+Build.TYPE  );
            Log.d(TAG, "onCreate: build RADIO-"+Build.RADIO  );
            Log.d(TAG, "onCreate: build TIME-"+Build.TIME  );
            //Log.d(TAG, "onCreate: build HOST-"  );

        }
    }

    public void dialogShowFunctionPackage(){

        List<String> packagesNames= Ut.getPackageNames( getApplicationContext() );
        List<String> listSelected;

        JSONObject jsonObject;
        jsonObject=new JSONObject();

        //获取到的包名写成{ "key1":false,"key2":.. }
        for (String name:
                packagesNames ) {
                jsonObject.put( name,false );
        }

        //将勾选的包名改写为true
        String packagesJson=MyFile.readFileToString( HookShare.PATH_FUNCTION_PACKAGES );
        if( packagesJson!="" ){
            listSelected= Ut.getSelectedJobjByJson ( packagesJson );

            if (listSelected!=null){
                Log.d(TAG, "dialogShowFunctionPackage: listSelected"+listSelected.size() );
                for ( String str :
                     listSelected ) {
                    Log.d(TAG, "dialogShowFunctionPackage: Str="+str );
                    if (jsonObject.containsKey( str ) ){
                        jsonObject.put( str,true );
                    }
                }
            }
        }

        Ut.fileWriterTxt( HookShare.PATH_FUNCTION_PACKAGES,jsonObject.toJSONString() );
        MyUi.dialogSetMultiChoiceItems(MainActivity.this, "packages"
                , R.mipmap.ic_launcher, HookShare.PATH_FUNCTION_PACKAGES, "确定", null);

    }

    public  void loadUiSetting(  ){
        
        EditText et_path=( EditText )  findViewById( R.id.et_path );
        Switch sw_script_watch= (Switch)findViewById( R.id.sw_script_watch );
        String uiJson=MyFile.readFileToString( HookShare.PATH_UI_SETTING );
        JSONObject jsonObject = null;

        
        if (uiJson!=""){
            jsonObject=JSON.parseObject(uiJson);
            //if (jsonObject.containsKey( "et_path" ));
            jsonObject= setUiDefault( jsonObject,"et_path","/sdcard/Download" );
            jsonObject= setUiDefault( jsonObject,"sw_script_watch",false );
            jsonObject= setUiDefault( jsonObject,"sw_scriptBootedRun",true );
            jsonObject= setUiDefault( jsonObject,"et_pkgName","com.rf.icon" );
            jsonObject= setUiDefault( jsonObject,"sw_enable_para",false );
        }else
        {
            //defult value
            jsonObject=new JSONObject();
            jsonObject.put( "et_path","/sdcard/Download" );
            jsonObject.put( "sw_script_watch", false );
            jsonObject.put( "sw_scriptBootedRun", true );
            jsonObject.put( "et_pkgName", "com.apple" );
            jsonObject.put( "sw_enable_para", false );

        }

        et_path.setText( jsonObject.get( "et_path" ).toString() )  ;
        et_pkgName.setText( jsonObject.get( "et_pkgName" ).toString() )  ;
        sw_scriptBootedRun.setChecked( jsonObject.getBoolean("sw_scriptBootedRun") );
        sw_script_watch.setChecked( jsonObject.getBoolean( "sw_script_watch" )  );
        sw_enable_para.setChecked( jsonObject.getBoolean( "sw_enable_para" )  );

        Log.d(TAG, "loadUiSetting: finish");
//        MyFile.fileWriterTxt( HookShare.PATH_UI_SETTING,jsonObject.toJSONString() );

    }

    public JSONObject setUiDefault( JSONObject jsonObject, String key,Object defaultV ){
        if (!jsonObject.containsKey(key))
            jsonObject.put( key,defaultV );
        return jsonObject;
    }

    public void saveUiSetting(  ){

        EditText et_path=( EditText )  findViewById( R.id.et_path );
        Switch sw_script_watch = ( Switch ) findViewById( R.id.sw_script_watch );
        Switch sw_scriptBootedRun=( Switch )findViewById(R.id.sw_scriptBootedRun);
        EditText et_pkgName=( EditText )findViewById(R.id.et_pkgName);

        JSONObject jsonObject=new JSONObject();
        jsonObject.put( "et_path",et_path.getText() );
        jsonObject.put("sw_script_watch", sw_script_watch.isChecked() );
        jsonObject.put("sw_scriptBootedRun", sw_scriptBootedRun.isChecked() );
        jsonObject.put("et_pkgName", et_pkgName.getText () );
        jsonObject.put("sw_enable_para",sw_enable_para.isChecked() );

        String command = "cp -r -f "+
                HookShare.PATH_UI_SETTING+
                " "
                + HookShare.pathUiSettingData;

        MyFile.execCmdsforResult(
                new String[]{  command,"chmod 777 "
                        +HookShare.pathUiSettingData
                          }
        );

        MyFile.fileWriterTxt( HookShare.PATH_UI_SETTING,jsonObject.toJSONString() );
        logUi("保存完成");

    }

    public void logUi(final String text){
                logTextview.setText( text );
    }

    public void logUiThread(final String text){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logTextview.setText( text );
            }
        });
    }

    public void startWatchService(){

        Context context=getApplicationContext();
        if ( Ut.isServiceRunning( context,AlarmService.class.getName())==false ){
            Log.d(TAG, "startWatchService: AlarmService not run.start..");
            Intent serIntent= new Intent( context, AlarmService.class);
            serIntent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
            serIntent.addFlags( Intent.FLAG_ACTIVITY_CLEAR_TASK );
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                Log.d(TAG, "startWatchService: startForegroundService" );
                context.startForegroundService(serIntent);
            }else{
                context.startService(serIntent);
            }
            //context.startService( serIntent );
            Log.v(TAG, "启动服务.....");
        }else{
            Log.d(TAG, "startWatchService: AlarmService already run.");
        }


    }

    public void ui(){

        //打开电话卡设置
        Button bt_phoneSet=(Button)findViewById(R.id.bt_phoneSet);
        bt_phoneSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity( new Intent( "ActivityPhone"
                          ));
            }
        });

        //跳转app详情
        Button bt_appDetails=(Button)findViewById(R.id.bt_appDetails);
        bt_appDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Ut.goToAppDetailSettings(this,)
                DataBack instance = DataBack.getInstance(mainActivityData);
                Ut.goToAppDetailSettings( getApplicationContext(),instance.getFunctionPackageName() );
            }
        });

        //启动监控服务
        Button bt_watch=  (Button)findViewById( R.id.bt_watch );
        bt_watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startWatchService();
            }

        });

        //保存ui设置
        Button bt_save_ui=(Button)findViewById( R.id.bt_save_ui);
        bt_save_ui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveUiSetting();
            }
        });

        //删除备份
        Button bt_delBack=findViewById( R.id.bt_delBack );
        bt_delBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBack.getInstance(mainActivityData).delBack();
            }
        });

        //显示备份的内容
        Button  bt_dataList = findViewById(R.id.bt_dataList );
        bt_dataList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBack.getInstance( mainActivityData ).dialogShowDataBack( MyUi.COUNT_FALSE );
            }
        });

        //载入
        Button bt_load=findViewById( R.id.bt_load );
        bt_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBack instance = DataBack.getInstance(mainActivityData);
                Ut.stopAppByKill(  MainActivity.this,instance.getFunctionPackageName() );
                instance.loadData();
            }
        });

        //保存数据
        Button bt_save=findViewById( R.id.bt_save  );
        bt_save.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        DataBack.getInstance(mainActivityData). saveAppByThread ();
                    }
                }
        );

        //选中执行功能列表
        Button bt_run_function=findViewById( R.id.bt_run_function );
        bt_run_function.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                       // dialogShowFunctionPackage();
                        //HookShare.PATH_FUNCTION_PACKAGES
                        Intent intent= new Intent( "Activity_packageShow"
                        );
                        //intent.putExtra(  )
                        intent.putExtra( HookShare.intentExtraPackageShowPath, HookShare.PATH_FUNCTION_PACKAGES );
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity( intent );
                    }
                }
        );

        //测试获取
 /*       Button bt_test=(Button) findViewById(R.id.bt_test);
        bt_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyUi.dialogShow( listDevice.toArray( new String[listDevice.size() ] ),MainActivity.this ) ;
            }
        });*/

        //
        Button bt_permission=(Button) findViewById(R.id.bt_permission);
        bt_permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPermissionManager1();
            }
        });



/*        //settingPath
        Button bt_showPath=( Button )findViewById( R.id.bt_showPath );
        bt_showPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogShowPath();
            }
        });*/

        //packages
                Button bt_package=  (Button)  findViewById(R.id.bt_package);
                bt_package.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        //dialogSelectPackageName();
                        Intent intent= new Intent( "Activity_packageShow"
                        );
                        //intent.putExtra(  )
                        intent.putExtra( HookShare.intentExtraPackageShowPath, HookShare.pathPackages );
                        startActivity( intent );

            }
        });

        //device
        bt_new=(Button) findViewById(R.id.bt_new);
        bt_new.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DataBack instans=DataBack.getInstance(mainActivityData);
                Ut.stopAppByKill( getApplicationContext(), instans.getFunctionPackageName() );
               // instans.delCacheByThread();
                //basehook
                BaseInfo baseInfo = FakeBase.randomDevice(getApplicationContext());
                JSONObject jsonObject= ( JSONObject) JSONObject.toJSON( baseInfo );

                //安卓id信息
                HookShare.WriteBean2Json( baseInfo,HookShare.pathDeviceJson );
                String command = "cp -r -f "+
                        HookShare.pathDeviceJson + " " + HookShare.pathDeviceJsonData;
                MyFile.execCmdsforResult( new String[]{ command,"chmod 666 "+HookShare.pathDeviceJsonData } );

                 command = "cp -r -f "+
                        HookShare.pathPackages + " " + HookShare.pathPackagesData;
                MyFile.execCmdsforResult( new String[]{ command,"chmod 666 "+HookShare.pathPackagesData } );


                /*
                //DeviceWriteDefaultPhone();
                SharedPreferences sp=getApplication().getSharedPreferences(
                        HookShare.configPhoneCountryCode,MODE_PRIVATE
                );
                String country=sp.getString(HookShare.configPhoneCountryCode,"");
                if (!country.equals("")){
                    String jsonMccmnc=MyFile.readFileToString(
                            HookShare.pathNkFolder+"/mccmncJsonData" );

                    //解析出mncmcc等所有相关值写入
                    if (!jsonMccmnc.equals("")){

                        JSONObject jobjMccmnc=JSON.parseObject(jsonMccmnc);
                        String jsonStringCountry=jobjMccmnc.getString(country);
                        JSONArray selectCountryJsonArray=JSONArray.parseArray(jsonStringCountry);

                        JSONObject jobjFinal=selectCountryJsonArray.getJSONObject( Ut.r_(0,
                                selectCountryJsonArray.size()-1) );

                        Log.d(TAG, "onClick: json="+country+"="
                        +jobjFinal.getString("getSimOperatorName") );
                        JSONObject jobjRet= new JSONObject( );
                        String key="mnc";
                        jobjRet.put( key,jobjFinal.getString(key) );
                         key="mcc";
                        jobjRet.put( key,jobjFinal.getString(key) );
                        key="getNetworkOperator";
                        jobjRet.put( key,jobjFinal.getString(key) );
                        key="getSimOperator";
                        jobjRet.put( key,jobjFinal.getString(key) );
                        key="getNetworkOperatorName";
                        jobjRet.put( key,jobjFinal.getString(key) );
                        key="getSimOperatorName";
                        jobjRet.put( key,jobjFinal.getString(key) );
                        key="getSimCountryIso";
                        jobjRet.put( key,jobjFinal.getString(key) );
                        key="getNetworkCountryIso";
                        jobjRet.put( key,jobjFinal.getString(key) );
                        key="getLine1Number";
                        jobjRet.put( key,jobjFinal.getString(key) );
                        MyFile.fileWriterTxt( HookShare.PATH_PHONE_DEVICE,jobjRet.toJSONString() );
                        command = "cp -r -f "+
                                HookShare.PATH_PHONE_DEVICE + " " + HookShare.PATH_DEVICE_PHONE_DATA;
                        MyFile.execCmdsforResult( new String[]{ command,"chmod 666 "+HookShare.PATH_DEVICE_PHONE_DATA  } );

                    }
                }else
                    DeviceWriteDefaultPhone();*/

//{"getDeviceId":"352003411773066",
// "getLine1Number":1164428807,"getNetworkCountryIso":"MY",
// "getNetworkOperator":50212,"getNetworkOperatorName":"Maxis",
// "getSimCountryIso":"my","getSimOperator":"50217","getSimOperatorName":"Maxis",
// "getSubscriberId":"502176939124995","mcc":"502","mnc":"17"}

           /*
                MyOpenHelper myOpenHelper=new MyOpenHelper( getApplicationContext() );
                SQLiteDatabase db=myOpenHelper.getWritableDatabase();
                for (  Map.Entry<String, Object> entry :
                    jsonObject.entrySet() ) {
                    ContentValues contentValues=new ContentValues();

                    if ( entry.getValue()!=null ){
                        contentValues.put( "k",entry.getKey()  );
                        contentValues.put("v",entry.getValue().toString() );
                    }else {
                        contentValues.put( "k",entry.getKey()  );
                        contentValues.put("v","" );
                    }
                    Log.d(TAG, "onClick: "+contentValues.toString());
                    db.insert( "base","k",contentValues );
                }
                db.close();*/
                
                //hookPackages
//                FakePackage fakePackage = FakePackage.getInstance(getApplicationContext());
//                fakePackage.fakePackages ();
                dialogShowDevice();


            }
        });
    }

    public void DeviceWriteDefaultPhone(){

        //{"getDeviceId":"352003411773066",
    // "getLine1Number":1164428807,"getNetworkCountryIso":"MY",
    // "getNetworkOperator":50212,"getNetworkOperatorName":"Maxis",
    // "getSimCountryIso":"my","getSimOperator":"50217","getSimOperatorName":"Maxis",
    // "getSubscriberId":"502176939124995","mcc":"502","mnc":"17"}

        JSONObject jsonObject=new JSONObject();
        jsonObject.put( "getDeviceId", PhoneRndTools.randomImei() );
        jsonObject.put( "getLine1Number",11+PhoneRndTools.randomNum(10) );
        jsonObject.put( "getNetworkCountryIso","MY" );
        jsonObject.put( "getNetworkOperator","50212" );
        jsonObject.put( "getNetworkOperatorName","Maxis" );
        jsonObject.put( "getSimCountryIso","my" );
        jsonObject.put( "getSimOperator","50217" );
        jsonObject.put( "getSimOperatorName","Maxis" );
        jsonObject.put( "getSubscriberId","50217"+PhoneRndTools.randomNum(10) );
        jsonObject.put( "mcc","502" );
        jsonObject.put( "mnc","17" );
        MyFile.fileWriterTxt( HookShare.PATH_PHONE_DEVICE,jsonObject.toJSONString() );

    }

    /**
     *
     */
    private  void dialogSelectPackageName(){

        List<String> listStringPackages =Ut.getPackageNames( MainActivity.this );
        List<String> google=new ArrayList<>();
        List<String> android=new ArrayList<>();
        List<String> other=new ArrayList<>();

        //
        for ( String str :
           listStringPackages  ) {
            if(str.indexOf( "com.google." )!=-1)
                google.add(str);
            else if ( str.indexOf("com.android")!=-1 )
                android.add(str);
            else if (str.indexOf("com.lge")==-1)
                other.add( str );
        }

        Ut.writeLines( "/sdcard/google.txt",google );
        Ut.writeLines( "/sdcard/android.txt",android );
        Ut.writeLines( "/sdcard/other.txt",other );
        Ut.writeLines( "/sdcard/1.txt",listStringPackages );
        List<Boolean> listBooleanPackages=new ArrayList<>();

        PackageManager packageManager = getApplication().getPackageManager();
       //  +"\r\n,"+
        List<PackageInfo> listPackageInfos=packageManager.getInstalledPackages(0);
        for ( PackageInfo p :
              listPackageInfos ) {

            Log.d(TAG, "PackageNameHook: " +
                    "name="+p.packageName+","+p.lastUpdateTime);

          /*  Log.d(TAG, "dialogSelectPackageName: \r\n" +
                            "packages="+p.packageName+"\r\n-sharedUserId="+p.sharedUserId
            +"\r\n,applicationInfo"+p.applicationInfo
                            +"\r\n,sharedUserId="+p.sharedUserId
                            +"\r\n,firstInstallTime="+p.firstInstallTime
                            +"\r\n,versionName="+p.versionName
                            +"\r\n,activities="+p.activities
                            +"\r\n,baseRevisionCode="+p.baseRevisionCode
                            +"\r\n,configPreferences="+p.configPreferences
                            +"\r\n,featureGroups="+p.featureGroups
                            +"\r\n,gids="+p.gids
                            +"\r\n,installLocation="+p.installLocation
                            +"\r\n,instrumentation="+p.instrumentation
                            +"\r\n,lastUpdateTime="+p.lastUpdateTime
                            +"\r\n,splitNames="+p.splitNames
                            +"\r\n,signatures="+p.signatures
                            +"\r\n,splitRevisionCodes="+p.splitRevisionCodes
                            +"\r\n,sharedUserLabel="+p.sharedUserLabel
                            +"\r\n,receivers="+p.receivers
                            +"\r\n,services="+p.services
                            +"\r\n,providers="+p.providers
                            +"\r\n,requestedPermissions="+p.requestedPermissions
                            +"\r\n,requestedPermissionsFlags="+p.requestedPermissionsFlags
                            +"\r\n,reqFeatures="+p.reqFeatures
                            +"\r\n,featureGroups="+p.featureGroups
                            +"\r\n______________________________________________"
                    );*/
        }


        String jsonTxtPackages= MyFile.readFileToString( HookShare.pathPackages );
        final JSONObject jsonObject;
        Log.d(TAG, "dialogSelectPackageName: jsonTxtPackages="+jsonTxtPackages );
        //
        if ( jsonTxtPackages==null||jsonTxtPackages.equals("")){
            {Log.d(TAG, "dialogSelectPackageName: first run");
            jsonObject=new JSONObject();
            //all packageName option put into listBooleanPackages
            for ( String string : listStringPackages  ) {
                jsonObject.put( string,false );
                listBooleanPackages.add(false);
            }
            Ut.fileWriterTxt( HookShare.pathPackages ,jsonObject.toJSONString() );}

        }else {
            Log.d(TAG, "dialogSelectPackageName: read last");
            JSONObject jobjSelectedPackages;
            jsonObject=JSON.parseObject( jsonTxtPackages );
            //判断实际应用列表和上次读取设置的差值,说明可能有安装卸载的变化
            if ( jsonObject.size()!=listStringPackages.size() ){
                //获取上次选中的包名,然后将列表item 对应显示出来
                 jobjSelectedPackages=HookShare.returnSelectedPackages( HookShare.pathPackages );
                for ( String string :
                        listStringPackages  ) {
                    //listBooleanPackages.add ( false );
                    if (jobjSelectedPackages!=null){
                        if ( jobjSelectedPackages.containsKey( string ) )
                            listBooleanPackages.add ( true );
                        else
                            listBooleanPackages.add( false );
                    }

                }

            }else {
                for ( String string :
                        listStringPackages  ) {
                    Log.d(TAG, "dialogSelectPackageName: k,v="+string+","+jsonObject.getBoolean( string )  );
                    listBooleanPackages.add ( jsonObject.getBoolean (string) );
                }

            }

        }

        final String items[]=listStringPackages.toArray( new String[ listStringPackages.size() ] );
       // final Boolean[] selectedTmp=listBooleanPackages.toArray( new Boolean[listBooleanPackages.size()] ) ;
        boolean[] selected=new boolean[ listStringPackages.size() ];
        boolean tp=false;

        Iterator<Boolean> iterator=listBooleanPackages.iterator();
        int i=0;
        while ( iterator.hasNext() ) {
                Boolean b=iterator.next();
            if ( b!=null )
                  //selected[i]=iterator.next().booleanValue();
                  selected[i]=b.booleanValue();
            i=i+1;
        }

        Log.d(TAG, "dialogSelectPackageName: selected-size=" +selected.length );

        final AlertDialog.Builder builder=new AlertDialog.Builder( MainActivity.this );  //先得到构造器
        Log.d( TAG, "dialogSelectPackageName: alertBuild" );
        builder.setTitle("appPackage"); //设置标题
        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可

        List<String> packageSeleted=new ArrayList<>();
            builder.setMultiChoiceItems(items,selected,new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    // dialog.dismiss();
                    if( isChecked )
                        jsonObject.put( items[which],true );
                        else
                        jsonObject.put( items[which],false );
                    //Toast.makeText(MainActivity.this, items[which]+isChecked, Toast.LENGTH_SHORT).show();
                }
            });


            builder.setPositiveButton("确定",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Ut.fileWriterTxt( HookShare.pathPackages,jsonObject.toJSONString() );
                    String command = "cp -r -f "+
                            HookShare.pathPackages + " " + HookShare.pathPackagesData ;
                    MyFile.execCmdsforResult( new String[]{ command,"chmod 666 "+HookShare.pathPackagesData } );
                    dialog.dismiss();
                    //Toast.makeText(MainActivity.this, "确定", Toast.LENGTH_SHORT).show();
                    //android会自动根据你选择的改变selected数组的值。
                    /*
                        for (int i=0;i<selected.length;i++){
                        Log.e("hongliang",""+selected[i]);
                    }
                    */
                }
            });

            AlertDialog alertDialo=builder.create();
            alertDialo.show();

    }

    private void dialogShowDevice () {

        String json=MyFile.readFileToString( HookShare.pathDeviceJson );
        JSONObject jobj=  JSON.parseObject ( json);

        ArrayList<String> list=new ArrayList<>();
        for (Map.Entry entry : jobj.entrySet()) {
            list.add( entry.getKey ()+":"+entry.getValue() );
        }

       final String items[]= list.toArray( new String[ list.size() ] );

        //dialog参数设置
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);  //先得到构造器
        builder.setTitle("参数"); //设置标题
        builder.setIcon(R.mipmap.ic_launcher);

        //设置图标，图片id即可
        //设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(items,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, items[which], Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("返回",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, "完成", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();

    }

    private void dialogShowPath(){

        final String items[]= { "phone:sdcard/yztc/device.txt"
                ,"Base:/sdcard/deviceJson.txt"
                ,"/sdcard/packages.txt" };

        //dialog参数设置
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);  //先得到构造器
        builder.setTitle("settingPath"); //设置标题
        builder.setIcon(R.mipmap.ic_launcher);

        //设置图标，图片id即可
        //设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(items,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(MainActivity.this, items[which], Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("返回",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //Toast.makeText(MainActivity.this, "完成", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
    }

    public void testHook(){

        context=getApplicationContext();
        Pattern p = Pattern.compile("a*b");
        Matcher m = p.matcher("aaaaab");
        boolean b = m.matches();
        Log.d(TAG, "testHook: matches=true*patter=*"+b +m.pattern() );

        WifiManager wSystemServi= (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        int vi1;
        for (int i=0;i<1;i++){
            wSystemServi.setWifiEnabled(true);
            SystemClock.sleep(500);
        }
        String tagMethod="testHook: ";

        Log.d(TAG,"testHook: getMacAddress"+wSystemServi.getConnectionInfo().getMacAddress() );
        TelephonyManager telephonyManager=(TelephonyManager)getApplicationContext().getSystemService(TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d(TAG,"testHook: getImei"+telephonyManager.getImei() );
        }else
            Log.d(TAG,tagMethod+"getDeviceId"+telephonyManager.getDeviceId() );

        //ro.serialno
        try {
            //Settings.Secure.class, "getString",
            Class cName = Class.forName("android.os.SystemProperties");
            Class[] classArray = new Class[1];
            classArray[0] = String.class;
            Method mMethod = null;
            mMethod = cName.getMethod("get", classArray);
            mMethod.setAccessible(true);
            Object[] objectArray = new Object[1];
            objectArray[0] = "ro.serialno";
            String sTempSerialN = (String) mMethod.invoke(cName, objectArray);
            Log.d( TAG,"ro.serialno="+sTempSerialN   );
//            objectArray[0]="sys.st.sn";
//            sTempSerialN = (String) mMethod.invoke(cName, objectArray);
//            Log.d( TAG,"sys.st.sn="+sTempSerialN   );
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "testHook: BOARD="+Build.BOARD );
        Log.d(TAG, "testHook: MANUFACTURER="+Build.MANUFACTURER );
        Log.d(TAG, "testHook: PRODUCT="+Build.PRODUCT );
        Log.d(TAG, "testHook: HOST="+Build.HOST );
        Log.d(TAG, "testHook: HARDWARE="+Build.HARDWARE );

    }

    public void  startPermissionManager1(){

        Intent intent=new Intent();
        DataBack instance = DataBack.getInstance(mainActivityData);
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        //intent.setData( Uri.parse("package:jp.naver.line.android") );
        intent.setData( Uri.parse("package:"+instance.getFunctionPackageName() ) );
        //intent.setFlags(0x10008000);
        intent.setComponent( new ComponentName("com.android.settings","com.android.settings.applications.InstalledAppDetailsTop" )   );
        startActivity( intent );

    }




}
