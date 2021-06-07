package com.example.xposedemo.utils;

import com.example.xposedemo.MyAbstract.MyAbstractCallBack;

public class MyRunnable implements Runnable {

    public MyAbstractCallBack myAbstractCallBack;

    public MyRunnable(MyAbstractCallBack myAbstractCallBack){
        this.myAbstractCallBack=myAbstractCallBack;
    }

    @Override
    public void run() {
        myAbstractCallBack.threadRun();
    }

}
