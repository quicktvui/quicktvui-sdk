package com.quicktvui.sdk.core.jsview;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import com.extscreen.runtime.api.ability.slotview.RecyclerViewEventHandler;
import com.sunrain.toolkit.utils.log.L;
import com.sunrain.toolkit.utils.observer.BaseObserver;
import com.tencent.mtt.hippy.utils.PixelUtil;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Observable;
import java.util.Set;

import com.quicktvui.sdk.core.internal.Constants;
import com.quicktvui.sdk.core.internal.IEsAppLoadHandler;
import com.quicktvui.sdk.core.jsview.slot.SlotView;
import com.quicktvui.sdk.core.utils.SlotUtils;
import com.quicktvui.sdk.base.args.EsMap;

/**
 * <br>
 *
 * <br>
 */
public class JsSlotView extends FrameLayout {

    private String mName;
    private String mSid;
    private EsMap mLoadData;
    private volatile int mSlotViewId;

    private FrameLayout mHolderViewContainer;

    private boolean mSlotViewNeedFocus = false;

    private final Map<String, Object> mCacheEventMap = new HashMap<>(5);

    private SoftReference<RecyclerViewEventHandler> mJsRecyclerViewHandler;

    private boolean mFistAttachToWindow = true;

    private SlotView currentSlotView;

    public JsSlotView(Context context, String name, EsMap params) {
        super(context);
        init(context);
        mName = name;
        mLoadData = new EsMap();
        mLoadData.pushString("name", mName);
        mLoadData.pushString("key", mSid);
        mLoadData.pushObject("params", params);
    }

    public void setNeedFocus(boolean needFocus) {
        mSlotViewNeedFocus = needFocus;
    }

    public String getName() {
        return mName;
    }

    // --------------------------------------------------------

    private void init(Context context) {
        mSid = String.valueOf(hashCode());
        setFocusable(false);
        setClipChildren(false);
        setClipToPadding(false);
    }

    void addSlotView(SlotView view) {
        currentSlotView = view;
        L.logDF("add SlotView " + mName + ",id: " + view.getId());
        addView(view, new FrameLayout.LayoutParams(-1, -1));

//        view.addOnAttachStateChangeListener(this);

        view.observe(new BaseObserver<Boolean>() {
            @Override
            public void onUpdate(Observable o, Boolean suspend) {
                if (!suspend) {
                    L.logD("do unsuspend");
                    clearHolderView(500);
                }
            }
        });
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        // 将SlotView放在占位View的底下
        if (child instanceof SlotView) {
            index = 0;
        }
        super.addView(child, index, params);
        if (child instanceof SlotView) {
            mSlotViewId = child.getId();
            fireCacheEvents();
            updateSlotFocus(child);
        }
    }

    //region 1. 占位View

    public void showDefaultPlaceHolderView() {
        Context context = getContext();
        mHolderViewContainer = new FrameLayout(context);
        mHolderViewContainer.setFocusable(true);
        mHolderViewContainer.setBackgroundDrawable(generatePlaceHolderDrawable());
        addView(mHolderViewContainer, new LayoutParams(MATCH_PARENT, MATCH_PARENT));
    }

    public void setPlaceHolderView(int viewResId) {
        View view = LayoutInflater.from(getContext()).inflate(viewResId, this, false);
        setPlaceHolderView(view);
    }

    public void setPlaceHolderView(View view) {
        replaceHolderView(view);
    }

    private Drawable generatePlaceHolderDrawable() {
        float cornerRadius = PixelUtil.dp2px(20);
        float[] outerRadii = {cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius, cornerRadius};
        ShapeDrawable drawable = new ShapeDrawable(new RoundRectShape(outerRadii, null, null));
        drawable.getPaint().setColor(Color.parseColor("#16264D"));
        return drawable;
    }

    private void replaceHolderView(View view) {
        if (mHolderViewContainer == null) return;
        mHolderViewContainer.setBackground(null);
        mHolderViewContainer.addView(view);
    }

    private void clearHolderView(int delay) {
        // 说明可能使用了宿主的PlaceHolderView
        // 约定宿主的placeholder放在JsSlotView的parent的背景上，JsSlotView来清除
        if (mHolderViewContainer == null) {
            findAndHidePlaceHolderView();
            return;
        }
        Runnable task = () -> {
            removeView(mHolderViewContainer);
            mHolderViewContainer = null;
        };
        if (delay == 0) {
            task.run();
        } else {
            postDelayed(task, delay);
        }
    }

