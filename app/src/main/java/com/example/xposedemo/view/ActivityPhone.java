package com.example.xposedemo.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.xposedemo.R;

import java.util.ArrayList;
import java.util.List;

public class ActivityPhone extends AppCompatActivity {

    public List<String> listCountry=new ArrayList<>();
    public Context context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context=getApplicationContext();
        listCountry.add("美国");
        listCountry.add("xx");
        listCountry.add("x2");
        listCountry.add("x3");
        setContentView(R.layout.layout_phone );
        ListView listViewCountry=(ListView)findViewById(R.id.listview_phone_country );
        listViewCountry.setAdapter( new ListViewAdapterPhoneCountry(listCountry,context) );

    }
}
