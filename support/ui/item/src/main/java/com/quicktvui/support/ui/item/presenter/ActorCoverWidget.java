package com.quicktvui.support.ui.item.presenter;

import android.content.Context;
import android.view.View;

import com.quicktvui.support.ui.item.widget.CoverWidget;

public class ActorCoverWidget extends CoverWidget {
    public ActorCoverWidget(Builder builder) {
        super(builder);
    }

    @Override
    protected void onMeasure(int width, int height) {
        super.onMeasure(width, height);
        if(width != height){
            //自动将头像改成正方形
            setSize(width,width);
        }
    }

    public static class Builder extends CoverWidget.Builder {
        public Builder(Context context,View hostView) {
            super(context,hostView);
        }
        public ActorCoverWidget build() {
            return new ActorCoverWidget(this);
        }

        @Override
        public Class getWidgetClass() {
            return ActorCoverWidget.class;
        }
    }
}
