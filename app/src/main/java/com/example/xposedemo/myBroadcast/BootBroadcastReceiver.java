package com.example.xposedemo.myBroadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.xposedemo.Hook.HookShare;
import com.example.xposedemo.MainActivity;
import com.example.xposedemo.functionModule.ScriptControl;
import com.example.xposedemo.myService.AlarmService;
import com.example.xposedemo.myService.BootService;
import com.example.xposedemo.utils.MyFile;
import com.example.xposedemo.utils.Ut;

public class BootBroadcastReceiver extends BroadcastReceiver {

//    版权声明：本文为CSDN博主「Rick-Lu」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//    原文链接：https://blog.csdn.net/u010947098/article/details/44862539
//
    public final static String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    private static final String TAG ="BootBroadcastReceiver" ;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(ACTION_BOOT_COMPLETED)){

            // 后边的XXX.class就是要启动的服务

            SharedPreferences sharedPreferences=context.getSharedPreferences(
                    HookShare.BootBroadcastReceiver ,Context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putInt( HookShare.BootBroadcastReceiverState,
                    HookShare.BootBroadcastReceiverBooted );
            editor.commit();
            Intent actIntent = new Intent(context.getApplicationContext(), MainActivity.class);
            //启动xposedDemo
            actIntent.setAction("android.intent.action.MAIN");
            actIntent.addCategory("android.intent.category.LAUNCHER");
            actIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(actIntent);
            Log.d(TAG, "onReceive: 接受到开机广播" );
            Log.v(TAG, "开机自动服务自动启动.....");
            // 启动应用，参数为需要自动启动的应用的包名
 /*           Intent serIntent= new Intent(context,  AlarmService.class);
            serIntent.addFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
            context.startService(serIntent);
            Log.v(TAG, "开机程序自动启动.....");*/

        }


    }




}
