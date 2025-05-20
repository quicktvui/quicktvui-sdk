package com.quicktvui.sdk.core.db.entity;

import android.text.TextUtils;

import android.support.annotation.NonNull;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;

import com.sunrain.toolkit.utils.FileUtils;
import com.sunrain.toolkit.utils.log.L;

import java.io.File;
import java.util.Objects;

import com.quicktvui.sdk.core.internal.Constants;

/**
 *
 */
@Entity(tableName = "rpk", primaryKeys = {"pkg", "ver"})
public class RpkEntity {

    @ColumnInfo
    @NonNull
    public String pkg;
    @ColumnInfo
    @NonNull
    public String ver;
    @ColumnInfo
    public int ver_code;
    @ColumnInfo
    public String uri;
    @ColumnInfo
    public String md5;
    @ColumnInfo
    public boolean enc;
    @ColumnInfo
    public boolean checking = true;
    /** 首次启动时间 **/
    @ColumnInfo
    public String firstStartTime = "0";
    /** 最新启动时间 **/
    @ColumnInfo
    public String lastStartTime = "0";

    // 版本路径
    @Ignore
    public File baseDir;
    // xxx.rpk
    @Ignore
    public File originFile;
    // xxx.rpk 解密
    @Ignore
    public File decryptFile;
    // 代码路径
    @Ignore
    public File codeDir;
    // 强制刷新
    @Ignore
    public boolean forceRefresh;
    @Ignore
    public boolean fromSpecial;
    @Ignore
    public int minAppVer;

    /** 检测本地缓存代码有效性 **/
    public static boolean checkCodeValidate(File rpkDir, RpkEntity self) {
        if (rpkDir == null || self == null) return false;
        if (self.codeDir == null) self.init(rpkDir);
        if (self.codeDir.exists()) {
            File indexJs = new File(self.codeDir, Constants.FILE_JS_INDEX);
            if (indexJs.exists()) return true;
        }
        L.logWF("code system corrupt");
        FileUtils.delete(self.codeDir);
        return false;
    }

    public void init(File dir) {
        if (dir == null) return;
        if(codeDir != null) return;
        baseDir = new File(dir, ver);
        FileUtils.createOrExistsDir(baseDir);
        originFile = new File(baseDir, "es" + Constants.PATH_RPK_SUFFIX);
        decryptFile = new File(baseDir, "es_decrypt" + Constants.PATH_RPK_SUFFIX);
        codeDir = new File(baseDir, Constants.PATH_RPK_CODE);
    }

    public RpkEntity setFirstStartTime(long firstStartTime) {
        this.firstStartTime = String.valueOf(firstStartTime);
        return this;
    }

    public long getFirstStartTime() {
        return TextUtils.isEmpty(firstStartTime) ? 0 : Long.parseLong(firstStartTime);
    }

    public RpkEntity setLastStartTime(long lastStartTime) {
        this.lastStartTime = String.valueOf(lastStartTime);
        return this;
    }

    public long getLastStartTime() {
        return TextUtils.isEmpty(lastStartTime) ? 0 : Long.parseLong(lastStartTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RpkEntity entity = (RpkEntity) o;
        return pkg.equals(entity.pkg) && ver.equals(entity.ver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pkg, ver);
    }

    @Override
    public String toString() {
        return "rpk{" +
                "pkg='" + pkg + '\'' +
                ", ver='" + ver + '\'' +
                ", ver_code=" + ver_code +
//                ", uri='" + uri + '\'' +
//                ", md5='" + md5 + '\'' +
//                ", enc=" + enc +
                ", checking=" + checking +
                ", firstStartTime=" + firstStartTime +
                ", lastStartTime=" + lastStartTime +
//                ", baseDir=" + baseDir +
//                ", originFile=" + originFile +
//                ", decryptFile=" + decryptFile +
//                ", codeDir=" + codeDir +
                ", forceRefresh=" + forceRefresh +
                ", fromSpecial=" + fromSpecial +
                '}';
    }
}
