package com.example.xposedemo.myService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.xposedemo.Hook.HookShare;
import com.example.xposedemo.MainActivity;
import com.example.xposedemo.functionModule.ScriptControl;
import com.example.xposedemo.myBroadcast.AlarmReceive;
import com.example.xposedemo.utils.MyFile;
import com.example.xposedemo.utils.MyUi;
import com.example.xposedemo.utils.Ut;

import org.json.JSONObject;

public class AlarmService extends Service {

    private static final int ONE_Miniute=60*1000;
    private static final int PENDING_REQUEST=0;
    private static final String TAG = "AlarmService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
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

        final Context context=this.getApplication();
        String uiJson=MyFile.readFileToString( HookShare.PATH_UI_SETTING );
        //if (uiJson!="")


        //通过AlarmManager定时启动广播
        AlarmManager alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
        long triggerAtTime= SystemClock.elapsedRealtime()+ONE_Miniute;//从开机到现在的毫秒书（手机睡眠(sleep)的时间也包括在内
        Intent i=new Intent(this, AlarmReceive.class);
        PendingIntent pIntent= PendingIntent.getBroadcast(this,PENDING_REQUEST,i,PENDING_REQUEST);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pIntent);
        return super.onStartCommand(intent, flags, startId);


    }

    public void  scriptRun(final Context context ){
        Toast.makeText( context,"检测脚本是否运行",Toast.LENGTH_LONG );
        //这里模拟后台操作
        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized ( this ){
                    Log.d(TAG, "run: loop");
                    MyFile.fileWriterTxt( HookShare.PATH_SCRIPT_RUNNING,"0" );
                    try {
                        Thread.sleep(20*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String tp= MyFile.readFileToString(HookShare.PATH_SCRIPT_RUNNING);
                    if (tp!="1"){
                        Ut.startApplicationWithPackageName( "com.li",context );
                    }

                }

            }
        }).start();

    }

}
