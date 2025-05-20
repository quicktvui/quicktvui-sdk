package com.quicktvui.support.imageloader;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.util.ByteBufferUtil;
import com.quicktvui.sdk.base.EsCallback;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.image.IEsImageLoader;
import com.sunrain.toolkit.utils.FileUtils;
import com.sunrain.toolkit.utils.SPUtils;
import com.sunrain.toolkit.utils.Utils;
import com.sunrain.toolkit.utils.log.L;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;


/**
 * <br>
 *
 * <br>
 */
public class EsGlideImageLoader implements IEsImageLoader {

    // 默认图片缓存大小
    public static final int DEFAULT_IMAGE_CACHE_SIZE = 80;
    // 默认图片缓存dir
    public static final String PATH_IMAGE_CACHE = "image_manager_disk_cache_es";

    private final Handler mHandler;

    public static void initGlobalGlide(Context context){
        initGlobalGlide(context, DEFAULT_IMAGE_CACHE_SIZE, PATH_IMAGE_CACHE);
    }

    public static void initGlobalGlide(Context context, int mbSize){
        initGlobalGlide(context, mbSize, PATH_IMAGE_CACHE);
    }

    public static void initGlobalGlide(Context context, String cacheDirName){
        initGlobalGlide(context, DEFAULT_IMAGE_CACHE_SIZE, cacheDirName);
    }

    public static void initGlobalGlide(Context context, int mbSize, String cacheDirName){
        GlideBuilder builder = new GlideBuilder();
        int diskCacheSizeBytes = mbSize * 1024 * 1024; // 80 MB
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, cacheDirName, diskCacheSizeBytes));
        Glide.init(context, builder);
        clearCacheIfNeed(context);
    }

    private static void clearCacheIfNeed(Context context) {
        if (isFirstChangeImageCache()) {
            new Thread(()-> FileUtils.delete(new File(context.getCacheDir(), DiskCache.Factory.DEFAULT_DISK_CACHE_DIR))).start();
            storeFirstChangeImageCache();
        }
    }

    private static final String SP_KEY_IS_CHANGE_IMAGE = "apply_img_cache";

    private static boolean isFirstChangeImageCache() {
        return SPUtils.getInstance("es_app").getBoolean(SP_KEY_IS_CHANGE_IMAGE, true);
    }

    private static void storeFirstChangeImageCache() {
        SPUtils.getInstance("es_app").put(SP_KEY_IS_CHANGE_IMAGE, false);
    }

    public EsGlideImageLoader() {
        mHandler = new Handler(Looper.getMainLooper());
        Glide glide = Glide.get(Utils.getApp());
        if (glide != null) {
            glide.getRegistry().replace(GlideUrl.class, InputStream.class, new EsHttpGlideUrlLoader.Factory());
        }
    }

    @Override
    public void loadImage(Context context, EsMap params, EsCallback<Object, Exception> callback) {
        if (context == null) return;
        if (params == null) return;
        if (context instanceof Activity) {
            if (((Activity) context).isFinishing()
                    || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1
                    && ((Activity) context).isDestroyed())) {
                return;
            }
        }
        Request request = new Request(params);
        if (L.DEBUG) L.logD("loadImage: " + request.url);
        LoadTarget target = new LoadTarget(mHandler, request, callback);
        Glide.with(context)
                .asDrawable()
                .load(request.url)
//                .downsample(DownsampleStrategy.CENTER_INSIDE)
                .apply(makeOptions(request))
                .listener(target)
                .into(target);
    }

    private RequestOptions makeOptions(Request request) {
        if(L.DEBUG){
            L.logI("load img " + request.url + " [" + request.width + "x" + request.height + "]");
        }
        RequestOptions options = new RequestOptions();
        if (!request.url.toLowerCase().endsWith(".gif")) {
            //fixme 20241203 by zhaopeng  为了解决人民日报图片过大（1920x2712)加载不出来的问题，将图片格式默认改为ARGB_565，并且format不根据fullQuality来判断
//            DecodeFormat f = request.fullQuality ? DecodeFormat.PREFER_ARGB_8888 : DecodeFormat.PREFER_RGB_565;
            DecodeFormat f = DecodeFormat.PREFER_RGB_565;
            if (request.format == 1) {
                f = DecodeFormat.PREFER_ARGB_8888;
            }
            options = options.format(f).dontAnimate();
//            if (request.fullQuality || EsContext.get().isRelieveImageSize()) {
            if (request.fullQuality) {
                options = options.override(Target.SIZE_ORIGINAL);
            } else {
                if (request.width > 0 && request.height > 0) {
                    options = options.override(request.width, request.height);
//                            .transform(new ResizeTransformation(request.width, request.height));
                }
            }
            if (request.circle) options = options.circleCrop();
        }
        if (!request.useMemoryCache || request.width >= 500 || request.height >= 500) {
            options = options.skipMemoryCache(true);
        }
        options = options.timeout(20000).priority(Priority.values()[request.priority]);
        return options;
    }

    private static final class ResizeTransformation extends BitmapTransformation {
        private int width;
        private int height;

        public ResizeTransformation(int width, int height) {
            this.width = width;
            this.height = height;
        }


        @Override
        protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap bitmap, int outWidth, int outHeight) {
            return Bitmap.createScaledBitmap(bitmap, width, height, true);
        }

        @Override
        public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
            String key = "resize_transformation_${width}_${height}";
            messageDigest.update(key.getBytes(Charset.forName("UTF-8")));
        }
    }

    @Override
    public void destroy(Context context) {

    }

    private static final class LoadTarget extends SimpleTarget<Drawable> implements RequestListener<Drawable> {

        private Handler handler;
        private EsCallback<Object, Exception> callback;

        public LoadTarget(Handler handler, Request request, EsCallback<Object, Exception> callback) {
//            super(request.width, request.height);
            this.handler = handler;
            this.callback = callback;
        }

        private long getResourceSize(Drawable resource) {
            if(!(resource instanceof BitmapDrawable)) return 0;
            Bitmap bitmap = ((BitmapDrawable) resource).getBitmap();
            if(bitmap == null) return 0;
            long size = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? bitmap.getAllocationByteCount() : bitmap.getByteCount();
            return size / 1024;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            if (resource instanceof GifDrawable) {
                byte[] bytes = ByteBufferUtil.toBytes(((GifDrawable) resource).getBuffer());
                handler.post(() -> this.callback.onSuccess(bytes));
            } else {
                if (L.DEBUG) {
                    long resourceSize = getResourceSize(resource);
                    if (resourceSize >= 3072) {
                        Log.e("[-EsGlideImageLoader-]", dataSource + " IMG MEMORY SIZE >= 3M !!!\n" + resourceSize + "K, " + model);
                    }
                }
                if (resource.getIntrinsicWidth() >= 4096
                        || resource.getIntrinsicHeight() >= 4096) {
                    Log.e("[-EsGlideImageLoader-]", dataSource + " width:" + resource.getIntrinsicWidth() + ", height:" + resource.getIntrinsicHeight() + " " + model);
                    callback.onFailed(new RuntimeException("图片太大！！！"));
                } else {
                    handler.post(() -> this.callback.onSuccess(resource));
                }
            }
            return false;
        }

        @Override
        public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
        }

        @Override
        public boolean onLoadFailed(GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            handler.post(() -> callback.onFailed(e));
            return false;
        }

        @Override
        public void onLoadFailed(Drawable errorDrawable) {
        }

    }
}
