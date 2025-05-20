package com.quicktvui.support.webview;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.IEsComponentView;
import com.quicktvui.sdk.base.core.EsProxy;
import com.sunrain.toolkit.utils.log.L;


public class ESWebView extends WebView implements IEsComponentView {

    protected static final String EVENT_PROP_URL = "url";

    protected static final String EVENT_PROP_ERROR_CODE = "errorCode";
    protected static final String EVENT_PROP_DESCRIPTION = "description";
    protected static final String EVENT_PROP_FAILING_URL = "failingUrl";
    protected static final String EVENT_PROP_JS_2_VUE = "js2VueValue";

    public enum Events {
        EVENT_ON_CORE_INIT_FINISHED("onCoreInitFinished"),
        EVENT_ON_VIEW_INIT_FINISHED("onViewInitFinished"),
        EVENT_ON_PAGE_STARTED("onPageStarted"),
        EVENT_ON_PAGE_FINISHED("onPageFinished"),
        EVENT_ON_LOAD_RESOURCE("onLoadResource"),
        EVENT_SHOULD_OVERRIDE_URL_LOADING("onShouldOverrideUrlLoading"),
        EVENT_ON_RECEIVED_SSL_ERROR("onReceivedSslError"),
        EVENT_ON_RECEIVED_ERROR("onReceivedError"),
        EVENT_ON_JS_2_VUE("onJs2Vue");

        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    private String loadUrl;

    private IJavascriptInterface javascriptInterface;
    private static final String JS_INTERFACE_NAME = "Js2Vue";

    public ESWebView(Context context) {
        super(context);
        init();
    }

    public ESWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ESWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ESWebView(Context context, AttributeSet attrs, int defStyleAttr, boolean privateBrowsing) {
        super(context, attrs, defStyleAttr, privateBrowsing);
        init();
    }

    private void init() {
        secure();
        //
        this.javascriptInterface = new IJavascriptInterface();

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
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                // 安全合规，不允许证书过期的网页被加载，故注释掉
//                super.onReceivedSslError(view, handler, error);
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
        EsMap eventMap = new EsMap();
        EsProxy.get().sendUIEvent(
                getId(), Events.EVENT_ON_CORE_INIT_FINISHED.toString(), eventMap);
        EsProxy.get().sendUIEvent(
                getId(), Events.EVENT_ON_VIEW_INIT_FINISHED.toString(), eventMap);
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
    }

    /** 安全合规 **/
    private void secure(){
        WebSettings settings = getSettings();
        if (settings != null) {
            settings.setSavePassword(false);
            settings.setAllowFileAccess(false);
        }
    }
}
