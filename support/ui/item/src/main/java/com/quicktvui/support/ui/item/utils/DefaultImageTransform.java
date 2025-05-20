package com.quicktvui.support.ui.item.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Shader;

import android.support.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import java.security.MessageDigest;

public class DefaultImageTransform extends BitmapTransformation {

    private final String TAG = getClass().getName();

    ColorMatrix colorMatrix;
    float contrast = 128f;

    public DefaultImageTransform() {
        colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.1f);
        colorMatrix.set(new float[] { contrast, 0, 0, 0, 0, 0,
                contrast, 0, 0, 0,// 改变对比度
                0, 0, contrast, 0, 0, 0, 0, 0, 1, 0 });
    }

    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {

        int width = toTransform.getWidth();
        int height = toTransform.getHeight();

        Bitmap bitmap = pool.get(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setHasAlpha(true);

        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(0.1f);
        colorMatrix.set(new float[] { contrast, 0, 0, 0, 0, 0,
                contrast, 0, 0, 0,// 改变对比度
                0, 0, contrast, 0, 0, 0, 0, 0, 1, 0 });

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawBitmap(toTransform,0,0,paint);

        return bitmap;
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
//        messageDigest.update((ID + radius + diameter + margin + cornerType).getBytes(CHARSET));
        messageDigest.update(TAG.getBytes(CHARSET));
    }
}
