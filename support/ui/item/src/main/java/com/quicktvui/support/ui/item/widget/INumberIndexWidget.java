package com.quicktvui.support.ui.item.widget;

import android.graphics.Canvas;

public interface INumberIndexWidget extends IWidget {

    //必须唯一名称
    String NAME = "Number";

    @Deprecated
    void setNumText(String num_text);

    void setNumber(int number);

    void setNumTextColor(int num_text_color);

    void setNumTextSize(int unit, float size);

    void setVisibility(int visible);

    void setVisible(boolean isShow);

    void draw(Canvas canvas);

    void setNumberWidgetScaleOffset(float numberScaleOffset);
}
