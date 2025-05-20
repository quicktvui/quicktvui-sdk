package com.quicktvui.support.ui.selectseries.bean;

public class MyData {
    private final PageData[] pageDataArray;
    public int currentGroup = -1;
    public int currentItem = -1;
    public int targetItemPos = -1;
    public int currentFocus = -1;
    public int pendingDisplayFocusPos = -1;

    public MyData(int size) {
        pageDataArray = new PageData[size];
        for (int i = 0; i < size; i++) {
            pageDataArray[i] = new PageData();
        }
    }

    public void setCurrentGroup(int currentGroup) {
        this.currentGroup = currentGroup;
    }

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
    }

    public int getState(int page) {
        return pageDataArray[page].state;
    }

    public void setState(int page, int state) {
        pageDataArray[page].state = state;
    }
}
