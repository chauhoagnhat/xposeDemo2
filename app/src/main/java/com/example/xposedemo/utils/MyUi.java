package com.example.xposedemo.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.xposedemo.MainActivity;
import com.example.xposedemo.R;
import com.example.xposedemo.myInterface.DialogCallBack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 简单的弹出显示列表内容的对话框
 */
public class MyUi implements DialogCallBack {

    private static final String TAG = "MyUi";

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
        boolean[] selected = Ut.listBooleanToArray(listSelected);
        if (selected == null) {
            Log.d(TAG, "setMultiChoiceItems: selected size err-null");
            return ;
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(activityContext);  //先得到构造器
        builder.setTitle(title); //设置标题
        builder.setIcon(icon);//设置图标，图片id即可
        List<String> packageSeleted = new ArrayList<>();

        final Ut.MyOnMultiChoiceClickListener myOnMultiChoiceClickListener=new Ut.MyOnMultiChoiceClickListener(
                jsonObject,items,selected
        );
        builder.setMultiChoiceItems(items, selected, myOnMultiChoiceClickListener);

        builder.setPositiveButton(PositiveButtonText, new DialogInterface.OnClickListener()  {

            @Override
            public void onClick(DialogInterface dialog, int which  ) {
                Ut.fileWriterTxt( pathJsonConfig, myOnMultiChoiceClickListener.jsonObjectListItems.toJSONString() );
                dialogCallBack.setPositiveButtonCallback(dialog,which);
                dialog.dismiss();
            }

        });

        AlertDialog alertDialo = builder.create();
        alertDialo.show();
    }

    @Override
    public void setPositiveButtonCallback(DialogInterface dialog, int which) {

    }

}
