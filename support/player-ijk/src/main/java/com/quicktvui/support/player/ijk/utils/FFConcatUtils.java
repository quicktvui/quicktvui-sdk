package com.quicktvui.support.player.ijk.utils;

import android.content.Context;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class FFConcatUtils {
    private static String basePath;
    private static final String SEPARATOR = "/";
    private static final String childPath = "FFCache";

    private static String getBasePath(Context context) {
        if (basePath != null) {
            return basePath;
        }
        basePath = context.getFilesDir().getAbsolutePath();
//        basePath = context.getExternalCacheDir().getAbsolutePath();
        return basePath;
    }

    public static String cover2Concat(Context context, String url) {
        String path = getBasePath(context) + SEPARATOR + childPath + SEPARATOR + getMd5(url) + StorageUtils.FF_CONCAT_SUFFIX;
//        String path = context.getExternalFilesDir(null).getAbsolutePath() + "/FFCache/" + getMd5(url) + StorageUtils.FF_CONCAT_SUFFIX;

        File dir = new File(getBasePath(context), childPath);
        if (!dir.exists()) {
            boolean mkdir = dir.mkdir();
            if (!mkdir) return null;
        }

        File concatFile = new File(path);
        if (concatFile.exists()) {
            return "file://" + path;
        }

        BufferedWriter bfw = null;
        try {
            bfw = new BufferedWriter(new FileWriter(concatFile, false));
            bfw.write("ffconcat version 1.0" + "\n");
            bfw.write("file '" + url + "'\n");
            bfw.write("file '" + path + "'\n");
            bfw.flush();
            return "file://" + path;
        } catch (Exception e) {
//            listener.onFail(e.getMessage());
            if (concatFile.exists()) {
                concatFile.delete();
            }
        } finally {
            if (bfw != null) {
                try {
                    bfw.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


    private static void runOnTempleThread(Runnable runnable) {
        new Thread(runnable).start();
    }

    public static void clearFFConcatS(Context context) {
        runOnTempleThread(()-> StorageUtils.deleteFile(new File(getBasePath(context), childPath)));
    }

    private static String getMd5(String s) {
        if (s == null) return "";
        String md5;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] digestBytes = messageDigest.digest(s.getBytes());
            md5 = bytesToHexString(digestBytes);
        } catch (NoSuchAlgorithmException e) {
            md5 = s;
        }

        return md5;
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public interface CoverListener {
        void onSuccess(String url);

        void onFail(String message);
    }
}
