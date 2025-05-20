package com.quicktvui.sdk.core.card;

import android.app.Activity;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LifecycleOwner;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.quicktvui.base.ui.TVFocusAnimHelper;
import com.sunrain.toolkit.utils.log.L;
import com.quicktvui.hippyext.RenderUtil;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.HippyRootView;

import com.quicktvui.sdk.core.EsData;
import com.quicktvui.sdk.core.EsManager;
import com.quicktvui.sdk.core.R;
import com.quicktvui.sdk.core.engine.ESEvents;
import com.quicktvui.sdk.core.internal.IEsAppLoadCallback;
import com.quicktvui.sdk.core.internal.IEsAppLoadHandler;
import com.quicktvui.sdk.base.EsEventPacket;
import com.quicktvui.sdk.base.EsException;
import com.quicktvui.sdk.base.EsNativeEventCallback;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.IEsComponentView;
import com.quicktvui.sdk.base.core.EsProxy;

/**
 * ----------卡片----------
 * onAttachedToWindow
 * onWindowVisibilityChanged:0
 * onVisibilityChanged:0
 * onWindowFocusChanged:true
 * --------------------
 * onWindowFocusChanged:false
 * onWindowVisibilityChanged:8
 * onVisibilityChanged:4
 * onDetachedFromWindow
 */
