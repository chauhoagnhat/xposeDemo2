package com.example.xposedemo.functionModule;

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;

import com.example.xposedemo.utils.MyFile;
import com.example.xposedemo.utils.MyUi;

public class ScriptControl {

    public static void setVolDown(){
      MyFile.execCmdsforResult( new String[]{ "sendevent /dev/input/event2 1 114 1","sendevent /dev/input/event2 0 0 0","sendevent /dev/input/event2 1 114 0",
                "sendevent /dev/input/event2 0 0 0" } );
    }



}
