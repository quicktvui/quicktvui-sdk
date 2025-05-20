package quicktvui.support.upload;

import android.util.Log;

import com.sunrain.toolkit.utils.log.L;

import java.io.File;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;

public class UploadManager {

    private OkHttpClient okHttpClient;

    public UploadManager() {
        okHttpClient = getOkHttpClient();
    }

    public OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder()
                .retryOnConnectionFailure(true)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .addNetworkInterceptor(getLogInterceptor()).build();
    }

    public Interceptor getLogInterceptor() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        return loggingInterceptor;
    }

    /**
     * 上传文件
     *
     * @param url
     * @param mediaType
     * @param filePramsKey
     * @param filePath
     * @param params
     * @return
     */
    public Call upload(String url,
                       String mediaType,
                       String filePramsKey,
                       String filePath,
                       Map<String, String> params,
                       Map<String, String> headerMap) {
        if (L.DEBUG) {
            Log.e("UploadManager", "-----UploadAPI--upload----1--->>>>" //
                    + "url:" + url //
                    + "----" + "mediaType:" + mediaType //
                    + "----" + "filePramsKey:" + filePramsKey //
                    + "----" + "filePath:" + filePath //
                    + "----" + "params:" + params + "----");
        }

        File file = new File(filePath);
        if (L.DEBUG) {
            L.logD("-----UploadAPI--upload----2222--->>>>" + mediaType);
        }

        MediaType type = MediaType.parse(mediaType);
        MultipartBody.Builder builder = null;
        if (type != null) {
            try {
                builder = new MultipartBody.Builder().setType(type);
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        //
        if (builder == null) {
            builder = new MultipartBody.Builder();
        }

        if (L.DEBUG) {
            L.logD("-----UploadAPI--upload-----file-->>>>" + builder);
        }

        try {
            if (params != null && params.size() > 0) {
                for (String key : params.keySet()) {
                    try {
                        String value = params.get(key);
                        builder.addFormDataPart(key, value);
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        MultipartBody requestBody = builder.addFormDataPart(filePramsKey.toString(),//
                filePath,//
                RequestBody.create(type, file)//
        ).build();

        Request.Builder requestBuilder = new Request.Builder()//
                .url(url)//
                .post(requestBody);

        try {
            if (headerMap != null && headerMap.size() > 0) {
                for (String key : headerMap.keySet()) {
                    String value = headerMap.get(key);
                    if (value != null) {
                        requestBuilder.header(key, value);
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        Request request = requestBuilder.build();//

        if (L.DEBUG) {
            L.logD("-----UploadAPI--newCall------->>>>" + request);
        }

        return okHttpClient.newCall(request);
    }
}
