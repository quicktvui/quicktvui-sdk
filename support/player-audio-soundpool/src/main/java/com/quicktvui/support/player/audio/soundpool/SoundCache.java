package com.quicktvui.support.player.audio.soundpool;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SoundCache {

    private List<Sound> loadSoundList;
    private int maxSize;
    //
    private boolean enabled = true;

    public void init(int maxSize) {
        if (!this.isEnabled()) {
            return;
        }
        this.maxSize = maxSize;
        this.loadSoundList = Collections.synchronizedList(new ArrayList<>());
    }

    public void put(Sound sound) {
        if (!this.isEnabled()) {
            return;
        }
        if (loadSoundList != null && !loadSoundList.contains(sound)) {
            //
            if (loadSoundList.size() >= maxSize) {
                loadSoundList.remove(0);
            }
            loadSoundList.add(sound);
        }
    }

    public boolean contains(Sound sound) {
        if (!this.isEnabled()) {
            return false;
        }
        return loadSoundList != null && loadSoundList.contains(sound);
    }

    public void remove(int soundId) {
        if (!this.isEnabled()) {
            return;
        }
        Sound sound = get(soundId);
        if (loadSoundList != null && sound != null) {
            loadSoundList.remove(sound);
        }
    }

    public void remove(Sound sound) {
        if (!this.isEnabled()) {
            return;
        }
        if (loadSoundList != null && sound != null) {
            loadSoundList.remove(sound);
        }
    }

    public Sound get(int soundId) {
        if (!this.isEnabled()) {
            return null;
        }
        if (loadSoundList != null) {
            int index = -1;
            for (int i = 0; i < loadSoundList.size(); i++) {
                Sound sound = loadSoundList.get(i);
                if (sound != null && sound.getSoundId() == soundId) {
                    index = i;
                }
            }
            if (index != -1) {
                return loadSoundList.get(index);
            }
        }
        return null;
    }

    public Sound get(String soundUrl) {
        if (!this.isEnabled()) {
            return null;
        }
        if (loadSoundList != null) {
            int index = -1;
            for (int i = 0; i < loadSoundList.size(); i++) {
                Sound sound = loadSoundList.get(i);
                if (sound != null && sound.getUrl().equals(soundUrl)) {
                    index = i;
                }
            }
            if (index != -1) {
                return loadSoundList.get(index);
            }
        }
        return null;
    }


    public void clear() {
        if (!this.isEnabled()) {
            return;
        }
        if (loadSoundList != null) {
            loadSoundList.clear();
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "SoundCache{" +
                "loadSoundList=" + loadSoundList +
                ", maxSize=" + maxSize +
                ", enabled=" + enabled +
                '}';
    }
}
