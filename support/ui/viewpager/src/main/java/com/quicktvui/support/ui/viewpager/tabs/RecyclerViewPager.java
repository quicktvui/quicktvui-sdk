package com.quicktvui.support.ui.viewpager.tabs;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.extscreen.support.viewpager2.widget.ViewPager2;
import com.quicktvui.base.ui.FocusDispatchView;
import com.quicktvui.base.ui.FocusUtils;
import com.quicktvui.base.ui.ITVView;
import com.quicktvui.base.ui.TriggerTaskHost;
import com.quicktvui.base.ui.waterfall.Chunk;
import com.quicktvui.base.ui.waterfall.WaterfallUtils;
import com.quicktvui.sdk.base.ITakeOverKeyEventListener;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.hippyext.RenderUtil;
import com.quicktvui.hippyext.TriggerTaskManagerModule;
import com.quicktvui.hippyext.views.fastlist.FastListView;
import com.quicktvui.hippyext.views.fastlist.FastPendingView;
import com.quicktvui.hippyext.views.fastlist.ListViewControlProp;
import com.quicktvui.hippyext.views.fastlist.ReplaceChildView;
import com.quicktvui.hippyext.views.fastlist.TVListView;
import com.quicktvui.hippyext.views.fastlist.Utils;
import com.quicktvui.hippyext.views.fastlist.VirtualListView;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.common.Callback;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.modules.Promise;
import com.tencent.mtt.hippy.uimanager.HippyViewBase;
import com.tencent.mtt.hippy.uimanager.NativeGestureDispatcher;
import com.tencent.mtt.hippy.uimanager.RenderNode;
import com.tencent.mtt.hippy.utils.ExtendUtil;
import com.tencent.mtt.hippy.utils.LogUtils;
import com.tencent.mtt.hippy.views.list.TVSingleLineListView;
import com.tencent.mtt.hippy.views.view.HippyViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.quicktvui.support.ui.viewpager.utils.RenderNodeUtils;
import com.quicktvui.support.ui.viewpager.utils.TabEnum;
import com.quicktvui.support.ui.viewpager.utils.TabHelper;
import com.quicktvui.support.ui.viewpager.utils.TabUtils;
import com.quicktvui.support.ui.viewpager.utils.TimeInterpolatorUtils;


public class RecyclerViewPager extends FrameLayout implements HippyViewBase, TVSingleLineListView, TriggerTaskHost, VirtualListView {

    public static final String FOCUS_TAG = "DebugVPFocus";
    PageAdapter mAdapter;

    RenderNode boundNode;
    private int initPage = 0;
    static String TAG = "ListViewPagerLog";
    private Runnable pageRunnable = null;
    PageContentAdapter mContentFactory;
    PageDataLoader mPageLoader;
    private Runnable resumeTaskRunnable;
    private Runnable notifyResumePlayerRunnable;
    private TabsParam tabsParam;
    //    int outOfDateTime = -1; //设置为-1时，没有过期时间
    public final ViewPager2 vp2;
    private ViewPager2.OnPageChangeCallback onPageChangeCallback;
    private ViewPager2ScrollHelper mScrollHelper;
    boolean enableTransform = true;
    private OnPageEventListener mOnPageEventListener;


    private TabsView tabsView;
    private View suspensionView;
    //是否开启顶部吸顶功能
    private boolean isSuspension;
    //是否使用默认吸顶背景色
    private boolean useSuspensionBg;
    private boolean focusSearchEnable = false;
    private boolean isHideOnSingleTab;
    int orientation;
    private final static int RESUME_TASK_DELAY = 500;
    private final static int FIRST_RESUME_TASK_DELAY = 1000;//第一次进入页面显示的delay时间
    private List<TabHelper> tabHelpers;//存放每一页的tabView数据和监听事件
    private final static int PAGE_SWITCH_DELAY = 300;
    public boolean useAdvancedFocusSearch = false;
    private boolean hasSetData = false;
    private boolean destroyed = false;
    private String currentSlidingMode = "";
    private String slidingValue = "";
    private final int firstTranslationValue;
    private final int rightTranslationValue;
    private final int leftTranslationValue;
    private boolean firstInit = false;
    private boolean firstLayout = true;
    private boolean isLeft = false;
    private boolean isSlidingEnable = false;
    private int mScreenWidth;
    private int mScreenHeight;
    private int anDuration = 200;
    private int switchDuration = 200;
    private int interType = 1;
    private boolean keyIntercept = false;
    private View rootView;


    public void setup(TabsParam tabsParam, RenderNode viewPagerNode) {
        this.tabsParam = tabsParam;
        this.boundNode = viewPagerNode;
        if (mScrollHelper != null) {
            mScrollHelper.setTabsParam(tabsParam);
        }
        if (this.mContentFactory != null) {
            this.mContentFactory.setRequestAutofocusOnPageChange(tabsParam.requestAutofocusOnPageChange);
        }
        if (vp2 != null) {
            vp2.setOffscreenPageLimit(tabsParam.offscreenPageLimit);
        }
        Log.i(FOCUS_TAG,"setup recyclerViewPager isSlidingEnable:"+isSlidingEnable);
        if (isSlidingEnable) {
            tabsParam.disableScrollAnimation = true;
//            setFocusable(true);
//            setFocusable(true);
//            requestFocus();
            //setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);

            EsProxy.get().setTakeOverKeyEventListener(new ITakeOverKeyEventListener() {
                @Override
                public boolean dispatchKeyEvent(KeyEvent event) {
                    View root = rootView;
                    if (root == null || tabsParam == null) {
                        return false;
                    }
                    Log.i(FOCUS_TAG," dispatchKeyEvent By EsProxy event action:"+event.getAction()+",key:"+event.getKeyCode()+",findRoot:"+root);
                    if(root instanceof HippyViewGroup){
                        Log.i(FOCUS_TAG,"ITakeOverKeyEventListener pageHidden:"+((HippyViewGroup) root).isPageHidden());
                        if(((HippyViewGroup) root).isPageHidden()){
                            //Log.v(FOCUS_TAG,"ITakeOverKeyEventListener return false on pageHidden");
                            return false;
                        }
                    }
                    if(event.getAction() == KeyEvent.ACTION_DOWN && isSlidingEnable && getOrientation() == RecyclerView.VERTICAL){
                        Log.v(FOCUS_TAG, "--dispatchKeyEvent ");
                        //miobox当有焦点时自动处理切换
                        int vector = 0;
                        if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP){
                            vector = -1;
                        }else if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN){
                            vector = 1;
                        }
                        if(tabsParam.autoChangePageByNative &&  vector != 0 && requestSwitchToPageOnVector(vector)){
                            Log.i(FOCUS_TAG, "eat keyEvent on pageChange isTranslationLeftModel:"+isTranslationLeftModel());
                            if(!isTranslationLeftModel()) {
//                                keyIntercept = true;
                            }
                            return  true;
                        }

                    }
                    return false;
                }
            });
        }
    }

    public boolean isSlidingEnable(){
        return isSlidingEnable;
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
//        final View view = findPageView(getCurrentItem());
//        if (view != null) {
//            return view.requestFocus();
//        }
        return super.requestFocus(direction, previouslyFocusedRect);
    }

    public void setContentFactory(PageContentAdapter mContentFactory) {
        this.mContentFactory = mContentFactory;
    }

    public int getCurrentItem() {
        return mTargetCurrent;
    }

    public int getTotalPage() {
        return mAdapter.getItemCount();
    }


    public TabsParam getTabsParam() {
        return tabsParam;
    }


    boolean isPreferSaveMemory(){
        return tabsParam == null || tabsParam.preferSaveMemory;
    }

    boolean isEnableDrawerAnimation(){
        return tabsParam != null && tabsParam.enableDrawerAnimation;
    }

    public boolean requestSwitchToPageOnVector(int vector){
//        Log.i(FOCUS_TAG,"requestSwitchToPageOnVector vector:"+vector);
        if(isSlidingEnable){//这里只服务于miobox这个项目
            if(tabsParam != null && !tabsParam.changePageOnFocusFail){
                return false;
            }
//            final int pos = wv.bindPosition;
            final int current = mTargetCurrent;
            final int count = getTotalPage();
            if(LogUtils.isDebug()) {
                Log.i(FOCUS_TAG, "onInterceptContentViewFocusSearch vector:" + vector + ",current:" + current + ",total:" + count);
            }
            int next = current + vector;
            //这里只服务于miobox这个项目，假定内容里的数据只有一行的情况下，vector==0,证明需要翻页
            if (vector != 0 && current != next && next > -1 && next < count) {
//                mTargetCurrent = next;
//                if(isFocusable()) {
//                    super.requestFocus();
//                }
                syncSelectState4CustomNavList(next);
                requestSwitchToPage(next,true);
                if(LogUtils.isDebug()) {
                    Log.e(FOCUS_TAG, "----requestSwitchToPage next next:" + next);
                }
                return true;
            }else{
                if(LogUtils.isDebug()) {
                    Log.e(FOCUS_TAG, "----requestSwitchToPage return on current == next : current " + current);
                }
                return false;
            }
        }
        return false;
    }

    private Rect tempRect = new Rect();
    public View  onInterceptContentViewFocusSearch(WaterfallListView wv,View focused,int direction){
        if(isSlidingEnable) {
            int vector = FocusUtils.getVectorByDirection(direction, getOrientation());
            boolean b = requestSwitchToPageOnVector(vector);
            return b ? focused : null;
        }else{
            return null;
        }
    }

    public View  onContentViewFocusSearchFailed(WaterfallListView wv,View focused,int direction){
        Log.i(FOCUS_TAG, "start onContentViewFocusSearchFailed direction:"+direction+",isTranslationLeftModel:"+ isTranslationLeftModel());
        if(keyIntercept){
            Log.i(FOCUS_TAG, "onContentViewFocusSearchFailed direction:"+direction+",keyIntercept:"+ true);
            keyIntercept = false;
            return focused;
        }

        return null;
    }



