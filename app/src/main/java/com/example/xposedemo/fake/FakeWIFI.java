package com.example.xposedemo.fake;

import com.example.xposedemo.bean.WIFIInfo;
import com.example.xposedemo.utils.PhoneRndTools;

public class FakeWIFI {
    public static WIFIInfo makeWIFI(){
        WIFIInfo wifiInfo = new WIFIInfo();
        wifiInfo.setBssid(PhoneRndTools.randomMac());
        wifiInfo.setMacAddress(PhoneRndTools.randomMac1());
        wifiInfo.setIp(PhoneRndTools.randomIPNum());
        wifiInfo.setSsid("WIFI");
        wifiInfo.toString();
        return wifiInfo;
    }

}
