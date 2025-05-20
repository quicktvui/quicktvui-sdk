package com.quicktvui.support.ui.selectseries;

import static com.quicktvui.hippyext.views.fastlist.Utils.getHippyContext;
import static com.quicktvui.hippyext.views.fastlist.Utils.getRenderNode;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.quicktvui.support.ui.largelist.AdvanceCenterScroller;
import com.quicktvui.support.ui.largelist.FocusWatcher;
import com.quicktvui.support.ui.largelist.GroupItem;
import com.quicktvui.support.ui.largelist.TemplateItemPresenterSelector;
import com.quicktvui.hippyext.RenderUtil;
import com.quicktvui.base.ui.TriggerTaskHost;
import com.quicktvui.support.ui.selectseries.components.GroupListView;
import com.quicktvui.support.ui.selectseries.components.LargeListView;
import com.quicktvui.support.ui.selectseries.utils.MyTemplateHelper;
import com.quicktvui.support.ui.selectseries.utils.TemplateHelper;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.uimanager.HippyViewBase;
import com.tencent.mtt.hippy.uimanager.NativeGestureDispatcher;
import com.tencent.mtt.hippy.uimanager.RenderNode;

import java.util.ArrayList;
import java.util.List;

import com.quicktvui.support.ui.R;

import com.quicktvui.support.ui.selectseries.bean.LazyDataItem;
import com.quicktvui.support.ui.selectseries.bean.MyData;
import com.quicktvui.support.ui.selectseries.bean.Param;
import com.quicktvui.support.ui.selectseries.bean.TemplateItem;
import com.quicktvui.support.ui.v7.widget.RecyclerView;
import com.quicktvui.support.ui.legacy.misc.ItemDecorations;
import com.quicktvui.support.ui.leanback.ArrayObjectAdapter;
import com.quicktvui.support.ui.leanback.PresenterSelector;

public class SelectSeriesViewGroup extends LinearLayout implements HippyViewBase, FocusWatcher.FocusWatch, TriggerTaskHost {

    public static final String TAG = SelectSeriesViewGroup.class.getSimpleName();

    public Param mParam; // 存放的是基础属性模板
    public MyData mData; // 存放的是状态

    RenderNode rootRenderNode;
    HippyEngineContext engineContext;
    int customIndex = 0;

    public LargeListView largeListView;
    public GroupListView groupListView;
    public EventSender event;

    ArrayObjectAdapter largeListAdapter;
    ArrayObjectAdapter groupListAdapter;
    private Runnable postTask;
    public Runnable postNotifyDataTask;

    FocusWatcher focusWatcher;

    private RecyclerView.ItemDecoration centerPaddingItemDecoration;

    private boolean displayed = false;

    private boolean created = false;

    private Context mContext;

    public Drawable[] arrow;
    public boolean[] drawArrow;

    public boolean groupUp = false;

    private int beforeDisplayFocusPos = -1;

    public SelectSeriesViewGroup(Context context) {
        this(context, null);
    }

    public SelectSeriesViewGroup(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectSeriesViewGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
    }

    public void setCustomIndex(int index) {
        this.customIndex = index;
    }

    private void init() {
        final TemplateItemPresenterSelector ps = TemplateHelper.setupPresenters();

        MyTemplateHelper.initTemplate(ps);
        setSelector(ps);
    }

    public void setGroupParam(HippyMap map) {
        getMParam().setGroupData(map);
        groupUp = getMParam().groupUp;
    }

    public void setScrollParam(HippyMap map) {
        getMParam().setScrollParam(map);
    }

    public void setCommonParam(HippyMap map) {
        getMParam().setCommonParam(map);
    }

    public void setInitData(int totalCount, int pageSize) {
        getMParam().setCoreData(totalCount, pageSize);

        init();
        buildCustomView();
        onPramSet();
    }

    private Param getMParam() {
        if (mParam == null) {
            mParam = new Param();
        }
        return mParam;
    }

    List<RenderNode> itemRenderNodes;

    private void buildCustomView() {
        engineContext = getHippyContext(this);
        rootRenderNode = getRenderNode(this);
        if (rootRenderNode.getChildCount() < 1) {
            throw new IllegalArgumentException("选集使用自定义item样式 必须传入view模版");
        }
        if (itemRenderNodes == null) {
            itemRenderNodes = new ArrayList<>();
        } else {
            itemRenderNodes.clear();
        }
        for (int i = 0; i < rootRenderNode.getChildCount(); i++) {
            itemRenderNodes.add(rootRenderNode.getChildAt(i));
        }
    }

