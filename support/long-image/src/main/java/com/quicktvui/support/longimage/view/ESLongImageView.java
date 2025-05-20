package com.quicktvui.support.longimage.view;

import android.content.Context;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Scroller;

import com.quicktvui.sdk.base.component.IEsComponentView;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.utils.LogUtils;

import com.quicktvui.support.longimage.utils.LongImageEventUtils;

/**
 * @auth: njb
 * @date: 2022/11/3 15:51
 * @desc: 长图组件
 */
public class ESLongImageView extends SubsamplingScaleImageView implements IEsComponentView, SubsamplingScaleImageView.OnImageEventListener {
    private static final String TAG = "ESLongImageViewLog";
    private Scroller mScroller;
    private final int scrollHeight = 200;
    private int scrollY = 0;
    private long mLastTime;

    public ESLongImageView(Context context) {
        this(context, null);
        initialise();
    }

    public ESLongImageView(Context context, AttributeSet attr) {
        super(context, attr);
        initialise();
    }

    private void initialise() {
        mScroller = new Scroller(getContext());
//        setMinScale(0.5f);
//        setMaxScale(1f);
//        setDoubleTapZoomStyle(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER);
//        setDoubleTapZoomDuration(1000);
//        setMinimumTileDpi(50);
//        setMinimumDpi(40);
//        setDoubleTapZoomScale(1.5F);
        this.setOnKeyListener((v, keyCode, event) -> {
            setDispatchKeyEvent(event, false);
            return false;
        });
    }

