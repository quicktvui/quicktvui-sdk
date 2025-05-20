package com.quicktvui.sdk.core.internal.loader;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.File;

import com.quicktvui.sdk.core.EsData;

/**
 * <br>
 * 调式地址加载
 * <br>
 * <br>
 *
 * @Created by WeiPeng on 2024-03-28 11:12
 */
public class DebugRpkLoader extends AbstractLoader {

    public DebugRpkLoader(@NonNull EsData app) {
        super(app);

        // fix debug server
        String debugServer = app.getAppDownloadUrl();
        if(debugServer.startsWith("http")){
            debugServer = debugServer.replaceAll("http://", "");
            app.setAppDownloadUrl(debugServer);
        }
    }

    @Nullable
    @Override
    protected File getFromLocalCache() throws Exception {
        return null;
    }

    @Nullable
    @Override
    public File getFromServer() throws Exception {
        return null;
    }

}
