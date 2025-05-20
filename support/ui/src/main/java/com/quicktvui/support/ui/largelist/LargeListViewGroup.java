package com.quicktvui.support.ui.largelist;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;

import com.quicktvui.support.ui.ScreenAdapt;
import com.quicktvui.support.ui.v7.widget.LinearSmoothScroller;
import com.quicktvui.support.ui.v7.widget.RecyclerView;

import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.IEsComponentView;
import com.sunrain.toolkit.utils.BuildConfig;
import com.quicktvui.hippyext.RenderUtil;
import com.quicktvui.base.ui.TriggerTaskHost;
import com.tencent.mtt.hippy.utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;

import com.quicktvui.support.ui.R;
import com.quicktvui.support.ui.legacy.misc.ItemDecorations;
import com.quicktvui.support.ui.leanback.ArrayObjectAdapter;
import com.quicktvui.support.ui.leanback.PresenterSelector;


public class LargeListViewGroup extends LinearLayout implements IEsComponentView, FocusWatcher.FocusWatch, TriggerTaskHost {

    Param mParam;
    MyData mData;

    MyLargeListView largeListView;
    MyGroupListView groupListView;
    EventSender event;


    ArrayObjectAdapter largeListAdapter;
    ArrayObjectAdapter groupListAdapter;
    private Runnable postTask;
    private Runnable postNotifyDataTask;

    FocusWatcher focusWatcher;

    private RecyclerView.ItemDecoration centerPaddingItemDecoration;

    private boolean displayed = false;

    private boolean created = false;

    public static String TAG = "LargeListLOG";


    Drawable[] arrow;
    boolean[] drawArrow;

    public LargeListViewGroup(Context context) {
        super(context);
    }

    public LargeListViewGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public LargeListViewGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void initParams(EsMap map, EsMap templateMap) {
        final Param p = new Param();
        p.apply(map, templateMap);
        this.mParam = p;
        if(LogUtils.isDebug()){
            Log.i(TAG,"id"+getId()+",initParams : "+p+" view id:"+this.getId());
        }
        onPramSet();
    }

    void onPramSet() {
//        Log.i(TAG,"id"+getId()+",onPramSet");
        if (mData == null) {
            event = new EventSender(this);
            int pageSize = computePageCount();
            mData = new MyData(pageSize);
            setupData();
            requestFirstShow();
        }
    }

