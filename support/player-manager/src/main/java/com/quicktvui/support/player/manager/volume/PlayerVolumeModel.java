package com.quicktvui.support.player.manager.volume;

/**
 * 声音控制
 */
public class PlayerVolumeModel implements IPlayerVolume {

    private float leftVolume;
    private float rightVolume;

    public PlayerVolumeModel(Builder builder) {
        leftVolume = builder.leftVolume;
        rightVolume = builder.rightVolume;
    }

    @Override
    public float getLeftVolume() {
        return leftVolume;
    }

    @Override
    public float getRightVolume() {
        return rightVolume;
    }

    @Override
    public void setLeftVolume(float leftVolume) {
        this.leftVolume = leftVolume;
    }

    @Override
    public void setRightVolume(float rightVolume) {
        this.rightVolume = rightVolume;
    }

    public static class Builder {

        public static final int MEDIA_PLAYER_MAX_VOLUME = 1;
        public static final int MEDIA_PLAYER_VOLUME_UN_SUPPORT = -1;

        private float leftVolume = MEDIA_PLAYER_MAX_VOLUME;
        private float rightVolume = MEDIA_PLAYER_MAX_VOLUME;

        public Builder() {
        }

        public Builder setLeftVolume(float leftVolume) {
            this.leftVolume = leftVolume;
            return this;
        }

        public Builder setRightVolume(float rightVolume) {
            this.rightVolume = rightVolume;
            return this;
        }

        public PlayerVolumeModel build() {
            return new PlayerVolumeModel(this);
        }
    }

    @Override
    public String toString() {
        return "VolumeModel{" +
                "leftVolume=" + leftVolume +
                ", rightVolume=" + rightVolume +
                '}';
    }
}
