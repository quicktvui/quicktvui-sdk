package com.quicktvui.sdk.core.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.sunrain.toolkit.utils.FileUtils;
import com.sunrain.toolkit.utils.log.L;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.List;

import com.quicktvui.sdk.core.db.entity.RpkEntity;
import com.quicktvui.sdk.core.internal.EsContext;

/**
 *
 */
public class EsDBHelper {

    private static final int DB_VER = 2;

    private SoftReference<EsDataBase> mDBCache;

    public RpkEntityDao rpkInfoDao() {
        return getDB().rpkEntityDao();
    }

    @Database(entities = {RpkEntity.class}, exportSchema = false, version = DB_VER)
    public static abstract class EsDataBase extends RoomDatabase {
        public abstract RpkEntityDao rpkEntityDao();

        @Override
        protected void finalize() throws Throwable {
            close();
            super.finalize();
        }
    }

    @Dao
    public interface RpkEntityDao {
        @Query("SELECT * FROM rpk ORDER BY lastStartTime")
        List<RpkEntity> getAllVersionsWithLastUseTime();

        @Query("SELECT * FROM rpk WHERE pkg = :pkg ORDER BY ver_code DESC")
        List<RpkEntity> getAllVersions(String pkg);

        @Query("SELECT * FROM (SELECT * FROM rpk WHERE pkg= :pkg AND checking=1 LIMIT 1) " +
                "UNION ALL " +
                "SELECT * FROM (SELECT * FROM rpk WHERE pkg= :pkg ORDER BY ver DESC LIMIT 1)")
        RpkEntity getLoadVer(String pkg);

        @Query("SELECT * FROM rpk WHERE pkg = :pkg AND checking = 1 LIMIT 1")
        RpkEntity getCheckingVersion(String pkg);

        @Query("SELECT * FROM rpk WHERE pkg = :pkg AND checking = 0 ORDER BY ver_code DESC LIMIT 1")
        RpkEntity getLatestVersion(String pkg);

        @Query("SELECT * FROM rpk WHERE pkg = :pkg AND ver = :ver")
        RpkEntity getSpecialVersion(String pkg, String ver);

        @Query("SELECT * FROM rpk WHERE lastStartTime <= :timeMills")
        List<RpkEntity> getBeforeTime(long timeMills);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insertOrUpdate(RpkEntity... entity);

        @Delete
        void delete(RpkEntity... entity);
    }

    EsDataBase getDB() {
        EsDataBase db;
        if (mDBCache == null || (db = mDBCache.get()) == null) {
            Context context = EsContext.get().getContext().getApplicationContext();
            db = Room.databaseBuilder(context, EsDataBase.class, "es_rpk.db")
                    .addMigrations(new Migration1_2())
                    // 保底，迁移失败清空数据库
                    .fallbackToDestructiveMigration()
                    .build();
            mDBCache = new SoftReference<>(db);
        }
        return db;
    }

    public void forceDelete() {
        try {
            L.logIF("force delete db");
            Context context = EsContext.get().getContext().getApplicationContext();
            File dbFile = context.getDatabasePath("es_rpk.db");
            FileUtils.delete(dbFile);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //region 单例

    private static final class EsDBHolder {
        private static final EsDBHelper INSTANCE = new EsDBHelper();
    }

    public static EsDBHelper get() {
        return EsDBHolder.INSTANCE;
    }

    private EsDBHelper() {
    }

    //endregion

}
