package com.quicktvui.support.player.manager.model;

import com.quicktvui.support.player.manager.definition.Definition;
import com.quicktvui.support.player.manager.utils.Preconditions;

/**
 * 播放地址
 */
public class VideoUrlModel implements IVideoUrl {

    private final String id;
    private String url;
    private final long duration;
    private final Object extra;
    private final Definition definition;

    public VideoUrlModel(Builder builder) {
        id = builder.id;
        url = builder.url;
        extra = builder.extra;
        duration = builder.duration;
        definition = builder.definition;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Definition getDefinition() {
        return definition;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public Object getExtra() {
        return extra;
    }

    @Override
    public long getDuration() {
        return duration;
    }

    public static class Builder {
        private String id;
        private String url;
        private long duration = -1;
        private Object extra;
        private Definition definition;

        public Builder() {
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setVideoUrl(String url) {
            this.url = Preconditions.checkNotNull(url);
            return this;
        }

        public Builder setExtra(Object extra) {
            this.extra = extra;
            return this;
        }

        public Builder setDuration(long duration) {
            this.duration = duration;
            return this;
        }

        public Builder setDefinition(Definition definition) {
            this.definition = definition;
            return this;
        }


        public VideoUrlModel build() {
            /**
             * 设置默认清晰度
             */
            if (definition == null) {
                definition = Definition.SD;
            }

            return new VideoUrlModel(this);
        }
    }

    @Override
    public String toString() {
        return "VideoUrlModel{" +
                "id='" + id + '\'' +
                ", url='" + url + '\'' +
                ", duration=" + duration +
                ", extra=" + extra +
                ", definition=" + definition +
                '}';
    }
}
