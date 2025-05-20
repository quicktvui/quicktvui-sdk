package com.quicktvui.sdk.core.card;

import android.content.Context;
import android.view.KeyEvent;
import android.view.View;

import com.quicktvui.sdk.base.component.IEsComponentView;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.views.view.HippyViewGroup;

public class ESCardGroupView extends HippyViewGroup implements IEsComponentView {
    private boolean needFocus;
    private boolean showDefaultBg = true;
    private String cardId;
//    private int placeHolderRadius = 8;
//    protected int left, top;
//    private Paint mPaint;
//    private RectF rectF;

    public ESCardGroupView(Context context) {
        super(context);
        setClipChildren(false);
        setClipToPadding(false);
//        mPaint = new Paint();
//        mPaint.setColor(Color.WHITE);
//        mPaint.setAlpha(25);
//        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//        rectF = new RectF();
    }

//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        rectF.set(left, top, w - left, h - top);
//    }
//
//    @Override
//    public void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        if (showDefaultBg) {
//            canvas.drawRoundRect(rectF, placeHolderRadius, placeHolderRadius, mPaint);
//        }
//    }

    @Override
    protected HippyEngineContext getHippyContext() {
        return super.getHippyContext();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
    }

    public boolean isNeedFocus() {
        return needFocus;
    }

    public void setNeedFocus(boolean needFocus) {
        this.needFocus = needFocus;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
    }

    public boolean isShowDefaultBg() {
        return showDefaultBg;
    }
//
//    public int getPlaceHolderRadius() {
//        return placeHolderRadius;
//    }
//
//    public void setPlaceHolderRadius(int placeHolderRadius) {
//        this.placeHolderRadius = placeHolderRadius;
//    }

    public void setShowDefaultBg(boolean showDefaultBg) {
        this.showDefaultBg = showDefaultBg;
//        invalidate();
//        if (showDefaultBg) {
//            setBackgroundResource(R.drawable.eskit_bg_webframe_default);
//        } else {
//            setBackgroundResource(R.drawable.eskit_bg_webfame_trans);
//        }
    }
}
