package com.quicktvui.support.webview;

import static com.quicktvui.sdk.base.IEsInfo.ES_OP_GET_ES_INFO;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;
import com.sunrain.toolkit.utils.log.L;

/** 系统浏览器 **/
@ESKitAutoRegister
public class ESWebViewComponent implements IEsComponent<ESWebView> {

    //operation
    private static final String OP_INIT_WEB_VIEW = "initWebView";

    private static final String OP_LOAD_URL = "loadUrl";

    //
    private static final String OP_SET_ENABLED = "setEnabled";
    private static final String OP_REQUEST_FOCUS = "requestFocus";
    private static final String OP_REMOVE_ALL_VIEWS = "removeAllViews";

    private static final String OP_CLEAR_COOKIE = "clearCookie";
    private static final String OP_CLEAR_CACHE = "clearCache";
    private static final String OP_CLEAR_FORM_DATA = "clearFormData";
    private static final String OP_CLEAR_HISTORY = "clearHistory";
    private static final String OP_CLEAR_MATCHES = "clearMatches";
    private static final String OP_CLEAR_FOCUS = "clearFocus";
    private static final String OP_CLEAR_SSL_PREFERENCES = "clearSslPreferences";

    //
    private static final String OP_EVALUATE_JAVA_SCRIPT = "evaluateJavascript";

    //
    private static final String OP_CAN_GO_BACK = "canGoBack";
    private static final String OP_GO_BACK = "goBack";
    private static final String OP_CAN_GO_FORWARD = "canGoForward";
    private static final String OP_GO_FORWARD = "goForward";
    private static final String OP_CAN_GO_BACK_OR_FORWARD = "canGoBackOrForward";
    private static final String OP_GO_BACK_OR_FORWARD = "goBackOrForward";

    //
    private static final String OP_ON_RESUME = "onResume";
    private static final String OP_ON_PAUSE = "onPause";
    private static final String OP_PAUSE_TIMERS = "pauseTimers";
    private static final String OP_RESUME_TIMERS = "resumeTimers";
    private static final String OP_DESTROY = "destroy";

    //
    private static final String OP_SET_JAVA_SCRIPT_ENABLED = "setJavaScriptEnabled";//是否开启JS支持
    private static final String OP_SET_PLUGINS_STATE = "setPluginState";//是否开启插件支持
    private static final String OP_SET_JAVA_SCRIPT_CAN_OPEN_WINDOWS_AUTOMATICALLY = "setJavaScriptCanOpenWindowsAutomatically";//是否允许JS打开新窗口

    private static final String OP_SET_USE_WIDE_VIEW_PORT = "setUseWideViewPort";//缩放至屏幕大小
    private static final String OP_SET_LOAD_WITH_OVERVIEW_MODE = "setLoadWithOverviewMode";//缩放至屏幕大小
    private static final String OP_SET_SUPPORT_ZOOM = "setSupportZoom";//是否支持缩放
    private static final String OP_ON_SET_BUILD_IN_ZOOM_CONTROLS = "setBuiltInZoomControls";//是否支持缩放变焦，前提是支持缩放
    private static final String OP_SET_DISPLAY_ZOOM_CONTROLS = "setDisplayZoomControls";//是否隐藏缩放控件

    private static final String OP_SET_ALLOW_FILE_ACCESS = "setAllowFileAccess";//是否允许访问文件
    private static final String OP_SET_DOM_STORAGE_ENABLED = "setDomStorageEnabled";//是否节点缓存
    private static final String OP_SET_DATABASE_ENABLED = "setDatabaseEnabled";//是否数据缓存
    private static final String OP_SET_APP_CACHE_ENABLED = "setAppCacheEnabled";//是否应用缓存
    private static final String OP_SET_APP_CACHE_PATH = "setAppCachePath";//设置缓存路径

