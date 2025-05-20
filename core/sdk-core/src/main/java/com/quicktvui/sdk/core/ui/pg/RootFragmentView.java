package com.quicktvui.sdk.core.ui.pg;

import static com.quicktvui.sdk.core.utils.CommonUtils.getRepositoryHost;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.sunrain.toolkit.utils.ThreadUtils;
import com.sunrain.toolkit.utils.ToastUtils;
import com.sunrain.toolkit.utils.log.L;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.HippyRootView;

import com.quicktvui.sdk.base.EsEventPacket;
import com.quicktvui.sdk.base.EsException;
import com.quicktvui.sdk.base.PromiseHolder;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.cover.IEsCoverView;
import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.R;
import com.quicktvui.sdk.core.internal.Constants;
import com.quicktvui.sdk.core.internal.EsContainerTaskV2;
import com.quicktvui.sdk.core.internal.EsContext;
import com.quicktvui.sdk.core.internal.EsViewManager;
import com.quicktvui.sdk.core.internal.IEsAppLoadCallback;
import com.quicktvui.sdk.core.internal.IEsAppLoadHandler;
import com.quicktvui.sdk.core.internal.IEsViewer;
import com.quicktvui.sdk.core.internal.loader.RecordInfo;
import com.quicktvui.sdk.core.pm.EsPageView;
import com.quicktvui.sdk.core.pm.IEsPageView;
import com.quicktvui.sdk.core.sf.UsageManager;
import com.quicktvui.sdk.core.sf.db.entity.UsageRecord;
import com.quicktvui.sdk.core.ui.BrowserBaseActivity;
import com.quicktvui.sdk.core.utils.DebugLog;
import com.quicktvui.sdk.core.utils.PluginUtils;
import com.quicktvui.sdk.core.views.EsDefaultCoverView;

import java.io.File;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * <br>
 * 注释
 * <br>
 * <br>
 * Created by WeiPeng on 2023-08-18 11:09
 */
public class RootFragmentView implements IEsViewer, IEsAppLoadCallback {

    private static final int TIME_DELAY_UPLOAD_LOG = EsDefaultCoverView.SHOW_UPLOAD_LOG; // 自动上传log

    private Activity mActivity;
    private FrameLayout mViewContainer;
    private FrameLayout mCoverContainer;
    private EsData mData;
    private final int mDefaultBackgroundColor;

    private EsContainerTaskV2 mContainerTask;
    private FragmentManager mFragmentManager;
    private List<SubFragmentView> mSubViews = new LinkedList<>();

    public RootFragmentView(FragmentActivity context, FrameLayout viewContainer, FrameLayout coverContainer, EsData data) {
        mActivity = context;
        mFragmentManager = context.getSupportFragmentManager();
        mViewContainer = viewContainer;
        mCoverContainer = coverContainer;
        mData = data;
        setupBackground(data);
        setupCoverView(data);
        mDefaultBackgroundColor = mData.isTransparent() ? Color.TRANSPARENT : ContextCompat.getColor(context, R.color.color_es_default_black_bg);
    }

    public void startLoad() {
        mContainerTask = new EsContainerTaskV2(mActivity, this);
        EsViewManager.get().onViewCreate(this);
        mContainerTask.startLoad(mData);
    }

    /**
     * 添加subview
     **/
    public void addSubView(SubFragmentView newPage) {
        mSubViews.add(newPage);
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.setReorderingAllowed(true);

        // 隐藏之前的Fragment
        SubFragmentView oldPage = (SubFragmentView) mFragmentManager.getPrimaryNavigationFragment();
        if (L.DEBUG) L.logD("add : " + newPage + ", hide: " + oldPage);
        if (oldPage != null) {
            if (!newPage.getPageView().isDialogMode()) {
                if (!oldPage.getPageView().isDialogMode()) { // Dialog上弹Dialog允许继续弹
                    ft.hide(oldPage);
                }
            }
            oldPage.beforeHide();
        }

        ft.add(mViewContainer.getId(), newPage, String.valueOf(newPage.getPageId()));
        ft.setPrimaryNavigationFragment(newPage);
        ft.commitNowAllowingStateLoss();
    }

    /**
     * 删除subview
     **/
    public void removeSubView(IEsPageView pv) {
        SubFragmentView remove = null;
        SubFragmentView show = null;
        for (SubFragmentView view : mSubViews) {
            if (view.getPageView() == pv) {
                mSubViews.remove(view);
                remove = view;
                break;
            }
        }
        if (!mSubViews.isEmpty()) {
            show = mSubViews.get(mSubViews.size() - 1);
        }
        commitRemove(remove, show);
    }

    /**
     * 显示顶层subview
     **/
    public void showTopSubView() {
        if (!mSubViews.isEmpty()) {
            SubFragmentView show = mSubViews.get(mSubViews.size() - 1);
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            ft.setReorderingAllowed(true);
            ft.show(show);
            ft.setPrimaryNavigationFragment(show);
            ft.commitNowAllowingStateLoss();
            onStartFake();
            onResumeFake();
        }
    }

