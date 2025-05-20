package com.quicktvui.sdk.core.internal;

import android.content.Context;
import android.text.format.DateUtils;

import com.sunrain.toolkit.utils.Utils;

import java.io.File;

/**
 * Create by weipeng on 2022/03/01 18:36
 */
public class Constants {

    public static final int DEV_FIX_WIDTH = 1920;

    // 缓存检测间隔
    public static final long ES_CACHE_CHECK_INTERVAL = 3 * DateUtils.MINUTE_IN_MILLIS;
    //    public static final long ES_CACHE_CHECK_INTERVAL = 30 * DateUtils.SECOND_IN_MILLIS;
    // 最大缓存大小
    public static final long ES_CACHE_MAX_SIZE = 200 * 1024 * 1024;
//    public static final long ES_CACHE_MAX_SIZE = 30 * 1024 * 1024;

    public static final String ES_ERROR_RPK_PACKAGE = "es.extscreen.runtime.error";
    public static final String ES_SETTING_RPK_PACKAGE = "es.extscreen.runtime.setting";

    public static final String ES_VUE_RUNTIME_PKG_APP = "eskit.runtime.app";   // 下载app vendor的标记
    public static final String ES_VUE_RUNTIME_PKG_CARD = "eskit.runtime.card"; // 下载card vendor的标记

    public static final String LINK_COMPONENT_CLASS_NAME = "eskit.sdk.core.mediasession.EsMediaPlayerService";

    //region 传值KEY

    public static final String K_APP_NAME = "appName";
    public static final String K_APP_ICON = "appIcon";
    public static final String K_APP_PACKAGE = "pkg";
    public static final String K_APP_VERSION = "ver";
    public static final String K_APP_REPO = "repo";

    //endregion

    public interface Event {
        String ES_REFERER = "es_referer";
        String ES_REFERER1 = "es_refererex1";
        String ES_REFERER2 = "es_refererex2";
        String[] ES_REFERER_LIST = {
                ES_REFERER,
                ES_REFERER1,
                ES_REFERER2,
        };

        /** 手机端投屏 **/
        int FROM_REMOTE = 0;
        /** 外部应用 **/
        int FROM_OUTER = 1;
        /** ES内部跳转 **/
        int FROM_ES_APP = 2;
        /** 测试专用 **/
        int FROM_TEST = 9999;

        /** UDP **/
        int FROM_UDP = 1;
        /** DLNA协议 **/
        int FROM_DLNA_PROTOCOL = 2;
        /** DLNA链接拼参数 **/
        int FROM_DLNA_URI = 3;
        /** MediaSession **/
        int FROM_MEDIA_SESSION = 4;
        /** 从Proxy启动(TCL) **/
        int FROM_PROXY = 5;
        /** 从WebSocket启动 **/
        int FROM_WEB_SOCKET = 6;
        /** 从AIDL启动 **/
        int FROM_AIDL = 7;
    }

    public interface INIT {
        int ERROR_BLOCK = -2;
        int ERROR_CID = -3;
    }

    public interface EsData {
        String K_PKG = "pkg";
        String K_VER = "ver";
        String K_REPO = "repository";
    }

    public interface Nexus {
        String REPO_META = "/meta.json";

        interface Meta {

            String K_LATEST = "latest";
            String K_VERSIONS = "versions";

            String K_APP_NAME_V1 = "name";
            String K_APP_NAME_V2 = Constants.K_APP_NAME;

            String K_APP_PKG_V1 = K_APP_PACKAGE;
            String K_APP_PKG_V2 = "name";

            String K_APP_ICON = "icon";

            String K_APP_PATH = "path";
            String K_APP_MD5 = "md5";
        }
    }

    //region 服务端口

    public static final int SERVER_PORT_UDP = 5000;
    public static final int SERVER_PORT_WS = 6000;
    public static final int SERVER_PORT_HTTP = 8000;

    //endregion

    //region 加载报错

    public static final int ERR_PRINT = -1;

    // 引擎错误
    /**
     * JsBridge出错
     **/
    public static final int ERR_BRIDGE = -101;
    /**
     * 连接DevServer失败
     **/
    public static final int ERR_DEVSERVER = -102;
    /**
     * 引擎状态不对
     **/
    public static final int ERR_WRONG_STATE = -103;
    /**
     * 加载代码出错
     **/
    public static final int ERR_INIT_EXCEPTION = -104;
    /**
     * 初始化超时
     **/
    public static final int ERR_INIT_TIMEOUT = -105;