//    @Override
//    public View focusSearch(View focused, int direction) {
//        final View v =  super.focusSearch(focused, direction);
//        Log.i(FOCUS_TAG,"-----focusSearch focused:"+focused+",direction");
//        Log.i(FOCUS_TAG,"focusSearch return "+v);
//        return v;
//    }
//
//    @Override
//    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
//        boolean b =  super.requestFocus(direction, previouslyFocusedRect);
//        Log.i(FOCUS_TAG,"-----requestFocus direction:"+direction);
//        return b;
//    }
//
//    @Override
//    public View focusSearch(int direction) {
//        View v =  super.focusSearch(direction);
//        Log.i(FOCUS_TAG,"focusSearch focusSearch:"+direction+",result:"+v);
//        return v;
//    }

    /**
     * 目标的当前页
     */
    int mTargetCurrent = -1;
    int mPrevPage = -1;
    int mPrevPageBeforeAnim = -1;

    public int getOrientation() {
        return orientation;
    }

    public RecyclerViewPager(Context context, boolean isVertical, boolean enableTransform, boolean isSliding, int firstTranslation, int rightTranslation, int leftTranslation, int animationDuration, int interpolatorType) {
        super(context);
        vp2 = new ViewPager2(context);
        this.orientation = isVertical ? ViewPager2.ORIENTATION_VERTICAL : ViewPager2.ORIENTATION_HORIZONTAL;
        vp2.setOrientation(orientation);
        mScrollHelper = new ViewPager2ScrollHelper(vp2);
        this.enableTransform = enableTransform;
        isSlidingEnable = isSliding;
        firstTranslationValue = firstTranslation;
        rightTranslationValue = rightTranslation;
        leftTranslationValue = leftTranslation;
        anDuration = animationDuration;
        interType = interpolatorType;
        mScreenWidth = getScreenWidth();
        mScreenHeight = getScreenHeight();
        setPageTransformer(enableTransform);
        addView(vp2);
        init();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (isSlidingEnable) {
            int translation = 0;
            if (Math.max((leftTranslationValue), 0) != 0) {
                translation = firstTranslationValue - leftTranslationValue;
            }
            //父布局传递进来的位置信息
            if (getChildCount() > 0) {
                if (firstLayout) {
                    if (changed) {
                        if (isTranslationLeftModel()) {
                            RecyclerViewPager.this.layout(leftTranslationValue + translation, 0, mScreenWidth + leftTranslationValue + translation, mScreenHeight);
                        } else {
                            RecyclerViewPager.this.layout(firstTranslationValue, 0, mScreenWidth + firstTranslationValue, mScreenHeight);
                        }
                    }
                } else {
                    if (changed) {
                        if (isTranslationLeftModel()) {
                            RecyclerViewPager.this.layout(leftTranslationValue + translation, 0, mScreenWidth + leftTranslationValue + translation, mScreenHeight);
                        } else {
                            RecyclerViewPager.this.layout(leftTranslationValue + translation, 0, mScreenWidth + leftTranslationValue + translation, mScreenHeight);
                        }
                    }
                }
            }
        }
        // getChildAt(0).layout(l);
        //RenderUtil.reLayoutView();
    }

    void setPageTransformer(boolean b) {
        this.enableTransform = b;
        final boolean disableScrollAnim = tabsParam != null && tabsParam.disableScrollAnimation;
        if (vp2 != null) {
            if (b) {
                vp2.setPageTransformer((page, position) -> {
                    if (page instanceof WaterfallListView) {
                        final int bindPosition = ((WaterfallListView) page).getBindPosition();
                        //Log.w(TAG, "PageTransformer ,position:" + position + ",prevPage:" + mPrevPage + ",targetPage:" + mTargetCurrent+",current:"+getCurrentItem()+",bindPosition:"+bindPosition+",mPrevPageBeforeAnim:"+mPrevPageBeforeAnim);
                        int diff = mTargetCurrent - mPrevPageBeforeAnim;
                        //Log.w(TAG, "PageTransformer diff" + diff );
                        if (disableScrollAnim) {
                            page.setAlpha(position == 0 ? 1 : 0);
                        } else {
                            if (bindPosition == mTargetCurrent || Math.abs(diff) < 2) {
                                if (position < -1 || position > 1) {
                                    page.setAlpha(0);
                                } else {
                                    //优化逻辑，去掉alpha动画，解决闪的问题
                                    if(tabsParam != null && tabsParam.alphaTransform){
                                        if (-1 <= position && position < 0) {
                                            page.setAlpha(1 + position - 0 * position);
                                        } else if (0 < position) {
                                            page.setAlpha(1 - position + 0 * position);
                                        } else {
                                            page.setAlpha(1);
                                        }
                                    }else {
                                        page.setAlpha(1);
                                    }
                                }
                            } else {
                                if (position < -1 || position > 1) {
                                    page.setAlpha(0);
                                } else {
                                    page.setAlpha(1);
                                }
                            }
                        }
                    }
                });
            } else {
                vp2.setPageTransformer(null);
            }
        }
    }

    private void exeSwitchAnimator() {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator objectAnimator1;
        ObjectAnimator objectAnimator2;
        objectAnimator1 = ObjectAnimator.ofFloat(RecyclerViewPager.this, "translationX", 0f, (float) rightTranslationValue, 0f);
        objectAnimator2 = ObjectAnimator.ofFloat(RecyclerViewPager.this, "alpha", 1.0f, 0.8f, 0f, 0.8f, 1.0f);
        animatorSet.setDuration(anDuration);
        animatorSet.setInterpolator(TimeInterpolatorUtils.getTimeInterpolatorByType(this.interType));
        animatorSet.play(objectAnimator1).with(objectAnimator2);
        animatorSet.start();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (vp2 != null) {
            vp2.measure(MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(h, MeasureSpec.EXACTLY));
            vp2.layout(0, 0, w, h);
        }
    }

    @Override
    public void addFocusables(ArrayList<View> views, int direction, int focusableMode) {
//        Log.i(TAG,"addFocusables focusSearchEnable ："+focusSearchEnable);
        if (!focusSearchEnable) {
            final View pageView = findPageView(vp2.getCurrentItem());
            if (pageView != null) {
                //只在当前显示的view里寻找焦点
                pageView.addFocusables(views, direction, focusableMode);
            }
        } else {
            super.addFocusables(views, direction, focusableMode);
        }
    }

    private RenderNode getBoundNode() {
        return boundNode;
    }


    public void setPageDataLoader(PageDataLoader loader) {
        this.mPageLoader = loader;
    }

    void buildAdapter() {
        if (tabHelpers == null) {
            tabHelpers = new ArrayList<>();
        }
        mAdapter = new PageAdapter();
        vp2.setAdapter(mAdapter);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        vp2.registerOnPageChangeCallback(onPageChangeCallback);
        if (rootView == null) {
            rootView = HippyViewGroup.findRootViewFromParent(this);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        vp2.unregisterOnPageChangeCallback(onPageChangeCallback);
        EsProxy.get().setTakeOverKeyEventListener(null);
    }

    public void setOffscreenPageLimit(int limit) {
        if (vp2 != null) {
            vp2.setOffscreenPageLimit(limit);
        }
    }

    /**
     * @param slidingMode "translation"
     */
    public void setSlidingMode(String slidingMode) {
        if (vp2 != null && !TextUtils.isEmpty(slidingMode)) {
            currentSlidingMode = slidingMode;
            slidingValue = slidingMode;
        }
    }

    void init() {
        //setupListNode();
        vp2.setOffscreenPageLimit(getTabsParam() == null ? 1 : getTabsParam().offscreenPageLimit);
        buildAdapter();
        onPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                TabUtils.logPerformance("onPageSelected position:"+position);
                final int prevPage = RecyclerViewPager.this.mPrevPage;
                if (LogUtils.isDebug()) {
                    Log.i(TAG, "onPageSelected position:" + position);
                }
                if (prevPage == position && getCurrentItem() == position) {
                    //这里可能会因viewPager内部重复调用了这个函数，导致页面刷新、焦点丢失的问题，所以这里拦截一下
                    Log.w(TAG,"onPageSelected return on same position:"+position+",current:"+getCurrentItem());
                    return;
                }
                RecyclerViewPager.this.mPrevPage = position;
                onBeforeChangeCurrentPage(position, prevPage);

                //requestLayoutManual();
                onChangeCurrentPage(position, prevPage);
                onAfterChangeCurrentPage(position, prevPage);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };
    }


    protected HippyEngineContext getHippyContext() {
        return RenderNodeUtils.getHippyContext(this);
    }


    void requestLoadDataOnCurrentChange(int item, int prevPage) {

        //加载当前页
        tryLoadPageData(item);
        if (tabsParam.loadDataStrategy == TabsParam.STRATEGY_ALWAYS) {
            //每次都加载新的数据，不提前加载
            return;
        }
        if(tabsParam.preloadStrategy == TabsParam.PRELOAD_STRATEGY_SINGLE){
            //只加载一次数据，后续一直用缓存
            return;
        }
        boolean forward = item - prevPage > 0;
        //预加载一页
        if (forward) {
            //向前方向
            final int total = mAdapter.getItemCount();
            final int nextPage = Math.min(total - 1, item + 1);
            if (nextPage != item) {
                this.tryLoadPageData(nextPage);
            }
        } else {
            //向后方向
            final int prevNext = Math.max(0, item - 1);
            if (prevNext != item) {
                this.tryLoadPageData(prevNext);
            }
        }
    }

    void tryLoadPageData(int item) {
        this.tryLoadPageData(item, false);
    }

    /**
     * //预加载其它页
     * let total = this.tabsContentData.length
     * let prevPage = Math.max(0, index - 1)
     * let nextPage = Math.min(total - 1, index + 1)
     * //拉临近上一页的数据
     * if (prevPage !== index) {
     * this.tryLoadLayoutData(prevPage)
     * }
     * //拉临近下一页的数据
     * if (nextPage !== index) {
     * this.tryLoadLayoutData(nextPage)
     * }
     */

    void tryLoadPageData(int item, boolean useDiff) {
        if (item < 0) {
            if (LogUtils.isDebug()) {
                Log.e(TAG, "tryLoadPageData return on item:" + item);
            }
            return;
        }
        final PageItem pageItem = getPageItem(item);
        if (LogUtils.isDebug()) {
            Log.v(TAG, "tryLoadPageData item:" + item + ",pageItem:" + pageItem);
        }
        if (pageItem.isNeedLoad() && mPageLoader != null) {
            if (LogUtils.isDebug()) {
                Log.e(TAG, "tryLoadPageData item:" + item + ",start !!!!");
            }
            //重新加载页面时，清空数据
            pageItem.notifyLoading();
            final View view = findPageView(item);
            if (view != null && !useDiff) {
                //当使用diff时，如果设置loading会导致diff无效，因为loading是采用数据的方式实现
                mContentFactory.changeLoading(view, item, true);
            }
            mPageLoader.loadPage(item, (o, throwable) -> {
                if (throwable == null && o instanceof HippyMap) {
                    HippyMap pageData = (HippyMap) o;
                    HippyArray data = pageData.getArray("list");
                    HippyMap params = pageData.getMap("params");
                    updateDataOnly(item, params, data);
                    tryUpdatePageView(item, useDiff, UPDATE_TYPE_INIT_DATA);
                    if (LogUtils.isDebug()) {
                        Log.d("ListViewPagerLogTest", "tryLoadPageData if 当前item值---->" + item);
                    }
                    requestResumeCurrentPage(item);
                } else {
                    updateDataOnly(item, null, null);
                }
            }, useDiff);
        } else {
            if (pageItem.dataState == 1) {
                boolean updateTab = false;
                if (mContentFactory.isSingleContent()) {
                    if (getCurrentItem() == pageItem.position) {
                        updateTab = true;
                    }
                }else{
                    updateTab = true;
                }
                if(updateTab) {
                    tryUpdatePageView(item, false, UPDATE_TYPE_RESET);
                    Log.d(TAG, "tryLoadPageData else当前item值---->" + item);
                    requestResumeCurrentPage(item);
                }
            } else {
                if (LogUtils.isDebug()) {
                    Log.e(TAG, "tryLoadPageData 没有设置数据，pageItem:" + pageItem);
                }
            }
        }
    }
    void syncSelectState4CustomNavList(int page){
        final TVListView tvListView = getNavListView();
        if (tvListView != null) {
            tvListView.setSelectChildPosition(page,true);
        }
    }

    /**
     * 在执行切换页面之前执行，有可能取消
     *
     * @param nextPage
     */
    void onPrepareChangeCurrentPage(int nextPage, int prevPage) {

        if (LogUtils.isDebug()) {
            Log.i(TAG, "onPrepareChangeCurrentPage prevPage:" + prevPage + ",nextPage:" + nextPage);
        }
        final View pageView = findPageView(mPrevPage);
        if (pageView != null) {
            mContentFactory.setDisplay(pageView, mPrevPage, false);
        }
        int prevItem = prevPage;
        if(getPageItem(prevItem) != null && getPageItem(prevItem).bindPlayerView != null){
//            Log.i(ReplaceChildView.TAG, "notifyBringToFront false on exeSwitchToPage page: "+prevItem);
            getPageItem(prevItem).bindPlayerView.notifyBringToFront(false);
        }
        if (getPageItem(nextPage) != null) {
            getPageItem(nextPage).resumedOnLayout = false;
        }
        TriggerTaskManagerModule.dispatchTriggerTask(this,"onBeforePageChange");
        //暂停所有非展示出来的页面
        removeCallbacks(resumeTaskRunnable);
        removeCallbacks(notifyResumePlayerRunnable);
        // 将当前页面置空
        pausePostTaskOnHide(prevPage);

        HippyMap map = new HippyMap();
        map.pushInt("current",prevPage);
        map.pushInt("next",nextPage);
        TabUtils.sendTabsEvent(tabsView,"onPrepareChangePage",map);
    }

    /**
     * 真正执行到切换页面任务，但是在那执行之前
     *
     * @param nextPage
     */
    void onBeforeChangeCurrentPage(int nextPage, int prevPage) {
        if (LogUtils.isDebug()) {
            Log.d(TAG, "onBeforeChangeCurrentPage nextPage:" + nextPage + ",prevPage:" + prevPage);
        }
        final PageItem pi = getPageItem(nextPage);
        if (isEnableDrawerAnimation() && pi != null && pi.drawerAnimationStartDirty) {
            resetDrawerAnimation(nextPage);
            pi.drawerAnimationStartDirty = false;
            HippyMap map = new HippyMap();
            map.pushInt("position",nextPage);
            TabUtils.sendTabsEvent(tabsView,"onDrawerOpenStart",map);
        }
        syncSelectState4CustomNavList(nextPage);
        TriggerTaskManagerModule.dispatchTriggerTask(this, "onPageChange");
        TriggerTaskManagerModule.dispatchTriggerTask(this, "onPageChange-"+nextPage);
        if(isPreferSaveMemory()){
            clearAllHidePageOnCurrentChange(nextPage);
        }

    }

    void onChangeCurrentPage(int nextPage, int prevPage) {
        if (LogUtils.isDebug()) {
            Log.d(TAG, "onChangeCurrentPage nextPage:" + nextPage + ",prevPage:" + prevPage);
        }
        //mContentFactory.clearPageData(nextPage, findPageView(nextPage));
        requestLoadDataOnCurrentChange(nextPage, prevPage);
        if (mOnPageEventListener != null) {
            mOnPageEventListener.onPageChanged(nextPage);
        }
    }

    //0 1  2 【3】4 5 6  -> 当位置3时，回收 1、5
    //回收所有不在current俩边的view,以达到节省内存的目的
    void clearAllHidePageOnCurrentChange(int current) {
        if (mAdapter != null && mAdapter.contentViews != null) {
            for (Map.Entry<Integer, View> entry : mAdapter.contentViews.entrySet()) {
                final int page = entry.getKey();
                boolean isMarkToClear = page < current - 1 || page > current + 1;
                if (isMarkToClear) {
                    final PageItem pi = getPageItem(page);
                    if (pi != null && !pi.updateDirty) {
                        final View view = entry.getValue();
                        if (view != null) {
                            pi.markDataDirty();
                            if (LogUtils.isDebug()) {
                                Log.i(TAG, "clearAllHide current: " + current + ",cleared : " + page);
                            }
                            mContentFactory.clearPageData(page, view);
                        }
                    }
                }
            }
        }

    }

    /**
     * 执行切换页面任务，在那执行之后
     *
     * @param nextPage
     */
    void onAfterChangeCurrentPage(int nextPage, int parePage) {
        if (LogUtils.isDebug()) {
            Log.d(TAG, "onAfterChangeCurrentPage nextPage:" + nextPage + ",parePage:" + parePage);
        }
        //
        //resume 当前页面task
        //requestResumePostTask(nextPage);
    }


    void requestResumeCurrentPage(int page) {
        if (LogUtils.isDebug()) {
            Log.d(TAG, "requestResumeCurrentPage mTargetCurrent===>\n" + mTargetCurrent + "vp2.getCurrentItem()===>\n" + vp2.getCurrentItem() + "page===>\n" + page
            );
        }
//        Log.i(TAG,"in touchMode:"+isInTouchMode());
        if (isSlidingEnable && !isTranslationLeftModel() && mTargetCurrent > -1) {
            if (mTargetCurrent == vp2.getCurrentItem()) {
                int currentPos = mTargetCurrent;
                this.requestResumePostTask(currentPos, tabsParam != null ? tabsParam.resumeTaskDelay : RESUME_TASK_DELAY);
            } else {
                if (isInTouchMode() && page == vp2.getCurrentItem()) {
                    this.requestResumePostTask(page, tabsParam != null ? tabsParam.resumeTaskDelay : RESUME_TASK_DELAY);
                }
                if (LogUtils.isDebug()) {
                    Log.d(TAG, "requestResumeCurrentPage return on page != getCurrentItem current:" + vp2.getCurrentItem() + "page:" + page);
                }
            }
        } else {
            if (page == vp2.getCurrentItem()) {
                this.requestResumePostTask(page, tabsParam != null ? tabsParam.resumeTaskDelay : RESUME_TASK_DELAY);
            } else {
                if (LogUtils.isDebug()) {
                    Log.d(TAG, "requestResumeCurrentPage return on page != getCurrentItem current:" + vp2.getCurrentItem() + "page:" + page);
                }
            }
        }
    }

    //    void requestResumePostTask(int page){
