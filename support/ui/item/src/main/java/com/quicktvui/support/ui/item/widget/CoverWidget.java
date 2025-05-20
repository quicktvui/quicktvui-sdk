package com.quicktvui.support.ui.item.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.SingleRequest;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.quicktvui.support.ui.item.Config;
import com.quicktvui.support.ui.item.ItemCenter;

import java.security.MessageDigest;

import com.quicktvui.support.ui.item.utils.SharedBitmapManager;


public class CoverWidget extends BuilderWidget<CoverWidget.Builder> implements ICoverWidget ,Transition.ViewAdapter {

    Drawable mDrawable;

    private Rect rect;


    //一个CoverWidget只有一个实例

    private LoadState loadState = new LoadState();

    public static final String TAG = "CoverWidget";

    private MyTarget loadTarget;

    //不可为0，为0时可能造成因任务没有取消，重复加载而导致的图片不清楚。
    int mDelayTime = 300;

    private void setRequestTag(Object tag) {
        loadState.changeTag(tag);
    }


    public Object getRequestTag() {
        return loadState.requestTag;
    }


//    @Nullable
//    @Override
//    public Callback getCallback() {
//        return this;
//    }

    @Override
    public View getView() {
        return mBuilder.hostView;
    }


    @Nullable
    @Override
    public Drawable getCurrentDrawable() {
        return mDrawable;
    }


    @Override
    public void setDrawable(Drawable drawable) {
        setResourceInternal(drawable);
    }


    private void setResourceInternal(Drawable drawable) {
        if(this.mDrawable == drawable && mDrawable != null){
            return;
        }
        mDrawable = drawable;
        if (mDrawable != null && !rect.isEmpty()) {
            mDrawable.setBounds(rect);
        }else{
            if (Config.DEBUG) {
                Log.w(TAG, " setResourceInternal rect is Null :"+rect);
            }
        }
        checkSize(drawable,rect);
        //maybeUpdateAnimatable(drawable);
        invalidateSelf();
    }


    void checkSize(Drawable drawable,Rect rect){
//        if(drawable != null && rect.width() > 60) {
//            if(!rect.equals(getBounds()) || rect.width() != getView().getWidth() || rect.height() != getView().getHeight()){
//                //Log.w(TAG,"checkSize error : size not match, drawable rect:"+rect+" bonus rect"+getBounds()+" hostView width:"+getView().getWidth()+" height:"+getView().getHeight()+"url :"+getCurrentImagePath());
//            }
//        }

    }

    @Override
    public void onViewDetachedFromWindow(View view) {
        super.onViewDetachedFromWindow(view);
        if (Config.DEBUG) {
            Log.d(TAG, " onViewAttachChange  ---- FromWindow this is  :"+this+" is Shown:"+view.isShown());
        }
        recycle();

    }

    @Override
    public void onViewAttachedToWindow(View view) {
        super.onViewAttachedToWindow(view);
        if (Config.DEBUG) {
            Log.i(TAG, " onViewAttachChange  +++# FromWindow this is  :"+this);
        }
        reload();
    }

    //    @Override
//    public void invalidateDrawable(@NonNull Drawable who) {
//        invalidateSelf();
//    }
//
//    @Override
//    public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {
////        if(v != null) {
////            v.scheduleDrawable(this,what,when);
////        }
//        if (Config.DEBUG) {
//            Log.d(TAG, "UpdateCover scheduleDrawable  coverWidget is :"+this);
//        }
//    }
//
//    @Override
//    public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {
////        if(v != null) {
////            v.unscheduleDrawable(this,what);
////        }
//        if (Config.DEBUG) {
//            Log.d(TAG, "UpdateCover unscheduleDrawable  coverWidget is :"+this);
//        }
//    }


    public static class Builder extends BuilderWidget.Builder<CoverWidget> {

        private Context context;
        private View hostView;
        private int roundCorner = 0;
        //private int marginBottom = 0;
        private int placeHolder = -1;
        RequestOptions options;
//        private int errorDrawable = -1;

