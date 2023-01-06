package com.example.xposedemo.bean;

import android.app.Activity;
import android.content.Context;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivityData {

    private Context context;
    private TextView logTextview;
    private String functionPackageName;
    private Context activityContext;
    private EditText et_path;

    public Activity getActivity() {
        return activity;
    }
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    private Activity activity;


    public EditText getEt_path() {
        return et_path;
    }
    public void setEt_path(EditText et_path) {
        this.et_path = et_path;
    }

    public Context getActivityContext() {
        return activityContext;
    }

    public void setActivityContext( Context activityContext ) {
        this.activityContext = activityContext;
    }

    public String getFunctionPackageName() {
        return functionPackageName;
    }

    public void setFunctionPackageName( String functionPackageName ) {
        this.functionPackageName = functionPackageName;
    }

    public Context getContext() {
        return context;
    }
    public void setContext(Context context) {
        this.context = context;
    }
    public TextView getLogTextview() {
        return logTextview;
    }
    public void setLogTextview(TextView logTextview) {
        this.logTextview = logTextview;
    }

}