    private void setupViews() {
        this.setOrientation(VERTICAL);
        removeAllViews();
        largeListView = new MyLargeListView(getContext());
        largeListView.initLayoutManager();
        if (mParam.totalCount <= mParam.disableScrollOnMinCount) {
            largeListView.setScrollType(-1);
//            Log.e(TAG, "setupViews 数据量较小，关闭滚动 disableScrollOnMinCount： " + mParam.disableScrollOnMinCount + ",totoalCount:" + mParam.totalCount);
        } else {
            largeListView.setScrollType(mParam.scrollType);
        }
        setClipChildren(false);
        largeListView.setClipChildren(false);

        //setClipToPadding(false)👇 XingRuGeng Add 解决HomeItemView图标被遮挡问题
        largeListView.setClipToPadding(isScrollTypePage());
//        largeListView.setBackgroundColor(Color.CYAN);
        final int width = mParam.contentWidth;
        LayoutParams lpUp;
        if (isScrollTypePage()) {
            lpUp = new LayoutParams(width + mParam.paddingForPageRight + mParam.paddingForPageLeft, mParam.contentHeight);
            largeListView.setPadding(mParam.paddingForPageLeft, 0, mParam.paddingForPageRight, 0);
            lpUp.leftMargin = mParam.marginLeft;
//        largeListView.setClipChildren(true);
            lpUp.gravity = Gravity.CENTER_HORIZONTAL;
        } else {
            lpUp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mParam.contentHeight);
            lpUp.leftMargin = mParam.marginLeft;
            largeListView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    int pos = parent.getChildAdapterPosition(view);
                    int padding = (int) ((parent.getWidth() - mParam.contentWidth) * 0.5);
                    if (pos == 0) {
                        outRect.left = padding;
                    }
                    if (mParam.totalCount > mParam.disableScrollOnMinCount
                            && pos == state.getItemCount() - 1) {
                        outRect.right = padding;
                    }
                }
            });
        }
        addView(largeListView, lpUp);
        largeListView.setChildSize(mParam.itemWidth);

        if (mParam.enableGroup) {
            groupListView = new MyGroupListView(getContext());
            groupListView.setClipChildren(false);
//            groupListView.setScrollType(mParam.scrollType);
//            groupListView.setBackgroundColor(Color.RED);
            LayoutParams lp = new LayoutParams(width, mParam.groupHeight);
            lp.topMargin = mParam.groupTopMargin;
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            lp.leftMargin = mParam.marginLeft;
            addView(groupListView, lp);
            groupListView.setChildSize(mParam.groupItemWidth);
        }

        if (isScrollTypePage()) {
            Drawable left = ContextCompat.getDrawable(getContext(), R.drawable.icon_serier_left_arrow);

            Drawable right = ContextCompat.getDrawable(getContext(), R.drawable.icon_serier_right_arrow);

            this.arrow = new Drawable[]{left, right};
            this.drawArrow = new boolean[]{false, false};
        }
    }


    private boolean isScrollTypePage() {
        return mParam != null && mParam.scrollType == AdvanceCenterScroller.SCROLL_TYPE_PAGE;
    }


    public void setup() {
        if (LogUtils.isDebug()) {
            Log.d(TAG, "setup !!!! mParam:" + mParam);
        }
        if (mParam != null) {
            if (largeListView == null) {
                setupViews();
                setupListInitScroll();
                setupUpList();
                setupGroupList();
                postLayout(true, 300);
                if (displayed) {
                    doDisplay();
                }
//                if (mParam.initFocusPosition > -1) {
//                   setFocusPosition(mParam.initFocusPosition);
//                } else if (mParam.initPosition > -1) {
//                    postDelayed(() ->
//                            exeListScrollToPosition(mParam.initPosition)
//                            ,20);
//                }
            }
        } else {
            throw new IllegalArgumentException("Param cannot be null, please call initParams method first!!");
        }

    }

    PresenterSelector selector;

    private void setupData() {
//        Log.d(TAG, "setupData id:" + getId());
        try {
            final ArrayObjectAdapter adapter = new ArrayObjectAdapter(selector);
            adapter.addAll(0, TemplateHelper.Companion.buildTemplateItemListObjectAdapter(mParam.totalCount, mParam.template));
            this.largeListAdapter = adapter;
            this.groupListAdapter = TemplateHelper.Companion.buildGroupObjectAdapter(mParam, mParam.group);
        }catch (Exception e){
//            Log.d(TAG, "setupData 错误 msg:" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void setSelector(PresenterSelector selector) {
        this.selector = selector;
    }

    void setupUpList() {

        largeListView.addItemDecoration(new ItemDecorations.SimpleBetweenItem(mParam.itemGap));

        largeListView.setObjectAdapter(largeListAdapter);

    }

    void setupGroupList() {
        if (groupListView != null) {
            groupListView.initLayoutManager();
            groupListView.addItemDecoration(new ItemDecorations.SimpleBetweenItem(mParam.groupGap));
            groupListView.setObjectAdapter(groupListAdapter);
        }

    }


    public void setPageData(int page, EsArray data) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "updateItemContent setPageData page:" + page + ",data length:" + data.size() + ",id:" + getId());
        }
        if (mData != null && largeListAdapter != null) {
            if (mData.getState(page) > 0) {
                //已经设置过数据
//                Log.e(TAG, "setPageData data already set page:" + page + " return");
                postLayout();
            } else {
                int state = data.size() > 0 ? 1 : -1;
//                Log.e(TAG, "!!!!!setPageData setNewData:state" + state);
                mData.setState(page, state);
                final int pageStart = computePageStartPosition(page);
                try {
                    final int toUpdateCount = Math.min(largeListAdapter.size(), data.size());
//                    Log.e(TAG, "updateItemContent !!!!!setPageData toUpdateCount:" + toUpdateCount + ",dataSize:" + data.size() + ",page:" + page);
                    for (int i = 0; i < toUpdateCount; i++) {
                        final Object o = largeListAdapter.get(pageStart + i);
                        if (o instanceof LazyDataItem) {
//                            Log.e(TAG,"!!!!!setPageData updateContent:"+(pageStart+i));
                            ((LazyDataItem) o).updateContent(data.getMap(i));
                        }
                    }

                } catch (Throwable t) {
                    t.printStackTrace();
                }

                if (largeListView != null) {
//                    final int end = largeListView.getEasyLayoutManager().findLastVisibleItemPosition();
//                    final int count = end - pageStart + 2;
                    notifyLargeListDataChangeByRange(pageStart);
                } else {
//                    Log.e(TAG, "setPageData largeListView == null ");
                    postLayout();
                }
            }
        } else {
            Log.e(TAG, "setPage Error , data " + mData + ",largeListView:" + largeListView);
        }

    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        Log.i(TAG,"requestFocus  direction:"+direction);
        if (direction == View.FOCUS_DOWN && largeListView != null && largeListView.getChildCount() > 0) {
            return largeListView.requestFocus(direction,previouslyFocusedRect);
        } else if (direction == View.FOCUS_UP && groupListView != null && groupListView.getChildCount() > 0){
            return groupListView.requestFocus(direction,previouslyFocusedRect);
        }else {
            return super.requestFocus(direction, previouslyFocusedRect);
        }
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction) {
//        Log.i(TAG,"addFocusables 2 direction:"+direction);
        super.addFocusables(views, direction);
    }



    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
        //
        Log.i(TAG,"addFocusables direction:"+direction);
        if (largeListView != null) {
            Log.i(TAG,"addFocusables direction:"+direction+",childCount:"+largeListView.getChildCount()
                    +",getSelectChildPosition:"+largeListView.getSelectChildPosition()
                    +",getFocusChildPosition:"+largeListView.getFocusChildPosition()
                    +",currentGroup:"+mData.currentGroup
            );

        }
        Log.i(TAG,"addFocusables direction:"+direction);
        if (direction == View.FOCUS_DOWN && largeListView != null && largeListView.getChildCount() > 0) {
            if (largeListView.getSelectChildPosition() < 0 && largeListView.getFocusChildPosition() < 0) {
                if (mData.currentGroup > -1) {
                    int pos = getGroupStart(mData.currentGroup);
                    largeListView.setFocusMemoryPosition(pos);
                }
            }
            Log.d(TAG,"addFocusables largeListView direction:"+direction);
            largeListView.addFocusables(views, direction, focusableMode);
        } else if (direction == View.FOCUS_UP && groupListView != null && groupListView.getChildCount() > 0) {
            Log.i(TAG,"addFocusables add groupListView:"+direction);
            groupListView.addFocusables(views, direction, focusableMode);
        } else {
            Log.e(TAG,"addFocusables super direction:"+direction);
            super.addFocusables(views, direction, focusableMode);
        }
    }

    void notifyLargeListDataChangeByRange(int start) {

        if (largeListView != null) {
//            largeListView.getObjectAdapter().notifyItemRangeChanged(start, count);
            largeListView.notifyItemRangeChanged(start);


        }
    }

    @Override
    public View focusSearch(View focused, int direction) {
        //Log.i(TAG,"focusSearch with focused:"+focused+",direction");
        return super.focusSearch(focused, direction);
    }

    @Override
    public View focusSearch(int direction) {
       // Log.i(TAG,"focusSearch direction "+direction);
        return super.focusSearch(direction);
    }

    void exeChangeGroupOnItemFocus(int pos) {
        doChangeGroupOnItemChange(pos);
    }

    void doChangeGroupOnItemChange(int pos) {
//        Log.i(TAG, "doChangeGroupOnItemChange pos:" + pos + ",mData.currentGroup:" + mData.currentGroup + ",groupListView:" + groupListView);
        if (groupListView != null) {
//            if(mData.currentGroup != pos) {
            //更改group状态
            if (mData.currentGroup != pos) {
                mData.setCurrentGroup(pos);
                groupListView.setSelectChildPosition(pos);
                //groupListView.scrollToPositionWithScrollType(pos);
                if (groupListView.hasFocus() && mParam.isAutoChangeOnFocus) {
                    groupListView.setFocusPosition(pos);
                } else {
                    groupListView.setScrollPosition(pos);
                }
//                RenderUtil.requestNodeLayout(this);
                postLayout(false, 300);
            }
        }
    }

    public void setGroupChildSelectByItemPosition(int pos) {
        try {
            final int groupPos = computeGroupPositionByDisplayItemPos(pos);
            if (groupListView != null) {
//            if(mData.currentGroup != pos) {
                //更改group状态
                groupListView.setSelectChildPosition(groupPos);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    void requestLayoutSelf() {

        try {
            RenderUtil.requestNodeLayout(this);
        } catch (Throwable t) {
            t.printStackTrace();
            Log.e(TAG, "requestLayoutSelf error:" + t.getMessage());
        }
//        Log.e(TAG,"getParent:"+getParent());
//        if(getParent() instanceof CustomLayoutView){
//            ((CustomLayoutView) getParent()).setLayoutRequestFromCustom(true);
//        }
//        requestLayout();
//        RenderUtil.addUpdateLayout(this);
    }

    boolean lastFocusItem = false;

    void exeChangeListPositionByGroupPosition(int groupPosition) {
        final int start = getGroupStart(groupPosition);
//        Log.d(TAG,"exeChangeListPositionByGroupPosition groupPosition:"+groupPosition+",start :"+start);
        if (start > -1) {
            final int end = start + mParam.groupSize - 1;
            final int currentPosition = largeListView.lastDisplayItemPosition;
//            if(currentPosition == -1 || ( currentPosition < start || currentPosition > end) || !lastFocusItem){
            if (currentPosition == -1 || !lastFocusItem) {
                exeListScrollToPosition(start);
                largeListView.lastDisplayItemPosition = -1;
                postLayout(false, 200);
            } else {
//                Log.d(TAG,"exeChangeListPositionByGroupPosition on last focusPosition currentPosition:"+currentPosition+",start :"+start);
            }
        }
    }


    int computePageStartPosition(int page) {
        return page * mParam.pageSize;
    }


    void requestLoadPageData(int page) {
//        Log.d(TAG, "requestLoadPageData page:" + page + ",id:" + getId() + "this:" + this);
        event.notifyLoadPageData(page);
    }

    void exeListScrollToPosition(int pos) {
//        Log.i(TAG,"exeListScrollToPosition pos:"+pos+",getChildCount:"+largeListView.getEasyLayoutManager().getChildCount());
        mData.setCurrentItem(pos);
        //largeListView.scrollToPositionWithScrollType(pos);
//            setInitPosition(pos);
        largeListView.setScrollPosition(pos);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        Log.e(TAG,"onLayout changed changed:"+changed);
        super.onLayout(changed, l, t, r, b);
//        requestFirstShow();
    }

    void onGroupChange(int pos) {
        if (groupListView != null) {
            groupListView.setSelectChildPosition(pos);
        }
//        mData.setCurrentItem(-1);
//        mData.setCurrentGroup(pos);
//        if(mData.currentGroup != pos){
//            //清除记录
        final int groupStart = getGroupStart(pos);
//            final boolean groupChange = pos != mData.currentGroup;
//            Log.e(TAG,"clearSelectFocusMemory,setNew position:"+groupStart+",lastFocusItem:"+lastFocusItem);
        if (!lastFocusItem || isScrollTypePage()) {
            largeListView.clearFocusMemory();
            largeListView.setFocusMemoryPosition(groupStart);
            mData.targetItemPos = groupStart;
        }

//        }
        if (!largeListView.hasFocus()) {
            exeChangeListPositionByGroupPosition(pos);
        }
        onGroupDisplayedOnScrollPageType(pos);
    }

    void onListFocusPositionChange(int itemPos) {
        mData.targetItemPos = itemPos;
        makeGroupRight(itemPos);
    }

    void makeGroupRight(int itemPos) {
        final int groupPosition = computeGroupPositionByDisplayItemPos(itemPos);
//        Log.d(TAG,"makeGroupRight itemPos:"+itemPos+",groupPosition:"+groupPosition);
        doChangeGroupOnItemChange(groupPosition);
        onGroupDisplayedOnScrollPageType(groupPosition);
    }

    boolean isTotalValid() {
        return mParam.totalCount > 0;
    }

    //上边列表Item展示出来时，需要根据展示的item的位置拉取对应数据
    void onLargeListItemLayout(int pos) {
//        Log.d(TAG,"onLargeListItemLayout pos:"+pos+",this:"+getId());
//        Log.i("updateItemContent","onLargeListItemLayout  pos:"+pos);
        if (mData != null) {
            if (isTotalValid()) {
                final int page = computePageByDisplayItemPos(pos);

                final int state = mData.getState(page);
                if (state < 0) {
                    requestLoadPageData(page);
                    mData.setState(page, 0);
                }
            }
        }
    }

    void clearLayoutUpdate() {
        if (postTask != null) {
            removeCallbacks(postTask);
        }
    }

    void clearNotifyData() {
        if (postNotifyDataTask != null) {
            removeCallbacks(postNotifyDataTask);
        }
    }

    void postLayout() {
        postLayout(true, 500, false);
    }

    void postLayout(boolean clear, int delay, boolean blockFocus) {
//            blockFocus();
        if (clear && postTask != null) {
            removeCallbacks(postTask);
        }
        final Runnable task = () -> {
//                releaseFocus();
            requestLayoutSelf();
            postTask = null;
        };
        this.postTask = task;
        postDelayed(task, delay);
    }

    void postLayout(boolean clear, int delay) {
        postLayout(clear, delay, false);
    }


    void requestFirstShow() {
        if (mParam != null && !created && displayed && getVisibility() == View.VISIBLE) {
//            Log.e(TAG, "requestFirstShow exe setup!!!!,this:" + getId());
            setup();
            created = true;
        } else {
//            Log.d(TAG, "requestFirstShow no need created:" + created + ",displayed:" + displayed + ",width:" + getWidth() + ",height:" + getHeight() + ",getVisibility:" + getVisibility());
        }
    }

    int getGroupStart(int pos) {
        int start = -1;
        if (mParam != null) {
            start = isScrollTypePage() ? pos * mParam.groupSize : pos * mParam.groupSize + mParam.scrollTargetOffset;
            start = Math.min(start, mParam.totalCount - 1);
        }
        return start;
    }


    int computePageByDisplayItemPos(int pos) {
        return pos / mParam.pageSize;
    }

    int computeDisplayGroupCount() {
        if (mParam != null) {
            return TemplateHelper.computeDisplayPageCount(mParam.totalCount, mParam.groupSize);
        }
        return -1;
    }

    int computeGroupPositionByDisplayItemPos(int pos) {
        return pos / mParam.groupSize;
    }

    int computePageCount() {
        return mParam.totalCount % mParam.pageSize == 0 ? mParam.totalCount / mParam.pageSize : mParam.totalCount / mParam.pageSize + 1;
    }

    GroupItem getGroupIetm(int pos) {
        if (mParam.enableGroup && groupListView.getObjectAdapter().size() > pos) {
            return (GroupItem) groupListView.getObjectAdapter().get(pos);
        }
        return null;
    }


    public void setSelectChildPosition(int pos) {
//        Log.e(TAG, "setSelectChildPosition:" + pos + " called !!!!");
        if (largeListView != null) {
            largeListView.setSelectChildPosition(pos);
        }
    }

    public void scrollToPosition(int pos, int offset, boolean anim) {
        largeListView.getEasyLayoutManager().scrollToPositionWithOffset(pos, offset);
        postLayout();
//        exeListScrollToPosition(pos,offset);
    }

    public void scrollToPosition(int pos) {
//        if(largeListView != null){
//            largeListView.scrollToPositionWithScrollType(pos);
//        }
        if (largeListView != null && largeListView.hasFocus() && mParam.isAutoChangeOnFocus) {
            setFocusPosition(pos);
        } else {
            setInitPosition(pos);
        }

    }

    int last = -1;

    public boolean requestChildFocus(int pos) {
//        Log.d(TAG,"requestChildFocus pos:"+pos+",this:"+getId());
        if (mParam.scrollType == AdvanceCenterScroller.SCROLL_TYPE_PAGE) {
            largeListView.markPendingFocusPosition(pos);
            int group = computeGroupPositionByDisplayItemPos(pos);
            int start = getGroupStart(group);
//            Log.e(TAG,"setFocusPosition group:"+group+",start:"+start);
            largeListView.setScrollPosition(start);
            largeListView.requestChildFocusDerectly(pos);
        } else {
            largeListView.setFocusPosition(pos);
        }
        postLayout();
        return true;
    }

    public void setFocusPosition(int pos) {
//        Log.e(TAG, "setFocusPosition pos:" + pos + ",this:" + getId());
        if (!displayed && created) {
            mData.pendingDisplayFocusPos = pos;
        } else {
            if (largeListView != null) {
                if (mParam.scrollType == AdvanceCenterScroller.SCROLL_TYPE_PAGE) {
                    largeListView.markPendingFocusPosition(pos);
                    int group = computeGroupPositionByDisplayItemPos(pos);
                    int start = getGroupStart(group);
//                    Log.e(TAG, "setFocusPosition group:" + group + ",start:" + start);
                    largeListView.setScrollPosition(start);
                } else {
                    largeListView.setFocusPosition(pos);
                }
                postLayout();
            }
        }

    }

    public void setDisplay(boolean b) {
//        Log.d(TAG, "setDisplay called b " + b);
        if (b != displayed) {
            displayed = b;
            if (b) {
                requestFirstShow();
                if (created) {
                    doDisplay();
                } else {
                    Log.e(TAG, "setDisplay no create return");
                }
            } else {
                doDismiss();
            }
        }
    }

    void setupListInitScroll() {
//        Log.d(TAG, "setupListInitScroll !!!!");
        if (mParam.initFocusPosition > -1) {
            final int ip = mParam.initFocusPosition;
            final int group = computeGroupPositionByDisplayItemPos(ip);
            if (groupListView != null) {
                groupListView.setSelectChildPosition(group);
            }
            setFocusPosition(ip);
        } else if (mParam.initPosition > -1) {
            final int ip = mParam.initPosition;
//            Log.e(TAG, "setScrollPosition pos:" + mParam.initPosition + ",this:" + getId());
            final int group = computeGroupPositionByDisplayItemPos(ip);
//            if(groupListView != null) {
//                groupListView.setSelectChildPosition(group);
//            }
            setInitPosition(mParam.initPosition);
            makeGroupRight(mParam.initPosition);
            postLayout();
        }
    }

    public void doDisplay() {
//        Log.d(TAG, "doDisplay !!!!");
        setVisibility(View.VISIBLE);
//        RenderUtil.requestNodeLayout(this);
        if (mData.pendingDisplayFocusPos > -1) {
            final int pos = mData.pendingDisplayFocusPos;
//            Log.e(TAG, "doDisplay doPendingDisplayFocusPos pos:" + mData.pendingDisplayFocusPos);
            mData.pendingDisplayFocusPos = -1;
            setFocusPosition(pos);
//            makeGroupRight(mda);
        }
        postLayout(false, 100);
    }

    public void doDismiss() {
//        Log.d(TAG, "doDismiss !!!!");
        if (created) {
            largeListView.setPendingFocusPosition(-1);
        }
        if (mData != null) {
            mData.currentGroup = -1;
        }
        setVisibility(View.INVISIBLE);
    }


    public void setInitPosition(int pos) {
        if(largeListView != null) {
//            Log.e(TAG, "setInitPosition pos:" + pos + ",this:" + getId() + ",largeListView:" + largeListView);
            if (mParam.scrollType == AdvanceCenterScroller.SCROLL_TYPE_PAGE) {
                int group = computeGroupPositionByDisplayItemPos(pos);
                int start = getGroupStart(group);
//                Log.e(TAG, "setInitPosition group:" + group + ",start:" + start);
                largeListView.setScrollPosition(start);
            } else {
                largeListView.setScrollPosition(pos);
            }
            makeGroupRight(pos);
        }else{
            Log.e(TAG,"setInitPosition error on largeListView null pos:"+pos);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void layoutArrows(int w, int h) {
        if (arrow != null) {
            final int aw = mParam.arrowWidth;
            final int ah = mParam.arrowHeight;
            final int leftMargin = mParam.arrowMarginLeft;
            final int rightMargin = mParam.arrowMarginRight;
//            final int top = (int) ((w - ah) * 0.5f);
            final int top = (int) ((h - ah) * 0.5f);
            arrow[0].setBounds(leftMargin, top, leftMargin + aw, top + ah);
            int rightLeft = w - rightMargin - aw;
            arrow[1].setBounds(rightLeft, top, rightLeft + aw, top + ah);
            invalidate();
//            Log.d(TAG,"layoutArrows called arrow:"+arrow[0].getBounds()+",top:"+top);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (arrow != null) {
            if (drawArrow[0]) {
                arrow[0].draw(canvas);
            }
            if (drawArrow[1]) {
                arrow[1].draw(canvas);
            }
        }
    }

    private void notifyItemClick(int pos) {
        final Object data = largeListView.getObjectAdapter().get(pos);
        if (data instanceof TemplateItem) {
            event.notifyItemClick(pos, ((TemplateItem) data).getContent());
        }
//        Log.e(TAG, "notifyItemClick: ============pos==============" + pos);
        largeListView.setSelectChildPosition(pos);
    }

    @Override
    public void notifyRecyclerViewFocusChanged(boolean hasFocus, boolean isOldFocusDescendantOf, View oldFocus, View focused) {
//        Log.e(TAG,"onHasFocusChange hasFocus"+hasFocus);
        if (event != null) {
            event.triggerFocusChange(this, hasFocus);
        }
    }

    int lastGroup = -1;

    void onGroupDisplayedOnScrollPageType(int group) {
        if (lastGroup != group) {
            lastGroup = group;
            if (mParam.scrollType == AdvanceCenterScroller.SCROLL_TYPE_PAGE) {
                int groupCount = computeDisplayGroupCount();
//                Log.d(TAG,"onPageDisplayedOnScrollPageType groupCount:"+groupCount+",group:"+group);
                if (groupCount < 2) {//只有一页
                    setArrowVisible(false, false);
                } else if (group >= groupCount - 1) {//
                    setArrowVisible(true, false);
                } else if (group < 1) {//第一页
                    setArrowVisible(false, true);
                } else {
                    setArrowVisible(true, true);
                }

            }
        }
    }

    void setArrowVisible(boolean leftVisi, boolean rightVisi) {
        if (arrow != null) {
            drawArrow[0] = leftVisi;
            drawArrow[1] = rightVisi;
//            Log.e(TAG,"setArrowVisible leftVisi:"+leftVisi+",rightVisi:"+rightVisi);
            arrow[0].setVisible(leftVisi, true);
            arrow[1].setVisible(rightVisi, true);
            arrow[0].invalidateSelf();
            arrow[1].invalidateSelf();
            postInvalidateDelayed(16);
        }
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (focusWatcher == null) {
            focusWatcher = new FocusWatcher(this);
        } else {
            focusWatcher.stopListenGlobalFocusChange();
        }
        focusWatcher.listenGlobalFocusChangeIfNeed();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (focusWatcher != null) {
            focusWatcher.stopListenGlobalFocusChange();
            focusWatcher = null;
        }
        clearNotifyData();
        clearLayoutUpdate();
        postTask = null;
        postNotifyDataTask = null;
    }

    @Override
    public ViewGroup getTarget() {
        return this;
    }

    @Override
    public View getHostView() {
        return this;
    }

    public void destroy() {
        if (focusWatcher != null) {
            focusWatcher.stopListenGlobalFocusChange();
            focusWatcher = null;
        }
        clearNotifyData();
        clearLayoutUpdate();
        postTask = null;
        postNotifyDataTask = null;
        try {
            if (largeListView != null) {
                largeListView.destroy();
                largeListView = null;
            }
            if (groupListView != null) {
                groupListView.destroy();
                groupListView = null;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        try {
            if (largeListAdapter != null) {
                largeListAdapter.clear();
            }
            if (groupListAdapter != null) {
                groupListAdapter.clear();
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        event = null;
        mData = null;
        mParam = null;
        arrow = null;
    }

    private final class MyLargeListView extends EasyListView {

        private OnClickListener clickListener;
        int lastDisplayItemPosition = -1;
        //        boolean pendingLayout = false;
        HashMap<Integer, Boolean> pendingLayoutMap;
        int[] pd = new int[2];
        int scrollState = SCROLL_STATE_IDLE;
        private boolean pendingLayout;

        public MyLargeListView(@NonNull Context context) {
            super(context, false);
            clickListener = v -> {
                final int p = getChildAdapterPosition(v);
                notifyItemClick(p);

            };
            pendingLayoutMap = new HashMap<>();
        }

        public void setPendingToUpdate(int pos) {
            this.pendingLayoutMap.put(pos, true);
        }

        public void clearPendingToUpdate(int pos) {
            this.pendingLayoutMap.put(pos, false);
        }


        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            layoutArrows(LargeListViewGroup.this.getWidth(), h);
        }

        @Override
        public void setScrollType(int type) {
            super.setScrollType(type);
        }

        @Override
        public void requestChildFocus(View child, View focused) {
            super.requestChildFocus(child, focused);
//            Log.v(TAG,"largeList requestChildFocus :"+child.getId()+",mFocusChildPosition:"+mFocusChildPosition);
            event.notifyItemFocus(mFocusChildPosition);
            onListFocusPositionChange(mFocusChildPosition);
            lastFocusItem = true;
            this.lastDisplayItemPosition = mFocusChildPosition;
        }

        @Override
        protected void onLayoutCompleted(State state) {
            super.onLayoutCompleted(state);
//            Log.e(TAG,"onLayoutCompleted itemCount:"+state.getItemCount()+",getChildCount:"+getChildCount());
//            if(scrollType != -1 && getLayoutManager() != null){
//                final int first = getEasyLayoutManager().findFirstCompletelyVisibleItemPosition();
//                final int last = getEasyLayoutManager().findLastCompletelyVisibleItemPosition();
//
//                if(first == 0 && last  >= mParam.totalCount -1){
//                    Log.e(TAG,"onLayoutCompleted 数据较小，关闭滚动 ");
//                    setScrollType(-1);
//                }
//            }

        }

        @Override
        protected void onLayoutItem(View child, int pos) {
            super.onLayoutItem(child, pos);
            onLargeListItemLayout(pos);
            child.setOnClickListener(clickListener);
            if (pendingLayoutMap != null && pendingLayoutMap.containsKey(pos)) {
                boolean b = pendingLayoutMap.get(pos);
                if (b) {
//                    Log.e(TAG, "onLayoutItem on PendingUPdate item pos:" + pos);
                    updateContent(pos, child);
                }
            }
        }

        boolean isInScrolling() {
            return scrollState != SCROLL_STATE_IDLE;
        }


        @Override
        public void onScrollStateChanged(int state) {
            super.onScrollStateChanged(state);
            this.scrollState = state;
            if (pendingLayout) {
                if (state == SCROLL_STATE_IDLE) {
//                    Log.e(TAG, "updateItemContent onScrollStateChanged on pendingLayout updateLayout notifyItemRangeChanged");

                    //getObjectAdapter().notifyItemRangeChanged(pd[0],pd[1]);
                    postNotify(hasFocus(), pd[0]);
                    pendingLayout = false;
                } else {
                    clearNotifyData();
                }
            }
        }

        @Override
        protected void onLayoutChildren(State state) {
            super.onLayoutChildren(state);
//            Log.e(TAG,"onLayoutChildren itemCount:"+getChildCount()+",layoutCount:"+getLayoutManager().getChildCount());
        }

        void updateContent(int pos, View child) {
            if (child instanceof PendingItemView) {
                final Object o = getObjectAdapter().get(pos);
                if (o instanceof TemplateItem && ((TemplateItem) o).isNotEmpty()) {
//                    Log.i(TAG,"updateItemContent  child set PendingContentView pos:"+pos);
                    pendingLayoutMap.put(pos, false);
                    ((PendingItemView) child).setContentData(o);
                } else {
//                    Log.e(TAG,"updateItemContent Erorr item is null pos:"+pos);
                }
            } else {
//                Log.e(TAG,"updateItemContent error child not impl PendingContentView");
            }
        }

        void postNotify(boolean block, int start) {
            clearNotifyData();
            if (block) {
//                blockFocus();
            }
            postNotifyDataTask = () -> {
//                Log.e(TAG,"postNotify itemCount:"+getChildCount()+",layoutCount:"+getLayoutManager().getChildCount()+" start:"+start);
//                getAdapter().notifyItemRangeChanged(start,count);
                if (isScrollTypePage()) {
                    for (int i = 0; i < mParam.groupSize; i++) {
                        pendingLayoutMap.put(start + i, true);
                    }
                } else {
                    //由于存在个别item已经渲染出来，存在无法刷新的问题，所以刷新范围扩大
                    int toUpdateStart = Math.max(start - mParam.updateAdditionRange, 0);
                    int toUpdateEnd = Math.min(toUpdateStart + mParam.pageSize + mParam.updateAdditionRange, getLayoutManager().getItemCount()) + 1;
//                    Log.e(TAG,"updateItemContent toUpdateStart:"+toUpdateStart+",toUpdateEnd:"+toUpdateEnd);
                    for (int i = toUpdateStart; i < toUpdateEnd; i++) {
                        pendingLayoutMap.put(i, true);
                    }
                }
                for (int i = 0; i < getLayoutManager().getChildCount(); i++) {
                    final View child = getLayoutManager().getChildAt(i);
                    final int pos = getChildAdapterPosition(child);
                    updateContent(pos, child);
                }
                if (block) {
//                    releaseFocus();
                }
                requestLayoutSelf();
            };
            LargeListViewGroup.this.postDelayed(postNotifyDataTask, 20);
        }

        private boolean isEndPosition(int pos) {
            return ((pos + 1) % mParam.pageDisplayCount) == 0;
        }

        private boolean isStartPosition(int pos) {
            return ((pos + 1) % mParam.pageDisplayCount) == 1;
        }


        @Override
        public View onInterceptFocusSearch(@NonNull View focused, int direction) {
            View v = null;
            if (direction == View.FOCUS_DOWN && groupListView != null) {
                final int groupPosition = mData.currentGroup;
                v = groupListView.getEasyLayoutManager().findViewByPosition(groupPosition);
            }

            if (mParam.blockFocus && direction == View.FOCUS_LEFT && mFocusChildPosition == 0) {
//                Log.e(TAG,"block left FocusON position 0");
                return focused;
            }

            if (isScrollTypePage() && (direction == View.FOCUS_LEFT || direction == View.FOCUS_RIGHT) && isInScrolling()) {
//                Log.e(TAG,"block in Scrolling");
                return focused;
            }

            if (v == null && scrollType == AdvanceCenterScroller.SCROLL_TYPE_PAGE) {

                //如果当前在边缘
                if (direction == FOCUS_RIGHT) {
                    if (mParam.pageDisplayCount < 1) {
                        throw new IllegalArgumentException("pageDisplayCount cannot be null !!！please set with InitParams");
                    }
                    final boolean isEnd = isEndPosition(mFocusChildPosition);
                    final int next = mFocusChildPosition + 1;
//                    Log.d(TAG,"onInterceptFocusSearchByPage isEnd:"+isEnd+",pos:"+mFocusChildPosition);
                    if (isEnd) {
                        if (next < getObjectAdapter().size()) {
                            blockFocus();
//                            clearFocus();
                            markPendingFocusPosition(next);
//                            smoothScrollBy(mParam.contentWidth > 0 ? mParam.contentWidth : getWidth() + mParam.itemGap - getPaddingLeft() - getPaddingRight(), 0);
                            getEasyLayoutManager().smoothScrollToPositionTop(this, next);
                            postDelayed(() -> {
                                markPendingFocusPosition(-1);
                                releaseFocus();
                                requestChildFocusDerectly(next);
                            }, 30);
                            v = focused;
                        }
                    }
                } else if (direction == FOCUS_LEFT) {
                    if (mParam.pageDisplayCount < 1) {
                        throw new IllegalArgumentException("pageDisplayCount cannot be null !!！please set with InitParams");
                    }
                    final boolean isStart = isStartPosition(mFocusChildPosition);
                    final int prev = mFocusChildPosition - 1;
//                    Log.d(TAG,"onInterceptFocusSearchByPage isStart:"+isStart+",pos:"+mFocusChildPosition);
                    if (isStart) {
                        if (prev > -1) {
                            blockFocus();
//                            clearFocus();
                            markPendingFocusPosition(prev);
//                            smoothScrollBy(-(mParam.contentWidth > 0 ? mParam.contentWidth : getWidth() + mParam.itemGap - getPaddingLeft() - getPaddingRight()), 0);
                            getEasyLayoutManager().smoothScrollToPositionTop(this, prev - mParam.groupSize + 1 > -1 ? prev - mParam.groupSize + 1 : 0);
                            postDelayed(() -> {
                                markPendingFocusPosition(-1);
                                releaseFocus();
                                requestChildFocusDerectly(prev);
                            }, 30);
                            v = focused;
                        }
                    }
                }
            }

            if (v == null) {
                v = super.onInterceptFocusSearch(focused, direction);
            }

            return v;
        }


        public void notifyItemRangeChanged(int start) {
            if (isInScrolling()) {
//                Log.e(TAG,"notifyItemRangeChanged return on Scrolling!!!!");
                pd[0] = start;
//                pd[1] = count;
                pendingLayout = true;
            } else {
//                Log.i(TAG,"notifyLargeListDataChangeByRange start:"+start+",count:"+count+",largeListView:"+largeListView);
//                Log.i("zhaopeng","notifyLargeListDataChangeByRange start:"+start+",count:"+count);
//                getObjectAdapter().notifyItemRangeChanged(start,count);
//                postLayout(true,100,true);
                postNotify(hasFocus(), start);
            }
        }
    }

    @Override
    public void requestLayout() {
//        Log.e(CustomLayoutView.TAG,"LargeList requestLayout !!,id:"+getId());
//        if(getParent() instanceof CustomLayoutView){
//            ((CustomLayoutView) getParent()).setLayoutRequestFromCustom(true);
//        }
//        RenderUtil.addUpdateLayout(this);
        super.requestLayout();
//        Log.d(TAG,"requestLayout called!!!!");
        postLayout(false, 16);

    }

    private final class MyGroupListView extends EasyListView {
        public MyGroupListView(@NonNull Context context) {
            super(context, false);
        }

        private int lastCheckPos = -1;

        @Override
        public void requestChildFocus(View child, View focused) {
            super.requestChildFocus(child, focused);
            event.notifyGroupPositionEvent(mFocusChildPosition);
            if (lastCheckPos > -1 && lastCheckPos != mFocusChildPosition) {
                onGroupChange(mFocusChildPosition);
            } else {
//                Log.e(TAG, "MyGroupListView onGroupChange do nothing on lastCheckPos == mFocusChildPosition");
            }
            lastCheckPos = mFocusChildPosition;
            lastFocusItem = false;
        }

        @Override
        public View onInterceptFocusSearch(@NonNull View focused, int direction) {
            View v = null;
            if (mParam.blockFocus && direction == View.FOCUS_LEFT && mFocusChildPosition == 0) {
//                Log.e(TAG,"block left FocusON position 0");
                return focused;
            }
            if (direction == View.FOCUS_UP) {
//                final int pos = largeListView.getFocusChildPosition();
//                v = largeListView.getEasyLayoutManager().findViewByPosition(pos);

                v = largeListView.getEasyLayoutManager().findViewByPosition(mData.targetItemPos);
//                Log.e(TAG,"onInterceptFocusSearch find targetItemPos:"+mData.targetItemPos);
            }
            if (v == null) {
                v = super.onInterceptFocusSearch(focused, direction);
            }

            return v;
        }
    }


    public static class Param {
        public int itemGap = 10;
        public int groupGap = 50;
        public int groupItemWidth = 0;
        public int groupItemHeight = 0;
        public int scrollTargetOffset = 1;
        boolean enableGroup;
        int totalCount;
        int pageSize;
        int groupSize;
        int pageDisplayCount;
        int initPosition;
        int initFocusPosition;
        int scrollType;
        int contentHeight;
        int groupHeight;
        int groupTopMargin;
        int preLoadNumber = 3;
        int itemWidth;
        int itemHeight;
        int contentWidth = -1;
        int arrowWidth = 24;
        int arrowHeight = 42;
        int arrowMarginRight = 45;
        int arrowMarginLeft = 45;
        int disableScrollOnMinCount = 3;
        int updateAdditionRange = 3;
        boolean blockFocus = true;
        boolean isAutoChangeOnFocus = true;
        EsMap template;
        EsMap group;
        private int marginLeft;
        private int paddingForPageLeft = 20;
        private int paddingForPageRight = 20;

        void apply(EsMap map, EsMap template) {
            ScreenAdapt screenAdapt = ScreenAdapt.getInstance();

            this.pageSize = map.containsKey("pageSize") ? map.getInt("pageSize") : 100;
            this.initPosition = map.containsKey("initPosition") ? map.getInt("initPosition") : -1;

            this.initFocusPosition = map.containsKey("initFocusPosition") ? map.getInt("initFocusPosition") : -1;
            this.contentWidth = map.containsKey("contentWidth") ? screenAdapt.transform(map.getInt("contentWidth")) : -1;
            this.marginLeft = map.containsKey("marginLeft") ? screenAdapt.transform(map.getInt("marginLeft")) : -1;
            this.paddingForPageLeft = screenAdapt.transform(map.containsKey("paddingForPageLeft") ? map.getInt("paddingForPageLeft") : 20);
            this.paddingForPageRight = screenAdapt.transform(map.containsKey("paddingForPageRight") ? map.getInt("paddingForPageRight") : 20);
            this.marginLeft = map.containsKey("marginLeft") ? screenAdapt.transform(map.getInt("marginLeft")) : -1;
            this.scrollType = map.containsKey("scrollType") ? map.getInt("scrollType") : 0;
            this.arrowMarginLeft = screenAdapt.transform(map.containsKey("arrowMarginLeft") ? map.getInt("arrowMarginLeft") : 45);
            this.arrowMarginRight = screenAdapt.transform(map.containsKey("arrowMarginRight") ? map.getInt("arrowMarginRight") : 45);
            this.arrowWidth = screenAdapt.transform(map.containsKey("arrowWidth") ? map.getInt("arrowWidth") : 24);
            this.arrowHeight = screenAdapt.transform(map.containsKey("arrowHeight") ? map.getInt("arrowHeight") : 42);
            this.arrowMarginRight = screenAdapt.transform(map.containsKey("arrowMarginRight") ? map.getInt("arrowMarginRight") : 45);
            this.scrollType = map.containsKey("scrollType") ? map.getInt("scrollType") : 0;
            this.disableScrollOnMinCount = map.containsKey("disableScrollOnMinCount") ? map.getInt("disableScrollOnMinCount") : 3;
            this.updateAdditionRange = map.containsKey("updateAdditionRange") ? map.getInt("updateAdditionRange") : 3;
            this.scrollTargetOffset = map.containsKey("scrollTargetOffset") ? map.getInt("scrollTargetOffset") : 1;
            this.blockFocus = !map.containsKey("blockFocus") || map.getBoolean("blockFocus");
            this.isAutoChangeOnFocus = !map.containsKey("isAutoChangeOnFocus") || map.getBoolean("isAutoChangeOnFocus");
            this.enableGroup = !map.containsKey("enableGroup") || map.getBoolean("enableGroup");
            this.template = template; //number / leftRight / topDown / text

            if (template != null) {
                this.itemWidth = screenAdapt.transform(template.getInt("width"));
                this.itemHeight = screenAdapt.transform(template.getInt("height"));
            }
            this.groupHeight = screenAdapt.transform(map.getInt("groupHeight"));
            this.groupSize = map.getInt("groupSize");
            this.pageDisplayCount = groupSize;
            if (map.containsKey("group")) {
                final EsMap g = map.getMap("group");
                this.groupGap = screenAdapt.transform(g.getInt("itemGap"));
                this.groupItemWidth = screenAdapt.transform(g.getInt("itemWidth"));
                this.groupItemHeight = screenAdapt.transform(g.getInt("itemHeight"));
                this.group = g;
            }
            this.groupTopMargin = screenAdapt.transform(map.getInt("groupTopMargin"));
            this.totalCount = map.getInt("totalCount");
            this.contentHeight = screenAdapt.transform(map.getInt("contentHeight"));
            this.itemGap = screenAdapt.transform(map.getInt("itemGap"));
            this.preLoadNumber = map.getInt("preLoadNumber");

        }

        @Override
        public String toString() {
            return "Param{" +
                    "itemGap=" + itemGap +
                    ", groupGap=" + groupGap +
                    ", groupItemWidth=" + groupItemWidth +
                    ", groupItemHeight=" + groupItemHeight +
                    ", enableGroup=" + enableGroup +
                    ", totalCount=" + totalCount +
                    ", pageSize=" + pageSize +
                    ", groupSize=" + groupSize +
                    ", pageDisplayCount=" + pageDisplayCount +
                    ", initPosition=" + initPosition +
                    ", initFocusPosition=" + initFocusPosition +
                    ", scrollType=" + scrollType +
                    ", contentHeight=" + contentHeight +
                    ", groupHeight=" + groupHeight +
                    ", groupTopMargin=" + groupTopMargin +
                    ", preLoadNumber=" + preLoadNumber +
                    ", itemWidth=" + itemWidth +
                    ", itemHeight=" + itemHeight +
                    ", template=" + template +
                    ", group=" + group +
                    '}';
        }
    }


    private final static class MyData {
        private final PageData[] pageDataArray;
        int currentGroup = -1;
        int currentItem = -1;
        int targetItemPos = -1;
        int currentFocus = -1;
        int pendingDisplayFocusPos = -1;

        private MyData(int size) {
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

    private static class PageData {
        int state = -1;//-1 没数据 0 加载中 1 加载成功
    }


    private final class MyLinearLayoutScroller extends LinearSmoothScroller {

        public MyLinearLayoutScroller(Context context) {
            super(context);
        }

        @Override
        protected int calculateTimeForScrolling(int dx) {
            return 16;
        }

        @Override
        protected int calculateTimeForDeceleration(int dx) {
            return 16;
        }
    }


}