//        this.requestResumePostTask(page,500);
//    }
    void requestResumePostTask(int page, int delay) {
        int finalDelay = delay;
        removeCallbacks(resumeTaskRunnable);
        resumeTaskRunnable = () -> {
//            if (LogUtils.isDebug()) {
//                Log.i(TAG, "requestResumePostTask  on " + page);
//            }
            resumePostTaskOnHide(page);
            onCurrentPageToShow(page);
        };
        final PageItem pi = getPageItem(page);
        final View pv = findPageView(page);
        if (pv != null) {
            if (pi.pendingFocusPosition > -1) {
                if (pi.isDataValid()) {
                    delay = tabsParam != null ? tabsParam.firstResumeTaskDelay : FIRST_RESUME_TASK_DELAY;
                }
            }
        }
        delay = Math.max(150,delay);//由于resumeTask的时间最小是100毫秒，所以这里延迟一下
        if (LogUtils.isDebug()) {
            Log.i(TAG, "requestResumePostTask  on " + page+",final Delay:"+delay);
        }

        postDelayed(resumeTaskRunnable, delay);
    }

    private boolean hasSyncNavList = false;

    void onCurrentPageToShow(int page) {
        if(!hasSyncNavList){
            hasSyncNavList = true;
            syncSelectState4CustomNavList(page);
        }
        final PageItem pi = getPageItem(page);
        final View pv = findPageView(page);
        if (LogUtils.isDebug() && pi != null) {
            Log.d(TAG, "onCurrentPageToShow page:" + page + ",dataValid:" + pi.isDataValid() + ",pi.pendingFocusPosition：" + pi.pendingFocusPosition+",drawerAnimationDirty:"+pi.drawerAnimationDirty);
        }
//        if (pv == null && pi != null) {
//            Log.e(TAG,"onCurrentPageToShow pageView is null updateDirtyOnNullPageView"+pi.updateDirtyOnNullPageView);
//            pi.updateDirtyOnNullPageView = false;
//            mScrollHelper.reLayout();
//            reloadPage(page);
//        }
        if (pv != null && pi != null) {
            if (pi.pendingFocusPosition > -1) {
                if (LogUtils.isDebug()) {
                    Log.d(TAG, "onCurrentPageToShow page:" + page + ",dataValid:" + pi.isDataValid());
                }
                if (pi.isDataValid() && !mContentFactory.isLoadingShown(pv, page)) {
                    if (!mContentFactory.isLoadingShown(pv, page)) {
                        mContentFactory.requestFirstFocus(pv, pi.pendingFocusPosition);
                        pi.pendingFocusPosition = -1;
                    } else {
                        if (LogUtils.isDebug()) {
                            Log.e(TAG, " return on loading is shown");
                        }
                    }
                } else {
                    if (LogUtils.isDebug()) {
                        Log.e(TAG, "pi.pendingFocusPosition > -1 return on data not valid");
                    }
                }
            }
            mContentFactory.setDisplay(pv, page, true);
//            if(isFocusable() && pv != null){
//                pv.requestFocus();
//            }

        }
        if (isEnableDrawerAnimation() && !isTranslationLeftModel()) {
            if(pi != null && pi.drawerAnimationDirty && pi.isDataValid()) {
                pi.drawerAnimationDirty = false;
                Log.i(TAG,"onCurrentPageToShow exeDrawerAnimation "+getTranslationX());
                exeDrawerAnimation(true, page);
            }
        }
        if (tabsView != null) {
            if (!TextUtils.isEmpty(tabsView.autoFocusID)) {
//                Log.i(AutoFocusUtils.TAG,"---------onCurrentPageToShow autoFocusID:"+tabsView.autoFocusID+",page:"+page+",view:"+pv);
                AutoFocusUtils.setAppearFocusTag(tabsView, tabsView.autoFocusID, 50);
            }
        }
        if (pv != null) {
            if (pi != null) {
//                if(pi.bindPlayerView != null){
//                    Log.i(ReplaceChildView.TAG, "+++notifyBringToFront true on exeSwitchToPage ："+page);
//                    pi.bindPlayerView.notifyBringToFront(true);
//                }else{
                Log.i(ReplaceChildView.TAG, "+++notifyBringToFront true on onCurrentPageToShow ："+page);
                notifyResumePlayerRunnable = () -> {
                    notifyViewBringToFrontTraverse(HippyViewGroup.findPageRootView(tabsView), page, true);
                };
                postDelayed(notifyResumePlayerRunnable, tabsParam.resumePlayerTaskDelay);
                pi.currentPlayerViewIndex = page;
            }
        }
        HippyMap map = new HippyMap();
        map.pushInt("page", page);
        TabUtils.sendTabsEvent(tabsView,"onPageBringToFront",map);
    }

    void notifyViewBringToFrontTraverse(View view,int page,boolean front){
        if(view != null){
            //
//            if(front && view instanceof ReplaceChildView){
//                ReplaceChildView rcv = (ReplaceChildView) view;
//                Log.i(ReplaceChildView.TAG,"----notifyViewBringToFrontTraverse replaceChildIfNeed page:"+page);
//                rcv.replaceChildIfNeed();
//            }
            if(view instanceof ITVView) {
                final PageItem pi = getPageItem(page);
                String bindingPlayer = pi.bindingPlayer;
                if (bindingPlayer != null && !bindingPlayer.isEmpty()) {
                    if (bindingPlayer.equals(ExtendUtil.getViewSID(view))) {
                        //找到了对应的view
                        Log.i(ReplaceChildView.TAG, "exe notifyBringToFront sid:" + bindingPlayer + ",page:" + page + ",front:" + front+",view:"+ExtendUtil.debugView(view));
                        if(front) {
                            pi.bindPlayerView = (ITVView) view;
                        }
                        ((ITVView) view).notifyBringToFront(front);
                    }else{
                        //Log.e(ReplaceChildView.TAG,"notifyBringToFront sid is not equal bindingPlayer:"+bindingPlayer+",sid:"+ExtendUtil.getViewSID(view));
                    }
                }else{
                    Log.i(ReplaceChildView.TAG, "notifyBringToFront sid is null or relation is empty");
                }
            }
            if(view instanceof ViewGroup){
                ViewGroup vg = (ViewGroup) view;
                for(int i = 0; i < vg.getChildCount(); i++){
                    View child = vg.getChildAt(i);
                    if(child != null){
                        notifyViewBringToFrontTraverse(child,page,front);
                    }
                }
            }

        }
    }

    void requestUIPageViewBindData(PageItem pi, int pos, boolean useDiff) {
        pi.postDataTask = () -> {
            View pv = findPageView(pos);
            if (pv == null) {
                //Log.e("ZHAOPENG","requestUIPageViewBindData find pv is null pos:"+pos);
                pi.updateDirtyOnNullPageView = true;
            }
            exeUIPageViewBindData(pv, pi, pos, useDiff);
            pi.postDataTask = null;
        };
        postDelayed(pi.postDataTask, 100);
    }

    void pausePostTaskOnHide(int pos) {
        if (pos > -1) {
            View view = findPageView(pos);
            if (view != null) {
                mContentFactory.pausePostTask(pos, view);
            }
            if (LogUtils.isDebug() && view != null) {
                Log.i(TAG, "POST_TASK pausePostTaskOnHide page:" + pos + ",view:" + view);
            }
        }
    }

    void resumePostTaskOnHide(int pos) {
        View view = findPageView(pos);
        if (view != null) {

            mContentFactory.resumePostTask(pos, view);
        }
        if (LogUtils.isDebug() && view != null) {
            Log.e(TAG, "POST_TASK resumePostTaskOnHide page:" + pos + ",view:" + view);
        }
    }

    /**
     * 获得content在模版里的renderNode 例如
     * * <list-view-pager>
     * *     <tv-list></tv-list>
     * * </list-view-pager>
     * * 就是找到<tv-list>对应的node
     *
     * @return
     */
    RenderNode findContentTemplateNode() {
        if (getBoundNode() != null && getBoundNode().getChildCount() > 0) {
            return getBoundNode().getChildAt(0);
        } else {
            return null;
        }
    }

    /**
     * 真正给列表设置数据
     *
     * @param pv
     * @param pi
     */
    boolean exeUIPageViewBindData(View pv, PageItem pi, int pos, boolean updateByDiff) {
        boolean useDiff = tabsParam != null && tabsParam.useDiff;
        if (LogUtils.isDebug()) {
            Log.d(TAG, "exeUIPageViewBindData pv  :" + pv + ",pi:" + pi + ",useDiff:" + useDiff + ",this destroyed:" + this.destroyed);
        }
        final RenderNode node = findContentTemplateNode();
        if (pv != null) {
            if (pi.updateDirty || useDiff) {
                final Object data = pi.pageData;
                if (LogUtils.isDebug()) {
                    Log.e(TAG, "exeUIPageViewBindData data :" + data + ",pi:" + pi + ",useDiff:" + useDiff);
                }
                if (pi.pageData == null) {
                    if (LogUtils.isDebug()) {
                        Log.e(TAG, "exeUIPageViewBindData pageData is null return");
                    }
                } else {
                    int posIndex = mTargetCurrent;
                    if (isSlidingEnable && !isTranslationLeftModel() && mTargetCurrent > -1) {
                        if (LogUtils.isDebug()) {
                            Log.e(TAG, ">>exeUIPageViewBindData on isSlidingEnable pv  :" + pv + ",pi:" + pi + ",useDiff:" + useDiff + ",this destroyed:" + this.destroyed);
                        }
                        //                        if(pi.drawerAnimationDirty && getCurrentItem() == pi.position) {
//                            exeDrawerAnimation(true, posIndex);
//                            pi.drawerAnimationDirty = false;
//                        }
                    } else {
                        if (LogUtils.isDebug()) {
                            Log.e(TAG, ">>exeUIPageViewBindData  pv  :" + pv + ",pi:" + pi + ",useDiff:" + useDiff + ",this destroyed:" + this.destroyed);
                        }
                    }
                    mContentFactory.pausePostTask(pos, pv);
                    mContentFactory.bindPageData(pos, pv, pi, node, tabHelpers.get(pos).getFastListPageChangeListener(), tabHelpers.get(pos).getFastListScrollToTopListener(), updateByDiff);
                    if(pos == getCurrentItem()) {
                        requestResumePostTask(pos, getTabsParam().resumeTaskDelay);
                    }
                    pi.updateDirty = false;
                }
                requestLayoutManual();
                return true;
            } else {
                if (LogUtils.isDebug()) {
                    Log.e(TAG, "exeUIPageViewBindData return on updateDirty Pos: " + pos);
                }
            }
        } else {
            if (LogUtils.isDebug()) {
                Log.e(TAG, "exeUIPageViewBindData error return on pv is null  pi:" + pi);
            }
        }
        return false;
    }

    FastListView findWaterfallView(int position){
        if(mContentFactory != null && mContentFactory.isSingleContent()){
            final View pageView = findPageView(position);
            //hardCode
            if(pageView instanceof SingleTabContentAdapter.TabWrapperView){
                return ((SingleTabContentAdapter.TabWrapperView) pageView).getContentView();
            }
        }else{
            final View pageView = findPageView(position);
            if (pageView instanceof FastListView) {
                return (FastListView) pageView;
            }
        }
        return null;
    }


    View findPageView(int position) {

        View v =  mAdapter.findPageView(position);
//        if (v == null) {
//            View itemView = mScrollHelper.findPageViewByPosition(position);
//            if (itemView != null) {
//                mAdapter.contentViews.put(position,itemView);
//                Log.i("ZHAOPENG","!!!findPageView null on position "+position+",put itemView:"+itemView);
//            }else{
//                Log.e("ZHAOPENG","!!!findPageView null on position "+position);
//            }
//            v = itemView;
//        }

        return v;
    }

