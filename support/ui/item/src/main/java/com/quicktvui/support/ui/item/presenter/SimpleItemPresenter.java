package com.quicktvui.support.ui.item.presenter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.request.RequestOptions;
import com.quicktvui.support.ui.item.ItemCenter;
import com.quicktvui.support.ui.item.host.ItemHostView;
import com.quicktvui.support.ui.item.utils.SharedDrawableManager;
import com.quicktvui.support.ui.item.widget.AbsWidget;
import com.quicktvui.support.ui.item.widget.BuilderWidget;
import com.quicktvui.support.ui.item.widget.CoverWidget;
import com.quicktvui.support.ui.item.widget.FocusBorderWidget;
import com.quicktvui.support.ui.item.widget.ICoverWidget;
import com.quicktvui.support.ui.item.widget.IFocusBorderWidget;
import com.quicktvui.support.ui.item.widget.INumberIndexWidget;
import com.quicktvui.support.ui.item.widget.IShadowWidget;
import com.quicktvui.support.ui.item.widget.LazyWidgetsHolder;
import com.quicktvui.support.ui.item.widget.LazyWorker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import com.quicktvui.support.ui.legacy.view.TVFocusScaleExcuter;
import com.quicktvui.support.ui.item.R;

import com.quicktvui.support.ui.item.widget.ShadowWidget;
import com.quicktvui.support.ui.item.widget.ShimmerWidget;
import com.quicktvui.support.ui.render.RenderNode;

public  class SimpleItemPresenter extends BaseItemPresenter {


    public static final int TASK_COVER = 0;
    public static final int TASK_FOCUS_WIDGET = 1;
    public static final int TASK_MISC = 2;

    public static final int Z_ORDER_SHADOW = -100;
    public static final int Z_ORDER_COVER= 200;
    public static final int Z_ORDER_MISC = 300;
    public static final int Z_ORDER_BORDER = 999;
    public static final int Z_ORDER_SHIMMER = 1000;



    public static class Builder{
        private int imgPlaceHolder = R.drawable.ic_default_placeholder_img;
        private float focusScaleX = 1.2f;
        private float focusScaleY = 1.2f;
        private int loadImageDelayTime = 0;
        private int updateFocusWidgetDelayTime = 1000;
        private int updateTagWidgetDelayTime = 600;
        private boolean activeDefaultShadow = false;
        private boolean activeFocusShadow = true;
        private boolean enableBorder = true;
        private boolean enableCover = true;
        private boolean enableShimmer = true;
        private int shimmerHostViewID = -1;
        private RequestOptions requestOptions;
        private int roundCorner = ItemCenter.defaultCornerRadius;
        private boolean enableLoadCoverManual = false;

        public Builder setRoundCorner(int roundCorner) {
            this.roundCorner = roundCorner;
            return this;
        }

        public int getRoundCorner() {
            return roundCorner;
        }

        public Builder setRequestOptions(RequestOptions requestOptions) {
            this.requestOptions = requestOptions;
            return this;
        }

        public RequestOptions getRequestOptions() {
            return requestOptions;
        }


        public Builder setEnableLoadCoverManual(boolean enableLoadCoverManual) {
            this.enableLoadCoverManual = enableLoadCoverManual;
            return this;
        }


        private int hostViewLayout = -1;

        Plugin mPlugin;

        public Builder setPlugin(Plugin mPlugin) {
            this.mPlugin = mPlugin;
            return this;
        }

        public Builder setShimmerHostViewID(int shimmerHostViewID) {
            this.shimmerHostViewID = shimmerHostViewID;
            return this;
        }

        public Builder setImgPlaceHolder(int imgPlaceHolder) {
            this.imgPlaceHolder = imgPlaceHolder;
            return this;
        }
        public Builder enableShimmer(boolean enableShimmer){
            this.enableShimmer = enableShimmer;
            return this;
        }
        public Builder setFocusScale(float focusScale) {
            this.focusScaleX = focusScale;
            this.focusScaleY = focusScale;
            return this;
        }

        public Builder setFocusScaleX(float focusScaleX) {
            this.focusScaleX = focusScaleX;
            return this;
        }

        public Builder enableBorder(boolean enableBorder) {
            this.enableBorder = enableBorder;
            return this;
        }

        public Builder setFocusScaleY(float focusScaleY) {
            this.focusScaleY = focusScaleY;
            return this;
        }

