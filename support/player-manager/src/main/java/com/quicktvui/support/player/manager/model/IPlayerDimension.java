package com.quicktvui.support.player.manager.model;

public interface IPlayerDimension {

    int getFullPlayerWidth();

    int getFullPlayerHeight();

    int getDefaultPlayerWidth();

    int getDefaultPlayerHeight();


    void setFullPlayerWidth(int fullPlayerWidth);

    void setFullPlayerHeight(int fullPlayerHeight);

    void setDefaultPlayerWidth(int defaultPlayerWidth);

    void setDefaultPlayerHeight(int defaultPlayerHeight);

    boolean isFullScreen();

    void setFullScreen(boolean fullScreen);
}
