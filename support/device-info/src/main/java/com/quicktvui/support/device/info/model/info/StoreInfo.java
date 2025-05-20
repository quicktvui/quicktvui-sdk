package com.quicktvui.support.device.info.model.info;

import android.content.Context;

import com.quicktvui.support.device.info.model.beans.StorageBean;

import java.util.ArrayList;
import java.util.List;

import com.quicktvui.support.device.info.utils.MemoryUtils;
import com.quicktvui.support.device.info.utils.SdUtils;


public class StoreInfo {

    /**
     * 获取内存信息
     *
     * @param context
     * @return
     */
    public static List<StorageBean> getStoreInfo(Context context) {
        List<StorageBean> list = new ArrayList<>();
        StorageBean bean = new StorageBean();
        SdUtils.getStoreInfo(context, bean);
        MemoryUtils.getMemoryInfo(context, bean);
        list.add(bean);
        return list;
    }

}
