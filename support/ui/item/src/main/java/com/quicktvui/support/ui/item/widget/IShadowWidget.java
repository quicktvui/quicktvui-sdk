package com.quicktvui.support.ui.item.widget;


public interface IShadowWidget extends IWidget{

    //必须唯一名称
    String NAME = "Shadow";

    void setFocusShadowVisible(boolean focusShadowVisible);

    void setDefaultShadowVisible(boolean defaultShadowVisible);
}
