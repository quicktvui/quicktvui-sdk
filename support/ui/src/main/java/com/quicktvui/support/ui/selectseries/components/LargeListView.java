package com.quicktvui.support.ui.selectseries.components;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

import android.support.annotation.NonNull;

import com.quicktvui.support.ui.largelist.AdvanceCenterScroller;
import com.quicktvui.support.ui.largelist.EasyListView;
import com.quicktvui.support.ui.largelist.PendingItemView;
import com.quicktvui.support.ui.selectseries.SelectSeriesViewGroup;
import com.quicktvui.support.ui.selectseries.presenters.CustomItemViewPresenter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.quicktvui.support.ui.selectseries.bean.TemplateItem;

import com.quicktvui.support.ui.leanback.ItemBridgeAdapter;
import com.quicktvui.support.ui.leanback.Presenter;

public class LargeListView extends EasyListView {

    private final OnClickListener clickListener;
    public int lastDisplayItemPosition = -1;
    //        boolean pendingLayout = false;
    HashMap<Integer, Boolean> pendingLayoutMap;
    int[] pd = new int[2];
    int scrollState = SCROLL_STATE_IDLE;
    private boolean pendingLayout;
    private final SelectSeriesViewGroup parent;

    @Deprecated
    public LargeListView(@NonNull Context context) {
        this(context, null);
    }

    public LargeListView(@NonNull Context context, SelectSeriesViewGroup parent) {
        super(context, false);
        this.parent = parent;
        clickListener = v -> {
            final int p = getChildAdapterPosition(v);
            parent.notifyItemClick(p);

        };
        pendingLayoutMap = new HashMap<>();
        setItemAnimator(null);
    }

    public void setPendingToUpdate(int pos) {
        this.pendingLayoutMap.put(pos, true);
    }

    public void clearPendingToUpdate(int pos) {
        this.pendingLayoutMap.put(pos, false);
    }

    @Override
    protected SingleLineLayoutManager onCreateLayoutManager(int orientation) {
        super.onCreateLayoutManager(orientation);
        return new LargeListLayoutManager(this, orientation);
    }

    Set<View> preSelectViews = new HashSet<>();
    View preSelectView;

    @Override
    public void setSelectChildPosition(int position) {
        super.setSelectChildPosition(position);
        // 支持showOnSelected
        ViewHolder viewHolder = findViewHolderForAdapterPosition(position);
        if (viewHolder instanceof ItemBridgeAdapter.ViewHolder) {
            Presenter.ViewHolder mViewHolder = ((ItemBridgeAdapter.ViewHolder) viewHolder).getViewHolder();
            if (mViewHolder instanceof CustomItemViewPresenter.MyHolder) {
                for (View selectView : preSelectViews) {
                    selectView.setVisibility(INVISIBLE);
                }
                preSelectViews.clear();
                for (View selectView : ((CustomItemViewPresenter.MyHolder) mViewHolder).selectViews) {
                    selectView.setVisibility(VISIBLE);
                }
                preSelectViews.addAll(((CustomItemViewPresenter.MyHolder) mViewHolder).selectViews);
                // 支持showOnOnlySelected
                if (!viewHolder.itemView.isFocused()) {
                    for (View selectView : ((CustomItemViewPresenter.MyHolder) mViewHolder).oSelectViews) {
                        selectView.setVisibility(VISIBLE);
                    }
                    preSelectViews.addAll(((CustomItemViewPresenter.MyHolder) mViewHolder).oSelectViews);
                } else {
                    preUFocusViews.addAll(((CustomItemViewPresenter.MyHolder) mViewHolder).oSelectViews);
                }
                // 支持showOnOnlyFocused
                for (View selectView : ((CustomItemViewPresenter.MyHolder) mViewHolder).oFocusViews) {
                    selectView.setVisibility(INVISIBLE);
                }
            }
        }
        // 支持showOnState selected
        View viewByPosition = getEasyLayoutManager().findViewByPosition(position);
        if (preSelectView != null) {
            preSelectView.setSelected(false);
        }
        if (viewByPosition != null) {
            viewByPosition.setSelected(true);
            preSelectView = viewByPosition;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        parent.layoutArrows(parent.getWidth(), h);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (parent.arrow != null) {
            if (parent.drawArrow[0]) {
                parent.arrow[0].draw(canvas);
            }
            if (parent.drawArrow[1]) {
                parent.arrow[1].draw(canvas);
            }
        }
    }

    @Override
    public void setScrollType(int type) {
        super.setScrollType(type);
    }

    Set<View> preFocusViews = new HashSet<>();
    Set<View> preUFocusViews = new HashSet<>();

    @Override
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        parent.event.notifyItemFocus(mFocusChildPosition);
        parent.onListFocusPositionChange(mFocusChildPosition);
        parent.lastFocusItem = true;
        this.lastDisplayItemPosition = mFocusChildPosition;

        // 支持showOnFocused
        Presenter.ViewHolder viewHolder = findChildViewHolder(child);
        if (viewHolder instanceof CustomItemViewPresenter.MyHolder) {
            for (View focusView : preFocusViews) {
                focusView.setVisibility(INVISIBLE);
            }
            preFocusViews.clear();
            for (View focusView : ((CustomItemViewPresenter.MyHolder) viewHolder).focusViews) {
                focusView.setVisibility(VISIBLE);
            }
            preFocusViews.addAll(((CustomItemViewPresenter.MyHolder) viewHolder).focusViews);
            // 支持showOnOnly oFocusViews
            if (!child.isSelected()) {
                for (View focusView : ((CustomItemViewPresenter.MyHolder) viewHolder).oFocusViews) {
                    focusView.setVisibility(VISIBLE);
                }
                preFocusViews.addAll(((CustomItemViewPresenter.MyHolder) viewHolder).oFocusViews);
            }
            // 支持showOnOnly oSelectViews
            for (View focusView : preUFocusViews) {
                focusView.setVisibility(VISIBLE);
                preSelectViews.add(focusView);
            }
            preUFocusViews.clear();
            for (View focusView : ((CustomItemViewPresenter.MyHolder) viewHolder).oSelectViews) {
                focusView.setVisibility(INVISIBLE);
                if (child.isSelected()) {
                    preUFocusViews.add(focusView);
                }
            }
        }
    }

