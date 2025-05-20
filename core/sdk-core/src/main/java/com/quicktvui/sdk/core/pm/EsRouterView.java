package com.quicktvui.sdk.core.pm;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;

import com.quicktvui.base.ui.FocusDispatchView;
import com.quicktvui.base.ui.IPageRootView;
import com.tencent.mtt.hippy.HippyInstanceContext;
import com.tencent.mtt.hippy.uimanager.HippyViewBase;
import com.tencent.mtt.hippy.uimanager.NativeGestureDispatcher;

import com.quicktvui.sdk.core.internal.EsAppLoadHandlerImpl;
import com.quicktvui.sdk.core.internal.EsAppLoadHandlerImplV2;
import com.quicktvui.sdk.core.internal.EsViewManager;
import com.quicktvui.sdk.core.internal.IEsViewer;
import com.quicktvui.sdk.core.ui.BrowserBaseActivity;
import com.quicktvui.sdk.core.ui.pg.IPageLoader;
import com.quicktvui.sdk.core.ui.pg.RootFragmentView;

public class EsRouterView extends FocusDispatchView implements HippyViewBase, IPageRootView {

    private final SparseArray<EsPageView> mViews = new SparseArray<>();

    @Override
    public View getPageContentView() {
        if (mViews.size() > 0) {
            return mViews.get(mViews.size() - 1).getView();
        }
        return null;
    }

    public EsRouterView(Context context) {
        super(context);
        EsViewManager.get().markObject2Engine(this, ((HippyInstanceContext)context).getEngineContext());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Context context = getContext();
        if (context instanceof HippyInstanceContext) {
            EsViewManager.get().markObject2Engine(this, ((HippyInstanceContext) context).getEngineContext());
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EsViewManager.get().unMarkObject2Engine(this);
    }

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
    }

    public void open(EsPageView pv) {
        mViews.append(pv.getPageId(), pv);
        IEsViewer topViewer = EsViewManager.get().findPageWithObject(this);
        if (topViewer instanceof EsAppLoadHandlerImpl) {
            ((EsAppLoadHandlerImpl) topViewer).addSubPage(pv);
            return;
        }
        if (topViewer instanceof EsAppLoadHandlerImplV2) {
            ((EsAppLoadHandlerImplV2) topViewer).onPageOpen(pv);
            return;
        }
        if (topViewer instanceof RootFragmentView) {
            IPageLoader loader = ((BrowserBaseActivity) topViewer.getAppContext()).getPageLoader();
            if (loader != null) {
                loader.addPage(pv);
            }
        }
    }

    public void close(int id) {
        EsPageView pv = mViews.get(id);
        mViews.remove(id);
        IEsViewer topViewer = EsViewManager.get().findPageWithObject(this);
        if (topViewer instanceof EsAppLoadHandlerImpl) {
            ((EsAppLoadHandlerImpl) topViewer).removeSubPage(pv);
            return;
        }
        if (topViewer instanceof EsAppLoadHandlerImplV2) {
            ((EsAppLoadHandlerImplV2) topViewer).onPageClose(pv);
            return;
        }
        if (topViewer instanceof RootFragmentView) {
            IPageLoader loader = ((BrowserBaseActivity) topViewer.getAppContext()).getPageLoader();
            if (loader != null) {
                loader.removePage(pv);
            }
        }
    }

    @Override
    public NativeGestureDispatcher getGestureDispatcher() {
        return null;
    }

    @Override
    public void setGestureDispatcher(NativeGestureDispatcher dispatcher) {

    }

    @Override
    public String toString() {
        return "EsRouterView{" +
                "id=" + getId() +
                '}';
    }
}
