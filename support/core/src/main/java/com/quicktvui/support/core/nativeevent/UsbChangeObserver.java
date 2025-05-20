package com.quicktvui.support.core.nativeevent;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.os.Handler;

import com.sunrain.toolkit.utils.SDCardUtils;
import com.sunrain.toolkit.utils.UsbUtils;
import com.sunrain.toolkit.utils.log.L;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.quicktvui.sdk.base.PromiseHolder;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;

/**
 * Create by weipeng on 2021/12/09 14:09
 *
 * @see #refreshDeviceList(Context)
 * <p>
 * return {
 * devices:[
 * {
 * id,
 * name,
 * class1,
 * class2
 * }
 * ]
 * }
 * <p>
 * id       设备id    int
 * name     名称      string
 * class1   类型      int
 * 0 取决于interfaceClass     {@link UsbConstants#USB_CLASS_PER_INTERFACE}
 * class2   类型      int
 * 1 音频设备                  {@link UsbConstants#USB_CLASS_AUDIO}
 * 2 通讯设备                  {@link UsbConstants#USB_CLASS_COMM}
 * 3 鼠标键盘一类人机设备        {@link UsbConstants#USB_CLASS_HID}
 * 5 物理设备                  {@link UsbConstants#USB_CLASS_PHYSICA}
 * 6 静止图像设备，例如数码相机   {@link UsbConstants#USB_CLASS_STILL_IMAGE}
 * 7 打印机                   {@link UsbConstants#USB_CLASS_PRINTER}
 * 8 大容量存储设备             {@link UsbConstants#USB_CLASS_MASS_STORAGE}
 * 9 HUB                      {@link UsbConstants#USB_CLASS_HUB}
 * 14 视频设备，例如摄像头      {@link UsbConstants#USB_CLASS_VIDEO}
 */
public class UsbChangeObserver extends BaseChangeObserver {

    private Handler mHandler;

    @Override
    protected void startObserver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_DETACHED);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_CHECKING);
//        filter.addDataScheme("file");

        mContext.registerReceiver(this, filter);
    }

    @Override
    protected void triggerIfNeed() {
        refreshDeviceList(mContext);
    }

    @Override
    protected void onObserverChange(Context context, Intent intent) {
        refreshDeviceList(context);
    }

    private void refreshDeviceList(Context context){
        UsbManager manager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        HashMap<String, UsbDevice> deviceList = manager.getDeviceList();
        if(deviceList != null && deviceList.size() > 0){
            Set<String> keys = deviceList.keySet();
            for (String key : keys) {
                UsbDevice device = deviceList.get(key);
                if (L.DEBUG) L.logD("device type:" + UsbUtils.getDeviceType(device));
                if(UsbUtils.isStorageDevice(device)){
                    checkDeviceState(device);
                    return;
                }
            }
        }

        EsMap result = new EsMap();
        result.pushArray("devices", new EsArray());
        notifyData(result);
    }

    private void checkDeviceState(UsbDevice device) {
        if (!UsbUtils.hasDevicePermission(device)) {
            UsbUtils.requestDevicePermission(device, result -> {
                if (L.DEBUG) L.logD("requestDevicePermission:" + result);
                if(result){
                    mHandler.postDelayed(this::getAllDeviceInfo, 1000);
                }
            });
            return;
        }
        mHandler.postDelayed(this::getAllDeviceInfo, 1000);
    }

    private void getAllDeviceInfo(){

        List<SDCardUtils.SDCardInfo> infoList = SDCardUtils.getSdcardInfo();
        if (L.DEBUG) L.logD("all sdcard:" + infoList.size());
        EsMap result = new EsMap();
        EsArray devices = new EsArray();
        result.pushArray("devices", devices);
        for (SDCardUtils.SDCardInfo info : infoList) {
//            if(Environment.MEDIA_MOUNTED.equals(info.getState()) && info.isRemovable() && info.isReadable()){
            if((Environment.MEDIA_CHECKING.equals(info.getState())
                    || Environment.MEDIA_MOUNTED.equals(info.getState()))
                    && info.isRemovable()){
                if (L.DEBUG) L.logD("checkDeviceState:" + info);
                devices.pushMap(PromiseHolder.create()
                        .put("name", info.getName())
                        .put("path", info.getPath())
                        .put("total", info.getTotalSize())
                        .put("available", info.getAvailableSize())
                        .getData());
            }else {
                if (L.DEBUG) L.logD("filter usb device:" + info);
            }
        }

        notifyData(result);

    }

    @Override
    protected void stopObserver() {
        if(mHandler != null) mHandler.removeCallbacksAndMessages(null);
        if (mContext != null) mContext.unregisterReceiver(this);
    }

    //region 单例

    private static final class UsbChangeObserverHolder {
        private static final UsbChangeObserver INSTANCE = new UsbChangeObserver();
    }

    public static UsbChangeObserver get() {
        return UsbChangeObserverHolder.INSTANCE;
    }

    private UsbChangeObserver() {
        mHandler = new Handler();
    }

    //endregion

}
