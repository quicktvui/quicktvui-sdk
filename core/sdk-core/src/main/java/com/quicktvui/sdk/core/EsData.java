package com.quicktvui.sdk.core;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.core.internal.Constants;
import com.quicktvui.sdk.core.utils.CommonUtils;
import com.quicktvui.sdk.core.utils.MapperUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Create by WeiPeng on 2020/12/19 20:25
 */
public class EsData implements Parcelable {

    // 旧数值，存在运算错误，启用下面新数值
//    public static final int ACTIVITY_FLAGS_CLEAR_TASK = 100;
//    public static final int ACTIVITY_FLAGS_SINGLE_TASK = 200;
//    public static final int ACTIVITY_FLAGS_SINGLE_TOP = 300;
//    public static final int ACTIVITY_FLAGS_SINGLE_INSTANCE = 400;

    public static final int ACTIVITY_FLAGS_CLEAR_TASK = 1;          // 1
    public static final int ACTIVITY_FLAGS_SINGLE_TASK = 1 << 1;      // 2
    public static final int ACTIVITY_FLAGS_SINGLE_TOP = 1 << 2;       // 4
    public static final int ACTIVITY_FLAGS_SINGLE_INSTANCE = 1 << 3;  // 8

    public static final int SPLASH_NONE = -1;
    public static final int SPLASH_DEFAULT = 0;
    public static final int SPLASH_NO_HEADER = 1;

    //    private String appId;
    // 小程序包名
    private String esPackage;
    // 指定加载版本号
    private String esVersion;
    // 指定name
    private String esName;
    // 指定icon
    private String esIcon;
    // 最低版本号
    private String esMinVersion;
    // 是否是Home界面，主要给大apk有默认界面的情况，关闭上层快应用的时候保留主界面。
    private boolean isHomePage;
    // 区分card
    private boolean isCard;
    // 启动参数
    private EsMap args;
    // 启动的IntentFlags
    private int flags;
    // 页面标记
    private String pageTag;
    // 页面最大打开个数 仅standard有效
    private int pageLimit;

    // 加载方式
    private String esPkgUrl;
    private String esPkgMd5;
    private boolean useEncrypt;

    // 是否单独进程
    private boolean multiProcess = true;

    // 使用WindowManager加载
    private boolean useWindow;
    // WindowManager情况下接收事件
    private boolean handleEvent = true;

    // 背景色
    private int backgroundColor = -1;
    // 显示启动广告
    private boolean showSplashAd = true;

    private boolean isTransparent = false;

    // 检测网络连接
    private boolean checkNetwork = true;

    // 服务端有新版 强制刷新
    // 加载一个老版本       am start -d 'esapp://com.quark.yun.tv/1.0.13?from=cmd'
    // 加载新版本，二次生效  am start -d 'esapp://com.quark.yun.tv?from=cmd'
    // 加载新版本，立即生效  am start -d 'esapp://com.quark.yun.tv?from=cmd&useLatest=true'
    private boolean useLatest = false;

    private int coverLayoutId = SPLASH_DEFAULT;
    private Serializable coverLayoutParams;

    // 扩展参数
    private EsMap exp;

    // 加载状态 0成功
    private int loadState = -1;

    private boolean isFeatureSingleActivity = false;

    private String repository;

    // 在Parcelable中传递接口
    private Map<String, ParcelableWrapper> mParcelableWrappers;

    public String getEsPackage() {
        return esPackage;
    }

    public String getName() {
        return esName;
    }

    @EsStartParam("name")
    public EsData setEsName(String name) {
        this.esName = name;
        return this;
    }

    public String getIcon() {
        return esIcon;
    }

    @EsStartParam("icon")
    public EsData setEsIcon(String icon) {
        this.esIcon = icon;
        return this;
    }

    @EsStartParam("ver")
    public EsData setEsVersion(String ver) {
        this.esVersion = ver;
        return this;
    }

    /**
     * 加载指定版本
     **/
    public String getEsVersion() {
        return esVersion == null ? "" : esVersion;
    }

    @EsStartParam("minVer")
    public EsData setEsMinVersion(String ver) {
        this.esMinVersion = ver;
        return this;
    }

