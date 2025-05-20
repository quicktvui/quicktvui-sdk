package com.quicktvui.support.ui.item.widget;


public interface IFocusBorderWidget extends IWidget {

    //必须唯一名称
    String NAME = "RoundBorder";

    void setBorderColor(int borderColor);

    //设置边框是否显示

    void setBorderVisible(boolean visible);
}
