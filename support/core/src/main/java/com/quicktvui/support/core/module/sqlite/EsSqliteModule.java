package com.quicktvui.support.core.module.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.PromiseHolder;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;
import com.sunrain.toolkit.utils.CloseUtils;
import com.sunrain.toolkit.utils.log.L;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Create by weipeng on 2022/05/18 15:23
 * Describe
 */
@ESKitAutoRegister
public class EsSqliteModule implements IEsModule, IEsInfo {

    public static final String K_VERSION = "__version";
    public static final String K_TABLES = "__tables";
    public static final String K_UPDATES = "__updates";
    public static final String K_SQLS = "__sqls";

    private final Map<String, DBHelper> mDBConnects = new HashMap<>(1);
    private String mKey;


    @Override
    public void init(Context context) {

    }

    @Override
    public void destroy() {
        L.logEF("destroy");
        Set<String> keys = mDBConnects.keySet();
        for (String key : keys) {
            mDBConnects.get(key).release();
        }
        mDBConnects.clear();
    }

    public void connect(EsMap params, EsPromise promise) {
        if (!testParams(params, promise)) return;
        Context context = EsProxy.get().getContext();
        mKey = EsProxy.get().getEsPackageName(this);
        if (context == null || TextUtils.isEmpty(mKey)) {
            PromiseHolder.create(promise).message("未知错误").sendFailed();
            return;
        }

        int dbVer = params.getInt(K_VERSION);
        EsArray tables = params.getArray(K_TABLES);
        EsArray updates = params.getArray(K_UPDATES);

        if (dbVer <= 0) dbVer = 1;

        if (tables == null) {
            PromiseHolder.create(promise).message("需要指定表结构 __tables[]").sendFailed();
            return;
        }

        String dbName = mKey + ".db";

        DBHelper db = getDB(mKey);

        if (db == null) {
            if (L.DEBUG) L.logD("创建 DBHelper");
            db = new DBHelper(context, dbName, dbVer, tables, updates);
            mDBConnects.put(mKey, db);
        }

        PromiseHolder.create(promise).sendSuccess();
    }

    public void execute(EsMap params, EsPromise promise) {
        if (!testParams(params, promise)) return;

        if (!params.containsKey(K_SQLS)) {
            PromiseHolder.create(promise).message("未指定sql语句  ".concat(K_SQLS)).sendFailed();
            return;
        }

        DBHelper db = getDB(mKey);
        if (db == null) {
            PromiseHolder.create(promise).message("未发现指定的数据库连接").sendFailed();
            return;
        }

        EsArray sqls = params.getArray(K_SQLS);
        db.execute(sqls, promise);
    }

    public void query(EsMap params, EsPromise promise) {
        if (!testParams(params, promise)) return;

        if (!params.containsKey(K_SQLS)) {
            PromiseHolder.create(promise).message("未指定sql语句  ".concat(K_SQLS)).sendFailed();
            return;
        }

        DBHelper db = getDB(mKey);
        if (db == null) {
            PromiseHolder.create(promise).message("未发现指定id的数据库连接").sendFailed();
            return;
        }

        EsArray sqls = params.getArray(K_SQLS);
        db.query(sqls, promise);
    }

    private boolean testParams(EsMap params, EsPromise promise) {
        boolean pass = params != null && params.size() > 0;
        if (!pass) PromiseHolder.create(promise).message("没有参数").sendFailed();
        return pass;
    }

    private DBHelper getDB(String key) {
        return mDBConnects.get(key);
    }

    private static final class DBHelper extends SQLiteOpenHelper {

        private Context context;
        private String name;
        private EsArray tables;
        private EsArray updates;

        private Handler mHandler;

        public DBHelper(Context context, String name, int version, EsArray tables, EsArray updates) {
            super(context, name, null, version);
            this.context = context;
            this.name = name;
            this.tables = tables;
            this.updates = updates;

            HandlerThread ht = new HandlerThread("es-sqlite");
            ht.start();
            mHandler = new Handler(ht.getLooper());

            if (L.DEBUG) L.logD("启动数据库线程");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            if (L.DEBUG) L.logD("onCreate");
            mHandler.post(new SqlExecutor(db, tables, null));
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (oldVersion >= newVersion) return;
            if (L.DEBUG) L.logD("onUpgrade");
            if (updates == null || updates.size() == 0) {
                closeDB();
                context.deleteDatabase(name);
                onCreate(getWritableDatabase());
                return;
            }

            int version = oldVersion;
            while (version <= newVersion) {
                int size = updates.size();
                for (int i = 0; i < size; i++) {
                    EsMap map = updates.getMap(i);
                    int targetVer = map.getInt(K_VERSION);
                    if (targetVer == version) {
                        EsArray sqls = map.getArray(K_SQLS);
                        mHandler.post(new SqlExecutor(db, sqls, null));
                    }
                }
                version++;
            }

        }

