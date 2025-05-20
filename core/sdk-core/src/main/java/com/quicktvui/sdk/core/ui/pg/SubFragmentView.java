package com.quicktvui.sdk.core.ui.pg;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunrain.toolkit.utils.ReflectUtils;
import com.sunrain.toolkit.utils.log.L;

import com.quicktvui.sdk.core.pm.EsPageView;

/**
 * 加载pageView
 * Created by WeiPeng on 2023-08-18 15:55
 */
public class SubFragmentView extends Fragment {

    private EsPageView mPageView;

    public void setPageView(EsPageView view) {
        mPageView = view;
    }

    public EsPageView getPageView() {
        return mPageView;
    }

    public int getPageId() {
        if(mPageView == null) return -1;
        return mPageView.getPageId();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mPageView == null) {
            return null;
        }
        return mPageView.getView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            afterShow();
        }
    }

    public void afterShow() {
        if (mPageView != null) mPageView.notifyAfterShow();
    }

    public void beforeHide() {
        if (mPageView != null) mPageView.notifyBeforeHide();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            SparseArray<Object> tags = ReflectUtils.reflect(mPageView).field("mKeyedTags").get();
            tags.clear();
        } catch (Exception e) {
            L.logEF("clear tags", e);
        }
        mPageView = null;
        if (L.DEBUG) L.logD("onDestroy");
    }

    private String getDialogMode(){
        if(mPageView == null) return "unknown";
        return String.valueOf(mPageView.isDialogMode());
    }

    @Override
    public String toString() {
        return "SubView{ id " + getPageId() + " dialog " + getDialogMode() + " hide: " + isHidden() +  " }";
    }
}
