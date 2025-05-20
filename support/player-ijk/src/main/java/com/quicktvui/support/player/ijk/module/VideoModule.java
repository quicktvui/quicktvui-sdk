package com.quicktvui.support.player.ijk.module;

import android.net.TrafficStats;
import android.os.Handler;
import android.os.Process;
import android.os.SystemClock;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.support.player.ijk.utils.FormatTools;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.annotation.HippyMethod;
import com.tencent.mtt.hippy.annotation.HippyNativeModule;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.modules.Promise;
import com.tencent.mtt.hippy.modules.javascriptmodules.EventDispatcher;
import com.tencent.mtt.hippy.modules.nativemodules.HippyNativeModuleBase;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 网速测试类，检查当前网络情况
 */
@ESKitAutoRegister
@HippyNativeModule(name = "VideoModule")
public class VideoModule extends HippyNativeModuleBase {
    private Handler mHandler;
    private Runnable mRunnable;
    private boolean isNetTesting;

    public VideoModule(HippyEngineContext context) {
        super(context);
    }

    @HippyMethod(name = "startNetSpeedTest")
    public void startNetSpeedTest() {
        if (isNetTesting) {
            return;
        }
        isNetTesting = true;

        if (mHandler == null || mRunnable == null) {
            mHandler = new Handler();
            mRunnable = new Runnable() {
                long mStartTime = SystemClock.elapsedRealtime();
                long mStartBytes2 = TrafficStats.getUidRxBytes(Process.myUid());
                long mStartBytes = TrafficStats.getTotalRxBytes();

                @Override
                public void run() {

                    long mEndTime = SystemClock.elapsedRealtime();
                    long mEndBytes2 = TrafficStats.getUidRxBytes(Process.myUid());
                    long mEndBytes = TrafficStats.getTotalRxBytes();

                    long timeElapsed = mEndTime - mStartTime;
                    long bytesTransferred = mEndBytes - mStartBytes;
                    long bytesTransferred2 = mEndBytes2 - mStartBytes2;

                    // 计算网速并显示在界面上
//                    double speedMbps = (bytesTransferred / (1024 * 1024.0)) / (timeElapsed / 1000.0);
                    double speedMbps = FormatTools.formatNetSpeed(bytesTransferred, timeElapsed);
//                    double speedMbps2 = (bytesTransferred2 / (1024 * 1024.0)) / (timeElapsed / 1000.0);
                    double speedMbps2 = FormatTools.formatNetSpeed(bytesTransferred2, timeElapsed);

                    HippyMap map = new HippyMap();
                    map.pushDouble("totalSpeed", speedMbps);
                    map.pushDouble("uidSpeed", speedMbps2);
                    mContext.getModuleManager().
                            getJavaScriptModule(EventDispatcher.class).
                            receiveNativeEvent("GlobalSpeed", map);

                    String speedText = String.format("%.2f MBps", speedMbps);
                    String speedText2 = String.format("%.2f MBps", speedMbps2);

                    mStartTime = mEndTime;
                    mStartBytes = mEndBytes;
                    mStartBytes2 = mEndBytes2;

                    mHandler.postDelayed(this, 1000); // 每秒更新一次
                }
            };
        }

        mHandler.postDelayed(mRunnable, 1000);
    }

    @HippyMethod(name = "stopNetSpeedTest")
    public void stopNetSpeedTest() {
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
        isNetTesting = false;
    }

    @HippyMethod(name = "testNetWidth")
    public void testNetWidth(String urlToTest, Promise promise) {
        testNetWidth2(urlToTest, 10, promise);
    }

    @HippyMethod(name = "testNetWidthByTimes")
    public void testNetWidth2(String urlToTest, int second, Promise promise) {
        new Thread(() ->
                testNetWidthReal(urlToTest, second, promise)).start();
    }

    private void testNetWidthReal(String urlToTest, int second, Promise promise) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        double speed = 0;
        try {
            URL url = new URL(urlToTest);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            inputStream = connection.getInputStream();

            byte[] buffer = new byte[1024];
            long bytesRead;
            long totalBytesRead = 0;
            long endTime = -1;
            long startTime = SystemClock.elapsedRealtime();

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                totalBytesRead += bytesRead;

                if ((endTime = SystemClock.elapsedRealtime()) - startTime >= second * 1000L) {
                    break;
                }
            }

            if (endTime != -1) {
                long duration = endTime - startTime;

                if (duration > 0) {
//                    speed = (totalBytesRead / (1024 * 1024.0)) / (duration / 1000.0); // Speed in MBps
                    speed = FormatTools.formatNetSpeed(totalBytesRead, duration);
                }

                String speedText = String.format("%.2f MBps", speed);

                HippyMap map = new HippyMap();
                map.pushDouble("speed",speed);
                promise.resolve(map);

            } else {
                HippyMap map = new HippyMap();
                map.pushDouble("speed", -1);
                promise.resolve(map);

            }
        } catch (Exception e) {
            HippyMap map = new HippyMap();
            map.pushDouble("speed", -1);
            promise.resolve(map);

            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void destroy() {
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
            mHandler = null;
            mRunnable = null;
        }
    }
}
