package com.quicktvui.support.player.audio.android;

 interface IPlayerCallback {

    void onPlayerStatusChanged(int playerStatus);
    void onPlayerInfo(int code, String message);
    void onPlayerError(int what, int extra);
}
