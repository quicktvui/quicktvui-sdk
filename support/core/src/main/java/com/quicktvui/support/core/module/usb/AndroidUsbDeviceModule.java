package com.quicktvui.support.core.module.usb;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.os.Build;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;
import com.sunrain.toolkit.utils.log.L;

import java.util.List;

/**
 *
 */
@ESKitAutoRegister
public class AndroidUsbDeviceModule implements IEsModule, IEsInfo,
        AndroidUsbDeviceManager.UsbDeviceListener {

    private AndroidUsbDeviceManager usbDeviceManager;

    @Override
    public void init(Context context) {
        usbDeviceManager = AndroidUsbDeviceManager.getInstance();
        usbDeviceManager.init(context);
        usbDeviceManager.registerUsbDeviceListener(this);
    }


    //-----------------------------权限相关--------------------------------------
    public void isUsbDevicePermissionsGranted(int vendorId, int productId, EsPromise promise) {
        try {
            if (L.DEBUG) {
                L.logD("#---------isUsbDevicePermissionsGranted---------->>>" + vendorId + "--->>>" + productId);
            }
            boolean isPermissionsGranted = usbDeviceManager.isUsbDevicePermissionsGranted(vendorId, productId);
            promise.resolve(isPermissionsGranted);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void requestUsbDevicePermission(String permission, int vendorId, int productId, EsPromise promise) {
        try {
            if (L.DEBUG) {
                L.logD("#---------requestUsbDevicePermission---------->>>" + vendorId + "--->>>" + productId);
            }
            usbDeviceManager.requestUsbDevicePermission(permission, vendorId, productId);
            promise.resolve(true);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //-------------------------------------------------------------------
    public void getUsbDeviceList(EsPromise promise) {
        try {
            List<UsbDevice> deviceList = usbDeviceManager.getUsbDeviceList();
            if (L.DEBUG) {
                L.logD("#---------getUsbDeviceList---------->>>" + deviceList);
            }
            if (deviceList != null && deviceList.size() > 0) {
                EsArray esArray = new EsArray();
                for (UsbDevice device : deviceList) {
                    try {
                        EsMap esMap = usbDeviceToEsMap(device);
                        esArray.pushMap(esMap);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
                promise.resolve(esArray);
            }
            //
            else {
                EsArray esArray = new EsArray();
                promise.resolve(esArray);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void getUsbDevice(EsArray esArray, EsPromise promise) {
        try {
            int vendorId = esArray.getInt(0);
            int productId = esArray.getInt(1);
            UsbDevice device = usbDeviceManager.getUsbDevice(vendorId, productId);
            //
            if (device != null) {
                EsMap esMap = usbDeviceToEsMap(device);
                if (promise != null) {
                    promise.resolve(esMap);
                }
            }
            //
            else {
                EsMap esMap = new EsMap();
                if (promise != null) {
                    promise.resolve(esMap);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void getUsbDeviceListByArray(EsArray deviceList, EsPromise promise) {
        EsArray resultArray = new EsArray();
        for (int i = 0; i < deviceList.size(); i++) {
            EsArray paramsArray = deviceList.getArray(i);
            int vendorId = paramsArray.getInt(0);
            int productId = paramsArray.getInt(1);

            UsbDevice device = usbDeviceManager.getUsbDevice(vendorId, productId);
            if (L.DEBUG) {
                L.logD("#---------getUsbDeviceListByArray---------->>>" +
                        " vendorId:" + vendorId +
                        " productId:" + productId +
                        " device:" + device
                );
            }
            if (device != null) {
                EsMap esMap = usbDeviceToEsMap(device);
                resultArray.pushMap(esMap);
            }
        }
        if (L.DEBUG) {
            L.logD("#---------getUsbDeviceListByArray---------->>>" + resultArray.size());
        }
        promise.resolve(resultArray);
    }

    private EsMap usbDeviceToEsMap(UsbDevice device) {
        EsMap esMap = new EsMap();
        try {
            esMap.pushString("deviceName", device.getDeviceName());
            esMap.pushInt("vendorId", device.getVendorId());
            esMap.pushInt("productId", device.getProductId());
            esMap.pushInt("deviceId", device.getDeviceId());
            esMap.pushInt("deviceProtocol", device.getDeviceProtocol());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                esMap.pushString("productName", device.getProductName());
                esMap.pushString("serialNumber", device.getSerialNumber());
                esMap.pushString("manufacturerName", device.getManufacturerName());
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return esMap;
    }

    @Override
    public void destroy() {
        try {
            AndroidUsbDeviceManager.getInstance().unregisterUsbDeviceListener(this);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            if (usbDeviceManager != null) {
                usbDeviceManager.destroy();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUsbDeviceAttached(UsbDevice usbDevice) {
        try {
            EsMap esMap = usbDeviceToEsMap(usbDevice);
            EsProxy.get().sendNativeEventTraceable(//
                    this, //
                    Events.EVENT_ON_USB_DEVICE_ATTACHED.toString(), //
                    esMap//
            );
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUsbDeviceDetached(UsbDevice usbDevice) {
        try {
            EsMap esMap = usbDeviceToEsMap(usbDevice);
            EsProxy.get().sendNativeEventTraceable(//
                    this, //
                    Events.EVENT_ON_USB_DEVICE_DETACHED.toString(), //
                    esMap//
            );
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public enum Events {
        EVENT_ON_USB_DEVICE_ATTACHED("onUsbDeviceAttached"),//
        EVENT_ON_USB_DEVICE_DETACHED("onUsbDeviceDetached");//

        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    @Override
    public void getEsInfo(EsPromise promise) {
        EsMap map = new EsMap();
        try {
            map.pushInt(IEsInfo.ES_PROP_INFO_VERSION, EsProxy.get().getSdkVersionCode());
            map.pushDouble(IEsInfo.ES_PROP_INFO_ESKIT_VERSION, EsProxy.get().getEsKitVersionCode());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        promise.resolve(map);
    }
}
