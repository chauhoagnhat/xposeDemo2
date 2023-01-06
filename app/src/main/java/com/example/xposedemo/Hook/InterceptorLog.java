package com.example.xposedemo.Hook;


import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.nio.charset.Charset;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

public class InterceptorLog implements Interceptor {

    private String TAG=this.getClass().getName();

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {

 /*       Log.d(TAG, "intercept: ");

        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("url=[");
        stringBuilder.append( chain.request().url() );
        stringBuilder.append("]");

        stringBuilder.append("body=[");
        stringBuilder.append(chain.request().body() );
        stringBuilder.append("]");

        stringBuilder.append("header=[");
        stringBuilder.append(chain.request().headers() );
        stringBuilder.append("]");

        Log.d(TAG, "intercept: "+stringBuilder.toString() );
        return null;*/

        CLogUtils.e(TAG, "intercept run" );
        Request request = chain.request();
        Response response = chain.proceed(request);
        printInfo(request, response);
        return response;

    }



    private void printInfo(Request request, Response response) {
        if (request != null && response != null) {
            String logInfo = "自定义日志打印".concat(" \r\n ")
                    .concat("Request Url-->：")
                    .concat(request.method())
                    .concat(" ")
                    .concat(request.url().toString())
                    .concat(" \r\n ")
                    .concat("Request Header-->：")
                    .concat(getRequestHeaders(request))
                    .concat(" \r\n ")
                    .concat("Request Parameters-->：")
                    .concat(getRequestParams(request))
                    .concat(" \r\n ")
                    .concat("Response Result-->：")
                    .concat(getResponseText(response));
            CLogUtils.e(logInfo);
        }
    }

    private String getResponseText(Response response) {
        String str = "Empty!";
        try {
            ResponseBody body = response.body();
            if (body != null && body.contentLength() != 0) {
                BufferedSource source = body.source();
                source.request(Long.MAX_VALUE);
                Buffer buffer = source.buffer();
                MediaType mediaType = body.contentType();
                if (mediaType != null) {
                    @SuppressWarnings("CharsetObjectCanBeUsed") Charset charset = mediaType.charset(
                            Charset.forName("UTF-8"));
                    if (charset != null) {
                        str = buffer.clone().readString(charset);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    private String getRequestParams(Request request) {
        String str = "Empty!";
        try {
            RequestBody body = request.body();
            if (body != null) {
                Buffer buffer = new Buffer();
                body.writeTo(buffer);
                MediaType mediaType = body.contentType();
                if (mediaType != null) {
                    @SuppressWarnings("CharsetObjectCanBeUsed") Charset charset = mediaType.charset(
                            Charset.forName("UTF-8"));
                    if (charset != null) {
                        str = buffer.readString(charset);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    private String getRequestHeaders(Request request) {
        Headers headers = request.headers();
        if (headers.size() > 0) {
            return headers.toString();
        } else {
            return "Empty!";
        }
    }
//————————————————
//    版权声明：本文为CSDN博主「_WJerry」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
//    原文链接：https://blog.csdn.net/sunnyjerry/article/details/114088552

}
