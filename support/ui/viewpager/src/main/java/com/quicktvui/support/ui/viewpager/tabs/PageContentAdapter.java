package com.quicktvui.support.ui.viewpager.tabs;

import android.content.Context;
import android.view.View;

import android.support.annotation.Nullable;

import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.modules.Promise;
import com.tencent.mtt.hippy.uimanager.RenderNode;

public abstract class PageContentAdapter {
    final Context context;

    public PageContentAdapter(Context context) {
        this.context = context;
    }

    abstract View createContentView(RenderNode templateNode, View agentView);

    public abstract void bindPageData(int position, View pv, PageItem pageItem, RenderNode templateNode
            , FastListPageChangeListener fastListPageChangeListener
            , FastListScrollToTopListener fastListScrollToTopListener, boolean useDiff);

    public abstract void updateItemByID(int pageIndex, View pv, PageItem pageItem,int itemPosition,Object itemData,boolean traverse);
    public abstract void updateChildItemByID(View pv, PageItem pageItem,int itemPosition,int childIndex,Object itemData,boolean traverse);
    public abstract View findRootItemView(@Nullable View pv, int itemPosition);
    public abstract boolean searchReplaceItemTraverse(int pageIndex,View pv, PageItem pageItem, String sid,Object itemData);

    public abstract void addPageData(int position, View pv, HippyArray data,int deleteCount);
    public abstract void removePageData(int position, View pv, int deleteCount);
    public abstract void insertPageData(int position, View pv, HippyArray data);

    public void pausePostTask(int position, View pv) {

    }
    public abstract void setRequestAutofocusOnPageChange(boolean requestAutofocusOnPageChange);


    public void resumePostTask(int position, View pv) {

    }

    public void recycle(int position, View pv) {

    }

    public void reset(int position,View pv){

    }

    public abstract void contentToTop(View v, int pos);

    public void reuseAfterRecycle(View pv) {

    }

    public void clearPageData(int position, View pv) {

    }

    public void changeLoading(View view, int pos, boolean show) {

    }

    public void onViewAttachedToWindow(View itemView, int bindingAdapterPosition){

    }

    public void onViewDetachedFromWindow(View itemView, int bindingAdapterPosition){

    }

    public void onBindViewHolder(View itemView, int position) {
    }

    public void requestFirstFocus(View view,int position){

    }

    public boolean isOnScrollTop(View v, int pos, float v1){
        return true;
    }

    public abstract void dispatchUIFunction(View view, int pageIndex,String functionName, HippyArray var, Promise promise);

    public void setDisplay(View view,int position,boolean b){

    }

    public void scrollToFocus(View view,int pos){

    }

    public void destroy(View pv){

    }

    public boolean isLoadingShown(View view,int pos){

        return false;
    }

    public boolean isSingleContent(){
        return false;
    }

    public void onBeforeChangeCurrentPage(View pv,int nextPage, int prevPage){

    }
}