    private static final String OP_SET_MEDIA_PLAYBACK_REQUIRES_USE_GESTURE = "setMediaPlaybackRequiresUserGesture";//是否要手势触发媒体
    private static final String OP_SET_STANDARD_FONT_FAMILY = "setStandardFontFamily";//设置字体库格式
    private static final String OP_SET_FIXED_FONT_FAMILY = "setFixedFontFamily";// 设置字体库格式
    private static final String OP_SET_SANS_SERIF_FONT_FAMILY = "setSansSerifFontFamily";//设置字体库格式
    private static final String OP_SET_SERIF_FONT_FAMILY = "setSerifFontFamily";//设置字体库格式
    private static final String OP_SET_CURSIVE_FONT_FAMILY = "setCursiveFontFamily";//设置字体库格式
    private static final String OP_SET_FANTASY_FONT_FAMILY = "setFantasyFontFamily";//设置字体库格式
    private static final String OP_SET_TEXT_ZOOM = "setTextZoom";//设置文本缩放的百分比
    private static final String OP_SET_MINIMUM_FONT_SIZE = "setMinimumFontSize";//设置文本字体的最小值(1~72)
    private static final String OP_SET_DEFAULT_FONT_SIZE = "setDefaultFontSize";//设置文本字体默认的大小

    private static final String OP_SET_LAYOUT_ALGORITHM = "setLayoutAlgorithm";//按规则重新布局
    private static final String OP_SET_LOADS_IMAGES_AUTOMATICALLY = "setLoadsImagesAutomatically";//是否自动加载图片
    private static final String OP_SET_DEFAULT_TEXT_ENCODING_NAME = "setDefaultTextEncodingName";//设置编码格式
    private static final String OP_SET_NEED_INITIAL_FOCUS = "setNeedInitialFocus";//是否需要获取焦点
    private static final String OP_SET_GEOLOCATION_ENABLED = "setGeolocationEnabled";//设置开启定位功能
    private static final String OP_SET_BLOCK_NETWORK_LOADS = "setBlockNetworkLoads";//是否从网络获取资源

    //
    private static final String OP_SET_SUPPORT_MULTIPLE_WINDOWS = "setSupportMultipleWindows";//
    private static final String OP_SET_APP_CACHE_MAX_SIZE = "setAppCacheMaxSize";//
    private static final String OP_SET_PAGE_CACHE_CAPACITY = "setPageCacheCapacity";//
    private static final String OP_SET_RENDER_PRIORITY = "setRenderPriority";//
    private static final String OP_SET_CACHE_MODE = "setCacheMode";//

    //----------------------------------------------------------
    private static final String OP_SET_ALLOW_CONTENT_ACCESS = "setAllowContentAccess";//
    private static final String OP_SET_ENABLE_SMOOTH_TRANSITION = "setEnableSmoothTransition";//
    private static final String OP_SET_SAVE_FORM_DATA = "setSaveFormData";//
    private static final String OP_SET_SAVE_PASSWORD = "setSavePassword";//
    private static final String OP_SET_TEXT_SIZE = "setTextSize";//
    private static final String OP_SET_DEFAULT_ZOOM = "setDefaultZoom";//
    private static final String OP_SET_LIGHT_TOUCH_ENABLED = "setLightTouchEnabled";//
    private static final String OP_SET_MINIMUM_LOGICAL_FONT_SIZE = "setMinimumLogicalFontSize";//
    private static final String OP_SET_DEFAULT_FIXED_FONT_SIZE = "setDefaultFixedFontSize";//
    private static final String OP_SET_BLOCK_NETWORK_IMAGE = "setBlockNetworkImage";//
    private static final String OP_SET_ALLOW_UNIVERSAL_ACCESS_FROM_FILE_URLS = "setAllowUniversalAccessFromFileURLs";//
    private static final String OP_SET_ALLOW_FILE_ACCESS_FROM_FILE_URLS = "setAllowFileAccessFromFileURLs";//
    private static final String OP_SET_DATABASE_PATH = "setDatabasePath";//
    private static final String OP_SET_GEOLOCATION_DATABASE_PATH = "setGeolocationDatabasePath";//
    private static final String OP_SET_USER_AGENT_STRING = "setUserAgentString";//
    private static final String OP_SET_MIXED_CONTENT_MODE = "setMixedContentMode";//
    private static final String OP_SET_OFFSCREEN_PRE_RASTER = "setOffscreenPreRaster";//
    private static final String OP_SET_SAFE_BROWSING_ENABLED = "setSafeBrowsingEnabled";//
    private static final String OP_SET_FORCE_DARK = "setForceDark";//
    private static final String OP_SET_DISABLED_ACTION_MODE_MENU_ITEMS = "setDisabledActionModeMenuItems";//

    protected Context context;

    @Override
    public ESWebView createView(Context context, EsMap params) {
        this.context = context;
        return new ESWebView(context);
    }

    @EsComponentAttribute
    public void url(ESWebView webView, String url) {
        if (!TextUtils.isEmpty(url)) {
            webView.loadUrl(url);
        }
    }