        public void execute(EsArray sqls, EsPromise promise) {
            mHandler.post(new SqlExecutor(getWritableDatabase(), sqls, promise));
        }

        public void query(EsArray sqls, EsPromise promise) {
            mHandler.post(new SqlExecutorCursor(getReadableDatabase(), sqls, promise));
        }

        private void closeDB() {
            mHandler.removeCallbacksAndMessages(null);
        }

        public void release() {
            closeDB();
            context = null;
            name = null;
            if (tables != null) {
                tables.clear();
            }
            if (updates != null) {
                updates.clear();
            }
            tables = null;
            updates = null;
            mHandler = null;
            System.gc();
        }

    }

    private static abstract class ReleaseRunnable implements Runnable {

        protected SQLiteDatabase db;

        public ReleaseRunnable(SQLiteDatabase db) {
            this.db = db;
        }

        @Override
        protected void finalize() throws Throwable {
            release();
            super.finalize();
        }

        protected void release() {
//            if (db != null && db.isOpen()) db.close();
//            if (L.DEBUG) L.logD("++++++++release " + this.hashCode() + " db:" + db.hashCode());
            db = null;
        }
    }

    private static final class SqlExecutor extends ReleaseRunnable {

        private EsArray sqls;
        private EsPromise promise;

        public SqlExecutor(SQLiteDatabase db, EsArray sqls, EsPromise promise) {
            super(db);
            this.sqls = sqls;
            this.promise = promise;
        }

        @Override
        public void run() {
            if (L.DEBUG) L.logD("开始执行 " + this.hashCode());
            if (sqls != null && sqls.size() > 0) {
                db.beginTransaction();
                try {
                    int size = sqls.size();
                    for (int i = 0; i < size; i++) {
                        String sql = sqls.getString(i);
                        if (L.DEBUG) L.logD("exec: " + sql);
                        db.execSQL(sql);
                    }
                    db.setTransactionSuccessful();
                    PromiseHolder.create(promise).sendSuccess();
                } catch (Exception e) {
                    L.logE(e.getMessage());
                    PromiseHolder.create(promise).message("" + e.getMessage()).sendFailed();
                }
                db.endTransaction();
            }
            release();
            if (L.DEBUG) L.logD("执行结束 " + this.hashCode());
        }

        @Override
        protected void release() {
            super.release();
            if (sqls != null) {
                sqls.clear();
                sqls = null;
                promise = null;
            }
        }
    }

    private static final class SqlExecutorCursor extends ReleaseRunnable {

        private EsArray sqls;
        private EsPromise promise;

        public SqlExecutorCursor(SQLiteDatabase db, EsArray sqls, EsPromise promise) {
            super(db);
            this.sqls = sqls;
            this.promise = promise;
        }

        @Override
        public void run() {
//            if (L.DEBUG) L.logD("开始执行 " + this.hashCode());
            if (sqls != null && sqls.size() > 0) {
                EsArray result = new EsArray();
                int size = sqls.size();
                for (int i = 0; i < size; i++) {
                    String sql = sqls.getString(i);
                    EsArray row = new EsArray();
                    result.pushArray(row);
                    Cursor cur = null;
                    try {
                        cur = db.rawQuery(sql, null);
                        if (cur != null) {
                            while (cur.moveToNext()) {
                                EsMap map = new EsMap();
                                int columnSize = cur.getColumnCount();
                                for (int index = 0; index < columnSize; index++) {
                                    fillMap(index, cur, map);
                                }
                                row.pushObject(map);
                            }
                        }

                    } catch (Exception e) {
                        L.logE(e.getMessage());
                        row.pushString("" + e.getMessage());
                    } finally {
                        CloseUtils.closeIO(cur);
                    }
                }
                PromiseHolder.create(promise).put("data", result).sendSuccess();
            }

            release();

//            if (L.DEBUG) L.logD("执行结束 " + this.hashCode());
        }

        private void fillMap(int index, Cursor cur, EsMap d) {
            String name = cur.getColumnName(index);
            int type = cur.getType(index);
            switch (type) {
                case Cursor.FIELD_TYPE_INTEGER:
                    d.pushInt(name, cur.getInt(index));
                    break;
                case Cursor.FIELD_TYPE_STRING:
                    d.pushString(name, cur.getString(index));
                    break;
                case Cursor.FIELD_TYPE_FLOAT:
                    d.pushDouble(name, cur.getFloat(index));
                    break;
                case Cursor.FIELD_TYPE_NULL:
                    d.pushNull(name);
                    break;
            }
        }

        @Override
        protected void release() {
            super.release();
            if (sqls != null) {
                sqls.clear();
                sqls = null;
                promise = null;
            }
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
}
