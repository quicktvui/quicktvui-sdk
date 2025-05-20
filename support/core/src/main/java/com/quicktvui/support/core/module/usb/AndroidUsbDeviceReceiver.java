package com.quicktvui.support.core.module.usb;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import com.sunrain.toolkit.utils.log.L;

public class AndroidUsbDeviceReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        UsbDevice usbDevice = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);

        if (L.DEBUG) {
            L.logD("#-------AndroidUsbDeviceReceiver-------->>>>>action:" + action);
            L.logD("#-------AndroidUsbDeviceReceiver-------->>>>>usbDevice:" + usbDevice);
        }

        if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
            AndroidUsbDeviceManager.getInstance().onUsbDeviceAttached(usbDevice);
        }
        //
        else if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
            AndroidUsbDeviceManager.getInstance().onUsbDeviceDetached(usbDevice);
        }
    }
}
