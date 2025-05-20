package com.quicktvui.support.ui.item.presenter;

import com.quicktvui.support.ui.item.host.ItemHostView;
import com.quicktvui.support.ui.item.widget.ITipWidget;
import com.quicktvui.support.ui.item.widget.ITitleWidget;
import com.quicktvui.support.ui.item.widget.LazyWidgetsHolder;

import com.quicktvui.support.ui.leanback.Presenter;

import com.quicktvui.support.ui.item.R;

import com.quicktvui.support.ui.item.widget.TipWidget;

public class TipWidgetPlugin extends SimpleItemPresenter.PluginImpl {

    private static final String TAG = "TipWG";

    private int marginBottom = -1;
    private int marginLeft= -1;

    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    @Override
    public void onCreatePresenter(SimpleItemPresenter presenter, SimpleItemPresenter.Builder builder) {
        super.onCreatePresenter(presenter, builder);
    }



    @Override
    public void onRegisterWidgetBuilder(LazyWidgetsHolder widgetsHolder) {
        super.onRegisterWidgetBuilder(widgetsHolder);
        TipWidget.Builder builder = new TipWidget.Builder(widgetsHolder.getItemHostView().getHostView().getContext());
        widgetsHolder.registerLazyWidget(ITipWidget.NAME,builder);
        builder.setZOrder(SimpleItemPresenter.Z_ORDER_BORDER + 10);
        if(marginBottom < 0){
            this.marginBottom = (int) widgetsHolder.getContext().getResources().getDimension(R.dimen.tip_margin_bottom);
        }
        if(marginLeft < 0){
            this.marginLeft = (int) widgetsHolder.getContext().getResources().getDimension(R.dimen.tip_margin_bottom);
        }
    }

    @Override
    public void onBindViewHolder(Presenter.ViewHolder viewHolder, Object item) {
        super.onBindViewHolder(viewHolder, item);

        final LazyWidgetsHolder lzh = (LazyWidgetsHolder) viewHolder;
        if(item instanceof ITipWidget.IModel) {
            final ITipWidget.IModel m = (ITipWidget.IModel) item;
            if (lzh.getWidget(ITipWidget.NAME) != null) {
                final ITipWidget tw = lzh.getWidget(ITipWidget.NAME);
                tw.setTip(m.getTipString());
            }
        }
    }

    @Override
    public void onItemHostViewSizeChanged(LazyWidgetsHolder lazyWidgetsHolder, ItemHostView hostView, int width, int height) {
        super.onItemHostViewSizeChanged(lazyWidgetsHolder, hostView, width, height);
        final ITipWidget tw = lazyWidgetsHolder.getWidget(ITipWidget.NAME);

        if(height > 0 && tw != null) {
            int marginBottom = this.marginBottom;

            if (lazyWidgetsHolder.getWidget(ITitleWidget.NAME) != null) {
                final ITitleWidget bottomBarWidget = lazyWidgetsHolder.getWidget(ITitleWidget.NAME);
                marginBottom += bottomBarWidget.height();
            }
            //更新tip的位置
            tw.getRenderNode().setPosition(marginLeft,height - marginBottom - tw.height());
        }
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
        super.onUnbindViewHolder(viewHolder);
        final LazyWidgetsHolder lzh = (LazyWidgetsHolder) viewHolder;
        final ITipWidget tw = lzh.getWidget(ITipWidget.NAME);
        if(tw != null){
            tw.setTip(null);
        }
    }


}
