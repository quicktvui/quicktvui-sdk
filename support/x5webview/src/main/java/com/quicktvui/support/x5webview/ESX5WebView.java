package com.quicktvui.support.x5webview;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;

import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.IEsComponentView;
import com.quicktvui.sdk.base.core.EsProxy;
import com.sunrain.toolkit.utils.log.L;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsListener;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class ESX5WebView extends WebView implements IEsComponentView {

    protected static final String EVENT_PROP_URL = "url";

    protected static final String EVENT_PROP_ERROR_CODE = "errorCode";
    protected static final String EVENT_PROP_DESCRIPTION = "description";
    protected static final String EVENT_PROP_FAILING_URL = "failingUrl";
    protected static final String EVENT_PROP_JS_2_VUE = "js2VueValue";
    protected static final String EVENT_PROP_JS_REWARD_CALL = "jsRewardCallValue";
    protected static final String EVENT_PROP_JS_FINISH_GAME = "jsFinishGameValue";
    private boolean mSimulateOpenState;

    public enum Events {
        EVENT_ON_CORE_INIT_FINISHED("onCoreInitFinished"),
        EVENT_ON_VIEW_INIT_FINISHED("onViewInitFinished"),
        EVENT_ON_PAGE_STARTED("onPageStarted"),
        EVENT_ON_PAGE_FINISHED("onPageFinished"),
        EVENT_ON_LOAD_RESOURCE("onLoadResource"),
        EVENT_SHOULD_OVERRIDE_URL_LOADING("onShouldOverrideUrlLoading"),
        EVENT_ON_RECEIVED_SSL_ERROR("onReceivedSslError"),
        EVENT_ON_RECEIVED_ERROR("onReceivedError"),
        EVENT_ON_JS_2_VUE("onJs2Vue"),
        EVENT_ON_JS_REWARD_CALL("onJsRewardCall"),
        EVENT_ON_JS_FINISH_GAME("onJsFinishGame");

        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    private IJavascriptInterface javascriptInterface;
    private static final String JS_INTERFACE_NAME = "Js2Vue";
    private static final String JS_INTERFACE_REWARD_CALL = "injectedObject";
    private String loadUrl;

    private Handler mHandler;

    private Runnable mRunnable;

    public ESX5WebView(Context context) {
        super(context);
        init();
    }

    public ESX5WebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ESX5WebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        secure();
        //
        this.javascriptInterface = new IJavascriptInterface();
        mHandler = new Handler(Looper.getMainLooper());
        if (mSimulateOpenState) {
            mRunnable = () -> setMouseClick(1, 1);
        }
        //
        setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                loadUrl = url;
                EsMap eventMap = new EsMap();
                eventMap.pushString(EVENT_PROP_URL, url);
                EsProxy.get().sendUIEvent(
                        getId(), Events.EVENT_ON_PAGE_STARTED.toString(), eventMap);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                EsMap eventMap = new EsMap();
                eventMap.pushString(EVENT_PROP_URL, url);
                EsProxy.get().sendUIEvent(
                        getId(), Events.EVENT_ON_PAGE_FINISHED.toString(), eventMap);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                EsMap eventMap = new EsMap();
                eventMap.pushString(EVENT_PROP_URL, url);
                EsProxy.get().sendUIEvent(
                        getId(), Events.EVENT_ON_LOAD_RESOURCE.toString(), eventMap);
                if (mSimulateOpenState) {
                    if (mHandler != null) {
                        mHandler.removeCallbacks(mRunnable);
                        mRunnable = () -> setMouseClick(1, 1);
                        mHandler.postDelayed(mRunnable, 1000);
                    }
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                EsMap eventMap = new EsMap();
                eventMap.pushString(EVENT_PROP_URL, url);
                EsProxy.get().sendUIEvent(
                        getId(), Events.EVENT_SHOULD_OVERRIDE_URL_LOADING.toString(), eventMap);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                EsMap eventMap = new EsMap();
                eventMap.pushInt(EVENT_PROP_ERROR_CODE, errorCode);
                eventMap.pushString(EVENT_PROP_DESCRIPTION, description);
                eventMap.pushString(EVENT_PROP_FAILING_URL, failingUrl);
                EsProxy.get().sendUIEvent(
                        getId(), Events.EVENT_ON_RECEIVED_ERROR.toString(), eventMap);
            }

            @Override
            public void onReceivedSslError(WebView webView, com.tencent.smtt.export.external.interfaces.SslErrorHandler sslErrorHandler, com.tencent.smtt.export.external.interfaces.SslError sslError) {
                // 安全合规，不允许证书过期的网页被加载，故注释掉
                //super.onReceivedSslError(webView, sslErrorHandler, sslError);
                EsMap eventMap = new EsMap();
                EsProxy.get().sendUIEvent(
                        getId(), Events.EVENT_ON_RECEIVED_SSL_ERROR.toString(), eventMap);
            }
        });
    }

    public void clearCookie() {
        if (TextUtils.isEmpty(loadUrl)) {
            if (L.DEBUG) {
                L.logD("---------clearCookie----url is null---->>>>>");
            }
            return;
        }

        if (L.DEBUG) {
            L.logD("---------clearCookie----start---->>>>>");
        }
        CookieSyncManager.createInstance(getContext());
        CookieManager cookieManager = CookieManager.getInstance();
        String cookieGlob = cookieManager.getCookie(getDomain(loadUrl));
        if (cookieGlob != null) {
            String[] cookies = cookieGlob.split(";");
            for (int i = 0; i < cookies.length; i++) {
                String[] cookieParts = cookies[i].split("=");
                cookieManager.setCookie(getDomain(loadUrl), cookieParts[0] + "=;");
            }
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                CookieSyncManager.getInstance().sync();
            } else {
                cookieManager.flush();
            }
        }
        if (L.DEBUG) {
            L.logD("---------clearCookie----end---->>>>>");
        }
    }

    private String getDomain(String url) {
        url = url.replace("http://", "").replace("https://", "");
        if (url.contains("/")) {
            url = url.substring(0, url.indexOf('/'));
        }
        return url;
    }

    public void initWebView() {
        QbSdk.setDownloadWithoutWifi(true);

        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int stateCode) {
                if (L.DEBUG) {
                    L.logD("onDownloadFinished: " + stateCode);
                }
            }

            @Override
            public void onInstallFinish(int stateCode) {
                if (L.DEBUG) {
                    L.logD("onInstallFinish: " + stateCode);
                }
            }

            @Override
            public void onDownloadProgress(int progress) {
                if (L.DEBUG) {
                    L.logD("onDownloadProgress: " + progress);
                }
            }
        });

        QbSdk.initX5Environment(getContext().getApplicationContext(), new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                // 内核初始化完成，可能为系统内核，也可能为系统内核
                EsMap eventMap = new EsMap();
                EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_CORE_INIT_FINISHED.toString(), eventMap);
            }

            /**
             * 预初始化结束
             * 由于X5内核体积较大，需要依赖网络动态下发，所以当内核不存在的时候，默认会回调false，此时将会使用系统内核代替
             * @param isX5 是否使用X5内核
             */
            @Override
            public void onViewInitFinished(boolean isX5) {
                EsMap eventMap = new EsMap();
                eventMap.pushString("isX5", String.valueOf(isX5));
                EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_VIEW_INIT_FINISHED.toString(), eventMap);
            }
        });
    }

    public void initJavaScriptInterface() {
        try {
            addJavascriptInterface(javascriptInterface, JS_INTERFACE_REWARD_CALL);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void initWebViewFocus(long delayTime, int x, int y) {
        try {
            ESX5WebView.this.setFocusableInTouchMode(true);
            ESX5WebView.this.setFocusableInTouchMode(true);
            ESX5WebView.this.setFocusable(true);
            ESX5WebView.this.setFocusable(true);
            if (mHandler != null) {
                mHandler.postDelayed(() -> setMouseClick(x, y), delayTime);
            } else {
                setMouseClick(x, y);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeJavaScriptInterface() {
        try {
            removeJavascriptInterface(JS_INTERFACE_REWARD_CALL);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        try {
            addJavascriptInterface(javascriptInterface, JS_INTERFACE_NAME);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        try {
            removeJavascriptInterface(JS_INTERFACE_NAME);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    class IJavascriptInterface {

        public IJavascriptInterface() {
        }

        @JavascriptInterface
        public void js2Vue(String value) {
            EsMap eventMap = new EsMap();
            eventMap.pushString(EVENT_PROP_JS_2_VUE, value);
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_JS_2_VUE.toString(), eventMap);
        }

        @JavascriptInterface
        public void rewardCall(String value) {
            EsMap eventMap = new EsMap();
            eventMap.pushString(EVENT_PROP_JS_REWARD_CALL, value);
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_JS_REWARD_CALL.toString(), eventMap);
        }

        @JavascriptInterface
        public void finishGame(String gameId) {
            EsMap eventMap = new EsMap();
            if (!TextUtils.isEmpty(gameId)) {
                eventMap.pushString(EVENT_PROP_JS_FINISH_GAME, gameId);
            }
            EsProxy.get().sendUIEvent(getId(), Events.EVENT_ON_JS_FINISH_GAME.toString(), eventMap);
        }
    }

    /**
     * 安全合规
     **/
    private void secure() {
        WebSettings settings = getSettings();
        if (settings != null) {
            settings.setSavePassword(false);
            settings.setAllowFileAccess(false);
        }
    }

    private void setMouseClick(int x, int y) {
        MotionEvent eventDown = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis() + 1000, MotionEvent.ACTION_DOWN, x, y, 0);
        dispatchTouchEvent(eventDown);
        MotionEvent eventUp = MotionEvent.obtain(System.currentTimeMillis(), System.currentTimeMillis() + 1000, MotionEvent.ACTION_UP, x, y, 0);
        dispatchTouchEvent(eventUp);
        eventDown.recycle();
        eventUp.recycle();
    }

    public void openSimulateClick(boolean isOpen) {
        mSimulateOpenState = isOpen;
    }
}
