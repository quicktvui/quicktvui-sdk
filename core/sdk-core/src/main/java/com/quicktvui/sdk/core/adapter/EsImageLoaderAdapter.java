package com.quicktvui.sdk.core.adapter;

import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;

import com.quicktvui.sdk.base.EsCallback;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.image.IEsImageLoader;
import com.quicktvui.sdk.core.internal.AbsEsImageLoader;
import com.quicktvui.sdk.core.internal.EsContext;
import com.quicktvui.sdk.core.internal.EsViewManager;
import com.quicktvui.sdk.core.utils.MapperUtils;
import com.sunrain.toolkit.utils.log.L;
import com.tencent.mtt.hippy.adapter.image.HippyDrawable;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.dom.node.NodeProps;

import java.io.File;
import java.util.Map;

/**
 * Create by weipeng on 2022/03/01 17:33
 */
public class EsImageLoaderAdapter extends AbsEsImageLoader {

    private IEsImageLoader mLoaderProxy;

    private int mEngineId = -1;
    private boolean mIsDebug;
    private String mDebugServer;

    public EsImageLoaderAdapter(IEsImageLoader imageLoader) {
        super();
        try {
            mLoaderProxy = imageLoader;
        } catch (Exception e) {
            L.logW("image loader", e);
        }
    }

    public EsImageLoaderAdapter setEngineId(int engineId, boolean isDebug, String debugServer) {
        this.mEngineId = engineId;
        this.mIsDebug = isDebug;
        this.mDebugServer = debugServer;
        return this;
    }

    private EsMap createFetchParams(String url, Object param) {
        EsMap map = new EsMap();
//        map.pushString("url", fixImageUrl(url));
        if (param != null) {
            if (param instanceof Map) {
                Object data = ((Map<?, ?>) param).get(NodeProps.PROPS);
                if(data instanceof HippyMap){
                    HippyMap props = (HippyMap) data;
                    for (String K : props.keySet()) {
                        Object V = props.get(K);
                        if (V instanceof HippyMap) {
                            map.pushAll(MapperUtils.hpMap2EsMap((HippyMap) V));
                        } else if (V instanceof HippyArray) {
                            map.pushArray(K, MapperUtils.hpArray2EsArray((HippyArray) V));
                        } else {
                            map.pushObject(K, V);
                        }
                    }
                }
            }else if(param instanceof EsMap){
                map.pushAll((EsMap) param);
            }else{
                L.logEF("组合参数错误");
            }
        }
        map.pushString("url", fixImageUrl(url));
        return map;
    }

    @Override
    public void fetchImage(String url, Callback requestCallback, Object param) {
        if (mLoaderProxy == null) return;
        if (TextUtils.isEmpty(url)) return;
        EsMap fetchParams = createFetchParams(url, param);
        mLoaderProxy.loadImage(EsContext.get().getContext(), fetchParams,
                new EsCallback<Object, Exception>() {
                    final HippyDrawable target = new HippyDrawable();

                    @Override
                    public void onSuccess(Object result) {
                        if (result instanceof BitmapDrawable) {
                            target.setData(((BitmapDrawable) result).getBitmap());
                        } else if (result instanceof byte[]) {
                            target.setData((byte[]) result);
                        }
                        requestCallback.onRequestSuccess(target);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        L.logEF("load img failed:" + e.getMessage());
                        requestCallback.onRequestFail(e, null);
                    }
                }
        );
    }

    @Override
    public void destroyIfNeed() {
        super.destroyIfNeed();
        if (mLoaderProxy != null) mLoaderProxy.destroy(EsContext.get().getContext());
    }

    private String fixImageUrl(String url) {
        if (mIsDebug) {
            if (url.startsWith("file://") || url.startsWith("http://127.0.0.1")) {
                int index = url.indexOf("assets");
                if (index >= 0) {
                    String debugServer = mDebugServer;
                    if(!TextUtils.isEmpty(debugServer)){
                        if(debugServer.startsWith("http")){
                            return debugServer + "/" + url.substring(index);
                        }else return "http://" + debugServer + "/" + url.substring(index);
                    }
                }
            }
        } else if (url.startsWith("file://")) {
            int index = url.indexOf("assets");
            if (index >= 0) {
                File cacheDir = EsViewManager.get().getAppRuntimeDir(mEngineId);
                if (cacheDir != null) {
                    String localFilePath = "file://" + cacheDir.getAbsolutePath() + "/" + url.substring(index);
                    if (cacheDir.getPath().startsWith("/")) return localFilePath;
                    return localFilePath.replace("file://", "file:///android_asset");
                }else{
                    L.logEF("image loader fix failed, engine:" + mEngineId);
                }

            }
        }
        return url.replaceAll(" ", "%20");
    }
}