//    /**
//     * 尝试使用现在列表里的数据更新列表UI
//     *
//     * @param item 指定要更新的Page
//     */
//    void tryUpdatePageView(int item) {
//        this.tryUpdatePageView(item, false,false);
//    }

    /**
     * 尝试使用现在列表里的数据更新列表UI
     *
     * @param item 指定要更新的Page
     */
    void tryUpdatePageView(int item, boolean useDiff,int type) {
//        if(type == UPDATE_TYPE_ON_BIND && mContentFactory != null && mContentFactory.isSingleContent()){
//            //单tab下onBind时
//        }
//        Log.i(SingleTabContentAdapter.TAG,"-----tryUpdatePageView item:"+item+",type:"+type);
        final PageItem data = getPageItem(item);
        if (data != null) {
            removeCallbacks(data.postDataTask);
            if (LogUtils.isDebug()) {
                Log.d(TAG, "tryUpdatePageView exeUpdate index :" + item);
            }
            requestUIPageViewBindData(data, item, useDiff);
        } else {
            if (LogUtils.isDebug()) {
                Log.e(TAG, "tryUpdatePageView no need pageItem :" + null + ",index:" + item);
            }
        }

    }


    public PageItem getPageItem(int index) {
        if (mAdapter.mPageList == null) {
            return null;
        }
        if (index > -1 && index < mAdapter.mPageList.size()) {
            return mAdapter.mPageList.get(index);
        }
        return null;
    }

    public void updatePageData(int page, HippyArray array, HippyMap params) {
        if (LogUtils.isDebug()) {
            Log.v(TAG, "updatePageData called page:" + page + ",array:" + array);
        }
        final PageItem pi = getPageItem(page);
        if (pi != null) {
            updateDataOnly(page, params == null ? pi.pageData.params : params, array);
            tryUpdatePageView(page,false,UPDATE_TYPE_USER_SET);
            //Log.d("ListViewPagerLogTest","updatePageData if 当前page值---->"+page);
            requestResumeCurrentPage(page);
        } else {
            if (LogUtils.isDebug()) {
                Log.i(TAG, "updatePageData 时没有数据，直接设置");
            }
            this.setPageData(page, params == null ? new HippyMap() : params, array);
        }
    }

    public void setPageData(int page, HippyMap params, HippyArray array) {
        if (LogUtils.isDebug()) {
            Log.i(TAG, "setPageData called page:" + page + ",params:" + params);
            Log.i(TabsLocalCache.TAG, "setPageData called page:" + page + ",params:" + params);
        }
        if (params != null && params.containsKey("useDiff")) {
            this.setPageData(page, params, array, params.getBoolean("useDiff"));
        } else {
            this.setPageData(page, params, array, false);
        }
    }

    //为某一页设置数据
    public void setPageData(int page, HippyMap params, HippyArray array, boolean useDiff) {
        //HippyArray array = pageData.getArray("data");
        //getCurrentList().setPendingData();
        if (LogUtils.isDebug()) {
            Log.i(TAG, "setPageData called page:" + page + ",array:" + array);
        }
        //先更新数据
        Log.i(ReplaceChildView.TAG, "setPageData called page:" + page + ",params:" + params);
        updateDataOnly(page, params, array);
        if (array != null && array.size() > 0) {
            final View view = findPageView(page);
            if (view != null) {
                mContentFactory.changeLoading(view, page, false);
            }
        }
        //如果current == page，证明需要更新当前页面
        if (mContentFactory.isSingleContent()) {
            if (isCurrentPageShown(page)) {
                tryUpdatePageView(page, useDiff, UPDATE_TYPE_USER_SET);
                //Log.d("ListViewPagerLogTest","setPageData 当前page值---->"+page);
                requestResumeCurrentPage(page);
            }
        }else {
            tryUpdatePageView(page, useDiff, UPDATE_TYPE_USER_SET);
            //Log.d("ListViewPagerLogTest","setPageData 当前page值---->"+page);
            requestResumeCurrentPage(page);
        }
    }

    public int UPDATE_TYPE_INIT_DATA = 0;
    public int UPDATE_TYPE_RESET = 1;
    public int UPDATE_TYPE_USER_SET = 2;
    public int UPDATE_TYPE_ON_BIND = 3;

    View lastFocusView;



    public void reloadAll(boolean tryUpdateCurrent) {
        this.reloadAll(tryUpdateCurrent,true);
    }
    public void reloadAll(boolean tryUpdateCurrent,boolean useDiff) {
        final int current = this.getCurrentItem();
        final int pageCount = getTotalPage();
        if (LogUtils.isDebug()) {
            Log.i(TAG, "reloadAll pageCount:" + pageCount + ",current:" + current);
        }
        for (int i = 0; i < pageCount; i++) {
            final PageItem pi = getPageItem(i);
            if (pi != null) {
                pi.markToReload();
            }
            if (current == i && tryUpdateCurrent) {

                this.tryLoadPageData(i, useDiff);
            }
        }
    }

    public void reloadPage(int position) {
        this.reloadPage(position,true);
    }
    public void reloadPage(int position,boolean useDiff) {
        final PageItem pi = getPageItem(position);
        Log.i(TAG, "reloadPage position:" + position+",useDiff:"+useDiff);
        if (pi != null) {
            pi.markToReload();
            this.tryLoadPageData(position, useDiff);
        }
    }

    public void markToReload(int position){
        final PageItem pi = getPageItem(position);
        Log.i(TAG, "reloadPage position:" + position);
        if (pi != null) {
            pi.markToReload();
        }
    }

    public void removePageData(int page,int position,  int deleteCount){
        PageItem pi = getPageItem(page);
        pi.removeSections(position, deleteCount);
        pi.markDataDirty();
        final View contentView = findPageView(page);
        Log.i(TAG,"removePageData contentView:"+contentView+",position:"+position+",deleteCount:"+deleteCount);
        mContentFactory.removePageData(position,contentView,deleteCount);
    }

    public void insertPageData(int page,int position, HippyArray sections){
        PageItem pi = getPageItem(page);
        pi.insertSections(position, sections);
        pi.markDataDirty();
        final View contentView = findPageView(page);
        Log.i(TAG,"insertPageData contentView:"+contentView+",position:"+position+",sections:"+sections.size());
        if (contentView != null) {
            mContentFactory.insertPageData(position,contentView,sections);
        }
    }

    private boolean isCurrentPageShown(int page){
        return getCurrentItem() == page && page != -1;
    }

    //为某一页追加数据
    public void addPageData(int page, HippyMap params, HippyArray array) {
        PageItem pi = getPageItem(page);
        Log.i(TAG, "addPageData page:" + page+",params:"+params+",arraySize:"+array.size()+",postTask:"+pi.postDataTask);
//        addPageDataOnly(page, array);
//        if (page == vp2.getCurrentItem()) {
//            tryUpdatePageView(page);
//            requestResumeCurrentPage(page);
//        }

        if (pi.postDataTask == null) {
            int deleteCount = 0;
            if (params != null) {
                deleteCount = params.getInt("deleteCount");
            }
            if (page == vp2.getCurrentItem()) {
                final View view = findPageView(page);
                if (mContentFactory != null && view != null) {
                    mContentFactory.addPageData(page, view, array, deleteCount);
                }
                requestLayoutManual();
            }
        }else{
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    int deleteCount = 0;
                    if (params != null) {
                        deleteCount = params.getInt("deleteCount");
                    }
                    if (page == vp2.getCurrentItem()) {
                        final View view = findPageView(page);
                        if (mContentFactory != null && view != null) {
                            mContentFactory.addPageData(page, view, array, deleteCount);
                        }
                        requestLayoutManual();
                    }
                }
            };
            postDelayed(task, 150);
        }
    }


    void updateDataOnly(int page, HippyMap params, Object data) {
        final PageItem pi = getPageItem(page);
        pi.pageData = new PageData(data);
        pi.pageData.params = params;
        if (params != null) {
            pi.bindingPlayer = params.getString("bindingPlayer");
        }
        if (LogUtils.isDebug()) {
            Log.d(TAG, "updateDataOnly page:" + page + ",pi:" + pi);
        }
        Log.d(ReplaceChildView.TAG, "updateDataOnly page:" + page + ",params:" + params);
        assert pi != null : "updateDataOnly error,这里数据不可为空";
        pi.markDataDirty();
        if (data != null) {
            pi.notifyLoaded();
        } else {
            pi.notifyReset();
        }
    }

    void addPageDataOnly(int page, Object data) {
        final PageItem pi = getPageItem(page);
        if (pi.pageData != null && pi.pageData.rawData != null) {
            ((HippyArray) pi.pageData.rawData).pushArray((HippyArray) data);
            assert pi != null : "updateDataOnly error,这里数据不可为空";
            pi.markDataDirty();
            if (data != null) {
                pi.notifyLoaded();
            } else {
                pi.notifyReset();
            }
        }
    }

    TVListView mNaveListView;

    TVListView getNavListView(){
        if (mNaveListView != null) {
            return mNaveListView;
        }
        if (tabsParam != null && tabsParam.syncNavListSID != null) {
            View v = null;
            final View root = HippyViewGroup.findPageRootView(this);
            if (root != null) {
                v = ExtendUtil.findViewBySID(tabsParam.syncNavListSID,root);
            }
            if(v instanceof TVListView){
                 mNaveListView = (TVListView) v;
            }
        }
        return mNaveListView;
    }


    private void resetDrawerAnimation(int item){
        if(!isTranslationLeftModel()) {
            if (this.getWidth() > 0) {
                this.setTranslationX(getWidth());
            }
            this.invalidate();
            PageItem pi = getPageItem(item);
            if (pi != null) {
                pi.drawerAnimationDirty = true;
                pi.drawerAnimationStartDirty = true;
            }
        }
    }
    public void requestSwitchToPage(final int item, final boolean animated) {
        if(isEnableDrawerAnimation()){
            if (drawerAnimation != null) {
                drawerAnimation.cancel();
            }
            resetDrawerAnimation(item);
        }

        if (LogUtils.isDebug()) {
            Log.d(TAG, "requestSwitchToPage page:" + item + ",animated:" + animated);
        }
        final int prev = mTargetCurrent;
        final View prevView = findPageView(prev);
        if (prevView != null && prevView.hasFocus()) {
            prevView.clearFocus();
        }

        if (prev == item) {
            if (LogUtils.isDebug()) {
                Log.e(TAG, "PageAdapterEvent:requestSwitchToPage return on same position:" + item + ",vp2.getOrientation():" + vp2.getOrientation());
            }
            PageItem pi = getPageItem(item);
            if (pi.disableScrollOnFirstScreen && tabsParam.autoScrollToTop) {
                final View view = findPageView(item);
                if (view != null) {
                    mContentFactory.contentToTop(view, 0);
                }
            }
            return;
        } else {
            if (!tabsParam.autoScrollToTop) {
                //不自动滚动的，当tab切换后，也需要滚动动顶部
                final View view = findPageView(item);
                if (view != null) {
                    mContentFactory.contentToTop(view, 0);
                }
            }
        }
        this.mTargetCurrent = item;
        onPrepareChangeCurrentPage(item, prev);
        if (pageRunnable != null) {
            removeCallbacks(pageRunnable);
            pageRunnable = null;
        }
        pageRunnable = new Runnable() {
            @Override
            public void run() {
                exeSwitchToPage(item, animated && enableTransform);
            }
        };
        postDelayed(pageRunnable, tabsParam != null ? tabsParam.pageSwitchDelay : PAGE_SWITCH_DELAY);
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
        //requestLayoutManual();
    }

    Object getAutoFocusID() {
        return tabsView == null ? null : tabsView.autoFocusID;
    }

    public void exeSwitchToPage(int item, boolean animated) {
        //viewPager2的切换动画无法调整，所以这里替换成mScrollHelper来切换
        //vp2.setCurrentItem(item,animated);
        final View pageView = findPageView(mPrevPage);
        if (pageView != null) {
            if (getAutoFocusID() != null && pageView.hasFocus()) {
                Log.e(TAG, "exeSwitchToPage clear focus on PageChange!!!");
                pageView.clearFocus();
            }
            mContentFactory.setDisplay(pageView, item, false);
        }
        //Log.e(ReplaceChildView.TAG, "exeSwitchToPage prev:" + prev + ",item:" + item + ",pageItem:" + getPageItem(item));

        //notifyViewBringToFrontTraverse(HippyViewGroup.findPageRootView(tabsView),prev,false);
        if (LogUtils.isDebug()) {
            Log.d(TAG, "exeSwitchToPage diff :" + (item - vp2.getCurrentItem()) + ",item:" + item + ",mPrevPage:" + mPrevPage);
        }
        final int diff = Math.abs(item - vp2.getCurrentItem());
        //避免快速滑动到相同item时不显示
        if (diff == 1) {
            mPrevPageBeforeAnim = vp2.getCurrentItem();
//            mPrevPageBeforeAnim = -1;
        } else {
            mPrevPageBeforeAnim = -1;
        }
        boolean anim = diff < 4 && animated;

        if (mContentFactory != null) {
            anim &= !mContentFactory.isSingleContent();
//            if (mContentFactory.isSingleContent()) {
//                tryUpdatePageView(item,false,UPDATE_TYPE_RESET);
//            }
            final PageItem pi = getPageItem(item);
            if (pi != null && mContentFactory.isSingleContent()) {
                pi.updateDirty = true;
            }
            View contentView = findPageView(item);
            mContentFactory.onBeforeChangeCurrentPage(contentView,item,-1);
        }

        //如果页面切换差距过大，去掉动画，直接切换
        if (isSlidingMode()) {
            mScrollHelper.setCurrentItem(item, anim, 4);
        } else {
            mScrollHelper.setCurrentItem(item, anim, 4);
        }
        //Log.d("ListViewPagerLogTest","exeSwitchToPage 当前item值---->"+item);

        requestResumeCurrentPage(item);
    }


    NativeGestureDispatcher mGestureDispatcher;

    @Override
    public NativeGestureDispatcher getGestureDispatcher() {
        return mGestureDispatcher;
    }

    @Override
    public void setGestureDispatcher(NativeGestureDispatcher dispatcher) {
            this.mGestureDispatcher = dispatcher;
    }


