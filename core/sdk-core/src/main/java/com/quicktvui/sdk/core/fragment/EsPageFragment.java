package com.quicktvui.sdk.core.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunrain.toolkit.utils.log.L;

import com.quicktvui.sdk.core.pm.EsPageView;

/**
 * <br>
 *
 * <br>
 */
public class EsPageFragment extends EsBaseFragment {

    private EsPageView mPageView;

    public EsPageFragment() {
        super();
    }

    @SuppressLint("ValidFragment")
    public EsPageFragment(EsPageView view) {
        super();
        mPageView = view;
    }

    public int getPageId() {
        if (mPageView != null) {
            return mPageView.getPageId();
        }
        return -1;
    }

    public EsPageView getPageView(){
        return mPageView;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (!mPageView.isDialogMode()) {
            mPageView.setBackgroundColor(Color.parseColor("#0E0E0E"));
        }
        if (L.DEBUG) L.logD("page_render " + mPageView.getPageName());
        return mPageView;
    }

    public void saveFocus() {
        View view = getView();
        if (view != null) {
            mPageView.notifyBeforeHide();
        }
    }

    public void resetFocus() {
        View view = getView();
        if (view != null && mPageView != null) {
            mPageView.notifyAfterShow();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPageView = null;
    }
}
