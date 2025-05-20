package com.quicktvui.support.ui.legacy.misc;

import android.app.Application;
import android.media.AudioManager;
import android.media.SoundPool;
import android.provider.Settings;

import com.quicktvui.support.ui.legacy.R;



public class TVSoundEffect {
    public static Application mContext;
    public static int SOUND_CANT_MOVE = -1;
    public static SoundPool mSoundPool;

    public static void init(Application application) {
        mContext = application;
        mSoundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        new Thread(new Runnable() {
            @Override
            public void run() {
                SOUND_CANT_MOVE = mSoundPool.load(mContext, R.raw.cant_move, 1);
            }
        }).start();
    }

    public static void playCantMove() {
        assert mContext != null;
        if (SOUND_CANT_MOVE > 0 && "1".equals(Settings.System.getString(mContext.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED))) {
            mSoundPool.play(SOUND_CANT_MOVE, 1, 1, 1, 0, 1);
        }
    }
}
