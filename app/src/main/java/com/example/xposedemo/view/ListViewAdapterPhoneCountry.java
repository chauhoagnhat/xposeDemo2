package com.example.xposedemo.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.xposedemo.Hook.Hook;
import com.example.xposedemo.Hook.HookShare;
import com.example.xposedemo.R;
import com.example.xposedemo.utils.MyFile;
import com.example.xposedemo.utils.Ut;

import java.util.List;

public class ListViewAdapterPhoneCountry extends BaseAdapter {

    public JSONArray selectCountryJsonArray;
    final List<String> listCountry;
    Context context = null;
    private String TAG = ListViewAdapterPhoneCountry.class.getName();
    public TextView textViewPara;

    public ListViewAdapterPhoneCountry(List<String> listCountry, Context context) {

        this.listCountry = listCountry;
        this.context = context;

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

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.layout_listviewitem_phone_country
                    , parent
                    , false);
        }

        final TextView textView = convertView.findViewById(
                R.id.lvitem_tv_phone_country);
        final CheckBox checkBox = convertView.findViewById(
                R.id.checkbox_country);
        final Spinner spinner = convertView.findViewById(
                R.id.listview_phone_country_Spinner_idx);
         textViewPara=(TextView)convertView
        .findViewById(R.id.listview_phone_country_tv_showPara);

        if (listCountry.get(position) != null)
            textView.setText(listCountry.get(position));

        ArrayAdapter spinnerAdapter = null;

        //获取上次勾选设置
        final SharedPreferences sp = context
                .getSharedPreferences(HookShare.configPhoneCountryCode
                        , Context.MODE_PRIVATE);
        //String country=sp.getString(HookShare.configPhoneCountryCode,"");
        final String country = textView.getText().toString();
        Log.d(TAG, "getView: country=" + country);
        Log.d(TAG, "textView: country=" + textView.getText().toString());
        final String configSelect = "configSelect";

        final SharedPreferences.Editor editor = sp.edit();
        String textviewText = textView.getText().toString();
                if (sp.getBoolean(configSelect + country, false)){
                    if (!textviewText.equals("不改")) {
                        spinnerAdapter = spinnerSetting(convertView, country);
                    }else
                        MyFile.fileWriterTxt(HookShare.PATH_PHONE_DEVICE_DATA,"");
                    checkBox.setChecked(true);
                }
                 else {
//                if (sp.getBoolean(configSelect + country, false))
//                    checkBox.setChecked( true );
                    textViewPara.setText("");
                    notifyDataSetChanged();
                 }


        if (!textviewText.equals("不改") && spinnerAdapter != null) {
            spinnerOnSelected( context, spinner, country,convertView );
        }

