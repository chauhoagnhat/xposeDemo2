package com.example.xposedemo.Hook;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import dalvik.system.DexClassLoader;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


import okhttp3.Interceptor;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;

public class HookOkHttp implements InvocationHandler {
    private final String TAG = this.getClass().getName(); ;
    private Context mOtherContext;
    private ClassLoader mLoader;
    private int flag=0;
    private int flag1=0;
    private Class<?> bin;
    private Class<?> mHttpLoggingInterceptorClass;
    private Class<?> mHttpLoggingInterceptorLoggerClass;
    private ArrayList<ClassLoader> AppAllCLassLoaderList = new ArrayList<>();
    private static Object httpLoggingInterceptor = null;
    private DexClassLoader mDexClassLoader;


    public HookOkHttp(XC_LoadPackage.LoadPackageParam sharePkgParam  ) {
        HookGetOutPushStream();
        hookAttach();
    }

    public void hookAttach(){
        XposedHelpers.findAndHookMethod(Application.class, "attach"
                , Context.class
                , new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        mOtherContext=(Context)param.args[0];

                        if (flag==0){
                            mLoader=mOtherContext.getClassLoader();
                            Log.d(TAG+"hookAttach" , "afterHookedMethod: get the classLoader" );
                            //flag=1;
                            hookOkClient();
                        }
                    }
                })   ;

    }

    private void hookOkClient() {
        try {
            bin = Class.forName("okhttp3.OkHttpClient$Builder", true, mLoader);
            //if ( bin!=null&&flag1==0 ){
            if ( bin!=null&&flag1==0 ){
                Log.d(TAG, "hookOkClient: get the okhttp3.OkHttpClient$Builder class");
                invoke();
                //flag1=1;
            }else{
                Log.d(TAG, "hookOkClient: can not get the okhttp3.OkHttpClient$Builder class");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void invoke(){
        Log.d(TAG, "invoke: found okHttp,hook build");
        XposedHelpers.findAndHookMethod(bin, "build", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                List interceptors=(List)XposedHelpers.getObjectField( param.thisObject,"interceptors" );
                //interceptors.add( new InterceptorLog() );
                interceptors.add( getHttpLoggingInterceptorClass() );


                //                OkHttpClient.Builder builder = (OkHttpClient.Builder) param.getResult();
//                builder.addInterceptor( new InterceptorLog() );
//                param.setResult( builder );

                //Interceptor interceptor= (Interceptor) interceptors.get(0);
                //addInterceptor



            }
        });

        XposedHelpers.findAndHookMethod(bin, "build", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod (MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                //List interceptors=(List)XposedHelpers.getObjectField( param.thisObject,"interceptors" );
//                  OkHttpClient.Builder builder = (OkHttpClient.Builder) param.getResult();
//                    builder.addInterceptor( new InterceptorLog() );
//                    param.setResult( builder );

                Log.d(TAG, "invoke afterHookedMethod: build find" );
                Field[] fields=param.thisObject.getClass().getDeclaredFields ();

                for ( Field field :
                    fields ) {
                    Log.d(TAG, "invoke: Field "+field );
                }

//                try {
//                    OkHttpClient okHttpClient=(OkHttpClient)param.thisObject;
//                    Log.d(TAG, "invoke afterHookedMethod:  okHttpClient="+okHttpClient );
//                } catch (Exception e) {
//                    Log.d(TAG, "invoke afterHookedMethod:  okHttpClient Exception="+e.toString() );
//                    e.printStackTrace();
//                }

                List networkInterceptors=(List)XposedHelpers.getObjectField( param.thisObject,"networkInterceptors" );
                networkInterceptors.add( new InterceptorLog() );


            }
        });


    }


    private Object InitInterceptor() throws

            InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
//        CLogUtils.e("拿到  HttpLoggingInterceptor 和 logger ");
        //通过 动态代理 拿到 这个 接口 实体类
        Object logger;
//        if (isDexLoader) {
//            logger = Proxy.newProxyInstance(mDexClassLoader, new Class[]{mHttpLoggingInterceptorLoggerClass}, Hook.this);
//        } else {

        //mHttpLoggingInterceptorLoggerClass "okhttp3.logging.HttpLoggingInterceptor$Logger"
        logger = Proxy.newProxyInstance(mLoader, new Class[]{mHttpLoggingInterceptorLoggerClass}, HookOkHttp.this );
