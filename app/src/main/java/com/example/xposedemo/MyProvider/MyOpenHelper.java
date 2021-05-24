package com.example.xposedemo.MyProvider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MyOpenHelper extends SQLiteOpenHelper {

    public final String TAG="MyOpenHelper";
    public String jsonStr;

    public MyOpenHelper(Context context) {
        super(context, "po.db", null, 1);
        Log.d(TAG, "MyOpenHelper: cons-run");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //第一次创建数据库时调用  SQLiteDatabase是操作数据库的类，实现增删改查
        //创建expressinfo表
        String sql="create table if not exists base(id integer Primary key autoincrement,  k varchar(20),v varchar(200) )";
        db.execSQL(sql);
        sql="create table if not exists phone(id integer Primary key autoincrement,  k varchar(20),v varchar(200) )";
        db.execSQL(sql);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

}