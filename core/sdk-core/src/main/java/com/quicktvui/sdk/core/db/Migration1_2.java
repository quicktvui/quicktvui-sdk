package com.quicktvui.sdk.core.db;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.migration.Migration;
import android.support.annotation.NonNull;

import com.sunrain.toolkit.utils.log.L;

/**
 *
 */
public class Migration1_2 extends Migration {

    /**
     * Creates a new migration between {@code startVersion} and {@code endVersion}.
     */
    public Migration1_2() {
        super(1, 2);
    }

    @Override
    public void migrate(@NonNull SupportSQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE rpk ADD COLUMN firstStartTime TEXT");
            db.execSQL("ALTER TABLE rpk ADD COLUMN lastStartTime TEXT");
        } catch (Exception e) {
            L.logW("db upload", e);
        }
    }

}
