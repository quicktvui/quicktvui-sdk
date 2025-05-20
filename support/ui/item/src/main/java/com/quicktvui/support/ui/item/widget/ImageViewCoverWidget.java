package com.quicktvui.support.ui.item.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import android.support.annotation.Nullable;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.quicktvui.support.ui.item.Config;
import com.quicktvui.support.ui.item.utils.SharedBitmapManager;


import com.quicktvui.support.ui.item.R;

@Deprecated
public class ImageViewCoverWidget extends BuilderWidget<ImageViewCoverWidget.Builder> implements ICoverWidget  {

    Drawable mDrawable;
   // Bitmap mBitmap;
//    private int moveOffset=0;
    private Rect rect;

    Request mRequest;

    Object mRequestTag;



    @Nullable
    private Animatable animatable;

    int mDelayTime = 0;

    void setRequestTag(Object tag) {
        this.mRequestTag = tag;
    }


    public Object getRequestTag() {
        return mRequestTag;
    }




    public static class Builder extends BuilderWidget.Builder<ImageViewCoverWidget> {

        private Context context;
        private ImageView imageView;
        private int roundCorner = -1;
        private int marginBottom = 0;
        private int placeHolder = -1;
//        private int errorDrawable = -1;

        public Builder(Context context, ImageView hostView) {
            super(context);
            this.context = context;
            this.imageView = hostView;
            roundCorner = context.getResources().getDimensionPixelSize(R.dimen.item_bar_corner);
        }


        public Builder setRoundCorner(int roundCorner) {
            this.roundCorner = roundCorner;
            return this;
        }

        public ImageViewCoverWidget build() {
            return new ImageViewCoverWidget(this);
        }



        public Builder setPlaceHolder(int placeHolder) {
            this.placeHolder = placeHolder;
            return this;
        }

//        public Builder setErrorDrawble(int errorDrawable) {
//            this.errorDrawable = errorDrawable;
//            return this;
//        }



        public Builder setMarginBottom(int marginBottom) {
            this.marginBottom = marginBottom;
            return this;
        }

        @Override
        public Class getWidgetClass() {
            return ImageViewCoverWidget.class;
        }
    }


    public ImageViewCoverWidget(Builder builder) {
        super(builder);
        setSize(MATCH_PARENT, MATCH_PARENT);
    }




    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        if(bounds.isEmpty()){
            rect = getBounds();
            return;
        }
        if (Config.DEBUG) {
            Log.d(TAG, " onBoundsChange1 UpdateCover is " + width() + " height is " +height()+" coverWidget is:"+this+" rect:"+rect+" url:"+getCurrentImagePath());
        }
        rect = bounds;
        rect.bottom = bounds.height() - mBuilder.marginBottom;
//        Log.d(TAG,"CoverWidget onBoundsChange rect:"+rect+" rect.bottom:"+rect.bottom+" mBuilder.marginBottom is "+mBuilder.marginBottom+" bounds is"+bounds);
        if (mDrawable != null) {
            mDrawable.setBounds(rect);
            invalidateSelf();
        }
        if (width() > 0 && height() > 0  && !TextUtils.isEmpty(getCurrentImagePath())) {
            //当CoverWidget的大小变化时，图片应该重新加载
            final Runnable runnable = new ImageViewCoverWidget.UpdateCover(getCurrentImagePath(), context, this);
            //cancelLoad();
            removeCoverRunnable();
            if (Config.DEBUG) {
                Log.d(TAG, " onBoundsChange2 UpdateCover is " + width() + " height is " +height()+" coverWidget is:"+this+" rect:"+rect+" url:"+getCurrentImagePath());
            }
            lazyUpdateCover(runnable, mDelayTime);
        }
    }

    @Override
    public void onDraw(Canvas canvas) {


    }


    @Override
    public void setImageDrawable(Drawable drawable) {
    }


    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);
    }

    @Override
    public void setImagePath(final String url) {
        cancelLoad();
        setRequestTag(url);
        if (Config.DEBUG) {
            Log.d(TAG, " setImagePath UpdateCover is " + width() + " height is " +height()+" coverWidget is:"+this+" url:"+url);
        }
        if (width() > 0 && height() > 0) {
            final Runnable runnable = new ImageViewCoverWidget.UpdateCover(url, context, this);
            lazyUpdateCover(runnable, mDelayTime);
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
        return animatable != null;
    }


    final static class UpdateCover implements Runnable {

        final String url;
        final ImageViewCoverWidget coverWidget;
        private Context context;

        UpdateCover(String url,Context context, ImageViewCoverWidget coverWidget) {
            this.url = url;
            this.coverWidget = coverWidget;
            this.context = context;
        }

        @Override
        public void run() {
            final Builder builder = coverWidget.mBuilder;
            final int width = coverWidget.width();
            final int height = coverWidget.height();
            if (Config.DEBUG) {
                Log.d(TAG, "UpdateCover width is " + width + " height is " + height+" url:"+url+" coverWidget is :"+coverWidget+" placeHolder:"+builder.placeHolder);
            }
            final Context context = this.context.getApplicationContext();
            if (null != context && null != url && url.equals(coverWidget.getCurrentImagePath())) {

                Glide.with(context).asDrawable().load(url).into(coverWidget.mBuilder.imageView);
//                ltb .options.signature(new GlideUrl(url+width+"x"+height));
//                BFImgDisplay.display(ltb
//                        .setPreferSize(coverWidget.width(), coverWidget.height())
//                        .setUri(url)
////                        .setShape(LoadTargetBuilder.ImageShape.Common)
//                        .setShape(coverWidget.mImageShape,coverWidget.mBuilder.roundCorner)


//                );
            }
        }

    }


    @Override
    public void cancelLoad() {
        setRequestTag(null);
        removeCoverRunnable();
    }

    @Override
    public void onRecycle() {

    }

    public void removeCoverRunnable() {
        if (mCoverUpdateRunnable != null) {
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
        if(mRequestTag instanceof String){
            return (String) mRequestTag;
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

    @Override
    public void recycle() {

    }

    @Override
    public void reload() {

    }

    public Runnable mCoverUpdateRunnable;

    public void lazyUpdateCover(Runnable coverUpdateRunnable, int delayTimeMillSecond) {

        this.mCoverUpdateRunnable = coverUpdateRunnable;

        postDelayed(mCoverUpdateRunnable, delayTimeMillSecond);
    }

}
