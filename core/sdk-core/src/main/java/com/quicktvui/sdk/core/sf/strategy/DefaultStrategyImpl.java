package com.quicktvui.sdk.core.sf.strategy;

import android.text.TextUtils;

import com.sunrain.toolkit.utils.log.L;

import java.util.List;

import com.quicktvui.sdk.core.sf.db.UsageDbHelper;
import com.quicktvui.sdk.core.sf.db.dao.UsageRecordDao;
import com.quicktvui.sdk.core.sf.db.entity.UsageRecord;

/**
 * @Description db 存储
 * @Author jersonk
 * @Date 2024/12/3 10:09
 * @Version v1.0
 */
public class DefaultStrategyImpl implements IUsageStrategy {

    @Override
    public List<UsageRecord> getAllUsageRecords(int days, int count) {
        UsageRecordDao dao = UsageDbHelper.get().getDao();
        try {
            if (days <= 0) {
                return dao.getAllUsageRecord(0, count);
            }
            long startTime = System.currentTimeMillis() - 1000L * 60 * 60 * 24 * days;
            return dao.getAllUsageRecord(startTime, count);
        } catch (Exception e) {
            L.logEF("getAll -- error : " + e.getLocalizedMessage());
            return null;
        }
    }

    @Override
    public UsageRecord getUsageRecordByPackageName(String rpkPkgName) {
        if (TextUtils.isEmpty(rpkPkgName)) {
            return null;
        }
        UsageRecordDao dao = UsageDbHelper.get().getDao();
        return dao.getUsageRecordByPackageName(rpkPkgName);
    }

    @Override
    public boolean insertRecord(UsageRecord record) {
        if (record == null || TextUtils.isEmpty(record.pkgName)) {
            return false;
        }
        try {
            UsageRecordDao dao = UsageDbHelper.get().getDao();
            UsageRecord old = dao.getUsageRecordByPackageName(record.pkgName);
            if (old != null) {
                L.logDF("insert : [ record = " + record + " ] has old history info | old = " + old);
                record.usageCount = old.usageCount + 1;

                // 快应用缓存拉起的情况下，部分字段为空的覆写问题
                if (TextUtils.isEmpty(record.appName) && !TextUtils.isEmpty(old.appName)) {
                    record.appName = old.appName;
                }
                if (TextUtils.isEmpty(record.versionCode) && !TextUtils.isEmpty(old.versionCode)) {
                    record.versionCode = old.versionCode;
                }
                if (TextUtils.isEmpty(record.versionName) && !TextUtils.isEmpty(old.versionName)) {
                    record.versionName = old.versionName;
                }
                if (TextUtils.isEmpty(record.icon) && !TextUtils.isEmpty(old.icon)) {
                    record.icon = old.icon;
                }
                if (TextUtils.isEmpty(record.iconCircle) && !TextUtils.isEmpty(old.iconCircle)) {
                    record.iconCircle = old.iconCircle;
                }
            } else {
                record.usageCount = 1;
            }
            record.lastUsageTime = System.currentTimeMillis();
            dao.insertOrUpdate(record);
        } catch (Exception e) {
            L.logEF("insert -- error : " + e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteRecord(String rpkPkgName) {
        if (TextUtils.isEmpty(rpkPkgName)) {
            return false;
        }
        try {
            UsageRecordDao dao = UsageDbHelper.get().getDao();
            dao.delete(UsageRecord.create(rpkPkgName));
        } catch (Exception e) {
            L.logEF("delete -- error : " + e.getLocalizedMessage());
            return false;
        }
        return true;
    }

}