        public Builder setActiveFocusShadow(boolean activeFocusShadow) {
            this.activeFocusShadow = activeFocusShadow;
            return this;
        }

        public Builder enableCover(boolean enableCover) {
            this.enableCover = enableCover;
            return this;
        }

        public Builder setHostViewLayout(int hostViewLayout) {
            this.hostViewLayout = hostViewLayout;
            return this;
        }

        public Builder setUpdateTaskDelayTime(int loadImageDelayTime, int updateFocusWidgetDelayTime, int updateTagWidgetDelayTime){
            this.loadImageDelayTime = loadImageDelayTime;
            this.updateFocusWidgetDelayTime = updateFocusWidgetDelayTime;
            this.updateTagWidgetDelayTime = updateTagWidgetDelayTime;
            return this;
        }

        public Builder setActiveDefaultShadow(boolean activeDefaultShadow) {
            this.activeDefaultShadow = activeDefaultShadow;
            return this;
        }

        public SimpleItemPresenter build(){
            final SimpleItemPresenter si =  new SimpleItemPresenter(this);

            return si;
        }
    }

    private final Builder mBuilder;

    public SimpleItemPresenter(Plugin plugin) {
        this(new Builder().setPlugin(plugin));
    }



    public Builder getBuilder() {
        return mBuilder;
    }

    public SimpleItemPresenter() {
        this(new Builder());
    }

    public SimpleItemPresenter(Builder builder) {
        this.mBuilder = builder;
        onCreatePresenter(builder);
    }

    protected void onCreatePresenter(Builder builder){
        if(builder.mPlugin != null){
            builder.mPlugin.onCreatePresenter(this,builder);
        }
    }

    @Override
    protected ItemHostView onCreateHostView(ViewGroup parent) {
        ItemHostView host = null;
        if(mBuilder.mPlugin != null){
            host = mBuilder.mPlugin.onCreateHostView(parent);
        }
        if(mBuilder.hostViewLayout > 0){
            host = (ItemHostView) View.inflate(parent.getContext(),mBuilder.hostViewLayout,null);
        }
        if(host == null) {
            host = new ItemSimpleHostView(parent.getContext());
        }
        return host;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        return super.onCreateViewHolder(parent);
    }

    protected BuilderWidget.Builder onCreateCoverBuilder(final LazyWidgetsHolder holder){
        return mBuilder.enableCover ?  new CoverWidget.Builder(context,holder.view).setOptions(mBuilder.requestOptions).setRoundCorner(mBuilder.roundCorner) : new EmptyCover.Builder(context);
    }

    protected BuilderWidget.Builder onCreateShimmerBuilder(final LazyWidgetsHolder holder){
        return mBuilder.enableShimmer ? new ShimmerWidget.Builder(context,holder.view).setTargetHostViewID(mBuilder.shimmerHostViewID) : new EmptyShimmer.Builder(context);
    }

    protected BuilderWidget.Builder onCreateNumberIndexBuilder(final LazyWidgetsHolder holder){
        return  new EmptyNumber.Builder(context);
    }

    protected BuilderWidget.Builder onCreateShadowBuilder(final LazyWidgetsHolder holder){
        if(!mBuilder.activeDefaultShadow && !mBuilder.activeFocusShadow){
            return new EmptyShadow.Builder(context);
        }
        return  new ShadowWidget.Builder(context).setFocusActive(mBuilder.activeFocusShadow).setDefaultActive(mBuilder.activeDefaultShadow);
    }

    protected BuilderWidget.Builder onCreateFocusBorderBuilder(final LazyWidgetsHolder holder){
        return mBuilder.enableBorder ? new FocusBorderWidget.Builder(context).setRoundCorner(mBuilder.roundCorner) : new EmptyBorder.Builder(context);
    }

