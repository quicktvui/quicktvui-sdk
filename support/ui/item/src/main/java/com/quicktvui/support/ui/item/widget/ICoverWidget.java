package com.quicktvui.support.ui.item.widget;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import com.quicktvui.support.ui.render.RenderNode;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public interface ICoverWidget extends IWidget{

    //必须唯一名称
    String NAME = "Cover";

    void setImageDrawable(@Nullable  Drawable d);
    void setImagePath(@NotNull String path);
    void setImageResource(int id);

    void setImageBitmap(@NotNull Bitmap bitmap);

    void cancelLoad();

    @Deprecated
    void onRecycle();

    boolean isNeedLoadNewImage(String path);

    @Nullable String getCurrentImagePath();
    void setLoadImageDelayTime(int delayTime);
    void notifyParentSizeChanged(int width,int height);

    RenderNode setSize(int width, int height);

    void recycle();
    void reload();

}
