package com.example.xposedemo.Hook;


import android.app.Application;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.util.Log;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.xposedemo.utils.Okhttp;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class webViewHook {

    private static final String TAG = webViewHook.class.getName() ;
    private String globalPageUrl="";
    public String pageUrlResp;
    public String envPageUrl;
    public String envStringPageUrlResp;
    WebResourceResponse webResourceResponse;
    public WebView webView;
    private WebResourceRequest envWebRequest;

    public webViewHook( XC_LoadPackage.LoadPackageParam sharePkgParam  ) {
        runHook(sharePkgParam);
    }

    public void runHook(  XC_LoadPackage.LoadPackageParam lpp  ){
        ApplicationInfo appInfo=lpp.appInfo;
        String string=appInfo.toString();
        Log.d(TAG, "runHook: pkg========"+string );

        webViewHookOnPageGetResp( lpp );

        XposedHelpers.findAndHookMethod(Application.class, "attach"
                , Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        //super.afterHookedMethod(param);
                        ClassLoader loader= ( (Context) param.args[0]).getClassLoader();
                        Class<?> class1=null;

                        try {
//                            File parent_path= Environment.getExternalStorageDirectory();
//                            File dir= new File( parent_path.getAbsoluteFile(),"Download" );
//                            Log.d(TAG, "afterHookedMethod: dir="+dir );
//                            File file=new File(dir,"apiKey.conf");
//                            byte[] byteArray2;
//
//                            try {
//                                byteArray2 = IOUtils.toByteArray(new FileInputStream(file));
//                                String string = new String(byteArray2);
//                                String apikey = string;
//                                Toast.makeText((Context) param.args[0], "apiKey->" + apikey, Toast.LENGTH_SHORT).show();
//
//                                Log.d(TAG, "afterHookedMethod: apiKey->"+apikey );
//                            } catch (IOException e) {
//                                Log.d(TAG, "afterHookedMethod: IOException="+e.toString() );
//                                e.printStackTrace();
//                            }
                           // new WebViewClient()
                            class1=loader.loadClass( "android.webkit.WebViewClient" );
                        } catch (ClassNotFoundException e) {
                            Log.d( TAG,"ClassNotFoundException "+e.toString() );
                            e.printStackTrace();
                        }

                        if (class1!=null){
                            XposedHelpers.findAndHookMethod(class1, "shouldInterceptRequest",
                                    WebView.class, String.class, new XC_MethodHook() {
                                        @Override
                                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                            super.afterHookedMethod(param);
                                            String url=param.args[1].toString();
                                            Log.d(TAG, "String url= "+url );
                                            if ( url.contains( "//w.line.me/sec/v3/recaptcha" ) ){
                                                Log.d(TAG, "WebView String suc..");
                                                if ( webResourceResponse!=null );
                                                param.setResult( webResourceResponse );
                                                super.afterHookedMethod(param);
                                            };
                                        }
                                    });

                            XposedHelpers.findAndHookMethod(class1, "shouldInterceptRequest"
                                    , WebView.class, WebResourceRequest.class, new XC_MethodHook() {
                                       //WebResourceRequest
                                        @Override
                                        protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                                            //super.afterHookedMethod(param);

                                            if (param.args != null) {

                                                WebResourceRequest webResourceRequest=(WebResourceRequest) param.args[1];
                                                Log.d(TAG, "shouldInterceptRequest: request="+webResourceRequest.getUrl().toString() );
                                                Log.d(TAG, "shouldInterceptRequest: request="+webResourceRequest.getUrl().toString() );
                                                String url=webResourceRequest.getUrl().toString();

                                                Log.d(TAG, "request url="+url);
                                                Log.d(TAG, "request url head="+webResourceRequest.getRequestHeaders() );
                                                Log.d(TAG, "request url ==============================");
//                                                webView=(WebView) param.args[0];
//                                                webView.post(new Runnable() {
//                                                    @Override
//                                                    public void run() {
//                                                        String webViewUrl=webView.getUrl();
//                                                        Log.d(TAG, "webView url="+webViewUrl );
//                                                    }
//                                                });


                                                if ( url.contains( "//w.line.me/sec/v3/recaptcha" ) ){
                                                    envWebRequest=webResourceRequest;
                                                  //  envStringPageUrlResp=getPageUrlResP( webResourceRequest );
                                                 //   Log.d(TAG, "envStringPageUrlResp: "+envStringPageUrlResp );
                                                    //webView.destroy();
                                                }

                                                //if ( url.contains( "//w.line.me/sec/v3/recaptcha" ) ){
                                                    //
                                                    if ( url.contains( "api2/bframe" ) ){
                                                    Log.d(TAG, "afterHookedMethod: " );

                                                    webView=(WebView) param.args[0];
                                                    webView.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            WebSettings settings = webView.getSettings();
                                                            settings.setJavaScriptEnabled(true);
                                                            settings.setJavaScriptCanOpenWindowsAutomatically(true);

                                                            try {
                                                                final URL url1=new URL( webView.getUrl() );
                                                                //webView.loadUrl("javascript:window.handler.show(document.body.innerHTML);");

                                                        /*        new Thread(new Runnable() {
                                                                    @Override
                                                                    public void run() {
                                                                        try {
                                                                            Log.d(TAG, "run: ="+url1 );
                                                                            byte[] byteArr= IOUtils.toByteArray( url1.openStream());
                                                                            String string=new String(byteArr);
                                                                            Log.d(TAG, "run: openStream="+string );
                                                                        } catch (IOException e) {
                                                                            Log.d(TAG, "run: IOException");
                                                                            e.printStackTrace();
                                                                        }
                                                                    }
                                                                }).start() ;*/
                                                            } catch (MalformedURLException e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                    });



                                            /*        webResourceResponse=runVerify(envStringPageUrlResp);
                                                    param.setResult(webResourceResponse);
                                                    final String token=get_google_token_result(envWebRequest);

                                                        Log.d(TAG, "afterHookedMethod: run js.");
                                                        webView.post(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Log.d(TAG, "run: run js.");
                                                                webView.loadUrl( "javascript:testCall(" +
                                                                        token+
                                                                        ")" );
                                                            }
                                                        });*/

                                      /*              new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                           WebResourceResponse webResourceResponse= runVerify( webResourceRequest );
                                                           param.setResult( webResourceResponse );
                                                        }
                                                    }).start();*/
                                                  //  webResourceResponse= runVerify( webResourceRequest );

                                                        //tmeCancel
                                     /*               webResourceResponse= runVerify( envWebRequest );
                                                    param.setResult( webResourceResponse );

                                                    Log.d(TAG, "afterHookedMethod: run js.");
                                                    webView.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Log.d(TAG, "run: run js.");
                                                            webView.loadUrl( "javascript:testCall()" );
                                                        }
                                                    });*/


                                                }

                                                    //tmpCancel
                                  /*              if (url.contains("api2/bframe") ){
                                                    Log.d(TAG, "afterHookedMethod: run js.");

                                                    webView.post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Log.d(TAG, "run: run js.");
                                                            webView.loadUrl( "javascript:testCall()" );
                                                        }
                                                    });
                                                }
*/


                                              // Log.d(TAG, "request url ============================== ");


                                            }

                                        }

                                    });

                        }else {
                            Log.d(TAG, "afterHookedMethod: android.webkit.WebViewClient not found" );
                        }

                    }
                });





