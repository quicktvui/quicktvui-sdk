package com.quicktvui.support.ui.swiftlist;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.quicktvui.support.ui.largelist.EasyListView;
import com.quicktvui.support.ui.largelist.EventSender;
import com.quicktvui.support.ui.largelist.LoadingItem;
import com.quicktvui.support.ui.v7.widget.RecyclerView;

import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.IEsComponentView;
import com.quicktvui.support.ui.largelist.TemplateHelper;
import com.quicktvui.support.ui.largelist.TemplateItem;
import com.quicktvui.hippyext.RenderUtil;
import com.tencent.mtt.hippy.utils.LogUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.quicktvui.support.ui.legacy.misc.ItemDecorations;
import com.quicktvui.support.ui.leanback.ArrayObjectAdapter;
import com.quicktvui.support.ui.leanback.PresenterSelector;


public class SwiftListView extends LinearLayout implements IEsComponentView {

    Param mParam;
    MyData mData;

    MyLargeListView largeListView;
    EventSender event;
    boolean isVertical = false;

    boolean displayed = false;

    boolean created = false;
    private Runnable postTask;

    ArrayObjectAdapter dataAdapter;


    private RecyclerView.ItemDecoration itemDecoration;

    private static String TAG = "SwiftListViewLOG";

    public SwiftListView(Context context, boolean isVertical) {
        super(context);
        this.isVertical = isVertical;
    }

    public SwiftListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public SwiftListView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void initParams(EsMap map, EsMap template) {
        if (mParam == null) {
            mParam = new Param();
            mParam.apply(map, template);
            Log.i(TAG, "initParams :" + mParam);
            mData = new MyData();
            event = new EventSender(this);
            setupData();
            requestLoadPageDataIfNeed(0);
            requestFirstShow();
        }
    }


    private void setupViews() {
        this.setOrientation(VERTICAL);
        removeAllViews();
        setFocusable(false);
        largeListView = new MyLargeListView(getContext());
        setClipChildren(false);
        if (isVertical) {
            largeListView.setChildSize(mParam.itemHeight);
        } else {
            largeListView.setChildSize(mParam.itemWidth);
        }
        largeListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                int pos = parent.getChildAdapterPosition(view);
                int padding = mParam.contentMargin;
                if (pos == 0) {
                    outRect.left = padding;
                }
                if (pos == state.getItemCount() - 1) {
                    outRect.right = padding;
                }
            }
        });
//        largeListView.addItemDecoration(new ItemDecorations.ListEndBlank(2000,getOrientation()));
        largeListView.setFocusable(false);
        largeListView.setClipChildren(false);
