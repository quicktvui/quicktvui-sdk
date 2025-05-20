package com.quicktvui.support.imageloader;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.EsHttpUrlConnectionFactory;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.load.model.stream.HttpGlideUrlLoader;
import com.sunrain.toolkit.utils.ReflectUtils;

import java.io.InputStream;

public class EsHttpGlideUrlLoader extends HttpGlideUrlLoader {

    public EsHttpGlideUrlLoader() {
    }

    public EsHttpGlideUrlLoader(@Nullable ModelCache<GlideUrl, GlideUrl> modelCache) {
        super(modelCache);
    }

    @Override
    public LoadData<InputStream> buildLoadData(@NonNull GlideUrl model, int width, int height, @NonNull Options options) {
        LoadData<InputStream> loadData = super.buildLoadData(model, width, height, options);
        if(loadData == null) return null;
        String proxyHostName = "";
        int proxyPort = 0;
        try {
            ReflectUtils.reflect(loadData.fetcher).field("connectionFactory", new EsHttpUrlConnectionFactory(proxyHostName, proxyPort));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loadData;
    }

    public static class Factory implements ModelLoaderFactory<GlideUrl, InputStream> {
        private final ModelCache<GlideUrl, GlideUrl> modelCache = new ModelCache<>(500);

        @NonNull
        @Override
        public ModelLoader<GlideUrl, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new EsHttpGlideUrlLoader(modelCache);
        }

        @Override
        public void teardown() {
            // Do nothing.
        }
    }
}
