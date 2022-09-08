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
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.xposedemo.utils.Okhttp;
import com.example.xposedemo.utils.Ut;

import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class webViewHook {

    private static final String TAG = webViewHook.class.getName();
    private String globalPageUrl = "";
    public String pageUrlResp;
    public String envPageUrl;
    public String envStringPageUrlResp;
    WebResourceResponse webResourceResponse;
    public WebView webView;
    private WebResourceRequest envWebRequest;
    public static boolean boolVerfy = false;
    public String envCookie;
    private String envToken;
    public Map<String, String> envHead;
    public String envHeadStr;
    private Context envContext;
    public String envHttpRui = "http://www.comioon.com/nk/token.txt" ;
    public String envHttpRuiToken2 = "http://www.comioon.com/nk/token2.txt";
    final String envPathStateReCaptcha = "/data/local/tmp/nk/ret.txt";
    private String siteKey;
    private Thread envThread;
    private String envPlatformKey;
    private String envToken2;

    public webViewHook(XC_LoadPackage.LoadPackageParam sharePkgParam) {
        runHook(sharePkgParam);
    }

    public void setEnvTokenNull() {
        envToken = null;
    }

    public void runHook(XC_LoadPackage.LoadPackageParam lpp) {

        ApplicationInfo appInfo = lpp.appInfo;
        String string = appInfo.toString();
        Log.d(TAG, "runHook: pkg========" + string);
        onPageLoad(lpp);

        XposedHelpers.findAndHookMethod(Application.class, "attach"
                , Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                        //super.afterHookedMethod(param);
                        ClassLoader loader = ((Context) param.args[0]).getClassLoader();
                        envContext = ((Context) param.args[0]);
                        Class<?> class1 = null;

                        try {
                            class1 = loader.loadClass("android.webkit.WebViewClient");
                        } catch (ClassNotFoundException e) {
                            Log.d(TAG, "ClassNotFoundException " + e.toString());
                            e.printStackTrace();
                        }

                        if (class1 != null) {

/*                            if ( boolVerfy ){
                                Log.d(TAG, "secend run: ");
                                Log.d(TAG, "afterHookedMethod: " );
                                  runVerify( );
                                verifyPost( envToken,envCookie,webView );
                                return;
                            }*/

                            XposedHelpers.findAndHookMethod(class1, "shouldInterceptRequest"
                                    , WebView.class, WebResourceRequest.class, new XC_MethodHook() {
                                        //WebResourceRequest
                                        @Override
                                        protected void afterHookedMethod(final MethodHookParam param) throws Throwable {
                                            //super.afterHookedMethod(param);

                                            if (param.args != null) {

                                                envPlatformKey = getTokenFromRui();
                                                envToken2 = getTokenFromRui(envHttpRuiToken2);

                                                WebResourceRequest webResourceRequest = (WebResourceRequest) param.args[1];
                                                envWebRequest = webResourceRequest;

                                                envHead = webResourceRequest.getRequestHeaders();
                                                envHeadStr = envHead.toString();

                                                Log.d(TAG, "shouldInterceptRequest: request=" + webResourceRequest.getUrl().toString());
                                                Log.d(TAG, "shouldInterceptRequest: request=" + webResourceRequest.getUrl().toString());
                                                String url = webResourceRequest.getUrl().toString();

                                                Log.d(TAG, "request url=" + url);
                                                Log.d(TAG, "request url head=" + webResourceRequest.getRequestHeaders());
                                                Log.d(TAG, "request url ==============================");

                                                if (url.contains("//w.line.me/sec/v3/recaptcha")) {
                                                 /*   if (boolVerfy) {
                                                        //Ut.fileWriterTxt(envPathStateReCaptcha, "return");
                                                        return;
                                                    }

                                                    //获取网页
                                                    String urlPageResp = getPageUrlResP( webResourceRequest );
                                                    if (urlPageResp == null)
                                                        return;
                                                    //获取siteKey

                                                    siteKey = getSiteKeyByUrlResp( urlPageResp );
                                                    if (urlPageResp == null) {

                                                        Log.d(TAG, "afterHookedMethod: urlPageResp null");
                                                        Ut.fileWriterTxt(envPathStateReCaptcha, "urlPageResp null");
                                                        return;
                                                    }

                                                    if ( siteKey == null ) {
                                                        Log.d(TAG, "afterHookedMethod: siteKey null");
                                                        Ut.fileWriterTxt(envPathStateReCaptcha, "siteKey fail");
                                                        return;
                                                    }
                            //设置响应页面
                                                    webResourceResponse = setRespStrToWebRespByte(urlPageResp);
                                                    if (webResourceResponse != null) ;
                                                    param.setResult(webResourceResponse);

                                                    if (envThread != null) {
                                                        Log.d(TAG, "afterHookedMethod: runHook interrupt");
                                                        envThread.interrupt();
                                                        //envThread.join();
                                                    }
                                                    //threadRunAgain();
                                                    envThread = new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            String googleRet = get_google_token_result(envWebRequest, siteKey);
                                                            if (googleRet == null) {
                                                                Log.d(TAG, "afterHookedMethod: get_google_token_result null");
                                                                boolVerfy = false;

                                                                return;
                                                            }
                                                            envToken = googleRet;
                                                        }
                                                    });
                                                    envThread.start();*/
//
//                                                    String googleRet = get_google_token_result(envWebRequest, siteKey);
//                                                    if (googleRet==null){
//                                                        Log.d(TAG, "afterHookedMethod: get_google_token_result null");
//                                                        boolVerfy=false;
//                                                        return;
//                                                    }
//                                                    envToken=googleRet;


                                                }

                                                if (url.contains( "recaptcha/api2/userverify" )) {

                                                    String fakstr = fakeGoogleResp(envToken);
                                                    Log.d(TAG, "afterHookedMethod: fakestr=" +
                                                            fakstr);
                                                    WebResourceResponse webResp =
                                                            new WebResourceResponse("text/html"
                                                                    , "utf-8"
                                                                    , new ByteArrayInputStream(fakstr.getBytes()));
                                                    param.setResult(webResp);
                                                    //boolVerfy = true;
                                                }

                                                if (url.contains("api2/bframe")) {


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

                        } else {
                            Log.d(TAG, "afterHookedMethod: android.webkit.WebViewClient not found");
                        }

                    }
                });


//        String replace = string.replace(oldStr, "测试提交啊");
//        String jsText = "mytoken='"+ globaToken+ "';"+ "function testCall(){alert('success');document.getElementById(\"g-recaptcha-response\").innerHTML=mytoken;}";
//        replace = replace.replace("", jsText + "\n");
//        WebResourceResponse webResourceResponse = new WebResourceResponse("text/html","utf-8", new ByteArrayInputStream(replace.getBytes()));
//        param.setResult(webResourceResponse);
////        作者：淡v漠
////        链接：https://www.jianshu.com/p/d564c28e8967
////        来源：简书
////        著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
    }

    public void threadRunAgain() {

        envThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String googleRet = get_google_token_result(envWebRequest, siteKey);
                if (googleRet == null) {
                    Log.d(TAG, "afterHookedMethod: get_google_token_result null");
                    boolVerfy = false;
                    threadRunAgain();
                    //return;
                }
                envToken = googleRet;
            }
        });
        envThread.start();
    }

    // webview的hook
    private void onPageLoad(XC_LoadPackage.LoadPackageParam loadPackageParam) {
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

                        if (boolVerfy) {
                            //Ut.fileWriterTxt(envPathStateReCaptcha, "return");
                            Log.d(TAG, "boolVerfy: return");
                            return;
                        }
                        // url 过滤
                        if (url == null || !url.startsWith("https://w.line.me/sec/v3/recaptcha")) {
                            return;
                        }

                        WebView webView = (WebView) param.args[0];
                        webView.evaluateJavascript("document.getElementsByTagName('html')[0].innerHTML;",
                                new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String value) {
                                        Log.e(TAG, " js注入获取到的网页源码是 " + value);
                                        //获取网页

                                        String urlPageResp = value;
                                        siteKey = getSiteKeyByUrlResp(value);

                                        if (urlPageResp == null) {

                                            Log.d(TAG, "afterHookedMethod: urlPageResp null");
                                            Ut.fileWriterTxt(envPathStateReCaptcha, "urlPageResp null");
                                            return;
                                        }

                                        if (siteKey == null) {
                                            Log.d(TAG, "afterHookedMethod: siteKey null");
                                            Ut.fileWriterTxt(envPathStateReCaptcha, "siteKey fail");
                                            return;
                                        }

//                                                    webResourceResponse = runVerify(webResourceRequest);
//                                                    if (webResourceRequest==null)
//                                                        return;

                                        //设置响应页面
//                        webResourceResponse = setRespStrToWebRespByte(urlPageResp);
//                        if (webResourceResponse != null) ;
//                        param.setResult(webResourceResponse);

                                        if (envThread != null) {
                                            Log.d(TAG, "afterHookedMethod: runHook interrupt");
                                            envThread.interrupt();
                                            //envThread.join();
                                        }

                                       //threadRunAgain();
                                        envThread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                String googleRet = get_google_token_result(envWebRequest, siteKey);
                                                if (googleRet == null) {
                                                    Log.d(TAG, "afterHookedMethod: get_google_token_result null");
                                                    boolVerfy = false;

                                                    return;
                                                }
                                                envToken = googleRet;
                                            }
                                        });
                                        envThread.start();
