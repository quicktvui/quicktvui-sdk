//package eskit.sdk.support.nativeevent;
//
//import android.content.Context;
//import android.content.Intent;
//
//import com.hili.sdk.mp.server.third.viewcommon.ViewCommonPowerWatcher;
//import com.hili.sdk.mp.server.utils.PromiseHolder;
//import com.sunrain.toolkit.utils.log.L;
//import com.tencent.mtt.hippy.common.HippyMap;
//
///**
// * Create by weipeng on 2021/12/09 11:46
// *
// * @see #onObserverChange(Context, Intent)
// *
// * return {
// *     maxLevel,
// *     level,
// *     state,
// *     plug
// * }
// *
// * maxLevel 最大电量     int    -1 状态未知
// * level    当前电量     int    -1 状态未知
// * state    1 状态未知   int    2 充电中   3 放电中   4 未充电   5 已充满
// * plug     -1 状态未知  int    1 使用充电器充电   2 使用USB充电   4 使用无线方式充电
// *
// */
//public class BatteryChangeObserver extends BaseChangeObserver {
//
//    // ViewComm
////    public static final String ACTION_POWER_ADAPTER_CHANGE = "com.vantop.adapter.action";
////    public static final String ACTION_BATTER_CHANGE = "com.vantop.battry.charge";
//
//    private ViewCommonPowerWatcher mViewCommonPowerWatcher;
//
//    @Override
//    protected void startObserver() {
////        IntentFilter filter = new IntentFilter();
////        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
////        filter.addAction("android.intent.action.BATTERY_LEVEL_CHANGED");
//
////        filter.addAction(BatteryManager.ACTION_CHARGING);
////        filter.addAction(BatteryManager.ACTION_CHARGING);
//
////        filter.addAction(Intent.ACTION_BATTERY_LOW);
////        filter.addAction(Intent.ACTION_BATTERY_OKAY);
////        filter.addAction(Intent.ACTION_POWER_CONNECTED);
////        filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
////        filter.addAction(ACTION_POWER_ADAPTER_CHANGE);
////        filter.addAction(ACTION_BATTER_CHANGE);
//
////        mContext.registerReceiver(this, filter);
//
//        mViewCommonPowerWatcher = new ViewCommonPowerWatcher((level, maxLevel, plugged) -> {
//            HippyMap data = PromiseHolder.create()
//                    .put("level", level)
//                    .put("maxLevel", maxLevel)
//                    .put("plugged", plugged)
//                    .getData();
//            if (L.DEBUG) L.logD("BatteryChangeObserver:" + data.toJSONObject());
//            notifyData(data);
//        });
//        mViewCommonPowerWatcher.start();
//    }
//
//    @Override
//    protected void triggerIfNeed() {
//    }
//
//    private int level;
//    private int scale; //电量的刻度
//    private int plugged;
//
//    @Override
//    protected void onObserverChange(Context context, Intent intent) {
//
////        String action = intent.getAction();
////        if(TextUtils.isEmpty(action)) return;
////
////        if (L.DEBUG) L.logD("onObserverChange: " + intent.getAction());
////        Bundle extras = intent.getExtras();
////        if(extras !=  null){
////            Set<String> keys = extras.keySet();
////            for (String key : keys) {
////                if (L.DEBUG) L.logD("extra: " + key + "  " + extras.get(key));
////            }
////        }
////
////        if (L.DEBUG) L.logD("------------------------------------------------");
//
////        switch (intent.getAction()){
////            case Intent.ACTION_BATTERY_CHANGED:
////                int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
////                if (L.DEBUG) L.logD("status:" + status);
////
////                level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
////                scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0);
////                plugged = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0);
////
////                break;
////        }
//
////        switch (intent.getAction()){
////            case Intent.ACTION_BATTERY_CHANGED:
//////                level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
//////                scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
//////                state = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
//////                plug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
//////                break;
////            case ACTION_POWER_ADAPTER_CHANGE:
////                level = PropertyUtils.getInt("vendor.mstar.batlevel", -1);
////                scale = 100;
////                plug = intent.getIntExtra("plugged", -1);
////                state = plug == 1 ? 2 : 3;
////                break;
////            case ACTION_BATTER_CHANGE:
////                plug = intent.getIntExtra("charging", -1);
////                state = plug == 1 ? 2 : 3;
////                break;
////            default:
////                level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
////                scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
////                break;
////        }
//
//
//        // ViewCommon兼容
////        if("viewcomm".equals(MiniContext.get().getAppChannel())){
////            level = PropertyUtils.getInt("vendor.mstar.batlevel", -1);
////            scale = 100;
////        }else{
////            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
////            scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
////        }
//
////                    float percent = level / scale;
////                    BatteryManager BATTERY_STATUS_UNKNOWN = 1	状态未知
////                    BatteryManager BATTERY_STATUS_CHARGING = 2	充电中
////                    BatteryManager BATTERY_STATUS_DISCHARGING = 3	放电中
////                    BatteryManager BATTERY_STATUS_NOT_CHARGING = 4	未充电
////                    BatteryManager BATTERY_STATUS_FULL = 5	已充满
////        int state = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
////                    BatteryManager BATTERY_PLUGGED_AC = 1	使用充电器充电
////                    BatteryManager BATTERY_PLUGGED_USB = 2	使用USB充电
////                    BatteryManager BATTERY_PLUGGED_WIRELESS = 4	使用无线方式充电
////        int plug = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
//
////        HippyMap data = PromiseHolder.create()
////                .put("level", level)
////                .put("maxLevel", scale)
////                .put("plugged", plugged)
////                .getData();
////        if (L.DEBUG) L.logD("BatteryChangeObserver:" + data.toJSONObject());
////        notifyData(data);
//    }
//
//    @Override
//    protected void stopObserver() {
//        if(mContext != null) mContext.unregisterReceiver(this);
//    }
//    //region 单例
//
//    private static final class BatteryChangeObserverHolder {
//        private static final BatteryChangeObserver INSTANCE = new BatteryChangeObserver();
//    }
//
//    public static BatteryChangeObserver get() {
//        return BatteryChangeObserverHolder.INSTANCE;
//    }
//
//    private BatteryChangeObserver() {
//    }
//
//    //endregion
//
//}
