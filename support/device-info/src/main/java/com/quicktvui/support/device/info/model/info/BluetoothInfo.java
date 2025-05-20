package com.quicktvui.support.device.info.model.info;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Pair;


import com.quicktvui.support.device.info.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;



public class BluetoothInfo {

    //@AddTrace(name = "BluetoothInfo.getBluetoothInfo")
    public static List<Pair<String, String>> getBluetoothInfo(Context context) {
        boolean enable = false;
        List<Pair<String, String>> list = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            BluetoothManager manager = ((BluetoothManager) context.getApplicationContext().getSystemService(Context.BLUETOOTH_SERVICE));
            if (manager != null) {
                // 仅依赖 BLUETOOTH、ACCESS_WIFI_STATE 权限，TODO 蓝牙需要打开
                BluetoothAdapter adapter = manager.getAdapter();
                enable = adapter.isEnabled();
                Set<BluetoothDevice> devices = adapter.getBondedDevices();
                for (BluetoothDevice device : devices) {
                    list.add(new Pair<>("Bluetooth Name", device.getName()));
                    list.add(new Pair<>("Bluetooth Mac", device.getAddress()));
                    list.add(new Pair<>("Type", device.getType() + ""));
                    list.add(new Pair<>("State", device.getBondState() + ""));
                    list.add(new Pair<>("", ""));
                }
            }
        }
        if (list.isEmpty()) {
            if (enable) {
                list.add(new Pair<>("Bluetooth Scan", context.getString(R.string.usb_not_found)));
            } else {
                list.add(new Pair<>("Bluetooth Scan", "Please turn on the Bluetooth switch."));
            }
        }
        return list;
    }

}
