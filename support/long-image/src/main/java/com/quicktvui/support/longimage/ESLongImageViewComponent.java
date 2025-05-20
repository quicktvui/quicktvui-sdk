package com.quicktvui.support.longimage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsCallback;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.support.longimage.utils.luban.Luban;
import com.quicktvui.support.longimage.utils.luban.OnCompressListener;
import com.quicktvui.support.longimage.view.ESLongImageView;
import com.quicktvui.support.longimage.view.SubsamplingScaleImageView;
import com.quicktvui.support.longimage.decoder.ImageSource;
import com.quicktvui.support.longimage.decoder.ImageViewState;
import com.quicktvui.support.longimage.utils.LongImageLoader;
import com.sunrain.toolkit.utils.BuildConfig;
import com.tencent.mtt.hippy.utils.LogUtils;

import java.io.File;
import java.util.Random;


@SuppressLint("LongLogTag")
@ESKitAutoRegister
public class ESLongImageViewComponent implements IEsComponent<ESLongImageView> {
    public static final String CLASS_NAME = "ESLongImageViewController";
    private static final String TAG = "ESLongImageViewComponentLog";
    private float defaultScale = -1;
    private int defaultPointX, defaultPointY;
    private int STEP = 200;
    private boolean enableAutoCompress = false; //自动压缩开关
    private float compressScale = 0.5f; //压缩倍数，默认0.5（像素比压缩为二分之一，内存占用压缩为四分之一）

    @Override
    public ESLongImageView createView(Context context, EsMap params) {
        return new ESLongImageView(context);
    }

