package com.quicktvui.support.core.module.permission;

import android.content.Context;
import android.util.Pair;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsCallback;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.PromiseHolder;
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
public class AndroidPermissionModule implements IEsModule, IEsInfo {

    @Override
    public void init(Context context) {

    }

    /**
     * 是否有权限
     *
     * @param esArray
     * @param promise
     */
    public void isPermissionsGranted(EsArray esArray, EsPromise promise) {
        if (L.DEBUG) {
            L.logD("#---------isGranted---------->>>");
        }
        try {
            int length = esArray.size();
            String[] pList = new String[length];
            for (int i = 0; i < length; i++) {
                pList[i] = esArray.getString(i);
            }
//            boolean isGranted = PermissionUtils.isGranted(pList); 长虹某些机型崩溃
            boolean isGranted = EsProxy.get().checkSelfPermission(pList);
            PromiseHolder.create(promise)
                    .put("granted", isGranted)
                    .sendSuccess();
        } catch (Throwable e) {
            e.printStackTrace();
            PromiseHolder.create(promise)
                    .put("granted", false)
                    .sendSuccess();
        }
    }

    public void isPermissionsGrantedReverse(EsArray esArray, EsPromise promise) {
        if (L.DEBUG) {
            L.logD("#---------isGranted---------->>>");
        }
        try {
            int length = esArray.size();
            String[] pList = new String[length];
            for (int i = 0; i < length; i++) {
                pList[i] = esArray.getString(i);
            }
//            boolean isGranted = PermissionUtils.isGranted(pList); 长虹某些机型崩溃
            boolean isGranted = EsProxy.get().checkSelfPermission(pList);
            if (isGranted) {
                PromiseHolder.create(promise)
                        .put("granted", true)
                        .sendFailed();
            } else {
                PromiseHolder.create(promise)
                        .put("granted", false)
                        .sendSuccess();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            PromiseHolder.create(promise)
                    .put("granted", false)
                    .sendSuccess();
        }
    }

    /**
     * 请求权限
     *
     * @param esArray
     * @param promise
     */
    public void requestPermissionsReverse(EsArray esArray, EsPromise promise) {
        requestPermissionList(esArray, promise, true);
    }

    public void requestPermissions(EsArray esArray, EsPromise promise) {
        requestPermissionList(esArray, promise, false);
    }

    private void requestPermissionList(EsArray esArray, EsPromise promise, boolean reverse) {
        if (L.DEBUG) {
            L.logD("#---------requestPermissions---------->>>");
        }
        int length = esArray.size();
        String[] pList = new String[length];
        for (int i = 0; i < length; i++) {
            pList[i] = esArray.getString(i);
        }
        EsProxy.get().requestPermission(this, pList, new EsCallback<List<String>, Pair<List<String>, List<String>>>() {
            @Override
            public void onSuccess(List<String> granted) {
                if (L.DEBUG) {
                    L.logD("#---------onGranted---------->>>" + granted);
                }
                try {
                    EsArray grantedList = new EsArray();
                    if (granted != null) {
                        for (String per : granted) {
                            grantedList.pushString(per);
                        }
                    }
                    if (reverse) {
                        PromiseHolder.create(promise)
                                .put("granted", true)
                                .put("grantedList", grantedList)
                                .sendFailed();
                    } else {
                        PromiseHolder.create(promise)
                                .put("granted", true)
                                .put("grantedList", grantedList)
                                .sendSuccess();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Pair<List<String>, List<String>> pair) {
                if (L.DEBUG) {
                    L.logD("#---------onDenied---------->>>"
                            + "deniedForever:" + pair.first + "--->>"
                            + "denied:" + pair.second + "--->>");
                }
                try {
                    EsArray deniedList = new EsArray();
                    if (pair.second != null) {
                        for (String per : pair.second) {
                            deniedList.pushString(per);
                        }
                    }
                    EsArray deniedForeverList = new EsArray();
                    if (pair.first != null) {
                        for (String per : pair.first) {
                            deniedForeverList.pushString(per);
                        }
                    }

                    if (reverse) {
                        PromiseHolder.create(promise)
                                .put("granted", false)
                                .put("deniedList", deniedList)
                                .put("deniedForeverList", deniedForeverList)
                                .sendSuccess();
                    } else {
                        PromiseHolder.create(promise)
                                .put("granted", false)
                                .put("deniedList", deniedList)
                                .put("deniedForeverList", deniedForeverList)
                                .sendFailed();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    if (L.DEBUG) {
                        L.logD("#-------onDenied-----error-->>");
                    }
                }
            }
        });
    }

    public void requestPermissionListPromise(EsArray esArray, EsPromise promise) {
        if (L.DEBUG) {
            L.logD("#--------requestPermissionListPromise----start-->>");
        }
//        int length = esArray.size();
//        String[] pList = new String[length];
//        for (int i = 0; i < length; i++) {
//            pList[i] = esArray.getString(i);
//        }
//        try {
//            PermissionUtils.permission(pList).request();
//        } catch (Throwable e) {
//            e.printStackTrace();
//        }

        PromiseHolder.create(promise)
                .put("granted", true)
                .put("grantedList", esArray)
                .sendSuccess();
        if (L.DEBUG) {
            L.logD("#--------requestPermissionListPromise----end-->>");
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

    @Override
    public void destroy() {

    }
}
