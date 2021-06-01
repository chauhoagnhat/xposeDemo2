package com.example.xposedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telecom.TelecomManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.example.xposedemo.Hook.HookShare;
import com.example.xposedemo.MyProvider.MyOpenHelper;
import com.example.xposedemo.bean.BaseInfo;
import com.example.xposedemo.fake.FakeBase;
import com.example.xposedemo.fake.FakePackage;
import com.example.xposedemo.utils.Common;
import com.example.xposedemo.utils.Ut;
import com.example.xposedemo.utils.SharedPref;
import com.example.xposedemo.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
    private Button bt_new;
    private List<Object> listDevice;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {

        super.onCreate( savedInstanceState );
        setContentView(R.layout.activity_main);
        new HookShare();
       // ApplicationPackageManager
        MyOpenHelper myOpenHelper=new MyOpenHelper( getApplicationContext() );
        myOpenHelper.getWritableDatabase();

//        Intent intent = getIntent();
//        CharSequence cs = intent.getCharSequenceExtra("filePath"); //filePath 为传入的文件路径信息
//        if (cs != null) {
//            File file = new File(cs.toString());
//            tvPath.setText(file.getPath());
//            files = file.listFiles();
//        } else {
//            File sdFile = Environment.getExternalStorageDirectory();
//            tvPath.setText(sdFile.getPath());
//            files = sdFile.listFiles();
//        }ra("filePath");

        PackageManager packageManager = getPackageManager();
        Intent mIntent = new Intent(Intent.ACTION_MAIN, null);
        Log.d(TAG, "onCreate: isInstance"+Intent.class.isInstance( mIntent ) );

        mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> listAllApps = packageManager.queryIntentActivities(mIntent, 0);


   /*     for ( ResolveInfo appInfo :
                listAllApps   ) {
            // appInfo = listAllApps.get(position);
            String pkgName = appInfo.activityInfo.packageName;//获取包名
            //根据包名获取PackageInfo mPackageInfo;（需要处理异常）

            PackageInfo mPackageInfo = null;
            try {
                mPackageInfo = getApplication().getPackageManager().getPackageInfo(pkgName, 0);
                Log.d(TAG, "onCreate: appName"+appInfo.loadLabel(packageManager).toString() );
                Log.d(TAG, "onCreate: packageName");
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            if (( mPackageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                //第三方应用
            } else {
                //系统应用
            }

        }*/

        viewById=findViewById(R.id.textView);
        viewById.setText("no data");
        //setSelectedPackges();

            //testHook();
            Ut.copyAssetsFile(context,"cpuinfo","/sdcard/cpuinfo" );
            String path=Environment.getExternalStorageDirectory().toString();
            Log.d(TAG, "onCreate: path="+path);

        deviceLog();
        ui();



        // String jsonStr= Utils.readFileToString(Environment.getExternalStorageDirectory ()+"/device.txt");

        String jsonStr= Utils.readFileToString( Common.DEVICE_PATH );

        if (jsonStr!=""&&jsonStr!=null){

            JSONObject jsonObjectPara;
            Log.d(TAG, "onCreate: jsonStr="+jsonStr );
            Map<String,String> map=JSONObject.parseObject(jsonStr,
                    new TypeReference< Map<String, String> >(){});
            string=Utils.join_map2str( map,"\r\n");
            Log.d(TAG, "onCreate: mapStr="+string );
            viewById.setText(string);

        }

        Log.d(TAG, "onCreate: run finish");

    }

    public void setSelectedPackges() {
        String json= Ut.readFileToString( HookShare.pathPackages );
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
        Log.d(TAG, "onStop: " );
        if ( HookShare.boolIsPackageEmpty()!=true )
            dialogShow( new String[]{ "未指定app" } );
        super.onStop();

    }

    private void deviceLog() {

        TelephonyManager  telephonyManager =( TelephonyManager )getApplicationContext(). getSystemService( Context.TELEPHONY_SERVICE );
        Log.d(TAG, "onCreate:getSubscriberId"+telephonyManager.getSubscriberId() );
        Log.d(TAG, "onCreate:getDeviceId"+telephonyManager.getDeviceId  () );

            PackageManager packageManager=getApplication().getPackageManager();
            List< PackageInfo > tp=packageManager.getInstalledPackages(0);

            listDevice=new ArrayList<>();
            String pa;

        Log.d(TAG, "deviceLog: ip="+Ut.getIPAddress( getApplicationContext() ) );
            
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {

            pa="onCreate: getDeviceId="+telephonyManager.getDeviceId(0);
            Log.d(TAG, pa );
            listDevice.add(pa);
            pa="onCreate: getSubscriberId="+telephonyManager. getSubscriberId ();
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

    public void ui(){
        //
        Button bt_test=(Button) findViewById(R.id.bt_test);
        bt_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogShow( listDevice.toArray( new String[listDevice.size() ] ) ) ;
            }
        });

        //
        Button bt_permission=(Button) findViewById(R.id.bt_permission);
        bt_permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPermissionManager1();
            }
        });

        //settingPath
        Button bt_showPath=( Button )findViewById( R.id.bt_showPath );
        bt_showPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogShowPath();
            }
        });

        //packages
        Button bt_package=  (Button)  findViewById(R.id.bt_package);
        bt_package.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSelectPackageName();
            }
        });

