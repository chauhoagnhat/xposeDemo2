package com.example.xposedemo.fake;


import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.xposedemo.bean.BaseInfo;
import com.example.xposedemo.utils.MyFile;
import com.example.xposedemo.utils.PhoneRndTools;
import com.example.xposedemo.utils.Ut;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class FakeBase {

    private static final String TAG ="FakeBase" ;
    public static final BaseInfo baseInfo = new BaseInfo();

    public static BaseInfo lg_device(){

        BaseInfo baseInfo = new BaseInfo();
        //baseInfo.setImei( PhoneRndTools.randomNum(15) );
        baseInfo.setImei( PhoneRndTools.randomImei()  );
        baseInfo.setNumber( PhoneRndTools.randomPhoneNum() );
        baseInfo.setSimserial( PhoneRndTools.randomNum(20) );
        baseInfo.setWifimac( PhoneRndTools.randomMac() );
        baseInfo.setBluemac( PhoneRndTools.randomMac1() );
        baseInfo.setAndroid_id( PhoneRndTools.randomABC(16) );
        baseInfo.setSerial( PhoneRndTools.randomNum(19) + "a" );
        baseInfo.setBrand("lge");
        //8996-130091-1802061512
        String baseBand= PhoneRndTools.setSystemBaseband() ;
        baseInfo.setBaseBand( baseBand  );
        baseInfo.setRadioVersion( baseBand );
        baseInfo.setBoard( "msm8998" );
        baseInfo.setCpu_abi( "arm64-v8a" );
        //baseInfo.setCpu_abi2("");
        baseInfo.setDevice("joan");
        baseInfo.setDisplay("PKQ1.190414.001");
        //baseInfo.setDisplay("OPM4.181019.012.P1");
        baseInfo.setFingerprint("lge/joan_nao_us/joan:9/PKQ1.190414.001/192451445de41:user/release-keys");
        //baseInfo.setFingerprint("google/sailfish/sailfish:8.1.0/OPM4.171019.021.P1/4810102:user/release-keys");
        baseInfo.setHardware("joan");
        baseInfo.setId("PKQ1.190414.001");
        baseInfo.setManufacturer("LGE");
        baseInfo.setModel("US998");
        baseInfo.setProduct("joan");
        baseInfo.setBootloader("unknown");
        //baseInfo.setBootloader("8996-012001-1711292200");
        baseInfo.setHost("LGEACI5R4");
        baseInfo.setTags("release-keys");
        baseInfo.setType("user");
        baseInfo.setIncremental("192451445de41");
        //baseInfo.setIncremental("4810102");
        baseInfo.setRelease("9");
        baseInfo.setSdk("28");
        baseInfo.setCodename("REL");
        baseInfo.setBuildtime("15281461"+PhoneRndTools.randomNum(7) );
        baseInfo.setDiscription("lge/joan_nao_us/joan:9/PKQ1.190414.001/192451445de41:user/release-keys");
        //baseInfo.setDiscription("sailfish-user 8.1.0 OPM4.171019.021.P1 4810102 release-keys");
        //XposedBridge.log( baseInfo.toString() );
        return baseInfo;

    }

    public static BaseInfo getInstance(){

        BaseInfo baseInfo = new BaseInfo();
        //baseInfo.setImei( PhoneRndTools.randomNum(15) );
        baseInfo.setImei( PhoneRndTools.randomImei()  );
        baseInfo.setNumber( PhoneRndTools.randomPhoneNum() );
        baseInfo.setSimserial( PhoneRndTools.randomNum(20));
        baseInfo.setWifimac( PhoneRndTools.randomMac());
        baseInfo.setBluemac( PhoneRndTools.randomMac1());
        baseInfo.setAndroid_id( PhoneRndTools.randomABC(16));
        baseInfo.setSerial(PhoneRndTools.randomNum(19) + "a");
        baseInfo.setBrand("google");
        //baseInfo.setBrand("Sumsung");
        //8996-130091-1802061512
        String baseBand= PhoneRndTools.setSystemBaseband() ;
        baseInfo.setBaseBand( baseBand  );
        baseInfo.setRadioVersion( baseBand );
        baseInfo.setBoard("sailfish");
        baseInfo.setCpu_abi("arm64-v8a");
        //baseInfo.setCpu_abi2("");
        baseInfo.setDevice("sailfish");
        baseInfo.setDisplay("OPM4.171019.021.P1");
        //baseInfo.setDisplay("OPM4.181019.012.P1");
        baseInfo.setFingerprint("google/sailfish/sailfish:8.1.0/OPM4.171019.021.P1/4820305:user/release-keys");
//        baseInfo.setFingerprint("google/sailfish/sailfish:8.1.0/OPM4.171019.021.P1/4810102:user/release-keys");
        baseInfo.setHardware("sailfish");
        baseInfo.setId("OPM4.171019.021.P1");
        baseInfo.setManufacturer("Google");
        baseInfo.setModel("Pixel");
        baseInfo.setProduct("sailfish");
        baseInfo.setBootloader("8996-012001-1711291800");
        //baseInfo.setBootloader("8996-012001-1711292200");
        baseInfo.setHost("wphs7.hot.corp.google.com");
        baseInfo.setTags("release-keys");
        baseInfo.setType("user");
        baseInfo.setIncremental("4820305");
        //baseInfo.setIncremental("4810102");
        baseInfo.setRelease("8.1.0");
        baseInfo.setSdk("27");
        baseInfo.setCodename("REL");
        baseInfo.setBuildtime("15281562"+PhoneRndTools.randomNum(7) );
        baseInfo.setDiscription("sailfish-user 8.1.0 OPM4.171019.021.P1 4820305 release-keys");

        //baseInfo.setDiscription("sailfish-user 8.1.0 OPM4.171019.021.P1 4810102 release-keys");
        //XposedBridge.log( baseInfo.toString() );

        return oppo_device();

        //return Sumsung_device();
        //return baseInfo;
        //return lg_device() ;

    }

    public static BaseInfo randomDevice(Context context){

        List<String> listModel= MyFile.readAssetsLines(context,"model.txt");
        int idx= Ut.r_( 0,listModel.size() );
        String DeviceString=listModel.get( idx );
        String[] deviceArr=DeviceString.split(",");

        String brand=deviceArr[0];
        String markedName=deviceArr[1];
        String device=deviceArr[2];
        String model=deviceArr[3];

        Log.d(TAG, "RandomDevice: brand="+brand );
        Log.d(TAG, "RandomDevice: MarkedName="+markedName );
        Log.d(TAG, "RandomDevice: device="+device );
        Log.d(TAG, "RandomDevice: model="+model );

        //baseInfo.setImei( PhoneRndTools.randomNum(15) );
        baseInfo.setImei( PhoneRndTools.randomImei()  );
        baseInfo.setNumber( PhoneRndTools.randomPhoneNum() );
        baseInfo.setSimserial( PhoneRndTools.randomNum(20));
        baseInfo.setWifimac( PhoneRndTools.randomMac());
        baseInfo.setBluemac( PhoneRndTools.randomMac1());
        baseInfo.setAndroid_id( PhoneRndTools.randomABC(16));
        baseInfo.setSerial(PhoneRndTools.randomNum(19) + "a");
        baseInfo.setBrand( brand );
        //baseInfo.setBrand("Sumsung");
        //8996-130091-1802061512
        String baseBand= PhoneRndTools.setSystemBaseband() ;
        baseInfo.setBaseBand( baseBand  );
        baseInfo.setRadioVersion( baseBand );
        baseInfo.setBoard( device  );
        baseInfo.setCpu_abi("arm64-v8a");
        //baseInfo.setCpu_abi2("");
        baseInfo.setDevice( device );
        String display=Ut.rLetterCapital( 3 )+Ut.r_(1,9)+".1"+Ut.r_num_n(1,5,9)
                +Ut.r_num_n(1,1001,1231)+".0"+Ut.r_num_n(1,10,23)
                +"."+Ut.rLetterCapital(1)+Ut.r_(1,9);
        Log.d(TAG, "randomDevice: display="+display);
       // baseInfo.setDisplay("OPM4.171019.021.P1");
        baseInfo.setDisplay(display);

        String[] sdkArr=getSdkVer();
        String sdkNum=sdkArr[0];
        String sdkVer=sdkArr[1];

        Log.d(TAG, "randomDevice: sdknum,sdkver="+sdkNum+","+sdkVer );

        String fingerprint=brand+"/"+device+"/"+device+":"+sdkVer+"/"+display+"/"+Ut.r_num_n(7,0,9)
                +":user/release-keys";
        Log.d(TAG, "randomDevice: fingerprint"+fingerprint);
        //baseInfo.setFingerprint("google/sailfish/sailfish:8.1.0/OPM4.171019.021.P1/4820305:user/release-keys");
        baseInfo.setFingerprint(fingerprint);
        //baseInfo.setHardware("sailfish");
        baseInfo.setHardware(device);
        //baseInfo.setId("OPM4.171019.021.P1");
        baseInfo.setId(display);

       // baseInfo.setManufacturer("Google");
        baseInfo.setManufacturer( brand );

       // baseInfo.setModel( "Pixel" );
        baseInfo.setModel( markedName );

       // baseInfo.setProduct("sailfish");
        baseInfo.setProduct( device );

        String bootLoader="8996-"+Ut.r_num_n(6,0,2)+"-"+Ut.r_num_n(1,15,19)
                +Ut.r_num_n(1,10,12)+Ut.r_num_n(1,10,30)+Ut.r_num_n(1,10,24)
                +"00";
        Log.d(TAG, "randomDevice: bootLoader="+bootLoader);
        //baseInfo.setBootloader("8996-012001-1711291800");
        baseInfo.setBootloader( bootLoader );
        int num2=Ut.r_(1,3);
        int num1=Ut.r_(1,4);
        String host=Ut.rLetterN(num1)+brand+Ut.rLetterN(num2);
        Log.d(TAG, "randomDevice: host="+host);
        //baseInfo.setHost("wphs7.hot.corp.google.com");
        baseInfo.setHost(host);
        baseInfo.setTags("release-keys");
        baseInfo.setType("user");
        String incremental=Ut.r_num_n(7,0,9);

        //baseInfo.setIncremental("4820305");
        baseInfo.setIncremental(incremental);
        Log.d(TAG, "randomDevice: incremental"+incremental);
        //baseInfo.setIncremental("4810102");
        //baseInfo.setRelease("8.1.0");
        baseInfo.setRelease( sdkVer );
        //baseInfo.setSdk("27");
        baseInfo.setSdk( sdkNum );
        baseInfo.setCodename("REL");
        String buildtime="15"+Ut.r_num_n(6,0,9)+PhoneRndTools.randomNum(7);
        Log.d(TAG, "randomDevice: buildtime"+buildtime );
       // baseInfo.setBuildtime("15281562"+PhoneRndTools.randomNum(7) );
        baseInfo.setBuildtime(buildtime);

        String discription=device+"-user "+sdkVer+" "+display+" "+incremental+" "+"release-keys";
        Log.d(TAG, "randomDevice: discription="+discription );

        //baseInfo.setDiscription("sailfish-user 8.1.0 OPM4.171019.021.P1 4820305 release-keys");
        baseInfo.setDiscription( discription );
        //baseInfo.setDiscription("sailfish-user 8.1.0 OPM4.171019.021.P1 4810102 release-keys");
        //XposedBridge.log( baseInfo.toString() );

        return baseInfo;

    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder stringBuilder=new StringBuilder();

        stringBuilder.append("getImei=");
        stringBuilder.append( baseInfo.getImei() );
        stringBuilder.append("\r\n");

        stringBuilder.append("getNumber=");
        stringBuilder.append( baseInfo.getNumber() );
        stringBuilder.append("\r\n");

        stringBuilder.append("getSimserial=");
        stringBuilder.append( baseInfo.getSimserial());
        stringBuilder.append("\r\n");

        stringBuilder.append("getWifimac=");
        stringBuilder.append( baseInfo.getWifimac());
        stringBuilder.append("\r\n");

        stringBuilder.append("getBluemac=");
        stringBuilder.append( baseInfo.getBluemac());
        stringBuilder.append("\r\n");

        stringBuilder.append("getAndroid_id=");
        stringBuilder.append( baseInfo.getAndroid_id());
        stringBuilder.append("\r\n");

        stringBuilder.append("getSerial=");
        stringBuilder.append( baseInfo.getSerial());
        stringBuilder.append("\r\n");

        stringBuilder.append("getBrand=");
        stringBuilder.append( baseInfo.getBrand());
        stringBuilder.append("\r\n");

        stringBuilder.append("getBaseBand=");
        stringBuilder.append( baseInfo.getBaseBand());
        stringBuilder.append("\r\n");

        stringBuilder.append("getRadioVersion=");
        stringBuilder.append( baseInfo.getRadioVersion());
        stringBuilder.append("\r\n");

        stringBuilder.append("getBoard=");
        stringBuilder.append( baseInfo.getBoard());
        stringBuilder.append("\r\n");

        stringBuilder.append("getCpu_abi=");
        stringBuilder.append( baseInfo.getCpu_abi());
        stringBuilder.append("\r\n");

        stringBuilder.append("getCpu_abi2=");
        stringBuilder.append( baseInfo.getCpu_abi2());
        stringBuilder.append("\r\n");

        stringBuilder.append("getDevice=");
        stringBuilder.append( baseInfo.getDevice());
        stringBuilder.append("\r\n");

        stringBuilder.append("getDisplay=");
        stringBuilder.append( baseInfo.getDisplay());
        stringBuilder.append("\r\n");

        stringBuilder.append("getSdk=");
        stringBuilder.append( baseInfo.getSdk());
        stringBuilder.append("\r\n");

        stringBuilder.append("getFingerprint=");
        stringBuilder.append( baseInfo.getFingerprint());
        stringBuilder.append("\r\n");

        stringBuilder.append("getHardware=");
        stringBuilder.append( baseInfo.getHardware());
        stringBuilder.append("\r\n");

        stringBuilder.append("getId=");
        stringBuilder.append( baseInfo.getId());
        stringBuilder.append("\r\n");

        stringBuilder.append("getManufacturer=");
        stringBuilder.append( baseInfo.getManufacturer());
        stringBuilder.append("\r\n");

        stringBuilder.append("getModel=");
        stringBuilder.append( baseInfo.getModel());
        stringBuilder.append("\r\n");

        stringBuilder.append("getProduct=");
        stringBuilder.append( baseInfo.getProduct());
        stringBuilder.append("\r\n");

        stringBuilder.append("getBootloader=");
        stringBuilder.append( baseInfo.getBootloader());
        stringBuilder.append("\r\n");

        stringBuilder.append("getHost=");
        stringBuilder.append( baseInfo.getHost());
        stringBuilder.append("\r\n");

        stringBuilder.append("getTags=");
        stringBuilder.append( baseInfo.getTags());
        stringBuilder.append("\r\n");

        stringBuilder.append("getIncremental=");
        stringBuilder.append( baseInfo.getIncremental());
        stringBuilder.append("\r\n");

        stringBuilder.append("getRelease=");
        stringBuilder.append( baseInfo.getRelease());
        stringBuilder.append("\r\n");

        stringBuilder.append("getRelease=");
        stringBuilder.append( baseInfo.getRelease());
        stringBuilder.append("\r\n");

        stringBuilder.append("getCodename=");
        stringBuilder.append( baseInfo.getCodename());
        stringBuilder.append("\r\n");

        stringBuilder.append("getBuildtime=");
        stringBuilder.append( baseInfo.getBuildtime());
        stringBuilder.append("\r\n");

        stringBuilder.append("getDiscription=");
        stringBuilder.append( baseInfo.getDiscription());
        stringBuilder.append("\r\n");

        stringBuilder.append("getDiscription=");
        stringBuilder.append( baseInfo.getDiscription());
        stringBuilder.append("\r\n");


        return stringBuilder.toString();
    }

    public static String[] getSdkVer(){

        Map<String,String> mapSdkVer=new HashMap<>();
        mapSdkVer.put("25","7.12");
        mapSdkVer.put("26","8.0");
        mapSdkVer.put("27","8.1.0");
        mapSdkVer.put("28","9.0");
        mapSdkVer.put("29","10.0");

        String[] keys = mapSdkVer.keySet().toArray(new String[0]);
        Random random = new Random();
        String randomKey = keys[random.nextInt(keys.length)];
        String randomValue = mapSdkVer.get(randomKey);

        return  new String[]{ randomKey,randomValue };

      //  原文链接：https://blog.csdn.net/weixin_29116603/article/details/114541209
    }

    public static BaseInfo Sumsung_device(){

        BaseInfo baseInfo = new BaseInfo();
        //baseInfo.setImei( PhoneRndTools.randomNum(15) );
        baseInfo.setImei( PhoneRndTools.randomImei()  );
        baseInfo.setNumber( PhoneRndTools.randomPhoneNum() );
        baseInfo.setSimserial(PhoneRndTools.randomNum(20));
        baseInfo.setWifimac(PhoneRndTools.randomMac());
        baseInfo.setBluemac(PhoneRndTools.randomMac1());
        baseInfo.setAndroid_id(PhoneRndTools.randomABC(16));
        baseInfo.setSerial(PhoneRndTools.randomNum(19) + "a");
        baseInfo.setBrand("Sumsung");
        //8996-130091-1802061512
        String baseBand= PhoneRndTools.setSystemBaseband() ;
        baseInfo.setBaseBand( baseBand  );
        baseInfo.setRadioVersion( baseBand );
        baseInfo.setBoard("SHW-M485W");
        baseInfo.setCpu_abi("arm64-v8a");
        //baseInfo.setCpu_abi2("");
        baseInfo.setDevice("p4notewifiktt");
        baseInfo.setDisplay("PKQ1.190414.001");
        //baseInfo.setDisplay("OPM4.181019.012.P1");
        baseInfo.setFingerprint("lge/joan_nao_us/joan:9/PKQ1.190414.001/192451445de41:user/release-keys");
        //baseInfo.setFingerprint("google/sailfish/sailfish:8.1.0/OPM4.171019.021.P1/4810102:user/release-keys");
        baseInfo.setHardware("joan");
        baseInfo.setId("PKQ1.190414.001");
        baseInfo.setManufacturer("Sumsung");
        baseInfo.setModel("SHW-M485W");
        baseInfo.setProduct("joan");
        baseInfo.setBootloader("unknown");
        //baseInfo.setBootloader("8996-012001-1711292200");
        baseInfo.setHost("LGEACI5R4");
        baseInfo.setTags("release-keys");
        baseInfo.setType("user");
        baseInfo.setIncremental( "192451445de41" );
        //baseInfo.setIncremental("4810102");
        baseInfo.setRelease("8");
        baseInfo.setSdk("27");
        baseInfo.setCodename("REL");
        baseInfo.setBuildtime("15281461"+PhoneRndTools.randomNum(7) );
        baseInfo.setDiscription("lge/joan_nao_us/joan:9/PKQ1.190414.001/192451445de41:user/release-keys");
        //baseInfo.setDiscription("sailfish-user 8.1.0 OPM4.171019.021.P1 4810102 release-keys");
        //XposedBridge.log( baseInfo.toString() );
        return baseInfo;

    }


    public static BaseInfo oppo_device(){

        BaseInfo baseInfo = new BaseInfo();
        //baseInfo.setImei( PhoneRndTools.randomNum(15) );
        baseInfo.setImei( PhoneRndTools.randomImei()  );
        baseInfo.setNumber( PhoneRndTools.randomPhoneNum() );
        baseInfo.setSimserial(PhoneRndTools.randomNum(20));
        baseInfo.setWifimac(PhoneRndTools.randomMac());
        baseInfo.setBluemac(PhoneRndTools.randomMac1());
        baseInfo.setAndroid_id(PhoneRndTools.randomABC(16));
        baseInfo.setSerial(PhoneRndTools.randomNum(19) + "a");
        baseInfo.setBrand("Oppo");
        //8996-130091-1802061512
        String baseBand= PhoneRndTools.setSystemBaseband() ;
        baseInfo.setBaseBand( baseBand  );
        baseInfo.setRadioVersion( baseBand );
        baseInfo.setBoard("CPH1823");
        baseInfo.setCpu_abi("arm64-v7a");
        //baseInfo.setCpu_abi2("");
        baseInfo.setDevice("CPH1823");
        baseInfo.setDisplay("PKQ1.190414.001");
        //baseInfo.setDisplay("OPM4.181019.012.P1");
        baseInfo.setFingerprint("Oppo/CPH1823/CPH1823:9/PKQ1.190414.001/192451445de41:user/release-keys");
        //baseInfo.setFingerprint("google/sailfish/sailfish:8.1.0/OPM4.171019.021.P1/4810102:user/release-keys");
        baseInfo.setHardware("CPH1823");
        baseInfo.setId("PKQ1.190414.001");
        baseInfo.setManufacturer("Oppo");
        baseInfo.setModel("CPH1823");
        baseInfo.setProduct("CPH1823");
        baseInfo.setBootloader("unknown");
        //baseInfo.setBootloader("8996-012001-1711292200");
        baseInfo.setHost("LGEACI5R4");
        baseInfo.setTags("release-keys");
        baseInfo.setType("user");
        baseInfo.setIncremental("192451445de41");
        //baseInfo.setIncremental("4810102");
        baseInfo.setRelease("8");
        baseInfo.setSdk("27");
        baseInfo.setCodename("REL");
        baseInfo.setBuildtime("15281461"+PhoneRndTools.randomNum(7) );
        baseInfo.setDiscription("Oppo/CPH1823/CPH1823:9/PKQ1.190414.001/192451445de41:user/release-keys");

        return baseInfo;

    }

}