    /**
     * 最低支持版本
     **/
    public String getEsMinVersion() {
        return esMinVersion;
    }

    public EsData isHomePage(boolean homePage) {
        isHomePage = homePage;
        return this;
    }

    public boolean isHomePage() {
        return isHomePage;
    }

    public boolean isCard() {
        return isCard;
    }

    @EsStartParam("isCard")
    public EsData setCard(boolean card) {
        isCard = card;
        return this;
    }

    @EsStartParam("transparent")
    public EsData setTransparent(boolean transparent) {
        this.isTransparent = transparent;
        return this;
    }

    public boolean isTransparent() {
        return isTransparent;
    }

    @EsStartParam("pkg")
    public EsData setAppPackage(String esPackage) {
        this.esPackage = esPackage;
        return this;
    }

    public EsMap getArgs() {
        return args;
    }

    @EsStartParam("args")
    public EsData setArgs(String json) {
        if (TextUtils.isEmpty(json)) return this;
        return setArgs(MapperUtils.tryMapperJson2EsMap(json));
    }

    public EsData setArgs(EsMap args) {
        this.args = args;
        return this;
    }

    public int getFlags() {
        return flags;
    }

    @EsStartParam("flags")
    public EsData setFlags(int flags) {
        // 兼容之前数值的错误
        this.flags = getFixFlags(flags);
        return this;
    }

    private int getFixFlags(int flags) {
        if (flags == 100) return ACTIVITY_FLAGS_CLEAR_TASK;
        if (flags == 200) return ACTIVITY_FLAGS_SINGLE_TASK;
        if (flags == 300) return ACTIVITY_FLAGS_SINGLE_TOP;
        if (flags == 400) return ACTIVITY_FLAGS_SINGLE_INSTANCE;
        if (flags == 236) return ACTIVITY_FLAGS_CLEAR_TASK | ACTIVITY_FLAGS_SINGLE_TASK;
        if (flags == 364) return ACTIVITY_FLAGS_CLEAR_TASK | ACTIVITY_FLAGS_SINGLE_TOP;
        if (flags == 500) return ACTIVITY_FLAGS_CLEAR_TASK | ACTIVITY_FLAGS_SINGLE_INSTANCE;
        return flags;
    }

    public boolean isDebug() {
        return !TextUtils.isEmpty(esPkgUrl) && esPkgUrl.endsWith(":38989");
    }

    public boolean isShowSplashAd() {
        return showSplashAd;
    }

    public EsData setShowSplashAd(boolean show) {
        this.showSplashAd = show;
        return this;
    }

    public String getPageTag() {
        return pageTag;
    }

    @EsStartParam("pageTag")
    public EsData setPageTag(String pageTag) {
        this.pageTag = pageTag;
        return this;
    }

    public int getPageLimit() {
        return pageLimit;
    }

    @EsStartParam("pageLimit")
    public EsData setPageLimit(int pageLimit) {
        this.pageLimit = pageLimit;
        return this;
    }

    public EsData setUseWindow(boolean use) {
        this.useWindow = use;
        return this;
    }

    public boolean isUseWindow() {
        return useWindow;
    }

    public EsData setHandleEvent(boolean handle) {
        handleEvent = handle;
        return this;
    }

    public boolean isHandleEvent() {
        return handleEvent;
    }

