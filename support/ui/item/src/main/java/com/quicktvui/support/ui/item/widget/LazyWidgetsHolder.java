package com.quicktvui.support.ui.item.widget;

import android.content.Context;
import android.os.Build;
import com.quicktvui.support.ui.leanback.Presenter;
import android.util.ArrayMap;
import android.util.Log;

import com.quicktvui.support.ui.item.host.ItemHostView;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

public class LazyWidgetsHolder extends Presenter.ViewHolder {


    Map<String,BuilderWidget.Builder> builderList;

    Map<String ,AbsWidget> widgetsCache;

    ItemHostView mItemHostView;

    LazyWorker mLazyWorker;

    WidgetsBuilder WidgetsBuilder;
    onWidgetInitCallback onWidgetInitCallback;

    Context context;

    private Map<String, Object> mFacets;

    public final Object getFacetByName(String name) {
        if (mFacets == null) {
            return null;
        }
        return mFacets.get(name);
    }

    public final void setFacetByName(String name, Object facetImpl) {
        if (mFacets == null) {
            mFacets = new HashMap();
        }
        mFacets.put(name, facetImpl);
    }

    public Context getContext() {
        return context;
    }

    public LazyWidgetsHolder(ItemHostView view) {

        super(view.getHostView());

        mItemHostView = view;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            builderList = new ArrayMap();
        }else{
            builderList = new HashMap<>();
        }

        mLazyWorker = new LazyWorker();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            this.widgetsCache = new ArrayMap<>();
        }else{
            widgetsCache = new HashMap<>();
        }

        this.context = view.getHostView().getContext();

    }



    public ItemHostView getItemHostView() {
        return mItemHostView;
    }

    public interface WidgetsBuilder {
        void onBuildWidgets(LazyWidgetsHolder holder);
    }

    public interface onWidgetInitCallback {
        void onWidgetInit(String name, AbsWidget widget,LazyWidgetsHolder holder);
    }

    public void setWidgetsBuilder(WidgetsBuilder WidgetsBuilder) {
        this.WidgetsBuilder = WidgetsBuilder;
    }


    public void setOnWidgetInitCallback(LazyWidgetsHolder.onWidgetInitCallback onWidgetInitCallback) {
        this.onWidgetInitCallback = onWidgetInitCallback;
    }

    /**
     *注册实例化Widget所需要的builder
     * @param name 注册组件的名称 稍后可以通过getWidget方式获取widget
     * @param builder
     */
    public void registerLazyWidget(final String name,final BuilderWidget.Builder builder){
        builderList.put(name,builder);
    }

    public BuilderWidget.Builder getLazyWidgetBuilder(final String name){
        return builderList.get(name);
    }

    /**
     *注册已经实例化Widget
     * @param name 注册组件的名称 稍后可以通过getWidget方式获取widget
     * @param widget 已经实例化过的widget
     */
    public void registerWidget(final String name,final AbsWidget widget){
        widgetsCache.put(name,widget);
    }

    /**
     *反注册实例化Widget
     * @param name 注册组件的名称
     */
    public void unRegisterLazyWidget(final String name){
        builderList.remove(name);
    }

    /**
     *反注册实例化Widget
     * @param name 注册组件的名称
     */
    public void unRegisterWidget(final String name){
        widgetsCache.remove(name);
    }


    public void build(){
        if(WidgetsBuilder != null){
            WidgetsBuilder.onBuildWidgets(this);
        }
    }

    public int getLazyWidgetCount(){
        return builderList.size();
    }


    public LazyWorker getLazyWorker() {
        return mLazyWorker;
    }

    public <T extends  AbsWidget> T getWidget(final String name){
        if(widgetsCache == null){
            throw new IllegalStateException("please call commitWidgets() method after register all widgets");
        }
        T result = (T) widgetsCache.get(name);
//        Log.d("AViewHolder","getWidget index is:"+index);
        if(result == null) {
            final BuilderWidget.Builder builder = builderList.get(name);
//            Log.d("AViewHolder","getWidget builder is:"+builder);
            if(builder != null) {
                try {
                    result = (T) createWidget(builder);
                    onWidgetCreated(name, result);
//                Log.d("AViewHolder","new Widget:"+result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (result != null) {
                    widgetsCache.put(name, result);
                }
            }
        }
        return result;
    }

    protected void onWidgetCreated(String  name,AbsWidget widget){
        mItemHostView.addWidget(widget);
        if(onWidgetInitCallback != null){
            onWidgetInitCallback.onWidgetInit(name,widget,this);
        }
    }


    Object createWidget(BuilderWidget.Builder builder) throws Exception {
        final Class clazz = builder.getWidgetClass();
//        Log.d("AViewHolder","createWidget class is "+clazz);
        Constructor con = null;
        try {
            con = clazz.getDeclaredConstructor(builder.getClass());
            con.setAccessible(true);
        } catch (Throwable e) {
            e.printStackTrace();
            if(builder != null) {
                Log.e("LazyWidgetsHolder", "createWidget 发生错误，请检查builder是否设置了class " + builder.getClass() );
            }
        }
//        Log.d("AViewHolder","con  is "+con);
        return con.newInstance(builder);
    }


}