    @Override
    protected void onRegisterWidgetBuilder(final LazyWidgetsHolder holder) {

        /**
         * SimpleItemPresenter 包括 cover/number/shadow/focusBorder 组件
         * 这里对他们进行注册
         */
        final BuilderWidget.Builder coverBuilder = onCreateCoverBuilder(holder);

        final BuilderWidget.Builder  numberBuilder = onCreateNumberIndexBuilder(holder);

        final BuilderWidget.Builder  shadowBuilder = onCreateShadowBuilder(holder);

        final BuilderWidget.Builder focusBorderBuilder = onCreateFocusBorderBuilder(holder);

        final BuilderWidget.Builder shimmerBuilder = onCreateShimmerBuilder(holder);
        coverBuilder.setZOrder(Z_ORDER_COVER);
        shadowBuilder.setZOrder(Z_ORDER_SHADOW);
        numberBuilder.setZOrder(Z_ORDER_MISC);
        focusBorderBuilder.setZOrder(Z_ORDER_BORDER);
        shimmerBuilder.setZOrder(Z_ORDER_SHIMMER);
        holder.registerLazyWidget(ICoverWidget.NAME,coverBuilder);//0
        holder.registerLazyWidget(INumberIndexWidget.NAME,numberBuilder);//2
        holder.registerLazyWidget(IShadowWidget.NAME,shadowBuilder);//4
        holder.registerLazyWidget(IFocusBorderWidget.NAME,focusBorderBuilder);//5
        holder.registerLazyWidget(ShimmerWidget.NAME,shimmerBuilder);
        if(mBuilder.mPlugin != null){
            mBuilder.mPlugin.onRegisterWidgetBuilder(holder);
        }
    }

    @Override
    protected void onHostViewFocusChanged(ItemHostView hostView, boolean hasFocus,final LazyWidgetsHolder widgetsHolder) {
        super.onHostViewFocusChanged(hostView, hasFocus, widgetsHolder);
        final LazyWorker lw = widgetsHolder.getLazyWorker();
        lw.cancelWork(TASK_FOCUS_WIDGET);

        float scaleX = mBuilder.focusScaleX;
        //最大宽度不要大于50
        final int hostWidth = hostView.getWidth();
        final int hostHeight = hostView.getHeight();
        float scaleY = mBuilder.focusScaleY;
        if(hostWidth * (scaleX - 1) > 130){
            scaleX = (hostWidth + 130) / (float)hostWidth;
            scaleY = scaleX;
        }

        if(scaleX != 1 || scaleY != 1){
            TVFocusScaleExcuter.handleOnFocusChange(hostView.getHostView(),hasFocus,scaleX,scaleY,TVFocusScaleExcuter.DEFAULT_DURATION);
        }
        if(widgetsHolder.getWidget(IShadowWidget.NAME) != null) {
            widgetsHolder.getWidget(IShadowWidget.NAME).onFocusChange(hasFocus);
        }
        if(widgetsHolder.getWidget(IFocusBorderWidget.NAME) != null){
            widgetsHolder.getWidget(IFocusBorderWidget.NAME).onFocusChange(hasFocus);
        }
        if(widgetsHolder.getWidget(ShimmerWidget.NAME) != null) {
            widgetsHolder.getWidget(ShimmerWidget.NAME).onFocusChange(hasFocus);
        }

        if(mBuilder.mPlugin != null){
            mBuilder.mPlugin.onHostViewFocusChanged(hostView,hasFocus,widgetsHolder);
        }
    }

    /**
     * 在onBindViewHolder期间会执行一些指定TASK_ID的任务，如果有需要添加一些额外的工作，在这里添加
     * @param holder
     * @param iModel
     * @param taskID
     */
    protected void onExecuteTask(LazyWidgetsHolder holder, Object iModel, int taskID){
        if(mBuilder.mPlugin != null){
            mBuilder.mPlugin.onExecuteTask(holder,iModel,taskID);
        }
    }


