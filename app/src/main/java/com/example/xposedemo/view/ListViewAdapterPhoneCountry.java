package com.example.xposedemo.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.xposedemo.Hook.HookShare;
import com.example.xposedemo.R;

import java.util.List;

public class ListViewAdapterPhoneCountry extends BaseAdapter {

    final List<String> listCountry;
    Context context=null;
    private String TAG=ListViewAdapterPhoneCountry.class.getName();

    public ListViewAdapterPhoneCountry(List<String> listCountry, Context context) {
        this.listCountry=listCountry;
        this.context=context;
    }

    @Override
    public int getCount() {
        return listCountry.size();
    }

    @Override
    public Object getItem(int position) {
        return listCountry.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if ( convertView==null )
            convertView= LayoutInflater.from(context).inflate(
                    R.layout.layout_listviewitem_phone_country ,parent,false);
        final TextView textView=convertView.findViewById(R.id.lvitem_tv_phone_country);

        final CheckBox checkBox=convertView.findViewById(R.id.checkbox_country);

        //获取上次勾选设置
        final SharedPreferences sp=context.getSharedPreferences(HookShare.configPhoneCountryCode
                ,Context.MODE_PRIVATE);

                if ( sp.getString(HookShare.configPhoneCountryCode,"")
                        .equals( textView.getText().toString() )){
                    checkBox.setChecked( true );
                }else
                    checkBox.setChecked( false );

            checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: checkbox_country_click");
                final SharedPreferences.Editor editor=sp.edit();
                if ( checkBox.isChecked() ){
                    editor.putString(HookShare.configPhoneCountryCode,textView.getText().toString());
                }else{
                    editor.putString(HookShare.configPhoneCountryCode, "" );
                }
                editor.commit();
                Log.d(TAG, "onClick: config="+sp.getString(HookShare.configPhoneCountryCode,"")  );
            }
        });


        if (listCountry.get(position)!=null)
             textView.setText(listCountry.get(position) );
        return convertView;


    }


}
