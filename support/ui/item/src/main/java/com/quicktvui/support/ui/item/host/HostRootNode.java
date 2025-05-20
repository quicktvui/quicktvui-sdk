package com.quicktvui.support.ui.item.host;

import android.view.View;

import com.quicktvui.support.ui.item.widget.BuilderWidget;
import com.quicktvui.support.ui.render.RenderNode;
import com.quicktvui.support.ui.render.RootNode;

import java.util.HashMap;
import java.util.Map;

 public class HostRootNode extends RootNode {


    public HostRootNode(View hostView) {
        super(hostView);
    }

    @Override
    public RenderNode add(RenderNode node) {
        if(node instanceof BuilderWidget){
            final BuilderWidget i = (BuilderWidget) node;
            childrenMap().put(i.getName(),i);
        }
        return super.add(node);
    }


    public BuilderWidget findWidget(String name){
        return childrenMap().get(name);
    }

    Map<String,BuilderWidget> mChildrenWidgets;

    Map<String,BuilderWidget> childrenMap(){
        if(mChildrenWidgets == null){
            mChildrenWidgets = new HashMap<>();
        }
        return mChildrenWidgets;
    }

}
