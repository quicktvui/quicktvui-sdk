package com.quicktvui.support.core.module.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.text.TextUtils;
import android.util.SparseArray;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;
import com.quicktvui.support.core.utils.BundleMapper;
import com.sunrain.toolkit.utils.log.L;

import org.json.JSONObject;

import java.util.concurrent.atomic.AtomicInteger;

@ESKitAutoRegister
public class ESBroadcastModule implements IEsModule, IEsInfo {

    private final AtomicInteger receiverIds = new AtomicInteger(0);
    private final SparseArray<ESBroadcastReceiver> receiverSparseArray = new SparseArray();

    private Context context;

    public enum Events {
        EVENT_ON_ES_BROADCAST_RECEIVE("onESBroadcastReceive");

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
    public void init(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * 注册广播
     */
    public void registerReceiver(EsMap params, EsPromise promise) {
        if (params == null) {
            promise.reject(-1);
            return;
        }
        if (L.DEBUG) {
            L.logD("#-----注册开始----registerReceiver--------->>>" + params);
        }
        //
        try {
            IntentFilter intentFilter = null;
            int receiverId = -1;

            //1.action
            try {
                if (params.containsKey("action")) {
                    EsArray esArray = params.getArray("action");
                    for (int i = 0; i < esArray.size(); i++) {
                        String action = esArray.getString(i);
                        if (!TextUtils.isEmpty(action)) {
                            if (intentFilter == null) {
                                intentFilter = new IntentFilter();
                            }
                            intentFilter.addAction(action);
                            if (L.DEBUG) {
                                L.logD("#addAction:" + action);
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            //2.category
            try {
                if (params.containsKey("category")) {
                    EsArray esArray = params.getArray("category");
                    for (int i = 0; i < esArray.size(); i++) {
                        String category = esArray.getString(i);
                        if (!TextUtils.isEmpty(category)) {
                            if (intentFilter == null) {
                                intentFilter = new IntentFilter();
                            }
                            intentFilter.addCategory(category);
                            if (L.DEBUG) {
                                L.logD("#addCategory:" + category);
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            //3.scheme
            try {
                if (params.containsKey("scheme")) {
                    EsArray esArray = params.getArray("scheme");
                    for (int i = 0; i < esArray.size(); i++) {
                        String scheme = esArray.getString(i);
                        if (!TextUtils.isEmpty(scheme)) {
                            if (intentFilter == null) {
                                intentFilter = new IntentFilter();
                            }
                            intentFilter.addDataScheme(scheme);
                            if (L.DEBUG) {
                                L.logD("#addDataScheme:" + scheme);
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            //4.type
            try {
                if (params.containsKey("type")) {
                    EsArray esArray = params.getArray("type");
                    for (int i = 0; i < esArray.size(); i++) {
                        String type = esArray.getString(i);
                        if (!TextUtils.isEmpty(type)) {
                            if (intentFilter == null) {
                                intentFilter = new IntentFilter();
                            }
                            intentFilter.addDataType(type);
                            if (L.DEBUG) {
                                L.logD("#addDataType:" + type);
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            //4.authority
            try {
                if (params.containsKey("authority")) {
                    EsArray esArray = params.getArray("authority");
                    for (int i = 0; i < esArray.size(); i++) {
                        EsMap esMap = esArray.getMap(i);
                        if (esMap != null) {
                            String host = esMap.getString("host");
                            String port = esMap.getString("port");
                            if (intentFilter == null) {
                                intentFilter = new IntentFilter();
                            }
                            intentFilter.addDataAuthority(host, port);
                            if (L.DEBUG) {
                                L.logD("#addDataAuthority:" + "host:" + host + "  port:" + port);
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            //5.authority
            try {
                if (params.containsKey("path")) {
                    EsArray esArray = params.getArray("path");
                    for (int i = 0; i < esArray.size(); i++) {
                        EsMap esMap = esArray.getMap(i);
                        if (esMap != null) {
                            String path = esMap.getString("path");
                            int type = esMap.getInt("type");
                            if (intentFilter == null) {
                                intentFilter = new IntentFilter();
                            }
                            intentFilter.addDataPath(path, type);
                            if (L.DEBUG) {
                                L.logD("#addDataPath:" + "path:" + path + "  type:" + type);
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            //6.schemeSpecificPart
            try {
                if (params.containsKey("schemeSpecificPart")) {
                    EsArray esArray = params.getArray("schemeSpecificPart");
                    for (int i = 0; i < esArray.size(); i++) {
                        EsMap esMap = esArray.getMap(i);
                        if (esMap != null) {
                            String ssp = esMap.getString("ssp");
                            int type = esMap.getInt("type");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                if (intentFilter == null) {
                                    intentFilter = new IntentFilter();
                                }
                                intentFilter.addDataSchemeSpecificPart(ssp, type);
                                if (L.DEBUG) {
                                    L.logD("#addDataSchemeSpecificPart:" + "ssp:" + ssp + "  type:" + type);
                                }
                            }
                        }
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            //
            if (intentFilter != null) {
                receiverId = receiverIds.addAndGet(1);
                ESBroadcastReceiver broadcastReceiver = new ESBroadcastReceiver(receiverId);
                context.registerReceiver(broadcastReceiver, intentFilter);
                receiverSparseArray.put(receiverId, broadcastReceiver);
                if (L.DEBUG) {
                    L.logD(receiverId + "#-----注册成功----registerReceiver----success------>>>" + params);
                }
                traceReceivers();
                if (promise != null) {
                    promise.resolve(receiverId);
                }
            } else {
                if (L.DEBUG) {
                    L.logD(receiverId + "#----注册失败-----registerReceiver-----error----->>>" + params);
                }
                if (promise != null) {
                    promise.reject(-1);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
            if (L.DEBUG) {
                L.logD("#---------registerReceiver-----error----->>>" + params);
            }
            if (promise != null) {
                promise.reject(-1);
            }
        }
    }

    /**
     * 取消注册广播
     */
    public void unregisterReceiver(int receiverId, EsPromise promise) {
        try {
            ESBroadcastReceiver receiver = //
                    receiverSparseArray.get(receiverId, null);
            if (receiver != null) {
                if (L.DEBUG) {
                    L.logD("#---------unregisterReceiver---------->>>" + receiverId);
                }
                context.unregisterReceiver(receiver);
            }
            receiverSparseArray.remove(receiverId);
            //
            if (promise != null) {
                promise.resolve(receiverId);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            //
            if (promise != null) {
                promise.reject(receiverId);
            }
        }
        traceReceivers();
    }

    public class ESBroadcastReceiver extends BroadcastReceiver {

        private int receiverId;

        public ESBroadcastReceiver(int id) {
            receiverId = id;
        }

        public int getReceiverId() {
            return receiverId;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (L.DEBUG) {
                L.logD("#---------onReceive---------->>>" + intent.toString());
            }
            EsMap params = new EsMap();
            try {
                params.pushInt("id", receiverId);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            try {
                String action = intent.getAction();
                params.pushString("action", action);
            } catch (Throwable e) {
                e.printStackTrace();
            }
            try {
                JSONObject jsonObject = BundleMapper.bundle2JsonObject(intent.getExtras());
                params.pushMap("extras", BundleMapper.json2EsMap(jsonObject));
            } catch (Throwable e) {
                e.printStackTrace();
            }
            EsProxy.get().sendNativeEventTraceable(ESBroadcastModule.this, //
                    Events.EVENT_ON_ES_BROADCAST_RECEIVE.toString(), params);
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


    private void traceReceivers() {
        if (L.DEBUG) {
            try {
                if (receiverSparseArray.size() > 0) {
                    for (int i = 0; i < receiverSparseArray.size(); i++) {
                        int receiverId = receiverSparseArray.keyAt(i);
                        ESBroadcastReceiver receiver = receiverSparseArray.get(receiverId);
                        if (L.DEBUG) {
                            L.logD(receiverId + "#----##-----receiver---trace------->>>" + receiver);
                        }
                    }
                } else {
                    if (L.DEBUG) {
                        L.logD("#---------receiver list size == 0---------->>>");
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void destroy() {
        if (receiverSparseArray.size() > 0) {
            for (int i = 0; i < receiverSparseArray.size(); i++) {
                int receiverId = receiverSparseArray.keyAt(i);
                unregisterReceiver(receiverId, null);
            }
            receiverSparseArray.clear();
        }
        traceReceivers();
    }
}
