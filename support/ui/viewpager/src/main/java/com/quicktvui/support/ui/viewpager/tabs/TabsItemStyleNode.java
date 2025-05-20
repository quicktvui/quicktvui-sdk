package com.quicktvui.support.ui.viewpager.tabs;

import android.view.View;

import com.tencent.mtt.hippy.annotation.HippyControllerProps;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.dom.node.StyleNode;

public class TabsItemStyleNode extends StyleNode {
    final TabsParam param;

    public TabsItemStyleNode() {
        this.param = new TabsParam();
    }

    public void setDataList(HippyArray array) {
        this.param.dataList = array;
    }

    @HippyControllerProps(name = "defaultIndex", defaultType = HippyControllerProps.NUMBER, defaultNumber = 0)
    public void setDefaultIndex(int defaultIndex) {
        param.defaultIndex = defaultIndex;
    }

    @HippyControllerProps(name = "blockFocusDirections", defaultType = HippyControllerProps.ARRAY)
    public void setBlockFocusDirections(HippyArray array) {
        if (array != null) {
            int[] directions = new int[array.size()];
            for (int i = 0; i < array.size(); i++) {
                switch (array.getString(i)) {
                    case "up":
                        directions[i] = View.FOCUS_UP;
                        break;
                    case "down":
                        directions[i] = View.FOCUS_DOWN;
                        break;
                    case "left":
                        directions[i] = View.FOCUS_LEFT;
                        break;
                    case "right":
                        directions[i] = View.FOCUS_RIGHT;
                        break;
                }
            }
            this.param.blockFocusDirections = directions;
        }
    }

    public HippyArray getTabsData() {
        return this.param.dataList;
    }

    public boolean isSuspension() {
        return this.param.isSuspension;
    }

    public boolean useSuspensionBg() {
        return this.param.useSuspensionBg;
    }

    public boolean isDataListValid() {
        return getTabsData() != null;
    }

}
