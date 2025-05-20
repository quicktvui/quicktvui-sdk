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
public class EsProxy implements IEsProxy {

    private IEsProxy mProxy;

    public void setProxy(IEsProxy helper){
        this.mProxy = helper;
    }

    public IEsProxy getProxy() {
        return mProxy;
    }

    @Nullable
    @Override
    public Activity getTopActivity() {
        return mProxy == null ? null : mProxy.getTopActivity();
    }

    @Nullable
    @Override
    public Activity getCurrentActivity(IEsTraceable traceable) {
        return mProxy == null ? null : mProxy.getCurrentActivity(traceable);
    }

    @Override
    public @Nullable Context getContext() {
        return mProxy == null ? null : mProxy.getContext();
    }

    @Override
    public @Nullable Context getContext(String pkgName) {
        return mProxy == null ? null : mProxy.getContext(pkgName);
    }

    public @Nullable String getEsPackageName(IEsTraceable traceable){
        return mProxy == null ? null : mProxy.getEsPackageName(traceable);
    }

    @Nullable
    @Override
    public String getChannel() {
        return mProxy == null ? null : mProxy.getChannel();
    }

    @Nullable
    @Override
    public String getEsAppPath(IEsTraceable traceable) {
        return mProxy == null ? null : mProxy.getEsAppPath(traceable);
    }

    @Nullable
    @Override
    public String getEsAppRuntimePath(IEsTraceable traceable) {
        return mProxy == null ? null : mProxy.getEsAppRuntimePath(traceable);
    }

    @Nullable
    @Override
    public String getEsAppRuntimePath(int engineId) {
        return mProxy == null ? null : mProxy.getEsAppRuntimePath(engineId);
    }

    @Override
    public boolean isDebugModel() {
        return mProxy != null && mProxy.isDebugModel();
    }

    @Override
    public String getDebugServer() {
        return mProxy == null ? null : mProxy.getDebugServer();
    }

    @Override
    public void updateLayout(IEsComponentView view) {
        if(mProxy == null) return;
        mProxy.updateLayout(view);
    }

    @Override
    public void sendUIEvent(int viewId, String eventName, Object params) {
        if(mProxy == null) return;
        mProxy.sendUIEvent(viewId, eventName, params);
    }

    @Override
    public void sendUIEvent(IEsTraceable traceable, int viewId, String eventName, Object params) {
        if(mProxy == null) return;
        mProxy.sendUIEvent(traceable, viewId, eventName, params);
    }

    @Override
    public void sendNativeEventTop(String eventName, Object params) {
        if(mProxy == null) return;
        mProxy.sendNativeEventTop(eventName, params);
    }

    @Override
    public void sendNativeEventAll(String eventName, Object params) {
        if(mProxy == null) return;
        mProxy.sendNativeEventAll(eventName, params);
    }

    @Override
    public void sendNativeEventTraceable(IEsTraceable traceable, String eventName, Object params) {
        if(mProxy == null) return;
        mProxy.sendNativeEventTraceable(traceable, eventName, params);
    }

    @Override
    public void sendNativeEvent2App(String packageName, String eventName, Object params) {
        if(mProxy == null) return;
        mProxy.sendNativeEvent2App(packageName, eventName, params);
    }

    @Override
    public boolean stateContainsAttribute(int[] stateSpecs, int attr) {
        return mProxy != null && mProxy.stateContainsAttribute(stateSpecs, attr);
    }

    @Override
    public boolean stateContainsAttribute(int[] stateSpecs, int[] state) {
        return mProxy != null && mProxy.stateContainsAttribute(stateSpecs, state);
    }

    @Override
    public boolean isRegisterComponent(String className) {
        return mProxy != null && mProxy.isRegisterComponent(className);
    }

    @Override
    public boolean isRegisterModule(String className) {
        return mProxy != null && mProxy.isRegisterModule(className);
    }

    @Override
    public void registerComponent(String... className) {
        if(mProxy == null) return;
        mProxy.registerComponent(className);
    }