    @Override
    public void dispatchFunction(ESWebView webView, String eventName, EsArray params, EsPromise promise) {
        if (L.DEBUG) {
            L.logD("#---dispatchFunction-->>>>>"
                    + eventName + "---->>>" + Thread.currentThread());
        }
        try {
            switch (eventName) {
                case ES_OP_GET_ES_INFO:
                    EsMap map = new EsMap();
                    promise.resolve(map);
                    break;
                case OP_INIT_WEB_VIEW:
                    webView.initWebView();
                    break;
                case OP_SET_ENABLED:
                    try {
                        boolean enabled = params.getBoolean(0);
                        webView.setEnabled(enabled);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_REQUEST_FOCUS:
                    try {
                        webView.requestFocus();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_REMOVE_ALL_VIEWS:
                    try {
                        webView.removeAllViews();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_CLEAR_COOKIE:
                    try {
                        webView.clearCookie();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_CLEAR_CACHE:
                    try {
                        boolean value = params.getBoolean(0);
                        webView.clearCache(value);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_CLEAR_FORM_DATA:
                    try {
                        webView.clearFormData();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_CLEAR_HISTORY:
                    try {
                        webView.clearHistory();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_CLEAR_MATCHES:
                    try {
                        webView.clearMatches();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_CLEAR_FOCUS:
                    try {
                        webView.clearFocus();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_CLEAR_SSL_PREFERENCES:
                    try {
                        webView.clearSslPreferences();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                //----------------------------------------------
                case OP_LOAD_URL:
                    try {
                        String url = params.getString(0);
                        if (L.DEBUG) {
                            L.logD("#---loadUrl------>>>" + url);
                        }
                        webView.loadUrl(url);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_EVALUATE_JAVA_SCRIPT:
                    try {
                        String url = params.getString(0);
                        if (L.DEBUG) {
                            L.logD("#---evaluateJavascript------>>>" + url);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            webView.evaluateJavascript(url, new ValueCallback<String>() {
                                @Override
                                public void onReceiveValue(String s) {
                                    if (promise != null) {
                                        if (L.DEBUG) {
                                            L.logD("#---evaluateJavascript--onReceiveValue------>>>" + s);
                                        }
                                        promise.resolve(s);
                                    }
                                }
                            });
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                //----------------------------------------------
                case OP_CAN_GO_BACK:
                    try {
                        boolean canGoBack = webView.canGoBack();
                        if (promise != null) {
                            if (L.DEBUG) {
                                L.logD("#---canGoBack------>>>" + canGoBack);
                            }
                            promise.resolve(canGoBack);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_GO_BACK:
                    try {
                        if (L.DEBUG) {
                            L.logD("#---goBack------>>>");
                        }
                        webView.goBack();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_CAN_GO_FORWARD:
                    try {
                        boolean canGoForward = webView.canGoForward();
                        if (promise != null) {
                            if (L.DEBUG) {
                                L.logD("#---canGoForward------>>>" + canGoForward);
                            }
                            promise.resolve(canGoForward);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_GO_FORWARD:
                    try {
                        if (L.DEBUG) {
                            L.logD("#---goForward------>>>");
                        }
                        webView.goForward();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_CAN_GO_BACK_OR_FORWARD:
                    try {
                        int steps = params.getInt(0);
                        boolean canGoBackOrForward = webView.canGoBackOrForward(steps);
                        if (promise != null) {
                            if (L.DEBUG) {
                                L.logD(steps + "#---canGoBackOrForward------>>>" + canGoBackOrForward);
                            }
                            promise.resolve(canGoBackOrForward);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_GO_BACK_OR_FORWARD:
                    try {
                        int steps = params.getInt(0);
                        if (L.DEBUG) {
                            L.logD("#---goBackOrForward------>>>" + steps);
                        }
                        webView.goBackOrForward(steps);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                //----------------------------------------------
                case OP_ON_RESUME:
                    try {
                        if (L.DEBUG) {
                            L.logD("#---onResume------>>>");
                        }
                        webView.onResume();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_ON_PAUSE:
                    try {
                        if (L.DEBUG) {
                            L.logD("#---onPause------>>>");
                        }
                        webView.onPause();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_PAUSE_TIMERS:
                    try {
                        if (L.DEBUG) {
                            L.logD("#---pauseTimers------>>>");
                        }
                        webView.pauseTimers();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_RESUME_TIMERS:
                    try {
                        if (L.DEBUG) {
                            L.logD("#---resumeTimers------>>>");
                        }
                        webView.resumeTimers();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_DESTROY:
                    try {
                        if (L.DEBUG) {
                            L.logD("#---destroy------>>>");
                        }
                        webView.destroy();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                //----------------------------------------------
                case OP_SET_JAVA_SCRIPT_ENABLED:
                    try {
                        boolean enabled = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setJavaScriptEnabled------>>>" + enabled);
                            }
                            webView.getSettings().setJavaScriptEnabled(enabled);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SET_PLUGINS_STATE:
                    try {
                        int value = params.getInt(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setPluginState------>>>" + value);
                            }
                            if (value == 0) {
                                webView.getSettings().setPluginState(WebSettings.PluginState.ON);
                            } else if (value == 1) {
                                webView.getSettings().setPluginState(WebSettings.PluginState.ON_DEMAND);
                            } else if (value == 2) {
                                webView.getSettings().setPluginState(WebSettings.PluginState.OFF);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SET_JAVA_SCRIPT_CAN_OPEN_WINDOWS_AUTOMATICALLY:
                    try {
                        boolean enabled = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setJavaScriptCanOpenWindowsAutomatically------>>>" + enabled);
                            }
                            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(enabled);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_USE_WIDE_VIEW_PORT:
                    try {
                        boolean enabled = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setUseWideViewPort------>>>" + enabled);
                            }
                            webView.getSettings().setUseWideViewPort(enabled);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_LOAD_WITH_OVERVIEW_MODE:
                    try {
                        boolean enabled = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setLoadWithOverviewMode------>>>" + enabled);
                            }
                            webView.getSettings().setLoadWithOverviewMode(enabled);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_SUPPORT_ZOOM:
                    try {
                        boolean enabled = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setSupportZoom------>>>" + enabled);
                            }
                            webView.getSettings().setSupportZoom(enabled);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_ON_SET_BUILD_IN_ZOOM_CONTROLS:
                    try {
                        boolean enabled = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setBuiltInZoomControls------>>>" + enabled);
                            }
                            webView.getSettings().setBuiltInZoomControls(enabled);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_DISPLAY_ZOOM_CONTROLS:
                    try {
                        boolean enabled = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setDisplayZoomControls------>>>" + enabled);
                            }
                            webView.getSettings().setDisplayZoomControls(enabled);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                //----------------------------------------------
                case OP_SET_ALLOW_FILE_ACCESS:
                    try {
                        boolean enabled = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setAllowFileAccess------>>>" + enabled);
                            }
                            webView.getSettings().setAllowFileAccess(enabled);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SET_DOM_STORAGE_ENABLED:
                    try {
                        boolean enabled = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setDomStorageEnabled------>>>" + enabled);
                            }
                            webView.getSettings().setDomStorageEnabled(enabled);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SET_DATABASE_ENABLED:
                    try {
                        boolean enabled = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setDatabaseEnabled------>>>" + enabled);
                            }
                            webView.getSettings().setDatabaseEnabled(enabled);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SET_APP_CACHE_ENABLED:
                    try {
                        boolean enabled = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setAppCacheEnabled------>>>" + enabled);
                            }
//                            webView.getSettings().setAppCacheEnabled(enabled);//todo api报错
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SET_APP_CACHE_PATH:
                    try {
                        String path = params.getString(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setAppCachePath------>>>" + path);
                            }
//                            webView.getSettings().setAppCachePath(path);//todo api报错
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                //----------------------------------------------
                case OP_SET_MEDIA_PLAYBACK_REQUIRES_USE_GESTURE:
                    try {
                        boolean enabled = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                                if (L.DEBUG) {
                                    L.logD("#---setMediaPlaybackRequiresUserGesture------>>>" + enabled);
                                }
                                webView.getSettings().setMediaPlaybackRequiresUserGesture(enabled);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_STANDARD_FONT_FAMILY:
                    try {
                        String font = params.getString(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setStandardFontFamily------>>>" + font);
                            }
                            webView.getSettings().setStandardFontFamily(font);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_FIXED_FONT_FAMILY:
                    try {
                        String font = params.getString(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setFixedFontFamily------>>>" + font);
                            }
                            webView.getSettings().setFixedFontFamily(font);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SET_SANS_SERIF_FONT_FAMILY:
                    try {
                        String font = params.getString(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setSansSerifFontFamily------>>>" + font);
                            }
                            webView.getSettings().setSansSerifFontFamily(font);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_SERIF_FONT_FAMILY:
                    try {
                        String font = params.getString(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setSerifFontFamily------>>>" + font);
                            }
                            webView.getSettings().setSerifFontFamily(font);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_CURSIVE_FONT_FAMILY:
                    try {
                        String font = params.getString(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setCursiveFontFamily------>>>" + font);
                            }
                            webView.getSettings().setCursiveFontFamily(font);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_FANTASY_FONT_FAMILY:
                    try {
                        String font = params.getString(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setFantasyFontFamily------>>>" + font);
                            }
                            webView.getSettings().setFantasyFontFamily(font);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_TEXT_ZOOM:
                    try {
                        int value = params.getInt(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setTextZoom------>>>" + value);
                            }
                            webView.getSettings().setTextZoom(value);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_MINIMUM_FONT_SIZE:
                    try {
                        int value = params.getInt(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setMinimumFontSize------>>>" + value);
                            }
                            webView.getSettings().setMinimumFontSize(value);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_DEFAULT_FONT_SIZE:
                    try {
                        int value = params.getInt(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setDefaultFontSize------>>>" + value);
                            }
                            webView.getSettings().setDefaultFontSize(value);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                //----------------------------------------------
                case OP_SET_LOADS_IMAGES_AUTOMATICALLY:
                    try {
                        boolean enabled = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setLoadsImagesAutomatically------>>>" + enabled);
                            }
                            webView.getSettings().setLoadsImagesAutomatically(enabled);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SET_NEED_INITIAL_FOCUS:
                    try {
                        boolean enabled = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setNeedInitialFocus------>>>" + enabled);
                            }
                            webView.getSettings().setNeedInitialFocus(enabled);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SET_GEOLOCATION_ENABLED:
                    try {
                        boolean enabled = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setGeolocationEnabled------>>>" + enabled);
                            }
                            webView.getSettings().setGeolocationEnabled(enabled);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SET_BLOCK_NETWORK_LOADS:
                    try {
                        boolean enabled = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setBlockNetworkLoads------>>>" + enabled);
                            }
                            webView.getSettings().setBlockNetworkLoads(enabled);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_DEFAULT_TEXT_ENCODING_NAME:
                    try {
                        String value = params.getString(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setDefaultTextEncodingName------>>>" + value);
                            }
                            webView.getSettings().setDefaultTextEncodingName(value);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_LAYOUT_ALGORITHM:
                    try {
                        int value = params.getInt(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setLayoutAlgorithm------>>>" + value);
                            }
                            if (value == 0) {
                                webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NORMAL);
                            } else if (value == 1) {
                                webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                            } else if (value == 2) {
                                webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
                            } else if (value == 3) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING);
                                }
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                //---------------------------------------------------
                case OP_SET_SUPPORT_MULTIPLE_WINDOWS:
                    try {
                        boolean value = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setSupportMultipleWindows------>>>" + value);
                            }
                            webView.getSettings().setSupportMultipleWindows(value);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SET_APP_CACHE_MAX_SIZE:
                    try {
                        long value = params.getLong(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setAppCacheMaxSize------>>>" + value);
                            }
//                            webView.getSettings().setAppCacheMaxSize(value);//todo api报错
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_RENDER_PRIORITY:
                    try {
                        int value = params.getInt(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setRenderPriority------>>>" + value);
                            }
                            if (value == 0) {
                                webView.getSettings().setRenderPriority(WebSettings.RenderPriority.NORMAL);
                            } else if (value == 1) {
                                webView.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);
                            } else if (value == 2) {
                                webView.getSettings().setRenderPriority(WebSettings.RenderPriority.LOW);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_CACHE_MODE:
                    try {
                        int value = params.getInt(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setCacheMode------>>>" + value);
                            }
                            webView.getSettings().setCacheMode(value);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                //--------------------------------------------------------
                case OP_SET_ALLOW_CONTENT_ACCESS:
                    try {
                        boolean value = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setAllowContentAccess------>>>" + value);
                            }
                            webView.getSettings().setAllowContentAccess(value);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_ENABLE_SMOOTH_TRANSITION:
                    try {
                        boolean value = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setEnableSmoothTransition------>>>" + value);
                            }
                            webView.getSettings().setEnableSmoothTransition(value);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_SAVE_FORM_DATA:
                    try {
                        boolean value = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setSaveFormData------>>>" + value);
                            }
                            webView.getSettings().setSaveFormData(value);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SET_SAVE_PASSWORD:
                    try {
                        boolean value = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setSavePassword------>>>" + value);
                            }
                            webView.getSettings().setSavePassword(value);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SET_TEXT_SIZE:
                    try {
                        int value = params.getInt(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setTextSize------>>>" + value);
                            }
                            if (value == 0) {
                                webView.getSettings().setTextSize(WebSettings.TextSize.SMALLEST);
                            } else if (value == 1) {
                                webView.getSettings().setTextSize(WebSettings.TextSize.SMALLER);
                            } else if (value == 2) {
                                webView.getSettings().setTextSize(WebSettings.TextSize.NORMAL);
                            } else if (value == 3) {
                                webView.getSettings().setTextSize(WebSettings.TextSize.LARGER);
                            } else if (value == 4) {
                                webView.getSettings().setTextSize(WebSettings.TextSize.LARGEST);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_DEFAULT_ZOOM:
                    try {
                        int value = params.getInt(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setDefaultZoom------>>>" + value);
                            }
                            if (value == 0) {
                                webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
                            } else if (value == 1) {
                                webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
                            } else if (value == 2) {
                                webView.getSettings().setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_LIGHT_TOUCH_ENABLED:
                    try {
                        boolean value = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setSavePassword------>>>" + value);
                            }
                            webView.getSettings().setLightTouchEnabled(value);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                case OP_SET_MINIMUM_LOGICAL_FONT_SIZE:
                    try {
                        int value = params.getInt(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setMinimumLogicalFontSize------>>>" + value);
                            }
                            webView.getSettings().setMinimumLogicalFontSize(value);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_DEFAULT_FIXED_FONT_SIZE:
                    try {
                        int value = params.getInt(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setDefaultFixedFontSize------>>>" + value);
                            }
                            webView.getSettings().setDefaultFixedFontSize(value);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_BLOCK_NETWORK_IMAGE:
                    try {
                        boolean value = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setBlockNetworkImage------>>>" + value);
                            }
                            webView.getSettings().setBlockNetworkImage(value);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_ALLOW_UNIVERSAL_ACCESS_FROM_FILE_URLS:
                    try {
                        boolean value = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setAllowUniversalAccessFromFileURLs------>>>" + value);
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                webView.getSettings().setAllowUniversalAccessFromFileURLs(value);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_ALLOW_FILE_ACCESS_FROM_FILE_URLS:
                    try {
                        boolean value = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setAllowFileAccessFromFileURLs------>>>" + value);
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                webView.getSettings().setAllowFileAccessFromFileURLs(value);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_DATABASE_PATH:
                    try {
                        String value = params.getString(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setDatabasePath------>>>" + value);
                            }
                            webView.getSettings().setDatabasePath(value);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_GEOLOCATION_DATABASE_PATH:
                    try {
                        String value = params.getString(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setGeolocationDatabasePath------>>>" + value);
                            }
                            webView.getSettings().setGeolocationDatabasePath(value);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_USER_AGENT_STRING:
                    try {
                        String value = params.getString(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setUserAgentString------>>>" + value);
                            }
                            webView.getSettings().setUserAgentString(value);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_MIXED_CONTENT_MODE:
                    try {
                        int value = params.getInt(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setMixedContentMode------>>>" + value);
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                webView.getSettings().setMixedContentMode(value);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;


                case OP_SET_OFFSCREEN_PRE_RASTER:
                    try {
                        boolean value = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setOffscreenPreRaster------>>>" + value);
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                webView.getSettings().setOffscreenPreRaster(value);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_SAFE_BROWSING_ENABLED:
                    try {
                        boolean value = params.getBoolean(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setSafeBrowsingEnabled------>>>" + value);
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                webView.getSettings().setSafeBrowsingEnabled(value);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_FORCE_DARK:
                    try {
                        int value = params.getInt(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setForceDark------>>>" + value);
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                webView.getSettings().setForceDark(value);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;

                case OP_SET_DISABLED_ACTION_MODE_MENU_ITEMS:
                    try {
                        int value = params.getInt(0);
                        if (webView != null && webView.getSettings() != null) {
                            if (L.DEBUG) {
                                L.logD("#---setDisabledActionModeMenuItems------>>>" + value);
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                webView.getSettings().setDisabledActionModeMenuItems(value);
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    break;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy(ESWebView view) {

    }
}