//                                        String googleRet = get_google_token_result(envWebRequest, siteKey);
//                                        if (googleRet == null) {
//                                            Log.d(TAG, "afterHookedMethod: get_google_token_result null");
//                                            boolVerfy = false;
//
//                                            return;
//                                        }
//                                        envToken = googleRet;

                                    }
                                });
                        super.afterHookedMethod(param);

                    }
                }
        );
    }

    public String getTokenFromRui() {
        return getTokenFromRui(envHttpRui);
    }


    public String getTokenFromRui(String url) {
        String ret = Okhttp.get(url);

        for (int i = 0; i < 5; i++) {
            if (ret != null) {
//                Log.d(TAG, "getTokenFromRui: ret=" + ret);
//                JSONObject jsonObject = JSON.parseObject(ret);
//                if (jsonObject.getIntValue("code") == 200) {
//                    Log.d(TAG, "getTokenFromRui: suc..");
//                    jsonObject = jsonObject.getJSONObject("data");
//                    jsonObject = jsonObject.getJSONObject("data");
//                    return jsonObject.getString("value_string");
//                } else {
//                    Ut.fileWriterTxt(envPathStateReCaptcha, "token fail");
//                    toast("get token from rui err-" + ret);
//                    Log.d(TAG, "getTokenFromRui:  err" + ret);
//                }
                Log.d(TAG, "getTokenFromRui: ret="+ret );
                return ret;
            } else {
                toast("get token from rui fail");
                Log.d(TAG, "getTokenFromRui: fail");
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        return null;
    }


    public void toast(String str) {
        if (envContext != null) {
            try {

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getSiteKeyByUrlResp(String pageUrlResp) {
        List<String> ret = Ut.regexp("siteKey: '([\\w]{30,100})'", pageUrlResp);
        if (ret != null) {
            Log.d(TAG, "getSiteKeyByUrlResp: siteKey=" + ret.get(1));
            return ret.get(1);
        }
        return null;
    }

    public String getPageUrlResP(WebResourceRequest webResourceRequest) {

        Map<String, String> head = webResourceRequest.getRequestHeaders();
        envHeadStr = head.toString();
        Log.d(TAG, "getPageUrlResP: head=" + envHeadStr);
        String url = webResourceRequest.getUrl().toString();
        Log.d(TAG, "getPageUrlResP: run..");

//        String siteKey="6Lfo_XYUAAAAAFQbdsuk6tETqnpKIg5gNxJy4xM0";
//        String platformKey="e30901b2fe14275b7130ceede4d4a0c3";
//        String pageUrl="https://w.line.me/sec/v3/recaptcha";
//        String platFormRequestId=get_google_token( platformKey,siteKey,pageUrl );
//        String finalResult=getGoogleResult( platformKey,platFormRequestId );
//        Log.d(TAG, "platFormRequestId: "+platFormRequestId
//                +" , finalResult="+finalResult );

        for (int i = 0; i < 4; i++) {
            String ret = Okhttp.get(url, head);
            if (ret != null) {
                Log.d(TAG, "getPageUrlResP: pageUrl=" + ret);
                envStringPageUrlResp = ret;
                return ret;
            } else {
                Log.d(TAG, "getPageUrlResP: fail");
            }
        }

        return null;

    }

    public void verifyPost(String token, String ck, final WebView webView) {
        Map<String, String> map = new HashMap<>();
        //	"User-Agent":       "Mozilla/5.0 (Linux; Android 10; LM-G850 Build/QKQ1.200216.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/98.0.4758.101 Mobile Safari/537.36 Line/12.0.1",
        //		"Content-Type":     "application/json;charset=UTF-8",
        //		"X-Requested-With": "jp.naver.line.android",
        //		"Referer":          "https://w.line.me/sec/v3/recaptcha",
        //		"Cookie":           "lsct_acct_init=" + cookie,
        // User-Agent=Mozilla/5.0 (Linux; Android 10; BE2011 Build/QKQ1.200719.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/104.0.5112.69 Mobile Safari/537.36 Line/12.12.0

        map.put("User-Agent", "Mozilla/5.0 (Linux; Android 10; BE2011 Build/QKQ1.200719.002; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/104.0.5112.69 Mobile Safari/537.36 Line/12.12.0");
        map.put("X-Requested-With", "jp.naver.line.android");
        map.put("Referer", "https://w.line.me/sec/v3/recaptcha");
        map.put("Content-Type", "application/json;charset=UTF-8");
        map.put("Cookie", "lsct_acct_init=" + ck);

        Log.d(TAG, "verifyPost: token=" + token);
        Log.d(TAG, "verifyPost: ck=" + ck);

        Log.d(TAG, "verifyPost: ck=" + ck
                + ",token=" + token);

        String jsonObject = "{\"verifier\":\"" + token + "\"}";
        String ret = Okhttp.post("https://w.line.me/sec/v3/recaptcha/result/verify"
                , jsonObject, map);

        final WebView webView2 = webView;

        if (ret != null) {
//            webView2.post(new Runnable() {
//                @Override
//                public void run() {
//                    webView2.destroy();
//                }
//            });

            Log.d(TAG, "verifyPost: ret=" + ret);

        } else {
            Log.d(TAG, "verifyPost: fail");
        }

    }

    public String get_google_token_result(WebResourceRequest webResourceRequest, String siteKey) {

        Map<String, String> head = webResourceRequest.getRequestHeaders();
        Log.d(TAG, "get_google_token_result: head=" + head.toString());
        String url = webResourceRequest.getUrl().toString();
        //6Lfo_XYUAAAAAFQbdsuk6tETqnpKIg5gNxJy4xM0
        //String  siteKey="6Lfo_XYUAAAAAFQbdsuk6tETqnpKIg5gNxJy4xM0";

        String platformKey = "5a3a8695e7b06b84dda4de45af7b6ea8";
        if (envPlatformKey == null) {
          //  platformKey="e30901b2fe14275b7130ceede4d4a0c3";
            platformKey="5a3a8695e7b06b84dda4de45af7b6ea8";
        }else
            platformKey=envPlatformKey;


        if (platformKey == null) {
            Ut.fileWriterTxt(envPathStateReCaptcha, "key fail");
            return null;
        }

        String pageUrl = "https://w.line.me/sec/v3/recaptcha";
        String platFormRequestId = get_google_token(platformKey, siteKey, pageUrl);

        String finalResult = getGoogleResult(platformKey, platFormRequestId);
        Log.d(TAG, "get_google_token_result: token=\n" +
                finalResult);

        return finalResult;

    }

    public String get_google_token_result() {

        //6Lfo_XYUAAAAAFQbdsuk6tETqnpKIg5gNxJy4xM0
        String siteKey = "6Lfo_XYUAAAAAFQbdsuk6tETqnpKIg5gNxJy4xM0";
        String platformKey = "e30901b2fe14275b7130ceede4d4a0c3";
        String pageUrl = "https://w.line.me/sec/v3/recaptcha";

        String platFormRequestId = get_google_token(platformKey, siteKey, pageUrl);
        String finalResult = getGoogleResult(platformKey, platFormRequestId);
        Log.d(TAG, "get_google_token_result: token=\n" +
                finalResult);
        return finalResult;

    }

    public WebResourceResponse runVerify() {

//        String siteKey="6Lfo_XYUAAAAAFQbdsuk6tETqnpKIg5gNxJy4xM0";
//        String platformKey="e30901b2fe14275b7130ceede4d4a0c3";
//        String pageUrl="https://w.line.me/sec/v3/recaptcha";
//        String platFormRequestId=get_google_token( platformKey,siteKey,pageUrl );
//        String finalResult=getGoogleResult( platformKey,platFormRequestId );
//        Log.d(TAG, "platFormRequestId: "+platFormRequestId
//                +" , finalResult="+finalResult );

        String finalResult = get_google_token_result();
        envToken = finalResult;

        Log.d(TAG, "runVerifyNew: url,head" + envPageUrl
                + "\n" + envHead.toString());
        String ret = Okhttp.get(envPageUrl, envHead);
        Log.d(TAG, "runVerify: finalResult=" + finalResult);
        //String replace = string.replace(oldStr, "测试提交啊");
        String jsText = "<script type=\"text/javascript\">mytoken='" + finalResult +
                "';"
                + "function testCall(){" +
                "document.getElementById(\"g-recaptcha-response-1\").innerHTML=mytoken;" +
                //"alert('success');" +
                "}" +
                "</script>";
//        replace = replace.replace("", jsText + "\n");
//        WebResourceResponse webResourceResponse = new WebResourceResponse("text/html","utf-8", new ByteArrayInputStream(replace.getBytes()));
//        param.setResult(webResourceResponse);


        if (ret != null) {

            Log.d(TAG, "runVerify: sucess..");
            Log.d(TAG, "runVerify: before=" + ret);
            //ret=ret.replace( "</textarea>",finalResult+"</textarea>" );
            //ret=ret.replace( "g-recaptcha-response","g-recaptcha-response");
            ret = ret.replace("display: none;", "");
            ret = ret.replace("</body>", jsText + "\n</body>");
            Log.d(TAG, "replace ret= " + ret);

            int idxSt = ret.indexOf("data-cookie-value=\"") + 19;
            envCookie = ret.substring(idxSt, idxSt + 36);
            Log.d(TAG, "envCookie=: " + envCookie);

            webResourceResponse
                    = new WebResourceResponse("text/html"
                    , "utf-8"
                    , new ByteArrayInputStream(ret.getBytes()));
            return webResourceResponse;
        }
        return null;
    }

    public String fakeGoogleResp(String token) {

        //"09AMjm62UECznAfNQj_U7yxpTPmR_Xif2p13uljT5ap9EYOmWpklgBCjRE_RCTtoDuyQt1U6LJq3ZYBWu8gsWnoju5DzYVlFOHYLs" +


        String token2;
        if (envToken2 == null)
            token2 = getTokenFromRui( envHttpRuiToken2 );
        else
            token2 = envToken2;

        String[] tokenArr;
        if (token2 != null) {
            Log.d(TAG, "fakeGoogleResp: token2 before=" + token2);
            tokenArr = token2.split("\\n");
            int r = Ut.r_(0, tokenArr.length - 1);
            token2 = tokenArr[r].trim();
            Log.d(TAG, "fakeGoogleResp: token2 after=" + token2);
        } else {
            Log.d(TAG, "fakeGoogleResp: get token2 fail fromRui");
            token2=Ut.readFileToString(         HookShare.pathNkFolderData
                    +"/token2.txt"  );
            if (token2!=null){
                tokenArr = token2.split("\\n");
                int r = Ut.r_(0, tokenArr.length - 1);
                token2 = tokenArr[r].trim();
                Log.d(TAG, "fakeGoogleResp: get local token2="+token2 );
            }else{
                Log.d(TAG, "fakeGoogleResp: get local token2 fail");
            }

           // token2 = "09AMjm62UECznAfNQj_U7yxpTPmR_Xif2p13uljT5ap9EYOmWpklgBCjRE_RcCTtoDuyQt1U6LJq3ZYBWu8gsWnoju5DzYVlFOHYLs";
        }

        String str = ")]}\'"
                + "\n[\"uvresp\",\"" +
                token +
                "\",1,120,null,null,null,null,null,\"" +
                token2 +
                "\"]";
        Log.d(TAG, "fakeGoogleResp: fake str=" + str);
        return str;

    }


    public WebResourceResponse runVerify(WebResourceRequest webResourceRequest) {

        Map<String, String> head = webResourceRequest.getRequestHeaders();
        Log.d(TAG, "runVerify: headToString=" + head.toString());

        envHead = webResourceRequest.getRequestHeaders();
        String url = webResourceRequest.getUrl().toString();
        envPageUrl = url;

        Log.d(TAG, "runVerify: head=" + head.toString());

        String ret = Okhttp.get(url, head);
        String siteKey = null;

        if (ret != null) {
            List<String> listTp = Ut.regexp("siteKey: '([\\w]{30,100})'", ret);
            if (ret != null) {
                Log.d(TAG, "runVerify: regexp found.");
                siteKey = listTp.get(1);
            } else {
                Log.d(TAG, "runVerify: regexp fail maybe get the urlPage fail");
                return null;
            }
        }

        String finalResult = get_google_token_result(webResourceRequest, siteKey);
        envToken = finalResult;

        if (finalResult == null) {
            return null;
        }

        Log.d(TAG, "runVerify: finalResult=" + finalResult);
/*        String jsText = "<script type=\"text/javascript\">mytoken='"+ finalResult+
                "';"
                + "function testCall(){" +
                "document.getElementById(\"g-recaptcha-response-1\").innerHTML=mytoken;" +
                //"alert('success');" +
                "}" +
                "</script>";*/

//        replace = replace.replace("", jsText + "\n");
//        WebResourceResponse webResourceResponse = new WebResourceResponse("text/html","utf-8", new ByteArrayInputStream(replace.getBytes()));
//        param.setResult(webResourceResponse);

        if (ret != null) {

            Log.d(TAG, "runVerify: sucess..");
            Log.d(TAG, "runVerify: before=" + ret);
            //           ret=ret.replace( "display: none;","" );
//            ret=ret.replace("</body>",jsText+"\n</body>");
//            Log.d(TAG, "replace ret= "+ret  );

            int idxSt = ret.indexOf("data-cookie-value=\"") + 19;
            envCookie = ret.substring(idxSt, idxSt + 36);
            Log.d(TAG, "envCookie=: " + envCookie);

            webResourceResponse
                    = new WebResourceResponse("text/html"
                    , "utf-8"
                    , new ByteArrayInputStream(ret.getBytes()));
            return webResourceResponse;

        }
        return null;
    }

    public WebResourceResponse setRespStrToWebRespByte(String resp) {
        WebResourceResponse webResourceResponse
                = new WebResourceResponse("text/html"
                , "utf-8"
                , new ByteArrayInputStream(resp.getBytes()));

        return webResourceResponse;
    }

    public WebResourceResponse runVerify(String resp) {


        Log.d(TAG, "runVerify: string run");
        //String replace = string.replace(oldStr, "测试提交啊");
        String jsText = "<script type=\"text/javascript\">mytoken='" +
                "';"
                + "function testCall(token){alert('success');" +
                "document.getElementById(\"g-recaptcha-response\").innerHTML=token;" +
                "}" +
                "</script>";


        if (resp != null) {

            Log.d(TAG, "runVerify: sucess..");
            Log.d(TAG, "runVerify: before=" + resp);
            //resp=resp.replace( "</textarea>",finalResult+"</textarea>" );
            resp = resp.replace("display: none;", "");
            resp = resp.replace("</body>", jsText + "\n</body>");
            Log.d(TAG, "replace ret= " + resp);

            webResourceResponse
                    = new WebResourceResponse("text/html"
                    , "utf-8"
                    , new ByteArrayInputStream(resp.getBytes()));
            return webResourceResponse;
        } else {
            Log.d(TAG, "runVerify: null");
        }
        return null;
    }


//    public get_cookie(){
//
//    }

    /**
     * @param key
     * @param siteKey
     * @param pageUrl
     * @return
     */
    public String get_google_token(String key, String siteKey, String pageUrl) {
        String url = "http://2captcha.com/in.php?"
                + "key=" + key
                + "&method=userrecaptcha"
                + "&googlekey=" + siteKey
                + "&pageurl=" + pageUrl
                + "&json=1"
                + "&userAgent=" + envHeadStr;

        //{"status":1,"request":"66435934048"}
        long t1 = System.currentTimeMillis();

        while (true) {

            String ret = Okhttp.get(url);
            if (ret != null) {

                Log.d(TAG, "get_google_token: ret=" + ret);
                JSONObject jsonObject = JSON.parseObject(ret);
                String v = jsonObject.getString("request");

                if (!v.equals("") &&
                        jsonObject.getIntValue("status") == 1) {
                    Ut.fileWriterTxt(envPathStateReCaptcha,
                            jsonObject.getString("request")
                    );
                    return v;
                } else {
                    Ut.fileWriterTxt( envPathStateReCaptcha, ret );
                    Log.d(TAG, "get_google_token: err=" + ret );
                }

            } else {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "get_google_token: fail");

            if (System.currentTimeMillis() - t1 > 60 * 1000) {

                Log.d(TAG, "get_google_token: outtime");
                Ut.fileWriterTxt(envPathStateReCaptcha, "outtime");
                break;

            }

        }

        return null;

    }


    //http://2captcha.com/res.php?
    // key=1abc234de56fab7c89012d34e56fa7b8&action=get&id=2122988149

    /**
     * @param key
     * @param id
     * @return
     */
    public String getGoogleResult(String key, String id) {
        String url = "http://2captcha.com/res.php?"
                + "key=" + key
                + "&action=get"
                + "&id=" + id
                + "&json=1";

        long t1 = System.currentTimeMillis();

        while (true) {

            String ret = Okhttp.get(url);
            if (ret != null) {

                JSONObject jobj = JSON.parseObject(ret);
                //"status" )==1
                if (jobj.getIntValue("status") == 1) {
                    Ut.fileWriterTxt(envPathStateReCaptcha, ret);
                    envToken = jobj.getString("request");
                    return jobj.getString("request");
                } else {
                    boolVerfy=true;
                    Ut.fileWriterTxt(envPathStateReCaptcha, ret);
                    Log.d(TAG, "getGoogleResult: err=" + ret + ",id=" + id
                    );
                }

            } else {
                Log.d(TAG, "getGoogleResult: fail");
            }

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (System.currentTimeMillis() - t1 > 180 * 1000) {
                Ut.fileWriterTxt(envPathStateReCaptcha, "outtime");
                return null;
            }

        }


    }


    public String convertToString(InputStream is) {

        BufferedReader bReader = new BufferedReader(new InputStreamReader(is));
        StringBuffer buffer = new StringBuffer();
        String line = null;
        try {
            while ((line = bReader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
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


