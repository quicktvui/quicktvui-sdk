package com.quicktvui.support.player.manager.manager;

import android.content.Context;

import com.quicktvui.support.player.manager.definition.Definition;
import com.quicktvui.support.player.manager.volume.IPlayerVolume;
import com.quicktvui.support.player.manager.volume.PlayerVolumeModel;

import java.lang.ref.WeakReference;

import com.quicktvui.support.player.manager.aspect.AspectRatio;
import com.quicktvui.support.player.manager.model.IPlayerDimension;
import com.quicktvui.support.player.manager.model.PlayerDimensionModel;
import com.quicktvui.support.player.manager.utils.Preconditions;

public class PlayerConfiguration {

    private WeakReference<Context> mContext;
    //是否默认起播
    private final boolean autoPlayVideo;
    //自动播放下一个视频， 默认自动播放下一个分集
    private boolean autoPlayNext;
    //循环播放
    private boolean loopPlay;
    //循环播放次数
    private int loopPlayTime;

    //是否默认自动显示播放器的view
    private boolean autoShowPlayerView;

    //-------------------------清晰度相关----------------------------
    //清晰度选择的类型
    //默认播放的清晰度，如果没有指定
    private final Definition defaultDefinition;
    private boolean isReadLocalDefinition;

    //-------------------------画面比例相关----------------------------
    //默认的屏幕比例，如果用户没有设置，则使用这个屏幕比例
    private final AspectRatio defaultAspectRatio;
    //是否保存切换的屏幕比例
    private final boolean isAutoSaveAspectRatio;

    //调试模式
    private final boolean isDebug;

    //宽高
    private IPlayerDimension playerDimension;

    //音量
    private IPlayerVolume playerVolume;

    //播放器配置,是否采用硬解码
    private final boolean usingHardwareDecoder;

    //是否用textureview
    private boolean usingTextureViewRender;

    //网络重新连接后是否重新播放
    private final boolean networkConnectedAutoPlay;

    //播放器是否可用
    private final boolean enabled;

    //默认支持可改变大小
    private final boolean enableChangeDimension;

    //使用播放器原始尺寸，暂时只在图片播放器上有效
    private final boolean useOriginPlayerDimension;

    public PlayerConfiguration(Builder builder) {
        mContext = new WeakReference<>(builder.context);
        autoPlayNext = builder.autoPlayNext;
        loopPlay = builder.loopPlay;
        loopPlayTime = builder.loopPlayTime;

        autoShowPlayerView = builder.autoShowPlayerView;

        defaultDefinition = builder.mDefaultDefinition;
        isReadLocalDefinition = builder.isReadLocalDefinition;

        defaultAspectRatio = builder.defaultAspectRatio;
        isAutoSaveAspectRatio = builder.isAutoSaveAspectRatio;

        isDebug = builder.isDebug;

        playerDimension = builder.playerDimension;
        playerVolume = builder.playerVolume;

        usingHardwareDecoder = builder.usingHardwareDecoder;
        usingTextureViewRender = builder.usingTextureViewRender;

        autoPlayVideo = builder.autoPlay;
        networkConnectedAutoPlay = builder.networkConnectedAutoPlay;
        enabled = builder.enabled;

        enableChangeDimension = builder.enableChangeDimension;

        //使用播放器原始尺寸
        useOriginPlayerDimension = builder.useOriginPlayerDimension;
    }

    public Context getContext() {
        return mContext.get();
    }

    public boolean isDebug() {
        return isDebug;
    }

    public boolean isAutoPlayNext() {
        return autoPlayNext;
    }

    public void setAutoPlayNext(boolean autoPlayNext) {
        this.autoPlayNext = autoPlayNext;
    }

    public void setLoopPlay(boolean loopPlay) {
        this.loopPlay = loopPlay;
    }

    /**
     * 循环播放的次数
     *
     * @return
     */
    public int getLoopPlayTime() {
        return loopPlayTime;
    }

    /**
     * 设置循环播放的次数
     *
     * @param loopPlayTime
     */
    public void setLoopPlayTime(int loopPlayTime) {
        this.loopPlayTime = loopPlayTime;
    }

    public boolean isAutoPlayVideo() {
        return autoPlayVideo;
    }

    public Definition getDefaultDefinition() {
        return defaultDefinition;
    }

    public AspectRatio getDefaultAspectRatio() {
        return defaultAspectRatio;
    }

    public boolean isReadLocalDefinition() {
        return isReadLocalDefinition;
    }

