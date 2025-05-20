package com.quicktvui.sdk.core;

import android.app.Application;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.quicktvui.base.ui.ESBaseConfigManager;
import com.quicktvui.base.ui.graphic.BaseBorderDrawable;
import com.quicktvui.base.ui.graphic.BaseBorderDrawableProvider;

import java.util.concurrent.ConcurrentHashMap;

import com.quicktvui.sdk.core.internal.IEsAppLoadCallback;
import com.quicktvui.sdk.base.image.IEsImageLoader;

/**
 * Create by weipeng on 2022/03/01 15:04
 */
public interface IEsManager {

    /**
     * 初始化EsKitSDK
     **/
    void init(Application app, InitConfig config);

    /**
     * 获取SDK初始化状态
     **/
    EsKitStatus getSdkInitStatus();

    /** 设置图片解析类 **/
    void setImageLoader(IEsImageLoader imageLoader);

    /**
     * 注册组件
     *
     * @param className
     */
    void registerComponent(String... className);

    /**
     * 注册Module
     *
     * @param className
     */
    void registerModule(String... className);


    /**
     * 从新界面启动
     **/
    void start(EsData data);

    /**
     * @deprecated
     * 返回代码包创建的View(单页面)
     * use {@link #loadV2(FragmentActivity, int, EsData, IEsAppLoadCallback)}
     **/
    @Deprecated
    void load(Context context, EsData data, IEsAppLoadCallback callback);

    /**
     * @deprecated
     * 返回代码包创建的View(多页面)
     * use {@link #loadV2(FragmentActivity, int, EsData, IEsAppLoadCallback)}
     **/
    void load(Context context, int containerLayoutId, EsData data, IEsAppLoadCallback callback);

    void loadV2(FragmentActivity activity, int containerLayoutId, EsData data, IEsAppLoadCallback callback);

    boolean isEsRunning();

    boolean isEsRunning(String pkg);

    //自定义HippyImageView边框drawable
    void setBorderDrawableProvider(BaseBorderDrawableProvider<ConcurrentHashMap<Integer, BaseBorderDrawable>> borderDrawableProvider);

    //packageJson解析
    void setESBaseConfigManager(ESBaseConfigManager esBaseConfigManager);

    //是否解除图片大小限制
    void relieveImageSize(boolean flag);
}
