package com.quicktvui.sdk.core.sf.strategy;

import java.util.List;

import com.quicktvui.sdk.core.sf.db.entity.UsageRecord;

/**
 * @Description 历史记录存储接口
 * @Author jersonk
 * @Date 2024/12/3 10:09
 * @Version v1.0
 */
public interface IUsageStrategy {

    List<UsageRecord> getAllUsageRecords(int days, int count);

    UsageRecord getUsageRecordByPackageName(String rpkPkgName);

    boolean insertRecord(UsageRecord record);

    boolean deleteRecord(String rpkPkgName);

}
