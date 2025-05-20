package com.quicktvui.support.ui.item.presenter;


import android.support.annotation.Nullable;


import com.quicktvui.support.ui.item.host.ItemHostView;
import com.quicktvui.support.ui.item.widget.AbsWidget;
import com.quicktvui.support.ui.item.widget.ITitleWidget;
import com.quicktvui.support.ui.item.widget.LazyWidgetsHolder;

import com.quicktvui.support.ui.item.R;


import com.quicktvui.support.ui.item.widget.TitleWidget;



public  class TitleItemPresenter extends SimpleItemPresenter {

    public static int TASK_TEXT = 4;


    public static class Builder extends SimpleItemPresenter.Builder{

        TitleWidget.Builder titleWidgetBuilder;


        public Builder setTitleWidgetBuilder(TitleWidget.Builder titleWidgetBuilder) {
            this.titleWidgetBuilder = titleWidgetBuilder;
            return this;
        }

        public Builder() {
            setImgPlaceHolder(R.drawable.ic_default_placeholder_img);
        }

        @Override
        public TitleItemPresenter build(){
            return new TitleItemPresenter(this);
        }
    }


    public TitleItemPresenter() {
        this(new Builder());
    }

    public TitleItemPresenter(Plugin plugin) {
        this((Builder)new Builder().setPlugin(plugin));
    }

    public TitleItemPresenter(Builder builder) {
        super(builder);
    }

    @Override
    public Builder getBuilder() {
        return (Builder) super.getBuilder();
    }

    @Override
    protected void onWidgetInitialized(AbsWidget widget, LazyWidgetsHolder lwh) {
        super.onWidgetInitialized(widget,lwh);
        if(widget instanceof ITitleWidget){
            widget.setZOrder(Z_ORDER_MISC);
            widget.onFocusChange(false);
        }

    }



    @Override
    protected void onRegisterWidgetBuilder(final LazyWidgetsHolder holder) {

        super.onRegisterWidgetBuilder(holder);
        final TitleWidget.Builder bd =  getBuilder().titleWidgetBuilder;
        holder.registerLazyWidget(ITitleWidget.NAME, bd);//1
    }


    @Override
    protected void onHostViewFocusChanged(ItemHostView hostView, boolean hasFocus, LazyWidgetsHolder widgetsHolder) {
        super.onHostViewFocusChanged(hostView, hasFocus, widgetsHolder);
        final AbsWidget holder = widgetsHolder.getWidget(ITitleWidget.NAME);
        if(holder != null){
            holder.onFocusChange(hasFocus);
        }
    }

    @Override
    protected void  onExecuteTask(final LazyWidgetsHolder holder, Object item, int taskID){
        final IModel iModel = (IModel) item;
        switch (taskID){
            case TASK_FOCUS_WIDGET :
                if(holder.getWidget(ITitleWidget.NAME) != null) {
                    ((ITitleWidget) holder.getWidget(ITitleWidget.NAME)).setVisible(true);
                }
                break;
            case TASK_MISC :

                break;

                default: break;
        }
    }



    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        final LazyWidgetsHolder lzh = (LazyWidgetsHolder) viewHolder;
        //发现组件
        final ITitleWidget bt = lzh.getWidget(ITitleWidget.NAME);

        final IModel iModel = (IModel) item;
        //先设置成默认状态

        super.onBindViewHolder(viewHolder,item);
        if(bt != null) {
            bt.setTitle(iModel.getTitle());
            bt.setSubTitle(iModel.getSubTitle());
        }

    }

    @Override
    protected void onItemHostViewSizeChanged(LazyWidgetsHolder lzh, ItemHostView hostView, int width, int height) {
        super.onItemHostViewSizeChanged(lzh, hostView, width, height);
//        if (hostView != null) {
//            final ITitleWidget bt = lzh.getWidget(ITitleWidget.NAME);
//            if(bt != null) {
//                bt.notifyParentHeightChange(height);
//                final ICoverWidget coverWidget = lzh.getWidget(ICoverWidget.NAME);
//                if(coverWidget != null){
//                    coverWidget.setSize(width,height - bt.height());
//                }
//            }
//        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
//        Log.d("MYCommonItemPresenter ","-----viewHolder unbind------: "+viewHolder+" tt "+tt--);
        //取消之前的任务，做好回收工作
        final LazyWidgetsHolder lzh = (LazyWidgetsHolder) viewHolder;

        super.onUnbindViewHolder(viewHolder);
    }


    /**
     * Created by HuangYong on 2018/9/10.
     */
    public interface IModel extends SimpleItemPresenter.IModel{

        /**
         * item bar上的文本
         * @return
         */
        @Nullable String getTitle();

        @Nullable String getSubTitle();
        /**
         * 获取分数
         * @return
         */
        @Nullable String getScore();

    }
}