    // 请求信息
    /**
     * 无网
     **/
    public static final int ERR_OFFLINE = -1000;
    /**
     * 服务端超时
     **/
    public static final int ERR_TIME_OUT = -1001;
    /**
     * 服务端出错
     **/
    public static final int ERR_SERVER = -1002;
    /**
     * 获取包信息失败
     **/
    public static final int ERR_INFO = -1003;
    /**
     * 服务端其它错误
     **/
    public static final int ERR_UNKNOWN = -1004;
    /**
     * 快应用版本不匹配
     **/
    public static final int ERR_VER_MATCH_FAIL = -1005;

    // 下载
    /**
     * 下载信息不全
     **/
    public static final int ERR_DOWNLOAD_INFO = -2000;
    /**
     * 下载失败
     **/
    public static final int ERR_DOWNLOAD = -2001;

    // 解压解密
    /**
     * 解密出错
     **/
    public static final int ERR_DECRYPT = -3000;
    /**
     * MD5不匹配
     **/
    public static final int ERR_MD5 = -3001;
    /**
     * 解压失败
     **/
    public static final int ERR_UNZIP = -3002;
    /**
     * ZIP不存在
     **/
    public static final int ERR_ZIP = -3003;
    /**
     * 包信息有误
     **/
    public static final int ERR_ZIP_FILES = -3004;

    // Assets加载
    /**
     * Assets加载失败
     **/
    public static final int ERR_ASSET = -4000;

    /**
     * 应用封禁
     **/
    public static final int ERR_APP_BLOCK = 20010;
    /**
     * 未找到快应用版本
     **/
    public static final int ERR_APP_NOT_FOUND = 20006;
    /**
     * 鉴权失败
     **/
    public static final int ERR_AUTH_FAILED = 20000;

    // Runtime
    /**
     * 线程错误
     **/
    public static final int ERR_RUNTIME_THREAD = -5000;
    /**
     * 读取接口错误
     **/
    public static final int ERR_RUNTIME_SERVER_ERR = -5001;
    /**
     * 接口数据解析失败
     **/
    public static final int ERR_RUNTIME_PARSE_ERR = -5002;
    /**
     * 没找到指定版本的Runtime
     **/
    public static final int ERR_RUNTIME_NOT_SUPPORT = -5003;

    //endregion

    //region 事件
    public interface GLOBAL_EVENT {
        // 内存变化事件
        String EVT_MEMORY_LEVEL_CHANGE = "OnMemoryLevelChange";
        // 低内存
        String EVT_MEMORY_LOW = "OnMemoryLow";

        // 生命周期
        String EVT_LIFE_CHANGE = "LifecycleChange";
        String EVT_LIFE_CHANGE_FRAGMENT = "onLifecycleChange";
        // Activity抛出
        String EVT_ON_NEW_INTENT = "OnNewIntent";
        // Activity向上抛出的事件
        String EVT_DISPATCH_KEY = "DispatchKeyEvent";

        // 接收到DLNA事件
        String EVT_DISPATCH_DLNA = "EVT_DLNA";

        // 插件状态事件
        String EVT_PLUGIN_EVENT = "onESPluginStateChanged";
        // Link协议消息
        String EVT_LINK_RECEIVE = "OnLinkEvent";

        String LIFE_CREATE = "onCreate";
        String LIFE_START = "onStart";
        String LIFE_RESUME = "onResume";
        String LIFE_PAUSE = "onPause";
        String LIFE_STOP = "onStop";
        String LIFE_DESTROY = "onDestroy";

        String EVT_ON_APP_OPEN = "onAppOpen";
        String EVT_ON_APP_CLOSE = "onAppClose";

        String EVT_ON_REQ_SLOT_CREATE = "onRequestCreateSlot";
        String EVT_ON_REQ_SLOT_DESTROY = "onRequestDestroySlot";
        String EVT_ON_SLOT_BIND = "onBindViewHolder";
        String EVT_ON_SLOT_ATTACH = "onViewAttachedToWindow";
        String EVT_ON_SLOT_DETACH = "onViewDetachedFromWindow";
        String EVT_ON_SLOT_RECYCLE = "onViewRecycled";
        String EVT_ON_SLOT_CUSTOM = "onCustomEvent";
        String EVT_ON_SLOT_DISPATCH_KEY = "onDispatchKeyEvent";
        String EVT_ON_SLOT_BACK_PRESSED = "onHardwareBackPress";
    }

    public static final String EVT_APP_OPEN_ERROR = "onAppOpenError";
    public static final String EVT_LINK_FROM_K = "__from__";
    public static final String EVT_LINK_FROM_V_MEDIASESSION = "media_session";
    public static final String EVT_LINK_FROM_V_CAST_DLNA = "cast_dlna";
    public static final String EVT_LINK_FROM_V_CAST_AIRPLAY = "airplay";

