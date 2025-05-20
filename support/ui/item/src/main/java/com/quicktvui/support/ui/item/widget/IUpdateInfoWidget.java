package com.quicktvui.support.ui.item.widget;

import android.graphics.Rect;


public interface IUpdateInfoWidget extends IWidget {

    //必须唯一名称
    String NAME = "Update";

    /**
     * 设置当前Item对应数据的更新信息
     *
     * @param updateInfo
     */
    void setUpdateInfo(String updateInfo);

    void setUpdateInfo(String title, String info, String endWord);

    void setUpdateBack(String colorString);

    void setUpdateTextColor(String colorString);

    void setUpdateTextSize(float size);

    void setUpdatePaddingLR(int paddingPx);

    void changeFocus(boolean gainFocus, int direction, Rect previouslyFocusedRect);

    void setScaleOffset(float mScaleOffset);
}
