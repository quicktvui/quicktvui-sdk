package com.quicktvui.support.ui.item.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.HashMap;

/**
 * 对Bitmap统一管理，避免重复decode资源
 */
public class SharedBitmapManager {
    static final HashMap<Integer, Bitmap> cache = new HashMap();

    public static Bitmap obtainBitmap(Context context, int bitmapID) {
        Bitmap d = cache.get(bitmapID);
        if (d == null) {
            d = BitmapFactory.decodeResource(context.getResources(), bitmapID);
            cache.put(bitmapID, d);
        }
        return d;
    }

}