    @EsStartParam("bgColor")
    public EsData setBackgroundColor(int color) {
        this.backgroundColor = color;
        return this;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    @EsStartParam("uri")
    public EsData setAppDownloadUrl(String uri) {
        esPkgUrl = uri;
        return this;
    }

    public EsData setAppLoadUri(String uri) {
        return setAppDownloadUrl(uri);
    }

    public String getAppDownloadUrl() {
        return esPkgUrl;
    }

    public EsData setAppMd5(String md5) {
        esPkgMd5 = md5;
        return this;
    }

    public String getAppMd5() {
        return esPkgMd5;
    }

    public String getRepository() {
        return repository;
    }

    @EsStartParam(Constants.EsData.K_REPO)
    public EsData setRepository(String repository) {
        this.repository = repository;
        return this;
    }

    @EsStartParam("enc")
    public EsData setUseEncrypt(boolean useEncrypt) {
        this.useEncrypt = useEncrypt;
        return this;
    }

    public boolean isUseEncrypt() {
        return useEncrypt;
    }

    public boolean isCheckNetwork() {
        return checkNetwork;
    }

    @EsStartParam("checkNetwork")
    public EsData setCheckNetwork(boolean checkNetwork) {
        this.checkNetwork = checkNetwork;
        return this;
    }

    @EsStartParam("useLatest")
    public EsData setUseLatest(boolean useLatest) {
        this.useLatest = useLatest;
        return this;
    }

    public boolean isUseLatest() {
        return useLatest;
    }

    public int getCoverLayoutId() {
        return coverLayoutId;
    }

    public Serializable getCoverLayoutParams() {
        return coverLayoutParams;
    }

    @EsStartParam("splash")
    public EsData setCoverLayoutId(int coverLayoutId) {
        return setCoverLayoutId(coverLayoutId, null);
    }

    public EsData setCoverLayoutId(int coverLayoutId, Serializable params) {
        this.coverLayoutId = coverLayoutId;
        this.coverLayoutParams = params;
        return this;
    }

    public EsMap getExp() {
        return exp;
    }

    @EsStartParam("exp")
    public EsData setExp(String json) {
        if (TextUtils.isEmpty(json)) return this;
        return setExp(MapperUtils.tryMapperJson2EsMap(json));
    }

    public EsData setExp(EsMap exp) {
        this.exp = exp;
        return this;
    }

    public EsData setLoadState(int loadState) {
        this.loadState = loadState;
        return this;
    }

    public int getLoadState() {
        return loadState;
    }

    public boolean isFeatureSingleActivity() {
        return isFeatureSingleActivity;
    }

    @EsStartParam("feature_single_activity")
    public EsData setFeatureSingleActivity(boolean single) {
        isFeatureSingleActivity = single;
        return this;
    }

    public boolean isClearTask() {
        return CommonUtils.isContainsFlag(getFlags(), ACTIVITY_FLAGS_CLEAR_TASK);
    }

    public EsData addInterface(String key, Object interfaceObj) {
        if (interfaceObj != null) {
            if(mParcelableWrappers == null){
                mParcelableWrappers = new HashMap<>();
            }
            mParcelableWrappers.put(key, new ParcelableWrapper<>(interfaceObj));
        }
        return this;
    }

    public <T> T getInterface(String key) {
        if (mParcelableWrappers != null) {
            ParcelableWrapper<T> wrapper = mParcelableWrappers.get(key);
            if (wrapper != null) {
                return wrapper.instance;
            }
        }
        return null;
    }

    public EsData clone() {
        Parcel clone = null;
        try {
            clone = Parcel.obtain();
            clone.writeParcelable(this, 0);
            clone.setDataPosition(0);
            return clone.readParcelable(EsData.class.getClassLoader());
        } finally {
            if (clone != null) clone.recycle();
        }
    }

    // ------------------------------------------------------------------------ //


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.esPackage);
        dest.writeString(this.esName);
        dest.writeString(this.esIcon);
        dest.writeString(this.esVersion);
        dest.writeString(this.esMinVersion);
        dest.writeByte(this.isHomePage ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isCard ? (byte) 1 : (byte) 0);
        dest.writeSerializable(this.args);
        dest.writeSerializable(this.exp);
        dest.writeString(this.esPkgUrl);
        dest.writeString(this.esPkgMd5);
        dest.writeByte(this.useEncrypt ? (byte) 1 : (byte) 0);
        dest.writeString(this.pageTag);
        dest.writeInt(this.pageLimit);
        dest.writeInt(this.flags);
        dest.writeInt(this.loadState);
        dest.writeByte(this.multiProcess ? (byte) 1 : (byte) 0);
        dest.writeByte(this.useWindow ? (byte) 1 : (byte) 0);
        dest.writeByte(this.handleEvent ? (byte) 1 : (byte) 0);
        dest.writeInt(this.backgroundColor);
        dest.writeByte(this.showSplashAd ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isTransparent ? (byte) 1 : (byte) 0);
        dest.writeByte(this.checkNetwork ? (byte) 1 : (byte) 0);
        dest.writeByte(this.useLatest ? (byte) 1 : (byte) 0);
        dest.writeInt(this.coverLayoutId);
        dest.writeString(this.repository);
        dest.writeSerializable(this.coverLayoutParams);
        dest.writeByte(this.isFeatureSingleActivity ? (byte) 1 : (byte) 0);
        dest.writeMap(mParcelableWrappers);
    }

    public EsData() {
    }

    protected EsData(Parcel in) {
        this.esPackage = in.readString();
        this.esName = in.readString();
        this.esIcon = in.readString();
        this.esVersion = in.readString();
        this.esMinVersion = in.readString();
        this.isHomePage = in.readByte() != 0;
        this.isCard = in.readByte() != 0;
        this.args = (EsMap) in.readSerializable();
        this.exp = (EsMap) in.readSerializable();
        this.esPkgUrl = in.readString();
        this.esPkgMd5 = in.readString();
        this.useEncrypt = in.readByte() != 0;
        this.pageTag = in.readString();
        this.pageLimit = in.readInt();
        this.flags = in.readInt();
        this.loadState = in.readInt();
        this.multiProcess = in.readByte() != 0;
        this.useWindow = in.readByte() != 0;
        this.handleEvent = in.readByte() != 0;
        this.backgroundColor = in.readInt();
        this.showSplashAd = in.readByte() != 0;
        this.isTransparent = in.readByte() != 0;
        this.checkNetwork = in.readByte() != 0;
        this.useLatest = in.readByte() != 0;
        this.coverLayoutId = in.readInt();
        this.repository = in.readString();
        this.coverLayoutParams = in.readSerializable();
        this.isFeatureSingleActivity = in.readByte() != 0;
        mParcelableWrappers = new HashMap<>();
        in.readMap(this.mParcelableWrappers, ParcelableWrapper.class.getClassLoader());
    }

    public static final Creator<EsData> CREATOR = new Creator<EsData>() {
        @Override
        public EsData createFromParcel(Parcel source) {
            return new EsData(source);
        }

        @Override
        public EsData[] newArray(int size) {
            return new EsData[size];
        }
    };

    public static final class ParcelableWrapper<T> implements Parcelable {

        private T instance;

        public ParcelableWrapper(T instance) {
            this.instance = instance;
        }

        public T getInstance(){
            return instance;
        }

        protected ParcelableWrapper(Parcel in) {
        }

        public static final Creator<ParcelableWrapper> CREATOR = new Creator<ParcelableWrapper>() {
            @Override
            public ParcelableWrapper createFromParcel(Parcel in) {
                return new ParcelableWrapper(in);
            }

            @Override
            public ParcelableWrapper[] newArray(int size) {
                return new ParcelableWrapper[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
        }
    }

    @Override
    public String toString() {
        return "EsData{" +
                "esPackage='" + esPackage + '\'' +
                ", name='" + esName + '\'' +
                ", icon='" + esIcon + '\'' +
                ", esVersion='" + esVersion + '\'' +
                ", isHomePage=" + isHomePage +
                ", isCard=" + isCard +
                ", args=" + args +
                ", exp=" + exp +
                ", flags=" + flags +
                ", loadState=" + loadState +
                ", pageTag='" + pageTag + '\'' +
                ", pageLimit=" + pageLimit +
                ", esPkgUrl='" + esPkgUrl + '\'' +
                ", esPkgMd5='" + esPkgMd5 + '\'' +
                ", useEncrypt=" + useEncrypt +
                ", multiProcess=" + multiProcess +
                ", useWindow=" + useWindow +
                ", handleEvent=" + handleEvent +
                ", backgroundColor=" + backgroundColor +
                ", showSplashAd=" + showSplashAd +
                ", isTransparent=" + isTransparent +
                ", checkNetwork=" + checkNetwork +
                ", useLatest=" + useLatest +
                ", coverLayoutId=" + coverLayoutId +
                ", repo=" + repository +
                ", coverLayoutParams=" + coverLayoutParams +
                '}';
    }
}