    public boolean isAutoSaveAspectRatio() {
        return isAutoSaveAspectRatio;
    }

    public boolean isNetworkConnectedAutoPlay() {
        return networkConnectedAutoPlay;
    }

    public boolean isEnableChangeDimension() {
        return enableChangeDimension;
    }

    public boolean isUseOriginPlayerDimension() {
        return useOriginPlayerDimension;
    }

    public int getFullPlayerWidth() {
        if (playerDimension != null) {
            return playerDimension.getFullPlayerWidth();
        }
        return 0;
    }

    public int getFullPlayerHeight() {
        if (playerDimension != null) {
            return playerDimension.getFullPlayerHeight();
        }
        return 0;
    }

    public int getDefaultPlayerWidth() {
        if (playerDimension != null) {
            return playerDimension.getDefaultPlayerWidth();
        }
        return 0;
    }

    public int getDefaultPlayerHeight() {
        if (playerDimension != null) {
            return playerDimension.getDefaultPlayerHeight();
        }
        return 0;
    }

    public boolean isFullScreen() {
        if (playerDimension != null) {
            return playerDimension.isFullScreen();
        }
        return false;
    }

    public IPlayerDimension getPlayerDimension() {
        return playerDimension;
    }

    public void setPlayerDimension(IPlayerDimension playerDimension) {
        this.playerDimension = playerDimension;
    }

    public void setFullScreen(boolean fullScreen) {
        if (playerDimension != null) {
            playerDimension.setFullScreen(fullScreen);
        }
    }

    public IPlayerVolume getPlayerVolume() {
        return playerVolume;
    }

    public void setPlayerVolume(IPlayerVolume playerVolume) {
        this.playerVolume = playerVolume;
    }

    public boolean isUsingHardwareDecoder() {
        return usingHardwareDecoder;
    }

    public boolean isUsingTextureViewRender() {
        return usingTextureViewRender;
    }

    public void setUsingTextureViewRender(boolean usingTextureViewRender) {
        this.usingTextureViewRender = usingTextureViewRender;
    }

    public boolean isAutoShowPlayerView() {
        return autoShowPlayerView;
    }

    public void setAutoShowPlayerView(boolean autoShowPlayerView) {
        this.autoShowPlayerView = autoShowPlayerView;
    }


    /**
     * 是否循环播放
     *
     * @return
     */
    public boolean isLoopPlay() {
        return loopPlay;
    }

    /**
     * 播放器是否可用
     *
     * @return
     */
    public boolean isEnabled() {
        return enabled;
    }

    public void release() {
        mContext = null;
    }


    /**
     * 构造器
     */
    public static class Builder {

        private static final int DEFAULT_LOOP_TIME = Integer.MAX_VALUE;

        private final Context context;
        private boolean autoPlayNext = true;
        private boolean autoPlay = true;
        //默认不循环播放
        private boolean loopPlay = false;
        private int loopPlayTime = DEFAULT_LOOP_TIME;

        private boolean autoShowPlayerView = true;

        private boolean isReadLocalDefinition = true;
        //默认不保存画面比例
        private boolean isAutoSaveAspectRatio = false;

        private boolean usingTextureViewRender = false;

        private boolean isDebug = false;

        private Definition mDefaultDefinition = Definition.HD;//默认高清

        private AspectRatio defaultAspectRatio = AspectRatio.AR_16_9_FIT_PARENT;//全屏

        private IPlayerVolume playerVolume;

        private IPlayerDimension playerDimension;

        private boolean usingHardwareDecoder = true;

        private boolean networkConnectedAutoPlay = true;

        //播放器是否可用
        private boolean enabled = true;

        //默认支持可改变大小
        private boolean enableChangeDimension = true;

        private boolean useOriginPlayerDimension = false;

        public Builder(Context context) {
            this.context = Preconditions.checkNotNull(context);
        }

        public Builder setDebug(boolean debug) {
            isDebug = debug;
            return this;
        }

        public Builder setAutoPlay(boolean autoPlay) {
            this.autoPlay = autoPlay;
            return this;
        }

        public Builder setAutoPlayNext(boolean autoPlayNext) {
            this.autoPlayNext = autoPlayNext;
            return this;
        }

        public Builder setLoopPlay(boolean loopPlay) {
            this.loopPlay = loopPlay;
            return this;
        }

        public Builder setLoopPlayTime(int loopPlayTime) {
            this.loopPlayTime = loopPlayTime;
            return this;
        }

