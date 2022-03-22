package com.example.xposedemo.view.packageShow;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.xposedemo.R;

import java.util.ArrayList;
import java.util.List;

public class Activity_packageShow extends AppCompatActivity implements AdapterView.OnItemLongClickListener {

    private ListView lv_package_show;
    private List<AppInfo> data;
    private AppAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_show);
        //初始化成员变量
        lv_package_show = (ListView) findViewById( R.id.lv_package_show );
        data = getAllAppInfos();
        adapter = new AppAdapter( Activity_packageShow.this,data );
        //显示列表
        lv_package_show.setAdapter(adapter);

        //给ListView设置item的点击监听
        lv_package_show.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            /**
             * parent : ListView
             * view : 当前行的item视图对象
             * position : 当前行的下标
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //提示当前行的应用名称
                String appName = data.get(position).getAppName();
                //提示
                Toast.makeText(Activity_packageShow.this, appName, 0).show();
            }
        });

        //给LitView设置Item的长按监听
        lv_package_show.setOnItemLongClickListener(this);

    }


    /*
     * 获得手机中全部应用信息的列表
     * AppInfo
     *  Drawable icon  图片对象
     *  String appName
     *  String packageName
     */
    protected List<AppInfo> getAllAppInfos() {

        List<AppInfo> list = new ArrayList<AppInfo>();
        // 获得应用的packgeManager
        PackageManager packageManager = getPackageManager();
        // 建立一个主界面的intent
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        // 获得包含应用信息的列表
        List<ResolveInfo> ResolveInfos = packageManager.queryIntentActivities(
                intent, 0);
        // 遍历
        for (ResolveInfo ri : ResolveInfos) {
            // 获得包名
            String packageName = ri.activityInfo.packageName;
            // 获得图标
            Drawable icon = ri.loadIcon(packageManager);
            // 获得应用名称
            String appName = ri.loadLabel(packageManager).toString();
            // 封装应用信息对象
            AppInfo appInfo = new AppInfo(icon, appName, packageName);
            // 添加到list
            list.add(appInfo);
        }
        return list;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

}