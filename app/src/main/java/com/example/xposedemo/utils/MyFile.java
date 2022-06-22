package com.example.xposedemo.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class MyFile {

    private static final String TAG = "MyFile";

    /**
     * https://blog.csdn.net/lnn368/article/details/103276529
     * @param oldPath
     * @param newPath
     */
    public void copyFolder(String oldPath, String newPath) {

        try {
            File file = new File(newPath);
            if (!file.exists()) file.mkdirs(); //创建新文件夹
            File a = new File(oldPath);
            String[] files = a.list();
            File temp = null;
            for (int i = 0; i < files.length; i++) {
                if (oldPath.endsWith(File.separator)) {
                    temp = new File(oldPath + files[i]);
                } else {
                    temp = new File(oldPath + File.separator + files[i]);
                }

                if (temp.isFile()) {
                    FileInputStream input = new FileInputStream(temp);
                    FileOutputStream output = new FileOutputStream(newPath + "/" +
                            (temp.getName()).toString());
                    byte[] b = new byte[1024];
                    int len;
                    while ((len = input.read(b)) != -1) {
                        output.write(b, 0, len);
                    }
                    output.flush();
                    output.close();
                    input.close();
                }
                if (temp.isDirectory()) {//递归拷贝
                    copyFolder(oldPath + "/" + files[i], newPath + "/" + files[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 执行cmd遍历文件名，需root
     * @param path
     * @return
     */
    public static List<String> execCmdGetFileNameList( String path ){
       return execCmdsforResult(
               new String[] {"cd "+path, "ls" } );
    }

    /**
     *String command = "cp -r " + sd_path + " " + data_path; 复制文件
     * ex: 获取文件名  ArrayList<String> list = execCmdsforResult(new String[] {"cd /data/data/com.testapp", "ls -R"});
     * @param cmds
     * @return
     */

    public static ArrayList execCmdsforResult(String[] cmds) {
        ArrayList<String> list = new ArrayList<String>();
        try {

            Process process = Runtime.getRuntime().exec("su");
            Log.d(TAG, "execCmdsforResult: run");
            OutputStream os = process.getOutputStream();
            process.getErrorStream();
            InputStream is = process.getInputStream();

            int i = cmds.length;
            for (int j = 0; j < i; j++) {
                String str = cmds[j];
                os.write((str + "\n").getBytes());
            }

            os.write("exit\n".getBytes());
            os.flush();
            os.close();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is) );

            while (true) {
                String str = reader.readLine();
                if (str == null)
                    break;
                list.add(str);
            }

            reader.close();
            process.waitFor();
            process.destroy();
            Log.d(TAG, "execCmdsforResult: end.");
            return list;

        } catch (Exception localException){

        }
        return list;

    }


    //http://web.rslnano.com/yhapi.ashx?act=getPhoneCode&token=83f6f31d1c8b6566bbbc8886c04d8406_647&mobile=65330483&iid=3211
    public static String readAssetsTxt(Context context, String fileName ){

        String text=null;
        try {
            InputStream is = context.getAssets().open( fileName );
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


    /**
     * 将图片转换成Base64编码的字符串
     */
    public static String imageToBase64(String path){

        if( TextUtils.isEmpty(path) ){
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try{
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data,Base64.NO_CLOSE);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null !=is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;

    }

    //http://web.rslnano.com/yhapi.ashx?act=getPhoneCode&token=83f6f31d1c8b6566bbbc8886c04d8406_647&mobile=65330483&iid=3211
    public static List<String> readAssetsLines(Context context, String fileName ){

        List<String> list = null;
        String text=null;
        try {
            InputStream is = context.getAssets().open( fileName );
            BufferedReader bufferedReader = new BufferedReader(  new InputStreamReader (is)  );
            list = new ArrayList<String>();
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                if (str.trim().length() > 2) {
                    list.add(str);
                }
            }
        } catch (IOException e) {
            // Should never happen!
            throw new RuntimeException(e);
        }
        return  list;
    }

        /**
         * 获取txt文件内容并按行放入list中
         */
        public static List<String> readLines(String path) {
            List<String> list = null;
            FileReader fileReader = null;
            try {
                fileReader = new FileReader(path);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                list = new ArrayList<String>();
                String str = null;
                while ((str = bufferedReader.readLine()) != null) {
                    if (str.trim().length() > 2) {
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
     * 读txt文件内容
     * @param FILE_IN txt路径
     * @return  读出的string
     */
    public static String readFileToString ( String FILE_IN )   {

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



    /**
     *
     * @param filePath
     * @param content
     */
    public static void fileWriterTxt(String filePath,String content) {

        File f=new File( filePath );
        if ( !f.exists() ) {
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "fileWriterTxt: path="+filePath);
        FileWriter fwriter = null;
        try {
            // true表示不覆盖原来的内容，而是加到文件的后面。若要覆盖原来的内容，直接省略这个参数就好
            fwriter = new FileWriter( filePath );
            fwriter.write( content );
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
               if (fwriter!=null){
                    fwriter.flush();
                    fwriter.close();
               }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

    }


}
