package com.quicktvui.support.player.ijk.player;

import android.annotation.SuppressLint;
import android.content.Context;

import com.quicktvui.sdk.base.EsException;
import com.quicktvui.sdk.base.ISoManager;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.support.player.ijk.setting.Settings;
import com.sunrain.toolkit.utils.log.L;


import com.quicktvui.support.ijk.base.IjkLibLoader;
import com.quicktvui.support.ijk.base.IjkMediaPlayer;

/**
 *
 */
public class IjkLibManager {

    private Context mContext;
    private static IjkLibManager instance;
    private static boolean isLibLoaded = false;
    // so for test
    private static final String TEST_SO_TAG = "test.so";
    // so for common
    private static final String COMMON_SO_TAG = "eskit.so.player.v1";;
    // so for network disk
    private static final String NETWORK_DISK_SO_TAG = "eskit.so.player.disk";
    private String IJK_SO_TAG = COMMON_SO_TAG;

    public static final int SO_TAG_TYPE_COMMON = 1;
    public static final int SO_TAG_TYPE_NETWORK_DISK = 2;
    public static final int SO_TAG_TYPE_TEST = 3;

    private boolean ijkDynamically = false;

    private IjkLibManager() {
    }

    public static IjkLibManager getInstance() {
        synchronized (IjkLibManager.class) {
            if (instance == null) {
                instance = new IjkLibManager();
            }
        }
        return instance;
    }

    public void init(Context context) {
        mContext = context;
    }

    public void setIjkSoTag(String ijkSoTag) {
        IJK_SO_TAG = ijkSoTag;
    }

    public void setIjkSoTagType(int tagType) {
        switch (tagType) {
            case SO_TAG_TYPE_NETWORK_DISK:
                IJK_SO_TAG = NETWORK_DISK_SO_TAG;
                break;
            case SO_TAG_TYPE_TEST:
                IJK_SO_TAG = TEST_SO_TAG;
                break;
            default:
                IJK_SO_TAG = COMMON_SO_TAG;

        }
    }

    public void setIjkDynamically(boolean isDynamically) {
        ijkDynamically = isDynamically;
    }

    public void loadLibrary(Settings settings, IIjkLibLoadCallback callback) {
        if (mContext != null) {
            int playerType = settings.getPlayerType();
            if (Settings.PV_PLAYER__AndroidMediaPlayer == playerType) {
                callback.onLibraryLoadSuccess();
            } else {
                loadLibrary(false, callback);
            }
        } else
            loadLibrary(false, callback);
    }

    /**
     * 加载或更新so
     *
     * @param cacheFistModel 是否强制使用缓存，
     *                      true -> 本地有缓存的情况，只会使用缓存;
     *                      false -> 没加载到内存的情况下，会优先请求接口检测更新，频率，一天检测一次
     * @param callback
     */
    public void loadLibrary(boolean cacheFistModel, IIjkLibLoadCallback callback) {
        if (L.DEBUG) {
            L.logD("-----------loadLibrary-----start--->>>>>");
        }
        //1.不支持动态so加载
        if (!EsProxy.get().isContainsFlag(1 << 2)) {
            if (L.DEBUG) {
                L.logD("-----------loadLibrary-----不支持动态so加载--->>>>>");
            }
            if (callback != null) {
                callback.onLibraryLoadSuccess();
            }
            return;
        }
        //3.已经下载过so了
        if (isLibraryLoaded()) {
            if (L.DEBUG) {
                L.logD("-----------loadLibrary-----isLibraryLoaded--->>>>>");
            }
            initPlayerLibrary(callback);
            return;
        }
        //4.开始下载so
        ISoManager soManager = EsProxy.get().getSoManager();
        if (soManager != null) {
            soManager.prepareSoFiles(IJK_SO_TAG, cacheFistModel, new ISoManager.Callback() {
                @Override
                public void onSuccess() {
                    if (L.DEBUG) {
                        L.logD("-----------loadLibrary---1--onSuccess--->>>>>");
                    }
                    initPlayerLibrary(callback);
                }

                @Override
                public void onError(EsException e) {
                    e.printStackTrace();
                    if (L.DEBUG) {
                        L.logD("-----------loadLibrary-----error--->>>>>" + e.getMessage());
                    }
                    if (callback != null) {
                        callback.onLibraryLoadError(e);
                    }
                }
            });
        }
        //
        else {
            if (L.DEBUG) {
                L.logD("-----------loadLibrary-----SoManager is null--->>>>>");
            }
            if (callback != null) {
                callback.onLibraryLoadError(new Exception("soManager is null..."));
            }
        }
    }

    private void initPlayerLibrary(IIjkLibLoadCallback callback) {
        // 保证同步
        IjkMediaPlayer.loadLibrariesOnce(new IjkLibLoader() {
            private boolean loading = true;

            @SuppressLint("UnsafeDynamicallyLoadedCode")
            @Override
            public void loadLibrary(String libName) {
                if (L.DEBUG) {
                    L.logD("-----------loadLibrary----libName--->>>>>" + libName);
                }
                if (loading) {
                    try {
                        EsProxy.get().getSoManager().loadLibrary(IJK_SO_TAG, libName);
                        if (L.DEBUG) {
                            L.logD("-----------loadLibrary----success--->>>>>");
                        }
                        isLibLoaded = true;
                    } catch (Throwable e) {
                        if (L.DEBUG) {
                            L.logE("-----------loadLibrary----error--->>>>>" + e.getMessage());
                        }
                        e.printStackTrace();
                        loading = false;
                        isLibLoaded = false;
                    }
                }

            }
        });

        //全部成功
        if (isLibLoaded) {
            if (L.DEBUG) {
                L.logD("----------loadLibrary----全部成功------>>>>>");
            }
            //success
            if (callback != null) {
                callback.onLibraryLoadSuccess();
            }
        }
        //有失败
        else {
            if (L.DEBUG) {
                L.logD("-----2------loadLibrary----有失败------>>>>>");
            }
            //error
            if (callback != null) {
                callback.onLibraryLoadError(new Throwable("dynamic load so error!!"));
            }
        }
    }

    public boolean isLibraryLoaded() {
        return isLibLoaded;
    }

    public interface IIjkLibLoadCallback {
        void onLibraryLoadSuccess();

        void onLibraryLoadError(Throwable e);
    }
}
