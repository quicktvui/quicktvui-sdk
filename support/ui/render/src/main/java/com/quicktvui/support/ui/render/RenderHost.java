package com.quicktvui.support.ui.render;


import android.view.View;


public interface RenderHost {


    @Deprecated
    void setRootNode(RenderNode node);

    RootNode getRootNode();

    <T extends View> T getHostView();

    <T> T as();

}