    public static final String LINK_ACTION_PLAY = "play";
    public static final String LINK_ACTION_FAST_FORWARD = "fastForward";
    public static final String LINK_ACTION_PLAY_FROM_URI = "playFromUri";
    public static final String LINK_ACTION_PLAY_FROM_SEARCH = "playFromSearch";

    public static final String LINK_ACTION_PLAY_FROM_MEDIA_ID = "playFromMediaId";
    public static final String LINK_ACTION_PAUSE = "pause";
    public static final String LINK_ACTION_SKIP_TO_NEXT = "skipToNext";

    public static final String LINK_ACTION_REWIND = "rewind";

    public static final String LINK_ACTION_PREPARE_FROM_URI="prepareFromUri";
    public static final String LINK_ACTION_PREPARE_FROM_SEARCH="prepareFromSearch";

    public static final String LINK_ACTION_PREPARE_FROM_MEDIA_ID="prepareFromMediaId";
    public static final String LINK_ACTION_PREPARE = "prepare";
    public static final String LINK_ACTION_SKIP_TO_PREVIOUS = "skipToPrevious";
    public static final String LINK_ACTION_STOP = "stop";
    public static final String LINK_ACTION_SEEK_TO = "seekTo";
    public static final String LINK_ACTION_SKIP_TO_QUEUE_ITEM = "skipToQueueItem";

    public static final String LINK_ACTION_SET_VOLUME = "setVolume";
    public static final String LINK_ACTION_SET_LOVE = "setCollection";
    public static final String LINK_ACTION_SET_REPEAT_MODEL = "setRepeatModel";

    public static final String LINK_ACTION_SET_SHUFFLE_MODEL = "setShuffleMode";


    public static final String LINK_ACTION_SET_PLAY_SPEED = "setSpeed";
    public static final String LINK_ACTION_RESOLUTION_SWITCHING = "resolutionSwitching";
    public static final String LINK_ACTION_FULLSCREEN = "fullscreen";
    public static final String LINK_ACTION_SKIP_BEGINNING_ENDING = "skipBegingEnding";


    //endregion

    // 后台后发送广播
    public static final String ACTION_ENTER_BACKGROUND = ".ACTION_ENTER_BACKGROUND";


    //region 目录

    public static final String PATH_ROOT = "rpk";
    public static final String PATH_PLUGIN = "plugins";
    public static final String PATH_APP = "apps";
    public static final String PATH_CARD = "cards";
    public static final String PATH_LIBs = "libs";
    public static final String PATH_RUNTIME = "runtimes";
    public static final String PATH_RPK_MD5 = ".md5";
    /**
     * 可读写目录
     **/
    public static final String PATH_APP_FILES = "files";
    public static final String PATH_RPK_ASSETS = "eslocal";
    public static final String PATH_RPK_SUFFIX = ".rpk";
    public static final String PATH_ZIP_SUFFIX = ".zip";
    public static final String PATH_RPK_CODE = "android";
    public static final String FILE_JS_VENDOR = "vendor.android.js";
    public static final String FILE_JS_INDEX = "index.android.js";
    public static final String FILE_PACKAGE_JSON = "package.json";
    public static final String PACKAGE_JSON_K_SPLASH = "splash";
    public static final String PACKAGE_JSON_K_SPLASH_MSG = "msg";
    public static final String PACKAGE_JSON_K_RUNTIME_SUFFIX = "runtimeSuffix";
    public static final String PACKAGE_JSON_K_RUNTIME_MIN_VERSION = "minRuntime";

    public static final String FILE_UPDATE_APK = "host-update.apk";

    public static File getRootDir() {
        return Utils.getApp().getDir(PATH_ROOT, Context.MODE_PRIVATE);
    }

    public static File getPluginDir() {
        return createDirectory(new File(getRootDir(), PATH_PLUGIN));
    }

    public static File getEsAppDir() {
        return createDirectory(new File(getRootDir(), PATH_APP));
    }

    public static File getEsCardDir() {
        return createDirectory(new File(getRootDir(), PATH_CARD));
    }

    public static File getEsSoDir() {
        return createDirectory(new File(getRootDir(), PATH_LIBs));
    }

    public static File getCacheDir() {
        return createDirectory(new File(Utils.getApp().getCacheDir(), PATH_ROOT));
    }

    public static File getRuntimeDir() {
        return createDirectory(new File(getRootDir(), PATH_RUNTIME));
    }

    private static File createDirectory(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    //endregion

}
