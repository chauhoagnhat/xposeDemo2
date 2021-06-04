package com.example.xposedemo;

import android.util.Log;

import androidx.test.runner.AndroidJUnit4;

import com.example.xposedemo.Hook.PackagesHook;
import com.example.xposedemo.utils.Ut;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

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

        Ut.crFolderEx( "/sdcard/nk/aa/bb/cc");

    }
}
