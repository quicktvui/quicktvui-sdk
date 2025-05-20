package com.quicktvui.sdk.core.ui.pg;

import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.quicktvui.sdk.base.EsEmptyCallback;
import com.quicktvui.sdk.base.ITakeOverKeyEventListener;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.R;
import com.quicktvui.sdk.core.internal.Constants;
import com.quicktvui.sdk.core.internal.EsContainerTaskV2;
import com.quicktvui.sdk.core.internal.EsViewManager;
import com.quicktvui.sdk.core.pm.EsPageView;
import com.quicktvui.sdk.core.pm.IEsPageView;
import com.sunrain.toolkit.utils.log.L;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <br>
 * 注释
 * <br>
 * <br>
 * Created by WeiPeng on 2023-08-18 11:02
 */
public class FragmentPageLoader extends BasePageLoader {

    private FrameLayout mViewFrameLayout;
    private FrameLayout mCoverFrameLayout;

    private final Stack<RootFragmentView> mApps = new Stack<>();

    public FragmentPageLoader(FragmentActivity activity) {
        super(activity);
        initViews();
    }

    // Integer.MAX_VALUE = 0x7FFFFFFF
    private final static int VIEW_ID_SEED = 0x7EFFFF00;
    private final AtomicInteger mViewIdGenerate = new AtomicInteger(VIEW_ID_SEED);

    private void initViews() {
        mViewFrameLayout = mActivity.findViewById(R.id.eskit_view_container);
        mCoverFrameLayout = mActivity.findViewById(R.id.eskit_cover_container);
    }

    RootFragmentView getTopApp() {
        if (!mApps.isEmpty()) {
            return mApps.peek();
        }
        return null;
    }

    @Override
    public void newApp(EsData data) {
        RootFragmentView lastViewer = getTopApp();
        // 处理生命周期
        if (lastViewer != null) {
            lastViewer.onPauseFake();
            lastViewer.onStopFake();
        }
        // 加载新的Viewer
        FrameLayout appContainer = new FrameLayout(mActivity){
            @Override
            public void addView(View child, int index, ViewGroup.LayoutParams params) {
                // 有些电视(强刷的时候)fragment添加view index是从0开始，导致新添加的view总是在后面
                index = -1;
                super.addView(child, index, params);
                if (L.DEBUG)
                    L.logD("addView: " + index + " " + child.getClass().getSimpleName() + " " + child.hashCode());
                if(L.DEBUG) {
                    View view = findViewById(R.id.es_rooview_version_text);
                    if (view != null) {
                        view.bringToFront();
                    }
                }
            }

            @Override
            public void onViewRemoved(View child) {
                super.onViewRemoved(child);
                if (L.DEBUG) L.logI("sunrain removeView: " + child.getClass().getSimpleName() + " " + child.hashCode());
            }
        };
        appContainer.setId(mViewIdGenerate.getAndIncrement());

        mViewFrameLayout.addView(appContainer);
        RootFragmentView view = new RootFragmentView(mActivity, appContainer, mCoverFrameLayout, data);
        view.startLoad();
        mApps.push(view);
        if (L.DEBUG) L.logI("newApp " + view);
    }

    @Override
    public void addPage(EsPageView pv) {
        RootFragmentView topApp = getTopApp();
        if (topApp == null) {
            L.logEF("add view block, can not find root container");
            return;
        }
        SubFragmentView subView = topApp.createSubView(pv);
        topApp.addSubView(subView);
    }

    @Override
    public void removePage(IEsPageView pv) {
        RootFragmentView topApp = getTopApp();
        if (topApp == null) {
            L.logEF("add view block, can not find root container");
            return;
        }
        topApp.removeSubView(pv);
    }