    private void commitRemove(SubFragmentView remove, SubFragmentView show) {
        if (remove == null) {
            return;
        }
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.setReorderingAllowed(true);

        if (L.DEBUG) L.logD("remove: " + remove + " show: " + show);
        ft.remove(remove);

        // 显示上一个Fragment
        if (show != null) {
            if (show.isHidden()) {
                ft.show(show);
            } else {
                show.afterShow();
            }
            ft.setPrimaryNavigationFragment(show);
        }
        ft.commitNowAllowingStateLoss();
    }

    public SubFragmentView createSubView(EsPageView pv) {
//        SubFragmentView subView = (SubFragmentView) mFragmentManager.getFragmentFactory()
//                .instantiate(mActivity.getClassLoader(), FragmentPageLoader.SUB_VIEW_NAME);
        SubFragmentView subView = new SubFragmentView();
        subView.setPageView(pv);
        return subView;
    }

    //region 生命周期

    public void onStartFake() {
        EsViewManager.get().onViewStart(this);
    }

    public void onResumeFake() {
        mContainerTask.onResume();
        EsViewManager.get().onViewResume(this);
    }

    public void onPauseFake() {
        EsViewManager.get().onViewPause(this);
        mContainerTask.onPause();
    }

    public void onStopFake() {
        EsViewManager.get().onViewStop(this);
    }

    //endregion

    //region IEsViewer

    @Override
    public Context getAppContext() {
        return mActivity;
    }

    @Override
    public EsContainerTaskV2 getTaskContainer() {
        return mContainerTask;
    }

    @Override
    public void toFinish() {
        ThreadUtils.runOnUiThread(() -> {
            // 页面关闭的时候不主动移除Fragment，因为Activity会大概延迟200ms关闭，造成3帧左右的背景延迟消失
//            commitRemove(mSubViews, null);
            destroy();
            if (mActivity instanceof BrowserBaseActivity) {
                IPageLoader loader = ((BrowserBaseActivity) mActivity).getPageLoader();
                loader.onAppFinish(this);
            }
            mActivity = null;
        });
    }

    private void destroy() {
        if (L.DEBUG) L.logD("app_chain destroy");
        onPauseFake();
        onStopFake();
        EsViewManager.get().onViewDestroy(this);
        EsContext.get().postDelay(() -> {
            if (mContainerTask != null) {
                mContainerTask.destroy();
                mContainerTask = null;
                ViewParent parent = mViewContainer.getParent();
                if (parent instanceof ViewGroup) {
                    ((ViewGroup) parent).removeView(mViewContainer);
                }
                mViewContainer = null;
                mCoverContainer = null;
            }
        }, 600);

        mData = null;
        mSubViews.clear();
        mSubViews = null;
        mFragmentManager = null;
    }

    @Nullable
    @Override
    public EsData getEsData() {
        return mData;
    }

    @Nullable
    @Override
    public HippyEngineContext getEngineContext() {
        return mContainerTask.getEngineContext();
    }

    @Override
    public void sendUIEvent(int viewId, String eventName, Object params) {
        mContainerTask.sendUIEvent(viewId, eventName, params);
    }

    @Override
    public void sendNativeEvent(String eventName, Object params) {
        mContainerTask.sendNativeEvent(eventName, params);
    }

    @Nullable
    @Override
    public File getAppRuntimeDir() {
        return mContainerTask.getAppRuntimeDir();
    }


    //endregion

    //region IEsAppLoadCallback

    @Override
    public void onStartLoad(IEsAppLoadHandler handler) {

    }

    private void printView(View view) {
        if (view instanceof ViewGroup) {
            int count = ((ViewGroup) view).getChildCount();
            DebugLog.d(this, view.getClass().getSimpleName() + " " + count);
            for (int i = 0; i < count; i++) {
                printView(((ViewGroup) view).getChildAt(i));
            }
        } else {
            DebugLog.d(this, view.getClass().getSimpleName());
        }
    }

    @Override
    public void onViewLoaded(HippyRootView view) {
        view.setBackgroundColor(mDefaultBackgroundColor);
        mViewContainer.removeAllViews();
        mViewContainer.addView(view);
        IEsCoverView coverView = getCoverView();
        if (coverView != null) {
            coverView.onEsRenderSuccess();
        }
    }

    @Override
    public void onLoadError(EsException e) {
        int code = e.getCode();

        // 鉴权失败
        if (code == Constants.ERR_AUTH_FAILED) {
            L.logEF("auth failed");
            ToastUtils.showShort(e.getMessage());
            EsViewManager.get().finishAllAppPage();
            return;
        }

        // 底座版本太低
        if(code == Constants.ERR_VER_MATCH_FAIL && PluginUtils.isClientHandlePluginUpgrade()) {
            showErrorInfoWithDefaultCover(e);
            return;
        }

        // 应用被封禁
        if (code == Constants.ERR_APP_BLOCK) {
            L.logEF("app blocked");
            processAppBlock();
        }

//        showErrorInfoWithDefaultCover(e);
        showErrorInfoWithRpk(e);
    }