//        //设置为上次选中的idx
//        if (position>-1){
//            editor.putString( checkBox.getText().toString()
//                    , String.valueOf(position) );
//        }

        final View finalConvertView = convertView;
        checkBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.d(TAG, "onClick: checkbox_country_click");
                if (checkBox.isChecked()) {

                    String country = textView.getText().toString();
                    if (country.equals("不改")) {
                        MyFile.fileWriterTxt(HookShare.PATH_PHONE_DEVICE_DATA
                                , "");
                        // spinnerOnSelected(context,spinner,"");
//                        editor.putString(
//                                HookShare.configPhoneCountryCode
//                                , textView.getText().toString()
                        editor.putBoolean(configSelect+country,true);
                        editor.commit();

                    } else {
//                        editor.putString(
//                                HookShare.configPhoneCountryCode
//                                , textView.getText().toString()
//                        );
                        editor.putBoolean(configSelect+country,true);
                        editor.commit();
                        //设置spinner
                        spinnerSetting(finalConvertView, country);
                        spinnerOnSelected(context, spinner, country,finalConvertView);
                    }

                } else {
//                        editor.putString(
//                                HookShare.configPhoneCountryCode, "" );
                        editor.putBoolean(configSelect+country,false);
                        editor.commit();
                        textViewPara=finalConvertView.findViewById(R.id.listview_phone_country_tv_showPara);
                        textViewPara.setText("");

//                    MyFile.fileWriterTxt( HookShare.PATH_PHONE_DEVICE_DATA
//                    ,"");

                }

            }

        });

        return convertView;


    }

    public void spinnerOnSelected(Context context, Spinner spinner, final String country
    , final View convertView) {
        SharedPreferences sp = context.getSharedPreferences(HookShare.configPhoneCountryCode, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sp.edit();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "onItemSelected: spinnerOnSelected");
                editor.putString(country, String.valueOf(position));
                editor.commit();
                JSONArray jsonArray = jsonParseMccMnc(country);
                JSONObject jsonObject = jsonArray.getJSONObject(position);
                textViewPara=convertView.findViewById(R.id.listview_phone_country_tv_showPara);
                textViewPara.setText( jsonObject.toJSONString() );
                writeMncMccJson(jsonObject);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d(TAG, "onItemSelected: onNothingSelected");
//                editor.putString(country, "0");
//                editor.commit();
            }
        });
    }

    public ArrayAdapter spinnerSetting(View convertView, String country) {

        //下拉框idx
        Spinner spinner = convertView.findViewById(
                R.id.listview_phone_country_Spinner_idx);

        //所以参数textview框
        TextView textViewShowPara = (TextView) convertView.findViewById(
                R.id.listview_phone_country_tv_showPara
        );

        //读取json数据,设置spinner
        String jsonMccmnc = MyFile.readFileToString(
                HookShare.pathNkFolderData
                        + "/mccmncJsonData"
        );
        Log.d(TAG, "spinnerSetting: json="+jsonMccmnc );
        SharedPreferences sp = context.getSharedPreferences(
                HookShare.configPhoneCountryCode, Context.MODE_PRIVATE
        );
        SharedPreferences.Editor editor = sp.edit();

        //层层解析json数据用于匹配adapter
        JSONObject jobjMccmnc = JSON.parseObject(jsonMccmnc);
        String jsonStringCountry = jobjMccmnc.getString(country);
        Log.d(TAG, "spinnerSetting: json=" + jsonMccmnc + ",country=" + country);
        selectCountryJsonArray = JSONArray
                .parseArray(jsonStringCountry);
        String[] arr = new String[
                selectCountryJsonArray.size()
                ];


        //设置adapter arr[]选项
        for (int i = 0; i < arr.length; i++) {
            JSONObject jobjFinal = selectCountryJsonArray
                    .getJSONObject(i);
            arr[i] = jobjFinal.getString(
                    "getNetworkOperatorName");
            Log.d(TAG, "onClick: json=" + arr[i]);
        }

        ArrayAdapter adapter = new ArrayAdapter<String>(
                convertView.getContext()
                , android.R.layout.simple_spinner_item
                , arr
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        String idx = sp.getString(country, "");
        if (!idx.equals("")) {
            spinner.setSelection(Integer.parseInt(idx));
        } else
            spinner.setSelection(0);

        editor.putString(country, Integer.toString(
                spinner.getSelectedItemPosition()
        ));
        editor.commit();

        //textview显示参数
        String jsonObjectPara = selectCountryJsonArray.get(spinner.getSelectedItemPosition()).toString();
        StringBuilder sb = new StringBuilder();
        JSONObject jsonObject = JSONObject.parseObject(jsonObjectPara);
        for (String str :
                jsonObject.keySet()) {
            sb.append(str);
            sb.append("=" + jsonObject.getString(str));
            sb.append("\r\n");
        }
        textViewShowPara.setText(sb.toString());
        return adapter;
    }

    public JSONArray jsonParseMccMnc(String country) {
        //读取json数据,设置spinner
        String jsonMccmnc = MyFile.readFileToString(
                HookShare.pathNkFolderData
                        + "/mccmncJsonData"
        );
        //层层解析json数据用于匹配adapter
        JSONObject jobjMccmnc = JSON.parseObject(jsonMccmnc);
        String jsonStringCountry = jobjMccmnc.getString(country);
        Log.d(TAG, "spinnerSetting: json=" + jsonMccmnc + ",country=" + country);
        selectCountryJsonArray = JSONArray
                .parseArray(jsonStringCountry);
        return selectCountryJsonArray;
    }


    public void writeMncMccJson(JSONObject jobjFinal) {

        JSONObject jobjRet = new JSONObject();
        String key = "mnc";
        jobjRet.put(key, jobjFinal.getString(key));
        key = "mcc";
        jobjRet.put(key, jobjFinal.getString(key));
        key = "getNetworkOperator";
        jobjRet.put(key, jobjFinal.getString(key));
        key = "getSimOperator";
        jobjRet.put(key, jobjFinal.getString(key));
        key = "getNetworkOperatorName";
        jobjRet.put(key, jobjFinal.getString(key));
        key = "getSimOperatorName";
        jobjRet.put(key, jobjFinal.getString(key));
        key = "getSimCountryIso";
        jobjRet.put(key, jobjFinal.getString(key));
        key = "getNetworkCountryIso";
        jobjRet.put(key, jobjFinal.getString(key));
        key = "getLine1Number";
        jobjRet.put(key, jobjFinal.getString(key) );
        Log.d(TAG, "writeMncMccJson: "+jobjRet.toJSONString() );

        MyFile.fileWriterTxt( HookShare.PATH_PHONE_DEVICE_DATA, jobjRet.toJSONString() );

    }

}
