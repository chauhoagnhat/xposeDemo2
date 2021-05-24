package com.example.xposedemo.fake;

import android.content.Context;

import com.example.xposedemo.utils.Ut;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FakePackage {

    private volatile static FakePackage instance=null;

    public final static String  pathPackagesGoogle ="/sdcard/packages/google.txt";
    public final static String  pathPackagesAndorid ="/sdcard/packages/andorid.txt";
    public final static String  pathPackagesChat  ="/sdcard/packages/chat.txt";
    public final static String  pathPackagesOther  ="/sdcard/packages/other.txt";
    public final String pathPackagesFolder="/sdcard/packages";
    public final Context context;
    public static final String PathPackagesFinalResult="/sdcard/packages/finalResult.txt";

    private FakePackage(Context context ){
        Ut.crFolder( pathPackagesFolder );
        this.context=context;
        FakePackage.fakeInit(context);
    }

    public static FakePackage getInstance(Context context ){
        if (instance == null) {
            synchronized (FakePackage.class) {
                if (instance == null) {
                    instance = new FakePackage( context );
                }
            }

        }
        return instance;
    }

    /**
     * 系统app+常用app+随机app
     */
    public void fakePackages (){

        //jp.naver.line.android

        //系统app
        List<String> listGoogle=Ut.readLines( pathPackagesGoogle );
        List<String> listAndroid=Ut.readLines( pathPackagesAndorid );

        //常用app
        List<String> listChat=Ut.readLines( pathPackagesChat );
        List<String> listChatRet=Ut.r_list( listChat,3 );

        //随机app
        List<String> listOther=Ut.readLines( pathPackagesOther );
        int r_num=Ut.r_( 7,15 );
        List<String> listOtherRet=Ut.r_list( listOther,r_num );

        List<String> listAll=new ArrayList<>();
        listAll.addAll(listGoogle);
        listAll.addAll(listAndroid);
        listAll.addAll(listChatRet);
        listAll.addAll(listOtherRet);
        Collections.shuffle( listAll );
        Ut.writeLines( PathPackagesFinalResult ,listAll );

    }

    public void fakeChat(){

    }

    public static void fakeInit(Context context){

        String android =Ut.readAssetsTxt( context,"android.txt"  );
        String google =Ut.readAssetsTxt( context,"google.txt"  );
        String chat =Ut.readAssetsTxt( context,"chat.txt"  );
        String other=Ut.readAssetsTxt(context,"other.txt");

        Ut.fileWriterTxt( pathPackagesAndorid,android );
        Ut.fileWriterTxt( pathPackagesGoogle ,google );
        Ut.fileWriterTxt( pathPackagesChat ,chat );
        Ut.fileWriterTxt( pathPackagesOther , other );

    }



}
