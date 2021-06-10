package com.example.xposedemo.functionModule;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.xposedemo.Hook.HookShare;
import com.example.xposedemo.MyInterface.DialogCallBack;
import com.example.xposedemo.R;
import com.example.xposedemo.bean.MainActivityData;
import com.example.xposedemo.utils.MyDate;
import com.example.xposedemo.utils.MyFile;
import com.example.xposedemo.utils.MyUi;
import com.example.xposedemo.utils.Ut;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class DataBack {
    //类加载时就初始化
    //private static final DataBack instance = new DataBack(  );

    private static volatile DataBack instance;
    private static final String TAG ="DataBack" ;

    public Activity activity;
    private static Context context;
    private TextView logTextview;
    private String functionPackageName;

    private String desSaveFilePathCommon ;
    private  String desSaveFilePathFinal;
    private  String backPathFromEdit;
    public Handler handler;
    public  Thread curThread;

    private DataBack( MainActivityData mainActivityData ){

        this.activity=mainActivityData.getActivity();
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
        if ( new File( HookShare.PATH_DATA_USER+"/"+packageName ).exists() )
            return HookShare.PATH_DATA_USER+"/"+packageName;
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


    public void loadData( ){

        final Button  bt_load =(Button)activity.findViewById( R.id.bt_load );
        bt_load.setEnabled (false);
        //activity.setVisible(false);

        final String path=  getLoadDataPath();
        final String pathLoadData=path+"/"+functionPackageName ;
        final String pathLoadDeviceTxt=path+"/deviceJson.txt";
        String pathLoadDevicePhoneTxt=path+"/devicePhone.txt";

        Log.d(TAG, "loadData: path="+pathLoadData);
        if( !new File( pathLoadData ).exists() ){
            bt_load. setEnabled (true);
            logUi("未找到指定数据"+pathLoadData);
            return;
        }
        Ut.stopAppByKill( activity.getApplicationContext(),functionPackageName);

        Runnable runnable=new Runnable() {
            @Override
            public void run() {

                delCache();
                logUi("删除完成" + "->" + HookShare.PATH_DATA_USER+"/" + functionPackageName);
                logUi("载入数据" + pathLoadData);
                MyFile.execCmdsforResult(new String[]{"cp -r " + pathLoadDeviceTxt + " "
                        + HookShare.pathDeviceJson});
                MyFile.execCmdsforResult(new String[]{"cp -r " + pathLoadDeviceTxt + " "
                        + HookShare.pathDeviceJson});
                //复制文件
                logUi("复制开始-" + pathLoadData);
                MyFile.execCmdsforResult(new String[]{"cp -r " + pathLoadData + " "
                        + "/data/data"});
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        bt_load.setEnabled(true);
                        activity.setVisible(true);
                    }
                });
                Ut.stopAppByKill( activity.getApplicationContext(),functionPackageName);

                logUi("载入完成" + path);

            }
        };

        threadWaitting();
      curThread=  new Thread( runnable );
      curThread.start();
        //thread.join();

    }

    public void delCacheByThread(){
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                delCache();
            }
        };

        curThread=new Thread( runnable );
        threadWaitting();
        curThread.start();

    }

    /**
     * 删除缓存
     */
    public void delCache() {

        if ( functionPackageName==""|!new File(HookShare.PATH_DATA_USER+"/"+functionPackageName ).exists() ){
            logUi("没找到缓存文件"+HookShare.PATH_DATA_USER+"/"+functionPackageName  );
            return;
        }

        //ArrayList<String> list = MyFile.execCmdsforResult(new String[] {"cd /data/data/"+functionPackageName , "ls"});
        ArrayList<String> list = MyFile.execCmdsforResult(new String[] {"cd "+HookShare.PATH_DATA_USER+"/" +functionPackageName , "ls"});
        for (String str :
                list) {
            if ( str!=null&&str!="" ) {
                String delCommandFile = HookShare.PATH_DATA_USER+"/" + functionPackageName + "/" + str;
                MyFile.execCmdsforResult(new String[]{"rm -r " +
                        delCommandFile}
                );
                logUi( "删除文件-"+delCommandFile );
            }
        }
        logUi("删除缓存完成");
    }

    public void delBack(){

        MyUi.setControlEnable( activity,R.id.bt_delBack ,false );

        final List<String> listSelected=Ut.getSelectedJobjByPath( HookShare.PATH_DATABACK_JSON );
        if ( listSelected.size()<1 ){
            logTextview.setText( "未选中数据" );
            MyUi.setControlEnable( activity,R.id.bt_delBack ,true );
        }else {

            Runnable runnable=new Runnable() {
                @Override
                public void run() {
                    Looper.prepare();
                    for ( String str :
                            listSelected  ) {
                        String path=desSaveFilePathCommon+"/"+str;
                        if ( !new File(path).exists() ){
                            logUi("备份不存在");
                            break;
                        }else {
                            for (int i=0;i<3;i++) {
                                logUi("开始删除第" + i + "次" + path);
                                MyFile.execCmdsforResult(new String[]{"rm -r " +
                                        path});
                                if (!new File(path).exists())
                                    break;
                            }

                        }

                    }
                    logUi("删除完成" );

                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyUi.setControlEnable( activity,R.id.bt_delBack ,true );
                        }
                    });


                    //dialogShowDataBack(3);
                    Looper.loop();

                }
            };

            threadWaitting();
            curThread=new Thread( runnable );
            curThread.start();

        }

    }


    /**
     * 列表展示应用备份数据,并保存.
     */
    public void dialogShowDataBack( int countTime ){
        //MyUi.dialogSetMultiChoiceItems();
        /**
         * 多选列表框 根据jsontxt { "包名":boolean },展示列表,点确定后回写json到路径
         * @param activityContext
         * @param title
         * @param icon  R.mipmap.ic_launcher
         * @param pathJsonConfigO
         * @param PositiveButtonText
         */

        if (!new File( desSaveFilePathCommon ).exists() ){
             logUi ( "没有目录,是否指定app？" );
            return;
        }

        Log.d(TAG, "dialogShowDataBack: savepath="+desSaveFilePathCommon );

        List<String> listFile= MyFile.execCmdGetFileNameList( desSaveFilePathCommon);
        if ( listFile.size()<1 ){
            MyFile.fileWriterTxt( HookShare.PATH_DATABACK_JSON ,"" );
            logUi( "err-未找到文件" );
            return;
        }
        Log.d(TAG, "dialogShowDataBack: listFile"+listFile.size() );
        MyUi.dialogSetMultiChoiceItems(activity, "备份文件列表",
                R.mipmap.ic_launcher, listFile, HookShare.PATH_DATABACK_JSON,
                "确定", null,new DialogCallBack() {
                    @Override
                    public void setPositiveButtonCallback(DialogInterface dialog, int which) {
                        String showStr="已选中数据:\r\n";
                        List<String> listSelected=MyUi.getSelectedJobjByPath( HookShare.PATH_DATABACK_JSON );
                        if ( listSelected.size()<1 ){
                            showStr="未选中数据";
                        }else{

                            showStr=showStr+ Ut.joinListString( listSelected, "\r\n" );
                            Log.d(TAG, "setPositiveButtonCallback: "+Ut.joinListString( listSelected, "\r\n" ) );
                        }
                        logUi ( showStr );
                    }

                    @Override
                    public void SetNegativeButtonCallback(DialogInterface dialog, int which) {

                    }
                },  countTime );

    }

    public String getLoadDataPath(){

        List<String> listSelected = Ut.getSelectedJobjByPath( HookShare.PATH_DATABACK_JSON );
        if (listSelected.size()>0){
            Log.d(TAG, "getLoadDataPath: path="+ desSaveFilePathCommon+"/"+listSelected.get(0) );
            String path=desSaveFilePathCommon+"/"+listSelected.get(0);
            MyFile.fileWriterTxt( HookShare.PATH_DATABACK_SELECTED_PATH,path );
            return path;
        }
        else
            return null;

    }

    public void saveAppByThread(){

        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                //Looper.prepare();
                saveAppData();
               //dialogShowDataBack(2);
                //Looper.loop();
            }
        };

        threadWaitting();
        curThread =new Thread( runnable );
        curThread.start();

    }

    public void threadWaitting(){

        try {
            if (curThread!=null){
                if (curThread.getState()!= Thread.State.TERMINATED );{
                    curThread.join();
                }
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public boolean saveAppData()    {

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyUi.setControlEnable( activity,R.id.bt_save,false );
            }
        });

      String format=  "yyyy~MM~dd,HH.mm.ss";
      String  timeStamp=MyDate.timeStamp();
      //Log.d(TAG,"saveAppData+timeStap="+ timeStamp );
        // Log.d(TAG,"saveAppData+data="+ MyDate. timeStamp2Date ( timeStamp ,null ) );

        desSaveFilePathFinal=
                desSaveFilePathCommon+"/"+"name-"
                +timeStamp+"-"
                +MyDate.timeStamp2Date ( timeStamp ,format )
        ;

        String appDataPath=desSaveFilePathFinal+"/"+functionPackageName;
        String cachePath=HookShare.PATH_DATA_USER+"/"+this.functionPackageName ;
        if ( !new File( cachePath ).exists() ) {
            Log.d(TAG, "saveAppData: "+functionPackageName+",缓存文件未发现");
            logUi("缓存文件未发现,是否没指定app"  );

            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    MyUi.setControlEnable( activity,R.id.bt_save,true );
                }
            });

            return false;
        }

        ArrayList<String> list = MyFile.execCmdsforResult(new String[] {"cd /data/data/"+functionPackageName , "ls"});
        Ut.crFolderEx ( appDataPath );

        logUi ( functionPackageName+"开始备份");
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
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MyUi.setControlEnable( activity,R.id.bt_save,true );
            }
        });

        return true;

    }

    public void logUi(final String text){

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logTextview.setText( text );
            }
        });

    }



}
