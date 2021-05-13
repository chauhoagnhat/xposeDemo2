package com.example.xposedemo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Utils {
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
                result.append(System.lineSeparator()+s);
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
