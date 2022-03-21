package com.example.xposedemo.view.packageShow;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.xposedemo.MainActivity;
import com.example.xposedemo.R;

import java.util.List;

public class AppAdapter extends BaseAdapter {

    Context mContext;
    private List<AppInfo> data;

    public AppAdapter(Context context) {
        mContext=context;
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
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

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

        //返回convertView
        return convertView;
    }
}
