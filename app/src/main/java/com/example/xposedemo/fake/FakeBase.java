package com.example.xposedemo.fake;


import com.example.xposedemo.bean.BaseInfo;
import com.example.xposedemo.utils.PhoneRndTools;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class FakeBase {

    public static BaseInfo getInstance(){

        BaseInfo baseInfo = new BaseInfo();
       // baseInfo.setImei( PhoneRndTools.randomNum(15) );
        baseInfo.setImei( PhoneRndTools.randomImei()  );
        baseInfo.setNumber(PhoneRndTools.randomPhoneNum());
        baseInfo.setSimserial(PhoneRndTools.randomNum(20));
        baseInfo.setWifimac(PhoneRndTools.randomMac());
        baseInfo.setBluemac(PhoneRndTools.randomMac1());
        baseInfo.setAndroid_id(PhoneRndTools.randomABC(16));
        baseInfo.setSerial(PhoneRndTools.randomNum(19) + "a");
        baseInfo.setBrand("google");
        //8996-130091-1802061512
        String baseBand= PhoneRndTools.setSystemBaseband() ;
        baseInfo.setBaseBand( baseBand  );
        baseInfo.setRadioVersion( baseBand );
        baseInfo.setBoard("sailfish");
        baseInfo.setCpu_abi("arm64-v8a");
        baseInfo.setCpu_abi2("");
        baseInfo.setDevice("sailfish");
        baseInfo.setDisplay("OPM4.171019.021.P1");
        baseInfo.setFingerprint("google/sailfish/sailfish:8.1.0/OPM4.171019.021.P1/4820305:user/release-keys");
        baseInfo.setHardware("sailfish");
        baseInfo.setId("OPM4.171019.021.P1");
        baseInfo.setManufacturer("Google");
        baseInfo.setModel("Pixel");
        baseInfo.setProduct("sailfish");
        baseInfo.setBootloader("8996-012001-1711291800");
        baseInfo.setHost("wphs7.hot.corp.google.com");
        baseInfo.setTags("release-keys");
        baseInfo.setType("user");
        baseInfo.setIncremental("4820305");
        baseInfo.setRelease("8.1.0");
        baseInfo.setSdk("27");
        baseInfo.setCodename("REL");
        baseInfo.setBuildtime("15281562"+PhoneRndTools.randomNum(7) );
        baseInfo.setDiscription("sailfish-user 8.1.0 OPM4.171019.021.P1 4820305 release-keys");
        //XposedBridge.log( baseInfo.toString() );
        return baseInfo;

    }

}
