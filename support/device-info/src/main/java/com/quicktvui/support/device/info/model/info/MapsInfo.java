package com.quicktvui.support.device.info.model.info;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.quicktvui.support.device.info.utils.LogUtils;

public class MapsInfo {

    public static List<String> getMapsInfo(Context context) {
        List<String> list = new ArrayList<>();
        String mapsFilename = "/proc/" + android.os.Process.myPid() + "/maps";
        BufferedReader bufferedReader = null;
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(mapsFilename);
            bufferedReader = new BufferedReader(fileReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                LogUtils.d("maps line: " + line);
                list.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                if (fileReader != null) {
                    fileReader.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return list;
    }

}
