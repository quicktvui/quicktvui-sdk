
package com.quicktvui.support.player.audio.utils;


public class Settings {
    public static final int PV_PLAYER__AndroidMediaPlayer = 1;
    public static final int PV_PLAYER__IjkMediaPlayer = 2;

    private int playerType = PV_PLAYER__IjkMediaPlayer;
    public void setPlayerType(int type) {
        playerType = type;
    }
    public int getPlayerType() {
        return playerType;
    }

    public boolean getUsingOpenSLES() {
        return false;
    }

    public boolean getEnableDetachedSurfaceTextureView() {
        return false;
    }

    public boolean getUsingMediaDataSource() {
        return false;
    }
}
