package com.quicktvui.support.ui.largelist;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import com.quicktvui.support.ui.ScreenAdapt;


public class ItemView extends FrameLayout implements PendingItemView {

    TextView tx;

    Drawable foucusDrawable;
    Drawable commonDrawable;
    Rect padding;

    RectF markRect;

    Paint mPaint;

    int markWidth = ScreenAdapt.getInstance().transform(36);
    int markHeight = ScreenAdapt.getInstance().transform(6);
    int rounder = ScreenAdapt.getInstance().transform(4);
    int markMargin = ScreenAdapt.getInstance().transform(2);

    public ItemView(@NonNull Context context, int width, int height, int textSize, ColorStateList color) {
        super(context);

        tx = new TextView(getContext());
        tx.setTextColor(color);
        tx.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        tx.setDuplicateParentStateEnabled(true);

        setFocusable(true);
        setClipChildren(false);
//        setBackgroundResource(R.drawable.item_background);
        LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        addView(tx, lp);

        RecyclerView.LayoutParams lpp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setLayoutParams(lpp);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);

    }

    public void setMarkWidth(int width) {
        this.markWidth = width;
        postInvalidateDelayed(16);
    }

    public void setMarkHeight(int markHeight) {
        this.markHeight = markHeight;
        postInvalidateDelayed(16);
    }

    public void setMarkMargin(int margin) {
        this.markMargin = margin;
        postInvalidateDelayed(16);
    }

    public void setMarkRounder(int rounder) {
        this.rounder = rounder;
        postInvalidateDelayed(16);
    }

    public void setMarkColor(int color) {
        this.mPaint.setColor(color);
        postInvalidateDelayed(16);
    }

    public void setFocusDrawable(@NotNull Drawable drawable, Rect padding) {
        this.foucusDrawable = drawable;
        this.padding = padding;
        drawable.setVisible(false, false);
    }

    public void setCommonDrawable(@NotNull Drawable drawable, Rect padding) {
        this.commonDrawable = drawable;
        this.padding = padding;
    }

    public void setBGVisible(boolean b) {
        if (foucusDrawable != null) {
            foucusDrawable.setVisible(b, false);
        }

    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        if (commonDrawable == null) return;
        if (selected)
            commonDrawable.setState(new int[]{android.R.attr.state_selected, android.R.attr.state_enabled});
        else
            commonDrawable.setState(new int[]{});
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        foucusDrawable.setBounds(padding.left, padding.top, w + padding.right, h + padding.bottom);
        if (commonDrawable != null)
            commonDrawable.setBounds(padding.left, padding.top, w + padding.right, h + padding.bottom);
        if (w > 0 & h > 0) {
            if (markRect == null) {
                markRect = new RectF();
            }
            int left = (int) ((w - markWidth) * 0.5f);
            int top = h + markMargin;
            markRect.set(left, top, left + markWidth, top + markHeight);
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (foucusDrawable.isVisible()) {
            foucusDrawable.draw(canvas);
        } else if (commonDrawable != null && commonDrawable.isVisible()) {
            commonDrawable.draw(canvas);
        }

        if (isSelected() && !isFocused() && isTaskVisible) {
            if (markRect != null) {
                canvas.drawRoundRect(markRect, rounder, rounder, mPaint);
            }
        }
        super.dispatchDraw(canvas);
    }


    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        foucusDrawable.setVisible(gainFocus, false);
        postInvalidateDelayed(16);
    }

    @Override
    public void draw(Canvas canvas) {

        super.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
    }

    public void setText(String text) {
        tx.setText(text);
    }


    @Override
    public void setContentData(Object itemData) {

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        setMarkVisible(false);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        setMarkVisible(isSelected());
    }

    @Override
    public void setSingleSelect(boolean select) {
        if (isSelected() != select) {
            setMarkVisible(select);
            setSelected(select);
        }
    }


    Runnable visibleTask;
    private boolean isTaskVisible = false;

    void setMarkVisible(boolean b) {
        if (!b) {
            isTaskVisible = false;
        }
        removeCallbacks(visibleTask);
        visibleTask = () -> {
            isTaskVisible = b;
            postInvalidateDelayed(16);
        };
        postDelayed(visibleTask, 100);
    }
}
