package com.quicktvui.sdk.base.core;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.util.Pair;

import com.quicktvui.sdk.base.EsCallback;
import com.quicktvui.sdk.base.IDiskCacheManager;
import com.quicktvui.sdk.base.IEsRemoteEventCallback;
import com.quicktvui.sdk.base.IEsTraceable;
import com.quicktvui.sdk.base.ISoManager;
import com.quicktvui.sdk.base.ITakeOverKeyEventListener;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.IEsComponent;
import com.quicktvui.sdk.base.component.IEsComponentView;
import com.quicktvui.sdk.base.display.IDisplayManager;
import com.quicktvui.sdk.base.model.ScreenInfo;
import com.quicktvui.sdk.base.model.ThirdEvent;
import com.quicktvui.sdk.base.module.IEsModule;

import java.util.List;

/**
 * Create by weipeng on 2022/03/02 15:28
 */
public interface IEsProxy {


    @Nullable Activity getTopActivity();
    /**
     * 获取当前Activity
     * @return
     */
    @Nullable Activity getCurrentActivity(IEsTraceable traceable);

    @Nullable Context getContext();

    /**
     * 获取指定包名的Context，仅用做插件版本使用
     * @param pkgName
     * @return
     */
    @Nullable Context getContext(String pkgName);

    /**
     * 获取EsApp的包名
     * @return
     * @param traceable
     */
    @Nullable String getEsPackageName(IEsTraceable traceable);

    /** 获取渠道信息 **/
    @Nullable String getChannel();

    /**
     * 获取EsApp路径, 用于文件存储管理
     * @return
     * /data/data/APK包名/app_rpk/apps/小程序包名/files
     * 示例:/data/data/com.extscreen.runtime/app_rpk/apps/es.com.baduanjin.tv/files
     */
    @Nullable String getEsAppPath(IEsTraceable traceable);

    /**
     * 获取EsApp运行时路径, 通常用于获取代码包里的assets
     * @return
     * /data/data/APK包名/app_rpk/apps/小程序包名/版本号/android
     * 示例:/data/data/com.extscreen.runtime/app_rpk/apps/es.com.baduanjin.tv/2.2.2203/android
     */
    @Nullable String getEsAppRuntimePath(IEsTraceable traceable);

    @Nullable String getEsAppRuntimePath(int engineId);

    boolean isDebugModel();

    String getDebugServer();

    /**
     * layout界面，用于Component的View多层嵌套的情况
     * @param view
     */
    void updateLayout(IEsComponentView view);

    /**
     * 向UI控件发送事件
     * @param viewId view.getId()
     * @param eventName 事件名称
     * @param params 事件参数，支持基本类型和 EsMap(及其隐含类型) EsArray(及其隐含类型)
     */
    void sendUIEvent(int viewId, String eventName, Object params);

    void sendUIEvent(IEsTraceable traceable, int viewId, String eventName, Object params);

    /**
     * 向Vue顶层页面发送事件
     * @param eventName 事件名称
     * @param params 事件参数，支持基本类型和 EsMap(及其隐含类型) EsArray(及其隐含类型)
     */
    void sendNativeEventTop(String eventName, Object params);

    /**
     * 向Vue所有页面发送事件
     * @param eventName 事件名称
     * @param params 事件参数，支持基本类型和 EsMap(及其隐含类型) EsArray(及其隐含类型)
     */
    void sendNativeEventAll(String eventName, Object params);

    /**
     * 向指定的IEsComponent或者IEsModule发送事件
     * @param traceable IEsComponent或者IEsModule对象
     * @param eventName 事件名称
     * @param params 事件参数，支持基本类型和 EsMap(及其隐含类型) EsArray(及其隐含类型)
     */
    void sendNativeEventTraceable(IEsTraceable traceable, String eventName, Object params);

    /**
     * 向指定Vue发送事件
     * @param packageName 快应用包名/卡片ID
     * @param eventName 事件名称
     * @param params 事件参数，支持基本类型和 EsMap(及其隐含类型) EsArray(及其隐含类型)
     */
    void sendNativeEvent2App(String packageName, String eventName, Object params);

    boolean stateContainsAttribute(int[] stateSpecs, int attr);

    boolean stateContainsAttribute(int[] stateSpecs, int[] state);

    /** 是否注册了组件 **/
    boolean isRegisterComponent(String className);

    /** 是否注册了组件 **/
    boolean isRegisterModule(String className);

    /**
     * 注册组件
     * @param className
     */
    void registerComponent(String... className);

    /**
     * 注册组件
     * @param clazz
     */
    void registerComponent(Class<? extends IEsComponent<?>>... clazz);

    /**
     * 注册Module
     * @param className
     */
    void registerModule(String... className);

    /**
     * 注册Module
     * @param clazz
     */
    void registerModule(Class<? extends IEsModule>... clazz);

    /** 注册原生provider **/
    void registerApiProvider(Object... providers);

    /**
     * 加载图片
     * @param params
     * @param callback
     */
    void loadImageBitmap(EsMap params, EsCallback<Bitmap, Throwable> callback);

    void loadImageBitmap(IEsTraceable traceable, EsMap params, EsCallback<Bitmap, Throwable> callback);

    /**
     * 获取SDK版本名称
     * 请使用{@link #getEsKitVersionName()}}
     * @return
     */
    @Deprecated
    String getSdkVersionName();

    /**
     * 获取SDK版本Code
     * 请使用{@link #getEsKitVersionCode()}}
     * @return
     */
    @Deprecated
    int getSdkVersionCode();

    /**
     * 获取EsKit版本名称
     * @return
     */
    String getEsKitVersionName();

    /**
     * 获取EsKit版本号
     * @return
     */
    double getEsKitVersionCode();

    EsMap getEsKitInfo();

    /**
     *
     * @param data
     * @param callback
     */
    void receiveEvent(String data, IEsRemoteEventCallback callback);

    /**
     * 接收三方消息
     * @param event
     */
    void receiveThirdEvent(ThirdEvent event);

    /** 获取屏幕信息 **/
    ScreenInfo getScreenInfo();

    /** 检查权限 **/
    boolean checkSelfPermission(String[] permissions);

    void requestPermission(IEsTraceable traceable, String[] permissions, EsCallback<List<String>, Pair<List<String>, List<String>>> callback);

    String getProxyHostName();

    int getProxyPort();

    /** 获取So下载类 **/
    ISoManager getSoManager();

    /** 获取缓存管理类 **/
    IDiskCacheManager getDiskCacheManager();

    /**
     * @return
     */
    IDisplayManager getDisplayManager();

    /** 获取代码包的package.json **/
    EsMap getPackageJson();

    void setTakeOverKeyEventListener(ITakeOverKeyEventListener listener);

    boolean isContainsFlag(int flag);
}