//        largeListView.setBackgroundColor(Color.CYAN);
        LayoutParams lpUp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mParam.contentHeight);
        addView(largeListView, lpUp);
    }

    void setupListInitScroll() {
        Log.d(TAG, "setupListInitScroll !!!!");
        if (mParam.initFocusPosition > -1) {
            final int ip = mParam.initFocusPosition;
            setFocusPosition(ip);
        } else if (mParam.initPosition > -1) {
            Log.e(TAG, "setScrollPosition pos:" + mParam.initPosition + ",this:" + getId());
            setInitPosition(mParam.initPosition);
            postLayout();
        }
    }

    public void setup() {
        if (mParam != null) {
            setupViews();
            setupListInitScroll();
            setupUpList();
            postLayout();
        } else {
            throw new IllegalArgumentException("Param cannot be null, please call initParams method first!!");
        }

    }

    private void setupData() {
        dataAdapter = new ArrayObjectAdapter(selector);
    }

    void setupUpList() {
        largeListView.initLayoutManager();
        largeListView.addItemDecoration(new ItemDecorations.SimpleBetweenItem(mParam.itemGap));
        largeListView.setObjectAdapter(dataAdapter);
    }

    PresenterSelector selector;

    public void setSelector(PresenterSelector selector) {
        this.selector = selector;
    }

    public void setPageData(int page, EsArray data) {
        if (LogUtils.isDebug()) {
            Log.e(TAG, "setPageData page:" + page + ",data length:" + data.size());
        }
        if (mData != null) {
            int state = data.size() > 0 ? 1 : -1;
            mData.setState(state);
            if (data.size() > 0) {
                mData.setCurrentPage(page);
                mData.hasFocusBeforAddData = largeListView != null && largeListView.hasFocus();
                final ArrayObjectAdapter adapter = dataAdapter;
                if (LogUtils.isDebug()) {
                    Log.i(TAG, "setPageData exe add data , adapterSize:" + adapter.size() + ",data length:" + data.size());
                }
                removeLoading();
                final List<Object> list = addLoading(adapter, TemplateHelper.buildTemplateItemListObjectAdapter(data, mParam.template));
                adapter.addAll(adapter.size(), list);
//                Log.e(TAG,"setPageData hasFocus position:"+pos+",hasFocusBeforAddData:"+mData.hasFocusBeforAddData);
//                if(mData.hasFocusBeforAddData && pos > -1){
//                    setFocusPosition(pos);
//                    Log.e(TAG,"setPageData exe setFocusPosition:"+pos);
//                }else{
//                    setInitPosition(pos);
//                }
                postLayout(false, 500, false);
            } else {
                Log.e(TAG, "setPageData no data : page:" + page);
            }
        }

    }

    private List<Object> addLoading(ArrayObjectAdapter adapter, Collection<Object> items) {
        final int loadingCount = mParam.loadingCount;
        ArrayList<Object> list = new ArrayList(items);
        if (loadingCount > 0) {
            final int start = adapter.size() + list.size();
            mData.loadingStartPos = start;
            for (int i = 0; i < loadingCount; i++) {
                list.add(new LoadingItem());
            }
        }
        return list;
    }

    private void removeLoading() {
        if (mData.loadingStartPos > 0 && dataAdapter.size() > mData.loadingStartPos && mParam.loadingCount > 0) {
            final Object a = dataAdapter.get(mData.loadingStartPos);
//            Log.d(TAG,"removeLoading start:"+mData.loadingStartPos+",count:"+mParam.loadingCount+",a:"+a);
            if (a instanceof LoadingItem) {
                dataAdapter.removeItems(mData.loadingStartPos, mParam.loadingCount);
            }
        }
    }

    void notifyLargeListDataChangeByRange(int start, int count) {
//        Log.i(TAG,"notifyLargeListDataChangeByRange start:"+start+",count:"+count);
        if (largeListView != null) {
            largeListView.getObjectAdapter().notifyItemRangeChanged(start, count);
        }
    }

    public void setInitPosition(int pos) {
//        Log.e(TAG,"setInitPosition pos:"+pos+",this:"+getId()+",largeListView:"+largeListView);
        if (largeListView != null) {
            largeListView.setScrollPosition(pos);
        } else {
            mData.pendingDisplayScrollPos = pos;
        }
    }


    void exeLoadPageData(int page) {
        if (LogUtils.isDebug()) {
            Log.e(TAG, "doLoadPageData page:" + page);
        }
        event.notifyLoadPageData(page);
    }

    void requestLoadPageDataIfNeed(int page) {
        final int state = mData.getState();
        if (LogUtils.isDebug()) {
            Log.v(TAG, "requestLoadPageDataIfNeed page:" + page + ",state:" + state + ",isNoMoreData:" + mData.isNoMoreData);
        }
        if (mData.isNoMoreData) {
            Log.e(TAG, "requestLoadPageDataIfNeed noMore Data return");
            return;
        }
        if (state != 0) {
            exeLoadPageData(page);
            mData.setState(0);
        }
    }


    void onListPositionChange(int pos) {

    }

    //上边列表Item展示出来时，需要根据展示的item的位置拉取对应数据
    void onLargeListItemLayout(int pos) {
        int count = largeListView.getObjectAdapter().size();
        if (LogUtils.isDebug()) {
            Log.d(TAG, "onLargeListItemLayout pos:" + pos + ",count:" + count + ",mData:" + mData);
        }

        if (mData != null && count > 0) {
            if ((mParam.preLoadNumber + pos) >= (count - 1 - mParam.loadingCount)) {
                requestLoadPageDataIfNeed(mData.getNextPage());
            } else {
                if (LogUtils.isDebug()) {
                    Log.d(TAG, "onLargeListItemLayout no need");
                }
            }
        }
    }


    int computePageByDisplayItemPos(int pos) {
        return pos / mParam.pageSize;
    }

    int computePageCount() {
        return mParam.totalCount % mParam.pageSize == 0 ? mParam.totalCount / mParam.pageSize : mParam.totalCount / mParam.pageSize + 1;
    }

    void requestFirstShow() {
        if (mParam != null && !created && displayed && getVisibility() == View.VISIBLE) {
//            Log.e(TAG,"requestFirstShow exe setup!!!!,this:"+getId());
            setup();
            created = true;
        } else {
//            Log.d(TAG,"requestFirstShow no need created:"+created+",displayed:"+displayed+",width:"+getWidth()+",height:"+getHeight()+",getVisibility:"+getVisibility());
        }
    }

    public void setFocusPosition(int pos) {
        if (largeListView != null) {
            largeListView.setFocusPosition(pos);
        } else {
            mData.pendingDisplayFocusPos = pos;
        }
    }

    private void notifyItemClick(int pos) {
        final Object data = largeListView.getObjectAdapter().get(pos);
        if (data instanceof TemplateItem) {
            event.notifyItemClick(pos, ((TemplateItem) data).getContent());
        }

    }

    public void setDisplay(boolean b) {
        Log.i(TAG, "setDisplay :" + b);
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

    public void doDismiss() {
        Log.d(TAG, "doDismiss !!!!");
        if (created) {
//            largeListView.setPendingFocusPosition(-1);
        }
        setVisibility(View.INVISIBLE);
    }

    void requestLayoutSelf() {
        try {
            RenderUtil.requestNodeLayout(this);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    void postLayout() {
        postLayout(true, 500, false);
    }

    void postLayout(boolean clear, int delay, boolean blockFocus) {
        if (blockFocus && largeListView != null) {
            largeListView.blockFocus();
        }
        if (clear && postTask != null) {
            removeCallbacks(postTask);
        }
        final Runnable task = () -> {
            if (largeListView != null) {
                largeListView.releaseFocus();
            }
            requestLayoutSelf();
            postTask = null;
        };
        this.postTask = task;
        postDelayed(task, delay);
    }

    void postLayout(boolean clear, int delay) {
        postLayout(clear, delay, false);
    }

    public void doDisplay() {
        Log.d(TAG, "doDisplay !!!!");
        setVisibility(View.VISIBLE);
        if (mData.pendingDisplayFocusPos > -1) {
            final int pos = mData.pendingDisplayFocusPos;
//            Log.e(TAG,"doDisplay doPendingDisplayFocusPos pos:"+mData.pendingDisplayFocusPos);
            mData.pendingDisplayFocusPos = -1;
            setFocusPosition(pos);
        }
        if (mData.pendingDisplayScrollPos > -1) {
//            Log.e(TAG, "setScrollPosition pos on doDisplay pendingDisplayScrollPos :" + mData.pendingDisplayScrollPos + ",this:" + getId());
            final int pos = mData.pendingDisplayScrollPos;
            mData.pendingDisplayScrollPos = -1;
            setInitPosition(pos);
        }
        postLayout(false, 100);
    }

    public void notifyNoMoreData() {
        Log.d(TAG, "notifyNoMoreData !!!!");
        if (mData != null) {
            mData.isNoMoreData = true;
        }
        removeLoading();
    }

    public void setFocusTargetChildPosition(int pos) {

        if (largeListView != null) {
            largeListView.clearFocusMemory();
            largeListView.setFocusMemoryPosition(pos);
        }
    }


    private final class MyLargeListView extends EasyListView {

        private OnClickListener clickListener;

        public MyLargeListView(@NonNull Context context) {
            super(context, false);
            clickListener = v -> {
                final int p = getChildAdapterPosition(v);
                notifyItemClick(p);
            };
        }

        @Override
        protected void onAddDefaultItemDecoration() {
//            super.onAddDefaultItemDecoration();
        }

        @Override
        public void requestChildFocus(View child, View focused) {
            super.requestChildFocus(child, focused);
            if (event != null) {
                event.notifyItemFocus(mFocusChildPosition);
            }
            onListPositionChange(mFocusChildPosition);
        }

        @Override
        public View onInterceptFocusSearch(@NonNull View focused, int direction) {
            return super.onInterceptFocusSearch(focused, direction);
        }

        @Override
        protected void onLayoutItem(View child, int pos) {
            super.onLayoutItem(child, pos);
            onLargeListItemLayout(pos);
            child.setOnClickListener(clickListener);
        }
    }


    static class Param {
        public int itemGap = 10;
        int totalCount;
        int maxCount;
        int pageSize;
        int initPosition = -1;
        int scrollType;
        int contentHeight;
        int preLoadNumber = 3;
        int loadingCount = 3;
        int itemWidth;
        int itemHeight;
        int contentMargin = 0;
        private int initFocusPosition = -1;

        void apply(EsMap map, EsMap template) {
            this.pageSize = map.containsKey("pageSize") ? map.getInt("pageSize") : 100;
            this.initPosition = map.containsKey("initPosition") ? map.getInt("initPosition") : -1;
            this.initFocusPosition = map.containsKey("initPosition") ? map.getInt("initFocusPosition") : -1;
            this.loadingCount = map.containsKey("loadingCount") ? map.getInt("loadingCount") : 3;
            this.contentMargin = map.containsKey("contentMargin") ? map.getInt("contentMargin") : 0;
            this.scrollType = map.containsKey("scrollType") ? map.getInt("scrollType") : 0;
            this.template = template; //number / leftRight / topDown / text
            if (template != null) {
                this.itemWidth = template.getInt("width");
                this.itemHeight = template.getInt("height");
            }
            this.totalCount = map.getInt("totalCount");
            this.maxCount = map.containsKey("maxCount") ? map.getInt("maxCount") : 2000;
            this.contentHeight = map.getInt("contentHeight");
            this.itemGap = map.getInt("itemGap");
            this.preLoadNumber = map.containsKey("preLoadNumber") ? map.getInt("preLoadNumber") : 3;
        }

        EsMap template;

        @Override
        public String toString() {
            return "Param{" +
                    "itemGap=" + itemGap +
                    ", totalCount=" + totalCount +
                    ", pageSize=" + pageSize +
                    ", initPosition=" + initPosition +
                    ", scrollType=" + scrollType +
                    ", contentHeight=" + contentHeight +
                    ", preLoadNumber=" + preLoadNumber +
                    ", itemWidth=" + itemWidth +
                    ", itemHeight=" + itemHeight +
                    ", initFocusPosition=" + initFocusPosition +
                    ", template=" + template +
                    '}';
        }
    }


    private final static class MyData {
        public int pendingDisplayFocusPos = -1;
        public int pendingDisplayScrollPos = -1;
        public boolean isNoMoreData = false;
        boolean hasFocusBeforAddData = false;
        int state = -1;
        int currentPage = 0;
        int loadingStartPos = -1;

        private MyData() {

        }

        public int getCurrentPage() {
            return state;
        }

        public int getNextPage() {
            return currentPage + 1;
        }

        public int moveNextPage() {
            this.currentPage += 1;
            return this.currentPage;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public void setCurrentPage(int page) {
            this.currentPage = page;
        }
    }


    public void setSelectChildPosition(int pos) {
        if (largeListView != null) {
            largeListView.setSelectChildPosition(pos);
        }
    }

    public void scrollToPosition(int pos, int offset, boolean anim) {
        if (largeListView != null) {
            largeListView.getEasyLayoutManager().scrollToPositionWithOffset(pos, offset);
        }
    }

    public void scrollToPosition(int pos) {
        if (largeListView != null) {
            largeListView.setScrollPosition(pos);
        }
    }

    public void updateData(int pos, EsMap data) {
    }

    public void requestChildFocus(int pos) {
        setFocusPosition(pos);
    }
}