    @Override
    public void onEsAppEvent(EsEventPacket packet) {

    }

    @Override
    public void requestFinish() {

    }

    @Override
    public void onAppOpened(final RecordInfo recordInfo) {
        if (recordInfo != null) {
            if (L.DEBUG) L.logD("onAppOpened - 记录打开的应用信息 : " + recordInfo);
            ThreadUtils.executeByIo(new ThreadUtils.SimpleTask<Boolean>() {
                @Override
                public Boolean doInBackground() throws Throwable {
                    return UsageManager.get().insertRecord(UsageRecord.create(recordInfo));
                }

                @Override
                public void onSuccess(Boolean result) {

                }
            });
        }
    }

    //endregion

    /**
     * 应用被封禁
     **/
    private void processAppBlock() {
        if (mContainerTask != null) {
            mContainerTask.deleteApp();
            mContainerTask.destroy();
        }

        if (mViewContainer != null) {
            mViewContainer.removeAllViews();
        }
    }

    private void setupBackground(EsData data) {
        int backgroundColor = data.getBackgroundColor();
        if (backgroundColor == -1) {
            if (data.isTransparent()) {
                backgroundColor = Color.TRANSPARENT;
            } else {
                backgroundColor = ContextCompat.getColor(mActivity, R.color.color_es_default_bg);
            }
        }
        try {
            mActivity.getWindow().setBackgroundDrawable(new ColorDrawable(backgroundColor));
        } catch (Exception e) {
            L.logW("setup background", e);
        }
    }

    private void setupCoverView(EsData data) {
        int coverLayoutId = data.getCoverLayoutId();
        if (coverLayoutId < 0) return;

        View coverView;
        if (coverLayoutId == EsData.SPLASH_DEFAULT ||
                coverLayoutId == EsData.SPLASH_NO_HEADER) {
            coverView = new EsDefaultCoverView(mActivity);
        } else {
            coverView = LayoutInflater.from(mActivity)
                    .inflate(coverLayoutId, null);
        }

        if (coverView instanceof IEsCoverView) {
            Serializable coverData = data.getCoverLayoutParams();
            if (coverLayoutId != EsData.SPLASH_NO_HEADER && coverData == null) {
                EsMap params = new EsMap();
                params.pushString(Constants.K_APP_PACKAGE, data.getEsPackage());
                params.pushString(Constants.K_APP_REPO, getRepositoryHost(data));
                params.pushString(Constants.K_APP_VERSION, data.getEsVersion());
                params.pushString(Constants.K_APP_NAME, data.getName());
                params.pushString(Constants.K_APP_ICON, data.getIcon());
                coverData = params;
            }
            ((IEsCoverView) coverView).onInit(coverData);
        } else {
            L.logEF("CoverView需要实现IEsCoverView接口");
        }
        mCoverContainer.addView(coverView);
    }

    public IEsCoverView getCoverView() {
        int childCount = mCoverContainer.getChildCount();
        if (childCount > 1) {
            L.logEF("多个CoverView！！！！！！！！！！");
            return null;
        }
        if (childCount == 1) {
            View childView = mCoverContainer.getChildAt(0);
            if (childView instanceof IEsCoverView) {
                return (IEsCoverView) childView;
            }
        }
        return null;
    }

    /**
     * 使用默认Cover显示错误信息
     **/
    private void showErrorInfoWithDefaultCover(EsException e) {
        IEsCoverView coverView = getCoverView();
        L.logIF("showErrorInfoWithDefaultCover " + coverView);
        if (coverView != null) {
            coverView.onEsRenderFailed(e);
        }
    }

    /**
     * 使用快应用显示错误信息
     **/
    private void showErrorInfoWithRpk(EsException e) {
        if (mContainerTask != null) {
            if (mContainerTask.isNexusLoader()) { // nexus使用内置的错误页面
                showErrorInfoWithDefaultCover(e);
                return;
            }
            EsData currentErrRpk = mContainerTask.getEsData();
            mContainerTask.destroy();
            // 报错的快应用是报错rpk本身，使用原有的错误页面，避免死循环
            if (currentErrRpk != null
                    && Constants.ES_ERROR_RPK_PACKAGE
                    .equals(currentErrRpk.getEsPackage())) {
                showErrorInfoWithDefaultCover(e);
                return;
            }
        }
        EsData data = new EsData();
        data.setAppPackage(Constants.ES_ERROR_RPK_PACKAGE);
        EsMap map = new EsMap();
        map.pushInt("errorCode", e.getCode());
        map.pushString("message", e.getMessage());
        map.pushMap("data", e.getData());
        data.setArgs(PromiseHolder.create()
                .put("params", map)
                .getData());
        mContainerTask = new EsContainerTaskV2(mActivity, this);
        mContainerTask.startLoad(data);
    }

    @Override
    public String toString() {
        return "RootFragmentView{ " +
                hashCode() +
                " }";
    }
}
