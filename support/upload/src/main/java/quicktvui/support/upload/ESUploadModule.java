package quicktvui.support.upload;

import android.content.Context;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;
import com.sunrain.toolkit.utils.log.L;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 文件上传
 */
@ESKitAutoRegister
public class ESUploadModule implements IEsModule, IEsInfo {

    private UploadManager uploadManager;

    private Call uploadCall = null;

    @Override
    public void init(Context context) {
        uploadManager = new UploadManager();
    }

    public void upload(String url,//
                       String mediaType,//
                       String filePramsKey,//
                       String filePath,//
                       EsMap esParams,//
                       EsMap headerParams) {
        if (uploadManager == null) {
            if (L.DEBUG) {
                L.logD("----uploadManager---is null------->>>>");
            }
            return;
        }

        if (L.DEBUG) {
            L.logD("----ESUploadModule---upload------->>>>"
                    + "url:" + url + "----"
                    + "mediaType:" + mediaType + "----"
                    + "filePramsKey:" + filePramsKey + "----"
                    + "filePath:" + filePath + "----"
                    + "esParams:" + esParams + "----"
            );
        }

        //start
        EsProxy.get().sendNativeEventTraceable(
                ESUploadModule.this,
                Events.EVENT_ON_UPLOAD_START.toString(),
                esParams);

        Map<String, String> params = new HashMap<>();
        try {
            if (esParams != null && esParams.size() > 0) {
                for (String key : esParams.keySet()) {
                    try {
                        String value = esParams.getString(key);
                        params.put(key, value);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        String rootPath = EsProxy.get().getEsAppPath(this);
        String miniProgramFilePath = rootPath + File.separator + filePath;
        try {
            uploadCall = uploadManager.upload(url, mediaType, filePramsKey, miniProgramFilePath, params, getParams(headerParams));
            uploadCall.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        if (response != null && response.body() != null) {
                            String result = response.body().string();
                            if (L.DEBUG) {
                                L.logD("----ESUploadModule---onResponse------->>>>" + "result:" + result);
                            }
                            //success
                            esParams.pushString("response", result);
                            EsProxy.get().sendNativeEventTraceable(
                                    ESUploadModule.this,
                                    Events.EVENT_ON_UPLOAD_SUCCESS.toString(),
                                    esParams);
                        }
                        //
                        else {
                            if (L.DEBUG) {
                                L.logD("----ESUploadModule---onResponse---null---->>>>");
                            }
                            notifyUploadError(esParams);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        if (L.DEBUG) {
                            L.logD("----ESUploadModule---onResponse---error---->>>>");
                        }
                        notifyUploadError(esParams);
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    if (L.DEBUG) {
                        L.logD("----ESUploadModule---onResponse---onFailure---->>>>");
                    }
                    notifyUploadError(esParams);
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    public void uploadES(String url,
                         String mediaType,
                         String filePramsKey,
                         String filePath,
                         EsMap esParams,
                         EsMap headerParams) {
        if (uploadManager == null) {
            if (L.DEBUG) {
                L.logD("----uploadManager---is null------->>>>");
            }
            return;
        }

        if (L.DEBUG) {
            L.logD("----ESUploadModule---upload------->>>>"
                    + "url:" + url + "----"
                    + "mediaType:" + mediaType + "----"
                    + "filePramsKey:" + filePramsKey + "----"
                    + "filePath:" + filePath + "----"
                    + "esParams:" + esParams + "----"
            );
        }

        //start
        EsProxy.get().sendNativeEventTraceable(
                ESUploadModule.this,
                Events.EVENT_ON_UPLOAD_START.toString(),
                esParams);

        Map<String, String> params = new HashMap<>();
        try {
            if (esParams != null && esParams.size() > 0) {
                for (String key : esParams.keySet()) {
                    try {
                        String value = esParams.getString(key);
                        params.put(key, value);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            uploadCall = uploadManager.upload(url, mediaType, filePramsKey, filePath, params, getParams(headerParams));
            uploadCall.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    try {
                        if (response != null && response.body() != null) {
                            String result = response.body().string();
                            if (L.DEBUG) {
                                L.logD("----ESUploadModule---onResponse------->>>>" + "result:" + result);
                            }
                            //success
                            esParams.pushString("response", result);
                            EsProxy.get().sendNativeEventTraceable(
                                    ESUploadModule.this,
                                    Events.EVENT_ON_UPLOAD_SUCCESS.toString(),
                                    esParams);
                        }
                        //
                        else {
                            if (L.DEBUG) {
                                L.logD("----ESUploadModule---onResponse---null---->>>>");
                            }
                            notifyUploadError(esParams);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        if (L.DEBUG) {
                            L.logD("----ESUploadModule---onResponse---error---->>>>");
                        }
                        notifyUploadError(esParams);
                    }
                }

                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                    if (L.DEBUG) {
                        L.logD("----ESUploadModule---onResponse---onFailure---->>>>");
                    }
                    notifyUploadError(esParams);
                }
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private Map<String, String> getParams(EsMap esParams) {
        Map<String, String> params = new HashMap<>();
        if (esParams != null && esParams.size() > 0) {
            for (String key : esParams.keySet()) {
                try {
                    String value = esParams.getString(key);
                    params.put(key, value);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
        return params;
    }

    private void notifyUploadError(EsMap esParams) {
        if (L.DEBUG) {
            L.logD("----ESUploadModule---notifyUploadError------>>>>" + esParams);
        }
        EsProxy.get().sendNativeEventTraceable(
                ESUploadModule.this,
                Events.EVENT_ON_UPLOAD_ERROR.toString(),
                esParams);
    }

    public enum Events {
        EVENT_ON_UPLOAD_START("onESUploadStart"),
        EVENT_ON_UPLOAD_SUCCESS("onESUploadSuccess"),
        EVENT_ON_UPLOAD_ERROR("onESUploadError");

        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    /**
     * 停止
     */
    public void stop() {
        try {
            if (uploadCall != null) {
                uploadCall.cancel();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消
     */
    public void cancel() {
        this.stop();
    }

    @Override
    public void getEsInfo(EsPromise promise) {
        EsMap map = new EsMap();
        /*try {
            map.pushInt(IEsInfo.ES_PROP_INFO_VERSION, BuildConfig.ES_KIT_BUILD_TAG_COUNT);
            map.pushString(IEsInfo.ES_PROP_INFO_PACKAGE_NAME, BuildConfig.LIBRARY_PACKAGE_NAME);
            map.pushString(IEsInfo.ES_PROP_INFO_CHANNEL, BuildConfig.ES_KIT_BUILD_TAG_CHANNEL);
            map.pushString(IEsInfo.ES_PROP_INFO_BRANCH, BuildConfig.ES_KIT_BUILD_TAG);
            map.pushString(IEsInfo.ES_PROP_INFO_COMMIT_ID, BuildConfig.ES_KIT_BUILD_TAG_ID);
            map.pushString(IEsInfo.ES_PROP_INFO_RELEASE_TIME, BuildConfig.ES_KIT_BUILD_TAG_TIME);
        } catch (Throwable e) {
            e.printStackTrace();
        }*/
        promise.resolve(map);
    }

    @Override
    public void destroy() {
        uploadManager = null;
    }
}
