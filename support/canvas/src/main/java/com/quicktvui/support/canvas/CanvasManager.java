/*
 * Copyright (c) 2021, the hapjs-platform Project Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.quicktvui.support.canvas;

import android.graphics.Bitmap;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;

import com.quicktvui.support.canvas.bridge.ApplicationContext;
import com.quicktvui.support.canvas.bridge.IPage;
import com.quicktvui.support.canvas.bridge.RenderEventCallback;
import com.quicktvui.support.canvas.bridge.SimpleActivityStateListener;
import com.quicktvui.support.canvas.canvas2d.CanvasContextRendering2D;
import com.quicktvui.support.canvas.canvas2d.CanvasView2D;
import com.quicktvui.support.canvas.image.CanvasImageHelper;
import com.quicktvui.support.canvas.runtime.HapEngine;
import com.quicktvui.support.canvas.utils.DisplayUtil;
import com.sunrain.toolkit.utils.ThreadUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class CanvasManager extends SimpleActivityStateListener implements ApplicationContext.PageLifecycleCallbacks {

    private static final String TAG = "CanvasManager";

    private static final Object LOCK = new Object();

    private ArrayMap<Integer, ArrayMap<Integer, CanvasView2D>> mCanvasHolders = new ArrayMap<>();
    private ArrayMap<Integer, ArrayMap<Integer, CanvasContext>> mContextArrayMap = new ArrayMap<>();
    private ConcurrentHashMap<Integer, ConcurrentHashMap<Integer, ConcurrentLinkedQueue<CanvasRenderAction>>>
            mCanvasRenderingCommandQueue = new ConcurrentHashMap<>();

    private String mPackageName;

    private boolean mHasRegisterPageLifecycle = false;
    private boolean mHasRegisterActivityLifecycle = false;

    private int pageId = -1;//当前canvas view id
    private int refId = -1;//元素id
    private CanvasView2D currentView;

    private final HashMap<CanvasView2D, Integer> hashMap = new HashMap<>();

    private final HashMap<Integer, Set<Bitmap>> bitmapsHashMap = new HashMap<>();

    private final Set<Bitmap> bitmapList = new LinkedHashSet<>();

    private HashMap<Integer, ArrayList<CanvasRenderAction>> mRenderAction = new HashMap<>();
    // 保存已经绘制过的 bitmap，不立即删除
    private final Set<Bitmap> drawnBitmaps = new LinkedHashSet<>();

    private CanvasManager() {
    }

    public static CanvasManager getInstance() {
        return Holder.instance;
    }

    @Override
    public void onPageStart(@NonNull IPage page) {
    }

    @Override
    public void onPageStop(@NonNull IPage page) {
    }

    @Override
    public void onPageDestroy(@NonNull IPage page) {
        mCanvasHolders.remove(page.getPageId());
        ArrayMap<Integer, CanvasContext> contexts = mContextArrayMap.remove(page.getPageId());
        if (contexts != null && contexts.size() > 0) {
            Collection<CanvasContext> values = contexts.values();
            for (CanvasContext canvasContext : values) {
                canvasContext.destroy();
            }
        }
        mCanvasRenderingCommandQueue.remove(page.getPageId());
    }

    @Override
    public void onActivityDestroy() {
        destroyData();
    }

    public void setPackageName(String packageName) {
        mPackageName = packageName;

        if (!TextUtils.isEmpty(packageName)) {
            if (!mHasRegisterPageLifecycle) {
                ApplicationContext context = HapEngine.getInstance(packageName).getApplicationContext();
                context.registerPageLifecycleCallbacks(this);
                mHasRegisterPageLifecycle = true;
            }
        }
    }

    public void registerActivityLifecycle(RenderEventCallback callback) {
        if (!mHasRegisterActivityLifecycle) {
            callback.addActivityStateListener(this);
            mHasRegisterActivityLifecycle = true;
        }
    }

    public void destroyData() {
        mCanvasRenderingCommandQueue.clear();
        mContextArrayMap.clear();
        mCanvasHolders.clear();
        if (mHasRegisterPageLifecycle) {
            ApplicationContext context = HapEngine.getInstance(mPackageName).getApplicationContext();
            context.unregisterPageLifecycleCallbacks(this);
            mHasRegisterPageLifecycle = false;
        }
        mHasRegisterActivityLifecycle = false;
        CanvasImageHelper.getInstance().clear();
    }

    public boolean addCanvas(CanvasView2D view2D) {
        if (view2D == null) {
            return false;
        }
        int pageId = view2D.getId();
        int ref = pageId;
        if (pageId == -1) {
            return false;
        }
        ArrayMap<Integer, CanvasView2D> map = mCanvasHolders.get(pageId);
        if (map == null) {
            map = new ArrayMap<>();
        }
        map.put(ref, view2D);
        mCanvasHolders.put(pageId, map);
        return true;
    }

    public void removeCanvas(CanvasView2D view) {
        int pageId = getPageId(view);
        if (pageId == Constants.INVALID_PAGE_ID) {
            return;
        }
        int ref = getRef(view);
        ArrayMap<Integer, CanvasView2D> map = mCanvasHolders.get(pageId);
        if (map != null) {
            map.remove(ref);
        }
        destroyData();
        clearAction();
        recycleBitmap();
        if (currentView != null) {
            currentView = null;
        }
    }

    public CanvasView2D getCanvas(int pageId, int ref) {
        if (!mCanvasHolders.containsKey(pageId)) {
            return null;
        }
        ArrayMap<Integer, CanvasView2D> map = mCanvasHolders.get(pageId);
        if (map == null || !map.containsKey(ref)) {
            return null;
        }
        return map.get(ref);
    }

    public CanvasContext getContext(int pageId, int ref) {
        /*if (pageId == Component.INVALID_PAGE_ID) {
            return null;
        }*/
        synchronized (LOCK) {
            return getContextInner(pageId, ref);
        }
    }

    public CanvasContext getContext(int pageId, int ref, String type) {
        /*if (pageId == Component.INVALID_PAGE_ID) {
            return null;
        }*/

        if (CanvasImageHelper.getInstance().isDestroyed()) {
            CanvasImageHelper.getInstance().reset();
        }

        if (is2dType(type)) {
            return getContext2d(pageId, ref);
        } /*else if (isWebGLType(type)) {
            return getWebGLContext(pageId, ref);
        }*/
        return null;
    }

    private CanvasContext getContextInner(int pageId, int ref) {
        ArrayMap<Integer, CanvasContext> map = mContextArrayMap.get(pageId);
        if (map != null) {
            return map.get(ref);
        }
        return null;
    }

    private CanvasContextRendering2D getContext2d(int pageId, int ref) {
        synchronized (LOCK) {
            CanvasContext context = getContextInner(pageId, ref);
            if (context == null) {
                if (currentView != null) {
                    context = new CanvasContextRendering2D(pageId, ref, DisplayUtil.px2dip(currentView.getWidth()));
                } else {
                    Log.d(TAG, "getContext2d: -------->currentView为空！");
                    context = new CanvasContextRendering2D(pageId, ref, 1920);//todo
                }
                setContext(pageId, ref, context);
                //prepareCanvasView(pageId, ref);
                return (CanvasContextRendering2D) context;
            }

            if (!context.is2d()) {
                return null;
            }
            return (CanvasContextRendering2D) context;
        }
    }

    private void prepareCanvasView(int pageId, int ref) {
        ThreadUtils.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                       /* ESCanvasController canvas = getCanvas(pageId, ref);
                        if (canvas == null) {
                            return;
                        }
                        canvas.prepareCanvasView();*/
                        /*if (canvas.getHostView() != null) {
                            canvas.addCanvasView((CanvasViewContainer) canvas.getHostView());
                        }*/
                    }
                });
    }

    private boolean is2dType(String type) {
        return TextUtils.equals(type, "2d");
    }

    private boolean isWebGLType(String type) {
        return TextUtils.equals(type, "webgl");
    }

    private void setContext(int pageId, int ref, CanvasContext context) {
        /*if (pageId == Component.INVALID_PAGE_ID) {
            return;
        }*/
        ArrayMap<Integer, CanvasContext> map = mContextArrayMap.get(pageId);
        if (map == null) {
            map = new ArrayMap<>();
            mContextArrayMap.put(pageId, map);
        }
        map.put(ref, context);
    }

    //触发渲染
    public void triggerRender(int currentPageId, int currentRefId) {
        ThreadUtils.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {
                        CanvasContext context = getContext(currentPageId, currentRefId);
                        if (context != null && context.is2d()) {
                            ((CanvasContextRendering2D) context).setDirty(true);
                        }

                        CanvasView2D canvas = getCanvas(currentPageId, currentRefId);
                        if (canvas == null) {
                            Log.d(Constants.TAG, "triggerRender,canvasView is null!");
                            return;
                        }
                        canvas.drawCanvas();
                    }
                });
    }

    public void addRenderActions(int pageId, int ref, ArrayList<CanvasRenderAction> actions) {
        if (actions == null || actions.isEmpty()) {
            return;
        }
        ConcurrentHashMap<Integer, ConcurrentLinkedQueue<CanvasRenderAction>> allCachedInPage = mCanvasRenderingCommandQueue.get(pageId);
        if (allCachedInPage == null) {
            allCachedInPage = new ConcurrentHashMap<>();
            mCanvasRenderingCommandQueue.put(pageId, allCachedInPage);
        }

        ConcurrentLinkedQueue<CanvasRenderAction> renderActionsQueue = allCachedInPage.get(ref);
        if (renderActionsQueue == null) {
            renderActionsQueue = new ConcurrentLinkedQueue<>();
            allCachedInPage.put(ref, renderActionsQueue);
        }

        if (renderActionsQueue.size() >= actions.size()) {
            ArrayList<CanvasRenderAction> localActions = new ArrayList<>(renderActionsQueue);
            int localSize = localActions.size();
            int addSize = actions.size();
            int start = localSize - addSize;
            boolean same = true;
            for (int i = 0; start < localSize && i < addSize; start++, i++) {
                if (localActions.get(start).hashCode() != actions.get(i).hashCode()) {
                    same = false;
                    break;
                }
            }
            if (same) {
                return;
            }
        }

        CanvasContextRendering2D context = getOrCreateContext2D(pageId, ref);
        if (context == null) {
            Log.e(TAG, "CanvasRenderingContext2D is NULL!");
            return;
        }

        for (CanvasRenderAction action : actions) {
            // todo 限制action数量
            // while (renderActionsQueue.size() >= MAX_CACHED_COMMAND_LIMIT) {
            // renderActionsQueue.poll();
            // }

            if (action.canClear(context)) {
                renderActionsQueue.clear();
                continue;
            }
            renderActionsQueue.add(action);
        }
    }

    private CanvasContextRendering2D getOrCreateContext2D(int pageId, int ref) {
        /*if (pageId == Component.INVALID_PAGE_ID) {
            return null;
        }*/
        synchronized (this) {
            return (CanvasContextRendering2D) getContext(pageId, ref, "2d");
        }
    }

    public ArrayList<CanvasRenderAction> getRenderActions(int pageId, int ref) {
        ConcurrentHashMap<Integer, ConcurrentLinkedQueue<CanvasRenderAction>> allCachedInPage = mCanvasRenderingCommandQueue.get(pageId);
        if (allCachedInPage == null) {
            return null;
        }
        ConcurrentLinkedQueue<CanvasRenderAction> queue = allCachedInPage.get(ref);
        if (queue == null) {
            return null;
        }
        return new ArrayList<>(queue);
    }

    private static class Holder {
        static CanvasManager instance = new CanvasManager();
    }

    public void setCurrentId(CanvasView2D view, int pageId, int refId) {
        this.currentView = view;
        this.pageId = pageId;
        this.refId = pageId;
        hashMap.put(view, pageId);
    }

    public int getPageId(CanvasView2D view2D) {
        try {
            if (hashMap != null && view2D != null && hashMap.get(view2D) != null) {
                return hashMap.get(view2D);
            } else {
                return 1;
            }
        } catch (Exception e) {
            return 1;
        }
    }

    public int getRef(CanvasView2D view2D) {
        try {
            if (hashMap != null && view2D != null && hashMap.get(view2D) != null) {
                return hashMap.get(view2D);
            } else {
                return 1;
            }
        } catch (Exception e) {
            return 1;
        }
    }

    public void setAction(int pageId, ArrayList<CanvasRenderAction> action) {
        mRenderAction.put(pageId, action);
    }

    public ArrayList<CanvasRenderAction> getAction(int pageId) {
        if (mRenderAction != null) {
            return mRenderAction.get(pageId);
        } else {
            return null;
        }
    }

    public Set<Bitmap> getBitmaps(int viewId) {
        Log.d(TAG, "getBitmaps: viewId------------------->" + viewId);
        return bitmapsHashMap.get(viewId);
    }

    public void setBitmap(int viewId, Bitmap bitmap) {
        Log.d(TAG, "setBitmap: viewId------------------->" + viewId + "    "+bitmap.hashCode());
        bitmapList.add(bitmap);
        setBitmaps(viewId, bitmapList);
        Log.d(TAG, "setBitmap: bitmapList------------------->" + bitmapList.size());
    }

    public void setBitmaps(int viewId, Set<Bitmap> bitmaps) {
        bitmapsHashMap.put(viewId, bitmaps);
    }

    public void deleteBitmapsByPageId(int pageId) {
        // 直接移除对应pageId的Set
        bitmapsHashMap.remove(pageId);
    }

    public void deleteBitmapsByBitmap(Bitmap targetBitmap) {
        // 遍历所有pageId的Set，删除匹配的Bitmap
        for (Set<Bitmap> bitmapSet : bitmapsHashMap.values()) {
            if (bitmapSet != null) {
                Log.d(TAG, "deleteBitmapsByBitmap: ------------------->" + targetBitmap);
                bitmapSet.remove(targetBitmap); // 直接删除单个元素
            }
        }
    }

    public void recycleBitmap() {
        bitmapList.clear();
        bitmapsHashMap.clear();
    }

    public void clearAction() {
        if (mRenderAction != null) {
            mRenderAction.clear();
        }
    }

    public void markBitmapDrawn(Bitmap bitmap) {
        drawnBitmaps.add(bitmap);
        bitmapList.remove(bitmap);
    }

    public void clearDrawnBitmaps() {
        for (Bitmap bitmap : drawnBitmaps) {
            deleteBitmapsByBitmap(bitmap);
        }
        drawnBitmaps.clear();
        bitmapList.clear();
    }
}