public class ESCardView extends FrameLayout implements IEsComponentView {
    private final static String TAG = "ESCardView";
    private View loadFailView, loadingView;
    private int contentPaddingLeft, contentPaddingRight, contentPaddingTop, contentPaddingBottom;
    private int loadingStatus; //0:未加载 1:已加载 2:加载中 3:加载失败
    private Context context;
    private String cardId;
    private ESCardCallBack esCardCallBack;
    private IEsAppLoadHandler iEsAppLoadHandler;
    private ESCardBean esCardBean;
    private EsNativeEventCallback eventCallback; //卡片接收vue事件回调
    protected int JSEventViewID;
    protected boolean defaultFocusable; //默认焦点状态
    private boolean usePlaceHolder; //是否使用默认背景
    private boolean useDefaultUI; //是否使用默认状态提示view
    private boolean useCache; //是否默认启用缓存
    private boolean autoRecycle; //是否启用销毁时默认存入缓存
    private boolean useESEvent = false; //是否启用接收卡片事件，默认关闭
    private boolean showPlackHolder = true;
    private float focusScale = 1.1f;
    private int placeHolderRadius = 8;
    private int left, top;
    private Paint mPaint;
    private RectF rectF;

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        TVFocusAnimHelper.handleOnFocusChange(this, gainFocus, focusScale, focusScale, TVFocusAnimHelper.DEFAULT_DURATION);
    }
    

    public ESCardView(@NonNull Context context) {
        this(context, null);
    }

    public ESCardView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ESCardView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ESCardView);
        usePlaceHolder = a.getBoolean(R.styleable.ESCardView_usePlaceHolder, false);
        useDefaultUI = a.getBoolean(R.styleable.ESCardView_useDefaultUI, true);
        useCache = a.getBoolean(R.styleable.ESCardView_useCache, true);
        cardId = a.getString(R.styleable.ESCardView_cardId);
        int contentPadding = a.getDimensionPixelSize(R.styleable.ESCardView_contentPadding, 0);
        contentPaddingLeft = a.getDimensionPixelSize(R.styleable.ESCardView_contentPaddingLeft, contentPadding);
        contentPaddingRight = a.getDimensionPixelSize(R.styleable.ESCardView_contentPaddingRight, contentPadding);
        contentPaddingTop = a.getDimensionPixelSize(R.styleable.ESCardView_contentPaddingTop, contentPadding);
        contentPaddingBottom = a.getDimensionPixelSize(R.styleable.ESCardView_contentPaddingBottom, contentPadding);
        a.recycle();
        init(context);
        if (usePlaceHolder) {
            setBackgroundResource(R.drawable.eskit_bg_webframe_default);
        }
        if (cardId != null) {
            load(cardId, useCache, true, new ESCardCallBackImpl());
        }
    }

    protected void init(Context context) {
        this.context = context;
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(25);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        rectF = new RectF();
    }

    public void setPlaceHolder(int left, int top, int right, int bottom) {
        this.left = left;
        this.top = top;
        rectF.set(left, top, right, bottom);
    }

    public void setPlaceHolderRadius(int radius) {
        this.placeHolderRadius = radius;
    }

    public void setFocusScale(float focusScale) {
        this.focusScale = focusScale;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        rectF.set(left, top, w - left, h - top);
        if (getChildCount() > 0 && getChildAt(0) != null) {
            RenderUtil.reLayoutView(getChildAt(0), contentPaddingLeft, contentPaddingTop, w, h);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showPlackHolder) {
            canvas.drawRoundRect(rectF, placeHolderRadius, placeHolderRadius, mPaint);
        }
    }

    private void registerWebFrameCallBack(ESCardCallBack esCardCallBack) {
        this.esCardCallBack = esCardCallBack;
    }

    //渲染view
    public void load(String cardId) {
        this.load(cardId, useCache, new ESCardCallBackImpl());
    }

    public void load(String cardId, boolean useCache) {
        this.load(cardId, useCache, new ESCardCallBackImpl());
    }

    public void load(String cardId, ESCardCallBack esCardCallBack) {
        this.load(cardId, useCache, esCardCallBack);
    }

    public void load(String cardId, boolean useCache, ESCardCallBack esCardCallBack) {
        this.load(cardId, useCache, false, esCardCallBack);
    }

    /**
     * 加载
     *
     * @param cardId         需要加载代码包的包名
     * @param useCache       是否使用缓存
     * @param withoutLoading 是否跳过loading（不对外开放）
     * @param esCardCallBack 回调接口
     */
    public void load(String cardId, boolean useCache, boolean withoutLoading, ESCardCallBack esCardCallBack) {
        if (!prepareLoading() || TextUtils.isEmpty(cardId)) {
            return;
        }
        if (useCache) {
            ESCardCache cardCache = ESCardManager.getInstance().getCardCache(context);
            if (cardCache != null && cardCache.containsKey(cardId)) {
                if (cardCache.get(cardId).getCacheView() != null) {
                    if (L.DEBUG) {
                        Log.v(TAG, "使用了缓存 cardId:" + cardId);
                    }
                    registerLifecycle((Activity) context, esCardCallBack);
                    //使用缓存
                    loadCache(cardCache.get(cardId), esCardCallBack);
                    return;
                }
            }
        }
        registerLifecycle((Activity) context, esCardCallBack);
        loadingStatus = 2;
        if (!withoutLoading) {
            loadLoading();
        }
        this.cardId = cardId;
        registerWebFrameCallBack(esCardCallBack);
        //
        resetFocusable();
        //
        EsData data = new EsData();
        data.setAppPackage(cardId);
        data.setCard(true);
        EsManager.get().load((Activity) context, data, new IEsAppLoadCallback() {
            @Override
            public void onStartLoad(IEsAppLoadHandler handler) {
                if (context instanceof Activity) {
                    if (((Activity) context).isFinishing() || ((Activity) context).isDestroyed()) {
                        return;
                    }
                }
                iEsAppLoadHandler = handler;
                if (L.DEBUG) {
                    Log.v(TAG, "onStartLoad----cardId:" + cardId);
                }
                if (esCardCallBack != null) {
                    esCardCallBack.onStartLoad(cardId, handler);
                }
            }

            @Override
            public void onViewLoaded(HippyRootView view) {
                if (context instanceof Activity) {
                    if (((Activity) context).isFinishing() || ((Activity) context).isDestroyed()) {
                        return;
                    }
                }
                if (L.DEBUG) {
                    Log.v(TAG, "onViewLoaded----cardId:" + cardId);
                }
                loadingStatus = 1;
//                removeLoading();
//                view.setBackgroundResource(R.drawable.eskit_bg_webframe_default);
                addHippyRootView(view);
                createWebFrame(cardId, view, iEsAppLoadHandler);
                if (esCardCallBack != null) {
                    esCardCallBack.onLoadSuccess(esCardBean);
                }
                if (useESEvent) {
                    registerESEvent(view.getEngineContext());
                }
            }

            @Override
            public void onLoadError(EsException e) {
                if (context instanceof Activity) {
                    if (((Activity) context).isFinishing() || ((Activity) context).isDestroyed()) {
                        return;
                    }
                }
                if (L.DEBUG) {
                    Log.v(TAG, "onLoadError----cardId:" + cardId + "----e:" + e.getMessage());
                }
                loadingStatus = 3;
                removeLoading();
                loadFail();
                createWebFrame(cardId, null, iEsAppLoadHandler);
                if (esCardCallBack != null) {
                    esCardCallBack.onLoadFailed(esCardBean);
                }
            }

            @Override
            public void onEsAppEvent(EsEventPacket packet) {
                if (L.DEBUG) {
                    Log.v(TAG, "onEsAppEvent----cardId:" + cardId);
                }
            }

            @Override
            public void requestFinish() {
                if (L.DEBUG) {
                    Log.v(TAG, "requestFinish----cardId:" + cardId);
                }
            }
        });
    }

    private void loadCache(ESCardBean esCardBean, ESCardCallBack esCardCallBack) {
        View view = esCardBean.getCacheView();
        if (view == null) {
            return;
        }
        if (!prepareLoading() || TextUtils.isEmpty(esCardBean.getCardId())) {
            return;
        }
        if (view.getParent() != null) {
            ((FrameLayout) view.getParent()).removeView(view);
        }
        this.esCardBean = esCardBean;
        this.cardId = esCardBean.getCardId();
        this.iEsAppLoadHandler = esCardBean.getiEsAppLoadHandler();
        registerWebFrameCallBack(esCardCallBack);
        loadingStatus = 1;
        //
        resetFocusable();
        //
        addHippyRootView(view);
        if (esCardCallBack != null) {
            esCardCallBack.onLoadSuccess(esCardBean);
        }
        if (useESEvent) {
            registerESEvent(((HippyRootView) view).getEngineContext());
        }
    }

    //加载中view
    private void loadLoading() {
//        if (useDefaultUI) {
//            if (loadingView == null) {
//                loadingView = new FrameLayout(context);
//                TextView textView = new TextView(context);
//                textView.setText("加载中...");
//                textView.setTextColor(Color.BLACK);
//                ((FrameLayout) loadingView).addView(textView, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
//            }
//            addView(loadingView, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            EsProxy.get().updateLayout(ESCardView.this);
//            RenderUtil.reLayoutView(loadingView, contentPaddingLeft, contentPaddingTop, ESCardView.this.getWidth(), ESCardView.this.getHeight());
//        }
    }

    //加载失败view
    private void loadFail() {
        if (useDefaultUI) {
            if (loadFailView == null) {
                loadFailView = new FrameLayout(context);
                TextView textView = new TextView(context);
                textView.setText("加载失败！");
                textView.setTextColor(Color.BLACK);
                ((FrameLayout) loadFailView).addView(textView, new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER));
            }
            addView(loadFailView, new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            EsProxy.get().updateLayout(ESCardView.this);
            RenderUtil.reLayoutView(loadFailView, contentPaddingLeft, contentPaddingTop, ESCardView.this.getWidth(), ESCardView.this.getHeight());
            if (hasFocus()) {
                loadFailView.requestFocus();
            }
            if (defaultFocusable) {
                setFocusable(false);
            }
        }
    }

    private boolean prepareLoading() {
        if (!useDefaultUI) {
            return true;
        }
        if (loadingStatus == 2) {
            return false;
        }
        if (loadingStatus == 1) {
            removeHippyRootView();
        } else if (loadingStatus == 3) {
            removeViewInLayout(loadFailView);
        }
        return true;
    }

    public void removeLoading() {
//        if (useDefaultUI && loadingView != null) {
//            removeViewInLayout(loadingView);
//        }
    }

    public void showPlaceHolder(boolean tag) {
        this.showPlackHolder = tag;
        if (!this.showPlackHolder) {
            invalidate();
        }
    }

    private void addHippyRootView(View view) {
        addView(view);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(view.getLayoutParams());
        lp.setMargins(contentPaddingLeft, contentPaddingTop, contentPaddingRight, contentPaddingBottom);
        view.setLayoutParams(lp);
        EsProxy.get().updateLayout(ESCardView.this);
        RenderUtil.reLayoutView(view, contentPaddingLeft, contentPaddingTop, ESCardView.this.getWidth(), ESCardView.this.getHeight());
    }

    private ESCardBean createWebFrame(String cardId, View view, IEsAppLoadHandler iEsAppLoadHandler) {
        esCardBean = new ESCardBean();
        esCardBean.setCardId(cardId);
        if (view != null) {
            esCardBean.setCacheView(view);
        }
        esCardBean.setiEsAppLoadHandler(iEsAppLoadHandler);
        return esCardBean;
    }

    public void makeFocusToCard() {
        if (getChildCount() > 0) {
            if (getChildAt(0) instanceof HippyRootView) {
                if (((ViewGroup) getChildAt(0)).hasFocus()) {
                    return;
                }
                boolean isFocusable = ((HippyRootView) getChildAt(0)).hasFocusable();
                if (hasFocus()) {
                    if (isFocusable) {
                        //说明布局中有能直接获取焦点的view
                        getChildAt(0).requestFocus();
                        showPlaceHolder(false);
                    } else {
                        //说明布局中没有可获取焦点的view，则暂时接管焦点

                    }
                } else {
                    showPlaceHolder(false);
                }
                if (isFocusable && defaultFocusable) {
                    setFocusable(false);
                }
            }
        }
    }

    //TODO:
    public void onResume() {
        //卡片恢复运行
    }

    public void registerESEvent(HippyEngineContext engineContext) {
        if (engineContext != null) {
            if (eventCallback == null) {
                eventCallback = new CardEsNativeEventCallBack(this);
            }
            ESEvents.on(engineContext.getEngineId(), eventCallback);
        }
    }

    private static class CardEsNativeEventCallBack implements EsNativeEventCallback {
        private ESCardView esCardView;

        public CardEsNativeEventCallBack(ESCardView esCardView) {
            this.esCardView = esCardView;
        }

        @Override
        public void onEvent(EsEventPacket event) {
            if (event.getEventName().equals("cardEvent")) {
                String eventName = event.getEventData().getString("eventName");
                String cardId = event.getEventData().getString("cardId");
                if (cardId != null && cardId.equals(esCardView.getESCardBean().getCardId())) {
                    if (eventName != null && eventName.equals("requestFocus")) {
                        //卡片内部请求焦点，暂时移除外部焦点
                        if (esCardView.getChildCount() > 0) {
                            if (esCardView.hasFocus()) {
                                esCardView.getChildAt(0).requestFocus();
                            }
                            if (esCardView.defaultFocusable) {
                                esCardView.setFocusable(false);
                            }
                        }
                    }
                }

            }
        }
    }

    //重新加载
    public void reload() {
        if (cardId != null) {
            resetFocusable();
            if (hasFocus()) {
                requestFocus();
            }
            if (loadingStatus != 2) {
                if (esCardCallBack != null) {
                    load(cardId, false, esCardCallBack);
                } else {
                    load(cardId, false);
                }
            }
        }
    }

    public void reloadByClick() {
        if (cardId != null) {
            getCardFocus();
            if (esCardCallBack != null) {
                load(cardId, false, esCardCallBack);
            } else {
                load(cardId, false);
            }
        }
    }

    //恢复焦点状态
    public void resetFocusable() {
        setFocusable(defaultFocusable);
    }

    //焦点在card上时并且获取焦点
    public void getCardFocus() {
        resetFocusable();
        if (defaultFocusable) {
            requestFocus();
        }
    }

    //向vue端发送自定义消息
    public void sendEvent2Vue(String eventName, EsMap esMap) {
        if (esCardCallBack != null) {
            esCardCallBack.sendEvent2Vue(eventName, esMap);
        } else {
            if (iEsAppLoadHandler != null) {
                iEsAppLoadHandler.sendEvent(eventName, esMap);
            }
        }
    }

    //设置加载中样式
    public void setLoadingView(View view) {
        if (view != null) {
            this.loadingView = view;
        }
    }

    //设置加载失败样式
    public void setLoadFailView(View view) {
        if (view != null) {
            this.loadFailView = view;
        }
    }

    private void removeHippyRootView() {
        if (getChildCount() > 0) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                if (getChildAt(i) instanceof HippyRootView) {
                    if (L.DEBUG) {
                        Log.v(TAG, "移除上个卡片 card.childAt" + i);
                    }
                    removeView(getChildAt(i));
                    EsProxy.get().updateLayout(ESCardView.this);
                    return;
                }
            }
        }
    }

    private void registerLifecycle(Activity activity, LifecycleObserver lifecycleObserver) {
        if (activity instanceof LifecycleOwner && lifecycleObserver != null) {
            ((LifecycleOwner) activity).getLifecycle().addObserver(lifecycleObserver);
        }
    }

    //---------------------------------
    public void setContentPadding(int left, int top, int right, int bottom) {
        this.contentPaddingLeft = left;
        this.contentPaddingRight = right;
        this.contentPaddingTop = top;
        this.contentPaddingBottom = bottom;
    }

    public void setUsePlaceHolder(boolean usePlaceHolder) {
        this.usePlaceHolder = usePlaceHolder;
        if (usePlaceHolder) {
            setBackgroundResource(R.drawable.eskit_bg_webframe_default);
        }
    }

    public void setUseDefaultUI(boolean useDefaultUI) {
        this.useDefaultUI = useDefaultUI;
    }

    public int getLoadingStatus() {
        return loadingStatus;
    }

    public ESCardBean getESCardBean() {
        return esCardBean;
    }

    public boolean isAutoRecycle() {
        return autoRecycle;
    }

    public void setAutoRecycle(boolean autoRecycle) {
        this.autoRecycle = autoRecycle;
    }

    public void setUseESEvent(boolean useESEvent) {
        this.useESEvent = useESEvent;
    }

    public void setDefaultFocusable(boolean defaultFocusable) {
        this.defaultFocusable = defaultFocusable;
    }

    public void reset() {
        removeAllViews();
        EsProxy.get().updateLayout(ESCardView.this);
        this.loadingStatus = 0;
        esCardCallBack = null;
        if (iEsAppLoadHandler != null) {
            iEsAppLoadHandler.onDestroy();
            iEsAppLoadHandler = null;
        }
        esCardBean = null;
        setFocusable(defaultFocusable);
    }

    //--------------缓存处理---------------
    //放入缓存池
    public boolean recycle() {
        if (cardId != null && esCardBean != null && loadingStatus == 1) {
            ESCardManager.getInstance().putCache(context, cardId, esCardBean);
            return true;
        }
        return false;
    }

    //
    public boolean removeCache() {
        if (cardId != null) {
            ESCardManager.getInstance().removeCache(context, this.hashCode() + "");
            return true;
        }
        return false;
    }

    public void resizeCacheSize(int newSize) {
        ESCardManager.getInstance().resize(context, newSize);
    }

    public void clearAllCache() {
        ESCardManager.getInstance().clearAllCache(context);
    }

    @Override
    protected void onVisibilityChanged(@NonNull View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        EsMap eventMap = new EsMap();
        eventMap.pushString("eventName", "onVisibilityChanged");
        eventMap.pushString("cardId", cardId);
        eventMap.pushInt("visible", visibility);
        this.sendEvent2Vue(TAG, eventMap);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        EsMap eventMap = new EsMap();
        eventMap.pushString("eventName", "onAttachedToWindow");
        eventMap.pushString("cardId", cardId);
        this.sendEvent2Vue(TAG, eventMap);
        if (this.loadingStatus == 0 && this.cardId != null) {
            this.load(cardId, esCardCallBack != null ? esCardCallBack : null);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        EsMap eventMap = new EsMap();
        eventMap.pushString("eventName", "onDetachedFromWindow");
        eventMap.pushString("cardId", cardId);
        this.sendEvent2Vue(TAG, eventMap);
        if (autoRecycle) {
            recycle();
        } else {
            if (iEsAppLoadHandler != null) {
                if (this.loadingStatus == 2) {
                    iEsAppLoadHandler.onPause();
                }
                removeAllViews();
                EsProxy.get().updateLayout(ESCardView.this);
                this.loadingStatus = 0;
                iEsAppLoadHandler.onDestroy();
                resetFocusable();
            }
        }
    }

    public void destroy() {
        if (esCardCallBack != null) {
            esCardCallBack = null;
        }
        if (iEsAppLoadHandler != null) {
            removeAllViews();
            EsProxy.get().updateLayout(ESCardView.this);
            this.loadingStatus = 0;
            iEsAppLoadHandler.onDestroy();
            iEsAppLoadHandler = null;
        }
    }

    @Override
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
//        Log.i(TAG,"requestFocus direction:"+direction +",loadingStatus:"+loadingStatus+",focusable:"+focusScale);
        if(loadingStatus == 1){
            if(getChildCount() > 0 ){
                getChildAt(0).requestFocus();
            }
//            makeFocusToCard();
            return true;
        }
        return  super.requestFocus(direction, previouslyFocusedRect);
    }
}
