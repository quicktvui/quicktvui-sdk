package com.quicktvui.sdk.core.sf.db.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import com.quicktvui.sdk.core.sf.db.entity.UsageRecord;

/**
 * @Description 使用记录查询
 * @Author jersonk
 * @Date 2024/11/27 16:49
 * @Version v1.0
 */
@Dao
public interface UsageRecordDao {

    @Query("SELECT * FROM usage_history WHERE lastUsageTime >= :startTime ORDER BY usageCount DESC, lastUsageTime DESC LIMIT :count")
    List<UsageRecord> getAllUsageRecord(long startTime, int count);

    @Query("SELECT * FROM usage_history WHERE pkgName = :packageName")
    UsageRecord getUsageRecordByPackageName(String packageName);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(UsageRecord... entity);

    @Delete
    void delete(UsageRecord... entity);

}