    public void loadCoverManual(final LazyWidgetsHolder holder, final Object iModel){
        final ICoverWidget cover = holder.getWidget(ICoverWidget.NAME);
        if(cover != null && iModel instanceof IModel) {
            holder.getLazyWorker().execute(TASK_COVER, new Runnable() {
                @Override
                public void run() {
                    cover.setImagePath((String) ((IModel) iModel).getCover());
                    onExecuteTask(holder, iModel, TASK_COVER);
                }
            }, mBuilder.loadImageDelayTime);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {

        final LazyWidgetsHolder lzh = (LazyWidgetsHolder) viewHolder;

        if(item instanceof SizeVariable){
            SizeVariable size = (SizeVariable) item;
            if(lzh.getItemHostView() != null){
                final View hv = lzh.getItemHostView().getHostView();
                if(hv.getLayoutParams() == null) {
                    hv.setLayoutParams(new RecyclerView.LayoutParams((int)size.getWidth(),(int)size.getHeight()));
                }
                lzh.getItemHostView().changeSize((int)size.getWidth(),(int)size.getHeight());
            }
        }

        final LazyWorker worker = lzh.getLazyWorker();

        if(item instanceof IModel) {
            final IModel imodel = (IModel) item;

            //发现组件
            final INumberIndexWidget nw = lzh.getWidget(INumberIndexWidget.NAME);
            final ICoverWidget cover = lzh.getWidget(ICoverWidget.NAME);
            //取消之前任务
            worker.cancelAllWork();
            final boolean isNeedLoadNewImage = cover.isNeedLoadNewImage((String) imodel.getCover());
            //final boolean isNeedLoadNewImage = false;
            if (isNeedLoadNewImage) {
                cover.cancelLoad();
                //先设置成默认状态
                cover.setImageDrawable(SharedDrawableManager.obtainDrawable(viewHolder.view.getContext(), mBuilder.imgPlaceHolder));
            }
            if (nw != null) {
                nw.setVisibility(View.INVISIBLE);
            }

            //更新封面任务
            if (isNeedLoadNewImage) {
                if(!mBuilder.enableLoadCoverManual){
                    worker.execute(TASK_COVER, new Runnable() {
                        @Override
                        public void run() {
                            cover.setImagePath((String) imodel.getCover());
                            onExecuteTask(lzh, imodel, TASK_COVER);
                        }
                    }, mBuilder.loadImageDelayTime);
                }
            } else {
                Log.v("SimpleItemPresenter", "没有必要更新图片url:" + imodel.getCover());
            }
            //焦点相关
            worker.execute(TASK_FOCUS_WIDGET, new Runnable() {
                @Override
                public void run() {
                    if (lzh.getWidget(IShadowWidget.NAME) != null) {
                        lzh.getWidget(IShadowWidget.NAME).onFocusChange(false);
                    }
                    onExecuteTask(lzh, imodel, TASK_FOCUS_WIDGET);
                }
            }, mBuilder.updateFocusWidgetDelayTime);
            //角标等其它任务
            worker.execute(TASK_MISC, new Runnable() {
                @Override
                public void run() {
                    if (nw != null) {
                        final int number = imodel.getNumIndex();
                        if (number != IModel.NUMBER_INDEX_SKIP) {
                            nw.setNumber(imodel.getNumIndex());
                        }
                        nw.setNumberWidgetScaleOffset(imodel.getNumberScaleOffset());
                        nw.setVisibility(imodel.getItemNumViewShow());
                    }
                    onExecuteTask(lzh, imodel, TASK_MISC);
                }
            }, mBuilder.updateTagWidgetDelayTime);
        }

        if(mBuilder.mPlugin != null){
            mBuilder.mPlugin.onBindViewHolder(viewHolder,item);
        }
    }

    public void updateNumberIndex(ViewHolder viewHolder,int numberIndex){
        if (viewHolder!=null){
            LazyWidgetsHolder holder = (LazyWidgetsHolder) viewHolder;
            ((INumberIndexWidget)holder.getWidget(INumberIndexWidget.NAME)).setNumber(numberIndex);
        }
    }

    public void updateNumberIndex(ViewHolder viewHolder, CommonItemPresenter.IModel iModel){
        if (viewHolder!=null&&iModel!=null){
            LazyWidgetsHolder holder = (LazyWidgetsHolder) viewHolder;
            ((INumberIndexWidget)holder.getWidget(INumberIndexWidget.NAME)).setNumber(iModel.getNumIndex());
        }
    }

    @Override
    public void onUnbindViewHolder(ViewHolder viewHolder) {
        //Log.d("MYCommonItemPresenter ","-----viewHolder unbind------: "+viewHolder+" tt ");
        //取消之前的任务，做好回收工作
        final LazyWidgetsHolder lzh = (LazyWidgetsHolder) viewHolder;
        final LazyWorker worker = lzh.getLazyWorker();
        if(worker != null) {
            worker.cancelAllWork();
        }
        final ICoverWidget cover = lzh.getWidget(ICoverWidget.NAME);
        if(cover != null) {
            cover.cancelLoad();
            //设置成默认图，以防止回收使用后，马上显示图片
            cover.setImageDrawable(SharedDrawableManager.obtainDrawable(viewHolder.view.getContext(), mBuilder.imgPlaceHolder));
        }

//        if(lzh.getWidget(INumberIndexWidget.NAME) != null){
//            final INumberIndexWidget numberIndexWidget = lzh.getWidget(INumberIndexWidget.NAME);
//            numberIndexWidget.setNumber(-1);
//        }
        if(mBuilder.mPlugin != null){
            mBuilder.mPlugin.onUnbindViewHolder(viewHolder);
        }
    }

    @Override
    protected void onWidgetInitialized(AbsWidget widget, LazyWidgetsHolder lwh) {
        super.onWidgetInitialized(widget, lwh);
        if(mBuilder.mPlugin != null){
            mBuilder.mPlugin.onWidgetInitialized(widget,lwh);
        }
    }

    @Override
    protected void onItemHostViewSizeChanged(LazyWidgetsHolder lazyWidgetsHolder, ItemHostView hostView, int width, int height) {
        super.onItemHostViewSizeChanged(lazyWidgetsHolder, hostView, width, height);
        if(mBuilder.mPlugin != null){
            mBuilder.mPlugin.onItemHostViewSizeChanged(lazyWidgetsHolder, hostView, width, height);
        }
    }

    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        if(mBuilder.mPlugin != null){
            mBuilder.mPlugin.onViewDetachedFromWindow(holder);
        }

    }

    @Override
    public void onViewAttachedToWindow(ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        if(mBuilder.mPlugin != null){
            mBuilder.mPlugin.onViewAttachedToWindow(holder);
        }

    }


    /**
     * Created by HuangYong on 2018/9/10.
     */
    public interface IModel {

        /**
         * 跳过更新标号
         */
        int NUMBER_INDEX_SKIP = -2;

        /**
         * 海报，地址URL、Bitmap、Drawable
         变化的         * @return
         */
        Object getCover();

        /**
         * 数字索引是否显示
         * View.VISIBLE
         * @return
         */
        int getItemNumViewShow();

        /**
         * 获取序号,如果值为负，则不会显示
         * 如果返回{@link #NUMBER_INDEX_SKIP} 则不会对标号进行更改，需要自己手动处理标号。
         * @return
         */
        int getNumIndex();

        /**
         * 设置缩放比例，默认1.0
         * @return
         */
        float getNumberScaleOffset();
    }

    public interface SizeVariable{
        float getWidth();
        float getHeight();
    }


    /**
     * 通过添加plugin的方式，可以扩展SimpleItemPresenter的功能，比如添加TipWidget的功能。
     */
    public interface Plugin{
        /**
         * 告知如何初始化HostView
         * @param parent
         * @return
         */
        ItemHostView onCreateHostView(ViewGroup parent);

        /**
         * Widget被初始化时回调
         * @param widget
         * @param lwh
         */
        void onWidgetInitialized(AbsWidget widget,final LazyWidgetsHolder lwh);

        /**
         * 通过注册widget Builder的方式，告知初始化哪些Widget
         * @param widgetsHolder
         */
        void onRegisterWidgetBuilder(LazyWidgetsHolder widgetsHolder);

        /**
         * 当hostView的焦点改变时回调
         * @param hostView
         * @param hasFocus
         * @param widgetsHolder
         */
        void onHostViewFocusChanged(ItemHostView hostView,boolean hasFocus,LazyWidgetsHolder widgetsHolder);

        /**
         * 执行任务
         * @param holder
         * @param iModel
         * @param taskID
         */
        void onExecuteTask(LazyWidgetsHolder holder, Object iModel, int taskID);

        /**
         * Presenter：onBindViewHolder
         * @param viewHolder
         * @param item
         */
        void onBindViewHolder(ViewHolder viewHolder, Object item);

        /**
         * Presenter：onBindViewHolder
         * @param viewHolder
         */
        void onUnbindViewHolder(ViewHolder viewHolder);

        /**
         * Presenter：onViewDetachedFromWindow
         */
        void onViewDetachedFromWindow(ViewHolder holder);
        /**
         * Presenter：onViewAttachedToWindow
         */
        void onViewAttachedToWindow(ViewHolder holder);

        /**
         * * HostView大小改变时被调用
         * @param lazyWidgetsHolder
         * @param hostView
         * @param width
         * @param height
         */
        void onItemHostViewSizeChanged(LazyWidgetsHolder lazyWidgetsHolder, ItemHostView hostView, int width, int height);

        /**
         * 获得宿主SimpleItemPresenter
         * @return
         */
        SimpleItemPresenter getHost();

        /**
         * SimpleItemPresenter创建时被调用
         * @param presenter  SimpleItemPresenter实例
         * @param builder SimpleItemPresenter的builder
         */
        void onCreatePresenter(SimpleItemPresenter presenter,Builder builder);

    }

    /**
     * 方便用来创建简单的plugin
     */
    public static class PluginImpl implements Plugin{

        SimpleItemPresenter simpleItemPresenter;

        @Override
        public SimpleItemPresenter getHost(){
            return simpleItemPresenter;
        }

        @Override
        public void onCreatePresenter(SimpleItemPresenter presenter,Builder builder) {
            this.simpleItemPresenter = presenter;
        }

        @Override
        public ItemHostView onCreateHostView(ViewGroup parent) {
            return null;
        }

        @Override
        public void onWidgetInitialized(AbsWidget widget, LazyWidgetsHolder lwh) {

        }

        @Override
        public void onRegisterWidgetBuilder(LazyWidgetsHolder widgetsHolder) {

        }

        @Override
        public void onHostViewFocusChanged(ItemHostView hostView, boolean hasFocus, LazyWidgetsHolder widgetsHolder) {

        }

        @Override
        public void onExecuteTask(LazyWidgetsHolder holder, Object iModel, int taskID) {

        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {

        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {

        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {

        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {

        }

        @Override
        public void onItemHostViewSizeChanged(LazyWidgetsHolder lazyWidgetsHolder, ItemHostView hostView, int width, int height) {

        }
    }

    /**
     * 使用多个插件时，使用此类
     */
    public static class PluginGroup extends PluginImpl {

        List<Plugin> plugins;

        public PluginGroup() {
            this.plugins = new ArrayList();
        }

        public PluginGroup addPlugin(Plugin plugin){
            plugins.add(plugin);
            return this;
        }

        public void removePlugin(Plugin p){
            this.plugins.remove(p);
        }

        @Override
        public void onCreatePresenter(SimpleItemPresenter presenter,Builder builder) {
            super.onCreatePresenter(presenter,builder);
            for(Plugin p : plugins){
                p.onCreatePresenter(presenter,builder);
            }
        }

        //这里无效
        @Override
        public ItemHostView onCreateHostView(ViewGroup parent) {
            return null;
        }

        @Override
        public void onWidgetInitialized(AbsWidget widget, LazyWidgetsHolder lwh) {
            for(Plugin p : plugins){
                p.onWidgetInitialized(widget,lwh);
            }
        }

        @Override
        public void onRegisterWidgetBuilder(LazyWidgetsHolder widgetsHolder) {
            for(Plugin p : plugins){
                p.onRegisterWidgetBuilder(widgetsHolder);
            }
        }

        @Override
        public void onHostViewFocusChanged(ItemHostView hostView, boolean hasFocus, LazyWidgetsHolder widgetsHolder) {
            for(Plugin p : plugins){
                p.onHostViewFocusChanged(hostView,hasFocus,widgetsHolder);
            }
        }

        @Override
        public void onExecuteTask(LazyWidgetsHolder holder, Object iModel, int taskID) {
            for(Plugin p : plugins){
                p.onExecuteTask(holder,iModel,taskID);
            }
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            for(Plugin p : plugins){
                p.onBindViewHolder(viewHolder,item);
            }
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
            for(Plugin p : plugins){
                p.onUnbindViewHolder(viewHolder);
            }
        }

        @Override
        public void onViewDetachedFromWindow(ViewHolder holder) {
            for(Plugin p : plugins){
                p.onViewDetachedFromWindow(holder);
            }
        }

        @Override
        public void onViewAttachedToWindow(ViewHolder holder) {
            for(Plugin p : plugins){
                p.onViewAttachedToWindow(holder);
            }
        }

        @Override
        public void onItemHostViewSizeChanged(LazyWidgetsHolder lazyWidgetsHolder, ItemHostView hostView, int width, int height) {
            for(Plugin p : plugins){
                p.onItemHostViewSizeChanged(lazyWidgetsHolder,hostView,width,height);
            }
        }

    }





    public static final class EmptyShadow extends BuilderWidget implements IShadowWidget {


        public EmptyShadow(Builder builder) {
            super(builder);
        }

        @Override
        public void setFocusShadowVisible(boolean focusShadowVisible) {

        }

        @Override
        public void setDefaultShadowVisible(boolean defaultShadowVisible) {

        }

        @Override
        public void setWidgetScale(float scale) {

        }

        @Override
        public String getName() {
            return IShadowWidget.NAME;
        }

        @Override
        public void onFocusChange(boolean gainFocus) {

        }

        @Override
        public RenderNode getRenderNode() {
            return this;
        }

        public final static class Builder extends BuilderWidget.Builder  {

            public Builder(Context context) {
                super(context);
            }

            @Override
            public Class getWidgetClass() {
                return EmptyShadow.class;
            }

            @Override
            public BuilderWidget build() {
                return new EmptyShadow(this);
            }
        }




    }

    public static final class EmptyNumber extends BuilderWidget implements INumberIndexWidget {


        public EmptyNumber(Builder builder) {
            super(builder);
        }





        @Override
        public String getName() {
            return IShadowWidget.NAME;
        }

        @Override
        public void onFocusChange(boolean gainFocus) {

        }

        @Override
        public RenderNode getRenderNode() {
            return this;
        }

        @Override
        public void setNumText(String num_text) {

        }

        @Override
        public void setNumber(int number) {

        }

        @Override
        public void setNumTextColor(int num_text_color) {

        }

        @Override
        public void setNumTextSize(int unit, float size) {

        }

        @Override
        public void setVisibility(int visible) {

        }

        @Override
        public void setVisible(boolean isShow) {

        }

        @Override
        public void setNumberWidgetScaleOffset(float numberScaleOffset) {

        }

        public final static class Builder extends BuilderWidget.Builder  {

            public Builder(Context context) {
                super(context);
            }

            @Override
            public Class getWidgetClass() {
                return EmptyNumber.class;
            }

            @Override
            public BuilderWidget build() {
                return new EmptyNumber(this);
            }
        }




    }

    public static class EmptyCover extends BuilderWidget implements ICoverWidget{

        public EmptyCover(Builder builder) {
            super(builder);
        }


        @Override
        public String getName() {
            return ICoverWidget.NAME;
        }

        @Override
        public void setImageDrawable(@Nullable Drawable d) {

        }

        @Override
        public void setImagePath(@NotNull String path) {

        }

        @Override
        public void setImageResource(int id) {

        }

        @Override
        public void setImageBitmap(@NotNull Bitmap bitmap) {

        }

        @Override
        public void cancelLoad() {

        }

        @Override
        public void onRecycle() {

        }

        @Override
        public boolean isNeedLoadNewImage(String path) {
            return false;
        }

        @Nullable
        @Override
        public String getCurrentImagePath() {
            return null;
        }

        @Override
        public void setLoadImageDelayTime(int delayTime) {

        }

        @Override
        public void notifyParentSizeChanged(int width, int height) {

        }

        @Override
        public void recycle() {

        }

        @Override
        public void reload() {

        }

        public final static class Builder extends BuilderWidget.Builder  {

            public Builder(Context context) {
                super(context);
            }

            @Override
            public Class getWidgetClass() {
                return EmptyCover.class;
            }

            @Override
            public BuilderWidget build() {
                return new EmptyCover(this);
            }
        }
    }
    public static class EmptyShimmer extends BuilderWidget{

        public EmptyShimmer(Builder builder) {
            super(builder);
        }

        @Override
        public String getName() {
            return ShimmerWidget.NAME;
        }
        public final static class Builder extends BuilderWidget.Builder{

            public Builder(Context context) {
                super(context);
            }

            @Override
            public Class getWidgetClass() {
                return EmptyShimmer.class;
            }

            @Override
            public BuilderWidget build() {
                return new EmptyShimmer(this);
            }
        }
    }

    public static class EmptyBorder extends BuilderWidget implements IFocusBorderWidget{

        public EmptyBorder(Builder builder) {
            super(builder);
        }


        @Override
        public String getName() {
            return IFocusBorderWidget.NAME;
        }

        @Override
        public void setBorderColor(int borderColor) {

        }

        @Override
        public void setBorderVisible(boolean visible) {

        }

        public final static class Builder extends BuilderWidget.Builder  {

            public Builder(Context context) {
                super(context);
            }

            @Override
            public Class getWidgetClass() {
                return EmptyBorder.class;
            }

            @Override
            public BuilderWidget build() {
                return new EmptyBorder(this);
            }
        }
    }


}



