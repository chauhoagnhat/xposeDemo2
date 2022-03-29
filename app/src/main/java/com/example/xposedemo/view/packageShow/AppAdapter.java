package com.example.xposedemo.view.packageShow;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.xposedemo.Hook.HookShare;
import com.example.xposedemo.MainActivity;
import com.example.xposedemo.R;
import com.example.xposedemo.utils.MyFile;
import com.example.xposedemo.utils.Ut;

import java.util.ArrayList;
import java.util.List;

public class AppAdapter extends BaseAdapter {

    private static final String TAG = ClassLoader.class.getName();
    Context mContext;
    private List<AppInfo> data;
    public JSONObject jobjSelectedPackages;

    private final static int FIRST_RUN=0;
    //private final  static int firstRunOver=1;
    private static int  runTimes;

    public AppAdapter(Context context,List<AppInfo> data) {
        mContext=context;
        this.data=data;

        List<String>listStringPackages;
        List<Boolean> listBooleanPackages=new ArrayList<>();
        String jsonTxtPackages= MyFile.readFileToString( HookShare.pathPackages );
        final JSONObject jsonObject;
        //Log.d(TAG, "dialogSelectPackageName: jsonTxtPackages="+jsonTxtPackages );
        Log.d(TAG, "dialogSelectPackageName: constructor run" );

        runTimes=FIRST_RUN;

        //包名写本地初始化
        if ( jsonTxtPackages==null||jsonTxtPackages.equals("")) {
            {
                Log.d(TAG, "dialogSelectPackageName: first run");
                jsonObject = new JSONObject();
                //all packageName option put into listBooleanPackages
                for (AppInfo appInfo : data ) {
                    jsonObject.put( appInfo.getPackageName(), false );
                    listBooleanPackages.add( false );
                }
                Ut.fileWriterTxt( HookShare.pathPackages, jsonObject.toJSONString() );
            }
        }else {
            Log.d(TAG, "AppAdapter: get-jobjSelectedPackages" );

        }

    }


    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    //返回带数据当前行的Item视图对象
    @SuppressLint("ResourceType")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Log.d(TAG, "getView: run.");
        //1. 若是convertView是null, 加载item的布局文件
        if(convertView==null) {
            Log.e("TAG", "getView() load layout");
            convertView = View.inflate(mContext, R.layout.layout_listviewitem_packageshow , null);
        }

        //2. 获得当前行数据对象
        AppInfo appInfo = data.get(position);
        //3. 获得当前行须要更新的子View对象
        ImageView imageView = (ImageView) convertView.findViewById(R.id.iv_item_icon);
        TextView textView = (TextView) convertView.findViewById(R.id.tv_item_name);
        //4. 给视图设置数据
        imageView.setImageDrawable(appInfo.getIcon());
        textView.setText(appInfo.getAppName());
        String packageName=appInfo.getPackageName();
        //CheckBox checkBox=(CheckBox)convertView.findViewById( R.id.ck_package );
        Log.d(TAG, "getView: runtime="+runTimes );
        //if (runTimes==FIRST_RUN){
            runTimes++;
            jobjSelectedPackages = HookShare.returnSelectedPackages();
            if (jobjSelectedPackages!=null){
                if ( jobjSelectedPackages.containsKey( packageName ) ){
                    textView.setTextColor ( Color.WHITE );
                    convertView.setBackgroundColor(Color.BLUE);
                    //checkBox.setChecked(true);
                }else{
                    //checkBox.setChecked(false);
                    textView.setTextColor ( Color.BLACK );
                    convertView.setBackgroundColor(Color.WHITE);
                }
            }

        //}

        //返回convertView
        return convertView;
    }
}
