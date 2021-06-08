package com.example.xposedemo.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.xposedemo.R;
import com.example.xposedemo.MyInterface.DialogCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 简单的弹出显示列表内容的对话框
 */
public class MyUi   {

    private static final String TAG = "MyUi";
    public static final int COUNT_FALSE=0;

    public static void dialogShow(String[] items, Context activityContext){
        final String[] items2=items;


        //dialog参数设置
        AlertDialog.Builder builder=new AlertDialog.Builder(activityContext);  //先得到构造器
        builder.setTitle("settingPath"); //设置标题
        builder.setIcon(R.mipmap.ic_launcher);

        //设置图标，图片id即可
        //设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        builder.setItems(items,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                //Toast.makeText(context, items2[which], Toast.LENGTH_SHORT).show();
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
     * 多选列表框 根据jsontxt { "包名":boolean },展示列表,点确定后回写json到路径
     * @param activityContext
     * @param title
     * @param icon  R.mipmap.ic_launcher
     * @param pathJsonConfigO
     * @param PositiveButtonText
     */
    public static void dialogSetMultiChoiceItems(Context activityContext, String title
            , int icon, String pathJsonConfigO
            , String PositiveButtonText , final DialogCallBack dialogCallBack ) {

        final String pathJsonConfig = pathJsonConfigO;
        String jsonTxtPackages =MyFile.readFileToString(pathJsonConfig);
        final JSONObject jsonObject;
        String k;

        List<String> listItems = new ArrayList<>();
        List<Boolean> listSelected = new ArrayList<>();
        Map<String,Boolean> mapRet=null;

        if (jsonTxtPackages == "") {
            Log.d(TAG, "setMultiChoiceItems: txtEmpty");
            return ;
        } else {
            mapRet=new HashMap<>();
            jsonObject = JSON.parseObject(jsonTxtPackages);
            if (jsonObject.size() < 1) {
                Log.d(TAG, "setMultiChoiceItems: jobj size<1");
                return ;
            }
            for (Map.Entry<String, Object> entry :
                    jsonObject.entrySet()) {
                k = entry.getKey();
                if (entry.getValue().equals(true) )
                    mapRet.put(k,true);
                listItems.add(k);
                listSelected.add( jsonObject.getBoolean( entry.getKey() ) );
            }

        }

        final String[] items = listItems.toArray( new String[listItems.size()] );
        boolean[] selected = Ut.listBooleanToArray( listSelected );
        if (selected == null) {
            Log.d(TAG, "setMultiChoiceItems: selected size err-null");
            return ;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);  //先得到构造器
        builder.setTitle(title); //设置标题
        builder.setIcon(icon);//设置图标，图片id即可
        List<String> packageSeleted = new ArrayList<>();

        final MyOnMultiChoiceClickListener myOnMultiChoiceClickListener=new MyUi.MyOnMultiChoiceClickListener(
                jsonObject,items,selected
        );

        builder.setMultiChoiceItems(items, selected, myOnMultiChoiceClickListener);
        builder.setPositiveButton(PositiveButtonText, new DialogInterface.OnClickListener()  {
            @Override
            public void onClick(DialogInterface dialog, int which  ) {
                Ut.fileWriterTxt( pathJsonConfig, myOnMultiChoiceClickListener.jsonObjectListItems.toJSONString() );
                if (dialogCallBack!=null)
                    dialogCallBack.setPositiveButtonCallback( dialog,which);
                dialog.dismiss();
            }

        });



        AlertDialog alertDialo = builder.create();
        alertDialo.show();
    }

    /**
     * 多选列表框 根据jsontxt { "包名":boolean },展示列表,点确定后回写json到路径
     * @param activityContext
     * @param title
     * @param icon  R.mipmap.ic_launcher
     * @param listitemsNew  新的listItem
     * @param pathJsonConfigO
     * @param PositiveButtonText
     *  @param textSetNegativeButton
     * @param countTime 是否倒计时
     * @return
     */
    public static AlertDialog dialogSetMultiChoiceItems(Context activityContext, String title
            , int icon, List<String> listitemsNew, String pathJsonConfigO
            , String PositiveButtonText, String textSetNegativeButton, final DialogCallBack dialogCallBack,
                                                       final int countTime) {

        final String pathJsonConfig = pathJsonConfigO;
        String jsonTxtPackages =MyFile.readFileToString(pathJsonConfig);
        final JSONObject jsonObject;
        String k;

        List<String> listItems = new ArrayList<>();
        List<Boolean> listSelected = new ArrayList<>();
        Map<String,Boolean> mapRet=null;

        listItems=listitemsNew;

        if (jsonTxtPackages == "") {
            Log.d(TAG, "dialogSetMultiChoiceItems: txtEmpty");
            jsonObject=new JSONObject();
            for ( String str :
                    listItems ) {
                listSelected.add(false);
                jsonObject.put( str,false );
            }
            //return ;
        } else {
            jsonObject=new JSONObject();

            List<String> selected=getSelectedJobjByPath( pathJsonConfig );
            if ( selected!=null ){
                for (String str :
                  listItems   ) {
                    jsonObject.put( str,false );
                    if ( selected.contains( str ) ){
                        listSelected.add(true);
                        jsonObject.put(str,true);
                    }
                    else
                        listSelected.add(false);
                }
            }else
                listSelected.add(false);


   /*         mapRet=new HashMap<>();
            jsonObject = JSON.parseObject(jsonTxtPackages);
            if (jsonObject.size() < 1) {
                Log.d(TAG, "dialogSetMultiChoiceItems: Jsonjobj size<1");
                return ;
            }
            for (Map.Entry<String, Object> entry :
                    jsonObject.entrySet()) {
                k = entry.getKey();
                if (entry.getValue().equals(true) )
                    mapRet.put(k,true);
                listItems.add(k);
                listSelected.add( jsonObject.getBoolean( entry.getKey() ) );
            }*/

        }


        final String[] items = listItems.toArray( new String[listItems.size()] );
        Log.d(TAG, "dialogSetMultiChoiceItems: listSelected-size-"+listSelected.size());
        boolean[] selected = Ut.listBooleanToArray( listSelected );
        if (selected == null) {
            Log.d(TAG, "setMultiChoiceItems: selected size err-null");
            return null;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);  //先得到构造器
        builder.setTitle(title); //设置标题
        builder.setIcon(icon);//设置图标，图片id即可
        List<String> packageSeleted = new ArrayList<>();

        final MyOnMultiChoiceClickListener myOnMultiChoiceClickListener=new MyUi.MyOnMultiChoiceClickListener(
                jsonObject,items,selected
        );

        builder.setMultiChoiceItems(items, selected, myOnMultiChoiceClickListener);
        builder.setPositiveButton( PositiveButtonText, new DialogInterface.OnClickListener()  {
            @Override
            public void onClick(DialogInterface dialog, int which  ) {
                Ut.fileWriterTxt( pathJsonConfig, myOnMultiChoiceClickListener.jsonObjectListItems.toJSONString() );
                dialog.dismiss();
                if (dialogCallBack!=null)
                    dialogCallBack.setPositiveButtonCallback( dialog,which);
            }
        });

        textSetNegativeButton=textSetNegativeButton==null?"返回":textSetNegativeButton;
        builder.setNegativeButton(textSetNegativeButton, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dialogCallBack.setPositiveButtonCallback( dialog,which );
            }
        });

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        if (countTime!=COUNT_FALSE )
            dialogCountClose( alertDialog,countTime,dialogCallBack,pathJsonConfig,myOnMultiChoiceClickListener );

