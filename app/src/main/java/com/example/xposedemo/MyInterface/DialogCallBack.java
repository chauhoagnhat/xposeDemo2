package com.example.xposedemo.MyInterface;

import android.content.DialogInterface;

import com.example.xposedemo.utils.MyUi;

public interface DialogCallBack    {
    public void setPositiveButtonCallback(DialogInterface dialog, int which  );
    public void SetNegativeButtonCallback(DialogInterface dialog, int which  );
}
