package com.quicktvui.support.ui.item.presenter;



import android.support.annotation.Nullable;

import com.quicktvui.support.ui.item.host.ItemHostView;
import com.quicktvui.support.ui.item.widget.AbsWidget;
import com.quicktvui.support.ui.item.widget.BuilderWidget;
import com.quicktvui.support.ui.item.widget.CoverWidget;
import com.quicktvui.support.ui.item.widget.ITitleWidget;
import com.quicktvui.support.ui.item.widget.LazyWidgetsHolder;

import com.quicktvui.support.ui.item.R;

import com.quicktvui.support.ui.item.widget.TitleWidget;

@Deprecated
public  class CommonItemPresenter extends SimpleItemPresenter {

    public static int TASK_TEXT = 4;


    public static class Builder extends SimpleItemPresenter.Builder{

        public Builder() {
            setImgPlaceHolder(R.drawable.ic_common_def_placeholder_toprounder);
        }

        @Override
        public CommonItemPresenter build(){
            return new CommonItemPresenter(this);
        }
    }


    public CommonItemPresenter() {
        this(new Builder());
    }

    public CommonItemPresenter(Builder builder) {
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
            widget.onFocusChange(false);
        }

    }

    @Override
    protected BuilderWidget.Builder onCreateCoverBuilder(LazyWidgetsHolder holder) {
        return new CoverWidget.Builder(context,holder.view);
    }

    @Override
    protected void onRegisterWidgetBuilder(final LazyWidgetsHolder holder) {

        super.onRegisterWidgetBuilder(holder);

        final TitleWidget.Builder bottomBuilder = new TitleWidget.Builder(context);


        bottomBuilder.setZOrder(Z_ORDER_BORDER - 1);

        holder.registerLazyWidget(ITitleWidget.NAME,bottomBuilder);//1

    }



    @Override
    protected void onHostViewFocusChanged(ItemHostView hostView, boolean hasFocus, LazyWidgetsHolder widgetsHolder) {
        super.onHostViewFocusChanged(hostView, hasFocus, widgetsHolder);
        widgetsHolder.getWidget(ITitleWidget.NAME).onFocusChange(hasFocus);
    }

    @Override
    protected void  onExecuteTask(final LazyWidgetsHolder holder, Object item, int taskID){
        final IModel iModel = (IModel) item;
        switch (taskID){
            case TASK_FOCUS_WIDGET :
                ((ITitleWidget)holder.getWidget(ITitleWidget.NAME)).setVisible(true);
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
        final ITitleWidget title = lzh.getWidget(ITitleWidget.NAME);
        if(title != null) {
            title.setTitle(null);
        }
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



