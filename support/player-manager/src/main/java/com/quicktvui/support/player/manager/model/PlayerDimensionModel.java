package com.quicktvui.support.player.manager.model;

import android.content.Context;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Objects;

import com.quicktvui.support.player.manager.utils.ScreenUtils;

public class PlayerDimensionModel implements IPlayerDimension {

    private int fullPlayerWidth;
    private int fullPlayerHeight;

    private int defaultPlayerWidth;
    private int defaultPlayerHeight;

    private boolean fullScreen;

    public PlayerDimensionModel(Builder builder) {
        fullPlayerWidth = builder.fullPlayerWidth;
        fullPlayerHeight = builder.fullPlayerHeight;
        defaultPlayerWidth = builder.defaultPlayerWidth;
        defaultPlayerHeight = builder.defaultPlayerHeight;
        fullScreen = builder.fullScreen;
    }

    @Override
    public int getFullPlayerWidth() {
        return fullPlayerWidth;
    }

    @Override
    public int getFullPlayerHeight() {
        return fullPlayerHeight;
    }

    @Override
    public int getDefaultPlayerWidth() {
        return defaultPlayerWidth;
    }

    @Override
    public int getDefaultPlayerHeight() {
        return defaultPlayerHeight;
    }

    @Override
    public boolean isFullScreen() {
        return fullScreen;
    }

    @Override
    public void setFullScreen(boolean fullScreen) {
        this.fullScreen = fullScreen;
    }

    @Override
    public void setFullPlayerWidth(int fullPlayerWidth) {
        this.fullPlayerWidth = fullPlayerWidth;
    }

    @Override
    public void setFullPlayerHeight(int fullPlayerHeight) {
        this.fullPlayerHeight = fullPlayerHeight;
    }

    @Override
    public void setDefaultPlayerWidth(int defaultPlayerWidth) {
        this.defaultPlayerWidth = defaultPlayerWidth;
    }

    @Override
    public void setDefaultPlayerHeight(int defaultPlayerHeight) {
        this.defaultPlayerHeight = defaultPlayerHeight;
    }

    public static class Builder {
        private Context context;
        private int fullPlayerWidth;
        private int fullPlayerHeight;

        private int defaultPlayerWidth;
        private int defaultPlayerHeight;

        private boolean fullScreen;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setFullPlayerWidth(int fullPlayerWidth) {
            this.fullPlayerWidth = fullPlayerWidth;
            return this;
        }

        public Builder setFullPlayerHeight(int fullPlayerHeight) {
            this.fullPlayerHeight = fullPlayerHeight;
            return this;
        }

        public Builder setDefaultPlayerWidth(int defaultPlayerWidth) {
            this.defaultPlayerWidth = defaultPlayerWidth;
            return this;
        }

        public Builder setDefaultPlayerHeight(int defaultPlayerHeight) {
            this.defaultPlayerHeight = defaultPlayerHeight;
            return this;
        }

        public Builder setFullScreen(boolean fullScreen) {
            this.fullScreen = fullScreen;
            return this;
        }

        public PlayerDimensionModel build() {
            if (fullPlayerWidth <= 0 || fullPlayerHeight <= 0) {
                this.fullPlayerWidth = ScreenUtils.getScreenWidth(context);
                this.fullPlayerHeight = ScreenUtils.getScreenHeight(context);
            }

            if (defaultPlayerHeight < -1 || defaultPlayerWidth < -1) {
                defaultPlayerWidth = fullPlayerWidth;
                defaultPlayerHeight = fullPlayerHeight;
            }
            return new PlayerDimensionModel(this);
        }
    }

    @NonNull
    @Override
    public String toString() {
        return "PlayerViewSizeModel{" +
                "fullPlayerWidth=" + fullPlayerWidth +
                ", fullPlayerHeight=" + fullPlayerHeight +
                ", defaultPlayerWidth=" + defaultPlayerWidth +
                ", defaultPlayerHeight=" + defaultPlayerHeight +
                ", defaultFullScreenPlay=" + fullScreen +
                '}';
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (super.equals(obj))
            return true;


        if (!(obj instanceof PlayerDimensionModel))
            return false;

        return defaultPlayerHeight == ((PlayerDimensionModel) obj).defaultPlayerHeight
                && defaultPlayerWidth == ((PlayerDimensionModel) obj).defaultPlayerWidth
                && fullPlayerHeight == ((PlayerDimensionModel) obj).fullPlayerHeight
                && fullPlayerWidth == ((PlayerDimensionModel) obj).fullPlayerWidth
                && fullScreen == ((PlayerDimensionModel) obj).fullScreen;
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultPlayerHeight, defaultPlayerWidth,
                fullPlayerHeight, fullPlayerWidth, fullScreen);
    }
}
