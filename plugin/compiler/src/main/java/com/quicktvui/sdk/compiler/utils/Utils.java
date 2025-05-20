package com.quicktvui.sdk.compiler.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.util.Random;

/**
 *
 */
public class Utils {
    public static String stringCapture(String text) {
        char[] cs = text.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    public static String generate(int length) {

        final char[] CHAR_POOL = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();

        long seed = System.currentTimeMillis(); // 当前毫秒时间戳
        Random random = new Random(seed);

        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHAR_POOL[random.nextInt(CHAR_POOL.length)]);
        }
        return sb.toString();
    }

    public static String generateMethodName(String modulePath, int length) {
        String strMd5 = getMd5(modulePath);
        if(strMd5 != null){
            return strMd5.substring(0, length);
        }
        return generate(6);
    }

    public static String getMd5(String input) {
        try {
            byte[] bytes = MessageDigest.getInstance("MD5").digest(input.getBytes());
            StringBuilder sb = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                sb.append(Character.forDigit((b >> 4) & 0xF, 16));
                sb.append(Character.forDigit(b & 0xF, 16));
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
