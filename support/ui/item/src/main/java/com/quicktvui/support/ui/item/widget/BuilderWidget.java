package com.quicktvui.support.ui.item.widget;

import android.content.Context;

import com.quicktvui.support.ui.item.utils.LazyTask;


public abstract class BuilderWidget<E extends BuilderWidget.Builder> extends AbsWidget implements LazyTask.Executor, IWidget {

    protected final E mBuilder;
    public abstract static class Builder<T extends BuilderWidget>{
       final Context context;
       float widgetScale = 1;
        private String name;


        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public String getName() {
            return name;
        }

        int zOrder;

        public Builder setWidgetScale(float scale){
            this.widgetScale = scale;
            return this;
        }

        public Builder setZOrder(int zOrder) {
            this.zOrder = zOrder;
            return this;
        }

        public int getZorder() {
            return zOrder;
        }

        public Builder(Context context) {
            this.context = context;
        }

        public Class getWidgetClass(){
            return getClass().getEnclosingClass();
        }

        public abstract T build();
    }


    public static class SimpleBuilder extends Builder{

        public SimpleBuilder(Context context) {
            super(context);
        }
        @Override
        public BuilderWidget build() {
            return new BuilderWidget(this) {
                @Override
                public String getName() {
                    return "Simple";
                }
            };
        }
    }


    public E getBuilder() {
        return mBuilder;
    }

    public BuilderWidget(E builder) {
        super(builder.context);
        this.mBuilder = builder;
        onCreate();
        setWidgetScale(mBuilder.widgetScale);
        setZOrder(builder.zOrder);
    }


    public Context getContext() {
        return this.mBuilder.context;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
