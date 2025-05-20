package com.quicktvui.sdk.core.sf;

import android.text.TextUtils;

import com.sunrain.toolkit.utils.log.L;

import java.util.List;

import com.quicktvui.sdk.core.sf.db.entity.UsageRecord;
import com.quicktvui.sdk.core.sf.strategy.DefaultStrategyImpl;
import com.quicktvui.sdk.core.sf.strategy.IUsageStrategy;

/**
 * @Description 本机快应用近期使用管理类
 * @Author jersonk
 * @Date 2024/11/27 17:52
 * @Version v1.0
 */
public class UsageManager {

    private UsageManager() {}

    private static final class Holder {
        private static final UsageManager instance = new UsageManager();
    }

    public static UsageManager get() {
        return Holder.instance;
    }

    /** 默认的记录存储类 */
    private IUsageStrategy iStrategy = new DefaultStrategyImpl();

    /** 设置存储方式 */
    public void setIStrategy(IUsageStrategy iStrategy) {
        this.iStrategy = iStrategy;
    }

    /**
     * 根据条件查询记录
     * @param days      多少天以内
     * @param count     查询个数
     * @return          List<UsageRecord>
     */
    public List<UsageRecord> getAllUsageRecords(int days, int count) {
        L.logDF("getAllUsageRecords -- days = " + days + " | count = " + count);
        if (iStrategy != null) {
            return iStrategy.getAllUsageRecords(days, count);
        }
        return null;
    }

    /**
     * 根据包名查询记录
     * @param pkgName   包名
     * @return          UsageRecord
     */
    public UsageRecord getUsageRecordByPackageName(String pkgName) {
        L.logDF("getUsageRecordByPackageName -- pkgName = " + pkgName);
        if (TextUtils.isEmpty(pkgName)) {
            return null;
        }
        if (iStrategy != null) {
            return iStrategy.getUsageRecordByPackageName(pkgName);
        }
        return null;
    }

    /**
     * 插入记录
     * @param record    使用记录
     * @return          插入结果
     */
    public boolean insertRecord(UsageRecord record) {
        L.logDF("insertRecord -- record = " + record);
        if (iStrategy != null) {
            return iStrategy.insertRecord(record);
        }
        return false;
    }

    /**
     * 删除历史记录
     * @param record    记录信息
     * @return          删除结果
     */
    public boolean deleteRecord(UsageRecord record) {
        L.logDF("deleteRecord -- record = " + record);
        if (record == null || TextUtils.isEmpty(record.pkgName)) {
            return false;
        }
        if (iStrategy != null) {
            return iStrategy.deleteRecord(record.pkgName);
        }
        return false;
    }

}
