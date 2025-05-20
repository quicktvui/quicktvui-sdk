package com.quicktvui.support.ui.render;

import android.view.View;


public class RootNode extends RenderNode {


    public RootNode(View hostView) {
        mHostView = hostView;
        mRootNode = this;
    }

    View mHostView;

    @Override
    public RenderNode add(RenderNode node) {
        return super.add(node);
    }


    public View getHostView() {
        return mHostView;
    }

    @Override
    public void invalidateSelf() {
        if(mHostView != null){
            mHostView.invalidate();
        }
    }


}