//device
        bt_new=(Button) findViewById(R.id.bt_new);
        bt_new.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //basehook
                BaseInfo baseInfo = FakeBase.getInstance();
                JSONObject jsonObject= (JSONObject) JSONObject.toJSON( baseInfo ) ;
                HookShare.WriteBean2Json( baseInfo,HookShare.pathDeviceJson );

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
                db.close();
                
                //hookPackages
                FakePackage fakePackage = FakePackage.getInstance(getApplicationContext());
                fakePackage.fakePackages ();

                dialogShowDevice();
            }
        });
    }

    /**
     *
     */
    private  void dialogSelectPackageName(){

        List<String> listStringPackages =Ut.getPackageNames( MainActivity.this );

        List<String> google=new ArrayList<>();
        List<String> android=new ArrayList<>();
        List<String> other=new ArrayList<>();

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


        String jsonTxtPackages=Ut.readFileToString( HookShare.pathPackages );
        final JSONObject jsonObject;

        //
        if ( jsonTxtPackages=="" ){
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
            if (jsonObject.size()!=listStringPackages.size() ){
                //获取上次选中的包名,然后将列表item 对应显示出来
                 jobjSelectedPackages=HookShare.returnSelectedPackages();
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
            selected[i]=iterator.next().booleanValue();
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

        String json=Ut.readFileToString( HookShare.pathDeviceJson );
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

    private void dialogShow( String[] items ){

        final String[] items2=items;

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
                Toast.makeText(MainActivity.this, items2[which], Toast.LENGTH_SHORT).show();
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

    private void dialogShowPath(){

        final String items[]= { "phone:sdcard/yztc/device.txt","Base:/sdcard/deviceJson.txt","/sdcard/packages.txt" };
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
/*
        ContentResolver resolver = getContentResolver();
        Uri url = Uri.parse( Common.URI+"insert" );
        Log.d(TAG, "insert: =" +Common.URI+"insert" );
        ContentValues values = new ContentValues();
        values.put("device",json  );
        Uri insert = resolver.insert(url, values);
        System.out.println(insert);
*/
    }

    public void query(){
        //获取内容解析者
/*        ContentResolver contentResolver = getContentResolver();
        Uri uri =Uri.parse(Common.URI+"query");
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
//	Cursor cursor = database.rawQuery("select * from info", null);

        while(cursor.moveToNext()){
            String name = cursor.getString(cursor.getColumnIndex("device"));
            String phone = cursor.getString(cursor.getColumnIndex("json"));
            System.out.println("name="+name+"phone"+phone);
        }*/

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
