package com.quicktvui.sdk.core.tookit;

import android.view.View;

/**
 * <br>
 *
 * <br>
 */
public interface IESToolkit {

    /** 可以使用焦点 **/
    IESToolkit focusable();

    /** 添加一个View **/
    void show(View view);

    /** 销毁View **/
    void hide();

    boolean isToolKitShow();
}
