package com.quicktvui.support.device.info.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class FileUtils {

    /**
     * 是否存在
     *
     * @param name
     * @return
     */
    public static boolean exists(String name) {
        return new File(name).exists();
    }

    public static String readFile(String name) {
        return readFile(new File(name));
    }

    public static String readFile(File file) {
        FileReader fileReader = null;
        BufferedReader reader = null;
        try {
            if (file!=null && file.exists()){
                String line;
                StringBuffer sb = new StringBuffer();
                fileReader = new FileReader(file);
                reader = new BufferedReader(fileReader);
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append('\n');
                }
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                }
                return sb.toString();
            } else {
                return "0";
            }
        } catch (Exception e) {
//            e.printStackTrace();
            Log.e("error", " ----------->"+e.getMessage() );
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    Log.e("error", "fileReader: ----------->"+e.getMessage() );
//                    e.printStackTrace();
                }
            }
        }
        return "0";
    }
}
