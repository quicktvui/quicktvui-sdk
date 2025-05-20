//package eskit.sdk.support.nativeevent;
//
//import android.content.Context;
//
//import com.sunrain.toolkit.utils.log.L;
//import com.sunrain.toolkit.utils.observer.BaseObserver;
//
//import java.util.Observable;
//
//import com.quicktvui.sdk.base.args.EsMap;
//import com.quicktvui.sdk.base.core.EsProxy;
//
///**
// * Create by weipeng on 2021/12/06 11:46
// */
//public class NativeEventReceiverManager {
//
//    public static final String EV_TIME = "onTimeStatusChange";
//    public static final String EV_USB = "onUsbStatusChange";
//    public static final String EV_BLUETOOTH = "onBluetoothStatusChange";
//    public static final String EV_NETWORK = "onNetworkStatusChange";
//    public static final String EV_BATTERY = "onBatteryStatusChange";
//
//    public void releaseAll() {
//        // 防止前端忘记解注册
//        TimeChangeObserver.get().release();
//        NetworkChangeObserver.get().release();
////        BatteryChangeObserver.get().release();
//        BluetoothChangeObserver.get().release();
//        UsbChangeObserver.get().release();
//        L.logDF("observer " + this.hashCode() + " release");
//    }
//
//    public void setListenTimeChange(boolean listen) {
//        if (listen) {
//            Context context = EsProxy.get().getContext();
//            TimeChangeObserver.get().observer(context, new BaseObserver<Void>() {
//                @Override
//                public void onUpdate(Observable o, Void arg) {
//                    sendEvent(EV_TIME, null);
//                }
//            });
//        } else {
//            TimeChangeObserver.get().release();
//        }
//    }
//
//    public void setListenNetworkChange(boolean listen) {
//        if (listen) {
//            Context context =EsProxy.get().getContext();
//            NetworkChangeObserver.get().observer(context, new BaseObserver<EsMap>() {
//                @Override
//                public void onUpdate(Observable o, EsMap data) {
//                    sendEvent(EV_NETWORK, data);
//                }
//            });
//        }else {
//            NetworkChangeObserver.get().release();
//        }
//    }
//
//    public void setListenUsbChange(boolean listen) {
//        if (listen) {
//            Context context = EsProxy.get().getContext();
//            UsbChangeObserver.get().observer(context, new BaseObserver<EsMap>() {
//                @Override
//                public void onUpdate(Observable o, EsMap data) {
//                    sendEvent(EV_USB, data);
//                }
//            });
//        }else {
//            UsbChangeObserver.get().release();
//        }
//    }
//
//    public void setListenBluetoothChange(boolean listen) {
//        if (listen) {
//            Context context = EsProxy.get().getContext();
//            BluetoothChangeObserver.get().observer(context, new BaseObserver<EsMap>() {
//                @Override
//                public void onUpdate(Observable o, EsMap data) {
//                    sendEvent(EV_BLUETOOTH, data);
//                }
//            });
//        } else {
//            BluetoothChangeObserver.get().release();
//        }
//    }
//
//    public void setListenBatteryChange(boolean listen) {
////        if (listen) {
////            Context context = EsProxy.get().getContext();
////            BatteryChangeObserver.get().observer(context, new BaseObserver<EsMap>() {
////                @Override
////                public void onUpdate(Observable o, EsMap data) {
////                    sendEvent(EV_BATTERY, data);
////                }
////            });
////        }else {
////            BatteryChangeObserver.get().release();
////        }
//    }
//
//    private void sendEvent(String name, Object params) {
//        EsProxy.get().sendNativeEventAll(name, params);
//    }
//}
