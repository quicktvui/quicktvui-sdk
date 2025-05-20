package com.quicktvui.support.player.audio.soundpool;

/**
 *
 */
public class Sound {

    private String url;
    private int soundId;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getSoundId() {
        return soundId;
    }

    public void setSoundId(int soundId) {
        this.soundId = soundId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Sound)) return false;

        Sound sound = (Sound) o;

        if (getSoundId() != sound.getSoundId()) return false;
        return getUrl() != null ? getUrl().equals(sound.getUrl()) : sound.getUrl() == null;
    }

    @Override
    public int hashCode() {
        int result = getUrl() != null ? getUrl().hashCode() : 0;
        result = 31 * result + getSoundId();
        return result;
    }

    @Override
    public String toString() {
        return "SoundBean{" +
                "url='" + url + '\'' +
                ", soundId=" + soundId +
                '}';
    }
}
