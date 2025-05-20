package com.quicktvui.support.player.audio.soundpool;

import android.media.AudioAttributes;
import android.media.SoundPool;

import com.quicktvui.sdk.base.EsPromise;
import com.sunrain.toolkit.utils.log.L;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class SoundPoolManager implements SoundPool.OnLoadCompleteListener {

    private static SoundPoolManager instance;
    private SoundPool soundPool;
    private SoundCache loadedSoundCache;
    private SoundCache soundCache;


    protected List<SoundPoolListener> listenerList =
            Collections.synchronizedList(new ArrayList<>());

    private SoundPoolManager() {
    }

    public static SoundPoolManager getInstance() {
        synchronized (SoundPoolManager.class) {
            if (instance == null) {
                instance = new SoundPoolManager();
            }
        }
        return instance;
    }

    @Override
    public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
        if (L.DEBUG) {
            L.logD("-----onLoadComplete----加载成功----->>>>>" + "\n" +
                    "soundPool:" + soundPool + "\n" +
                    "sampleId:" + sampleId + "\n" +
                    "status:" + status + "\n"
            );
        }
        if (soundCache != null) {
            Sound sound = soundCache.get(sampleId);
            soundCache.remove(sound);

            if (loadedSoundCache != null) {
                loadedSoundCache.put(sound);
            }

            if (L.DEBUG) {
                L.logD("-----onLoadComplete----加载成功---更新缓存------>>>>>" + "\n" +
                        "sound:" + sound + "\n" +
                        "soundCache:" + soundCache + "\n" +
                        "loadedSoundCache:" + loadedSoundCache + "\n"
                );
            }
        }
        notifySoundPoolLoadCompleted(sampleId, status);
    }

    /**
     *
     */
    public void initSoundPool(int usage, int contentType,
                              int maxStreams, int streamType, boolean enableCache) {
        if (soundPool != null) {
            return;
        }

        if (L.DEBUG) {
            L.logD("----未初始化过------initSoundPool--------->>>>>" + "\n" +
                    "usage:" + usage + "\n" +
                    "contentType:" + contentType + "\n" +
                    "maxStreams:" + maxStreams + "\n" +
                    "streamType:" + streamType + "\n"
            );
        }

        //
        loadedSoundCache = new SoundCache();
        loadedSoundCache.setEnabled(enableCache);
        loadedSoundCache.init(maxStreams);
        //
        soundCache = new SoundCache();
        soundCache.setEnabled(enableCache);
        soundCache.init(maxStreams);
        try {
            // 5.0之后
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

                if (L.DEBUG) {
                    L.logD("------5.0之后-----initSoundPool--------->>>>>" + "\n" +
                            "usage:" + usage + "\n" +
                            "contentType:" + contentType + "\n" +
                            "maxStreams:" + maxStreams + "\n" +
                            "streamType:" + streamType + "\n"
                    );
                }
                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setUsage(usage)
                        .setContentType(contentType)
                        .build();
                soundPool = new SoundPool.Builder()
                        .setMaxStreams(maxStreams)
                        .setAudioAttributes(audioAttributes)
                        .build();
            }
            //5.0以前
            else {
                if (L.DEBUG) {
                    L.logD("------5.0以前-----initSoundPool--------->>>>>" + "\n" +
                            "maxStreams:" + maxStreams + "\n" +
                            "streamType:" + streamType + "\n"
                    );
                }
                // 创建SoundPool
                soundPool = new SoundPool(maxStreams, streamType, 0);
            }
            //
            soundPool.setOnLoadCompleteListener(this);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    /**
     * 加载
     *
     * @param url
     */
    public int load(String url) {
        if (L.DEBUG) {
            L.logD("---------load-----开始---->>>>>" +
                    "url" + url + "----"
            );
        }
        if (loadedSoundCache != null) {
            Sound loadedSong = loadedSoundCache.get(url);
            if (loadedSong != null) {
                if (L.DEBUG) {
                    L.logD("---------load----已经加载过了，直接返回--------->>>>>" +
                            "loadedSong:" + loadedSong
                    );
                }
                notifySoundPoolLoadCompleted(loadedSong.getSoundId(), 0);
                return loadedSong.getSoundId();
            }
        }

        if (soundPool != null) {
            int soundId = soundPool.load(url, 1);
            if (soundId == 0) {
                if (L.DEBUG) {
                    L.logD("---------load----加载失败----->>>>>" +
                            "url" + url + "----" +
                            "soundId" + soundId
                    );
                }
                notifySoundPoolLoadError(url);
            }
            //
            else {
                Sound sound = new Sound();
                sound.setSoundId(soundId);
                sound.setUrl(url);
                if (!soundCache.contains(sound)) {
                    soundCache.put(sound);
                }
                if (L.DEBUG) {
                    L.logD("---------load--结束--放入加载缓存--------->>>>>" +
                            "sound:" + sound + "\n" +
                            "soundCache:" + soundCache + "\n" +
                            "loadedSoundCache:" + loadedSoundCache + "\n"
                    );
                }
            }
            if (L.DEBUG) {
                L.logD("-----2------load--------->>>>>" +
                        "url" + url + "----" +
                        "soundId" + soundId
                );
            }
            return soundId;
        }
        return 0;
    }


    public void load(String url, EsPromise promise) {
        if (loadedSoundCache != null) {
            Sound loadedSong = loadedSoundCache.get(url);
            if (loadedSong != null) {
                if (L.DEBUG) {
                    L.logD("---------load----已经加载过了，直接返回--------->>>>>" + "\n" +
                            "url:" + url + "\n" +
                            "soundId:" + loadedSong.getSoundId() + "\n" +
                            "loadedSong:" + loadedSong + "\n"
                    );
                }
                //1.
                int soundId = loadedSong.getSoundId();
                if (promise != null) {
                    promise.resolve(soundId);
                }
                //2.
                notifySoundPoolLoadCompleted(soundId, 0);
                return;
            }
        }

        if (soundPool != null) {
            int soundId = soundPool.load(url, 1);
            if (soundId == 0) {
                if (L.DEBUG) {
                    L.logD("---------load----加载失败----->>>>>" + "\n" +
                            "url:" + url + "\n" +
                            "soundId:" + soundId + "\n"
                    );
                }
                //1.
                if (promise != null) {
                    promise.resolve(soundId);
                }
                //2.
                notifySoundPoolLoadError(url);
            }
            //
            else {
                Sound sound = new Sound();
                sound.setSoundId(soundId);
                sound.setUrl(url);
                if (!soundCache.contains(sound)) {
                    soundCache.put(sound);
                }
                if (L.DEBUG) {
                    L.logD("---------load--结束--放入加载缓存--------->>>>>" + "\n" +
                            "url:" + url + "\n" +
                            "soundId:" + soundId + "\n" +
                            "sound:" + sound + "\n" +
                            "soundCache:" + soundCache + "\n" +
                            "loadedSoundCache:" + loadedSoundCache + "\n"
                    );
                }
                //
                if (promise != null) {
                    promise.resolve(soundId);
                }
            }
        }
    }

    /**
     *
     */
    public void unload(int soundID) {
        try {
            if (soundPool != null) {
                if (L.DEBUG) {
                    L.logD("----------unload------开始--->>>>>" +
                            "soundID:" + soundID
                    );
                }
                soundPool.unload(soundID);
            }
            if (soundCache != null) {
                soundCache.remove(soundID);
            }

            if (loadedSoundCache != null) {
                loadedSoundCache.remove(soundID);
            }
            if (L.DEBUG) {
                L.logD("----------unload------结束--->>>>>" +
                        "soundID:" + soundID + "\n" +
                        "soundCache:" + soundCache + "\n" +
                        "loadedSoundCache:" + loadedSoundCache + "\n"
                );
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放
     */
    public int play(int soundID, float leftVolume, float rightVolume,
                    int priority, int loop, float rate) {
        try {
            if (soundPool != null) {
                int streamID = soundPool.play(soundID, leftVolume, rightVolume, priority, loop, rate);
                if (L.DEBUG) {
                    L.logD("----------play--------->>>>>" + "\n" +
                            "soundID:" + soundID + "\n" +
                            "leftVolume:" + leftVolume + "\n" +
                            "rightVolume:" + rightVolume + "\n" +
                            "priority:" + priority + "\n" +
                            "loop:" + loop + "\n" +
                            "rate:" + rate
                    );
                }
                return streamID;
            }
            //
            else {
                if (L.DEBUG) {
                    L.logD("----------play--------->>>>>" + "\n" +
                            "soundPool is null"
                    );
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 暂停
     */
    public void pause(int streamID) {
        try {
            if (soundPool != null) {
                if (L.DEBUG) {
                    L.logD("----------pause--------->>>>>" + "\n" +
                            "streamID:" + streamID
                    );
                }
                soundPool.pause(streamID);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void autoPause() {
        try {
            if (soundPool != null) {
                if (L.DEBUG) {
                    L.logD("----------autoPause--------->>>>>"
                    );
                }
                soundPool.autoPause();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 重新开始
     */
    public void resume(int streamID) {
        try {
            if (soundPool != null) {
                if (L.DEBUG) {
                    L.logD("----------resume--------->>>>>" +
                            "streamID:" + streamID
                    );
                }
                soundPool.resume(streamID);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void autoResume() {
        try {
            if (soundPool != null) {
                if (L.DEBUG) {
                    L.logD("----------autoResume--------->>>>>"
                    );
                }
                soundPool.autoResume();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 停止
     */
    public void stop(int streamID) {
        try {
            if (soundPool != null) {
                if (L.DEBUG) {
                    L.logD("----------stop--------->>>>>" +
                            "streamID:" + streamID
                    );
                }
                soundPool.stop(streamID);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    /**
     * @param streamID
     * @param leftVolume
     * @param rightVolume
     */
    public void setVolume(int streamID, float leftVolume, float rightVolume) {
        try {
            if (soundPool != null) {
                if (L.DEBUG) {
                    L.logD("----------setVolume--------->>>>>" +
                            "streamID:" + streamID +
                            "leftVolume:" + leftVolume +
                            "rightVolume:" + rightVolume
                    );
                }
                soundPool.setVolume(streamID, leftVolume, rightVolume);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * @param streamID
     * @param rate
     */
    public void setRate(int streamID, float rate) {
        try {
            if (soundPool != null) {
                if (L.DEBUG) {
                    L.logD("----------setRate--------->>>>>" +
                            "streamID:" + streamID +
                            "rate:" + rate
                    );
                }
                soundPool.setRate(streamID, rate);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * @param streamID
     * @param priority
     */
    public void setPriority(int streamID, int priority) {
        try {
            if (soundPool != null) {
                if (L.DEBUG) {
                    L.logD("----------setPriority--------->>>>>" +
                            "streamID:" + streamID +
                            "priority:" + priority
                    );
                }
                soundPool.setPriority(streamID, priority);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * @param streamID
     * @param loop
     */
    public void setLoop(int streamID, int loop) {
        try {
            if (soundPool != null) {
                if (L.DEBUG) {
                    L.logD("----------setLoop--------->>>>>" +
                            "streamID:" + streamID +
                            "loop:" + loop
                    );
                }
                soundPool.setLoop(streamID, loop);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     *
     */
    public void release() {
        try {
            if (soundPool != null) {
                if (L.DEBUG) {
                    L.logD("----------release--------->>>>>"
                    );
                }
                soundPool.release();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        try {
            if (soundCache != null) {
                soundCache.clear();
            }
            if (loadedSoundCache != null) {
                loadedSoundCache.clear();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    public interface SoundPoolListener {
        void onSoundLoadComplete(int sampleId, int status);

        void onSoundLoadError(String url);

    }

    public void registerSoundPoolListener(SoundPoolListener listener) {
        if (listener != null && !listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    public void unregisterSoundPoolListener(SoundPoolListener listener) {
        if (listener != null) {
            listenerList.remove(listener);
        }
    }

    public void notifySoundPoolLoadCompleted(int sampleId, int status) {
        if (listenerList != null && listenerList.size() > 0) {
            for (SoundPoolListener listener : listenerList) {
                try {
                    listener.onSoundLoadComplete(sampleId, status);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void notifySoundPoolLoadError(String url) {
        if (listenerList != null && listenerList.size() > 0) {
            for (SoundPoolListener listener : listenerList) {
                try {
                    listener.onSoundLoadError(url);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
