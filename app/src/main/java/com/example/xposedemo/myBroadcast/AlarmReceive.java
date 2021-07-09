package com.example.xposedemo.myBroadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.example.xposedemo.myService.AlarmService;

public class AlarmReceive extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent2=new Intent( context, AlarmService.class );
        context.startService( intent2 );
    }

}
