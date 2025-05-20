package com.quicktvui.sdk.core.internal;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.quicktvui.base.ui.ESBaseConfigManager;
import com.quicktvui.base.ui.graphic.BaseBorderDrawable;
import com.quicktvui.base.ui.graphic.BaseBorderDrawableProvider;
import com.quicktvui.sdk.base.IEsRemoteEventCallback;
import com.quicktvui.sdk.base.ISoManager;
import com.quicktvui.sdk.base.image.IEsImageLoader;
import com.quicktvui.sdk.core.EsKitStatus;
import com.quicktvui.sdk.core.EsManager;
import com.quicktvui.sdk.core.InitConfig;
import com.quicktvui.sdk.core.adapter.EsApiAdapter;
import com.quicktvui.sdk.core.entity.DeviceInfo;
import com.quicktvui.sdk.core.ext.log.ILogCallback;
import com.quicktvui.sdk.core.utils.SpUtils;
import com.sunrain.toolkit.utils.EncodeUtils;
import com.sunrain.toolkit.utils.ThreadUtils;
import com.sunrain.toolkit.utils.log.L;

import org.json.JSONObject;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by weipeng on 2022/03/01 15:37
 */
public class EsContext {

    private Application mContext;
    private IEsImageLoader mImageLoader;
    private String mClientId;
    private String mDeviceId;
    private String mDeviceName;
    private BaseBorderDrawableProvider<ConcurrentHashMap<Integer, BaseBorderDrawable>> mBorderDrawableProvider;
    private ESBaseConfigManager esBaseConfigManager;
    private boolean relieveImageSize = false;
    private SoftReference<DeviceInfo> mDeviceInfo;
    private SoftReference<Gson> mGsonRef;
    private IEsRemoteEventCallback mEsRemoteEventCallback;

    // 动态So管理类
    private ISoManager mSoManager;
    private EsApiAdapter mEsApiAdapter;

    private Set<ILogCallback> mLogCallbacks;

    // 常见系统中文名
    private static final Map<String, String> sNames = new HashMap<>();

    // 要打印的系统信息，方便调试
    private String mSystemInfo;

    static {
        sNames.put("XIAOMI", "小米");
        sNames.put("CHANGHONG", "长虹");
        sNames.put("BAOFENG", "暴风");
        sNames.put("SKYWORTH", "创维");
        sNames.put("HISENSE", "海信");
        sNames.put("KONKA", "康佳");
        sNames.put("LETV", "乐视");
        sNames.put("NVIDIA", "英伟达");
    }

    public void init(Application context) {
        mContext = context;
        mClientId = SpUtils.getClientId();
        String deviceId = InitConfig.getDefault().getDeviceId();
        if (!TextUtils.isEmpty(deviceId)) {
            mDeviceId = deviceId;
        }
        printSystemInfo();
    }

    /**
     * 未初始化
     **/
    public boolean isInitNotOk() {
        EsKitStatus status = EsManager.get().getSdkInitStatus();
        return status != EsKitStatus.STATUS_SUCCESS && status != EsKitStatus.STATUS_ERROR;
    }

    /**
     * 初始化成功
     **/
    public boolean isInitSuccess() {
        EsKitStatus status = EsManager.get().getSdkInitStatus();
        return status == EsKitStatus.STATUS_SUCCESS;
    }

    /**
     * 初始化失败
     **/
    public boolean isInitFail() {
        EsKitStatus status = EsManager.get().getSdkInitStatus();
        return status == EsKitStatus.STATUS_ERROR;
    }

    public void setImageLoader(IEsImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }

    /**
     * 注册Vue外发的事件回调
     **/
    public void setRemoteEventCallback(IEsRemoteEventCallback callback) {
        this.mEsRemoteEventCallback = callback;
    }

    public IEsRemoteEventCallback getRemoteEventCallback() {
        return mEsRemoteEventCallback;
    }

    public Context getContext() {
        return mContext;
    }

    public IEsImageLoader getImageLoader() {
        return mImageLoader;
    }

    public void post(Runnable runnable) {
        ThreadUtils.runOnUiThread(runnable);
    }

    public void postDelay(Runnable runnable, long delayMillis) {
        ThreadUtils.runOnUiThreadDelayed(runnable, delayMillis);
    }

    /**
     * SDK集成的ID
     **/
    public String getAppId() {
        return InitConfig.getDefault().getAppId();
    }

    /**
     * SDK集成渠道
     **/
    public String getAppChannel() {
        return InitConfig.getDefault().getChannel();
    }

    /**
     * 自定义服务器
     **/
    public String getCustomServer() {
        return InitConfig.getDefault().getCustomServer();
    }

    /**
     * 自定义埋点服务器
     **/
    public String getCustomTrackServer() {
        return InitConfig.getDefault().getCustomTrackServer();
    }

    public void setCustomServer(String host) {
        InitConfig.getDefault().setCustomServer(host);
    }

    public EsContext setClientId(String clientId) {
        this.mClientId = clientId;
        SpUtils.saveClientId(clientId);
        return this;
    }

    /**
     * 获取生成的CID
     **/
    public String getClientId() {
//        return mClientId;
        return "9999999";
    }