    @Override
    protected void onLayoutCompleted(State state) {
        super.onLayoutCompleted(state);
    }

    @Override
    protected void onLayoutItem(View child, int pos) {
        super.onLayoutItem(child, pos);
//        parent.onLargeListItemLayout(pos);
        child.setOnClickListener(clickListener);
        if (pendingLayoutMap != null && pendingLayoutMap.containsKey(pos)) {
            boolean b = Boolean.TRUE.equals(pendingLayoutMap.get(pos));
            if (b) {
                updateContent(pos, child);
            }
        }
    }

    @Override
    public void onChildAttachedToWindow(@NonNull View child) {
        super.onChildAttachedToWindow(child);
        int pos = (int) child.getTag();
        parent.onLargeListItemLayout(pos);
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
                postNotify(hasFocus(), pd[0]);
                pendingLayout = false;
            } else {
                parent.clearNotifyData();
            }
        }
    }

    @Override
    protected void onLayoutChildren(State state) {
        super.onLayoutChildren(state);
    }

    void updateContent(int pos, View child) {
        if (child instanceof PendingItemView) {
            final Object o = getObjectAdapter().get(pos);
            if (o instanceof TemplateItem && ((TemplateItem) o).isNotEmpty()) {
                pendingLayoutMap.put(pos, false);
                ((PendingItemView) child).setContentData(o);
            }
        }
    }

    void postNotify(boolean block, int start) {
        parent.clearNotifyData();
        parent.postNotifyDataTask = () -> {
            if (parent.isScrollTypePage()) {
                for (int i = 0; i < parent.mParam.groupSize; i++) {
                    pendingLayoutMap.put(start + i, true);
                }
            } else {
                //由于存在个别item已经渲染出来，存在无法刷新的问题，所以刷新范围扩大
                int toUpdateStart = Math.max(start - parent.mParam.updateAdditionRange, 0);
                int toUpdateEnd = Math.min(toUpdateStart + parent.mParam.pageSize + parent.mParam.updateAdditionRange, getLayoutManager().getItemCount()) + 1;
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

            parent.requestLayoutSelf();
        };
        this.postDelayed(parent.postNotifyDataTask, 20);
    }

    private boolean isEndPosition(int pos) {
        return ((pos + 1) % parent.mParam.pageDisplayCount) == 0;
    }

    private boolean isStartPosition(int pos) {
        return ((pos + 1) % parent.mParam.pageDisplayCount) == 1;
    }


    @Override
    public View onInterceptFocusSearch(@NonNull View focused, int direction) {
        View v = null;
        if (direction == View.FOCUS_UP || direction == View.FOCUS_DOWN) {
            // 处理list失焦
            for (View focusView : preFocusViews) {
                focusView.setVisibility(INVISIBLE);
            }
            preFocusViews.clear();

            for (View focusView : preUFocusViews) {
                focusView.setVisibility(VISIBLE);
                preSelectViews.add(focusView);
            }
            preUFocusViews.clear();
        }
        if (direction == (parent.groupUp ? View.FOCUS_UP : View.FOCUS_DOWN) && parent.groupListView != null) {
            final int groupPosition = parent.mData.currentGroup;
            v = parent.groupListView.getEasyLayoutManager().findViewByPosition(groupPosition);
        }

        if (parent.mParam.blockFocus && direction == View.FOCUS_LEFT && mFocusChildPosition == 0) {
//                Log.e(TAG,"block left FocusON position 0");
            return focused;
        }

        if (parent.isScrollTypePage() && (direction == View.FOCUS_LEFT || direction == View.FOCUS_RIGHT) && isInScrolling()) {
//                Log.e(TAG,"block in Scrolling");
            return focused;
        }

        if (v == null && scrollType == AdvanceCenterScroller.SCROLL_TYPE_PAGE) {

            //如果当前在边缘
            if (direction == FOCUS_RIGHT) {
                if (parent.mParam.pageDisplayCount < 1) {
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
                if (parent.mParam.pageDisplayCount < 1) {
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
                        getEasyLayoutManager().smoothScrollToPositionTop(this, prev - parent.mParam.pageDisplayCount + 1 > -1 ? prev - parent.mParam.pageDisplayCount + 1 : 0);
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
            pd[0] = start;
            pendingLayout = true;
        } else {
            postNotify(hasFocus(), start);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (selectPos > -1) {
            int temPosition = defaultSectionPosition;
            setSelectChildPosition(selectPos);
            defaultSectionPosition = temPosition;
        }
    }

    public static class LargeListLayoutManager extends EasyLinearLayoutManager {

        LargeListView listView;

        public LargeListLayoutManager(LargeListView singleLineRecyclerView, int orientation) {
            super(singleLineRecyclerView, orientation);
            this.listView = singleLineRecyclerView;
        }

        @Override
        public void layoutDecoratedWithMargins(@NonNull View child, int left, int top, int right, int bottom) {
            super.layoutDecoratedWithMargins(child, left, top, right, bottom);
            if (listView.selectPos > -1) {
                final boolean select = getPosition(child) == listView.selectPos;
                if (select) {
                    listView.setSelectChildPosition(listView.selectPos);
                }
            }
        }
    }
}
