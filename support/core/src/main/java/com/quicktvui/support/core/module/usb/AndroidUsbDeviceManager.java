package com.quicktvui.support.core.module.usb;

import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED;
import static android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.sunrain.toolkit.utils.log.L;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class AndroidUsbDeviceManager {

    private static AndroidUsbDeviceManager instance;
    private UsbManager usbManager;

    protected List<UsbDeviceListener> listenerList =
            Collections.synchronizedList(new ArrayList<>());

    private Context context;

    private boolean isReceiverRegistered;
    private AndroidUsbDeviceReceiver usbDeviceReceiver = new AndroidUsbDeviceReceiver();

    private AndroidUsbDeviceManager() {
    }

    public static AndroidUsbDeviceManager getInstance() {
        synchronized (AndroidUsbDeviceManager.class) {
            if (instance == null) {
                instance = new AndroidUsbDeviceManager();
            }
        }
        return instance;
    }

    public void init(Context ctx) {
        try {
            context = ctx;
            usbManager = (UsbManager) ctx.getSystemService(Context.USB_SERVICE);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            if (!isReceiverRegistered) {
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(ACTION_USB_DEVICE_ATTACHED);
                intentFilter.addAction(ACTION_USB_DEVICE_DETACHED);
                this.context.registerReceiver(usbDeviceReceiver, intentFilter);
                isReceiverRegistered = true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        try {
            if (usbDeviceReceiver != null) {
                context.unregisterReceiver(usbDeviceReceiver);
            }
        } catch (Throwable e) {
            L.logWF("" + e.getMessage());
            if(L.DEBUG){
                e.printStackTrace();
            }
        }
    }

    /**
     * 是否获取了权限
     */
    public boolean isUsbDevicePermissionsGranted(int vendorId, int productId) {
        if (usbManager == null) {
            return false;
        }
        try {
            UsbDevice usbDevice = getUsbDevice(vendorId, productId);
            if (usbDevice != null) {
                return usbManager.hasPermission(usbDevice);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 请求权限
     */
    public void requestUsbDevicePermission(String permission, int vendorId, int productId) {
        if (usbManager == null) {
            return;
        }
        try {
            UsbDevice usbDevice = getUsbDevice(vendorId, productId);
            if (usbDevice != null) {
                PendingIntent pendingIntent =
                        PendingIntent.getBroadcast(context, 1, new Intent(permission), 0);
                usbManager.requestPermission(usbDevice, pendingIntent);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //----------------------------遥控器设备初始化设备列表---------------------------
    public List<UsbDevice> getUsbDeviceList() {
        if (usbManager == null) {
            return null;
        }
        try {
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
            if (deviceList == null || deviceList.size() <= 0) {
                return null;
            }
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            List<UsbDevice> usbDevices = new ArrayList<>();
            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();
                usbDevices.add(device);
            }
            return usbDevices;
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }

    public UsbDevice getUsbDevice(int vendorId, int productId) {
        try {
            if (usbManager == null) {
                if (L.DEBUG) {
                    L.logD("#---------getUsbDevice-----usbManager == null----->>>");
                }
                return null;
            }
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
            if (deviceList == null || deviceList.size() <= 0) {
                if (L.DEBUG) {
                    L.logD("#---------getUsbDevice-----deviceList == null----->>>");
                }
                return null;
            }
            Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();
                if (device.getVendorId() == vendorId && device.getProductId() == productId) {
                    return device;
                }
            }
            return null;
        } catch (Throwable e) {
            e.printStackTrace();
            if (L.DEBUG) {
                L.logD("#---------getUsbDevice-----error----->>>");
            }
            return null;
        }
    }


    //------------------------------------------------------

    /**
     * receiver调用
     */
    public void onUsbDeviceAttached(UsbDevice usbDevice) {
        notifyUsbDeviceAttached(usbDevice);
    }

    /**
     * receiver调用
     */
    public void onUsbDeviceDetached(UsbDevice usbDevice) {
        notifyUsbDeviceDetached(usbDevice);
    }

    public void registerUsbDeviceListener(UsbDeviceListener listener) {
        if (listener != null && !listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    public void unregisterUsbDeviceListener(UsbDeviceListener listener) {
        if (listener != null) {
            listenerList.remove(listener);
        }
    }

    public void notifyUsbDeviceAttached(UsbDevice usbDevice) {
        if (listenerList != null && listenerList.size() > 0) {
            for (UsbDeviceListener listener : listenerList) {
                try {
                    listener.onUsbDeviceAttached(usbDevice);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void notifyUsbDeviceDetached(UsbDevice usbDevice) {
        if (listenerList != null && listenerList.size() > 0) {
            for (UsbDeviceListener listener : listenerList) {
                try {
                    listener.onUsbDeviceDetached(usbDevice);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface UsbDeviceListener {
        void onUsbDeviceAttached(UsbDevice usbDevice);

        void onUsbDeviceDetached(UsbDevice usbDevice);
    }
}
