package com.example.xposedemo.functionModule;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;

import com.example.xposedemo.Hook.HookShare;
import com.example.xposedemo.bean.MainActivityData;
import com.example.xposedemo.utils.MyFile;
import com.example.xposedemo.utils.Ut;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DataBack {

    //类加载时就初始化
    //private static final DataBack instance = new DataBack(  );

    private static volatile DataBack instance;
    private static final String TAG ="DataBack" ;

    private static Context context;
    private TextView logTextview;
    private String functionPackageName;

    private String desSaveFilePathCommon ;
    private  String desSaveFilePathFinal;
    private  String backPathFromEdit;


    private DataBack(MainActivityData mainActivityData){

        this.context=mainActivityData.getContext();
        this.logTextview=mainActivityData.getLogTextview();
        this.functionPackageName=getFunctionPackageName();
        this.backPathFromEdit = mainActivityData.getEt_path().getText().toString();
        this.desSaveFilePathCommon=backPathFromEdit+"/back/"+ functionPackageName;


//        if ( functionPackageName!=null )
//            saveAppData();
//        else
//            logTextview.setText( "未选中执行功能的app" );

    }


    public static DataBack getInstance( MainActivityData mainActivityData ){
        instance=new DataBack(mainActivityData);
        return instance;
    }

    public String getPathDataPackage(String packageName){
        if ( new File( "/data/data/"+packageName ).exists() )
            return "/data/data/"+packageName;
        else
            return null;
    }

    public String getFunctionPackageName(){

        List<String> listSelected=Ut.getSelectedJobjByPath( HookShare.PATH_FUNCTION_PACKAGES );
        if (null==listSelected||listSelected.size()<1)
            return null;

        Log.d(TAG, "getFunctionPackageName="+listSelected.get(0) );
        return  listSelected.get(0);

    }


    public void loadData( String name ){

        String pathLoadData=desSaveFilePathCommon+"/"+name+"/"+functionPackageName ;
        String pathLoadDeviceTxt=desSaveFilePathCommon+"/"+name+"/deviceJson.txt";
        String pathLoadDevicePhoneTxt=desSaveFilePathCommon+"/"+name+"/devicePhone.txt";

        if( !new File( pathLoadData ).exists() ){
            logUi("未找到指定数据"+pathLoadData);
            return;
        }

        ArrayList<String> list = MyFile.execCmdsforResult(new String[] {"cd /data/data/"+functionPackageName , "ls"});
        for (String str :
                list) {
            if ( str!=null&&str!="" )
                MyFile.execCmdsforResult( new String[] {"rm -r " +
                        "/data/data/"+functionPackageName+"/"+str }
            );
        }
        logUi( "删除完成"+"->"+"/data/data/"+functionPackageName );

        logUi( "载入数据"+pathLoadData );
        MyFile.execCmdsforResult( new String[] {"cp -r " +pathLoadDeviceTxt+" "
                +HookShare.pathDeviceJson  } );
        
        //logUi( "载入数据"+pathLoadData );
        MyFile.execCmdsforResult( new String[] {"cp -r " +pathLoadDeviceTxt+" "
             +HookShare.pathDeviceJson  } );
        MyFile.execCmdsforResult( new String[] {"cp -r " + pathLoadData +" "
                +"/data/data"  } );

        logUi( "载入完成"+name );

    }

    public boolean saveAppData()    {

        desSaveFilePathFinal=desSaveFilePathCommon+"/"+System.currentTimeMillis();
        String appDataPath=desSaveFilePathFinal+"/"+functionPackageName;

        String cachePath="/data/data/"+this.functionPackageName ;
        if ( !new File( cachePath ).exists() ) {
            Log.d(TAG, "saveAppData: "+functionPackageName+",缓存文件未发现");
            return false;
        }

        ArrayList<String> list = MyFile.execCmdsforResult(new String[] {"cd /data/data/"+functionPackageName , "ls"});
        Ut.crFolderEx ( appDataPath );

        logTextview.setText( functionPackageName+"开始备份");
        for (String str :
                list) {
            logUi( "开始复制文件"+str+"->"+desSaveFilePathCommon );
            MyFile.execCmdsforResult( new String[] {"cp -r /data/data/"+functionPackageName+"/"+str+" "
                    +appDataPath  });
        }
        MyFile.execCmdsforResult( new String[] {"cp -r "+HookShare.pathDeviceJson+" "
                +desSaveFilePathFinal  });
        MyFile.execCmdsforResult( new String[] {"cp -r "+HookShare.PATH_PHONE_DEVICE +" "
                +desSaveFilePathFinal  });

        logUi ( functionPackageName+"备份完成"+desSaveFilePathFinal );
        return true;

    }

    public void logUi(String text){
        logTextview.setText(text );
    }
}
