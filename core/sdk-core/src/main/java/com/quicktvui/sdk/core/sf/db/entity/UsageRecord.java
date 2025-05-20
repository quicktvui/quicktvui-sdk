package com.quicktvui.sdk.core.sf.db.entity;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.Objects;

import com.quicktvui.sdk.core.internal.loader.RecordInfo;

/**
 * @Description 快应用使用历史记录
 * @Author jersonk
 * @Date 2024/11/27 15:53
 * @Version v1.0
 */
@Entity(tableName = "usage_history", primaryKeys = {"pkgName"})
public class UsageRecord implements Serializable {

    private static final long serialVersionUID = -2689451311584628325L;

    @ColumnInfo
    @NotNull
    public String pkgName;
    @ColumnInfo
    public String appName;
    @ColumnInfo
    public String versionCode;
    @ColumnInfo
    public String versionName;
    @ColumnInfo
    public String icon;
    @ColumnInfo
    public String iconCircle;
    @ColumnInfo
    public int usageCount;         // 使用次数
    @ColumnInfo
    public long lastUsageTime;     // 最近一次使用时间

    public static UsageRecord create(String pkgName, String appName, String versionCode, String versionName, String icon, String iconCircle) {
        UsageRecord obj = new UsageRecord();
        obj.pkgName = pkgName;
        obj.appName = appName;
        obj.versionCode = versionCode;
        obj.versionName = versionName;
        obj.icon = icon;
        obj.iconCircle = iconCircle;
        return obj;
    }

    public static UsageRecord create(RecordInfo recordInfo) {
        UsageRecord obj = new UsageRecord();
        obj.pkgName = recordInfo.pkg;
        obj.appName = recordInfo.name;
        obj.versionCode = recordInfo.versionCode;
        obj.versionName = recordInfo.versionName;
        obj.icon = recordInfo.iconNormal;
        obj.iconCircle = recordInfo.iconCircle;
        return obj;
    }

    public static UsageRecord create(String pkgName) {
        UsageRecord obj = new UsageRecord();
        obj.pkgName = pkgName;
        return obj;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UsageRecord)) return false;
        UsageRecord that = (UsageRecord) o;
        return pkgName.equals(that.pkgName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pkgName);
    }

    @Override
    public String toString() {
        return "UsageRecord{" +
                "pkgName='" + pkgName + '\'' +
                ", appName='" + appName + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", versionName='" + versionName + '\'' +
                ", icon='" + icon + '\'' +
                ", iconCircle='" + iconCircle + '\'' +
                ", usageCount=" + usageCount +
                ", lastUsageTime=" + lastUsageTime +
                '}';
    }
}
