package com.quicktvui.support.player.ijk.utils;

import android.util.Log;

import java.io.File;
import java.io.IOException;


/**
 * 本地代理存储相关的工具类
 */

public class StorageUtils {

    private static final String TAG = "StorageUtils";

    public static final int DEFAULT_BUFFER_SIZE = 8 * 1024;
    public static final String INFO_FILE = "video.info";
    public static final String LOCAL_M3U8_SUFFIX = "_local.m3u8";
    public static final String PROXY_M3U8_SUFFIX = "_proxy.m3u8";
    public static final String M3U8_SUFFIX = ".m3u8";
    public static final String NON_M3U8_SUFFIX = ".video";
    public static final String FF_CONCAT_SUFFIX = ".ffconcat";

    private static final Object sInfoFileLock = new Object();

    public static final int CLEAR_CACHE_FLAG_USE_TIME = 1;          // 根据过期时间清除
    public static final int CLEAR_CACHE_FLAG_USE_SIZE = 1 << 1;     // 根据总缓存大小清除
    public static final int CLEAR_CACHE_FLAG_USE_NUMBER = 1 << 2;   // 根据缓存视频数量进行清除



//    public static File getNumberCacheDir(File cacheRoot) {
//        return new File(cacheRoot, "num");
//    }
//
//    public static File getSizeCacheDir(File cacheRoot) {
//        return new File(cacheRoot, "size");
//    }
//
//    public static File getMixCacheDir(File cacheRoot) {
//        return new File(cacheRoot, "mix");
//    }

    public static void clearAllData(File file) {
        if (file == null || !file.exists()) return;
        File[] listFiles = file.listFiles();
        if (listFiles == null) return;
        for (File itemFile : listFiles) { // 这里应该是单条视频的文件夹
            deleteFile(itemFile);
        }
    }

    /**
     * 清理过期的数据 以文件夹为单位
     *
     * @param file        视频缓存路径
     * @param expiredTime 过期时间
     */
    public static void cleanExpiredCacheData(File file, long expiredTime) {
        if (file == null || !file.exists()) return;
        File[] listFiles = file.listFiles();
        if (listFiles == null) return;
        for (File itemFile : listFiles) { // 这里应该是单条视频的文件夹
            Log.d(TAG, "itemFile " + itemFile.getAbsolutePath());
            if (isExpiredCacheData(itemFile.lastModified(), expiredTime)) {
                Log.d(TAG, "deleteFile " + itemFile.getAbsolutePath());
//                delete(itemFile);
                deleteFile(itemFile);
            }
        }
    }

    private static boolean isExpiredCacheData(long lastModifiedTime, long expiredTime) {
        long now = System.currentTimeMillis();
        return Math.abs(now - lastModifiedTime) > expiredTime;
    }

    private static void delete(File file) throws IOException {
        if (file.isFile() && file.exists()) {
            boolean isDeleted = file.delete();
            if (!isDeleted) {
                throw new IOException(String.format("File %s cannot be deleted", file.getAbsolutePath()));
            }
        }
    }

    // 删除视频文件夹里的文件
    public static boolean deleteFile(File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) return true;
            for (File f : files) {
                if (!f.delete()) return false;
            }
        }
        return file.delete();
    }

    /**
     * 获取file目录中所有文件的总大小
     *
     * @param file
     * @return
     */
    public static long getTotalSize(File file) {
        if (file.isDirectory()) {
            long totalSize = 0;
            File[] files = file.listFiles();
            if (files == null) return 0;
            for (File f : files) {
                totalSize += getTotalSize(f);
            }
            return totalSize;
        } else {
            return file.length();
        }
    }

    public static void setLastModifiedTimeStamp(File file) throws IOException {
        if (file.exists()) {
            long now = System.currentTimeMillis();
            boolean modified = file.setLastModified(now);
            if (!modified) {
                modify(file);
            }
        }
    }

    //file是一个目录文件
    private static void modify(File file) throws IOException {
        File tempFile = new File(file, "tempFile");
        if (!tempFile.exists()) {
            tempFile.createNewFile();
            tempFile.delete();
        } else {
            tempFile.delete();
        }
    }

    // -----------------缓存改造分割线-------------------------

    public static int addClearFlag(int clearFlags,int flag) {
        clearFlags |= flag;
        return clearFlags;
    }

    public static boolean containUseTime(int flags) {
        return (flags & CLEAR_CACHE_FLAG_USE_TIME) > 0;
    }

    public static boolean containUseSize(int flags) {
        return (flags & CLEAR_CACHE_FLAG_USE_SIZE) > 0;
    }

    public static boolean containUseNumber(int flags) {
        return (flags & CLEAR_CACHE_FLAG_USE_NUMBER) > 0;
    }
}
