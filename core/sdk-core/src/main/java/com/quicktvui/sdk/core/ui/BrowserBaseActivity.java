package com.quicktvui.sdk.core.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.quicktvui.sdk.base.EsCallback;
import com.quicktvui.sdk.base.cover.IEsCoverView;
import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.internal.EsContext;
import com.quicktvui.sdk.core.ui.pg.FragmentPageLoader;
import com.quicktvui.sdk.core.ui.pg.IPageLoader;
import com.quicktvui.sdk.core.utils.EskitLazyInitHelper;
import com.quicktvui.sdk.core.utils.PermissionRequestHelper;
import com.quicktvui.sdk.core.views.DebugFocusHelper;
import com.sunrain.toolkit.utils.ThreadUtils;
import com.sunrain.toolkit.utils.log.L;

import com.quicktvui.sdk.core.R;

import java.util.List;

/**
 * <br>
 * 支持单页面加载多个rpk的Activity
 * <br>
 * <br>
 * Created by WeiPeng on 2023-08-18 10:05
 */
public class BrowserBaseActivity extends BrowserBaseFullActivity {

    private IPageLoader mPageLoader;

    private PermissionRequestHelper mPermissionRequestHelper;

    DebugFocusHelper mDebugFocusHelper;

    // 缓存数据 用于销毁时重建
    private EsData mCacheData;
    public static final String CACHE_START_DATA = "_cache_data";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 如果SDK是被动初始化模式，在Activity重建的情况下需要
        EskitLazyInitHelper.initIfNeed();
        if (EsContext.get().getContext() == null) { // 懒初始化失败
            finish();
            return;
        }

        if (L.DEBUG) L.logI("app_chain activity create");

        EsData data = null;

        // restore
        if(savedInstanceState != null){
            data = savedInstanceState.getParcelable(CACHE_START_DATA);
            L.logD("restore: " + (data != null));
        }

        if (data == null) {
            Intent intent = getIntent();
            if (intent != null) data = intent.getParcelableExtra("data");
        }

        // 没有启动参数
        if (data == null) {
            L.logEF("No start data");
            finish();
            return;
        }

        // 未知错误，可能后台非正常杀死
        if (EsContext.get().getContext() == null) {
            L.logEF("UNKNOWN_ERROR!");
            finish();
            return;
        }
        mCacheData = data;
        useHardwareAccelerated();
        super.setContentView(R.layout.eskit_single_task_root);

        mPermissionRequestHelper = new PermissionRequestHelper();

        mPageLoader = new FragmentPageLoader(this);
        mPageLoader.newApp(data);

        mDebugFocusHelper = new DebugFocusHelper(this);
        mDebugFocusHelper.startListen();
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
        L.logIF("onSaveInstanceState");
        if (mCacheData != null) {
            outState.putParcelable(CACHE_START_DATA, mCacheData);
        }
    }

    public void loadAppFromSelf(EsData data) {
        ThreadUtils.runOnUiThread(() -> {
            mPageLoader.newApp(data);
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent == null) return;
        EsData data = intent.getParcelableExtra("data");
        if (L.DEBUG) L.logI("app_chain activity new intent " + data);
        if (data == null) return;
        if (mPageLoader != null) {
            mPageLoader.updatePage(data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mPageLoader != null) {
            mPageLoader.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mPageLoader != null) {
            mPageLoader.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPageLoader != null) {
            mPageLoader.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mPageLoader != null) {
            mPageLoader.onStop();
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mPageLoader.dispatchKeyEvent(event)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onBackPressed() {
        sendBroadcast(new Intent("eskit.action.start.load.rpk"));
        if (mPageLoader.onBackPressed(super::onBackPressed)) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        sendBroadcast(new Intent("eskit.action.start.load.rpk"));
        if (L.DEBUG) L.logI("app_chain activity destroy");
        if(mPageLoader != null) {
            mPageLoader.onDestroy();
            mPageLoader = null;
        }
        super.onDestroy();
        mPermissionRequestHelper = null;
        if (mDebugFocusHelper != null) {
            mDebugFocusHelper.stopListen();
            mDebugFocusHelper = null;
        }
    }

    public IPageLoader getPageLoader() {
        return mPageLoader;
    }

    /**
     * 请求权限
     **/
    public void requestPermission(String[] permissions, EsCallback<List<String>, Pair<List<String>, List<String>>> callback) {
        if (mPermissionRequestHelper == null) return;
        mPermissionRequestHelper.requestPermission(this, permissions, callback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (mPermissionRequestHelper != null) {
            mPermissionRequestHelper.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public IEsCoverView getCoverView() {
        FrameLayout coverParent = findViewById(R.id.eskit_cover_container);
        if (coverParent != null && coverParent.getChildCount() > 0) {
            View coverView = coverParent.getChildAt(0);
            if (coverView instanceof IEsCoverView) {
                return (IEsCoverView) coverView;
            }
        }
        return null;
    }
}