        return alertDialog;
    }

    public static void dialogCountClose(final Dialog alertDialog, final int countTime
            , final DialogCallBack dialogCallBack,
                                        final String pathJsonConfig, final MyOnMultiChoiceClickListener myOnMultiChoiceClickListener){
        final Timer mOffTime = new Timer(true);
        final Handler mOffHandler = new Handler() {

            public void handleMessage(Message msg) {
                if (msg.what > 0) {
                    // 动态显示倒计时
                    alertDialog.setTitle ("    即将关闭："+msg.what);
                } else {
                    //倒计时结束自己主动关闭
                    if(alertDialog!=null){
                        alertDialog.dismiss();
                    }
                    //off();关闭后的操作
                    MyFile.fileWriterTxt( pathJsonConfig, myOnMultiChoiceClickListener.jsonObjectListItems.toJSONString());
                    dialogCallBack.setPositiveButtonCallback (null,0);
                    mOffTime.cancel();
                }
                super.handleMessage(msg);
            }

        };

         //倒计时
            TimerTask tt = new TimerTask(){
            class TimeTmp{
                private int time_;
                public TimeTmp(int time_) {
                    this.time_=time_;
                }
            }

            public final TimeTmp timeTmp=new TimeTmp( countTime );
            public void run() {
                if (timeTmp.time_ > 0) {
                    timeTmp.time_--;
                }
                Message msg = new Message();
                msg.what = timeTmp.time_;
                mOffHandler.sendMessage(msg);
            }
        };
        mOffTime.schedule(tt, 1000, 1000);
    }

    /**根据将check的选项的最终结果存入 jsonObjectListItems
     * jsonObjectListItems 选项列表,
     * items
     *
     */
    public static class  MyOnMultiChoiceClickListener  implements DialogInterface.OnMultiChoiceClickListener    {

        public JSONObject jsonObjectListItems;
        public String[] items;
        public boolean[] selected;

        public MyOnMultiChoiceClickListener(JSONObject jsonObjectListItems,String[] items,boolean[] selected ){
            this.jsonObjectListItems=jsonObjectListItems;
            this.items=items;
            this.selected=selected;
        }

        public Map<String,Boolean> mapRet;
        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            // dialog.dismiss();
            jsonObjectListItems.put( items[ which ],isChecked );
        }

    }


    /**
     *  返回值为true的 { "key1","key2".. }  ,一般用于 多选列表对话框展示
     * @param pathJsonTxt  txt路径 txtJson格式 { "key":boolean }
     * @return
     */
    public static List<String>  getSelectedJobjByPath ( String pathJsonTxt ) {

        String json=MyFile.readFileToString( pathJsonTxt );
        JSONObject jsonObject= JSON.parseObject(json);
        JSONObject jobj2=null;

        List<String> listRet=null;

        if( jsonObject ==null)
            return null;

        listRet=new ArrayList<>();
        for ( Map.Entry<String,Object> entry :
                jsonObject.entrySet() ) {
            if( (boolean)entry.getValue() ){
                Log.d(TAG, "returnSelectedJobj: "+entry.getKey()  );
                listRet.add( entry.getKey() );
            }
        }
        return listRet;
    }


    /**
     *  返回值为true的 { "key1","key2".. }  ,一般用于 多选列表对话框展示
     * @param json  txt路径 txtJson格式 { "key":boolean }
     * @return
     */
    public static List<String>  getSelectedJobjByJson ( String json ) {

        JSONObject jsonObject= JSON.parseObject(json);
        JSONObject jobj2=null;

        List<String> listRet=null;
        if( jsonObject ==null)
            return null;

        listRet=new ArrayList<>();
        for ( Map.Entry<String,Object> entry :
                jsonObject.entrySet() ) {
            if( (boolean)entry.getValue() ){
                Log.d(TAG, "returnSelectedJobj: "+entry.getKey()  );
                listRet.add( entry.getKey() );
            }
        }
        return listRet;

    }


}