//        String replace = string.replace(oldStr, "测试提交啊");
//        String jsText = "mytoken='"+ globaToken+ "';"+ "function testCall(){alert('success');document.getElementById(\"g-recaptcha-response\").innerHTML=mytoken;}";
//        replace = replace.replace("", jsText + "\n");
//        WebResourceResponse webResourceResponse = new WebResourceResponse("text/html","utf-8", new ByteArrayInputStream(replace.getBytes()));
//        param.setResult(webResourceResponse);
//
////        作者：淡v漠
////        链接：https://www.jianshu.com/p/d564c28e8967
////        来源：简书
////        著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
    }

    // webview的hook
    private void webViewHookOnPageGetResp(XC_LoadPackage.LoadPackageParam loadPackageParam) {
        XposedHelpers.findAndHookMethod("android.webkit.WebViewClient",// app的WebViewClient  的具体实现类
                loadPackageParam.classLoader,
                "onPageFinished",
                WebView.class,
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        Log.e(TAG, "======================webview  beforeHookedMethod");
                        super.beforeHookedMethod(param);
                    }

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        Log.e(TAG, "======================webview  afterHookedMethod");
                        String url = null;
                        if (param.args.length == 2) {
                            url = (String) param.args[1];
                            Log.e(TAG, " =====url 是 " + url);
                        }
                        // url 过滤
                        if (url == null || !url.startsWith("https://w.line.me/")) {
                            return;
                        }
                        WebView webView = (WebView) param.args[0];
                        webView.evaluateJavascript("document.getElementsByTagName('html')[0].innerHTML;",
                                new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String value) {
                                        Log.e(TAG, " js注入获取到的网页源码是 " + value);

                                    }
                                });
                        super.afterHookedMethod(param);
                    }
                }
        );
    }


    public String getPageUrlResP(WebResourceRequest webResourceRequest){

        Map<String,String> head=webResourceRequest.getRequestHeaders();
        String url=webResourceRequest.getUrl().toString();
        Log.d(TAG, "getPageUrlResP: run..");
//        String siteKey="6Lfo_XYUAAAAAFQbdsuk6tETqnpKIg5gNxJy4xM0";
//        String platformKey="e30901b2fe14275b7130ceede4d4a0c3";
//        String pageUrl="https://w.line.me/sec/v3/recaptcha";
//        String platFormRequestId=get_google_token( platformKey,siteKey,pageUrl );
//        String finalResult=getGoogleResult( platformKey,platFormRequestId );
//        Log.d(TAG, "platFormRequestId: "+platFormRequestId
//                +" , finalResult="+finalResult );
        String ret= Okhttp.get( url, head);

        if (ret!=null){
            envStringPageUrlResp=ret;
            return ret;
        }
        return null;


    }

    public void verifyPost( String token,String ck ){
        Map<String,String> map=new HashMap<>();
        //	"User-Agent":       "Mozilla/5.0 (Linux; Android 10; LM-G850 Build/QKQ1.200216.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/98.0.4758.101 Mobile Safari/537.36 Line/12.0.1",
        //		"Content-Type":     "application/json;charset=UTF-8",
        //		"X-Requested-With": "jp.naver.line.android",
        //		"Referer":          "https://w.line.me/sec/v3/recaptcha",
        //		"Cookie":           "lsct_acct_init=" + cookie,
       // User-Agent=Mozilla/5.0 (Linux; Android 10; BE2011 Build/QKQ1.200719.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/104.0.5112.69 Mobile Safari/537.36 Line/12.12.0

        map.put( "User-Agent","Mozilla/5.0 (Linux; Android 10; BE2011 Build/QKQ1.200719.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/104.0.5112.69 Mobile Safari/537.36 Line/12.12.0" );
        map.put( "X-Requested-With", "jp.naver.line.android" );
        map.put("Referer","https://w.line.me/sec/v3/recaptcha"  );
        map.put("Content-Type","application/json;charset=UTF-8" );
        map.put( "Cookie","lsct_acct_init="+ ck );

        Log.d(TAG, "verifyPost: ck="+ck
        +",token="+token );

        String jsonObject="{\"verifier\":\""+token+"\"}";

        String ret=Okhttp.post( "https://w.line.me/sec/v3/recaptcha/result/verify"
        ,jsonObject,map);
        
        if (ret!=null){
            Log.d(TAG, "verifyPost: ret="+ret );
            
        }else{
            Log.d(TAG, "verifyPost: fail");
        }

    }

    public String get_google_token_result( WebResourceRequest webResourceRequest ){

        Map<String,String> head=webResourceRequest.getRequestHeaders();
        Log.d(TAG, "get_google_token_result: head="+head.toString() );

        String url=webResourceRequest.getUrl().toString();
        //6Lfo_XYUAAAAAFQbdsuk6tETqnpKIg5gNxJy4xM0
        String siteKey="6Lfo_XYUAAAAAFQbdsuk6tETqnpKIg5gNxJy4xM0";
        String platformKey="e30901b2fe14275b7130ceede4d4a0c3";
        String pageUrl="https://w.line.me/sec/v3/recaptcha";

        String platFormRequestId=get_google_token( platformKey,siteKey,pageUrl );
        String finalResult=getGoogleResult( platformKey,platFormRequestId );

        return  finalResult;

    }

    public  WebResourceResponse runVerify( WebResourceRequest webResourceRequest ){

        Map<String,String> head=webResourceRequest.getRequestHeaders();
        String url=webResourceRequest.getUrl().toString();

        Log.d(TAG, "runVerify: head="+head.toString() );

//        String siteKey="6Lfo_XYUAAAAAFQbdsuk6tETqnpKIg5gNxJy4xM0";
//        String platformKey="e30901b2fe14275b7130ceede4d4a0c3";
//        String pageUrl="https://w.line.me/sec/v3/recaptcha";
//        String platFormRequestId=get_google_token( platformKey,siteKey,pageUrl );
//        String finalResult=getGoogleResult( platformKey,platFormRequestId );
//        Log.d(TAG, "platFormRequestId: "+platFormRequestId
//                +" , finalResult="+finalResult );

        String finalResult=get_google_token_result( webResourceRequest );

        String ret= Okhttp.get( url, head);
        Log.d(TAG, "runVerify: finalResult="+finalResult );
        //String replace = string.replace(oldStr, "测试提交啊");
        String jsText = "<script type=\"text/javascript\">mytoken='"+ finalResult+
                "';"
                + "function testCall(){alert('success');" +
                "document.getElementById(\"g-recaptcha-response\").innerHTML=mytoken;" +
                "}" +
                "</script>";
//        replace = replace.replace("", jsText + "\n");
//        WebResourceResponse webResourceResponse = new WebResourceResponse("text/html","utf-8", new ByteArrayInputStream(replace.getBytes()));
//        param.setResult(webResourceResponse);

        if (ret!=null){

            Log.d(TAG, "runVerify: sucess..");
            Log.d(TAG, "runVerify: before="+ret );
            ret=ret.replace( "</textarea>",finalResult+"</textarea>" );
            ret=ret.replace( "display: none;","" );
            ret=ret.replace("</body>",jsText+"\n</body>");
            Log.d(TAG, "replace ret= "+ret  );

            webResourceResponse
                    =new WebResourceResponse( "text/html"
                    ,"utf-8"
                    ,new ByteArrayInputStream(ret.getBytes())  );
                return webResourceResponse;
        }
        return null;
    }


    public  WebResourceResponse runVerify( String resp )
    {


        Log.d(TAG, "runVerify: string run");
        //String replace = string.replace(oldStr, "测试提交啊");
        String jsText = "<script type=\"text/javascript\">mytoken='"+
                "';"
                + "function testCall(token){alert('success');" +
                "document.getElementById(\"g-recaptcha-response\").innerHTML=token;" +
                "}" +
                "</script>";


        if (resp!=null){

            Log.d(TAG, "runVerify: sucess..");
            Log.d(TAG, "runVerify: before="+resp );
            //resp=resp.replace( "</textarea>",finalResult+"</textarea>" );
            resp=resp.replace( "display: none;","" );
            resp=resp.replace("</body>",jsText+"\n</body>");
            Log.d(TAG, "replace ret= "+resp  );

            webResourceResponse
                    =new WebResourceResponse( "text/html"
                    ,"utf-8"
                    ,new ByteArrayInputStream(resp.getBytes())  );
            return webResourceResponse;
        }else {
            Log.d(TAG, "runVerify: null");
        }
        return null;
    }


