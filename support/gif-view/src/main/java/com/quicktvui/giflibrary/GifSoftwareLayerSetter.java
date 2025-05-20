package com.quicktvui.giflibrary;

import static android.view.View.LAYER_TYPE_NONE;
import static android.view.View.LAYER_TYPE_SOFTWARE;

import android.graphics.Paint;
import android.os.Build.VERSION;
import android.support.annotation.Nullable;
import com.quicktvui.rastermill.FrameSequenceDrawable;
import android.widget.ImageView;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.bumptech.glide.request.target.Target;

public class GifSoftwareLayerSetter implements RequestListener<FrameSequenceDrawable> {
    private int mLoopCount = 3;

    public GifSoftwareLayerSetter(int loopCount) {
        this.mLoopCount = loopCount;
    }

    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<FrameSequenceDrawable> target, boolean isFirstResource) {
        ImageView view = (ImageView)((ImageViewTarget)target).getView();
        if (11 <= VERSION.SDK_INT) {
            view.setLayerType(LAYER_TYPE_NONE, (Paint)null);
        }

        return false;
    }

    public boolean onResourceReady(FrameSequenceDrawable resource, Object model, Target<FrameSequenceDrawable> target, DataSource dataSource, boolean isFirstResource) {
        ImageView view = (ImageView)((ImageViewTarget)target).getView();
        if (11 <= VERSION.SDK_INT) {
            view.setLayerType(LAYER_TYPE_SOFTWARE, (Paint)null);
        }

        FrameSequenceDrawable drawable = new FrameSequenceDrawable(resource.getFramSequence());
        drawable.setLoopBehavior(this.mLoopCount);
        view.setImageDrawable(drawable);
        return true;
    }
}