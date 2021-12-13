package com.example.xposedemo.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.xposedemo.R;

import java.util.List;

public class ListViewAdapterPhoneCountry extends BaseAdapter {

    final List<String> listCountry;
    Context context=null;
    public ListViewAdapterPhoneCountry(List<String> listCountry, Context context) {
        this.listCountry=listCountry;
        this.context=context;
    }

    @Override
    public int getCount() {
        return listCountry.size()+1;
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
        TextView textView=convertView.findViewById(R.id.lvitem_tv_phone_country);
        if (listCountry.get(position)!=null)
             textView.setText(listCountry.get(position) );
        return convertView;
    }
}