    void onPramSet() {
        if (mData == null) {
            event = new EventSender(this);
            int pageSize = computePageCount();
            mData = new MyData(pageSize);
            if (beforeDisplayFocusPos > -1) {
                mData.pendingDisplayFocusPos = beforeDisplayFocusPos;
                beforeDisplayFocusPos = -1;
            }
            setupData(); // 初始化并配置两个adapter
            requestFirstShow();
        }
    }

    private void setupViews() {
        this.setOrientation(VERTICAL);
        removeAllViews();
        largeListView = new LargeListView(getContext(), this);
        largeListView.initLayoutManager();
        if (mParam.totalCount <= mParam.disableScrollOnMinCount) {
            largeListView.setScrollType(-1);
        } else {
            largeListView.setScrollType(mParam.scrollType);
        }
        setClipChildren(false);
        largeListView.setClipChildren(false);

        //setClipToPadding(false)👇 XingRuGeng Add 解决HomeItemView图标被遮挡问题
        largeListView.setClipToPadding(isScrollTypePage());
        int width = mParam.contentWidth;
        int height = mParam.contentHeight;
        if (height < 0) {
            height = getItemRenderNode().getHeight();
        }
        LayoutParams lpUp;
        if (isScrollTypePage()) {
            if (groupUp) {
                lpUp = new LayoutParams(width < 0 ? ViewGroup.LayoutParams.MATCH_PARENT
                        //                : width + mParam.paddingForPageRight + mParam.paddingForPageLeft, height);
                        : width, height);
                largeListView.setPadding(mParam.paddingForPageLeft, 0, mParam.paddingForPageRight, 0);
                //            lpUp.leftMargin = mParam.marginLeft;
                lpUp.gravity = Gravity.CENTER_HORIZONTAL;
            } else {
                // 换成本身占满父布局的方案
                lpUp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                int paddingForPageLeft, paddingForPageRight;
                if (width < 0) {
                    paddingForPageLeft = mParam.paddingForPageLeft;
                    paddingForPageRight = mParam.paddingForPageRight;
                } else {
                    int halfPadding = (int) ((getWidth() - width) * 0.5f + 0.5f);
                    paddingForPageLeft = halfPadding + mParam.paddingForPageLeft;
                    paddingForPageRight = halfPadding + mParam.paddingForPageRight;
                }
                largeListView.setPadding(paddingForPageLeft, 0, paddingForPageRight, 0);
            }
        } else {
            lpUp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
            lpUp.leftMargin = mParam.marginLeft;
            largeListView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                    super.getItemOffsets(outRect, view, parent, state);
                    int pos = parent.getChildAdapterPosition(view);
                    int padding;
                    if (width < 0) {
                        padding = 80;
                    } else {
                        padding = (int) ((parent.getWidth() - width) * 0.5);
                    }
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

        if (!groupUp)
            addView(largeListView, lpUp);
//        largeListView.setChildSize(mParam.itemWidth);

        if (mParam.enableGroup) {
            groupListView = new GroupListView(getContext(), this);
            groupListView.setClipChildren(false);
            LayoutParams lp = new LayoutParams(width < 0 ? ViewGroup.LayoutParams.MATCH_PARENT : width,
                    mParam.groupHeight > 0 ? mParam.groupHeight
                            : mParam.groupItemHeight > 0 ? mParam.groupItemHeight
                            : getHeight() - height - mParam.groupTopMargin); // mParam.groupHeight
            if (!groupUp)
                lp.topMargin = mParam.groupTopMargin;
            else
                lp.bottomMargin = mParam.groupTopMargin;
            lp.gravity = Gravity.CENTER_HORIZONTAL;
            lp.leftMargin = mParam.groupMarginLeft > 0 ? mParam.groupMarginLeft :
                    (width < 0 ? 60 : 0);
            addView(groupListView, lp);
//            groupListView.setChildSize(mParam.groupItemWidth);
        }

        if (groupUp)
            addView(largeListView, lpUp);

        if (isScrollTypePage()) {
            Drawable left = ContextCompat.getDrawable(getContext(), R.drawable.icon_serier_left_arrow);

            Drawable right = ContextCompat.getDrawable(getContext(), R.drawable.icon_serier_right_arrow);

            this.arrow = new Drawable[]{left, right};
            this.drawArrow = new boolean[]{false, false};
        }
    }

    public boolean isScrollTypePage() {
        return mParam != null && mParam.scrollType == AdvanceCenterScroller.SCROLL_TYPE_PAGE;
    }

    public void setup() {
        if (mParam != null) {
            if (largeListView == null) {
                setupViews();
                setupUpList();
                setupGroupList();
                setupListInitScroll();
                if (displayed) {
                    doDisplay();
                } else {
                    postLayout(true, 300);
                }
            }
        } else {
            throw new IllegalArgumentException("Param cannot be null, please call initParams method first!!");
        }

    }

    PresenterSelector selector;

    private void setupData() {
        try {
            final ArrayObjectAdapter adapter = new ArrayObjectAdapter(selector);
            adapter.addAll(0, TemplateHelper.Companion.buildTemplateItemListObjectAdapter(mParam.totalCount));
            this.largeListAdapter = adapter;
            if (mParam != null && mParam.enableGroup)
                this.groupListAdapter = TemplateHelper.Companion.buildGroupObjectAdapter(mParam, mParam.group);
        } catch (Exception e) {
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


    public void setPageData(int page, HippyArray data) {
        if (mData != null && largeListAdapter != null) {
            if (mData.getState(page) > 0) {
                //已经设置过数据
                postLayout();
            } else {
                int state = data.size() > 0 ? 1 : -1;
                mData.setState(page, state);
                final int pageStart = computePageStartPosition(page);
                try {
                    final int toUpdateCount = Math.min(largeListAdapter.size() - pageStart, data.size());
//                    Log.e(TAG, "updateItemContent !!!!!setPageData toUpdateCount:" + toUpdateCount + ",dataSize:" + data.size() + ",page:" + page);
                    for (int i = 0; i < toUpdateCount; i++) {
                        final Object o = largeListAdapter.get(pageStart + i);
                        if (o instanceof LazyDataItem) {
                            ((LazyDataItem) o).updateContent(data.getMap(i));
                        }
                    }
//                        ((SimpleItemAnimator) Objects.requireNonNull(largeListView.getItemAnimator())).setSupportsChangeAnimations(false);
                    largeListAdapter.notifyArrayItemRangeChanged(pageStart,
                            toUpdateCount);

                } catch (Throwable t) {
                    t.printStackTrace();
                }


//                if (largeListView != null) {
//                    notifyLargeListDataChangeByRange(pageStart);
//                } else {
//                    postLayout();
//                }
            }
        } else {
            Log.e(TAG, "setPage Error , data " + mData + ",largeListView:" + largeListView);
        }

    }


    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
//        if (direction == View.FOCUS_DOWN // 之前FOCUS_DOWN
        if (direction == (groupUp ? View.FOCUS_UP : View.FOCUS_DOWN) // 之前FOCUS_DOWN
                && largeListView != null && largeListView.getChildCount() > 0) {
            if (largeListView.getSelectChildPosition() < 0 && largeListView.getFocusChildPosition() < 0) {
                if (mData.currentGroup > -1) {
                    int pos = getGroupStart(mData.currentGroup);
                    largeListView.setFocusMemoryPosition(pos);
                }
            }
            largeListView.addFocusables(views, direction, focusableMode);
//        } else if (direction == View.FOCUS_UP
        } else if (direction == (groupUp ? View.FOCUS_DOWN : View.FOCUS_UP)
                && groupListView != null && groupListView.getChildCount() > 0) {
            groupListView.addFocusables(views, direction, focusableMode);
        } else {
            super.addFocusables(views, direction, focusableMode);
        }
    }

    void notifyLargeListDataChangeByRange(int start) {

        if (largeListView != null) {
            largeListView.notifyItemRangeChanged(start);
        }
    }

    @Override
    public View focusSearch(View focused, int direction) {
        return super.focusSearch(focused, direction);
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
        if (mParam == null || !mParam.enableGroup) {
            return;
        }
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

    public void requestLayoutSelf() {

        try {
            RenderUtil.requestNodeLayout(this);
        } catch (Throwable t) {
            t.printStackTrace();
            Log.e(TAG, "requestLayoutSelf error:" + t.getMessage());
        }
    }

    public boolean lastFocusItem = false;

    void exeChangeListPositionByGroupPosition(int groupPosition) {
        final int start = getGroupStart(groupPosition);
        if (start > -1) {
            final int currentPosition = largeListView.lastDisplayItemPosition;
            if (currentPosition == -1 || !lastFocusItem) {
                exeListScrollToPosition(start);
                largeListView.lastDisplayItemPosition = -1;
                postLayout(false, 200);
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
        mData.setCurrentItem(pos);
        largeListView.setScrollPosition(pos);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    public void onGroupChange(int pos) {
        if (groupListView != null) {
            groupListView.setSelectChildPosition(pos);
        }

        // 清除记录
        final int groupStart = getGroupStart(pos);
        if (!lastFocusItem || isScrollTypePage()) {
            largeListView.clearFocusMemory();
            largeListView.setFocusMemoryPosition(groupStart);
            mData.targetItemPos = groupStart;
        }

        if (!largeListView.hasFocus()) {
            exeChangeListPositionByGroupPosition(pos);
        }
        onGroupDisplayedOnScrollPageType(pos);
    }

    public void onListFocusPositionChange(int itemPos) {
        mData.targetItemPos = itemPos;
        if (mParam != null) {
            if (mParam.enableGroup)
                makeGroupRight(itemPos);
            if (mParam.scrollType == AdvanceCenterScroller.SCROLL_TYPE_PAGE)
                beforeArrowVisibleSet(itemPos);
        }
    }

    void makeGroupRight(int itemPos) {
        final int groupPosition = computeGroupPositionByDisplayItemPos(itemPos);
        doChangeGroupOnItemChange(groupPosition);
//        onGroupDisplayedOnScrollPageType(groupPosition);
    }

    void beforeArrowVisibleSet(int pos) {
        final int groupPosition = computeUIPagePositionByItemPos(pos);
        onGroupDisplayedOnScrollPageType(groupPosition);
    }

    boolean isTotalValid() {
        return mParam.totalCount > 0;
    }

    //上边列表Item展示出来时，需要根据展示的item的位置拉取对应数据
    public void onLargeListItemLayout(int pos) {
        if (mData != null) {
            if (isTotalValid()) {
                final int page = computePageByDisplayItemPos(pos);

                final int state = mData.getState(page);
                if (state < 0) {
                    requestLoadPageData(page);
                    mData.setState(page, 0);
                }

//                int pageNum = mParam.totalCount / mParam.pageSize;
//                if (mParam.totalCount % mParam.pageSize != 0) pageNum++;
//                for (int i = page + 1, j = 0; i < pageNum && j < 2; i++, j++) {
//                        final int state1 = mData.getState(i);
//                        if (state1 < 0) {
//                            int finalI = i;
//                            postDelayed(new Runnable() {
//                                @Override
//                                public void run() {
//
//                                    final int state1 = mData.getState(finalI);
//                                    if (state1 < 0) {
//                                        requestLoadPageData(finalI);
//                                        mData.setState(finalI, 0);
//                                    }
//
//                                }
//                            }, 100);
//                        }
//                    }

//                int pageNum = mParam.totalCount / mParam.pageSize;
//                if (mParam.totalCount % mParam.pageSize != 0) pageNum++;
//                for (int i = page + 1; i < pageNum; i++) {
//                    final int state2 = mData.getState(i);
//                    if (state2 < 0) {
//                        requestLoadPageData(i);
//                        mData.setState(i, 0);
//                    }
            }
        }
    }

    void clearLayoutUpdate() {
        if (postTask != null) {
            removeCallbacks(postTask);
        }
    }

    public void clearNotifyData() {
        if (postNotifyDataTask != null) {
            removeCallbacks(postNotifyDataTask);
        }
    }

    void postLayout() {
        postLayout(true, 500, false);
    }

    void postLayout(boolean clear, int delay, boolean blockFocus) {
        if (clear && postTask != null) {
            removeCallbacks(postTask);
        }
        final Runnable task = () -> {
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
            setup();
            created = true;
        }
    }

    int getGroupStart(int pos) {
        int start = -1;
        if (mParam != null) {
            start = isScrollTypePage() ? pos * mParam.groupSize
                    : pos * mParam.groupSize + mParam.scrollTargetOffset;
            start = Math.min(start, mParam.totalCount - 1);
        }
        return start;
    }

    int getUIPageStart(int pos) {
        int start = -1;
        if (mParam != null) {
            start = pos * mParam.pageDisplayCount;
            start = Math.min(start, mParam.totalCount - 1);
        }
        return start;
    }

    int computePageByDisplayItemPos(int pos) {
        return pos / mParam.pageSize;
    }

    int computeDisplayGroupCount() {
        if (mParam != null) {
            return (mParam.totalCount % mParam.pageDisplayCount == 0) ?
                    mParam.totalCount / mParam.pageDisplayCount :
                    mParam.totalCount / mParam.pageDisplayCount + 1;
        }
        return -1;
    }

    int computeGroupPositionByDisplayItemPos(int pos) {
        if (mParam.groupSize == 0) {
            return 0;
        }
        return pos / mParam.groupSize;
    }

    int computeUIPagePositionByItemPos(int pos) {
        if (mParam.pageDisplayCount == 0) {
            return 0;
        }
        return pos / mParam.pageDisplayCount;
    }

    int computePageCount() {
        return mParam.totalCount % mParam.pageSize == 0 ? mParam.totalCount / mParam.pageSize : mParam.totalCount / mParam.pageSize + 1;
    }

    GroupItem getGroupItem(int pos) {
        if (mParam.enableGroup && groupListView.getObjectAdapter().size() > pos) {
            return (GroupItem) groupListView.getObjectAdapter().get(pos);
        }
        return null;
    }


    public void setSelectChildPosition(int pos) {
        if (largeListView != null) {
            largeListView.setSelectChildPosition(pos);
        }
    }

    public void scrollToPosition(int pos, int offset, boolean anim) {
        largeListView.getEasyLayoutManager().scrollToPositionWithOffset(pos, offset);
        postLayout();
    }

    public void scrollToPosition(int pos) {
        if (largeListView != null && largeListView.hasFocus() && mParam.isAutoChangeOnFocus) {
            setFocusPosition(pos);
        } else {
            setInitPosition(pos);
        }

    }

    int last = -1;

    public void requestChildFocus(int pos) {
        if (mParam.scrollType == AdvanceCenterScroller.SCROLL_TYPE_PAGE) {
            largeListView.markPendingFocusPosition(pos);
//            int group = computeGroupPositionByDisplayItemPos(pos);
            int group = computeUIPagePositionByItemPos(pos);
//            int start = getGroupStart(group);
            int start = getUIPageStart(group);
            largeListView.setScrollPosition(start);
            largeListView.requestChildFocusDerectly(pos);
        } else {
            largeListView.setFocusPosition(pos);
        }
        postLayout();
    }

    public void setFocusPosition(int pos) {
//        Log.e(TAG, "setFocusPosition pos:" + pos + ",this:" + getId());
        if (!displayed && created) {
            mData.pendingDisplayFocusPos = pos;
        } else {
            if (largeListView != null) {
                if (mParam.scrollType == AdvanceCenterScroller.SCROLL_TYPE_PAGE) {
                    largeListView.markPendingFocusPosition(pos);
//                    int group = computeGroupPositionByDisplayItemPos(pos);
                    int group = computeUIPagePositionByItemPos(pos);
//                    int start = getGroupStart(group);
                    int start = getUIPageStart(group);
                    largeListView.setScrollPosition(start);
                } else {
                    largeListView.setFocusPosition(pos);
                }
                postLayout();
            } else {
                beforeDisplayFocusPos = pos;
            }
        }

    }

    public void setDisplay(boolean b) {
        if (b != displayed) {
            displayed = b;
            if (created) {
                if (b) {
                    doDisplay();
                } else {
                    doDismiss();
                }
            }
        }
    }

    void setupListInitScroll() {
        if (mParam.initFocusPosition > -1) {
            final int ip = mParam.initFocusPosition;
            final int group = computeGroupPositionByDisplayItemPos(ip);
            if (groupListView != null) {
                groupListView.setSelectChildPosition(group);
            }
            setFocusPosition(ip);
        } else if (mParam.initPosition > -1) {
            setInitPosition(mParam.initPosition);
            postLayout();
        }
    }

    public void doDisplay() {
        setVisibility(View.VISIBLE);
        if (mData.pendingDisplayFocusPos > -1) {
            final int pos = mData.pendingDisplayFocusPos;
            mData.pendingDisplayFocusPos = -1;
            setFocusPosition(pos);
        }
        postLayout(false, 100);
    }

    public void doDismiss() {
        if (created) {
            largeListView.setPendingFocusPosition(-1);
        }
        if (mData != null) {
            mData.currentGroup = -1;
        }
        setVisibility(View.INVISIBLE);
    }


    public void setInitPosition(int pos) {
        if (largeListView != null) {
            if (mParam.scrollType == AdvanceCenterScroller.SCROLL_TYPE_PAGE) {
//                int group = computeGroupPositionByDisplayItemPos(pos);
//                int start = getGroupStart(group);
                int group = computeUIPagePositionByItemPos(pos);
                int start = getUIPageStart(group);
                largeListView.setScrollPosition(start);
                if (groupUp) {
                    mData.targetItemPos = start;
                }
                beforeArrowVisibleSet(pos);
            } else {
                largeListView.setScrollPosition(pos);
                if (groupUp) {
                    mData.targetItemPos = pos;
                }
            }
            if (mParam.enableGroup)
                makeGroupRight(pos);
        } else {
            Log.e(TAG, "setInitPosition error on largeListView null pos:" + pos);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public void layoutArrows(int pW, int h) {
        if (arrow != null) {
            int largeListWidth = mParam.contentWidth < 0 ? pW - mParam.paddingForPageRight - mParam.paddingForPageLeft
                    : mParam.contentWidth;
            final int aw = mParam.arrowWidth;
            final int ah = mParam.arrowHeight;
            int leftMargin = mParam.arrowMarginLeft;
            if (leftMargin < 0)
                leftMargin = (int) (((pW - largeListWidth) * 0.5f - aw) * 0.5);
            int rightMargin = mParam.arrowMarginRight;
            if (rightMargin < 0) {
                rightMargin = leftMargin;
            }

            int top, bottom;
//            if (!groupUp) {
//                top = ((int) ((h - ah) * 0.5f));
//                bottom = top + ah;
//            } else {
//                bottom = pH - ((int) ((h - ah) * 0.5f));
//                top = bottom - ah;
//            }
//            arrow[0].setBounds(leftMargin, top, leftMargin + aw, bottom);
//            int rightLeft = pW - rightMargin - aw;
//            arrow[1].setBounds(rightLeft, top, rightLeft + aw, bottom);

            top = ((int) ((h - ah) * 0.5f));
            bottom = top + ah;
            int rightLeft;
            if (groupUp) {
                arrow[0].setBounds(-leftMargin - aw, top, -leftMargin, bottom);
                rightLeft = (int) (pW - rightMargin - (pW - largeListWidth) * 0.5f - aw + 0.5f);
            } else {
                arrow[0].setBounds(leftMargin, top, leftMargin + aw, bottom);
                rightLeft = pW - rightMargin - aw;
            }
            arrow[1].setBounds(rightLeft, top, rightLeft + aw, bottom);
            invalidate();
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
//        if (arrow != null) {
//            if (drawArrow[0]) {
//                arrow[0].draw(canvas);
//            }
//            if (drawArrow[1]) {
//                arrow[1].draw(canvas);
//            }
//        }
    }

    public void notifyItemClick(int pos) {
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
        if (lastGroup != group) { // 这一个判断类似防抖功能 一会儿处理
            lastGroup = group;
            if (mParam.scrollType == AdvanceCenterScroller.SCROLL_TYPE_PAGE) {
                int groupCount = computeDisplayGroupCount();
                if (groupCount < 2) {//只有一页
                    setArrowVisible(false, false);
                } else if (group >= groupCount - 1) {
                    setArrowVisible(true, false);
                } else setArrowVisible(group >= 1, true);

            }
        }
    }

    void setArrowVisible(boolean leftVisi, boolean rightVisi) {
        if (arrow != null) {
            drawArrow[0] = leftVisi;
            drawArrow[1] = rightVisi;
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

    @Override
    public void requestLayout() {
        super.requestLayout();
        postLayout(false, 16);

    }

    @Override
    public NativeGestureDispatcher getGestureDispatcher() {
        return null;
    }

    @Override
    public void setGestureDispatcher(NativeGestureDispatcher nativeGestureDispatcher) {

    }


    public HippyEngineContext getEngineContext() {
        return engineContext;
    }

    public RenderNode getItemRenderNode() {
        if (customIndex < 0 || customIndex > itemRenderNodes.size() - 1) {
            if (itemRenderNodes.size() > 0) {
                return itemRenderNodes.get(0);
            } else {
                throw new IllegalArgumentException("传入的customIndex不合法，customIndex--" + customIndex);
            }
        }
        return itemRenderNodes.get(customIndex);
    }

}
