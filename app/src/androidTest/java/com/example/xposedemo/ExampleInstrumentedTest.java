package com.example.xposedemo;

import android.util.Log;

import androidx.test.runner.AndroidJUnit4;

import com.example.xposedemo.Hook.PackagesHook;
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

    @Test
    public void useAppContext() {

        String str="2022-08-19 23:28:51.938 10513-10732/? D/java.lang.Class: runVerify: before=\n" +
                "    <!DOCTYPE html>\n" +
                "    <html xmlns=\"http://www.w3.org/1999/html\">\n" +
                "    <head>\n" +
                "      <meta charset=\"utf-8\">\n" +
                "      <title></title>\n" +
                "      <meta http-equiv=\"X-UA-Compatible\" content=\"IE=Edge\">\n" +
                "      <meta name=\"viewport\"\n" +
                "            content=\"width=device-width, initial-scale=1,maximum-scale=1,minimum-scale=1, user-scalable=no, target-densitydpi=medium-dpi\">\n" +
                "      <link rel=\"stylesheet\" href=\"https://www.line-website.com/sec-v3-recaptcha/service.recaptcha.486a67e88e.css\">\n" +
                "    \n" +
                "      <link rel=\"icon\" href=\"https://scdn.line-apps.com/n/line_lp/img/line_favicon.ico\" type=\"image/x-icon\"/>\n" +
                "      <link rel=\"shortcut icon\" href=\"https://scdn.line-apps.com/n/line_lp/img/line_favicon.ico\" type=\"image/x-icon\"/>\n" +
                "    </head>\n" +
                "    <body>\n" +
                "    <div class=\"grecaptcha\">\n" +
                "      <div>\n" +
                "        <div id=\"recaptcha_normal\"></div>\n" +
                "      </div>\n" +
                "      <textarea id=\"g-recaptcha-response-1\" name=\"g-recaptcha-response\" class=\"g-recaptcha-response\"\n" +
                "                style=\"width: 250px; height: 40px; border: 1px solid #c1c1c1; margin: 10px 25px; padding: 0px; resize: none;  display: none; \"></textarea>\n" +
                "    </div>\n" +
                "    <div class=\"grecaptcha-info\"></div>\n" +
                "    <div id=\"data\"\n" +
                "         data-cookie-name=\"lsct_acct_init\"\n" +
                "         data-cookie-value=\"d4ef5979-f1ea-4bab-8a4f-e918cc88462a\"\n" +
                "         data-cookie-path=\"/sec\">\n" +
                "    </div>\n" +
                "    \n" +
                "    <script>\n" +
                "      var OPTIONS = {\n" +
                "        pageType: 'main',\n" +
                "        siteKey: '6Lfo_XYUAAAAAFQbdsuk6tETqnpKIg5gNxJy4xM0',\n" +
                "        labels: {\n" +
                "          popup_message_fallback: \"Verification failed.\\nPlease try again later.\"\n" +
                "        }\n" +
                "      };\n" +
                "    </script>\n" +
                "    <script src=\"https://d.line-scdn.net/n/line_setting/js/lc.line.setting.recaptcha.main_1586230455.js\"></script>\n" +
                "    <script src=\"https://www.google.com/recaptcha/api.js?onload=initRecaptcha&render=explicit\" async defer></script>\n" +
                "    </body>\n" +
                "    </html>\n";

        Pattern p=Pattern.compile( "siteKey: '([\\w]{30,100})'" );
        Matcher m=p.matcher(str);
        Boolean isFind = m.find();

        if(isFind){
            String str2 = m.group(1);
            Log.d(TAG, "useAppContext: count="+m.groupCount() );
            Log.d(TAG, "useAppContext: "+str2 );
        }

        if (  m.matches() ){
            MatchResult matchResult= m.toMatchResult();
            Log.d(TAG, "useAppContext: result="+matchResult.toString()  );
        }

        Log.d(TAG, "useAppContext: ="+m.matches() );
       // Ut.crFolderEx( "/sdcard/nk/aa/bb/cc");
        System.out.println(m.matches() + "---");

    }


}