//        }
//        CLogUtils.e("拿到  动态代理的 class");
        Object loggingInterceptor = mHttpLoggingInterceptorClass.getConstructor( mHttpLoggingInterceptorLoggerClass )
                .newInstance(logger);
//        CLogUtils.e("拿到  拦截器的实体类  ");
        Object level;
//        if (isDexLoader) {
//            level = mDexClassLoader.loadClass("okhttp3.logging.HttpLoggingInterceptor$Level").getEnumConstants()[3];
//        } else {
        level = mLoader.loadClass("okhttp3.logging.HttpLoggingInterceptor$Level").getEnumConstants()[3];
//        }
           CLogUtils.e(TAG,"拿到  Level 枚举   "+level );

        //调用 函数
        //setLevel
        XposedHelpers.findMethodBestMatch( mHttpLoggingInterceptorClass, "setLevel",
                level.getClass() ).invoke(loggingInterceptor, level );

        Log.d(TAG, "InitInterceptor: 拦截器实例初始化成功");
        return loggingInterceptor;

    }


    /**
     * Hook 底层的方法
     * 这个是不管 什么 框架请求都会走的 函数
     */
    private void HookGetOutPushStream() {

        CLogUtils.e("开始 Hook底层 实现 ");
        //java.net.SocketInputStream


        try {
            XposedHelpers.findAndHookMethod(
                    XposedHelpers.findClass("java.net.SocketOutputStream", mLoader),
                    "write",
                    byte[].class,
                    int.class,
                    int.class,
//                    int.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            StringBuilder TraceString = new StringBuilder();
                         //   if (isShowStacktTrash) {
                      /*     if (true){
                                try {
                                    int b = 1 / 0;
                                } catch (Throwable e) {
                                    StackTraceElement[] stackTrace = e.getStackTrace();
                                    TraceString.append(" --------------------------  >>>> " + "\n");
                                    for (StackTraceElement stackTraceElement : stackTrace) {
                                        //FileUtils.SaveString(  );
                                        TraceString.append("   栈信息      ").append(stackTraceElement.getClassName()).append(".").append(stackTraceElement.getMethodName()).append("行数  ").append(stackTraceElement.getLineNumber()).append("\n");
                                    }
                                    TraceString.append("<<<< --------------------------  " + "\n");
                                }
                            }*/
                            TraceString.append("<<<<------------------------------>>>>>  \n")
                                    .append(new String((byte[]) param.args[0], StandardCharsets.UTF_8))
                                    .append("\n <<<<------------------------------>>>>>").append("\n");

                            CLogUtils.NetLogger(TraceString.toString());
//                            FileUtils.SaveString(mSimpleDateFormat.format(new Date(System.currentTimeMillis())) + "\n" + "  " +
//                                    TraceString.toString(), mOtherContext.getPackageName());
                        }
                    });