    /**
     * 设置自定义设备ID
     **/
    public EsContext setDeviceId(String deviceId) {
        this.mDeviceId = deviceId;
        return this;
    }

    /**
     * 获取设备ID，需初始化的时候传入
     **/
    public String getDeviceId() {
        return mDeviceId;
    }

    public String getDeiceFriendlyName() {
        String name = getZhCnName(Build.MANUFACTURER);
        if (TextUtils.isEmpty(name)) name = getZhCnName(Build.BRAND);
        if (TextUtils.isEmpty(name)) name = Build.BRAND;
        return name;
    }

    public String getDeviceName() {
        String deviceName = InitConfig.getDefault().getDeviceName();
        if (!TextUtils.isEmpty(deviceName)) {
            mDeviceName = deviceName;
        }
        if (TextUtils.isEmpty(mDeviceName)) {
            String cid = getClientId();
            String code = TextUtils.isEmpty(cid) ? "GUEST" : cid.substring(cid.length() - 3).toUpperCase();
            mDeviceName = "超级投屏" +
                    "(" + getDeiceFriendlyName() + ") " + code;
        }
        return mDeviceName;
    }

    public Map<String, String> getDeviceInfo() {
        DeviceInfo info;
        if (mDeviceInfo == null || (info = mDeviceInfo.get()) == null) {
            info = new DeviceInfo(mContext);
            mDeviceInfo = new SoftReference<>(info);
        }
        return info.getInfo();
    }

    public Gson getGson() {
        if (mGsonRef == null || mGsonRef.get() == null) {
            mGsonRef = new SoftReference<>(new Gson());
        }
        return mGsonRef.get();
    }

    private String getZhCnName(String type) {
        if (!TextUtils.isEmpty(type)) {
            String upper = type.toUpperCase();
            Set<String> keys = sNames.keySet();
            for (String key : keys) {
                if (upper.contains(key)) return sNames.get(key);
            }
        }
        return null;
    }

    public EsContext setSoManager(ISoManager manager) {
        this.mSoManager = manager;
        return this;
    }

    public ISoManager getSoManager() {
        return mSoManager;
    }

    public EsApiAdapter getEsApiAdapter() {
        return mEsApiAdapter;
    }

    public EsContext setEsApiAdapter(EsApiAdapter adapter) {
        this.mEsApiAdapter = adapter;
        return this;
    }

    public void addLogCallback(ILogCallback callback) {
        if (mLogCallbacks == null) {
            mLogCallbacks = new HashSet<>();
        }
        mLogCallbacks.add(callback);
    }

    public void removeLogCallback(ILogCallback callback) {
        if (mLogCallbacks == null) return;
        mLogCallbacks.remove(callback);
    }

    public Set<ILogCallback> getLogCallbacks() {
        return mLogCallbacks;
    }

    //region 单例

    private static final class EsContextHolder {
        private static final EsContext INSTANCE = new EsContext();
    }

    public static EsContext get() {
        return EsContextHolder.INSTANCE;
    }

    private EsContext() {
    }

    //endregion


    public BaseBorderDrawableProvider<ConcurrentHashMap<Integer, BaseBorderDrawable>> getBorderDrawableProvider() {
        return mBorderDrawableProvider;
    }

    public void setBorderDrawableProvider(BaseBorderDrawableProvider<ConcurrentHashMap<Integer, BaseBorderDrawable>> mBorderDrawableProvider) {
        this.mBorderDrawableProvider = mBorderDrawableProvider;
    }

    public ESBaseConfigManager getEsBaseConfigManager() {
        return esBaseConfigManager;
    }

    public void setEsBaseConfigManager(ESBaseConfigManager esBaseConfigManager) {
        this.esBaseConfigManager = esBaseConfigManager;
    }

    public boolean isRelieveImageSize() {
        return relieveImageSize;
    }

    public void setRelieveImageSize(boolean relieveImageSize) {
        this.relieveImageSize = relieveImageSize;
    }

    public synchronized void printSystemInfo() {
        try {
            if (mSystemInfo == null) {
                JSONObject jo = new JSONObject();

                jo.putOpt("device", Build.DEVICE);
                jo.putOpt("manufacturer", Build.MANUFACTURER);
                jo.putOpt("board", Build.BOARD);
                jo.putOpt("brand", Build.BRAND);
                jo.putOpt("id", Build.ID);
                jo.putOpt("osv", Build.VERSION.RELEASE);
                jo.putOpt("sdk_int", Build.VERSION.SDK_INT);
                jo.putOpt("abi1", Build.CPU_ABI);
                jo.putOpt("abi2", Build.CPU_ABI2);

                mSystemInfo = EncodeUtils.base64Encode2String(jo.toString().getBytes());
            }
            Random random = new Random(System.currentTimeMillis());
            String salt = "lYLPRzc4F1SRZc6xrkZClp4XnefCKrE2ENszLNIn5";
            int c1 = random.nextInt(salt.length());
            int c2 = random.nextInt(salt.length());
            int c3 = random.nextInt(salt.length());
            L.logIF(new String(new char[]{
                    salt.charAt(c1),
                    salt.charAt(c2),
                    salt.charAt(c3),
            }) + mSystemInfo);
        } catch (Exception ignore) {
        }
    }
}
