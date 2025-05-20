package com.quicktvui.giflibrary;

import android.content.Context;
import android.net.Uri;
import com.quicktvui.rastermill.FrameSequenceDrawable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class GifImageLoader {
    public GifImageLoader() {
    }

    public static void display(Context context, String url, ImageView targeView, int loopCount) {
        display(context, url, targeView, loopCount, -1, -1);
    }

    public static void display(Context context, String url, ImageView targeView, int loopCount, int placeHolder, int errorHolder) {
        Uri uri = Uri.parse(url);
        Glide.with(context).as(FrameSequenceDrawable.class).listener(new GifSoftwareLayerSetter(loopCount)).load(uri).into(targeView);
    }
}