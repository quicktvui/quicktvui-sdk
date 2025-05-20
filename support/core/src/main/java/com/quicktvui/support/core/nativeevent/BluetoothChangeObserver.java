package com.quicktvui.support.core.nativeevent;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.quicktvui.sdk.base.PromiseHolder;

/**
 * Create by weipeng on 2021/12/09 14:09
 *
 * @see #checkDeviceState()
 *
 * return {
 *     enable,
 *     name
 * }
 *
 * enable   开关  boolean  true/false
 * name     蓝牙名称  string
 *
 */
public class BluetoothChangeObserver extends BaseChangeObserver {

    @Override
    protected void startObserver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(this, filter);
    }

    @Override
    protected void triggerIfNeed() {
        checkDeviceState();
    }

    @Override
    protected void onObserverChange(Context context, Intent intent) {
        checkDeviceState();
    }

    @SuppressLint("MissingPermission")
    private void checkDeviceState() {
        PromiseHolder holder = PromiseHolder.create();
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

            holder.put("enable", adapter.isEnabled());
            holder.put("name", adapter.getName());

            // 某类设备是否连接
//            adapter.getProfileConnectionState(BluetoothProfile.A2DP)

        } catch (Exception e) {
            e.printStackTrace();
            holder.put("enable",false);
        }

        notifyData(holder.getData());
    }

    @Override
    protected void stopObserver() {
        if(mContext != null) mContext.unregisterReceiver(this);
    }

    //region 单例

    private static final class BluetoothChangeObserverHolder{
        private static final BluetoothChangeObserver INSTANCE = new BluetoothChangeObserver();
    }

    public static BluetoothChangeObserver get(){
        return BluetoothChangeObserverHolder.INSTANCE;
    }

    private BluetoothChangeObserver(){}

    //endregion

}
