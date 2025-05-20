package com.quicktvui.sdk.core.sf.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.support.annotation.NonNull;

import com.sunrain.toolkit.utils.FileUtils;
import com.sunrain.toolkit.utils.log.L;

import java.io.File;
import java.lang.ref.SoftReference;

import com.quicktvui.sdk.core.internal.EsContext;
import com.quicktvui.sdk.core.sf.db.dao.UsageRecordDao;
import com.quicktvui.sdk.core.sf.db.entity.UsageRecord;

/**
 * @Description 快应用使用记录 db 管理类
 * @Author jersonk
 * @Date 2024/11/27 17:05
 * @Version v1.0
 */
public class UsageDbHelper {

    private UsageDbHelper() {}

    private static final class Holder {
        private static final UsageDbHelper INSTANCE = new UsageDbHelper();
    }

    public static UsageDbHelper get() {
        return Holder.INSTANCE;
    }

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "rpk_usage.db";

    private SoftReference<UsageDataBase> mDBCache;

    @Database(entities = {UsageRecord.class}, exportSchema = false, version = DB_VERSION)
    public static abstract class UsageDataBase extends RoomDatabase {

        public abstract UsageRecordDao rpkUsageRecordDao();

        @Override
        protected void finalize() throws Throwable {
            close();
            super.finalize();
        }
    }

    private UsageDataBase createDB() {
        UsageDataBase db;
        if (mDBCache == null || (db = mDBCache.get()) == null) {
            Context context = EsContext.get().getContext().getApplicationContext();
            db = Room.databaseBuilder(context, UsageDataBase.class, DB_NAME)
                    // 保底，迁移失败清空数据库
                    .fallbackToDestructiveMigration()
                    // 数据库创建回调
                    .addCallback(new RoomDatabase.Callback() {
                        @Override
                        public void onCreate(@NonNull SupportSQLiteDatabase db) {
                            super.onCreate(db);
                            if(L.DEBUG) L.logIF("onCreate db ：" + db.getPath());
                        }

                        @Override
                        public void onOpen(@NonNull SupportSQLiteDatabase db) {
                            super.onOpen(db);
                            if(L.DEBUG) L.logIF("onOpen db ：" + db.getPath());
                        }
                    })
                    .build();
            mDBCache = new SoftReference<>(db);
        }
        return db;
    }

    public void delDb() {
        try {
            L.logIF("delete db");
            Context context = EsContext.get().getContext().getApplicationContext();
            File dbFile = context.getDatabasePath(DB_NAME);
            FileUtils.delete(dbFile);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public UsageRecordDao getDao() {
        return createDB().rpkUsageRecordDao();
    }

}