    private void findAndHidePlaceHolderView() {
        ViewParent parent = getParent();
        int level = 3;
        do {
            if (parent instanceof ViewGroup) {
                Object tag = ((ViewGroup) parent).getTag();
                if (Objects.equals(tag, mName)) {
                    ((ViewGroup) parent).setBackground(null);
                    break;
                }
                parent = getParent();
                continue;
            }
            break;
        } while (parent != null && level-- > 0);
    }

    //endregion

    //region 2. 焦点处理

    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
        L.logDF("setFocusable " + focusable);
    }

    /**
     * 更新焦点逻辑
     **/
    public void updateSlotFocus(View child) {
        setFocusable(!mSlotViewNeedFocus);
        if (!mSlotViewNeedFocus) {
            setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
            L.logDF("block child focus");
        } else if (hasFocus()) {
            child.requestFocus();
            L.logDF("child request focus");
        }
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        sendFocusChangeEvent(gainFocus, direction);
    }

    public void sendFocusChangeEvent(boolean gainFocus) {
        sendFocusChangeEvent(gainFocus, -1);
    }

    public void sendFocusChangeEvent(boolean gainFocus, int direction) {
        sendFocusChangeEvent(gainFocus, direction, null);
    }

    public void sendFocusChangeEvent(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {

        EsMap data = new EsMap();
        data.pushBoolean("gainFocus", gainFocus);
        data.pushInt("direction", direction);

        if (previouslyFocusedRect != null) {
            EsMap rect = new EsMap();
            rect.pushInt("left", previouslyFocusedRect.left);
            rect.pushInt("top", previouslyFocusedRect.top);
            rect.pushInt("right", previouslyFocusedRect.right);
            rect.pushInt("bottom", previouslyFocusedRect.bottom);
            data.pushObject("previouslyFocusedRect", rect);
        }

//        sendEvent("onFocusChanged", data);
        JsSlotViewManager.get().sendEvent(this, "onFocusChanged", data);
    }

    //endregion

    //region 3. 发送事件

    void sendCustomEvent(final String eventName, final Object data) {
        EsMap map = new EsMap();
        map.pushString("eventName", eventName);
        map.pushObject("eventData", data);
        sendEvent(Constants.GLOBAL_EVENT.EVT_ON_SLOT_CUSTOM, map);
    }

    void sendEvent(final String eventName, final Object data) {
        if (mSlotViewId == 0) {
            cacheEvent(eventName, data);
            return;
        }
        IEsAppLoadHandler loadHandler = SlotUtils.getAppLoaderHandler(getContext());
        if (loadHandler == null) return;
        Object params = data;
        if (data instanceof Bundle) {
            EsMap map = new EsMap();
            map.pushBundle((Bundle) data);
            params = map;
        }
        mLoadData.pushObject("params", params);
        loadHandler.sendEvent(eventName, mLoadData);
    }

    /**
     * SlotView没加载之前缓存事件
     **/
    private synchronized void cacheEvent(String eventName, Object data) {
        mCacheEventMap.put(eventName, data);
    }

    /**
     * 执行view没加载之前缓存的事件
     **/
    private synchronized void fireCacheEvents() {
        Map<String, Object> events = new HashMap<>(mCacheEventMap);
        Set<String> keys = events.keySet();
        for (String key : keys) {
            sendEvent(key, events.get(key));
        }
    }

    //endregion

    //region 4. attachedToWindow/detachedFromWindow

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(mFistAttachToWindow) {
            mFistAttachToWindow = false;
            return;
        }
        findAndHidePlaceHolderView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    //endregion

    //region 5. RecyclerView接口封装

    public RecyclerViewEventHandler getRecyclerViewEventHandler() {
        RecyclerViewEventHandler handler;
        if (mJsRecyclerViewHandler == null || (handler = mJsRecyclerViewHandler.get()) == null) {
            handler = new RecyclerViewHandlerImpl(this);
            mJsRecyclerViewHandler = new SoftReference<>(handler);
        }
        return handler;
    }

    //endregion

    //region 设置激活鼠标事件
    public void setEnableMouse() {
        if (currentSlotView != null) {
            currentSlotView.setMouseEnable();
            L.logDF("setEnableMouse " + currentSlotView.getId());
        }
    }
    //endregion

    //region 数据获取

    public String getSid() {
        return mSid;
    }

    public EsMap getLoadData() {
        return mLoadData;
    }

    //endregion

    @Override
    public String toString() {
        return "JsView{" +
                "name='" + mName + '\'' +
                ", sid='" + mSid + '\'' +
                '}';
    }
}
