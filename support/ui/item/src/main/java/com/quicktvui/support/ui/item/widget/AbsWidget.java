package com.quicktvui.support.ui.item.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import android.support.annotation.NonNull;

import com.quicktvui.support.ui.item.utils.LazyTask;

import com.quicktvui.support.ui.render.RenderNode;


public abstract class AbsWidget extends RenderNode implements LazyTask.Executor, IWidget {


    Context context;


    public AbsWidget(Context context) {
        super();
        this.context = context;
        onCreate();
        setWidgetScale(1);
    }

    @Override
    public void onFocusChange(boolean gainFocus){

    }

    @Override
    public RenderNode getRenderNode() {
        return this;
    }


    @Override
    public void setWidgetScale(float scale){

    }

    @Override
    public void draw(@NonNull Canvas canvas) {

        super.draw(canvas);
    }

    protected void onCreate(){

    }



    public abstract String getName();

    public Context getContext() {
       return context;
    }


    @Override
    public void onViewDetachedFromWindow(View view) {

    }

    @Override
    public void onViewAttachedToWindow(View view) {

    }
}