    public void setDispatchKeyEvent(KeyEvent event, boolean isFocusKey) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP) {
                if (isFocusKey) {
//                    startSmoothScrollUp(0, 50);
                    touchMoveUp();
                }
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_DOWN) {
                if (isFocusKey) {
//                    startSmoothScrollDown(0, 50);
                    touchMoveDown();
                }
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (isFocusKey) {
//                    startSmoothScrollDown(0, 50);
                    touchMoveLeft();
                }
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (isFocusKey) {
                    touchMoveRight();
                }
            }
            if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_CENTER) {
                if (isFocusKey) {
                    LogUtils.d(TAG, "触发了center！");
                }
                long mCurTime = System.currentTimeMillis();
//                mLastTime = mCurTime;
//                mCurTime = System.currentTimeMillis();
                if (mCurTime - mLastTime < 500) {
                    mLastTime = 0;
                    LogUtils.d(TAG, "触发了双击！" + "-----maxScale:" + getMaxScale() + "-----minScale:" + getMinScale());
//                    scaleAndCenter();
                    doubleClickEvent();
                } else {
                    mLastTime = mCurTime;
                }
            }
        }
    }

    public void touchMoveUp() {
        long downTime = SystemClock.currentThreadTimeMillis();
        MotionEvent down = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, 0, 0, 0);
        downTime += 1000;
        MotionEvent move = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_MOVE, 0, 200, 0);
        downTime += 1000;
        MotionEvent up = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, 0, 0, 0);
        dispatchTouchEvent(down);
        dispatchTouchEvent(move);
        dispatchTouchEvent(up);
    }

    public void touchMoveDown() {
        long downTime = SystemClock.currentThreadTimeMillis();
        MotionEvent down = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, 0, 0, 0);
        downTime += 1000;
        MotionEvent move = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_MOVE, 0, -200, 0);
        downTime += 1000;
        MotionEvent up = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, 0, 0, 0);
        dispatchTouchEvent(down);
        dispatchTouchEvent(move);
        dispatchTouchEvent(up);
    }

    public void touchMoveLeft() {
        long downTime = SystemClock.currentThreadTimeMillis();
        MotionEvent down = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, 0, 0, 0);
        downTime += 1000;
        MotionEvent move = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_MOVE, 200, 0, 0);
        downTime += 1000;
        MotionEvent up = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, 0, 0, 0);
        dispatchTouchEvent(down);
        dispatchTouchEvent(move);
        dispatchTouchEvent(up);
    }

    public void touchMoveRight() {
        long downTime = SystemClock.currentThreadTimeMillis();
        MotionEvent down = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, 0, 0, 0);
        downTime += 1000;
        MotionEvent move = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_MOVE, -200, 0, 0);
        downTime += 1000;
        MotionEvent up = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, 0, 0, 0);
        dispatchTouchEvent(down);
        dispatchTouchEvent(move);
        dispatchTouchEvent(up);
    }

    public void doubleClickEvent() {
        long downTime = SystemClock.currentThreadTimeMillis();
        MotionEvent down1 = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, 0, 0, 0);
        downTime += 10;
        MotionEvent up1 = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, 0, 0, 0);
        downTime += 100;
        MotionEvent down2 = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_DOWN, 0, 0, 0);
        downTime += 10;
        MotionEvent up2 = MotionEvent.obtain(downTime, downTime, MotionEvent.ACTION_UP, 0, 0, 0);
        dispatchTouchEvent(down1);
        dispatchTouchEvent(up1);
        dispatchTouchEvent(down2);
        dispatchTouchEvent(up2);
    }

    /**
     * 向下滑动
     *
     * @param desX
     * @param ms
     */
    public void startSmoothScrollDown(int desX, int ms) {
        int startX = getScrollX();
        int startY = getScrollY();
        scrollY = getScrollY();
        int scrollDownHeight = this.getSHeight() - scrollY - scrollHeight;
        //startScroll(x起始坐标，y起始坐标，x方向偏移值，y方向偏移值，滚动时长)
        if (LogUtils.isDebug()) {
            LogUtils.d(TAG, "startX:" + startX + "----向下滑动距离---" + scrollDownHeight + "----滑动y坐标----" + startY + "----getSHeight:" + this.getSHeight());
        }
        if (scrollDownHeight > 920) {
            mScroller.startScroll(startX, startY, desX - startX, scrollHeight, ms);
        }

        invalidate();
    }

    /**
     * 向上滑动
     *
     * @param desX
     * @param ms
     */
    public void startSmoothScrollUp(int desX, int ms) {
        int startX = getScrollX();
        int startY = getScrollY();
        float y = getY();
        float translationY = getTranslationY();
        scrollY = getScrollY();
        if (scrollY > 0) {
            if (scrollY < scrollHeight) {
                mScroller.startScroll(0, startY, desX, -scrollY, ms);
            } else {
                mScroller.startScroll(0, startY, desX, -scrollHeight, ms);
            }
        }
        if (LogUtils.isDebug()) {
            LogUtils.d(TAG, "向上滑动滑动x坐标" + startX + "----滑动y坐标----" + startY + "----y:" + y + "----translationY:" + translationY);
        }
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (!isReady()) {
            return;
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void onReady() {
        if (LogUtils.isDebug()) {
            LogUtils.d(TAG, "图片资源已准备" + this.isReady());
        }
        HippyMap map = new HippyMap();
        map.pushBoolean("onReady", this.isReady());
        LongImageEventUtils.sendLongImageEvent(this, LongImageEventUtils.LONG_IMAGE_ON_READY, map);
    }

    @Override
    public void onImageLoaded() {
        if (LogUtils.isDebug()) {
            LogUtils.d(TAG, "图片已加载" + this.isImageLoaded());
        }
        HippyMap map = new HippyMap();
        map.pushBoolean("onImageLoaded", this.isImageLoaded());
        LongImageEventUtils.sendLongImageEvent(this, LongImageEventUtils.LONG_IMAGE_ON_IMAGE_LOADED, map);
    }

    @Override
    public void onPreviewLoadError(Exception e) {
        if (LogUtils.isDebug()) {
            LogUtils.e(TAG, "图片预览失败" + e.getMessage());
        }
        HippyMap map = new HippyMap();
        map.pushString("errorMessage", e.getMessage());
        LongImageEventUtils.sendLongImageEvent(this, LongImageEventUtils.LONG_IMAGE_ON_PREVIEW_LOAD_ERROR, map);
    }

    @Override
    public void onImageLoadError(Exception e) {
        if (LogUtils.isDebug()) {
            LogUtils.e(TAG, "图片加载失败" + e.getMessage());
        }
        HippyMap map = new HippyMap();
        map.pushString("errorMessage", e.getMessage());
        LongImageEventUtils.sendLongImageEvent(this, LongImageEventUtils.LONG_IMAGE_ON_IMAGE_LOAD_ERROR, map);
    }

    public void onImageLoadError(String errorMessage){
        if (LogUtils.isDebug()) {
            LogUtils.e(TAG, "图片加载失败" + errorMessage);
        }
        HippyMap map = new HippyMap();
        map.pushString("errorMessage", errorMessage);
        LongImageEventUtils.sendLongImageEvent(this, LongImageEventUtils.LONG_IMAGE_ON_IMAGE_LOAD_ERROR, map);
    }

    @Override
    public void onTileLoadError(Exception e) {
        if (LogUtils.isDebug()) {
            LogUtils.e(TAG, "图片平铺加载失败" + e.getMessage());
        }
        HippyMap map = new HippyMap();
        map.pushString("onTileLoadErrorMessage", e.getMessage());
        LongImageEventUtils.sendLongImageEvent(this, LongImageEventUtils.LONG_IMAGE_ON_TILE_LOAD_ERROR, map);
    }

    @Override
    public void onPreviewReleased() {
        if (LogUtils.isDebug()) {
            if (this.getDrawingCache() != null) {
                LogUtils.d(TAG, "图片预加载资源已回收" + this.getDrawingCache().isRecycled());
                HippyMap map = new HippyMap();
                map.pushBoolean("onPreviewReleased", this.getDrawingCache().isRecycled());
                LongImageEventUtils.sendLongImageEvent(this, LongImageEventUtils.LONG_IMAGE_ON_PREVIEW_RELEASED, map);
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        recycle();
        System.gc();
    }
}
