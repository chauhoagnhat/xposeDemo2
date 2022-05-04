package com.example.xposedemo.myBroadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.xposedemo.Hook.HookShare;
import com.example.xposedemo.MainActivity;
import com.example.xposedemo.myService.AlarmService;
import com.example.xposedemo.utils.MyFile;
import com.example.xposedemo.utils.Ut;

import java.lang.ref.WeakReference;

public class VolumeChangeObserver {

    private static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";
    private static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";
    private final String TAG = this.getClass().getName();

    public interface VolumeChangeListener {
        /**
         * 系统媒体音量变化
         * @param volume
         */
        void onVolumeChanged(int volume);
    }

    private VolumeChangeListener mVolumeChangeListener;
    private VolumeBroadcastReceiver mVolumeBroadcastReceiver;
    private Context mContext;
    private AudioManager mAudioManager;
    private boolean mRegistered = false;

    public VolumeChangeObserver(Context context) {
        mContext = context;
        mAudioManager = (AudioManager) context.getApplicationContext()
                .getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * 获取当前媒体音量
     * @return
     */
    public int getCurrentMusicVolume() {
        return mAudioManager != null ? mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC) : -1;
    }

    /**
     * 获取系统最大媒体音量
     * @return
     */
    public int getMaxMusicVolume() {
        return mAudioManager != null ? mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) : 15;
    }

    public VolumeChangeListener getVolumeChangeListener() {
        return mVolumeChangeListener;
    }

    public void setVolumeChangeListener(VolumeChangeListener volumeChangeListener) {
        this.mVolumeChangeListener = volumeChangeListener;
    }

    /**
     * 注册音量广播接收器
     * @return
     */
    public void registerReceiver() {
        mVolumeBroadcastReceiver = new VolumeBroadcastReceiver(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction(VOLUME_CHANGED_ACTION);
        mContext.registerReceiver(mVolumeBroadcastReceiver, filter);
        mRegistered = true;
    }

    /**
     * 解注册音量广播监听器，需要与 registerReceiver 成对使用
     */
    public void unregisterReceiver() {
        if (mRegistered) {
            try {
                mContext.unregisterReceiver(mVolumeBroadcastReceiver);
                mVolumeChangeListener = null;
                mRegistered = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static class VolumeBroadcastReceiver extends BroadcastReceiver {

        private WeakReference<VolumeChangeObserver> mObserverWeakReference;
        private String TAG="VolumeBroadcastReceiver";
        public VolumeBroadcastReceiver(VolumeChangeObserver volumeChangeObserver) {
            mObserverWeakReference = new WeakReference<>(volumeChangeObserver);
        }


        public void setUiSwitch(boolean flag){
            //设置
            String uiJson= MyFile.readFileToString( HookShare.PATH_UI_SETTING );
            JSONObject jsonObject=null;

            Log.d(TAG, "set_switch");
            if (uiJson!=""){
                jsonObject= JSON.parseObject(uiJson);
                if (jsonObject.containsKey("sw_script_watch")){
                    jsonObject.put("sw_script_watch",flag);
                    Ut.fileWriterTxt(HookShare.PATH_UI_SETTING,jsonObject.toJSONString());
                }
                //return jsonObject.getBoolean ("sw_script_watch") ;
            }
        }


        @Override
        public void onReceive(Context context, Intent intent) {

            //媒体音量改变才通知
            Log.d(TAG, "onReceive: "+intent.getAction() );
            AudioManager am=(AudioManager)
                    context.getSystemService(Context.AUDIO_SERVICE);
            int max = am.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
            int current = am.getStreamVolume( AudioManager.STREAM_MUSIC );

            Log.d(TAG, "onReceive: max="+max);
            Log.d(TAG, "onReceive: current="+current );

            if ( AlarmService.watchRun){

                AlarmService.watchRun =false;
                setUiSwitch(false);

                if (AlarmService.t1 !=null){
                    AlarmService.t1.interrupt();
                }
                Intent intentMain = new Intent(context,MainActivity.class);
                intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity.volumeChangeObserver.unregisterReceiver();

                Intent intenService = new Intent();
                intenService.setAction("com.example.xposedemo.myService.AlarmService.java");
                context.stopService(intenService);

                context.startActivity( intentMain );

            }
            else {

                AlarmService.watchRun =true;
                setUiSwitch(true);

                Intent intentMain = new Intent(context,MainActivity.class);
                intentMain.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                MainActivity.volumeChangeObserver.unregisterReceiver();
                // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Intent intenService = new Intent();
                intenService.setAction("com.example.xposedemo.myService.AlarmService.java");
                context.stopService(intenService);

                context.startActivity( intentMain );

            }

            if (current>=max-1){
                am.adjustStreamVolume (AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                am.adjustStreamVolume (AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
            }

            Log.d(TAG, "onReceive: intent.getAction="+intent.getAction() );
            if (VOLUME_CHANGED_ACTION.equals(intent.getAction())
                    && (intent.getIntExtra(EXTRA_VOLUME_STREAM_TYPE, -1) == AudioManager.STREAM_MUSIC)) {

                VolumeChangeObserver observer = mObserverWeakReference.get();
                if (observer != null) {
                    VolumeChangeListener listener = observer.getVolumeChangeListener();
                    if (listener != null) {
                        int volume = observer.getCurrentMusicVolume();
                        if (volume >= 0) {
                            listener.onVolumeChanged(volume);
                        }
                    }
                }


            }

        }

    }


}
