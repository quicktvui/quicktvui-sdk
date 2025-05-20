package com.quicktvui.support.device.info.utils;

import android.annotation.SuppressLint;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.support.v4.content.ContextCompat;
import android.text.format.Formatter;
import android.util.Log;

import com.quicktvui.support.device.info.model.beans.StorageBean;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.UUID;


public class SdUtils {

    public static boolean isMounted() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static String getDirPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    private static long totalStore = 0L;

    /**
     * 获取 sd 卡存储信息
     *
     * @param context
     * @param bean
     */
    public static void getStoreInfo(Context context, StorageBean bean) {
        File externalStorageDirectory = Environment.getExternalStorageDirectory();
        File dataDirectory = Environment.getDataDirectory();
        File cacheDir = context.getCacheDir();
        File fileDir = context.getFilesDir();
        totalStore = 0;
        String romStorage = getRealStorage(context);
        long freeSpace = 0L;
        long freeSpace22 = 0L;
        long availableStorage = 0L;
        long usableSpace = 0L;
        String free = "";
        bean.setStorePath(externalStorageDirectory.getAbsolutePath());
        long totalSpace = externalStorageDirectory.getTotalSpace();
        freeSpace = externalStorageDirectory.getFreeSpace();
        freeSpace22 = externalStorageDirectory.getUsableSpace();//todo ch9632 该参数返回也是0
        //freeSpace33 = externalStorageDirectory.getTotalSpace();//todo ch9632 该参数返回也是0

        //testApi(context);

        /*Log.d("test", "totalSpace: -----" + totalSpace);
        Log.d("test", "freeSpace: -----" + freeSpace);
        Log.d("test", "freeSpace22: -----" + freeSpace22);
        Log.d("test", "cacheDir: -----" + cacheDir.getUsableSpace());
        Log.d("test", "fileDir: -----" + fileDir.getUsableSpace());*/

        if (freeSpace == 0) {
            freeSpace = externalStorageDirectory.getUsableSpace();//todo ch9632 该参数返回也是0
            if (freeSpace == 0) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    StatFs statEx = new StatFs(Environment.getExternalStorageDirectory().getPath());
                    try {
                        availableStorage = statEx.getAvailableBytes();
                    } catch (NoSuchMethodError e3) {
                        availableStorage = ((long) statEx.getBlockSize()) * ((long) statEx.getAvailableBlocks());
                    }
                    //Log.d("test", "getStoreInfo1: ------------->" + availableStorage);
                    if (availableStorage == 0) {
                        StatFs dataFs = new StatFs(dataDirectory.getPath());
                        try {
                            availableStorage = dataFs.getAvailableBytes();
                        } catch (NoSuchMethodError e3) {
                            availableStorage = ((long) dataFs.getBlockSize()) * ((long) dataFs.getAvailableBlocks());
                        }
                        //Log.d("test", "getStoreInfo1:dataFs数据 ------------->" + availableStorage);
                    }
                }
            }
        }
        /*Log.d("test", "getStoreInfo2: ------------->" + freeSpace);
        Log.d("test", "getStoreInfo3 totalStore1 : ------------->" + totalStore);
        Log.d("test", "getStoreInfo3: totalSpace2 ------------->" + totalSpace);
        Log.d("test", "getStoreInfo3:availableStorage ------------->" + availableStorage);*/
        if (freeSpace == 0) {
            usableSpace = totalStore > 0 ? totalStore - availableStorage : totalSpace - availableStorage;
            //Log.d("test", "usableSpace11 ------------->" + usableSpace);
        } else {
            usableSpace = totalStore > 0 ? totalStore - freeSpace : totalSpace - freeSpace;
            //Log.d("test", "usableSpace22 ------------->" + usableSpace);
        }
        String total = Formatter.formatFileSize(context, totalSpace);
        String usable = Formatter.formatFileSize(context, usableSpace);
        if (freeSpace == 0) {
            free = Formatter.formatFileSize(context, availableStorage);
        } else {
            free = Formatter.formatFileSize(context, freeSpace);
        }
        bean.setTotalStore(total);
        bean.setFreeStore(free);
        bean.setUsedStore(usable);
        int ratio = (int) ((usableSpace / (double) totalSpace) * 100);
        bean.setRatioStore(ratio);
        bean.setRomSize(romStorage);
    }

    private static void testApi(Context context) {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        List<StorageVolume> storageVolumes = null;

        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long blockSize = statFs.getBlockSize();
        long totalBlocks = statFs.getBlockCount();
        long availableBlocks = statFs.getAvailableBlocks();

        long totalSpace = blockSize * totalBlocks;
        long availableSpace = blockSize * availableBlocks;

        Log.d("test", "testApi:1 ------------>" + totalSpace);
        Log.d("test", "testApi:2 ------------>" + availableSpace);

        File[] externalStorageDirs = ContextCompat.getExternalFilesDirs(context, null);
        for (File dir : externalStorageDirs) {
            if (dir != null) {
                Log.d("Storage", "External storage directory: " + dir.getAbsolutePath());
                Log.d("Storage", "External storage directory: " + dir.getTotalSpace());
                Log.d("Storage", "External storage directory: " + dir.getUsableSpace());
            }
        }

        StatFs statFsroot = new StatFs(Environment.getRootDirectory().getPath());
        StatFs statFsdata = new StatFs(Environment.getDataDirectory().getPath());
        long blockSize1 = statFsroot.getBlockSize();
        long totalBlocks1 = statFsroot.getBlockCount();
        long availableBlocks1 = statFsroot.getAvailableBlocks();

        long blockSize2 = statFsdata.getBlockSize();
        long totalBlocks2 = statFsdata.getBlockCount();
        long availableBlocks2 = statFsdata.getAvailableBlocks();

        long totalSpace1 = blockSize1 * totalBlocks1;
        long availableSpace1 = blockSize1 * availableBlocks;

        Log.d("test", "testApi:1 ------------>" + totalSpace);
        Log.d("test", "testApi:2 ------------>" + availableSpace);


        Log.d("test", "testApi:3 ------------>" + availableBlocks1);
        Log.d("test", "testApi:4 ------------>" + blockSize2);
        Log.d("test", "testApi:5 ------------>" + totalBlocks2);
        Log.d("test", "testApi:6------------>" + availableBlocks2);
        Log.d("test", "testApi:7 ------------>" + totalSpace1);
        Log.d("test", "testApi:8 ------------>" + availableSpace1);


        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {//大于7.0
            storageVolumes = storageManager.getStorageVolumes();
            for (StorageVolume volume : storageVolumes) {
                if (volume.isRemovable()) {
                    String path = volume.getDirectory().getPath();
                    Log.d("Storage", "External storage path: " + path);
                }
            }
        }*/
    }

    /**
     * 获取 ROM 空间大小
     *
     * @param context
     * @return
     */
    private static String getRomTotal(Context context) {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return Formatter.formatFileSize(context, totalBlocks * blockSize);
    }

    @SuppressLint("DiscouragedPrivateApi")
    public static String getRealStorage(Context context) {
        try {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            int version = Build.VERSION.SDK_INT;
            float unit = version >= Build.VERSION_CODES.O ? 1000 : 1024;
            if (version < Build.VERSION_CODES.M) {
                Method getVolumeList = StorageManager.class.getDeclaredMethod("getVolumeList");
                StorageVolume[] volumeList = (StorageVolume[]) getVolumeList.invoke(storageManager);
                if (volumeList != null) {
                    Method getPathFile = null;
                    for (StorageVolume volume : volumeList) {
                        if (getPathFile == null) {
                            getPathFile = volume.getClass().getDeclaredMethod("getPathFile");
                        }
                        File file = (File) getPathFile.invoke(volume);
                        totalStore += file.getTotalSpace();
//                        Log.d("test", "file.getTotalSpace(): ------------>" + file.getTotalSpace());
//                        Log.d("test", "totalStore累加: ------------>" + totalStore);
                    }
                }
            } else {
                @SuppressLint("PrivateApi") Method getVolumes = StorageManager.class.getDeclaredMethod("getVolumes");
                List<Object> getVolumeInfo = (List<Object>) getVolumes.invoke(storageManager);
                for (Object obj : getVolumeInfo) {
                    Field getType = obj.getClass().getField("type");
                    int type = getType.getInt(obj);
                    if (type == 1) {
                        long totalSize = 0L;
                        if (version >= Build.VERSION_CODES.O) {
                            Method getFsUuid = obj.getClass().getDeclaredMethod("getFsUuid");
                            String fsUuid = (String) getFsUuid.invoke(obj);
                            totalSize = getTotalSize(context, fsUuid);
//                            Log.d("test", "getRealStorage1: --------->" + totalSize);
                        } else if (version >= Build.VERSION_CODES.N_MR1) {
                            Method getPrimaryStorageSize = StorageManager.class.getMethod("getPrimaryStorageSize");
                            totalSize = (long) getPrimaryStorageSize.invoke(storageManager);
//                            Log.d("test", "getRealStorage2: --------->" + totalSize);
                        }
                        Method isMountedReadable = obj.getClass().getDeclaredMethod("isMountedReadable");
                        boolean readable = (boolean) isMountedReadable.invoke(obj);
                        if (readable) {
                            Method file = obj.getClass().getDeclaredMethod("getPath");
                            File f = (File) file.invoke(obj);
                            if (totalSize == 0) {
                                totalSize = f.getTotalSpace();
                            }
                            totalStore += totalSize;
//                            Log.d("test", "getRealStorage3: --------->" + totalStore);
                        }
                    } else if (type == 0) {
                        Method isMountedReadable = obj.getClass().getDeclaredMethod("isMountedReadable");
                        boolean readable = (boolean) isMountedReadable.invoke(obj);
                        if (readable) {
                            Method file = obj.getClass().getDeclaredMethod("getPath");
                            File f = (File) file.invoke(obj);
                            totalStore += f.getTotalSpace();
//                            Log.d("test", "getRealStorage4: --------->" + totalStore);
                        }
                    }
                }
            }
//            Log.d("test", "getRealStorage5: --------->" + totalStore);
            return getUnit(totalStore, unit);
        } catch (Exception ignore) {

        }
        return null;
    }

    private static String[] units = {"B", "KB", "MB", "GB", "TB"};

    /**
     * 进制转换
     */
    private static String getUnit(float size, float base) {
        int index = 0;
        while (size > base && index < 4) {
            size = size / base;
            index++;
        }
        return String.format(Locale.getDefault(), "%.2f %s ", size, units[index]);
    }

    /**
     * API 26 android O
     * 获取总共容量大小，包括系统大小
     */
    @SuppressLint("NewApi")
    private static long getTotalSize(Context context, String fsUuid) {
        try {
            UUID id;
            if (fsUuid == null) {
                id = StorageManager.UUID_DEFAULT;
            } else {
                id = UUID.fromString(fsUuid);
            }
            StorageStatsManager stats = context.getSystemService(StorageStatsManager.class);
            return stats.getTotalBytes(id);
        } catch (NoSuchFieldError | NoClassDefFoundError | NullPointerException | IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

}
