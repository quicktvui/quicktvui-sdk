
package com.quicktvui.support.ijk.base;

import android.graphics.Bitmap;

public final class IjkTimedBitmap {

    private Bitmap mBitmap = null;
    private int bitmapWidth = -1;
    private int bitmapHeight = -1;

    public IjkTimedBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public IjkTimedBitmap(Bitmap bitmap, int width, int height) {
        mBitmap = bitmap;
        bitmapWidth = width;
        bitmapHeight = height;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public int getWidth() {
        return bitmapWidth;
    }

    public int getHeight() {
        return bitmapHeight;
    }
}
