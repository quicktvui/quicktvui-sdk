package com.quicktvui.support.longimage.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.quicktvui.sdk.base.EsCallback;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.sunrain.toolkit.utils.log.L;

import java.io.InputStream;


import com.quicktvui.support.imageloader.EsHttpGlideUrlLoader;
import com.quicktvui.support.imageloader.Request;

public class LongImageLoader {
    private final Handler mHandler;
    static volatile LongImageLoader INSTANCE;
    private LongLoadTarget target;

    public static LongImageLoader getInstance() {
        if (INSTANCE == null) {
            synchronized (LongImageLoader.class) {
                if (INSTANCE == null) {
                    INSTANCE = new LongImageLoader();
                }
            }
        }
        return INSTANCE;
    }

    public LongImageLoader() {
        mHandler = new Handler(Looper.getMainLooper());
        Glide glide = Glide.get(EsProxy.get().getContext());
        if (glide != null) {
            glide.getRegistry().replace(GlideUrl.class, InputStream.class, new EsHttpGlideUrlLoader.Factory());
        }
        target = new LongLoadTarget(mHandler, false, 0.5f);
    }

    public void loadImage(Context context, EsMap params, boolean enableAutoCompress, float compressScale, EsCallback<Bitmap, Throwable> callback) {
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()
                    || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                    && ((Activity) context).isDestroyed())) {
                return;
            }
        }
        Request request = new Request(params);
        if (L.DEBUG) L.logD("loadImage: " + request.url);
        if (target != null) {
            target.setEnableAutoCompress(enableAutoCompress);
            target.setCompressScale(compressScale);
            target.setCallback(callback);
            Glide.with(context)
                    .asBitmap()
                    .load(request.url)
                    .apply(makeOptions(request))
                    .listener(target)
                    .into(target);
        }
    }

    public void loadImageUri(Context context, Uri uri, EsMap params,boolean enableAutoCompress, float compressScale, EsCallback<Bitmap, Throwable> callback ){
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()
                    || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                    && ((Activity) context).isDestroyed())) {
                return;
            }
        }
        Request request = new Request(params);
        if (L.DEBUG) L.logD("loadImage: " + uri.toString());
        if (target != null) {
            target.setEnableAutoCompress(enableAutoCompress);
            target.setCompressScale(compressScale);
            target.setCallback(callback);
            Glide.with(context)
                    .asBitmap()
                    .load(uri)
                    .apply(makeOptions(request))
                    .listener(target)
                    .into(target);
        }
    }

    private static class LongLoadTarget extends SimpleTarget<Bitmap> implements RequestListener<Bitmap> {

        private Handler handler;
        private EsCallback<Bitmap, Throwable> callback;
        private boolean enableAutoCompress;
        private float compressScale;

        public LongLoadTarget(Handler handler, boolean enableAutoCompress, float compressScale) {
            this.handler = handler;
            this.enableAutoCompress = enableAutoCompress;
            this.compressScale = compressScale;
        }

        @Override
        public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
            handler.post(() -> this.callback.onSuccess(enableAutoCompress ? matrixBitmap(resource, compressScale) : resource));
            return false;
        }

        @Override
        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
        }

        @Override
        public boolean onLoadFailed(GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
            handler.post(() -> callback.onFailed(e));
            return false;
        }

        @Override
        public void onLoadFailed(Drawable errorDrawable) {
        }

        public void setCallback(EsCallback<Bitmap, Throwable> callback) {
            this.callback = callback;
        }

        public Bitmap matrixBitmap(Bitmap bitmap, Float scale) {
            Matrix matrix = new Matrix();
            matrix.setScale(scale, scale);
//            BitmapFactory.Options options=new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
            Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            return bm;
        }

        public void setEnableAutoCompress(boolean enableAutoCompress) {
            this.enableAutoCompress = enableAutoCompress;
        }

        public void setCompressScale(float compressScale) {
            this.compressScale = compressScale;
        }
    }

    private RequestOptions makeOptions(Request request) {
        RequestOptions options = new RequestOptions();
        if (!request.url.toLowerCase().endsWith(".gif")) {
            DecodeFormat f = request.fullQuality ? DecodeFormat.PREFER_ARGB_8888 : DecodeFormat.PREFER_RGB_565;
            options = options.skipMemoryCache(true).format(f).dontAnimate();
            if (request.width > 0 && request.height > 0)
                options = options.override(request.width, request.height);
            if (request.circle) options = options.circleCrop();
        }
        if (request.width >= 500 || request.height >= 500) {
            options = options.skipMemoryCache(true);
        }
        options = options.timeout(20000).priority(Priority.values()[request.priority]);
        return options;
    }

    public void destroy(Context context) {

    }
}
