package com.quicktvui.support.ui.largelist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.quicktvui.sdk.base.args.EsMap;

import com.quicktvui.support.ui.R;
import com.quicktvui.support.ui.leanback.Presenter;

public class LoadingPresenter extends Presenter implements TemplatePresenter {

    Template template;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        View v = View.inflate(parent.getContext(), R.layout.loading_layout_presenter_top_down, null);
        v.setFocusable(false);
        if (template != null) {
            v.setLayoutParams(new RecyclerView.LayoutParams(template.width, template.height));
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {

    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {

    }

    @Override
    public void applyProps(EsMap props) {
        if (template == null) {
            template = new Template();
        }
        template.apply(props);
    }

    @Override
    public Presenter getPresenter() {
        return this;
    }

    private static class LoadingView extends FrameLayout {

        public LoadingView(@NonNull Context context) {
            super(context);
        }
    }
}
