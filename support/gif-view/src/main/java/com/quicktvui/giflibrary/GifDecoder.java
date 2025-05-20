package com.quicktvui.giflibrary;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.quicktvui.rastermill.FrameSequence;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;

import java.io.IOException;
import java.io.InputStream;

public class GifDecoder implements ResourceDecoder<InputStream, FrameSequence> {
    public GifDecoder() {
    }

    public boolean handles(@NonNull InputStream source, @NonNull Options options) throws IOException {
        return true;
    }

    @Nullable
    public Resource<FrameSequence> decode(@NonNull InputStream source, int width, int height, @NonNull Options options) throws IOException {
        try {
            FrameSequence fs = FrameSequence.decodeStream(source);
            return new SimpleResource(fs);
        } catch (Exception var6) {
            throw new IOException("Cannot load gif from stream", var6);
        }
    }
}