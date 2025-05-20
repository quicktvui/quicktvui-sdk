package com.quicktvui.sdk.core.module;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.sunrain.toolkit.utils.ThreadUtils;
import com.sunrain.toolkit.utils.log.L;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.module.IEsModule;
import com.quicktvui.sdk.core.sf.UsageManager;
import com.quicktvui.sdk.core.sf.db.entity.UsageRecord;

import java.util.List;

/**
 * @Description 历史记录接口
 * @Author jersonk
 * @Date 2024/11/27 17:23
 * @Version v1.0
 */
@ESKitAutoRegister
public class AppUsageModule implements IEsModule {

    @Override
    public void init(Context context) {

    }

    @Override
    public void destroy() {

    }

    /**
     * 根据条件查询使用记录
     * @param days      多少天以内
     * @param limit     查询条数
     * @param promise   json
     */
    public void getAllUsageRecord(int days, int limit, EsPromise promise) {
        L.logDF("getAllUsageRecord : days = " + days + " | limit = " + limit);
        ThreadUtils.executeByIo(new ThreadUtils.Task<List<UsageRecord>>() {
            @Override
            public List<UsageRecord> doInBackground() throws Throwable {
                return UsageManager.get().getAllUsageRecords(days, limit);
            }

            @Override
            public void onSuccess(List<UsageRecord> result) {
                if (result == null || result.isEmpty()) {
                    if (promise != null) {
//                        promise.resolve(null);
                        promise.resolve("[]");
                    }
                } else {
                    if (promise != null) {
                        String json = new Gson().toJson(result);
                        promise.resolve(json);
                    }
                }
            }

            @Override
            public void onCancel() {
                L.logEF("onCancel -- ");
                if (promise != null) {
                    promise.resolve(null);
                }
            }

            @Override
            public void onFail(Throwable t) {
                L.logEF("onFail -- " + t.getLocalizedMessage());
                if (promise != null) {
                    promise.resolve(null);
                }
            }
        });
    }

//    public void insertRecord(EsMap record, EsPromise promise) {
//        if (record == null || TextUtils.isEmpty(record.getString("pkgName"))) {
//            if (promise != null) {
//                promise.resolve(false);
//            }
//            return;
//        }
//        ThreadUtils.executeByIo(new ThreadUtils.Task<Boolean>() {
//            @Override
//            public Boolean doInBackground() throws Throwable {
//                UsageRecord newRecord = UsageRecord.create(
//                        record.getString("pkgName"),
//                        record.getString("appName"),
//                        record.getString("versionCode"),
//                        record.getString("versionName"),
//                        record.getString("icon"),
//                        record.getString("iconCircle")
//                );
//                return UsageManager.get().insertRecord(newRecord);
//            }
//
//            @Override
//            public void onSuccess(Boolean result) {
//                if (promise != null) {
//                    promise.resolve(result);
//                }
//            }
//
//            @Override
//            public void onCancel() {
//                L.logEF("onCancel -- ");
//                if (promise != null) {
//                    promise.resolve(false);
//                }
//            }
//
//            @Override
//            public void onFail(Throwable t) {
//                L.logEF("onFail -- " + t.getLocalizedMessage());
//                if (promise != null) {
//                    promise.resolve(false);
//                }
//            }
//        });
//
//
//    }

    public void deleteRecord(String pkgName, EsPromise promise) {
        if (TextUtils.isEmpty(pkgName)) {
            if (promise != null) {
                promise.resolve(false);
            }
            return;
        }
        ThreadUtils.executeByIo(new ThreadUtils.Task<Boolean>() {
            @Override
            public Boolean doInBackground() throws Throwable {
                return UsageManager.get().deleteRecord(UsageRecord.create(pkgName));
            }

            @Override
            public void onSuccess(Boolean result) {
                if (promise != null) {
                    promise.resolve(result);
                }
            }

            @Override
            public void onCancel() {
                L.logEF("onCancel -- ");
                if (promise != null) {
                    promise.resolve(false);
                }
            }

            @Override
            public void onFail(Throwable t) {
                L.logEF("onFail -- " + t.getLocalizedMessage());
                if (promise != null) {
                    promise.resolve(false);
                }
            }
        });
    }

}