//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        boolean b =  super.dispatchTouchEvent(ev);
//        Log.i(TAG,"ViewPager dispatchTouchEvent "+b);
//        return b;
//    }

    void requestLayoutManual() {
        if (getId() != -1) {
            RenderUtil.requestNodeLayout(this);
        } else {
            RenderUtil.reLayoutView(this, (int) getX(), (int) getY(), getWidth(), getHeight());
        }
    }

    public void reset() {
        this.mPrevPage = -1;
        //for(int i = 0; )
        resetContent();
        if (getCurrentItem() > -1) {//重新设置数据后，将吸顶重置
            Log.e(TAG, "contentToTop on reset getCurrentItem " + getCurrentItem());
            //mContentFactory.contentToTop(findPageView(getCurrentItem()),getCurrentItem());
            final TabHelper th = tabHelpers != null ? tabHelpers.get(getCurrentItem()) : null;
            if (th != null) {
                if (th.getFastListPageChangeListener().isOnTop()) {
                    th.getFastListPageChangeListener().setOnTop(false);
                    if (isHideOnSingleTab) {
                        TabUtils.sendTabsEvent(tabsView, TabEnum.SUSPENSION_BOTTOM_START.getName(), new HippyMap());
                    } else {
                        TabUtils.moveToBottom(suspensionView, tabsView, th.getFastListPageChangeListener().isUseSuspensionBg(), th.getFastListPageChangeListener().getSuspensionTop());
                    }
                }
                th.getFastListPageChangeListener().setTotalDy(0);
            }
        }
        vp2.setAdapter(null);

    }

    public void resetContent(){
        Log.i(TAG,"resetContent pageCount:"+getTotalPage());
        if (getTotalPage() > 0 && mContentFactory != null) {
            for(int i = 0; i < getTotalPage(); i ++){
                final View view = findPageView(i);
                if (view != null) {
                    mContentFactory.reset(i,view);
                }
            }

        }
    }

    public void setInitInfo(int count, int init, HippyArray data, boolean isHideOnSingleTabs) {
        assert mAdapter != null;
        final int oldCount = mAdapter.getItemCount();
        boolean changed = mAdapter.getItemCount() != count || count == 0 || oldCount > 0;
        if (LogUtils.isDebug()) {
            Log.d(TAG, "PageAdapterEvent: setInitInfo called count:" + count + ",initPage:" + init + ",changed:" + changed);
        }
        if (hasSetData) {
            Log.e(TAG, "setInitInfo reset");
            reset();
            mAdapter.clear();
        }
        TabUtils.logPerformance("recycler setAdapter count:"+count);
        if (vp2.getAdapter() == null) {
            vp2.setAdapter(mAdapter);
        }
        if (count > 0) {
            this.initPage = init;
//            requestSwitchToPage(init,false);
            this.mTargetCurrent = init;
            this.isHideOnSingleTab = isHideOnSingleTabs;
            final boolean isHasSetData = hasSetData;
            mScrollHelper.postTaskDelay(() -> exeSwitchToPage(init, false), 30);
            if (isHasSetData) {
                Log.e(TAG, "setInitInfo hasSetData requestLoadDataOnCurrentChange!!");
                mScrollHelper.postTaskDelay(() -> requestLoadDataOnCurrentChange(initPage, mPrevPage), 100);
            }
            mAdapter.setup(count, data);
            for (int i = 0; i < count; i++) {
                TabHelper tabHelper = new TabHelper();
                if (suspensionView != null) {
                    tabHelper.setFastListPageChangeListener(new FastListPageChangeListener(tabsView, suspensionView,
                            isSuspension, useSuspensionBg, isHideOnSingleTab, tabsParam.checkOffset, i, tabsParam.listenScrollEvent));
                } else {
                    tabHelper.setFastListPageChangeListener(new FastListPageChangeListener(tabsView, isSuspension,
                            useSuspensionBg, isHideOnSingleTab, tabsParam.checkOffset, i, tabsParam.listenScrollEvent));
                }
                tabHelper.setFastListScrollToTopListener(new FastListScrollToTopListener(tabsView, i));
                tabHelper.setTabsView(tabsView);
                tabHelpers.add(tabHelper);
            }
            mAdapter.notifyDataSetChanged();
            hasSetData = true;
//            setCurrentItem(init,false,0);
        } else {
            vp2.setAdapter(null);
        }
        if (changed) {
            requestLayoutManual();
        }
    }

    public void setInitialPageIndex(int initialPage) {
        this.initPage = initialPage;
    }

    public void setFocusSearchEnabled(boolean b) {
        this.focusSearchEnable = b;
    }

    public void setInitFocusPosition(int i) {
        final PageItem pi = getPageItem(i);
        if (pi != null) {
            pi.pendingFocusPosition = 0;
        }
    }

    void blockRootView() {
        FocusDispatchView.blockFocus(getRootView());
    }

    void unBlockRootView() {
        FocusDispatchView.unBlockFocus(getRootView());
    }

    void requestDefaultFocus(int delay, int pageIndex) {
//        blockRootView();
        final View view = findPageView(pageIndex);
        if (view != null) {
            //view.clearFocus();
//            view.requestFocus();
            blockRootView();
            //这里不可以清除焦点，否则会造成焦点丢失
//            if(view instanceof ViewGroup){
//                ViewGroup vg = (ViewGroup) view;
//                if (vg.getFocusedChild() != null) {
//                    vg.getFocusedChild().clearFocus();
//                }
//            }
            mContentFactory.contentToTop(view, pageIndex);
            postDelayed(() -> {
                unBlockRootView();
                mContentFactory.requestFirstFocus(findPageView(pageIndex), 0);
            }, delay);
        }
    }

    public void resetSuspensionView(){
        final TabHelper th = tabHelpers != null ? tabHelpers.get(getCurrentItem()) : null;
        if (th != null && th.getFastListPageChangeListener() != null ) {
            th.getFastListPageChangeListener().setOnTop(false);
            TabUtils.moveToBottom(suspensionView, tabsView, th.getFastListPageChangeListener().isUseSuspensionBg(), th.getFastListPageChangeListener().getSuspensionTop(),0);
        }
    }

    public boolean handleBackPressed(int pos, boolean isSingleTabState) {
        final View v = findPageView(pos);
        final TabHelper th = tabHelpers != null ? tabHelpers.get(pos) : null;
        if (th != null) {
            if (th.getFastListPageChangeListener().isOnTop()) {
                th.getFastListPageChangeListener().setOnTop(false);
                if (isHideOnSingleTab) {
                    TabUtils.sendTabsEvent(tabsView, TabEnum.SUSPENSION_BOTTOM_START.getName(), new HippyMap());
                    resetSuspensionView();
                } else {
                    TabUtils.moveToBottom(suspensionView, tabsView, th.getFastListPageChangeListener().isUseSuspensionBg(), th.getFastListPageChangeListener().getSuspensionTop());
                }
            }
            th.getFastListPageChangeListener().setTotalDy(0);
        }
        if (isSingleTabState) {
            if (!mContentFactory.isOnScrollTop(v, pos, tabsParam.checkOffset * v.getHeight())) {
                if(tabsParam.autoReFocusOnSingleTab){
//                    requestDefaultFocus(300, 0);
                    mContentFactory.scrollToFocus(v, 0);
                    return true;
                }else{
                    return false;
                }
            } else {
                return false;
            }
        }
        if (v != null) {
            if (tabsParam.autoScrollToTop) {
                if (th != null) {
                    Log.i(TAG,"isOnTop :"+mContentFactory.isOnScrollTop(v, pos, tabsParam.checkOffset * v.getHeight()));
                }
                if(getOrientation() == ViewPager2.ORIENTATION_VERTICAL && th != null){
                    if(!mContentFactory.isOnScrollTop(v, pos, tabsParam.checkOffset * v.getHeight())){
                        requestDefaultFocus(300, pos);
                        return true;
                    }else {
                        return false;
                    }
                }
                mContentFactory.contentToTop(v, pos);
            }
            return true;
        }
        return false;
    }

    public void invokeContentFunction(int pageIndex, String functionName, HippyArray var, Promise promise) {
        if (mContentFactory != null) {
            final View view = findPageView(pageIndex);
            if (view != null) {
                Log.i(TAG, "invokeContentFunction  view is " + view);
                mContentFactory.dispatchUIFunction(view, pageIndex, functionName, var, promise);
            } else {
                Log.e(TAG, "invokeContentFunction error view is null");
            }
        }
    }

    public void destroy() {
        this.destroyed = true;

        try {
            if (mAdapter != null) {
                int count = mAdapter.getItemCount();
                for (int i = 0; i < count; i++) {
                    PageItem pi = getPageItem(i);
                    if (pi != null && pi.postDataTask != null) {
                        removeCallbacks(pi.postDataTask);
                    }
                }
                mAdapter.destroy();
            }
            removeCallbacks(resumeTaskRunnable);
            if (pageRunnable != null) {
                removeCallbacks(pageRunnable);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
        if (mOnPageEventListener != null) {
            mOnPageEventListener = null;
        }
    }

    @Override
    public void setBlockFocusOn(int[] focus) {

    }

    public int[] mBlockFocusOnFail;

    @Override
    public void setBlockFocusOnFail(int[] directions) {
        this.mBlockFocusOnFail = directions;
    }

    @Override
    public ListViewControlProp getControlProps() {
        return null;
    }

    @Override
    public void diffSetScrollToPosition(int position, int offset) {

    }

    public void scrollToFocus(int pos) {
        if (mContentFactory != null) {
            final View view = findPageView(getCurrentItem());
            if (view != null) {
                mContentFactory.scrollToFocus(view, pos);
            } else {
                if (LogUtils.isDebug()) {
                    Log.e(TAG, "scrollToFocus error view is null");
                }
            }
        }
    }

    public void setUseAdvancedFocusSearch(boolean flag) {
        this.useAdvancedFocusSearch = flag;
    }

    public int searchItemIDPageIndexTraverse(String itemID){
        final int pageCount = getTotalPage();
        int toUpdateIndex = -1;
        //从所有page中寻找数据
        for (int i = 0; i < pageCount; i++) {
            final PageItem pi = getPageItem(i);
            if (pi != null) {
                if (pi.pageData != null && pi.pageData.rawData instanceof HippyArray) {
                    final int searchIndex = RenderNodeUtils.searchItemIndexByData((HippyArray) pi.pageData.rawData, itemID, "_id");
                    if (searchIndex > -1) {
                        //如果寻找到了数据，则证明需要更新这一页
                        toUpdateIndex = i;
                        break;
                    }
                }
            }
            // mContentFactory.bindPageData(toUpdateIndex,);
        }
        return toUpdateIndex;
    }


    /**
     * 根据tabs中的数据指定的id，来寻找对应item刷新
     *
     * @param id
     * @param newData
     */
    public void updateItemMatched(String idKey, Object id, Object newData, boolean traverse) {
        final int pageCount = getTotalPage();
        int toUpdateIndex = -1;
        //从所有page中寻找数据
        for (int i = 0; i < pageCount; i++) {
            final PageItem pi = getPageItem(i);
            if (pi != null) {
                if (pi.pageData != null && pi.pageData.rawData instanceof HippyArray) {
                    final int searchIndex = RenderNodeUtils.searchItemIndexByData((HippyArray) pi.pageData.rawData, id, idKey);
                    if (searchIndex > -1) {
                        //如果寻找到了数据，则证明需要更新这一页
                        toUpdateIndex = i;
                        break;
                    }
                }
            }
            // mContentFactory.bindPageData(toUpdateIndex,);
        }
        Log.i(TAG, "updateItemByID toUpdateIndex:" + toUpdateIndex + ",id:" + id);
        if (toUpdateIndex > -1) {
            View view = findPageView(toUpdateIndex);
            PageItem pi = getPageItem(toUpdateIndex);
            int itemPos = -1;
            if (pi != null) {
                if (pi.pageData != null && pi.pageData.rawData instanceof HippyArray) {
                    itemPos = RenderNodeUtils.replaceData((HippyArray) pi.pageData.rawData, newData, id, idKey);
                }
            }
            Log.i(TAG, "updateItemByID find itemPosition:" + itemPos);
            if (itemPos > -1) {
                mContentFactory.updateItemByID(toUpdateIndex, view, pi, itemPos, newData, traverse);
            }
        } else {
            Log.e(TAG, "updateItemByID cant find data , return id:" + id);
        }

    }

    public void requestNodeFocus(String id) {
        View view = TabUtils.findNodeViewByID(this, id);
        if (LogUtils.isDebug()) {
            Log.i(TAG, "requestNodeFocus find view:" + view);
        }
        if (LogUtils.isDebug()) {
            logAllChildren(this);
        }
        if(Utils.isParentItemRootView(view)){
            final View v =  Utils.getPlaceholderContainer(view);
            if (v != null) {
                view = v;
            }
        }
        if (view != null) {
            view.requestFocus();
        } else {
            Log.e(TAG, "requestNodeFocus error findView null ,id :" + id);
        }
    }


    private void logAllChildren(View view) {
        Log.i(TAG, "logAllChildren view tag:" + view.getTag());
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                logAllChildren(((ViewGroup) view).getChildAt(i));
            }
        }
    }

    public void requestPageFocus(int page) {
        Log.i(TAG, "requestPageFocus page:" + page);
        final View view = findPageView(page);
        if (view != null) {
            view.requestFocus();
        }
    }

    /**
     * 更新指定page中的指定item
     * @param pageIndex page索引
     * @param position 版块position
     * @param childIndex 格子index
     * @param data 数据
     * @param updateView 是否更新页面
     * @param traverse 是否遍历更新
     */
    public void updateChildNode(int pageIndex, int position, int childIndex, Object data, boolean updateView, boolean traverse) {
        final PageItem pi = getPageItem(pageIndex);
        //mContentFactory.updateChildItemByID();
        if (LogUtils.isDebug()) {
            Log.i(TAG, "updateChildNode pageIndex:" + pageIndex + ",position:" + position + ",childIndex:" + childIndex+",updateView:"+updateView+",traverse:"+traverse);
            Log.i(TAG, "updateChildNode data:" + data);
        }
        if (pi.pageData != null && pi.pageData.rawData instanceof HippyArray) {
            //itemPos =  RenderNodeUtils.replaceData((HippyArray) pi.pageData.rawData,newData,id,idKey);
            RenderNodeUtils.replaceChildData((HippyArray) pi.pageData.rawData, position, data, childIndex);
            if (updateView) {
                mContentFactory.updateChildItemByID(findPageView(pageIndex), pi, position, childIndex, data, traverse);
            }
        }else{
            Log.e(TAG,"updateChildNode return on "+pi);
        }
    }

    public HippyMap getChildNodeState(int pageIndex, int position, int childIndex, String name) {
        View rootItemView = mContentFactory.findRootItemView(findPageView(pageIndex), position);
        if (LogUtils.isDebug()) {
            Log.i(TAG, "getChildNodeState pageIndex:" + pageIndex + ",position:" + position + ",childIndex:" + childIndex + ",rootItemView:" + rootItemView + ",name:" + name);
        }
        if (rootItemView != null) {
            if (!TextUtils.isEmpty(name)) {
                View targetGroup = ExtendUtil.findViewByName(name, rootItemView);
                if (LogUtils.isDebug()) {
                    Log.i(TAG, "getChildNodeStateByName pageIndex:" + pageIndex + ",position:" + position + ",childIndex:" + childIndex + ",targetGroup:" + targetGroup);
                }
                if (targetGroup != null) {
                    return ExtendUtil.getChildState(targetGroup, childIndex);
                } else {
                    return null;
                }
            } else {
                return ExtendUtil.getChildState(rootItemView, childIndex);
            }
        } else {
            return null;
        }
    }

    public View findRootNodeView(int pageIndex, int position) {
        return mContentFactory.findRootItemView(findPageView(pageIndex), position);
    }


    public HippyMap getRootNodeState(int pageIndex, int position) {
        if (LogUtils.isDebug()) {
            Log.i(TAG, "getRootNodeState pageIndex:" + pageIndex + ",position:" + position);
        }
        View rootItemView = mContentFactory.findRootItemView(findPageView(pageIndex), position);
        if (rootItemView != null) {
            return ExtendUtil.getViewState(rootItemView);
        } else {
            return null;
        }
    }

    public void updateRootNode(int pageIndex, int position, Object data, boolean traverse) {
        final PageItem pi = getPageItem(pageIndex);
        if (pi.pageData != null && pi.pageData.rawData instanceof HippyArray) {
            //itemPos =  RenderNodeUtils.replaceData((HippyArray) pi.pageData.rawData,newData,id,idKey);
            RenderNodeUtils.replaceRootData((HippyArray) pi.pageData.rawData, position, data);
            mContentFactory.updateItemByID(pageIndex, findPageView(pageIndex), pi, position, data, traverse);
        }
    }

    @Override
    public View getHostView() {
        return this;
    }


    public void dispatchUIFunctionOnChildNode(HippyArray var, Promise promise) {
        //Log.d(TAG,"dispatchUIFunctionOnChildNode called var:"+var);
        final int pageIndex = var.getInt(0);
        final int position = var.getInt(1);
        final int childIndex = var.getInt(2);
        final String listViewName = var.getString(3);
        final HippyArray rootNodeParamsArray = var.getArray(4);
        //final String childTargetViewName = var.getString(4);
        final View rootItemView = findRootNodeView(pageIndex, position);
        if (rootItemView != null) {
            View listView = ExtendUtil.findViewByName(listViewName, rootItemView);
            if (listView instanceof FastPendingView) {
                //Log.e(TAG,"dispatchUIFunctionOnChildNode listView view :"+listView);
                ((FastPendingView) listView).dispatchItemFunction(rootNodeParamsArray, promise);
                return;
            } else {
                Log.e(TAG, "dispatchUIFunctionOnChildNode error on find FastPendingView view :" + listView);
            }
        } else {
            Log.e(TAG, "dispatchUIFunctionOnChildNode error on rootView is null");
        }
        if (promise != null) {
            promise.reject(-1);
        }
    }

    public void setFirstFocusTarget(int page, String id) {
        final View pageView = findPageView(page);
        Log.i(TAG, "setFirstFocusTarget page:" + page + ",id:" + id + ",pageView:" + pageView);
        if (pageView instanceof WaterfallListView) {
            ((WaterfallListView) pageView).setFirstFocusTarget(id);
        }
    }

    public void cancelAll() {
        Log.i(TAG,"recyclerView cancelAll");
        if (resumeTaskRunnable != null) {
            removeCallbacks(resumeTaskRunnable);
        }
        if (pageRunnable != null) {
            removeCallbacks(pageRunnable);
        }
        final PageItem item = getPageItem(getCurrentItem());
        if (item != null) {
            removeCallbacks(item.postDataTask);
            item.postDataTask = null;
        }
        View view = findPageView(getCurrentItem());
        if (view != null) {
            mContentFactory.recycle(getCurrentItem(),view);
        }
    }

    public void onChunkAttachedToWindow(Chunk chunk) {
//        Log.i(ReplaceChildView.TAG,"onChunkAttachedToWindow chunk:"+ WaterfallUtils.debugChunk(chunk));
        final String sid = WaterfallUtils.getChunkSID(chunk);
        final PageItem pi = getPageItem(getCurrentItem());
        if (pi.bindingPlayer != null && pi.bindingPlayer.equals(sid)) {
            if (chunk.getView() instanceof ITVView) {
                Log.i(ReplaceChildView.TAG,"onChunkAttachedToWindow notifyBringToFront true on page:"+getCurrentItem());
                //((ITVView) chunk.getView()).notifyBringToFront(true);
                notifyResumePlayer((ITVView) chunk.getView());
            }
        }
    }

    public void notifyResumePlayer(ITVView rv) {
        if (notifyResumePlayerRunnable != null) {
            removeCallbacks(notifyResumePlayerRunnable);
        }
        notifyResumePlayerRunnable = () -> {
            if (rv != null) {
                Log.i(ReplaceChildView.TAG,"notifyResumePlayer  true view:"+ExtendUtil.debugView(rv.getView()));
                rv.notifyBringToFront(true);
            }
        };
        postDelayed(notifyResumePlayerRunnable, tabsParam == null ? 500 : tabsParam.resumePlayerTaskDelay);
    }

    public void searchReplaceItemByItemID(String sid, Object data) {
        final int pageIndex = searchItemIDPageIndexTraverse(sid);
        Log.d("DebugReplaceItem","searchReplaceItemByItemID sid:"+sid+",pageIndex:"+pageIndex);
        if (pageIndex > -1) {
            PageItem pi = getPageItem(pageIndex);
            if (pi != null) {
                RenderNodeUtils.replaceData((HippyArray) pi.pageData.rawData,data,sid,"_id");
//                RenderNodeUtils.replaceChildData((HippyArray) pi.pageData.rawData, pageIndex, data, childIndex);
            }

            final View view = findPageView(pageIndex);
            if (view != null) {
                mContentFactory.searchReplaceItemTraverse(pageIndex,view,getPageItem(pageIndex), sid,data);
            }
        }else{
            Log.e("DebugReplaceItem","searchReplaceItemByItemID fail on pageIndex:"+pageIndex+",sid:"+sid);
        }
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void searchUpdateItemDataByItemID(String s, Object o, boolean b) {
        Log.i("DebugReplaceItem","RV searchUpdateItemDataByItemID sid:"+s+",data:"+o+",traverse:"+b);
        //final int pageIndex = searchItemIDPageIndexTraverse(s);
        searchReplaceItemByItemID(s,o);
    }

    private final class Holder extends RecyclerView.ViewHolder {

        public Holder(@NonNull View itemView) {
            super(itemView);
        }
    }

    private final class PageAdapter extends RecyclerView.Adapter<Holder> {
        //所有数据
        SparseArray<PageItem> mPageList;
        //存在的缓存view数
        ArrayList<View> scrapContentViews;
        HashMap<Integer, View> contentViews;
        //发送事件view
//        View agentView;


        private View obtainPageView(View agentView) {
            View pv;
            if (scrapContentViews.size() > 0) {
                pv = scrapContentViews.remove(0);
                mContentFactory.reuseAfterRecycle(pv);
            } else {
                pv = createNewView(agentView);
            }
            return pv;
        }

        private View createNewView(View agentView) {
            final View v = mContentFactory.createContentView(findContentTemplateNode(), agentView);
            return v;
        }

        private PageAdapter() {
            super();
            scrapContentViews = new ArrayList<>();
            contentViews = new HashMap<>();
            setHasStableIds(true);
        }

        @Override
        public int getItemCount() {
            final int count = mPageList == null ? 0 : mPageList.size();
            if (LogUtils.isDebug()) {
                Log.v(TAG, "Adapter getCount:" + count);
            }
            return count;
        }

        @Override
        public void onViewAttachedToWindow(@NonNull Holder holder) {
            super.onViewAttachedToWindow(holder);
            if (LogUtils.isDebug()) {
                Log.i(TAG, "PageAdapterEvent:onViewAttachedToWindow pos:" + holder.getAdapterPosition());
            }
            mContentFactory.onViewAttachedToWindow(holder.itemView, holder.getAdapterPosition());
        }

        @Override
        public void onViewDetachedFromWindow(@NonNull Holder holder) {
            super.onViewDetachedFromWindow(holder);
            final int position = holder.getAdapterPosition();
            if (LogUtils.isDebug()) {
                Log.i(TAG, "PageAdapterEvent:onViewDetachedFromWindow pos:" + position);
            }
            final PageItem pi = getPageItem(position);
            if (pi != null) {
                //pi.markDataDirty();
                //因为在clearAllHide时，将数据置空，这里将不再需要
            }
            mContentFactory.onViewDetachedFromWindow(holder.itemView, position);
            //mContentFactory.clearPageData(position,holder.itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            if (contentViews == null) {
                return;
            }
            contentViews.put(position, holder.itemView);
            mContentFactory.onBindViewHolder(holder.itemView, position);
            if (mContentFactory.isSingleContent()) {
                if (getCurrentItem() == position) {
                    tryUpdatePageView(position,false,UPDATE_TYPE_ON_BIND);
                }
            }else{
                tryUpdatePageView(position,false,UPDATE_TYPE_ON_BIND);
            }
            if (LogUtils.isDebug()) {
                Log.d(TAG, "PageAdapterEvent:onBindViewHolder !! holder :" + holder.hashCode() + ",position:" + position);
            }
        }

        @Override
        public void onViewRecycled(@NonNull Holder holder) {
            super.onViewRecycled(holder);
            if (contentViews == null) {
                return;
            }
            if(isPreferSaveMemory()) {
                final int position = holder.getAdapterPosition();
                mContentFactory.recycle(position, holder.itemView);
                contentViews.remove(position);
                final PageItem item = getPageItem(position);
                if (item != null) {
                    item.markDataDirty();
                    removeCallbacks(item.postDataTask);
                    item.postDataTask = null;
                }
            }
            if (LogUtils.isDebug()) {
                Log.i(TAG, "PageAdapterEvent:onViewRecycled !! holder :" + holder.hashCode() + ",position:" + holder.getAdapterPosition() + ",getBindingAdapterPosition:" + holder.getAdapterPosition());
            }
        }

        @Override
        public long getItemId(int position) {
            return mPageList.size() > position ? mPageList.get(position).hashCode() : NO_ID;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = obtainPageView(tabsView);
            Holder holder = new Holder(view);

            view.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
            return holder;
        }

        public void setup(int count, HippyArray data) {
            if (mPageList != null) {
                mPageList.clear();
            }
            mPageList = new SparseArray<>();
            for (int i = 0; i < count; i++) {
                PageItem pi = new PageItem(i);
                pi.tabsParam = tabsParam;
                mPageList.put(i, pi);
            }
        }

        public void clear() {
            if (this.mPageList != null) {
                this.mPageList.clear();
            }
        }

        public View findPageView(int position) {
            return contentViews != null ? contentViews.get(position) : null;
        }

        public void destroy() {
            if (contentViews != null) {
                for (int i = 0; i < contentViews.size(); i++) {
                    final View pv = contentViews.get(i);
                    if (pv != null) {
                        mContentFactory.destroy(pv);
                    }
                }
                contentViews = null;
            }
        }
    }


    final static class PageData {
        Object rawData;
        HippyMap params;

        public PageData(Object rawData) {
            this.rawData = rawData;
        }
    }

    public interface PageDataLoader {
        void loadPage(int pos, Callback<Object> callback, boolean useDiff);
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
//        Log.e(FOCUS_TAG,"### dispatchKeyEventPreIme event on View event getAction:"+event.getAction());
        if(isSlidingEnable && getOrientation() == ViewPager2.ORIENTATION_VERTICAL && event.getAction() == KeyEvent.ACTION_DOWN){//这里只服务于miobox项目
            final View pageView = findPageView(getCurrentItem());
            final View v = ExtendUtil.findViewByName("QUICKTVUI_WARTERFALL_LIST",pageView);
            final FastListView fv = v instanceof FastListView ? (FastListView) v : null;
//            Log.i(FOCUS_TAG,"====1 focusPosition:"+fv.getFocusChildPosition()+",childCount:"+getTotalPage()+",isLeft:"+isLeft);
            int mineTabFirstFocus = tabsParam != null ? tabsParam.mineTabFirstFocusIndex : -1;
            //fixme 这里为miobox写死逻辑，不可更新
            int firstPos = getCurrentItem() == (getTotalPage() -1 ) ? mineTabFirstFocus : 0;
            if(fv != null && fv.getFocusChildPosition() == firstPos){
                Log.i(FOCUS_TAG,"==== focusPosition:"+fv.getFocusChildPosition());
                if (!isTranslationLeftModel() && hasFocus()) {
                    Log.i(FOCUS_TAG, "====KEYCODE_DPAD_RIGHT keyEvent:"+event.getKeyCode()+",pageView:"+ ExtendUtil.debugView(pageView)+",listView:"+ExtendUtil.debugView(v));
                    if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if(LogUtils.isDebug()){
                            Log.e(FOCUS_TAG, "向左移动 return true");
                        }
                        requestTranslationLeft();
                        keyIntercept = true;
                        return true;
                    }
                }
                if(event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT && isTranslationLeftModel()){
                    Log.i(FOCUS_TAG, "====KEYCODE_DPAD_LEFT keyEvent:"+event.getKeyCode()+",pageView:"+ ExtendUtil.debugView(pageView)+",listView:"+ExtendUtil.debugView(v));
                    if (LogUtils.isDebug()) {
                        Log.e(FOCUS_TAG, "向右移动");
                    }
                    requestTranslationRight();
                    return true;
                }
            }

        }
        return super.dispatchKeyEventPreIme(event);
    }
