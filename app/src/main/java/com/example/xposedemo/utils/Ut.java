package com.example.xposedemo.utils;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Looper;
import android.os.Parcel;
import android.os.RemoteException;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.xposedemo.MainActivity;
import com.example.xposedemo.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

public class Ut {
    protected static final String PREFS_FILE = "gank_device_id.xml";
    protected static final String PREFS_DEVICE_ID = "gank_device_id";
    private static final String TAG = "DeviceUtils";
    protected static String uuid;

    /**
     * 按行写入
     * @param path
     * @param list
     * @throws IOException
     */
    public static void writeLines( String path,List<String> list )   {

        File fout = new File(path);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(fout);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            for (String string:
                    list ) {
                bw.write(string);
                bw.newLine();
            }
            bw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "writeLines: FileNotFoundException"+e);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "writeLines: IOException"+e);
        }


    }

    /**
     *
     * @param min
     * @param max
     * @return
     */
    public static int r_( int min,int max ){
        int randomNumber;
            randomNumber = (int) (((max - min + 1) * Math.random() + min ));
            return randomNumber;
    }

    /**
     * 从list中随机取n个元素
     * @param listObj
     * @param n
     * @return
     */
    public static <T> List<T> r_list(List<T> listObj , int n ){
        Collections.shuffle( listObj );
        List<T> listRet=new ArrayList<>();
        for ( int i=0;i<n;i++){
            listRet.add( listObj.get(i)  );
        }
        return  listRet;
    }


    /**
     * 获取txt文件内容并按行放入list中
     */
    public static List<String> readLines(String path)   {
        List<String> list=null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(path);
            BufferedReader bufferedReader =new BufferedReader(fileReader);
            list =new ArrayList<String>();
            String str=null;
            while((str=bufferedReader.readLine())!=null) {
                if(str.trim().length()>2) {
                    list.add(str);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, "getFileContext: FileNotFoundException");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "getFileContext: IOException");
        }

        return list;
    }

    /**
     *
     * @param path 文件夹路径
     */
    public static void crFolder(String path) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    /**
     *
     * @param context
     * @return
     */
    public static List<String> getPackageNames( Context context ){

        PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        //用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<>();
        //从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add( packName );
            }
        }
        return packageNames;

    }

    /**
     * 弹框
     * @param context
     * @param items  new String[]{ "消息内容" }
     */
    private void dialogShow(final Context context, String[] items ){

        final String[] items2=items;

        //dialog参数设置
        AlertDialog.Builder builder=new AlertDialog.Builder(context);  //先得到构造器
        builder.setTitle("settingPath"); //设置标题
        builder.setIcon(R.mipmap.ic_launcher);

        //设置图标，图片id即可
        //设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(items,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Toast.makeText(context, items2[which], Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("返回",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //Toast.makeText(MainActivity.this, "完成", Toast.LENGTH_SHORT).show();
            }
        });

        builder.create().show();
    }



    /**
     * 获取系统属性
     * @param properties
     * @return
     */
    public  static Object getSystemProperties( String properties )  {
        String arch = "";//cpu类型
        Method get;
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            get = clazz.getDeclaredMethod("get", new Class[]{String.class});
            return get.invoke(clazz, new Object[]{properties});
        } catch (Exception e) {
            e.printStackTrace();
        }

        return  null;
    }

    public static void fileWriterTxt(String filePath,String content) {
        FileWriter fwriter = null;
        try {
            // true表示不覆盖原来的内容，而是加到文件的后面。若要覆盖原来的内容，直接省略这个参数就好
            fwriter = new FileWriter( filePath );
            fwriter.write(content);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                fwriter.flush();
                fwriter.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }


    /**
     * 获取uuid
     * @param context
     * @return
     */
    static public String getUDID( Context context )
    {
        if( uuid ==null ) {
            synchronized ( MainActivity.class ) {
                if( uuid == null) {

                     SharedPreferences prefs = context.getSharedPreferences( PREFS_FILE, 0);
                     String id = prefs.getString( PREFS_DEVICE_ID, null );
                    Log.d(TAG, "getUDID: LOCAL"+id );
                    id=null;

                    if (id != null) {
                        // Use the ids previously computed and stored in the prefs file
                        uuid = id;
                    } else {

                        final String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

                        // Use the Android ID unless it's broken, in which case fallback on deviceId,
                        // unless it's not available, then fallback on a random number which we store
                        // to a prefs file
                        try {
                            if (!"9774d56d682e549c".equals(androidId)) {
                                uuid = UUID.nameUUIDFromBytes(androidId.getBytes("utf8")).toString();
                                Log.d(TAG, "getUDID: if"+uuid );
                            } else {
                                final String deviceId = ((TelephonyManager) context.getSystemService( Context.TELEPHONY_SERVICE )).getDeviceId();
                                Log.d(TAG, "getUDID: ELSE" );
                                uuid = deviceId!=null ? UUID.nameUUIDFromBytes( deviceId.getBytes("utf8")).toString() : UUID.randomUUID().toString();
                            }
                        } catch (UnsupportedEncodingException e) {
                            throw new RuntimeException(e);
                        }

                        // Write the value out to the prefs file
                        prefs.edit().putString(PREFS_DEVICE_ID, uuid).commit();


                    }
                }
            }
        }
        return uuid;
    }

    public static String readAssetsTxt( Context context,String fileName ){
        String text=null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            // Convert the buffer into a string.
            text = new String(buffer, "GB2312");
        } catch (IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
        }
        return text;

    }

    public static boolean copyAssetsFile(Context context, String assetsPath, String savePath)  {
        // assetsPath 为空时即 /assets
        try{
            InputStream is = context.getAssets().open(assetsPath);
            FileOutputStream fos = new FileOutputStream(new File(savePath));
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                // buffer字节
                fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
            }
            fos.flush();// 刷新缓冲区
            is.close();
            fos.close();
        }catch (Exception e){
            return false;
        }
        return true;
    }

    /**
     * 这个方法是耗时的，不能在主线程调用
     */
    public static String getGoogleAdId(Context context) throws Exception {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            return "Cannot call in the main thread, You must call in the other thread";
        }
        PackageManager pm = context.getPackageManager();
        pm.getPackageInfo("com.android.vending", 0);
        AdvertisingConnection connection = new AdvertisingConnection();
        Intent intent = new Intent(
                "com.google.android.gms.ads.identifier.service.START");
        intent.setPackage("com.google.android.gms");
        if (context.bindService(intent, connection, Context.BIND_AUTO_CREATE)) {
            try {
                AdvertisingInterface adInterface = new AdvertisingInterface(
                        connection.getBinder());
                return adInterface.getId();
            } finally {
                context.unbindService(connection);
            }
        }
        return "";
    }

    private static final class AdvertisingConnection implements ServiceConnection {
        boolean retrieved = false;
        private final LinkedBlockingQueue<IBinder> queue = new LinkedBlockingQueue<>(1);

        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                this.queue.put(service);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }

        public void onServiceDisconnected(ComponentName name) {
        }

        IBinder getBinder() throws InterruptedException {
            if (this.retrieved)
                throw new IllegalStateException();
            this.retrieved = true;
            return this.queue.take();
        }
    }

    private static final class AdvertisingInterface implements IInterface {
        private IBinder binder;

        AdvertisingInterface(IBinder pBinder) {
            binder = pBinder;
        }

        public IBinder asBinder() {
            return binder;
        }

        public String getId() throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            String id;
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                binder.transact(1, data, reply, 0);
                reply.readException();
                id = reply.readString();
            } finally {
                reply.recycle();
                data.recycle();
            }
            return id;
        }

        public boolean isLimitAdTrackingEnabled(boolean paramBoolean)
                throws RemoteException {
            Parcel data = Parcel.obtain();
            Parcel reply = Parcel.obtain();
            boolean limitAdTracking;
            try {
                data.writeInterfaceToken("com.google.android.gms.ads.identifier.internal.IAdvertisingIdService");
                data.writeInt(paramBoolean ? 1 : 0);
                binder.transact(2, data, reply, 0);
                reply.readException();
                limitAdTracking = 0 != reply.readInt();
            } finally {
                reply.recycle();
                data.recycle();
            }
            return limitAdTracking;
        }
    }

    /**
     * 读txt文件内容
     * @param FILE_IN txt路径
     * @return  读出的string
     */
    public static String readFileToString ( String FILE_IN ) {
        String str="";
        File file=new File( FILE_IN );

        try {
            FileInputStream in=new FileInputStream(file);
            // size 为字串的长度 ，这里一次性读完
            int size=in.available();
            byte[] buffer=new byte[size];
            in.read(buffer);
            in.close();
            str=new String(buffer,"UTF-8");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            //return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }


    public static String txt2String(String path){

        File file=new File(path);
        StringBuilder result = new StringBuilder();
        try{
            BufferedReader br = new BufferedReader(new FileReader(file));//构造一个BufferedReader类来读取文件
            String s = null;
            while((s = br.readLine())!=null){//使用readLine方法，一次读一行
                result.append(System.lineSeparator()+s );
            }
            br.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return result.toString();
    }

    /**
     * 拼接map
     * @param params
     * @param Delimiter 分隔符
     * @return
     */
    public static String  join_map2str (Map<String, String> params, String Delimiter ) {
        List<String> keys = new ArrayList<String>(params.keySet());
        // Collections.sort(keys);//不按首字母排序, 需要按首字母排序请打开
        StringBuilder prestrSB = new StringBuilder();
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            String value = params.get(key);
//            if (encode) {
//                try {
//                    value = URLEncoder.encode(value, "GBK");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//            }
            if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
                prestrSB.append(key).append("=").append(value);
            } else {
                prestrSB.append(key).append("=").append(value).append(Delimiter);
            }
        }
        return prestrSB.toString();
    }

}

