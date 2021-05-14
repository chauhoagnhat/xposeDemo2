package com.example.xposedemo;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;

import com.example.xposedemo.Hook.HookShare;
import com.example.xposedemo.Hook.Phone;
import com.example.xposedemo.fake.FackBase;
import com.example.xposedemo.utils.PhoneRndTools;
import com.example.xposedemo.utils.Ut;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

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

        // Context of the app under test.
        // Context appContext = InstrumentationRegistry.getTargetContext();
        // assertEquals("com.example.xposedemo", appContext.getPackageName());
        // Log.d(TAG, "useAppContext: build ID-"+ Build.ID  );
       // HookShare.WriteBean2Json( FackBase.getInstance() );
        // Log.d(TAG, "useAppContext: read="+Ut.readFileToString( HookShare.pathNewDeviceOrder )  );

    }
}
