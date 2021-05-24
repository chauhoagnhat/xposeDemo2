package com.example.xposedemo.MyProvider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyProvider  extends ContentProvider {

    private MyOpenHelper openHelper;
    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int QUERY_SUCESS = 0;
    private static final int INSERT_MATCHED = 1;
    private static final int UPDATE_MATCHED = 2;
    private static final int DELETE_MATCHED = 3;


    static
    {
        //给当前的URI匹配器添加一个匹配规则
        sURIMatcher.addURI("com.example.xposedemo.MyProvider", "query", QUERY_SUCESS);
        sURIMatcher.addURI("com.example.xposedemo.MyProvider", "insert", INSERT_MATCHED);
        sURIMatcher.addURI("com.example.xposedemo.MyProvider", "update", UPDATE_MATCHED);
        sURIMatcher.addURI("com.example.xposedemo.MyProvider", "delete", DELETE_MATCHED);
        //sURIMatcher.addURI("com.itheima.provider", "student", 5);
    }

    @Override
    public boolean onCreate() {
        openHelper= new MyOpenHelper ( getContext()) ;
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //使用URI匹配器 进行URI匹配的判断 如果匹配上了 返回正确的结果 如果没有匹配上抛出异常
        int result = sURIMatcher.match(uri);
        if(result == QUERY_SUCESS){
            SQLiteDatabase db = openHelper.getReadableDatabase();
            Cursor cursor = db.query("info", projection, selection, selectionArgs, null, null, sortOrder);
            return cursor;
        }else{
            throw new IllegalStateException("口令错误");
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        int result = sURIMatcher.match(uri);
        if(result==INSERT_MATCHED){
            SQLiteDatabase db = openHelper.getReadableDatabase();
            long insert = db.insert("info", null, values);
            //可以通过内容解析者发出一个数据变化的通知
            //第一个参数 uri notifyChange 之后 通过这个uri来判断 改调用哪个内容结观察者
            //第二个参数  内容观察者对象 如果传null 注册了这个uri的所有观察者都能收到通知
            getContext().getContentResolver().notifyChange(uri, null);
            return Uri.parse(String.valueOf(insert));
        }else{
            return null;
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int result = sURIMatcher.match(uri);
        if(result==DELETE_MATCHED){
            SQLiteDatabase db = openHelper.getReadableDatabase();
            int delete = db.delete("info", selection, selectionArgs);
            return delete;
        }else{
            return -1;
        }

    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int result = sURIMatcher.match(uri);
        if(result==UPDATE_MATCHED){
            SQLiteDatabase db = openHelper.getReadableDatabase();
            int update = db.update("info", values, selection, selectionArgs);
            db.close();
            return update;
        }else{
            return -1;
        }
    }


}