//
//    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
//        EsProxy.get()
        //hardcode
        if(keyIntercept && event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT){
            Log.v(FOCUS_TAG, "### dispatchKeyEvent event on View keyIntercept:"+ true);
            keyIntercept = false;
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public TabsView getTabsView() {
        return tabsView;
    }

    public void setTabsView(TabsView tabsView) {
        this.tabsView = tabsView;
    }

    public View getSuspensionView() {
        return suspensionView;
    }

    public void setSuspensionView(View suspensionView) {
        this.suspensionView = suspensionView;
    }

    public boolean isSuspension() {
        return isSuspension;
    }

    public void setSuspension(boolean suspension) {
        isSuspension = suspension;
    }

    public boolean isUseSuspensionBg() {
        return useSuspensionBg;
    }

    public void setUseSuspensionBg(boolean useSuspensionBg) {
        this.useSuspensionBg = useSuspensionBg;
    }

    public boolean isHideOnSingleTab() {
        return isHideOnSingleTab;
    }

    public void setHideOnSingleTab(boolean hideOnSingleTab) {
        isHideOnSingleTab = hideOnSingleTab;
    }

    public void setOnPageEventListener(OnPageEventListener onPageEventListener) {
        this.mOnPageEventListener = onPageEventListener;
    }

    public List<TabHelper> getTabHelpers() {
        return tabHelpers;
    }

    public abstract static class OnPageEventListener {
        public void onPageChanged(int page) {

        }
    }

    public boolean isSlidingMode() {
        return isSlidingEnable && !TextUtils.isEmpty(currentSlidingMode) && currentSlidingMode.equals("translation");
    }

    private void clearSlidingMode() {
        currentSlidingMode = "";
    }

    private void setSlidingMode() {
        currentSlidingMode = slidingValue;
    }

    public ObjectAnimator drawerAnimation;

    public void exeDrawerAnimation(boolean open,final int pageIndex){
        if (drawerAnimation != null) {
            drawerAnimation.cancel();
            drawerAnimation = null;
        }
        int drawerPosition = getWidth();
        if(open){
            drawerAnimation = ObjectAnimator.ofFloat(this, "translationX", drawerPosition, 0);
        }else{
            drawerAnimation = ObjectAnimator.ofFloat(this, "translationX", 0f, drawerPosition);
        }
        drawerAnimation.setDuration(tabsParam != null ? tabsParam.switchDuration : this.switchDuration);
        drawerAnimation.setInterpolator(TimeInterpolatorUtils.getTimeInterpolatorByType(this.interType));
        drawerAnimation.setRepeatMode(ValueAnimator.RESTART);
        drawerAnimation.setRepeatCount(0);
        drawerAnimation.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationStart(Animator animator) {
//                HippyViewEvent event = new HippyViewEvent("onDrawerOpenEnd");
//                HippyMap map = new HippyMap();
//                map.pushInt("position",pageIndex);
//                event.send(tabsView,map);
            }

            @Override
            public void onAnimationEnd(Animator animator) {
//                int translation = 0;
//                if (Math.max((leftTranslationValue), 0) != 0) {
//                    translation = firstTranslationValue - leftTranslationValue;
//                }
//                if (isTranslationLeftModel()) {
//                    RecyclerViewPager.this.layout(leftTranslationValue + translation, 0, mScreenWidth + leftTranslationValue + translation, mScreenHeight);
//                }
//                    HippyViewEvent event = new HippyViewEvent("onDrawerOpenEnd");
                    HippyMap map = new HippyMap();
                    map.pushInt("position",pageIndex);
//                    event.send(tabsView,map);
                TabUtils.sendTabsEvent(tabsView,"onDrawerOpenEnd",map);
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        drawerAnimation.start();
    }

    public void translationLeft() {
        if (isSlidingEnable) {
            if (!isTranslationLeftModel()) {
                if (drawerAnimation != null) {
                    drawerAnimation.cancel();
                }
                isLeft = true;
                firstLayout = false;
                ObjectAnimator objectAnimator;
                objectAnimator = ObjectAnimator.ofFloat(this, "translationX", 0f, -(float) leftTranslationValue);
                objectAnimator.setDuration(this.anDuration);
                objectAnimator.setInterpolator(TimeInterpolatorUtils.getTimeInterpolatorByType(this.interType));
                objectAnimator.setRepeatMode(ValueAnimator.RESTART);
                objectAnimator.setRepeatCount(0);
                objectAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        int translation = 0;
                        if (Math.max((leftTranslationValue), 0) != 0) {
                            translation = firstTranslationValue - leftTranslationValue;
                        }
                        if (isTranslationLeftModel()) {
                            RecyclerViewPager.this.layout(leftTranslationValue + translation, 0, mScreenWidth + leftTranslationValue + translation, mScreenHeight);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                objectAnimator.start();
                clearSlidingMode();
            }
        }
    }

    public void requestTranslationLeft(){
        HippyMap map = new HippyMap();
        map.pushInt("position",getCurrentItem());
        TabUtils.sendTabsEvent(tabsView,"onDrawerMoveToLeft",map);
        this.translationLeft();
    }

    public void requestTranslationRight(){
        HippyMap map = new HippyMap();
        map.pushInt("position",getCurrentItem());
        TabUtils.sendTabsEvent(tabsView,"onDrawerMoveToRight",map);
        this.translationRight();
    }

    public void translationRight() {
        if (isSlidingEnable) {
            if (isTranslationLeftModel()) {
                isLeft = false;
                firstLayout = false;
                ObjectAnimator objectAnimator;
                objectAnimator = ObjectAnimator.ofFloat(this, "translationX", -(float) leftTranslationValue, 0f);
                objectAnimator.setDuration(this.anDuration);
                objectAnimator.setInterpolator(TimeInterpolatorUtils.getTimeInterpolatorByType(this.interType));
                objectAnimator.setRepeatMode(ValueAnimator.RESTART);
                objectAnimator.setRepeatCount(0);
                objectAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        int translation = 0;
                        if (Math.max((leftTranslationValue), 0) != 0) {
                            translation = firstTranslationValue - leftTranslationValue;
                        }
                        if (!isTranslationLeftModel()) {
                            RecyclerViewPager.this.layout(leftTranslationValue + translation, 0, mScreenWidth + leftTranslationValue + translation, mScreenHeight);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
                objectAnimator.start();
                setSlidingMode();
            }
        }
    }

    public boolean isTranslationLeftModel() {
        return isLeft;
    }

    private int getScreenWidth() {
        WindowManager mWm = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        mWm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public int getScreenHeight() {
        WindowManager mWm = (WindowManager) this.getContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        // 获取屏幕信息
        mWm.getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }
}
