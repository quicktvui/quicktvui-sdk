package com.quicktvui.support.device.info.model.info;

import static android.content.Context.SENSOR_SERVICE;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class HardwareInfo {

    public static List<Pair<String, String>> getHardwareInfo(Context context) {
        List<Pair<String, String>> list = new ArrayList<>();
        SensorManager sensorManager = (SensorManager) context.getSystemService(SENSOR_SERVICE);
        Sensor gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        if (gyroscope != null && gyroscope.getName() != null) {
            list.add(new Pair<>("GYROSCOPE", gyroscope.getName()));
        }
        Sensor magnetic = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        if (magnetic!=null && magnetic.getName()!=null){
            list.add(new Pair<>("MAGNETIC", magnetic.getName()));
        }
        Sensor accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        if (accelerometer!=null && accelerometer.getName()!=null){
            list.add(new Pair<>("ACCELEROMETER", accelerometer.getName()));
        }
        return list;
    }
}
