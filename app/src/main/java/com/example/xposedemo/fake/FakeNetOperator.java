package com.example.xposedemo.fake;

import com.example.xposedemo.bean.NetOperatorInfo;
import com.example.xposedemo.utils.PhoneRndTools;

public class FakeNetOperator {
    public static NetOperatorInfo makeNetOperator(){
        NetOperatorInfo netOperatorInfo = new NetOperatorInfo();
        netOperatorInfo.setPhoneNum(PhoneRndTools.randomPhoneNum());
        netOperatorInfo.setPhoneCardNum(PhoneRndTools.randomNum(20));
        netOperatorInfo.setNetwrokOperator("46001");
        netOperatorInfo.setCarrier("中国电信");
        netOperatorInfo.setNationalISONum("cn");
        netOperatorInfo.setCountryCode("cn");
        netOperatorInfo.setType("1");
        netOperatorInfo.setNetType("6");
        netOperatorInfo.setPhoneType("5");
        netOperatorInfo.setSimState("10");
        netOperatorInfo.setDeviceversion("100");
        return netOperatorInfo;
    }
}
