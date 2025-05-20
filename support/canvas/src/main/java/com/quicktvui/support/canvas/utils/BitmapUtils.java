/*
 * Copyright (c) 2021, the hapjs-platform Project Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.quicktvui.support.canvas.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.sunrain.toolkit.utils.Utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class BitmapUtils {

    public static final String NINE_PATCH_SUFFIX = ".9.png";
    private static final String TAG = "BitmapUtils";
    private static final Map<String, WeakReference<Drawable.ConstantState>> sCache =
            new ConcurrentHashMap<>();

    private static void removeOldCache(Uri uri) {
        Set<String> keys = sCache.keySet();
        if (keys == null || keys.size() <= 0) {
            return;
        }
        Set<String> keyList = new HashSet<>();
        keyList.addAll(keys);
        for (String key : keyList) {
            if (!TextUtils.isEmpty(key) && key.contains(uri.toString())) {
                sCache.remove(key);
            }
        }
    }

    private static Drawable createDrawable(Uri uri) {
        if (uri == null) {
            return null;
        }
        InputStream is = null;
        try {
            Context context = Utils.getApp();
            is = context.getContentResolver().openInputStream(uri);
            // NinePatchDrawable has error padding value if not set resource
            // in Android KK.
            return Drawable
                    .createFromResourceStream(context.getResources(), null, is, uri.toString());
        } catch (FileNotFoundException e) {
            Log.e(TAG, "createDrawable: ", e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                // Ignore
                Log.e(TAG, "create drawable error", e);
            }
        }

        return null;
    }

    public static Bitmap safeDecodeRegion(
            BitmapRegionDecoder regionDecoder, Rect rect, BitmapFactory.Options options) {
        Bitmap result = null;
        try {
            result = regionDecoder.decodeRegion(rect, options);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "safeDecodeRegion() failed OOM %s", e);
        } catch (Exception e) {
            Log.e(TAG, "safeDecodeRegion() failed %s", e);
        }

        return result;
    }

    public static BitmapRegionDecoder safeCreateBitmapRegionDecoder(InputStream stream) {
        BitmapRegionDecoder decoder = null;
        try {
            decoder = BitmapRegionDecoder.newInstance(stream, false);
        } catch (OutOfMemoryError e) {
            Log.e(TAG, "safeCreateBitmapRegionDecoder() failed OOM %s", e);
        } catch (Exception e) {
            Log.e(TAG, "safeCreateBitmapRegionDecoder() failed %s", e);
        }
        return decoder;
    }

    public static boolean isValidate(Bitmap bitmap) {
        return bitmap != null && !bitmap.isRecycled();
    }

    public static boolean isValidate(BitmapRegionDecoder regionDecoder) {
        return regionDecoder != null && !regionDecoder.isRecycled();
    }

    public static void fetchBitmap(Uri uri, BitmapLoadCallback callback) {
        //fetchBitmap(uri, callback, 0, 0);
    }

    /*public static void fetchBitmap(
            final Uri imgUri, final BitmapLoadCallback bitmapLoadCallback, int width, int height) {
        if (imgUri == null || TextUtils.isEmpty(imgUri.toString()) || bitmapLoadCallback == null) {
            return;
        }
        ImageDecodeOptions options =
                ImageDecodeOptions.newBuilder()
                        .setForceStaticImage(true)
                        .setDecodePreviewFrame(true)
                        .build();
        ResizeOptions resizeOptions = null;
        int realWidth = width;
        int realHeight = height;
        if (realWidth > 0 && realHeight > 0) {
            if (width == IntegerUtil.UNDEFINED || height == IntegerUtil.UNDEFINED) {
                realWidth = 0;
                realHeight = 0;
            }
            boolean doResize = (realWidth > 0 && realHeight > 0);
            resizeOptions = doResize ? new ResizeOptions(realWidth, realHeight) : null;
        }
        ImageRequest imageRequest =
                ImageRequestBuilder.newBuilderWithSource(imgUri)
                        .setImageDecodeOptions(options)
                        .setAutoRotateEnabled(true)
                        .setResizeOptions(resizeOptions)
                        .build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> mBackgroundDataSource =
                imagePipeline.fetchDecodedImage(imageRequest, null);
        mBackgroundDataSource.subscribe(
                new BaseBitmapDataSubscriber() {
                    @Override
                    protected void onFailureImpl(
                            DataSource<CloseableReference<CloseableImage>> dataSource) {
                        bitmapLoadCallback.onLoadFailure();
                    }

                    @Override
                    public void onNewResultImpl(
                            DataSource<CloseableReference<CloseableImage>> dataSource) {
                        if (!dataSource.isFinished()) {
                            return;
                        }
                        CloseableReference<CloseableImage> closeableImageRef =
                                dataSource.getResult();
                        Bitmap bitmap = null;
                        if (closeableImageRef != null
                                && closeableImageRef.get() instanceof CloseableBitmap) {
                            bitmap = ((CloseableBitmap) closeableImageRef.get())
                                    .getUnderlyingBitmap();
                        }
                        if (bitmap != null) {
                            bitmapLoadCallback.onLoadSuccess(closeableImageRef, bitmap);
                        } else {
                            bitmapLoadCallback.onLoadFailure();
                        }
                    }

                    @Override
                    protected void onNewResultImpl(Bitmap bitmap) {
                    }
                },
                UiThreadImmediateExecutorService.getInstance());
    }*/

    public static Bitmap fetchBitmapSync(Uri uri) {
        return fetchBitmapSync(uri, 0, 0);
    }

    public static Bitmap fetchBitmapSync(Uri uri, int width, int height) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            throw new RuntimeException("cannot fetch Bitmap on MainThread!");
        }
        if (uri == null || TextUtils.isEmpty(uri.toString())) {
            return null;
        }
        /*ImageDecodeOptions options =
                ImageDecodeOptions.newBuilder()
                        .setForceStaticImage(true)
                        .setDecodePreviewFrame(true)
                        .build();
        ResizeOptions resizeOptions = null;
        int realWidth = width;
        int realHeight = height;
        if (realWidth > 0 && realHeight > 0) {
            if (width == IntegerUtil.UNDEFINED || height == IntegerUtil.UNDEFINED) {
                realWidth = 0;
                realHeight = 0;
            }
            boolean doResize = (realWidth > 0 && realHeight > 0);
            resizeOptions = doResize ? new ResizeOptions(realWidth, realHeight) : null;
        }
        ImageRequest imageRequest =
                ImageRequestBuilder.newBuilderWithSource(uri)
                        .setImageDecodeOptions(options)
                        .setAutoRotateEnabled(true)
                        .setResizeOptions(resizeOptions)
                        .build();
        ImagePipeline imagePipeline = Fresco.getImagePipeline();
        DataSource<CloseableReference<CloseableImage>> dataSource =
                imagePipeline.fetchDecodedImage(imageRequest, null);
        Bitmap bitmap = null;
        try {
            CloseableReference<CloseableImage> result = DataSources.waitForFinalResult(dataSource);
            if (result != null && result.get() instanceof CloseableBitmap) {
                bitmap = ((CloseableBitmap) result.get()).getUnderlyingBitmap();
            }
        } catch (Throwable ignored) {
            Log.e(TAG, "fetch bitmap sync error", ignored);
        } finally {
            dataSource.close();
        }*/
        return null;
    }

    public interface OnDrawableDecodedListener {
        void onDrawableDecoded(Drawable drawable, Uri uri);
    }

    public interface BitmapLoadCallback {
        //void onLoadSuccess(CloseableReference<CloseableImage> reference, Bitmap bitmap);

        void onLoadFailure();
    }
}
