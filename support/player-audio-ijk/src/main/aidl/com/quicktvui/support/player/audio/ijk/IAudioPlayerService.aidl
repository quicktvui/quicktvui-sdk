package com.quicktvui.support.player.audio.ijk;
import com.quicktvui.support.player.audio.ijk.IPlayerCallback;

interface IAudioPlayerService {

    void initAudioPlayer();
    void play(String url);
    void start();
    void pause();
    void resume();
    void seekTo(int msec);
    void stop();
    void reset();
    void release();
    boolean isPlaying();
    boolean isPaused();
    long getDuration();
    long getCurrentPosition();
    int getBufferPercentage();
    void setPlayRate(float speed);
    float getCurrentPlayRate();
    void setVolume(float volume);
    void setLeftRightVolume(float leftVolume, float rightVolume);
    float getLeftVolume();
    float getRightVolume();

    void registerPlayerCallback(IPlayerCallback callback);
    void unregisterPlayerCallback(IPlayerCallback callback);
}