    @EsComponentAttribute
    public void setImageSrc(ESLongImageView view, String url) {
        if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "bigLong dispatchUIFunction functionName: setImageSrc---" + view + ",url:" + url);
        }
        if (view != null) {
            loadImage(view, url);
        }
    }


    @EsComponentAttribute
    public void setImageData(ESLongImageView view, String url) {
        if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "bigLong dispatchUIFunction functionName: setImageData---" + view + ",url:" + url);
        }
        if (view != null) {
            setImage(view, url);
        }
    }

    /**
     * 自动压缩开关 默认关闭
     *
     * @param view
     * @param enable
     */
    @EsComponentAttribute
    public void enableAutoCompress(ESLongImageView view, boolean enable) {
        this.enableAutoCompress = enable;
    }

    /**
     * 压缩倍数 默认0.5f
     *
     * @param scale
     */
    @EsComponentAttribute
    public void autoCompressScale(float scale) {
        this.compressScale = scale;
    }

    @EsComponentAttribute
    public void setMinimumDpi(ESLongImageView view, int minimumDpi) {
        if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "bigLong dispatchUIFunction functionName:" + view + ",minimumDpi:" + minimumDpi);
        }
        if (view != null) {
            view.setMinimumDpi(minimumDpi);
        }
    }

    @EsComponentAttribute
    public void setMinimumTileDpi(ESLongImageView view, int minimumTileDpi) {
//        if (BuildConfig.DEBUG) {
        LogUtils.d(TAG, "bigLong dispatchUIFunction functionName:" + view + ",minimumTileDpi:" + minimumTileDpi);
//        }
        if (view != null) {
            view.setMinimumTileDpi(minimumTileDpi);
        }
    }

    @EsComponentAttribute
    public void setMaximumDpi(ESLongImageView view, int dpi) {
        if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "bigLong dispatchUIFunction functionName:" + view + ",dpi:" + dpi);
        }
        if (view != null) {
            view.setMaximumDpi(dpi);
        }
    }

    @EsComponentAttribute
    public void setDoubleTapZoomDpi(ESLongImageView view, int dpi) {
        if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "bigLong dispatchUIFunction functionName:" + view + ",dpi:" + dpi);
        }
        if (view != null) {
            view.setDoubleTapZoomDpi(dpi);
        }
    }

    @EsComponentAttribute
    public void setDoubleTapZoomScale(ESLongImageView view, float doubleTapZoomScale) {
        if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "bigLong dispatchUIFunction functionName:" + view + ",doubleTapZoomScale:" + doubleTapZoomScale);
        }
        if (view != null) {
            view.setDoubleTapZoomScale(doubleTapZoomScale);
        }
    }

    @EsComponentAttribute
    public void setDoubleTapZoomDuration(ESLongImageView view, int tapZoomDuration) {
        if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "bigLong dispatchUIFunction functionName:" + view + ",tapZoomDuration:" + tapZoomDuration);
        }
        if (view != null) {
            view.setDoubleTapZoomDuration(tapZoomDuration);
        }
    }

    @EsComponentAttribute
    public void setScaleAndCenter(ESLongImageView view, float scale, int x, int y) {
        if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "bigLong dispatchUIFunction functionName:" + view + ",scale:" + scale);
        }
        if (view != null) {
            view.setScaleAndCenter(scale, new PointF(x, y));
        }
    }

    @EsComponentAttribute
    public void resetScaleAndCenter(ESLongImageView view) {
        if (view != null) {
            view.resetScaleAndCenter();
        }
    }

    @EsComponentAttribute
    public void setOrientation(ESLongImageView view, int orientation) {
        if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "bigLong dispatchUIFunction functionName:" + view + ",orientation:" + orientation);
        }
        if (view != null) {
            view.setOrientation(orientation);
        }
    }

    @EsComponentAttribute
    public void setMaxTileSize(ESLongImageView view, int maxPixels) {
        if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "bigLong dispatchUIFunction functionName:" + view + ",maxPixels:" + maxPixels);
        }
        if (view != null) {
            view.setMaxTileSize(maxPixels);
        }
    }

    @EsComponentAttribute
    public void setMaxTileSize(ESLongImageView view, int maxPixels, int maxPixelsY) {
        if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "bigLong dispatchUIFunction functionName:" + view + ",maxPixels:" + maxPixels);
        }
        if (view != null) {
            view.setMaxTileSize(maxPixels, maxPixelsY);
        }
    }

    @EsComponentAttribute
    public void setPanLimit(ESLongImageView view, int panLimit) {
        if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "bigLong dispatchUIFunction functionName:" + view + ",panLimit:" + panLimit);
        }
        if (view != null) {
            view.setPanLimit(panLimit);
        }
    }

    @EsComponentAttribute
    public void setMinimumScaleType(ESLongImageView view, int scaleType) {
        if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "bigLong dispatchUIFunction functionName:" + view + ",scaleType:" + scaleType);
        }
        if (view != null) {
            view.setMinimumScaleType(scaleType);
        }
    }

    @EsComponentAttribute
    public void setMaxScale(ESLongImageView view, float maxScale) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "bigLong dispatchUIFunction functionName:" + view + ",maxScale:" + maxScale);
        }
        if (view != null) {
            view.setMaxScale(maxScale);
        }
    }

    @EsComponentAttribute
    public void setMinScale(ESLongImageView view, float minScale) {
        if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "bigLong dispatchUIFunction functionName:" + view + ",minScale:" + minScale);
        }
        if (view != null) {
            view.setMinScale(minScale);
        }
    }

    @EsComponentAttribute
    public void setZoomEnabled(ESLongImageView view, boolean zoomEnabled) {
        if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "bigLong dispatchUIFunction functionName:" + view + ",zoomEnabled:" + zoomEnabled);
        }
        if (view != null) {
            view.setZoomEnabled(zoomEnabled);
        }
    }

    @EsComponentAttribute
    public void setQuickScaleEnabled(ESLongImageView view, boolean quickScaleEnabled) {
        if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "bigLong dispatchUIFunction functionName:" + view + ",quickScaleEnabled:" + quickScaleEnabled);
        }
        if (view != null) {
            view.setQuickScaleEnabled(quickScaleEnabled);
        }
    }

    @EsComponentAttribute
    public void setPanEnabled(ESLongImageView view, boolean panEnabled) {
        if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "bigLong dispatchUIFunction functionName:" + view + ",panEnabled:" + panEnabled);
        }
        if (view != null) {
            view.setPanEnabled(panEnabled);
        }
    }

    @EsComponentAttribute
    public void setEagerLoadingEnabled(ESLongImageView view, boolean eagerLoadingEnabled) {
        if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "bigLong dispatchUIFunction functionName:" + view + ",eagerLoadingEnabled:" + eagerLoadingEnabled);
        }
        if (view != null) {
            view.setEagerLoadingEnabled(eagerLoadingEnabled);
        }
    }

    @EsComponentAttribute
    public void setDebug(ESLongImageView view, boolean debug) {
        if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "bigLong dispatchUIFunction functionName:" + view + ",debug:" + debug);
        }
        if (view != null) {
            view.setDebug(debug);
        }
    }

    @EsComponentAttribute
    public void setOnLongClickListener(ESLongImageView view, View.OnLongClickListener onLongClickListener) {
        if (view != null) {
            view.setOnLongClickListener(onLongClickListener);
        }
    }

    @EsComponentAttribute
    public void viewToSourceCoord(ESLongImageView view, PointF pointF) {
        if (view != null) {
            view.viewToSourceCoord(pointF);
        }
    }

    @EsComponentAttribute
    public void viewToSourceCoord(ESLongImageView view, float vx, float vy) {
        if (view != null) {
            view.viewToSourceCoord(vx, vy);
        }
    }

    @EsComponentAttribute
    public void viewToSourceCoord(ESLongImageView view, PointF vxy, PointF sTarget) {
        if (view != null) {
            view.viewToSourceCoord(vxy, sTarget);
        }
    }

    @EsComponentAttribute
    public void sourceToViewCoord(ESLongImageView view, PointF pointF) {
        if (view != null) {
            view.sourceToViewCoord(pointF);
        }
    }

    @EsComponentAttribute
    public void sourceToViewCoord(ESLongImageView view, float vx, float vy) {
        if (view != null) {
            view.sourceToViewCoord(vx, vy);
        }
    }

    @EsComponentAttribute
    public void sourceToViewCoord(ESLongImageView view, PointF sxy, PointF vTarget) {
        if (view != null) {
            view.sourceToViewCoord(sxy, vTarget);
        }
    }

    @EsComponentAttribute
    public void setDispatchKeyEventFocus(ESLongImageView view, boolean isFocusKey) {
        if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "bigLong dispatchUIFunction functionName:" + view + ",isFocusKey:" + isFocusKey);
        }
        if (view != null) {
            view.setOnKeyListener((v, keyCode, event) -> {
                view.setDispatchKeyEvent(event, isFocusKey);
                return false;
            });
        }
    }

    @EsComponentAttribute
    public void setDefaultScaleAndPointF(ESLongImageView view, EsArray esArray) {
        if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "bigLong dispatchUIFunction functionName:" + view + ",esArray:" + esArray.toString());
        }
        if (view != null && esArray != null) {
            this.defaultScale = Float.parseFloat(esArray.getString(0));
            this.defaultPointX = esArray.getInt(1);
            this.defaultPointY = esArray.getInt(2);
        }
    }

    @EsComponentAttribute
    public void setMoveStep(ESLongImageView view, int data) {
        if (view != null) {
            this.STEP = data;
        }
    }

    /**
     * 回收图片及各种资源
     *
     * @param isRecycler 是否回收资源
     * @param view
     */
    @EsComponentAttribute
    public void recycle(ESLongImageView view, boolean isRecycler) {
        if (BuildConfig.DEBUG) {
            LogUtils.d(TAG, "bigLong dispatchUIFunction functionName:" + isRecycler + ",recycle:回收资源");
        }
        if (view != null) {
            if (isRecycler) {
                view.recycle();
            }
        }
    }

    public void loadImage(ESLongImageView view, String url) {
        if (!TextUtils.isEmpty(url)) {
            if (url.startsWith("file://") || url.startsWith("content://")) {
                setImageLocal(view, url);
            } else if (url.startsWith("http:") || url.startsWith("https:")) {
                setImage(view, url);
            }
        } else {
            view.onImageLoadError("图片地址为空！");
        }
    }

    public void setImageLocal(ESLongImageView view, String url) {
        try {
            if (url.startsWith("file://") || url.startsWith("content://")) {
                EsMap data = new EsMap();
                data.pushString("url", url);
                Uri uri = Uri.parse(url);
                LongImageLoader.getInstance().loadImageUri(EsProxy.get().getContext(), uri, data, enableAutoCompress, compressScale, new EsCallback<Bitmap, Throwable>() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        try {
                            if (LogUtils.isDebug()) {
                                LogUtils.d(TAG, "---图片宽度为---- " + bitmap.getWidth() + "---图片高度为---" + bitmap.getHeight() + "-----内存占用:" + bitmap.getByteCount());
                            }
                            if (defaultScale >= 0) {
                                view.setImage(ImageSource.cachedBitmap(bitmap), new ImageViewState(defaultScale, new PointF(defaultPointX, defaultPointY), 0));
                            } else {
                                view.setImage(ImageSource.cachedBitmap(bitmap));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        view.onImageLoaded();
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                        //尝试压缩
                        loadLuban(view, url);
//                            view.recycle();
//                            view.onImageLoadError(throwable.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            view.onImageLoadError(e);
        }

    }

    public void loadLuban(ESLongImageView view, String url) {
        if (!TextUtils.isEmpty(url)) {
            Luban.Builder builder = Luban.with(EsProxy.get().getContext())
                    .ignoreBy(100)
                    .setTargetDir(getPath())
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {

                        }

                        @Override
                        public void onSuccess(File file) {
                            try {
                                if (LogUtils.isDebug()) {
                                    LogUtils.d(TAG, "---图片路径为---- " + file.getPath());
                                }
                                if (defaultScale >= 0) {
                                    view.setImage(ImageSource.cachedBitmap(BitmapFactory.decodeFile(file.getPath())), new ImageViewState(defaultScale, new PointF(defaultPointX, defaultPointY), 0));
                                } else {
                                    view.setImage(ImageSource.cachedBitmap(BitmapFactory.decodeFile(file.getPath())));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            view.onImageLoaded();
                        }

                        @Override
                        public void onError(Throwable e) {
                            view.recycle();
                            view.onImageLoadError(e.getMessage());
                        }
                    });
            if (url.startsWith("file://") || url.startsWith("content://")) {
                Uri uri = Uri.parse(url);
                builder.load(uri);
            } else if (url.startsWith("http:") || url.startsWith("https:")) {
                builder.load(url);
            }
            builder.launch();
        }
    }

    //设置图片 网络地址
    public void setImage(ESLongImageView view, String url) {
        try {
            if (view != null) {
                if (!TextUtils.isEmpty(url)) {
                    if (BuildConfig.DEBUG) {
                        LogUtils.d(TAG, url);
                    }
                    EsMap data = new EsMap();
                    data.pushString("url", url);
                    LongImageLoader.getInstance().loadImage(EsProxy.get().getContext(), data, enableAutoCompress, compressScale, new EsCallback<Bitmap, Throwable>() {
                        @Override
                        public void onSuccess(Bitmap bitmap) {
                            try {
                                if (LogUtils.isDebug()) {
                                    LogUtils.d(TAG, "---图片宽度为---- " + bitmap.getWidth() + "---图片高度为---" + bitmap.getHeight() + "-----内存占用:" + bitmap.getByteCount());
                                }
                                if (defaultScale >= 0) {
                                    view.setImage(ImageSource.cachedBitmap(bitmap), new ImageViewState(defaultScale, new PointF(defaultPointX, defaultPointY), 0));
                                } else {
                                    view.setImage(ImageSource.cachedBitmap(bitmap));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            view.onImageLoaded();
                        }

                        @Override
                        public void onFailed(Throwable throwable) {
                            //尝试压缩
                            loadLuban(view, url);
//                            view.recycle();
//                            view.onImageLoadError(throwable.getMessage());
                        }
                    });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            view.onImageLoadError(e);
        }
    }

    private String getPath() {
        String path = Environment.getExternalStorageDirectory() + "/Luban/image/";
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }


    @Override
    public void dispatchFunction(ESLongImageView view, String eventName, EsArray params, EsPromise promise) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "bigLong dispatchUIFunction functionName:" + eventName + ",var:" + params);
        }
        switch (eventName) {
            case "setImageSrc":
                setImageSrc(view, params.getString(0));
                break;
            case "setLongImageData":
                //设置图片源
                setImage(view, params.getString(0));
                break;
            case "animateScaleAndCenter":
                //指定中心点位置并缩放 附加动画
                animateScaleAndCenter(view, Float.parseFloat(params.getString(0)), params.getInt(1), params.getInt(2), params.getInt(3));
                break;
            case "setScaleAndCenter":
                //指定中心点位置并缩放
                view.setScaleAndCenter(Float.parseFloat(params.getString(0)), new PointF(params.getInt(1), params.getInt(2)));
                break;
            case "setMinimumTileDpi":
                //降低图片质量
                view.setMinimumTileDpi(params.getInt(0));
                break;
            case "randomScaleAndCenter":
                //随机中心点位置和缩放倍数
                play(view);
                break;
            case "scaleToMax":
                //缩放到最大
                view.setScaleAndCenter(view.getMaxScale(), new PointF(0, 0));
                break;
            case "scaleToMin":
                //缩放到最小
                view.setScaleAndCenter(view.getMinScale(), new PointF(0, 0));
                break;
            case "setMinimumDpi":
                //设置最小dpi
                view.setMinimumDpi(params.getInt(0));
                break;
            case "doubleTouch":
                //双击屏幕
                doubleClickEvent(view);
                break;
            case "moveUp":
                //屏幕上移
                touchMoveUp(view);
                break;
            case "moveDown":
                //屏幕下移
                touchMoveDown(view);
                break;
            case "moveLeft":
                //屏幕左移
                touchMoveLeft(view);
                break;
            case "moveRight":
                //屏幕右移
                touchMoveRight(view);
                break;
            case "focusable":
                boolean focusable = params.getBoolean(0);
                if (focusable) {
                    view.setFocusable(true);
                    view.requestFocus();
                }
                break;
            case "recycle":
                //图片回收
                if (view != null) {
                    view.recycle();
                }
                break;
            case "enableAutoCompress":
                if (view != null) {
                    boolean enableAutoCompress = params.getBoolean(0);
                    this.enableAutoCompress(view, enableAutoCompress);
                }
                break;
            case "autoCompressScale":
                if (view != null) {
                    float autoCompressScale = Float.parseFloat(params.getString(0));
                    this.autoCompressScale(autoCompressScale);
                }
                break;
            case "getLongImageInfo":
                //获取图片信息
                EsMap map = new EsMap();
                try {
                    map.pushDouble("maxScale", view.getMaxScale());
                    map.pushDouble("minScale", view.getMinScale());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                promise.resolve(map);
                break;
        }
    }

    public void animateScaleAndCenter(ESLongImageView view, float scale, int x, int y, int duration) {
        if (view.isReady()) {
            PointF center = new PointF(x, y);
            SubsamplingScaleImageView.AnimationBuilder animationBuilder = view.animateScaleAndCenter(scale, center);
            animationBuilder.withDuration(duration).withInterruptible(false).start();
        }
    }

    private void play(ESLongImageView view) {
        Random random = new Random();
        if (view.isReady()) {
            float maxScale = view.getMaxScale();
            float minScale = view.getMinScale();
            float scale = (random.nextFloat() * (maxScale - minScale)) + minScale;
            PointF center = new PointF(random.nextInt(view.getSWidth()), random.nextInt(view.getSHeight()));
            SubsamplingScaleImageView.AnimationBuilder animationBuilder = view.animateScaleAndCenter(scale, center);
            animationBuilder.withDuration(1000).withInterruptible(false).start();
        }
    }

    public void touchMoveUp(View view) {
        long downTime = SystemClock.currentThreadTimeMillis();
        MotionEvent down = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, 0, 0, 0);
        downTime += 1000;
        MotionEvent move = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_MOVE, 0, STEP, 0);
        downTime += 1000;
        MotionEvent up = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, 0, 0, 0);
        view.dispatchTouchEvent(down);
        view.dispatchTouchEvent(move);
        view.dispatchTouchEvent(up);
    }

    public void touchMoveDown(View view) {
        long downTime = SystemClock.currentThreadTimeMillis();
        MotionEvent down = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, 0, 0, 0);
        downTime += 1000;
        MotionEvent move = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_MOVE, 0, -STEP, 0);
        downTime += 1000;
        MotionEvent up = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, 0, 0, 0);
        view.dispatchTouchEvent(down);
        view.dispatchTouchEvent(move);
        view.dispatchTouchEvent(up);
    }

    public void touchMoveLeft(View view) {
        long downTime = SystemClock.currentThreadTimeMillis();
        MotionEvent down = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, 0, 0, 0);
        downTime += 1000;
        MotionEvent move = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_MOVE, STEP, 0, 0);
        downTime += 1000;
        MotionEvent up = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, 0, 0, 0);
        view.dispatchTouchEvent(down);
        view.dispatchTouchEvent(move);
        view.dispatchTouchEvent(up);
    }

    public void touchMoveRight(View view) {
        long downTime = SystemClock.currentThreadTimeMillis();
        MotionEvent down = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, 0, 0, 0);
        downTime += 1000;
        MotionEvent move = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_MOVE, -STEP, 0, 0);
        downTime += 1000;
        MotionEvent up = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, 0, 0, 0);
        view.dispatchTouchEvent(down);
        view.dispatchTouchEvent(move);
        view.dispatchTouchEvent(up);
    }

    public void doubleClickEvent(View view) {
        long downTime = SystemClock.currentThreadTimeMillis();
        MotionEvent down1 = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, 0, 0, 0);
        downTime += 10;
        MotionEvent up1 = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, 0, 0, 0);
        downTime += 100;
        MotionEvent down2 = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, 0, 0, 0);
        downTime += 10;
        MotionEvent up2 = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, 0, 0, 0);
        view.dispatchTouchEvent(down1);
        view.dispatchTouchEvent(up1);
        view.dispatchTouchEvent(down2);
        view.dispatchTouchEvent(up2);
    }

    @Override
    public void destroy(ESLongImageView view) {
        if (view != null) {
            view.recycle();
            System.gc();
        }
    }
}
