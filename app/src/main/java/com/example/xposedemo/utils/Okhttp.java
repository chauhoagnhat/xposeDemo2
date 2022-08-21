package com.example.xposedemo.utils;


import android.util.Log;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class Okhttp {

    private static final String TAG = "nkScritp-Okhttp";
    private volatile static OkHttpClient okHttpClient;
    private volatile static Request request;
    private static long timeout=120;

    public static OkHttpClient getOkHttpClient() {
        if (okHttpClient==null){
            okHttpClient=new OkHttpClient.Builder()
                    .readTimeout(timeout,TimeUnit.SECONDS)
                    .writeTimeout(timeout,TimeUnit.SECONDS)
                    .build();
        }
        return okHttpClient;
    }

    public static void setTimeout( long timeoutB ){
        timeout=timeoutB;
        okHttpClient=null;
    }

    /**
     *
     * @param url
     * @param headers
     * @return
     */
    public static String get(String url,Map<String,String> headers)   {

        getOkHttpClient();
        request=new Request.Builder()
                .headers( Headers.of( headers ) )
                .url(url).build();
        Response response = null;

        try {
            response = okHttpClient.newCall( request).execute();
            String ret=response.body().string();
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "get: exception="+e.toString()  );
            return null;
        }

    }

    public static String get(String url)   {

        getOkHttpClient();
        request=new Request.Builder().url(url).build();
        Response response = null;

        try {
            response = okHttpClient.newCall( request).execute();
            String ret=response.body().string();
            return ret;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "get: exception="+e.toString()  );
            return null;
        }

    }

    public static String  post(String url, Map<String,String> para ){

        getOkHttpClient();
        //构建postbody
        FormBody.Builder builderFormBody= new FormBody.Builder();
        for (String str:
                para.keySet() ) {
            builderFormBody
                    .add(str,para.get(str) );
        }
        FormBody formBody=  builderFormBody.build();
        request=new Request.Builder().url(url)
                .post( formBody )
                .build();

        Response response = null;
        try {
            response = okHttpClient.newCall( request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "post: exception="+e.toString()  );
            return null;
        }

    }

    /**
     * 带hearders
     * @param url
     * @param para
     * @param heads
     * @return
     */
    public static String post(String url, Map<String,String> para,Map<String,String> heads ) {

        getOkHttpClient();

        //构建postbody
        FormBody.Builder builderFormBody= new FormBody.Builder();
        for (String str:
                para.keySet() ) {
            builderFormBody
                    .add(str,para.get(str) );
        }
        FormBody formBody=  builderFormBody.build();

        //RequestBody requestBody = RequestBody.create(JSON, String.valueOf(json))

        request=new Request.Builder().url(url)
                .headers( Headers.of(heads) )
                .post( formBody )
                .build();

        Response response = null;
        try {
            response = okHttpClient.newCall( request).execute();
            return response.body().string();

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "get: exception="+e.toString()  );
            return null;
        }

    }

    public static String post(String url, String para,Map<String,String> heads ) {

        getOkHttpClient();

        //构建postbody
//        FormBody.Builder builderFormBody= new FormBody.Builder();
//        for (String str:
//                para.keySet() ) {
//            builderFormBody
//                    .add(str,para.get(str) );
//        }
//        FormBody formBody=  builderFormBody.build();


        MediaType JSON = MediaType.parse("application/json;charset=utf-8");
        RequestBody requestBody = RequestBody.create(JSON, para);

        request=new Request.Builder().url(url)
                .headers( Headers.of(heads) )
                .post( requestBody )
                .build();

        Response response = null;
        try {
            response = okHttpClient.newCall( request).execute();
            return response.body().string();

        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "get: exception="+e.toString()  );
            return null;
        }

    }


    /**
     *  Okhttp.uploadImage( "http://192.168.1.14/submit", mutableMapOf( "file1" to "sdcard/1.png"
     *         "file2" to "sdcard/1.png" ) )
     * @param url
     * @param imageFieldPath{ "fileName":"filePath" }
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public static String uploadImage(String url,Map<String,String> imageFieldPath ) throws IOException, JSONException {

        getOkHttpClient();
        //Log.d("imagePath", imagePath);
        //File file = new File(imagePath);
        MultipartBody.Builder builder=    new MultipartBody.Builder()
                .setType( MultipartBody.FORM );

        for ( String key :
                imageFieldPath.keySet()) {
            RequestBody image = RequestBody.create( MediaType.parse("image/png"), new File( imageFieldPath.get(key) ) );
            builder.addFormDataPart( key,imageFieldPath.get(key),image );
        }
        RequestBody requestBody=builder.build();

 /*       RequestBody image = RequestBody.create( MediaType.parse("image/png"), file);
        RequestBody image2 = RequestBody.create( MediaType.parse("image/png"), file);*/

/*        RequestBody requestBody = new MultipartBody.Builder()
                .setType( MultipartBody.FORM )
                .addFormDataPart("file1", imagePath, image)
                .addFormDataPart("file2", imagePath, image2)
                .build();*/

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
        } catch (IOException e) {
            Log.d(TAG, "uploadImage: exception"+e.toString() );
            e.printStackTrace();
            return null;
        }
        //Log.d(TAG, "uploadImage: "+response.body().string() );
        return response.body().string();

    }



}