    @Override
    public void registerComponent(Class<? extends IEsComponent<?>>... clazz) {
        if(mProxy == null) return;
        mProxy.registerComponent(clazz);
    }

    @Override
    public void registerModule(String... className) {
        if(mProxy == null) return;
        mProxy.registerModule(className);
    }

    @Override
    public void registerModule(Class<? extends IEsModule>... clazz) {
        if(mProxy == null) return;
        mProxy.registerModule(clazz);
    }

    @Override
    public void registerApiProvider(Object... providers) {
        if(mProxy == null) return;
        mProxy.registerApiProvider(providers);
    }

    @Override
    public void loadImageBitmap(EsMap params, EsCallback<Bitmap, Throwable> callback) {
        if(mProxy == null) return;
        mProxy.loadImageBitmap(params, callback);
    }

    @Override
    public void loadImageBitmap(IEsTraceable traceable, EsMap params, EsCallback<Bitmap, Throwable> callback) {
        if(mProxy == null) return;
        mProxy.loadImageBitmap(traceable, params, callback);
    }

    @Override
    public String getSdkVersionName() {
        return mProxy == null ? "" : mProxy.getSdkVersionName();
    }

    @Override
    public int getSdkVersionCode() {
        return mProxy == null ? -1 : mProxy.getSdkVersionCode();
    }

    @Override
    public double getEsKitVersionCode() {
        return mProxy == null ? -1 : mProxy.getEsKitVersionCode();
    }

    @Override
    public String getEsKitVersionName() {
        return mProxy == null ? "" : mProxy.getEsKitVersionName();
    }

    @Override
    public EsMap getEsKitInfo() {
        return mProxy == null ? new EsMap() : mProxy.getEsKitInfo();
    }

    @Override
    public void receiveEvent(String data, IEsRemoteEventCallback callback) {
        if(mProxy == null) return;
        mProxy.receiveEvent(data, callback);
    }

    @Override
    public void receiveThirdEvent(ThirdEvent event) {
        if(mProxy == null) return;
        mProxy.receiveThirdEvent(event);
    }

    @Override
    public ScreenInfo getScreenInfo() {
        return mProxy == null ? null : mProxy.getScreenInfo();
    }

    @Override
    public boolean checkSelfPermission(String[] permissions) {
        return mProxy != null && mProxy.checkSelfPermission(permissions);
    }

    @Override
    public void requestPermission(IEsTraceable traceable, String[] permissions, EsCallback<List<String>, Pair<List<String>, List<String>>> callback) {
        if(mProxy != null) mProxy.requestPermission(traceable, permissions, callback);
    }

    @Override
    public String getProxyHostName() {
        return mProxy == null ? null : mProxy.getProxyHostName();
    }

    @Override
    public int getProxyPort() {
        return mProxy == null ? 0 : mProxy.getProxyPort();
    }

    @Override
    public ISoManager getSoManager() {
        return mProxy == null ? null : mProxy.getSoManager();
    }

    @Override
    public IDiskCacheManager getDiskCacheManager() {
        return mProxy == null ? null : mProxy.getDiskCacheManager();
    }

    @Override
    public IDisplayManager getDisplayManager() {
        return mProxy == null ? null : mProxy.getDisplayManager();
    }

    @Override
    public EsMap getPackageJson() {
        return mProxy == null ? null : mProxy.getPackageJson();
    }

    @Override
    public void setTakeOverKeyEventListener(ITakeOverKeyEventListener listener) {
        if(mProxy == null) return;
        mProxy.setTakeOverKeyEventListener(listener);
    }

    @Override
    public boolean isContainsFlag(int flag) {
        if(mProxy == null) return false;
        return mProxy.isContainsFlag(flag);
    }

    //region 单例

    private static final class EsHelperProxyHolder{
        private static final EsProxy INSTANCE = new EsProxy();
    }

    public static IEsProxy get(){
        return EsHelperProxyHolder.INSTANCE;
    }

    private EsProxy(){}

    //endregion


}
