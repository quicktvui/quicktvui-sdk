package com.quicktvui.sdk.core.internal.loader;

import static com.quicktvui.sdk.core.internal.loader.IRpkLoader.PREFIX_ASSETS;
import static com.quicktvui.sdk.core.internal.loader.IRpkLoader.PREFIX_FILE;
import static com.quicktvui.sdk.core.internal.loader.IRpkLoader.PREFIX_HTTP;
import static com.quicktvui.sdk.core.internal.loader.IRpkLoader.SUFFIX_HTTP;
import static com.quicktvui.sdk.core.utils.CommonUtils.getRepositoryHost;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.InitConfig;
import com.quicktvui.sdk.base.EsException;
import com.quicktvui.sdk.core.adapter.DefaultRpkLoaderAdapter;

/**
 * <br>
 * RPK Loader Factory
 * <br>
 * <br>
 *
 * @Created by WeiPeng on 2024-03-27 16:24
 */
public final class RpkLoaderFactory {

    @NonNull
    public static IRpkLoader createLoader(EsData data) {

        DefaultRpkLoaderAdapter adapter = InitConfig.getDefault().getRpkLoaderAdapter();

        String uri = data.getAppDownloadUrl();
        if (!TextUtils.isEmpty(uri)) {
            if (uri.startsWith(PREFIX_FILE)) {
                return adapter.createFileRpkLoader(data);
            }

            if (uri.startsWith(PREFIX_ASSETS)) {
                return adapter.createAssetsRpkLoader(data);
            }

            if (uri.endsWith(SUFFIX_HTTP)) {
                return adapter.createDebugRpkLoader(data);
            }

            if (uri.startsWith(PREFIX_HTTP)) {
                return adapter.createHttpRpkLoader(data);
            }
        } else {
            String repository = getRepositoryHost(data);
            if (!TextUtils.isEmpty(repository)) {
                return adapter.createNexusRpkLoader(data);
            }
            return adapter.createApiRpkLoader(data);
        }

        throw new EsException(-1, "loader not found");
    }

}
