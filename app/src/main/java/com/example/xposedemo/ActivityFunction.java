package com.example.xposedemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.xposedemo.Hook.HookShare;
import com.example.xposedemo.utils.Ut;

public class ActivityFunction extends AppCompatActivity {
    private final String TAG = this.getClass().getName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ( getIntent()
                .getBooleanExtra( HookShare.mainActivityExtra,false ) ){
            getIntent().putExtra(HookShare.mainActivityExtra,false);
            Ut.restartApp( getApplicationContext(), "com.apple" );
            Log.d(TAG,"启动app，退出");
            return;
        }

    }
}
