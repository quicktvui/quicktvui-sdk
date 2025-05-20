package com.quicktvui.support.core.module.file;

import android.content.Context;
import android.util.SparseArray;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;
import com.sunrain.toolkit.utils.log.L;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 */
@ESKitAutoRegister
public class AndroidFileModule implements IEsModule, IEsInfo {

    private static final AtomicInteger fileIds = new AtomicInteger(0);
    private final SparseArray<File> fileSparseArray = new SparseArray();

    @Override
    public void init(Context context) {
    }

    /**
     *
     */
    public void newFile(EsMap params, EsPromise promise) {
        if (params == null || !params.containsKey("pathname")) {
            return;
        }
        String pathname = params.getString("pathname");
        if (L.DEBUG) {
            L.logD("#---------newFile---------->>>pathname:" + pathname);
        }
        try {
            String rootPath = EsProxy.get().getEsAppPath(this);
            File file = new File(rootPath + pathname);
            if (L.DEBUG) {
                L.logD("#---------newFile---------->>>file:" + file.getAbsolutePath());
            }
            int fileId = fileIds.addAndGet(1);
            fileSparseArray.put(fileId, file);
            promise.resolve(fileId);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.resolve(-1);
        }
    }

    public void newESFile(EsMap params, EsPromise promise) {
        if (params == null || !params.containsKey("pathname")) {
            return;
        }
        String pathname = params.getString("pathname");
        try {
            File file = new File(pathname);
            int fileId = fileIds.addAndGet(1);
            fileSparseArray.put(fileId, file);
            promise.resolve(fileId);
        } catch (Throwable e) {
            e.printStackTrace();
            promise.resolve(-1);
        }
    }

    public void getName(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.getName());
            } else {
                promise.reject("file is null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void getParent(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.getParent());
            } else {
                promise.reject("file is null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void getPath(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.getPath());
            } else {
                promise.reject("file is null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void isAbsolute(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.isAbsolute());
            } else {
                promise.reject("file is null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void getAbsolutePath(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.getAbsolutePath());
            } else {
                promise.reject("file is null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void getCanonicalPath(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.getCanonicalPath());
            } else {
                promise.reject("file is null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void canRead(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.canRead());
            } else {
                promise.reject("file is null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void canWrite(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.canWrite());
            } else {
                promise.reject("file is null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void exists(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.exists());
            } else {
                promise.reject("file is null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }


    public void isDirectory(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.isDirectory());
            } else {
                promise.reject("file is null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void isFile(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.isFile());
            } else {
                promise.reject("file is null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void isHidden(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.isHidden());
            } else {
                promise.reject("file is null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void lastModified(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.lastModified());
            } else {
                promise.reject("file is null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void length(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.length());
            } else {
                promise.reject("file is null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void createNewFile(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.createNewFile());
            } else {
                promise.reject("file is null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void deleteOnExit(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                file.deleteOnExit();
                promise.resolve(true);
            } else {
                promise.reject("file is null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void list(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                EsArray esArray = new EsArray();
                String[] fileList = file.list();
                if (fileList != null && fileList.length > 0) {
                    for (int i = 0; i < fileList.length; i++) {
                        esArray.pushString(fileList[i]);
                    }
                }
                promise.resolve(esArray);
            } else {
                promise.reject("file is null");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void mkdir(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.mkdir());
            } else {
                promise.reject("file is null");
            }

        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void mkdirs(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.mkdirs());
            } else {
                promise.reject("file is null");
            }

        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }


    public void setLastModified(EsMap params, EsPromise promise) {
        try {
            long time = params.getLong("time");
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.setLastModified(time));
            } else {
                promise.reject("file is null");
            }

        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }


    public void getTotalSpace(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.getTotalSpace());
            } else {
                promise.reject("file is null");
            }

        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void getFreeSpace(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.getFreeSpace());
            } else {
                promise.reject("file is null");
            }

        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void getUsableSpace(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.getUsableSpace());
            } else {
                promise.reject("file is null");
            }

        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }


    public void delete(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.delete());
            } else {
                promise.reject("file is null");
            }

        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void setReadOnly(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.setReadOnly());
            } else {
                promise.reject("file is null");
            }

        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void setWritableOwnerOnly(EsMap params, EsPromise promise) {
        try {
            boolean writable = params.getBoolean("writable");
            boolean ownerOnly = params.getBoolean("ownerOnly");
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.setWritable(writable, ownerOnly));
            } else {
                promise.reject("file is null");
            }

        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void setWritable(EsMap params, EsPromise promise) {
        try {
            boolean writable = params.getBoolean("writable");
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.setWritable(writable));
            } else {
                promise.reject("file is null");
            }

        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void setReadableOwnerOnly(EsMap params, EsPromise promise) {
        try {
            boolean readable = params.getBoolean("readable");
            boolean ownerOnly = params.getBoolean("ownerOnly");
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.setReadable(readable, ownerOnly));
            } else {
                promise.reject("file is null");
            }

        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void setReadable(EsMap params, EsPromise promise) {
        try {
            boolean readable = params.getBoolean("readable");
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.setReadable(readable));
            } else {
                promise.reject("file is null");
            }

        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }


    public void setExecutableOwnerOnly(EsMap params, EsPromise promise) {
        try {
            boolean executable = params.getBoolean("executable");
            boolean ownerOnly = params.getBoolean("ownerOnly");
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.setExecutable(executable, ownerOnly));
            } else {
                promise.reject("file is null");
            }

        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void setExecutable(EsMap params, EsPromise promise) {
        try {
            boolean executable = params.getBoolean("executable");
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.setExecutable(executable));
            } else {
                promise.reject("file is null");
            }

        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void canExecute(EsMap params, EsPromise promise) {
        try {
            File file = getFile(params);
            if (file != null) {
                promise.resolve(file.canExecute());
            } else {
                promise.reject("file is null");
            }

        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void renameTo(EsMap params, EsPromise promise) {
        try {
            int renameFileId = params.getInt("renameFileId");
            File renameFile = getFile(renameFileId);

            File file = getFile(params);
            if (file != null && renameFile != null) {
                promise.resolve(file.renameTo(renameFile));
            } else {
                promise.reject("file is null");
            }

        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }

    public void close(EsMap params, EsPromise promise) {
        try {
            if (params != null && params.containsKey("id")) {
                int fileId = params.getInt("id");
                fileSparseArray.remove(fileId);
                promise.resolve(true);
            } else {
                promise.reject(false);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            promise.reject(e.getMessage());
        }
    }


    private File getFile(EsMap params) {
        if (params == null || !params.containsKey("id")) {
            return null;
        }
        int fileId = params.getInt("id");
        return getFile(fileId);
    }

    private File getFile(int fileId) {
        File file = (File) this.fileSparseArray.get(fileId, null);
        return file;
    }

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
        fileSparseArray.clear();
    }
}
