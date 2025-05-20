package com.quicktvui.support.network.speed;

import android.content.Context;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.Process;
import android.os.SystemClock;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;
import com.quicktvui.support.network.speed.util.FormatTools;

@ESKitAutoRegister
public class ESNetworkSpeedModule implements IEsModule, IEsInfo {

    private static final String TAG = "ESNetworkSpeedModule";
    private Context context;
    private boolean isNetTesting;

    private Runnable mRunnable;

    private Handler mHandler;

    private long mStartTime;
    private long mUidStartBytes;
    private long mTotalStartBytes;

    @Override
    public void init(Context context) {
        this.context = context;
    }


    public void showNetSpeed() {
        doNetSpeed(1000);
    }

    public void showNetSpeed(long delayTime) {
        doNetSpeed(delayTime);
    }

    public void stopNetSpeed() {
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        isNetTesting = false;
    }

    private void doNetSpeed(long delayTime) {
        if (isNetTesting) {
            return;
        }
        isNetTesting = true;

        if (mHandler == null || mRunnable == null) {
            mHandler = new Handler();
            mStartTime = SystemClock.elapsedRealtime();
            mUidStartBytes = TrafficStats.getUidRxBytes(Process.myUid());
            mTotalStartBytes = TrafficStats.getTotalRxBytes();
            /*if (BuildConfig.DEBUG) {
                Log.d(TAG, "mUidStartBytes---" + mUidStartBytes);
                Log.d(TAG, "mTotalStartBytes---" + mTotalStartBytes);
            }*/
            mRunnable = new Runnable() {
                @Override
                public void run() {
                    double speedKbps = 0;
                    double speedMbps = 0;
                    long mEndTime = SystemClock.elapsedRealtime();
                    long mUidEndBytes = TrafficStats.getUidRxBytes(Process.myUid());
                    long mTotalEndBytes = TrafficStats.getTotalRxBytes();
                    long timeElapsed = mEndTime - mStartTime;
                    long bytesTransferred = mTotalEndBytes - mTotalStartBytes;
                    long bytesTransferredCurrentUid = mUidEndBytes - mUidStartBytes;

                    if (bytesTransferred == 0){
                        speedKbps = FormatTools.formatNetSpeedKB(bytesTransferredCurrentUid, timeElapsed);
                        speedMbps = FormatTools.formatNetSpeedMB(bytesTransferredCurrentUid, timeElapsed);
                        EsMap map = new EsMap();
                        map.pushString("speedKbps", String.valueOf(speedKbps));
                        map.pushString("speedMbps", String.valueOf(speedMbps));
                        EsProxy.get().sendNativeEventTraceable(ESNetworkSpeedModule.this, "NetSpeed", map);
                    } else {
                        // 计算网速并显示在界面上
                        speedKbps = FormatTools.formatNetSpeedKB(bytesTransferred, timeElapsed);
                        speedMbps = FormatTools.formatNetSpeedMB(bytesTransferred, timeElapsed);

                        EsMap map = new EsMap();
                        map.pushString("speedKbps", String.valueOf(speedKbps));
                        map.pushString("speedMbps", String.valueOf(speedMbps));
                        EsProxy.get().sendNativeEventTraceable(ESNetworkSpeedModule.this, "NetSpeed", map);
                    }
                    /*if (BuildConfig.DEBUG) {
                        Log.d(TAG, "mUidEndBytes---" + mUidEndBytes);
                        Log.d(TAG, "mTotalEndBytes---" + mTotalEndBytes);
                        Log.d(TAG, "当前接受总字节1---" + bytesTransferred);
                        Log.d(TAG, "当前网络uid进程接收字节2---" + bytesTransferred2);
                        Log.d(TAG, "时间戳---" + timeElapsed);
                    }*/

                    mStartTime = mEndTime;
                    mTotalStartBytes = mTotalEndBytes;
                    mUidStartBytes = mUidEndBytes;

                    mHandler.postDelayed(this, delayTime); //默认1秒更新一次
                }
            };
        }
        mHandler.postDelayed(mRunnable, delayTime);
    }

    public void removeListener() {
        isNetTesting = false;
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mRunnable = null;
            mHandler = null;
        }
    }

    @Override
    public void getEsInfo(EsPromise esPromise) {

    }

    @Override
    public void destroy() {
        removeListener();
    }
}
