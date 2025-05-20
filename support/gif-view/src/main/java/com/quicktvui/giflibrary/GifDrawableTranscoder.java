package com.quicktvui.giflibrary;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.quicktvui.rastermill.FrameSequence;
import com.quicktvui.rastermill.FrameSequenceDrawable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;
import com.bumptech.glide.load.resource.transcode.ResourceTranscoder;

public class GifDrawableTranscoder implements ResourceTranscoder<FrameSequence, FrameSequenceDrawable> {
    public GifDrawableTranscoder() {
    }

    @Nullable
    public Resource<FrameSequenceDrawable> transcode(@NonNull Resource<FrameSequence> toTranscode, @NonNull Options options) {
        FrameSequenceDrawable drawable = new FrameSequenceDrawable((FrameSequence)toTranscode.get());
        return new SimpleResource(drawable);
    }
}