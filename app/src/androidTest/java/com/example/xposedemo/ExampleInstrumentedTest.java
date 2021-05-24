package com.example.xposedemo;

import android.util.Log;

import androidx.test.runner.AndroidJUnit4;

import com.example.xposedemo.Hook.PackagesHook;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private static final String TAG ="ExampleInstrumentedTest" ;

    @Test
    public void useAppContext() {
        String time=""+ PackagesHook.rnd_time( 60*60*24,60*60*24*7  );
        Log.d(TAG, "useAppContext: " +time );
    }
}