//            XposedHelpers.findAndHookMethod(httpUrlConnClass, mLoader,
//                    httpUrlConnGetInputStreamMethod, new XC_MethodHook() {
//                        @Override
//                        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws IOException {
//                            final Object httpUrlConnection = param.thisObject;
//                            URL url = (URL) XposedHelpers.callMethod(httpUrlConnection, httpUrlConnGetUrlMethod);
//                            InputStream in = (InputStream) param.getResult();
//
//                            InputStreamWrapper wrapper = CACHE.get();
//
//                            if (wrapper != null) {
//                                param.setResult(wrapper);
//                                return;
//                            }
//                            //流只能读取一次 用完在放回去
//                            wrapper = new InputStreamWrapper(in) {
//                                public void close() {
//                                    try {
//                                        super.close();
//                                    } catch (IOException e) {
//                                        e.printStackTrace();
//                                    }
//                                    CACHE.remove();
//                                }
//                            };
//                            //把数据流放到指定ThreadLocal里面保存起来
//                            CACHE.set(wrapper);
//                            param.setResult(wrapper);
//                            //判断是否保存成功
//                            if (wrapper.size() > 0) {
//                                //拿到解码类型
//                                String contentEncoding = (String) XposedHelpers.callMethod(httpUrlConnection,
//                                        httpUrlConnGetContentEncodingMethod);
//                                BufferedReader reader = null;
//                                StringBuffer responseBody = new StringBuffer();
//                                try {
//                                    if (contentEncoding != null) {
//                                        if ("gzip".equals(contentEncoding)) {
//                                            in = new GZIPInputStream(in);
//                                        }
//                                        CLogUtils.e("走了 gzip 解码");
//
//                                        //对gzip进行解析
//                                        reader = new BufferedReader(new InputStreamReader(in));
//
//                                        String line;
//                                        while ((line = reader.readLine()) != null) {
//                                            responseBody.append(line);
//                                        }
//                                    } else {
//                                        CLogUtils.e("走了 Body 解码");
//
//                                        //返回的是一个HttpEngine
//                                        Object HttpEngine = XposedHelpers.callMethod(httpUrlConnection,
//                                                httpUrlConngetGetResponseMethod);
//                                        Object respons = XposedHelpers.callMethod(HttpEngine,
//                                                httpUrlConngetGetResponseMethod);
//                                        //先判断是否含有Body
//                                        boolean hasBody = (boolean) XposedHelpers.callMethod(HttpEngine,
//                                                "hasBody", respons);
//                                        if (hasBody) {
//                                            Object body = XposedHelpers.callMethod(respons,
//                                                    "body");
//                                            String string = (String) XposedHelpers.callMethod(body,
//                                                    "string");
//                                            responseBody.append(string);
//
//                                        }
//                                    }
//                                    Map<String, List<String>> requestHeadersMap =
//                                            (Map<String, List<String>>) XposedHelpers.callMethod(httpUrlConnection,
//                                                    httpUrlConngetHeaderFieldsMethod);
//
//                                    StringBuilder RequestHeaders = new StringBuilder();
//
//                                    if (requestHeadersMap != null) {
//
//                                        for (String key : requestHeadersMap.keySet()) {
//                                            RequestHeaders.append(key);
//                                            List<String> values = requestHeadersMap.get(key);
//                                            for (String value : values) {
//                                                RequestHeaders.append(value).append(" ");
//                                            }
//                                            RequestHeaders.append("\n");
//                                        }
//                                    }
//
//                                    CLogUtils.e("当前Url \n" + url.toString() + "\n");
//                                    CLogUtils.e("请求头部信息 \n" + RequestHeaders.toString() + "\n");
//
//                                    CLogUtils.e("当前响应 \n" + responseBody.toString() + "\n");
//
//                                    StringBuilder TraceString = new StringBuilder();
//
//
//                                    if (isShowStacktTrash) {
//                                        try {
//                                            int b = 1 / 0;
//                                        } catch (Throwable e) {
//                                            StackTraceElement[] stackTrace = e.getStackTrace();
//                                            TraceString.append(" --------------------------  >>>> " + "\n");
//                                            for (StackTraceElement stackTraceElement : stackTrace) {
//                                                //FileUtils.SaveString(  );
//                                                TraceString.append("   栈信息      ").append(stackTraceElement.getClassName()).append(".").append(stackTraceElement.getMethodName()).append("行数  ").append(stackTraceElement.getLineNumber()).append("\n");
//                                            }
//                                            TraceString.append("<<<< --------------------------  " + "\n");
//                                        }
//                                    }
//
////                                    TraceString.append("<<<<------------------------------>>>>>  ")
////                                            .append("Url :  ").append(url.toString()).append("\n")
////                                            .append("请求头部信息 :  ").append(RequestHeaders.toString()).append("\n")
////                                            .append("响应信息 :  ").append(responseBody.toString()).append("\n")
////                                            .append(" <<<<------------------------------>>>>>").append("\n");
////
////                                    FileUtils.SaveString(mSimpleDateFormat.format(new Date(System.currentTimeMillis())) + "\n" + "  " +
////                                            TraceString.toString(), mOtherContext.getPackageName());
//
//                                } catch (Throwable e) {
//                                    CLogUtils.e("转换出现错误  " + e.toString() + "  line  " + e.getStackTrace());
//
//                                } finally {
//                                    try {
//                                        if (reader != null) {
//                                            reader.close();
//                                        }
//                                    } catch (Throwable e) {
//                                        CLogUtils.e("inputStream hook handle close reader error. url:" + url + ", contentEncoding:" + contentEncoding);
//                                    }
//                                }
//                            }
//                        }
//                    });
//
//
        } catch (Throwable e) {
            CLogUtils.e("HookGetOutPushStream     " + e.toString());
            e.printStackTrace();
        }
    }


    /**
     * 尝试获取拦截器
     *
     * @return
     */
    @NonNull
    private synchronized Object getHttpLoggingInterceptorClass() {
        //防止多次初始化影响性能
        if (httpLoggingInterceptor != null) {
            return httpLoggingInterceptor;
        }

        try {
            //第一步 首先  判断 本身项目里 是否存在 拦截器
            //okhttp3.logging.HttpLoggingInterceptor
            try {

                mHttpLoggingInterceptorClass = getClass("okhttp3.logging.HttpLoggingInterceptor");
                mHttpLoggingInterceptorLoggerClass = getClass("okhttp3.logging.HttpLoggingInterceptor$Logger");
                
            } catch (Throwable e) {

            }
            if ( mHttpLoggingInterceptorClass != null && mHttpLoggingInterceptorLoggerClass != null ) {
                CLogUtils.e(TAG,"get the app interceptor");
                return InitInterceptor();
            }

      /*      if (mHttpLoggingInterceptorLoggerClass == null || mHttpLoggingInterceptorClass == null) {
                //当前 App 使用了OkHttp 三种种情况，需分别进行处理
                //1,App没有被混淆，没有拦截器
                if (isExactness()) {
                    CLogUtils.e("当前App的OkHttp没有被混淆,可直接动态添加拦截器");
                    //直接尝试动态加载即可
                    return initLoggingInterceptor();
                } else {
                    CLogUtils.e("当前App的OkHttp被混淆");
                    //2,App被混淆，有拦截器,根据拦截器特征获取
                    Object httpLoggingInterceptorForClass = getHttpLoggingInterceptorForClass();
                    if (httpLoggingInterceptorForClass == null) {
                        CLogUtils.e("App Okhttp被混淆 并且没有拦截器");
                        //3,App被混淆，没有拦截器
                        return getHttpLoggingInterceptorImp();
                    } else {
                        CLogUtils.e("App Okhttp被混淆 存在拦截器");
                        return httpLoggingInterceptorForClass;
                    }
                }


            }*/

        } catch (Throwable e) {
            CLogUtils.e("getHttpLoggingInterceptor  拦截器初始化出现异常    " + e.toString());
            e.printStackTrace();
        }
        return null;
    }


    /*public HttpLoggingInterceptor getHttpLoggingInterceptor(LogLevel logLevel) {
        HttpLoggingInterceptor.Level level;
        if (BuildConfig.DEBUG) {
            level = HttpLoggingInterceptor.Level.NONE;
            return new HttpLoggingInterceptor().setLevel(level);
        }
        switch (logLevel) {
            case body:
                level = HttpLoggingInterceptor.Level.BODY;
                break;
            case header:
                level = HttpLoggingInterceptor.Level.HEADERS;
                break;
            case basic:
                level = HttpLoggingInterceptor.Level.BASIC;
                break;
            default:
                level = HttpLoggingInterceptor.Level.BODY;
                break;
        }
        return new HttpLoggingInterceptor().setLevel(level);
    }
*/

    /**
     * 遍历当前进程的Classloader 尝试进行获取指定类
     *
     * @param className
     * @return
     */
    private Class getClass(String className) {
        Class<?> aClass = null;
        try {
            try {
                aClass = Class.forName(className);
            } catch (ClassNotFoundException classNotFoundE) {

                try {
                    aClass = Class.forName(className, false, mLoader);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                if (aClass != null) {
                    return aClass;
                }
                try {
                    for (ClassLoader classLoader : AppAllCLassLoaderList) {
                        try {
                            aClass = Class.forName(className, false, classLoader);
                        } catch (Throwable e) {
                            continue;
                        }
                        if (aClass != null) {
                            return aClass;
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            return aClass;
        } catch (Throwable e) {

        }
        return null;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
        CLogUtils.NetLogger((String) objects[0]);
        return null;
    }

}
