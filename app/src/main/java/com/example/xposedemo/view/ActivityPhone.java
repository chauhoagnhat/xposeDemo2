package com.example.xposedemo.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.xposedemo.R;
import com.example.xposedemo.view.data.PhoneInfo;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ActivityPhone extends AppCompatActivity {

    private static final String TAG = "ActivityPhone" ;
    public List<String> listCountry=new ArrayList<>();
    public Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context=getApplicationContext();
        listCountry.add("不改");
        listCountry.add("hk");
        listCountry.add("us");
        setContentView(R.layout.layout_phone );

        ListView listViewCountry=(ListView)findViewById( R.id.listview_phone_country );
        listViewCountry.setAdapter( new ListViewAdapterPhoneCountry( listCountry,context) );
        Method method=null;

        try {

            TelephonyManager telephonyManager=
                    (TelephonyManager)getSystemService( TELEPHONY_SERVICE );
            Class clazz=Class.forName( "android.telephony.TelephonyManager" );

            String methodName="getNetworkOperatorName";
            method=clazz.getDeclaredMethod( methodName );
            Log.d(TAG, "onCreate: telephony methodName="+methodName+",result="+method.invoke( telephonyManager )   );
            PhoneInfo phoneInfo=new PhoneInfo();

//            String methodName="getSimOperatorNameForPhone";
//            method=clazz.getDeclaredMethod( methodName );
//            method.setAccessible(true);
//            phoneInfo.setGetSimOperatorName( (String) method.invoke(telephonyManager) );

 /*           String methodName="getSimOperatorNameForPhone";
            method=clazz.getDeclaredMethod (methodName,int.class );
            method.setAccessible(true);
            Object result=method.invoke( telephonyManager,0 );
            Log.d( methodName, "onCreate: ="+result );
            phoneInfo.setGetSimOperatorName( (String)result );*/

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }


    }
}
