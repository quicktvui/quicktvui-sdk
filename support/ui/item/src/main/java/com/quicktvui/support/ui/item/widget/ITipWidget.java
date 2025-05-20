package com.quicktvui.support.ui.item.widget;

import org.jetbrains.annotations.Nullable;

public interface ITipWidget extends IWidget {

    //必须唯一名称
    String NAME = "Tip_Widget";

    void setTip(@Nullable String var1);

    interface IModel{
        String getTipString();
    }


}