    @Override
    public void updatePage(EsData data) {
        // 清除所有界面
//        clearTaskIfNeed(data);

        String targetPackage = data.getEsPackage();
        // 找到需要clearTop的app和需要更新的app
        List<RootFragmentView> clearTopApps = new ArrayList<>(5);
        RootFragmentView targetApp = null;
        int size = mApps.size();
        for (int i = size - 1; i >= 0; i--) {
            RootFragmentView app = mApps.get(i);
            EsData appData = app.getEsData();
            if (appData != null) {
                if (!appData.getEsPackage().equals(targetPackage)) {
                    clearTopApps.add(app);
                } else {
                    targetApp = app;
                    break;
                }
            }
        }

        if (targetApp != null) {
            for (RootFragmentView fragmentView : clearTopApps) {
                fragmentView.toFinish();
                mApps.remove(fragmentView);
            }

            RootFragmentView topApp = getTopApp();
            if (topApp != null) {
                topApp.showTopSubView();
            }

            EsMap args = data.getArgs();
            if (args != null) {
                targetApp.sendNativeEvent(Constants.GLOBAL_EVENT.EVT_ON_NEW_INTENT, data.getArgs());
            }
        } else {
            newApp(data);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        ITakeOverKeyEventListener takeOverKey = EsViewManager.get().getTakeOverKeyEventListener(mActivity.hashCode());
        if (takeOverKey != null) {
            L.logIF("take_over check: " + takeOverKey);
        }

        if (takeOverKey != null && takeOverKey.dispatchKeyEvent(event)) {
            return true;
        }
        RootFragmentView topViewer = getTopApp();
        if (topViewer != null) {
            EsMap data = new EsMap();
            data.pushInt("action", event.getAction());
            data.pushInt("keyCode", event.getKeyCode());
            data.pushInt("keyRepeat", event.getRepeatCount());
            topViewer.sendNativeEvent(Constants.GLOBAL_EVENT.EVT_DISPATCH_KEY, data);
        }
        return false;
    }

    @Override
    public boolean onBackPressed(EsEmptyCallback callback) {
        RootFragmentView topViewer = getTopApp();
        if (topViewer != null) {
            EsContainerTaskV2 task = topViewer.getTaskContainer();
            if (task != null) {
                return task.onBackPressed(callback);
            }
        }
        return false;
    }

    @Override
    public void onAppFinish(RootFragmentView app) {
        if (L.DEBUG) L.logD("app_chain finish " + app);
        mApps.remove(app);
        RootFragmentView topApp = getTopApp();
        if (topApp == null) {
            if (!mActivity.isFinishing()) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1 || !mActivity.isDestroyed()) {
                    if (L.DEBUG) L.logD("app_chain finish activity");
                    mActivity.finish();
                }
            }
        } else {
            if (L.DEBUG) L.logD("app_chain show top subview");
            topApp.showTopSubView();
        }
    }

    @Override
    public boolean isAnyAppRunning() {
        return !mApps.isEmpty();
    }

    @Override
    public void onStart() {
        RootFragmentView topApp = getTopApp();
        if (topApp != null) {
            topApp.onStartFake();
        }
    }

    @Override
    public void onResume() {
        RootFragmentView topApp = getTopApp();
        if (topApp != null) {
            topApp.onResumeFake();
        }
    }

    @Override
    public void onPause() {
        RootFragmentView topApp = getTopApp();
        if (topApp != null) {
            topApp.onPauseFake();
        }
    }

    @Override
    public void onStop() {
        RootFragmentView topApp = getTopApp();
        if (topApp != null) {
            topApp.onStopFake();
        }
    }

    @Override
    public void onDestroy() {
        mViewIdGenerate.set(VIEW_ID_SEED);
        if (!mApps.isEmpty()) {
            List<RootFragmentView> apps = new ArrayList<>(mApps);
            for (RootFragmentView app : apps) {
                if (L.DEBUG) L.logD("toFinishApp " + app);
                app.toFinish();
            }
        }
        EsViewManager.get().setTakeOverKeyEventListener(mActivity.hashCode(), null);
        mViewFrameLayout.removeAllViews();
        mCoverFrameLayout.removeAllViews();
        mViewFrameLayout = null;
        mCoverFrameLayout = null;
//        mActivity = null;
    }


}