        public Builder(Context context,View hostView) {
            super(context);
            this.context = context;
            this.hostView = hostView;
        }

        public Builder setOptions(RequestOptions options) {
            this.options = options;
            return this;
        }

        public Builder setRoundCorner(int roundCorner) {
            this.roundCorner = roundCorner;
            return this;
        }

        public CoverWidget build() {
            return new CoverWidget(this);
        }


        public Builder setPlaceHolder(int placeHolder) {
            this.placeHolder = placeHolder;
            return this;
        }



//        public Builder setMarginBottom(int marginBottom) {
//            this.marginBottom = marginBottom;
//            return this;
//        }

        @Override
        public Class getWidgetClass() {
            return CoverWidget.class;
        }
    }


    public CoverWidget(Builder builder) {
        super(builder);
        setSize(MATCH_PARENT, MATCH_PARENT);
    }


    @Override
    public void recycle() {
        if(Config.DEBUG){
            Log.d(TAG,"recycle called this:"+this);
        }
        removeCoverRunnable();
        if(loadTarget != null){
            loadTarget.discard();
            if(context instanceof Activity){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if(!((Activity) context).isDestroyed()){
                        Glide.with(context).clear(loadTarget);
                    }
                }else{
                    try {
                        Glide.with(context).clear(loadTarget);
                    }catch (Throwable t){
                        t.printStackTrace();
                    }
                }
            }else{
                try {
                    Glide.with(context).clear(loadTarget);
                }catch (Throwable t){
                    t.printStackTrace();
                }
            }
            loadTarget = null;
        }
        loadState.status = Status.PENDING;
        mDrawable = null;
    }

    @Override
    public void reload() {
        if(Config.DEBUG){
            Log.d(TAG,"reload called this:"+this);
        }
        final boolean isSizeValid = loadState.isSizeValid;
        if(isSizeValid && !TextUtils.isEmpty(loadState.getLoadUrl())) {
            requestLoadImage(this, loadState.validWidth,loadState.validHeight,loadState.getLoadUrl());
        }else{
            Log.e(TAG,"reload fail loadState Invalid :"+loadState);
        }

    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        if(bounds.isEmpty()){
            rect = getBounds();
            loadState.isSizeValid = false;
            return;
        }


        final int targetWidth = bounds.width();
        final int targetHeight = bounds.height();
        loadState.validWidth = targetWidth;
        loadState.validHeight = targetHeight;

        loadState.isSizeValid = targetWidth > 0 && targetHeight > 0;
        if (Config.DEBUG) {
            Log.d(TAG, " onBoundsChange1 loadState:"+loadState);
        }
        rect = bounds;
       // rect.bottom = bounds.height() - mBuilder.marginBottom;
//        Log.d(TAG,"CoverWidget onBoundsChange rect:"+rect+" rect.bottom:"+rect.bottom+" mBuilder.marginBottom is "+mBuilder.marginBottom+" bounds is"+bounds);
        if (mDrawable != null) {
            mDrawable.setBounds(rect);
            invalidateSelf();
        }
        if (loadState.isSizeValid  && !TextUtils.isEmpty(getCurrentImagePath())) {
            //当CoverWidget的大小变化时，图片应该重新加载
            requestLoadImage(this,targetWidth,targetHeight,loadState.getLoadUrl());
            //cancelLoad();
            if (Config.DEBUG) {
                Log.d(TAG, " onBoundsChange2 post UpdateCover ");
            }

        }else{
            if (Config.DEBUG) {
                Log.w(TAG, " onBoundsChange2 size is InValid drawable: "+mDrawable);
            }
        }
    }

    public void onResourceReady(Drawable resource, Transition transition) {

    }

    private void requestLoadImage(CoverWidget coverWidget,int targetWidth,int targetHeight,final String url){

        if (Config.DEBUG) {
            Log.w(TAG, " requestLoadImage targetWidth: "+targetWidth+" targetHeight:"+targetHeight+" url:"+url);
        }
        if(loadTarget != null){
            loadTarget.discard();
            loadTarget = null;
        }

        if(!ItemCenter.useOriginalImageSize) {
            loadTarget = new MyTarget(coverWidget, url, targetWidth, targetHeight);
        }else{
            loadTarget = new MyTarget(coverWidget, url, Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL);
        }

        removeCoverRunnable();
        final Runnable runnable = new CoverWidget.UpdateCover(url, context, loadTarget, getBuilder(),targetWidth, targetHeight,loadState);

        loadState.status = Status.RUNNING;
        lazyUpdateCover(runnable, mDelayTime);
    }

    @Override
    public void onDraw(Canvas canvas) {

        if (mDrawable != null) {
            mDrawable.draw(canvas);
            if(mDrawable instanceof  GifDrawable) {
                final GifDrawable d = (GifDrawable) mDrawable;
                getView().postInvalidateDelayed(16);
            }
//            if(animatable != null){
//                mDrawable.invalidateSelf();
//                getView().postInvalidateDelayed(16);
//            }
        }
    }


    @Override
    public void setImageDrawable(Drawable drawable) {
        removeCoverRunnable();
        if(drawable != null){
            loadState.status = Status.COMPLETE;
        }
        setResourceInternal(drawable);
    }


    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);
    }

    @Override
    public void setImagePath(final String url) {
        cancelLoad();
        setRequestTag(url);

        if (loadState.isSizeValid) {
            if (Config.DEBUG) {
                Log.d(TAG, " setImagePath isSizeValid is true  load state:"+loadState);
            }
            requestLoadImage(this,loadState.validWidth,loadState.validHeight,url);
        }else{
            if (Config.DEBUG) {
                Log.w(TAG, " setImagePath isSizeValid is false ,wait for size, loadState:"+loadState);
            }
            loadState.status = Status.WAITING_FOR_SIZE;
        }
    }

    @Override
    public void setImageResource(int id) {
        Bitmap bitmap = SharedBitmapManager.obtainBitmap(context, id);
        final BitmapDrawable drawable = new BitmapDrawable(context.getResources(),bitmap);
        setImageDrawable(drawable);
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        final BitmapDrawable drawable = new BitmapDrawable(context.getResources(),bitmap);
        setImageDrawable(drawable);
    }

    @Override
    public String getName() {
        return NAME;
    }

    boolean isGIf(){
//        return animatable != null;
        return false;
    }


    final static class UpdateCover implements Runnable {

        final String url;
        final MyTarget target;
        private Context context;
        final LoadState targetLoadState;
        final int preferWidth,preferHeight;
        final Builder builder;

        UpdateCover(String url, Context context, MyTarget target,Builder builder, int preferWidth, int preferHeight,LoadState loadState) {
            this.url = url;
            this.target = target;
            this.builder = builder;
            this.context = context;
            targetLoadState = loadState;
            this.preferWidth = preferWidth;
            this.preferHeight = preferHeight;
        }

        @Override
        public void run() {
            if (Config.DEBUG) {
                Log.i(TAG, "UpdateCoverTask run this:"+this);
            }

            final Context context = this.context.getApplicationContext();

            if(context instanceof Activity){
                Activity a = (Activity) context;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if(a.isDestroyed()){
                        return;
                    }
                }
            }
            if (null != context && targetLoadState.isValidURL(url)) {

                final RequestBuilder rb = Glide.with(context).asDrawable().diskCacheStrategy(ItemCenter.diskCacheStrategy);

                if(ItemCenter.skipMemoryCache){
                    rb.skipMemoryCache(true);
                }

//                if(ItemCenter.globalRequestOptionsFactory != null){
//                    rb.apply(ItemCenter.globalRequestOptionsFactory.build());
//                }

                if(builder.options == null){

                    if(builder.placeHolder > 0) {
                        rb.placeholder(builder.placeHolder);
                    }

                    if(builder.roundCorner > 0){
                        rb.transform(new GlideRoundTransform(builder.roundCorner));
                    }
                }else{
                    rb.apply(builder.options);
                }
                if(Config.DEBUG) {
                    Log.i(TAG, "UpdateCoverTask execute glide  image preferWidth:"+preferWidth+" preferHeight:"+preferHeight);
                }

                if(context instanceof Activity){
                    Activity a = (Activity) context;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        if(a.isDestroyed()){
                            return;
                        }
                    }
                }

                rb.load(url).into(target);
            }else{
                if(Config.DEBUG) {
                    Log.w(TAG, "UpdateCoverTask cancel glide  image ");
                }
            }
        }

        @Override
        public String toString() {
            return "UpdateCover{" +
                    "url='" + url + '\'' +
                    ", target=" + target +
                    ", context=" + context +
                    ", preferWidth=" + preferWidth +
                    ", preferHeight=" + preferHeight +
                    '}';
        }
    }


    public void onRecycle(){
        if(loadTarget != null){
            loadTarget.discard();
            loadTarget = null;
        }
        loadState.status = Status.PENDING;
        removeCoverRunnable();
    }

    @Override
    public void cancelLoad() {
        setRequestTag(null);
        if(loadTarget != null){
            loadTarget.discard();
            loadTarget = null;
        }
        loadState.status = Status.PENDING;
        removeCoverRunnable();
    }

    public void removeCoverRunnable() {
        if (mCoverUpdateRunnable != null) {
            if(Config.DEBUG) {
                Log.w(TAG, "remove pending CoverRunnable :"+mCoverUpdateRunnable);
            }
            removeCallbacks(mCoverUpdateRunnable);
        }
        this.mCoverUpdateRunnable = null;
    }

    @Override
    public boolean isNeedLoadNewImage(String path) {
        if(path == null){
            return false;
        }
        return !path.equals(getCurrentImagePath());
    }

    @Override
    public String getCurrentImagePath() {
        if(loadState.requestTag instanceof String){
            return (String) loadState.requestTag;
        }
        return null;
    }

    @Override
    public void setLoadImageDelayTime(int delayTime) {
        this.mDelayTime = delayTime;
    }

    @Override
    public void notifyParentSizeChanged(int width, int height) {
        //this.reSize(height);
    }

    Runnable mCoverUpdateRunnable;

    void lazyUpdateCover(Runnable coverUpdateRunnable, int delayTimeMillSecond) {
        this.mCoverUpdateRunnable = coverUpdateRunnable;

        postDelayed(mCoverUpdateRunnable, delayTimeMillSecond);
    }

    private void logRequest(SingleRequest request){
        if(Config.DEBUG) {

            if (request != null) {
                final String basic = "request isComplete:"+request.isComplete()+", isCleared:"+request.isCleared()+",isRunning:"+request.isRunning();
                Log.d(TAG,basic);
            } else {
                Log.d(TAG,"request is null");
            }
        }
    }

    private enum Status {
        /** Created but not yet running. */
        PENDING,
        /** In the process of fetching media. */
        RUNNING,
        /** Waiting for a callback given to the Target to be called to determine target dimensions. */
        WAITING_FOR_SIZE,
        /** Finished loading media successfully. */
        COMPLETE,
        /** Failed to load media, may be restarted. */
        FAILED,
        /** Cleared by the user with a placeholder set, may be restarted. */
        CLEARED,
    }

    static final class LoadState {

        Object requestTag;
        Request glideRequest;
        boolean isSizeValid = false;
        int validWidth = 0;
        int validHeight = 0;
        Status status = Status.PENDING;

        void changeTag(Object tag){
            this.requestTag = tag;
        }

        boolean isValidTag(Object tag){
            return tag != null && tag.equals(requestTag);
        }

        boolean isValidURL(String url){
            return isValidTag(url);
        }

        String getLoadUrl(){
            if(requestTag instanceof String) {
                return (String) requestTag;
            }else{
                return null;
            }
        }

        @Override
        public String toString() {
            return "LoadState{" +
                    "requestTag=" + requestTag +
                    ", glideRequest=" + glideRequest +
                    ", isSizeValid=" + isSizeValid +
                    ", validWidth=" + validWidth +
                    ", validHeight=" + validHeight +
                    ", status=" + status +
                    '}';
        }
    }

    final static class MyTarget extends CustomTarget<Drawable> implements Callback {

        final CoverWidget coverWidget;
        final String url;

        private boolean discard = false;

        @Nullable
        private Animatable animatable;


        MyTarget(CoverWidget coverWidget, String url) {
            super();
            this.coverWidget = coverWidget;
            this.url = url;
        }

        MyTarget(CoverWidget coverWidget, String url,int width,int height) {
            super(width,height);
            this.coverWidget = coverWidget;
            this.url = url;
        }

        @Override
        public void onResourceReady(Drawable resource, Transition transition) {

//        if (transition == null || !transition.transition(resource, this)) {
//            //20190506 这里只关注gif。由url加载的图片，将在updateCover中更新
//           // setResourceInternal(resource);
//        } else {
//            maybeUpdateAnimatable(resource);
//        }
            if(checkStatusValid()) {
                if (Config.DEBUG) {
                    Log.d(TAG, "UpdateCover onResourceReady  resource is :"+resource +" cover rect :"+coverWidget.rect);
                }
                coverWidget.loadState.status = Status.COMPLETE;
                coverWidget.onResourceReady(resource,transition);
                coverWidget.setResourceInternal(resource);
                //getSize();
                SizeReadyCallback cb = new SizeReadyCallback() {
                    @Override
                    public void onSizeReady(int width, int height) {
                        if(width > 0 && height > 0){
                            coverWidget.rect = new Rect(0,0,width,height);
                            coverWidget.mDrawable .setBounds(coverWidget.rect);
                        }
                    }
                };
                getSize(cb);
                maybeUpdateAnimatable(resource);

            }else{
                Log.e("CoverWidget","onResourceReady status invalid");
            }
        }

        private void maybeUpdateAnimatable(@Nullable Drawable resource) {
            if (Config.DEBUG) {
                Log.d(TAG, "maybeUpdateAnimatable resource:"+resource);
            }
            if (resource instanceof Animatable) {
                animatable = (Animatable) resource;
                if(animatable instanceof GifDrawable){

                    ((GifDrawable) animatable).setLoopCount(-1);
                    ((GifDrawable) animatable).setVisible(true,true);
                    ((GifDrawable) animatable).start();
                }
//            animatable.start();
                resource.setCallback(this);
            } else {
                if(animatable != null && animatable instanceof Drawable) {
//                ((Drawable)animatable).setCallback(null);
                }
                animatable = null;
            }
        }

        @Override
        public void onLoadStarted(@Nullable Drawable placeholder) {

            if(checkStatusValid()) {
                if (Config.DEBUG) {
                    Log.d(TAG, "UpdateCover onLoadStarted  coverWidget is :"+this +" placeHolder:"+placeholder);
                }
                if (placeholder != null) {
                    coverWidget.setDrawable(placeholder);
                }
            }else{
                Log.e("CoverWidget","onLoadStarted status invalid");
            }
        }

        private boolean checkStatusValid(){
            return !discard && coverWidget.getRequestTag() != null && coverWidget.getRequestTag().equals(url);
        }




        @Override
        public void onLoadFailed(@Nullable Drawable errorDrawable) {

            if(checkStatusValid()) {
                if (Config.DEBUG) {
                    Log.d(TAG, "UpdateCover onLoadFailed  errorDrawable is :"+errorDrawable);
                }
                coverWidget.loadState.status = Status.FAILED;
                if (errorDrawable != null) {
                    coverWidget.setResourceInternal(errorDrawable);
                } else {
                    coverWidget.setResourceInternal(null);
                }
            }else{
                Log.e("CoverWidget","onLoadFailed status invalid");
            }
        }

        @Override
        public void onLoadCleared(@Nullable Drawable placeholder) {

            if(checkStatusValid()) {
                if (Config.DEBUG) {
                    Log.d(TAG, "UpdateCover onLoadCleared  coverWidget is :"+this+" placeholder :"+placeholder);
                }
                coverWidget.loadState.status = Status.CLEARED;
                coverWidget.removeCoverRunnable();

                if (placeholder == null) {
                    coverWidget.setResourceInternal(null);
                } else {
                    coverWidget.setResourceInternal(placeholder);
                }
            }else{
                Log.e("CoverWidget","onLoadCleared status invalid");
            }
//        setDrawable(placeholder);
        }



        @Override
        public void onStart() {
            if (animatable != null) {
                animatable.start();
                if(Config.DEBUG) {
                    Log.d("CoverWidget", "onStart ");
                }
            }
        }

        @Override
        public void onStop() {
            if (animatable != null) {
                animatable.stop();
                if(Config.DEBUG) {
                    Log.d("CoverWidget", "onStop ");
                }
            }
        }

        public void discard() {
            discard = true;
        }

        @Override
        public void invalidateDrawable(@NonNull Drawable who) {
            coverWidget.invalidateSelf();
        }

        @Override
        public void scheduleDrawable(@NonNull Drawable who, @NonNull Runnable what, long when) {

        }

        @Override
        public void unscheduleDrawable(@NonNull Drawable who, @NonNull Runnable what) {

        }
    }


    public static class GlideRoundTransform extends BitmapTransformation {

        private float radius = 0f;

        public GlideRoundTransform(int radius) {
            this.radius = radius;
        }

        @Override
        protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
            Bitmap bitmap = TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight);
            return roundCrop(pool, bitmap,outWidth,outHeight);
        }

        private static Bitmap getAlphaSafeBitmap(
                @NonNull BitmapPool pool, @NonNull Bitmap maybeAlphaSafe,int width,int height) {
            Bitmap.Config safeConfig = getAlphaSafeConfig(maybeAlphaSafe);
            if (safeConfig.equals(maybeAlphaSafe.getConfig())) {
                return maybeAlphaSafe;
            }

            Bitmap argbBitmap = pool.get(width,height, safeConfig);
            new Canvas(argbBitmap).drawBitmap(maybeAlphaSafe, 0 /*left*/, 0 /*top*/, null /*paint*/);

            // We now own this Bitmap. It's our responsibility to replace it in the pool outside this method
            // when we're finished with it.
            return argbBitmap;
        }

        @NonNull
        private static Bitmap.Config getAlphaSafeConfig(@NonNull Bitmap inBitmap) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // Avoid short circuiting the sdk check.
                if (Bitmap.Config.RGBA_F16.equals(inBitmap.getConfig())) { // NOPMD
                    return Bitmap.Config.RGBA_F16;
                }
            }

            return Bitmap.Config.ARGB_8888;
        }

        // Avoids warnings in M+.
        private static void clear(Canvas canvas) {
            canvas.setBitmap(null);
        }


        private Bitmap roundCrop(BitmapPool pool, Bitmap source,int width,int height) {
            if (source == null) return null;

            Bitmap.Config safeConfig = getAlphaSafeConfig(source);
            Bitmap toTransform = getAlphaSafeBitmap(pool, source,source.getWidth(),source.getHeight());
            Bitmap result = pool.get(source.getWidth(), source.getHeight(), safeConfig);

            Canvas canvas = new Canvas(result);
            Paint paint = new Paint();
            paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
            paint.setAntiAlias(true);
            RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getHeight());
            canvas.drawRoundRect(rectF, radius, radius, paint);
            clear(canvas);
            if (!toTransform.equals(source)) {
                pool.put(toTransform);
            }
            return result;
        }

        public String getId() {
            return getClass().getName() + Math.round(radius);
        }

        @Override
        public void updateDiskCacheKey(MessageDigest messageDigest) {

        }


    }


}
