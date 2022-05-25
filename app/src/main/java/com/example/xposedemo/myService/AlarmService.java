package com.example.xposedemo.myService;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.xposedemo.ActivityFunction;
import com.example.xposedemo.Hook.HookShare;
import com.example.xposedemo.MainActivity;
import com.example.xposedemo.R;
import com.example.xposedemo.functionModule.ScriptControl;
import com.example.xposedemo.myBroadcast.AlarmReceive;
import com.example.xposedemo.utils.MyFile;
import com.example.xposedemo.utils.Ut;

public class AlarmService extends Service {

    private static final int ONE_Miniute=1*60*1000;
    private static final int PENDING_REQUEST=0;
    private static final String TAG = "AlarmService";
    public static boolean watchRun =true;
    public static Thread t1;
    public PendingIntent pIntent;
    public AlarmManager alarmManager;
    public long triggerAtTime;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        serviceNoti();
    }

    public void serviceNoti(){

        Context context=getApplicationContext();
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        String id = "1";
        String name = "channel_name_1";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(id, name, NotificationManager.IMPORTANCE_DEFAULT);
            mChannel.setSound(null, null);
            notificationManager.createNotificationChannel(mChannel);
            notification = new Notification.Builder(context)
                    .setChannelId(id)
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(false)// 设置这个标志当用户单击面板就可以让通知将自动取消
                    .setOngoing(true)// true，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    .setSmallIcon(R.drawable.ic_launcher_background).build();
        } else {
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setContentTitle(context.getResources().getString(R.string.app_name))
                    .setContentIntent(pendingIntent)
                    .setPriority(Notification.PRIORITY_DEFAULT)// 设置该通知优先级
                    .setAutoCancel(false)// 设置这个标志当用户单击面板就可以让通知将自动取消
                    .setOngoing(true)// true，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                    .setSmallIcon(R.drawable.ic_launcher_background);
            notification = notificationBuilder.build();
        }
        startForeground(1, notification);
        Log.d(TAG,"service onCreate");
    }

    /**
     *
     版权声明：本文为CSDN博主「IT_默」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
     原文链接：https://blog.csdn.net/u014733374/article/details/53413970/
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        //serviceNoti();
        final Context context=this.getApplication();

        //通过AlarmManager定时启动广播
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

//        if ( watchRun ==false ){
//            return super.onStartCommand(intent, flags, startId);
//        }


        Intent i=new Intent(this, AlarmReceive.class);
        pIntent = PendingIntent.getBroadcast(this,PENDING_REQUEST,i,PENDING_REQUEST);

        Log.d(TAG, "onStartCommand: alartSevice is running");
        if ( getJsonWatch() ){
            scriptRun( context );
        }else{
            Log.d(TAG, "onStartCommand-run: awake-setting");
            triggerAtTime = SystemClock.elapsedRealtime()+ONE_Miniute;
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime, pIntent);
        }


        if ( watchRun ==false ){
            return super.onStartCommand(intent, flags, startId);
        }


        //从开机到现在的毫秒书（手机睡眠(sleep)的时间也包括在内
//        triggerAtTime = SystemClock.elapsedRealtime()+ONE_Miniute;

        if (watchRun){
//            Intent i=new Intent(this, AlarmReceive.class);
//            pIntent = PendingIntent.getBroadcast(this,PENDING_REQUEST,i,PENDING_REQUEST);
            //alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pIntent);
        }

        //alarmManager.cancel(pIntent);
        return super.onStartCommand(intent, flags, startId);

    }

    public boolean getJsonWatch(){

        String uiJson=MyFile.readFileToString( HookShare.PATH_UI_SETTING );
        JSONObject jsonObject=null;
        Log.d(TAG, "getJsonWatch");
        if (uiJson!=""){
            jsonObject= JSON.parseObject(uiJson);
            if (jsonObject.containsKey("sw_script_watch")){
                Log.d(TAG, "getJsonWatch: value="+jsonObject.getBoolean ("sw_script_watch") );
                return jsonObject.getBoolean ("sw_script_watch")  ;
            }
        }
        return false;
    }

    public void scriptRun(final Context context ){
        Toast.makeText( context,"检测脚本是否运行",Toast.LENGTH_LONG );
        //这里模拟后台操作
        t1 = new Thread(new Runnable() {
             @Override
             public void run() {
                 synchronized ( this ){
                     scriptRunSub(context);
                     Log.d(TAG, "run: watch-run="+watchRun);
                     //if (watchRun){
                         Log.d(TAG, "run: awake-setting");
                         triggerAtTime = SystemClock.elapsedRealtime()+ONE_Miniute;
                         alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime, pIntent);
                    // }
                 }
             }
         });

       t1.start();

//        try {
//            t1.join();
//        } catch (InterruptedException e) {
//            Log.d(TAG, "scriptRun: "+e.toString() );
//            watchRun=false;
//            e.printStackTrace();
//        }


    }

    public void scriptRunSub(Context context){

        Looper.prepare();
        if (!getJsonWatch())
            return;
        Log.d(TAG, "run: loop");
        Toast.makeText(context,"判断脚本是否运行",Toast.LENGTH_LONG);
        Log.d(TAG, "scriptRunSub: 判断脚本是否运行");
        MyFile.fileWriterTxt( HookShare.PATH_SCRIPT_RUNNING,"0" );
        try {
            Thread.sleep(10*1000);
            //Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }

       // String pkgName="com.rf.icon";
        String uiJson=MyFile.readFileToString( HookShare.PATH_UI_SETTING );
        JSONObject jobjUi=JSON.parseObject ( uiJson );
        String pkgName=jobjUi.getString("et_pkgName");
        if (pkgName == null) {
            return;
        }else{
            Log.d(TAG, "scriptRunSub: "+pkgName );
        }
            

        String tp= MyFile.readFileToString(HookShare.PATH_SCRIPT_RUNNING);
        if (Integer.parseInt(tp)!=1){
            Log.d(TAG, "scriptRunSub: script not running,beginning..");
            for (int i=0;i<3;i++) {
                boolean boolSuc=false;

                try {

                    Intent intentMain = new Intent(context,MainActivity.class);

                    intentMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    //intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intentMain.putExtra(HookShare.mainActivityExtra,true );
                    //MainActivity.volumeChangeObserver.unregisterReceiver();
                    context.startActivity( intentMain );

                    Log.d(TAG, "run: stop-app"+i);
                    Ut.stopAppByCmd(pkgName);
                    Ut.stopAppByCmd("com.tunnelworkshop.postern");
                    Ut.stopAppByCmd(HookShare.packageSurboard);
                    Log.d(TAG, "run: stop-app2");

   /*                 Intent actIntent = new Intent(context.getApplicationContext(), MainActivity.class);
                    //启动xposedDemo
                    actIntent.setAction("android.intent.action.MAIN");
                    actIntent.addCategory("android.intent.category.LAUNCHER");
                    //actIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(actIntent);
                    //Thread.sleep(1000);;*/
                    Log.d(TAG, "scriptRunSub: 启动app"+pkgName );
                    Ut.restartApp ( context,pkgName );
                    Toast.makeText(context,"等待脚本加载10秒",Toast.LENGTH_LONG);
                    Thread.sleep(8000 );

                    for (int j=0;j<4;j++){

                        if ( !getJsonWatch() ) {
                            return;
                        }
                        ScriptControl.setVolDown();
                        Log.d(TAG, "run: set vol down"+j);
                        Thread.sleep(7000 );

                        tp = MyFile.readFileToString(HookShare.PATH_SCRIPT_RUNNING);
                        if (Integer.parseInt(tp) == 1) {

                            boolSuc=true;
                            Toast.makeText(context, "启动成功", Toast.LENGTH_SHORT );
                            Log.d(TAG, "run: 启动成功");
                            break;
                     /*       //再次确认启动成功
                            MyFile.fileWriterTxt( HookShare.PATH_SCRIPT_RUNNING,"0" );
                            Thread.sleep(10000);
                            tp = MyFile.readFileToString( HookShare.PATH_SCRIPT_RUNNING );

                            if (Integer.parseInt(tp) == 1) {
                                Log.d(TAG, "run: 再次确认启动成功");

                                break;
                            }else {
                                Log.d(TAG, "run: 再次确认启动失败");
                                boolSuc=false;
                                break;
                            }*/

                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (boolSuc)
                    break;
                if (!getJsonWatch()) {
                    Log.d(TAG, "run: not need watch was set");
                    break;
                }
            }

        }else {
            Log.d(TAG, "run: 脚本已启动无需启动");
        }
        //
        //Looper.loop ();
    }

    @Override
    public void onDestroy() {

        Log.d(TAG, "run: awake-settingBefore"+watchRun );
        if (watchRun){
            Log.d(TAG, "run: awake-setting1");
            triggerAtTime = SystemClock.elapsedRealtime()+ONE_Miniute;
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime, pIntent);
        }
        super.onDestroy();

    }


}
