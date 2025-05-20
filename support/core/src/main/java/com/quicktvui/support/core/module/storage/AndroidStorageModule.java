package com.quicktvui.support.core.module.storage;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;
import com.sunrain.toolkit.utils.log.L;

import java.io.File;

/**
 *
 */
@ESKitAutoRegister
public class AndroidStorageModule implements IEsModule, IEsInfo {

    private static final String TAG = "FileModule";

    private AndroidStorageManager storageManager;

    @Override
    public void init(Context context) {
        storageManager = AndroidStorageManager.getInstance();
    }

    public void getCacheDir(EsPromise promise) {
        try {
            File file = storageManager.getCacheDir();
            if (file != null) {
                String path = file.getAbsolutePath();
                if (L.DEBUG) {
                    L.logD("#---------getCacheDir---------->>>" + path);
                }
                promise.resolve(path);
            } else {
                promise.reject("file is null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void getFilesDir(EsPromise promise) {
        try {
            File file = storageManager.getFilesDir();
            if (file != null) {
                String path = file.getAbsolutePath();
                if (L.DEBUG) {
                    L.logD("#---------getFilesDir---------->>>" + path);
                }
                promise.resolve(path);
            } else {
                promise.reject("file is null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void getExternalCacheDir(EsPromise promise) {
        try {
            File file = storageManager.getExternalCacheDir();
            if (file != null) {
                String path = file.getAbsolutePath();
                if (L.DEBUG) {
                    L.logD("#---------getExternalCacheDir---------->>>" + path);
                }
                promise.resolve(path);
            } else {
                promise.resolve("");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }


    public void getExternalStorageState(EsPromise promise) {
        try {
            promise.resolve(storageManager.getExternalStorageState());
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void hasExternalStoragePermission(EsPromise promise) {
        try {
            promise.resolve(storageManager.hasExternalStoragePermission());
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }
    //------------------------------------------------------------------------------

    public boolean isExternalStorageAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 机身总存储
     *
     * @param promise
     * @return
     */
    public void getInternalStorageTotalSize(EsPromise promise) {
        try {
            File file = Environment.getDataDirectory();
            StatFs sf = new StatFs(file.getPath());
            long blockSize = sf.getBlockSize();
            long totalBlocks = sf.getBlockCount();
            long totalSize = blockSize * totalBlocks;
            if (L.DEBUG) {
                L.logD(file.getPath() + "#---------getInternalStorageTotalSize---------->>>" + totalSize);
            }
            promise.resolve(totalSize);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    /**
     * 机身可用存储空间
     *
     * @param promise
     * @return
     */
    public void getInternalStorageAvailableSize(EsPromise promise) {
        try {
            File file = Environment.getDataDirectory();
            StatFs sf = new StatFs(file.getPath());
            long blockSize = sf.getBlockSize();
            long availableBlocks = sf.getAvailableBlocks();
            long totalSize = blockSize * availableBlocks;
            if (L.DEBUG) {
                L.logD(file.getPath() + "#---------getInternalStorageAvailableSize---------->>>" + totalSize);
            }
            promise.resolve(totalSize);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    //------------------------------------------------------------------------------
    public void getExternalStorageTotalSize(EsPromise promise) {
        try {
            if (isExternalStorageAvailable()) {
                File file = Environment.getExternalStorageDirectory();
                StatFs sf = new StatFs(file.getPath());
                long blockSize = sf.getBlockSize();
                long totalBlocks = sf.getBlockCount();
                long totalSize = blockSize * totalBlocks;
                if (L.DEBUG) {
                    L.logD(file.getPath() + "#---------getExternalStorageTotalSize---------->>>" + totalSize);
                }
                promise.resolve(totalSize);
            } else {
                promise.resolve(-1);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }


    public void getExternalStorageAvailableSize(EsPromise promise) {
        try {
            if (isExternalStorageAvailable()) {
                File file = Environment.getExternalStorageDirectory();
                StatFs sf = new StatFs(file.getPath());
                long blockSize = sf.getBlockSize();
                long availableBlocks = sf.getAvailableBlocks();
                long totalSize = blockSize * availableBlocks;
                if (L.DEBUG) {
                    L.logD(file.getPath() + "#---------getExternalStorageAvailableSize---------->>>" + totalSize);
                }
                promise.resolve(totalSize);
            } else {
                promise.resolve(-1);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    //------------------------------------------------------------------------------
    public void getStorageTotalSize(String filePath, EsPromise promise) {
        try {
            File file = new File(filePath);
            StatFs sf = new StatFs(file.getPath());
            long blockSize = sf.getBlockSize();
            long totalBlocks = sf.getBlockCount();
            long totalSize = blockSize * totalBlocks;
            if (L.DEBUG) {
                L.logD("#---------getStorageTotalSize---------->>>" + totalSize);
            }
            promise.resolve(totalSize);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void getStorageAvailableSize(String filePath, EsPromise promise) {
        try {
            File file = new File(filePath);
            StatFs sf = new StatFs(file.getPath());
            long blockSize = sf.getBlockSize();
            long availableBlocks = sf.getAvailableBlocks();
            long totalSize = blockSize * availableBlocks;
            if (L.DEBUG) {
                L.logD("#---------getStorageAvailableSize---------->>>" + totalSize);
            }
            promise.resolve(totalSize);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }
    //------------------------------------------------------------------------------

    @Override
    public void getEsInfo(EsPromise promise) {
        EsMap map = new EsMap();
        try {
            map.pushInt(IEsInfo.ES_PROP_INFO_VERSION, EsProxy.get().getSdkVersionCode());
            map.pushDouble(IEsInfo.ES_PROP_INFO_ESKIT_VERSION, EsProxy.get().getEsKitVersionCode());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        promise.resolve(map);
    }

    @Override
    public void destroy() {

    }
}
