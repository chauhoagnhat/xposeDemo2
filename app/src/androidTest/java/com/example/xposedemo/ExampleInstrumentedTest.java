package com.example.xposedemo;

import android.util.Log;

import androidx.test.runner.AndroidJUnit4;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.xposedemo.Hook.PackagesHook;
import com.example.xposedemo.utils.Okhttp;
import com.example.xposedemo.utils.Ut;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private static final String TAG ="ExampleInstrumentedTest" ;
    private String envHttpRui="http://54.241.117.38:10000/config?key=LineWashTaskStart";

    @Test
    public void useAppContext() {
    String str="09AMjm62UECznAfNQj_U7yxpTPmR_Xif2p13uljT5ap9EYOmWpklgBCjRE_RCTtoDuyQt1U6LJq3ZYBWu8gsWnoju5DzYVlFOHYLs\n" +
            "09AMjm62UECznAfNQj_U7yxpTPmR_Xif2p13uljT5ap9EYOmWpklgBCjRE_RCTtoDuyQt1U6LJq3ZYBWu8gsWnoju5DzYVlFOHYLs\n" +
            "09AMjm62UECznAfNQj_U7yxpTPmR_Xif2p13uljT5ap9EYOmWpklgBCjRE_RCTtoDuyQt1U6LJq3ZYBWu8gsWnoju5DzYVlFOHYLs\n" +
            "09AMjm62UECznAfNQj_U7yxpTPmR_Xif2p13uljT5ap9EYOmWpklgBCjRE_RCTtoDuyQt1U6LJq3ZYBWu8gsWnoju5DzYVlFOHYLs";

    String[] arr=str.split("\\n");
        Log.d(TAG, "useAppContext: "+arr.length );
        Log.d(TAG, "useAppContext: "+arr[0] );

//
//        Okhttp.setTimeout( 200 );
//        getTokenFromRui();

    }

    public String getTokenFromRui() {
        String ret = Okhttp.get(envHttpRui);

        for (int i = 0; i < 4; i++) {
            if (ret != null) {
                JSONObject jsonObject = JSON.parseObject(ret);
                if (jsonObject.getIntValue("code") == 200) {
                    Log.d(TAG, "getTokenFromRui: suc..");
                    jsonObject = jsonObject.getJSONObject("data");
                    jsonObject = jsonObject.getJSONObject("data");
                    Log.d(TAG, "getTokenFromRui: ="+jsonObject.getString("value_string") );
                    return jsonObject.getString("value_string");
                } else {
                   // toast("get token from rui err-" + ret);
                    Log.d(TAG, "getTokenFromRui:  err" + ret);
                }
            } else {
                //toast("get token from rui fail");

                Log.d(TAG, "getTokenFromRui: fail "+ret);
            }
        }

        return null;

    }


}
