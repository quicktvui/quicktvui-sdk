package com.quicktvui.sdk.core.ui;

import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ACTION_ES_APP_V2;
import static com.quicktvui.sdk.core.protocol.EsProtocolDispatcher.K_ACTION_V2;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.core.internal.Constants;
import com.quicktvui.sdk.core.protocol.EsProtocolDispatcher;
import com.quicktvui.sdk.core.protocol.Protocol_2;
import com.quicktvui.sdk.core.utils.EskitLazyInitHelper;
import com.quicktvui.sdk.core.utils.MapperUtils;
import com.sunrain.toolkit.utils.log.L;

import org.json.JSONObject;

/**
 * Create by weipeng on 2022/04/07 18:46
 * <p>
 * adb shell am start -d 'esapp://action/start?es_pkg=es.hello.world\&from=cmd\&flags=100\&args={\"a\":\"aa\"\,\"b\":123}\&exp={\"testexp\":456}'
 * adb shell am start -d 'esapp://action/start?es_pkg=es.com.extscreen.baiduyun\&from=tcl_multi_es_media_session\&flags=100'
 * adb shell "am start -d '"'esapp://action/start?es_pkg=es.com.extscreen.baiduyun&from=tcl_multi_es_media_session&flags=100&args={"url":"/player","token":"123.7c2ca6d576667329161ec8b1378a46fa.Ygbs15iUKvQr5D0xOfPyxdDKyHJ8oLb8PqXOEhD.tgTMjw","path":"/我的资源/电影/蓝色大海的传说 OST/视频/01.mkv"}'"'"
 * <p>
 */
public class BrowserProxyActivity extends Activity {

    private final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        delayParseIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        delayParseIntent();
    }

    private void delayParseIntent() {
        EskitLazyInitHelper.initIfNeed();
        mHandler.removeCallbacksAndMessages(null);
        int delayTime = Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1 ? 300 : 0;
        mHandler.postDelayed(() -> {
            try {
                prepareIntent();
            } catch (Exception e) {
                L.logW("parse intent", e);
                finish("启动错误:" + e.getMessage());
            }
        }, delayTime);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void prepareIntent() throws Exception {
        Intent intent = getIntent();
        if (intent == null) {
            finish("Intent获取失败");
            return;
        }

        // 兼容处理Messenger投屏
        if (Protocol_2.dealWithMessenger(intent)) {
            L.logIF("deal messenger");
            delayFinish();
            return;
        }

        JSONObject jo = MapperUtils.intent2JsonObject(intent);
        if (jo == null) {
            finish("无效参数");
            return;
        }

        jo.put(K_ACTION_V2, K_ACTION_ES_APP_V2);
        EsMap from = new EsMap();
        from.pushObject(Constants.Event.ES_REFERER, Constants.Event.FROM_OUTER);
        EsProtocolDispatcher.tryDispatcher(from, jo, null);
        delayFinish();

    }

    private final Runnable mFinishRunnable = new Runnable() {
        @Override
        public void run() {
            finish("delay finish");
        }
    };

    private void delayFinish() {
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(mFinishRunnable, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHandler.removeCallbacksAndMessages(null);
        finish("onPause");
    }

    private void finish(String msg) {
        if(!isFinishing()){
            L.logIF(msg);
            finish();
        }
    }

}