//    public get_cookie(){
//
//    }

    /**
     *
     * @param key
     * @param siteKey
     * @param pageUrl
     * @return
     */
    public String get_google_token( String key,String siteKey,String pageUrl  ){
        String url="http://2captcha.com/in.php?"
        +"key="+key
        +"&method=userrecaptcha"
                +"&googlekey="+siteKey
                +"&pageurl="+pageUrl
                +"&json=1";
        //{"status":1,"request":"66435934048"}

     while (true){

        String ret=Okhttp.get( url );
        if (ret!=null){
            Log.d(TAG, "get_google_token: ret="+ret );
            JSONObject jsonObject= JSON.parseObject( ret );
            String v=jsonObject.getString( "request" );
            if ( !v.equals("")&&
             jsonObject.getIntValue ( "status" )==1 ){
                return v;
            }else{
                Log.d(TAG, "get_google_token: err="+ret );
            }
        }else{
            Log.d(TAG, "get_google_token: fail");
        }

         try {
             Thread.sleep(5000);
         } catch (InterruptedException e) {
             e.printStackTrace();
         }

         }
    }

    //http://2captcha.com/res.php?
    // key=1abc234de56fab7c89012d34e56fa7b8&action=get&id=2122988149

    /**
     *
     * @param key
     * @param id
     * @return
     */
    public String getGoogleResult(String key,String id ){
        String url="http://2captcha.com/res.php?"
                +"key="+key
                +"&action=get"
                +"&id="+id
                +"&json=1";
        while (true){

            String ret=Okhttp.get( url );
            if (ret!=null){

                JSONObject jobj=JSON.parseObject( ret );
                //"status" )==1
                if ( jobj.getIntValue( "status" )==1 ){
                    return jobj.getString("request");
                }else{
                    Log.d(TAG, "getGoogleResult: err="+ret+",id="+id
                    );
                }

            }else{
                Log.d(TAG, "getGoogleResult: fail");
            }

            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }


    public String convertToString(InputStream is){

        BufferedReader bReader = new BufferedReader(new InputStreamReader(is));
        StringBuffer buffer = new StringBuffer();
        String line = null;
        try {
            while((line = bReader.readLine())!=null){
                buffer.append(line);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            try {
                bReader.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return buffer.toString();
    }

}
