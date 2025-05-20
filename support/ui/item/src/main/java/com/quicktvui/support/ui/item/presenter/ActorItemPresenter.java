package com.quicktvui.support.ui.item.presenter;

import android.util.TypedValue;

import com.quicktvui.support.ui.item.host.ItemHostView;
import com.quicktvui.support.ui.item.widget.AbsWidget;
import com.quicktvui.support.ui.item.widget.ActorBottomTitleWidget;
import com.quicktvui.support.ui.item.widget.ActorNumberWidget;
import com.quicktvui.support.ui.item.widget.ActorTagWidget;
import com.quicktvui.support.ui.item.widget.BuilderWidget;
import com.quicktvui.support.ui.item.widget.IActorTagWidget;
import com.quicktvui.support.ui.item.widget.INumberIndexWidget;
import com.quicktvui.support.ui.item.widget.ITitleWidget;
import com.quicktvui.support.ui.item.widget.LazyWidgetsHolder;
import com.quicktvui.support.ui.item.widget.RoundFocusBorderWidget;
import com.quicktvui.support.ui.item.widget.RoundShadowWidget;

import com.quicktvui.support.ui.item.R;


public  class ActorItemPresenter extends SimpleItemPresenter {

    public static int TASK_TEXT = 4;


    /**
     * ActorItemPresenter的Builder
     */
    public static class Builder extends SimpleItemPresenter.Builder{

//        private int diameter = 212;
        /**
         * 设置演员头像半径
         * @param diameter
         * @return
         */
        @Deprecated
        public Builder setImageDiameter(int diameter){
//            this.diameter = diameter;
            return this;
        }

    }

    private final Builder mBuilder;

    @Override
    protected void onWidgetInitialized(final AbsWidget widget, LazyWidgetsHolder lwh) {
        super.onWidgetInitialized(widget,lwh);
        if(widget instanceof ActorBottomTitleWidget){
            final ActorBottomTitleWidget aw = (ActorBottomTitleWidget) widget;
            aw.onFocusChange(false);
            aw.setBackGroundResource(R.drawable.translucent_bg);

            aw.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        }
        if(widget instanceof INumberIndexWidget){
            widget.setZOrder(Z_ORDER_BORDER + 1);
        }

    }


    protected BuilderWidget.Builder onCreateCoverBuilder(final LazyWidgetsHolder holder){
        return  new ActorCoverWidget.Builder(context,holder.view);
    }

    protected BuilderWidget.Builder onCreateNumberIndexBuilder(final LazyWidgetsHolder holder){
        return  new ActorNumberWidget.Builder(context);
    }
    protected BuilderWidget.Builder onCreateShadowBuilder(final LazyWidgetsHolder holder){
        return  new RoundShadowWidget.Builder(context);
    }
    protected BuilderWidget.Builder onCreateFocusBorderBuilder(final LazyWidgetsHolder holder){
        return  new RoundFocusBorderWidget.Builder(context);
    }



    @Override
    protected void onRegisterWidgetBuilder(final LazyWidgetsHolder holder) {

        super.onRegisterWidgetBuilder(holder);

        final ActorBottomTitleWidget.Builder bottomBuilder = new ActorBottomTitleWidget.Builder(context);

        final ActorTagWidget.Builder tagBuilder = new ActorTagWidget.Builder(context);

        tagBuilder.setZOrder(Z_ORDER_BORDER + 1);
        bottomBuilder.setZOrder(Z_ORDER_BORDER + 1);

        holder.registerLazyWidget(ITitleWidget.NAME,bottomBuilder);//1
        holder.registerLazyWidget(IActorTagWidget.NAME,tagBuilder);//3
    }

    @Override
    protected void onHostViewFocusChanged(ItemHostView hostView, boolean hasFocus, LazyWidgetsHolder widgetsHolder) {
        super.onHostViewFocusChanged(hostView, hasFocus, widgetsHolder);
        widgetsHolder.getWidget(IActorTagWidget.NAME).onFocusChange(hasFocus);
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
                final IActorTagWidget tag = holder.getWidget(IActorTagWidget.NAME);
                tag.setActorTag(iModel.getActorTag());
                tag.setVisible(true);
                break;

                default: break;
        }
    }


    public ActorItemPresenter(Builder mBuilder) {
        super(mBuilder);
        this.mBuilder = mBuilder;
    }

    public ActorItemPresenter() {
        this((Builder) new Builder().setImgPlaceHolder(R.drawable.ic_common_def_placeholder_circle));
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        final LazyWidgetsHolder lzh = (LazyWidgetsHolder) viewHolder;
        //发现组件
        final IActorTagWidget tag = lzh.getWidget(IActorTagWidget.NAME);
        final ITitleWidget bt = lzh.getWidget(ITitleWidget.NAME);
        final IModel iModel = (IModel) item;
        //取消之前任务
        bt.setTitle(null);
        //先设置成默认状态
        tag.setVisible(false);

        super.onBindViewHolder(viewHolder,item);

        lzh.getLazyWorker().execute(TASK_TEXT,new Runnable(){
            @Override
            public void run() {
                bt.setTitle(iModel.getTitle());
            }
        },400);
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
        String getTitle();

        /**
         * 演员的Tag(导演）
         */
        String getActorTag();

    }
}



