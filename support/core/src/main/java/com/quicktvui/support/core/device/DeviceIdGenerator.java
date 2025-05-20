package com.quicktvui.support.core.device;


import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.sunrain.toolkit.utils.log.L;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 */
public class DeviceIdGenerator implements IDeviceGenerator {

    @Override
    public String generate(Context context) {
        try {
            long startTime = System.currentTimeMillis();
            //1 compute DEVICE ID
            String m_szDevIDShort = "";
            try {
                m_szDevIDShort = "35" + //we make this look like a valid IMEI
                        Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                        Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                        Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                        Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                        Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                        Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                        Build.USER.length() % 10; //13 digits
            } catch (Throwable e) {
                e.printStackTrace();
            }

            //2 android ID - unreliable
            String m_szAndroidID = "";
            try {
                m_szAndroidID = Settings.Secure.getString(context.getContentResolver(), Settings.System.ANDROID_ID);
            } catch (Throwable e) {
                e.printStackTrace();
            }


            //3 wifi manager, read MAC address - requires  android.permission.ACCESS_WIFI_STATE or comes as null
            String m_szWLANMAC;
            try {
                WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                m_szWLANMAC = wm.getConnectionInfo().getMacAddress();
            } catch (Exception e) {
                m_szWLANMAC = "";
            }

            //4 Bluetooth MAC address  android.permission.BLUETOOTH required
            String m_szBTMAC = null;
            try {
                m_szBTMAC = getBluetoothAddress();
                Log.d("getUUID", "getBluetoothAddress value ::  " + m_szBTMAC);
            } catch (Throwable e) {
                e.printStackTrace();
            }

            if (TextUtils.isEmpty(m_szBTMAC)) {
                try {
                    BluetoothAdapter m_BluetoothAdapter = null; // Local Bluetooth adapter
                    m_BluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    m_szBTMAC = m_BluetoothAdapter.getAddress();
                } catch (Exception e) {
                    m_szBTMAC = "";
                }
            }

            //5 SUM THE IDs
            String m_szLongID = m_szDevIDShort + m_szAndroidID + m_szWLANMAC + m_szBTMAC;
            MessageDigest m = null;
            try {
                m = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            m.update(m_szLongID.getBytes(), 0, m_szLongID.length());
            byte p_md5Data[] = m.digest();

            String m_szUniqueID = new String();
            for (int i = 0; i < p_md5Data.length; i++) {
                int b = (0xFF & p_md5Data[i]);
                // if it is a single digit, make sure it have 0 in front (proper padding)
                if (b <= 0xF) m_szUniqueID += "0";
                // add number to string
                m_szUniqueID += Integer.toHexString(b);
            }
            m_szUniqueID = m_szUniqueID.toUpperCase();

            long endTime = System.currentTimeMillis();
            if (L.DEBUG) {
                L.logD("----------AndroidDeviceId---耗时--->>>" + (endTime - startTime));
            }
            return m_szUniqueID;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "";
    }

    private static String getBluetoothAddress() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            Field field = bluetoothAdapter.getClass().getDeclaredField("mService");
            // 参数值为true，禁用访问控制检查
            field.setAccessible(true);
            Object bluetoothManagerService = field.get(bluetoothAdapter);
            if (bluetoothManagerService == null) {
                return null;
            }
            Method method = bluetoothManagerService.getClass().getMethod("getAddress");
            Object address = method.invoke(bluetoothManagerService);
            if (address != null && address instanceof String) {
                return (String) address;
            } else {
                return null;
            }
            //抛一个总异常省的一堆代码...
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