        public Builder setAutoShowPlayerView(boolean autoShowPlayerView) {
            this.autoShowPlayerView = autoShowPlayerView;
            return this;
        }

        public Builder setPlayerDimension(IPlayerDimension playerDimension) {
            this.playerDimension = playerDimension;
            return this;
        }

        public Builder setPlayerVolume(IPlayerVolume playerVolume) {
            this.playerVolume = playerVolume;
            return this;
        }

        public Builder setNetworkConnectedAutoPlay(boolean networkConnectedAutoPlay) {
            this.networkConnectedAutoPlay = networkConnectedAutoPlay;
            return this;
        }

        public Builder setEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        /**
         * 设置是否可以支持改变播放器尺寸
         *
         * @param enableChangeDimension
         * @return
         */
        public Builder setEnableChangeDimension(boolean enableChangeDimension) {
            this.enableChangeDimension = enableChangeDimension;
            return this;
        }

        public Builder setUseOriginPlayerDimension(boolean useOriginPlayerDimension) {
            this.useOriginPlayerDimension = useOriginPlayerDimension;
            return this;
        }

        /**
         * 设置默认的清晰度
         *
         * @param defaultDefinition
         * @return
         */
        public Builder setDefaultDefinition(Definition defaultDefinition) {
            mDefaultDefinition = defaultDefinition;
            return this;
        }

        /**
         * 设置默认的屏幕比例
         *
         * @param defaultAspectRatio
         */
        public Builder setDefaultAspectRatio(AspectRatio defaultAspectRatio) {
            this.defaultAspectRatio = defaultAspectRatio;
            return this;
        }

        /**
         * 是否按本地清晰度进行选择播放清晰度
         * 如果设为true mDefaultDefinition将不会起作用
         * 如果设为false mDefaultDefinition 将为基准清晰度
         *
         * @param readLocalDefinition
         * @return
         */
        public Builder setReadLocalDefinition(boolean readLocalDefinition) {
            isReadLocalDefinition = readLocalDefinition;
            return this;
        }

        /**
         * 是否保存用户手动切换的屏幕比例
         *
         * @param isAutoSaveAspectRatio
         * @return
         */
        public Builder setAutoSaveAspectRatio(boolean isAutoSaveAspectRatio) {
            this.isAutoSaveAspectRatio = isAutoSaveAspectRatio;
            return this;
        }

        /**
         * 是否使用硬解码
         *
         * @param usingHardwareDecoder
         * @return
         */
        public Builder setUsingHardwareDecoder(boolean usingHardwareDecoder) {
            this.usingHardwareDecoder = usingHardwareDecoder;
            return this;
        }

        /**
         * 是否启用TextureView渲染播放器
         *
         * @param usingTextureViewRender
         * @return
         */
        public Builder usingTextureViewRender(boolean usingTextureViewRender) {
            this.usingTextureViewRender = usingTextureViewRender;
            return this;
        }

        public PlayerConfiguration build() {
            //设置播放器的默认尺寸
            if (playerDimension == null) {
                playerDimension = new PlayerDimensionModel.Builder(context)
                        .build();
            }

            //音量
            if (playerVolume == null) {
                playerVolume = new PlayerVolumeModel.Builder()
                        .build();
            }
            return new PlayerConfiguration(this);
        }
    }

    @Override
    public String toString() {
        return "PlayerConfiguration{" +
                "mContext=" + mContext +
                ", autoPlayVideo=" + autoPlayVideo +
                ", autoPlayNext=" + autoPlayNext +
                ", loopPlay=" + loopPlay +
                ", loopPlayTime=" + loopPlayTime +
                ", autoShowPlayerView=" + autoShowPlayerView +
                ", defaultDefinition=" + defaultDefinition +
                ", isReadLocalDefinition=" + isReadLocalDefinition +
                ", defaultAspectRatio=" + defaultAspectRatio +
                ", isAutoSaveAspectRatio=" + isAutoSaveAspectRatio +
                ", isDebug=" + isDebug +
                ", playerDimension=" + playerDimension +
                ", playerVolume=" + playerVolume +
                ", usingHardwareDecoder=" + usingHardwareDecoder +
                ", usingTextureViewRender=" + usingTextureViewRender +
                ", networkConnectedAutoPlay=" + networkConnectedAutoPlay +
                ", enabled=" + enabled +
                ", enableChangeDimension=" + enableChangeDimension +
                ", useOriginPlayerDimension=" + useOriginPlayerDimension +
                '}';
    }
}
