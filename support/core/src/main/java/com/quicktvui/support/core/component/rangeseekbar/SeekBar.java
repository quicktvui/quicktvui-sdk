package com.quicktvui.support.core.component.rangeseekbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import android.support.annotation.ColorInt;
import android.support.annotation.IntDef;
import android.support.v4.content.ContextCompat;

import java.text.DecimalFormat;

import com.quicktvui.sdk.base.EsCallback;
import com.quicktvui.sdk.base.IEsTraceable;
import com.quicktvui.support.core.R;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;

public class SeekBar {

    private static final boolean isLoggable = false;
    private static final String TAG = "RangeSeekBar";

    //the indicator show mode
    public static final int INDICATOR_SHOW_WHEN_TOUCH = 0;
    public static final int INDICATOR_ALWAYS_HIDE = 1;
    public static final int INDICATOR_ALWAYS_SHOW_AFTER_TOUCH = 2;
    public static final int INDICATOR_ALWAYS_SHOW = 3;

    @IntDef({INDICATOR_SHOW_WHEN_TOUCH,//
            INDICATOR_ALWAYS_HIDE,//
            INDICATOR_ALWAYS_SHOW_AFTER_TOUCH,//
            INDICATOR_ALWAYS_SHOW//
    })
    public @interface IndicatorModeDef {
    }

    public static final int WRAP_CONTENT = -1;
    public static final int MATCH_PARENT = -2;

    private int indicatorShowMode;

    //进度提示背景的高度，宽度如果是0的话会自适应调整
    //Progress prompted the background height, width,
    private int indicatorHeight;
    private int indicatorWidth;
    //进度提示背景与按钮之间的距离
    //The progress indicates the distance between the background and the button
    private int indicatorMargin;
    private Drawable indicatorDrawableId;
    private String indicatorUrl;

    private int indicatorArrowSize;
    private int indicatorTextSize;
    private int indicatorTextColor;
    private float indicatorRadius;
    private int indicatorBackgroundColor;
    private int indicatorPaddingLeft, indicatorPaddingRight, indicatorPaddingTop, indicatorPaddingBottom;
    private Drawable thumbDrawableId;
    private String thumbDrawableUrl;

    private Drawable thumbInactivatedDrawableId;
    private String thumbInactivatedDrawableUrl;

    private int thumbWidth;
    private int thumbHeight;

    //when you touch or move, the thumb will scale, default not scale
    float thumbScaleRatio;

    //****************** the above is attr value  ******************//

    int left, right, top, bottom;
    float currPercent;
    float material = 0;
    private boolean isShowIndicator;
    boolean isLeft;
    Bitmap thumbBitmap;
    Bitmap thumbInactivatedBitmap;
    Bitmap indicatorBitmap;
    ValueAnimator anim;
    String userText2Draw;
    boolean isActivate = false;
    boolean isVisible = true;
    RangeSeekBar rangeSeekBar;
    String indicatorTextStringFormat;
    Path indicatorArrowPath = new Path();
    Rect indicatorTextRect = new Rect();
    Rect indicatorRect = new Rect();
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    DecimalFormat indicatorTextDecimalFormat;
    int scaleThumbWidth;
    int scaleThumbHeight;

    public SeekBar(RangeSeekBar rangeSeekBar, AttributeSet attrs, boolean isLeft) {
        this.rangeSeekBar = rangeSeekBar;
        this.isLeft = isLeft;
        initAttrs(attrs);
        initBitmap();
        initVariables();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray t = getContext().obtainStyledAttributes(attrs, R.styleable.RangeSeekBar);
        if (t == null) return;
        indicatorMargin = (int) t.getDimension(R.styleable.RangeSeekBar_rsb_indicator_margin, 0);
        indicatorShowMode = t.getInt(R.styleable.RangeSeekBar_rsb_indicator_show_mode, INDICATOR_ALWAYS_HIDE);
        indicatorHeight = t.getLayoutDimension(R.styleable.RangeSeekBar_rsb_indicator_height, WRAP_CONTENT);
        indicatorWidth = t.getLayoutDimension(R.styleable.RangeSeekBar_rsb_indicator_width, WRAP_CONTENT);
        indicatorTextSize = (int) t.getDimension(R.styleable.RangeSeekBar_rsb_indicator_text_size, Utils.dp2px(getContext(), 14));
        indicatorTextColor = t.getColor(R.styleable.RangeSeekBar_rsb_indicator_text_color, Color.WHITE);
        indicatorBackgroundColor = t.getColor(R.styleable.RangeSeekBar_rsb_indicator_background_color, ContextCompat.getColor(getContext(), R.color.rsbColorSeekBarDefault));
        indicatorPaddingLeft = (int) t.getDimension(R.styleable.RangeSeekBar_rsb_indicator_padding_left, 0);
        indicatorPaddingRight = (int) t.getDimension(R.styleable.RangeSeekBar_rsb_indicator_padding_right, 0);
        indicatorPaddingTop = (int) t.getDimension(R.styleable.RangeSeekBar_rsb_indicator_padding_top, 0);
        indicatorPaddingBottom = (int) t.getDimension(R.styleable.RangeSeekBar_rsb_indicator_padding_bottom, 0);
        indicatorArrowSize = (int) t.getDimension(R.styleable.RangeSeekBar_rsb_indicator_arrow_size, 0);
        thumbWidth = (int) t.getDimension(R.styleable.RangeSeekBar_rsb_thumb_width, 0);
        thumbHeight = (int) t.getDimension(R.styleable.RangeSeekBar_rsb_thumb_height, 0);
        thumbScaleRatio = t.getFloat(R.styleable.RangeSeekBar_rsb_thumb_scale_ratio, 1f);
        indicatorRadius = t.getDimension(R.styleable.RangeSeekBar_rsb_indicator_radius, 0f);
        t.recycle();
    }

    protected void initVariables() {
        scaleThumbWidth = thumbWidth;
        scaleThumbHeight = thumbHeight;
        if (indicatorHeight == WRAP_CONTENT) {
            indicatorHeight = Utils.measureText("8", indicatorTextSize).height() + indicatorPaddingTop + indicatorPaddingBottom;
        }
        if (indicatorArrowSize <= 0) {
            indicatorArrowSize = (int) (thumbWidth / 4);
        }
    }

    public Context getContext() {
        return rangeSeekBar.getContext();
    }

    public Resources getResources() {
        if (getContext() != null) return getContext().getResources();
        return null;
    }

    /**
     * 初始化进度提示的背景
     */
    private void initBitmap() {
        if (TextUtils.isEmpty(indicatorUrl)){
            setIndicatorDrawableId(indicatorDrawableId);
        }
       if (TextUtils.isEmpty(thumbDrawableUrl)){
           setThumbDrawableId(thumbDrawableId, thumbWidth, thumbHeight);
       }
       if (TextUtils.isEmpty(thumbInactivatedDrawableUrl)){
           setThumbInactivatedDrawableId(thumbInactivatedDrawableId, thumbWidth, thumbHeight);
       }

    }

    /**
     * 计算每个按钮的位置和尺寸
     * Calculates the position and size of each button
     *
     * @param x position x
     * @param y position y
     */
    protected void onSizeChanged(int x, int y) {
        initVariables();
        initBitmap();
        left = (int) (x - getThumbScaleWidth() / 2);
        right = (int) (x + getThumbScaleWidth() / 2);
        top = y - getThumbHeight() / 2;
        bottom = y + getThumbHeight() / 2;
    }


    public void scaleThumb() {
        scaleThumbWidth = (int) getThumbScaleWidth();
        scaleThumbHeight = (int) getThumbScaleHeight();
        int y = rangeSeekBar.getProgressBottom();
        top = y - scaleThumbHeight / 2;
        bottom = y + scaleThumbHeight / 2;
        setThumbDrawableId(thumbDrawableId, scaleThumbWidth, scaleThumbHeight);
    }

    public void resetThumb() {
        scaleThumbWidth = getThumbWidth();
        scaleThumbHeight = getThumbHeight();
        int y = rangeSeekBar.getProgressBottom();
        top = y - scaleThumbHeight / 2;
        bottom = y + scaleThumbHeight / 2;
        setThumbDrawableId(thumbDrawableId, scaleThumbWidth, scaleThumbHeight);
    }

    public float getRawHeight() {
        return getIndicatorHeight() + getIndicatorArrowSize() + getIndicatorMargin() + getThumbScaleHeight();
    }

    /**
     * 绘制按钮和提示背景和文字
     * Draw buttons and tips for background and text
     *
     * @param canvas Canvas
     */
    protected void draw(Canvas canvas) {
        if (isLoggable) {
            Log.e(TAG, "------------draw-->>>>>");
        }

        if (!isVisible) {
            return;
        }
        int offset = (int) (rangeSeekBar.getProgressWidth() * currPercent);
        canvas.save();
        canvas.translate(offset, 0);
        // translate canvas, then don't care left
        canvas.translate(left, 0);
        if (isShowIndicator) {
            onDrawIndicator(canvas, paint, formatCurrentIndicatorText(userText2Draw));
        }
        onDrawThumb(canvas);
        canvas.restore();
    }


    /**
     * 绘制按钮
     * 如果没有图片资源，则绘制默认按钮
     * <p>
     * draw the thumb button
     * If there is no image resource, draw the default button
     *
     * @param canvas canvas
     */
    protected void onDrawThumb(Canvas canvas) {
        if (!isActivate) {
            if (isLoggable) {
                Log.e(TAG, "onDrawThumb==>thumbInactivatedBitmap==>"//
                        + "thumbInactivatedBitmap:" + thumbInactivatedBitmap + "\n"//
                        + "left:" + 0 + "\n"//
                        + "top:" + (rangeSeekBar.getProgressTop() + (rangeSeekBar.getProgressHeight() - scaleThumbHeight) / 2f));
            }
            if (thumbInactivatedBitmap != null) {
                canvas.drawBitmap(thumbInactivatedBitmap, 0, rangeSeekBar.getProgressTop() + (rangeSeekBar.getProgressHeight() - scaleThumbHeight) / 2f, null);
            }
        } else if (thumbBitmap != null) {
            if (isLoggable) {
                Log.e(TAG, "onDrawThumb==>thumbBitmap==>"//
                        + "thumbBitmap:" + thumbBitmap + "\n"//
                        + "left:" + 0 + "\n"//
                        + "top:" + (rangeSeekBar.getProgressTop() + (rangeSeekBar.getProgressHeight() - scaleThumbHeight) / 2f));
            }
            canvas.drawBitmap(thumbBitmap, 0, rangeSeekBar.getProgressTop() + (rangeSeekBar.getProgressHeight() - scaleThumbHeight) / 2f, null);
        }
    }

    /**
     * 格式化提示文字
     * format the indicator text
     *
     * @param text2Draw
     * @return
     */
    protected String formatCurrentIndicatorText(String text2Draw) {
        SeekBarState[] states = rangeSeekBar.getRangeSeekBarState();
        if (TextUtils.isEmpty(text2Draw)) {
            if (isLeft) {
                if (indicatorTextDecimalFormat != null) {
                    text2Draw = indicatorTextDecimalFormat.format(states[0].value);
                } else {
                    text2Draw = states[0].indicatorText;
                }
            } else {
                if (indicatorTextDecimalFormat != null) {
                    text2Draw = indicatorTextDecimalFormat.format(states[1].value);
                } else {
                    text2Draw = states[1].indicatorText;
                }
            }
        }
        if (indicatorTextStringFormat != null) {
            text2Draw = String.format(indicatorTextStringFormat, text2Draw);
        }
        return text2Draw;
    }

    /**
     * This method will draw the indicator background dynamically according to the text.
     * you can use to set padding
     *
     * @param canvas    Canvas
     * @param text2Draw Indicator text
     */
    protected void onDrawIndicator(Canvas canvas, Paint paint, String text2Draw) {
        if (text2Draw == null) return;
        paint.setTextSize(indicatorTextSize);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(indicatorBackgroundColor);
        paint.getTextBounds(text2Draw, 0, text2Draw.length(), indicatorTextRect);
        int realIndicatorWidth = indicatorTextRect.width() + indicatorPaddingLeft + indicatorPaddingRight;
        if (indicatorWidth > realIndicatorWidth) {
            realIndicatorWidth = indicatorWidth;
        }

        int realIndicatorHeight = indicatorTextRect.height() + indicatorPaddingTop + indicatorPaddingBottom;
        if (indicatorHeight > realIndicatorHeight) {
            realIndicatorHeight = indicatorHeight;
        }

        indicatorRect.left = (int) (scaleThumbWidth / 2f - realIndicatorWidth / 2f);
        indicatorRect.top = bottom - realIndicatorHeight - scaleThumbHeight - indicatorMargin;
        indicatorRect.right = indicatorRect.left + realIndicatorWidth;
        indicatorRect.bottom = indicatorRect.top + realIndicatorHeight;
        //draw default indicator arrow
        if (indicatorBitmap == null) {
            //arrow three point
            //  b   c
            //    a
            int ax = scaleThumbWidth / 2;
            int ay = indicatorRect.bottom;
            int bx = ax - indicatorArrowSize;
            int by = ay - indicatorArrowSize;
            int cx = ax + indicatorArrowSize;
            indicatorArrowPath.reset();
            indicatorArrowPath.moveTo(ax, ay);
            indicatorArrowPath.lineTo(bx, by);
            indicatorArrowPath.lineTo(cx, by);
            indicatorArrowPath.close();
            canvas.drawPath(indicatorArrowPath, paint);
            indicatorRect.bottom -= indicatorArrowSize;
            indicatorRect.top -= indicatorArrowSize;
        }

        //indicator background edge processing
        int defaultPaddingOffset = Utils.dp2px(getContext(), 1);
        int leftOffset = indicatorRect.width() / 2 - (int) (rangeSeekBar.getProgressWidth() * currPercent) - rangeSeekBar.getProgressLeft() + defaultPaddingOffset;
        int rightOffset = indicatorRect.width() / 2 - (int) (rangeSeekBar.getProgressWidth() * (1 - currPercent)) - rangeSeekBar.getProgressPaddingRight() + defaultPaddingOffset;

        if (leftOffset > 0) {
            indicatorRect.left += leftOffset;
            indicatorRect.right += leftOffset;
        } else if (rightOffset > 0) {
            indicatorRect.left -= rightOffset;
            indicatorRect.right -= rightOffset;
        }

        //draw indicator background
        if (indicatorBitmap != null) {
            Utils.drawBitmap(canvas, paint, indicatorBitmap, indicatorRect);
        } else if (indicatorRadius > 0f) {
            canvas.drawRoundRect(new RectF(indicatorRect), indicatorRadius, indicatorRadius, paint);
        } else {
            canvas.drawRect(indicatorRect, paint);
        }

        //draw indicator content text
        int tx, ty;
        if (indicatorPaddingLeft > 0) {
            tx = indicatorRect.left + indicatorPaddingLeft;
        } else if (indicatorPaddingRight > 0) {
            tx = indicatorRect.right - indicatorPaddingRight - indicatorTextRect.width();
        } else {
            tx = indicatorRect.left + (realIndicatorWidth - indicatorTextRect.width()) / 2;
        }

        if (indicatorPaddingTop > 0) {
            ty = indicatorRect.top + indicatorTextRect.height() + indicatorPaddingTop;
        } else if (indicatorPaddingBottom > 0) {
            ty = indicatorRect.bottom - indicatorTextRect.height() - indicatorPaddingBottom;
        } else {
            ty = indicatorRect.bottom - (realIndicatorHeight - indicatorTextRect.height()) / 2 + 1;
        }

        //draw indicator text
        paint.setColor(indicatorTextColor);
        canvas.drawText(text2Draw, tx, ty, paint);
    }

    /**
     * 拖动检测
     *
     * @return is collide
     */
    protected boolean collide(float x, float y) {
        int offset = (int) (rangeSeekBar.getProgressWidth() * currPercent);
        return x > left + offset && x < right + offset && y > top && y < bottom;
    }

    protected void slide(float percent) {
        if (percent < 0) percent = 0;
        else if (percent > 1) percent = 1;
        currPercent = percent;
    }

    protected void setShowIndicatorEnable(boolean isEnable) {
        switch (indicatorShowMode) {
            case INDICATOR_SHOW_WHEN_TOUCH:
                isShowIndicator = isEnable;
                break;
            case INDICATOR_ALWAYS_SHOW:
            case INDICATOR_ALWAYS_SHOW_AFTER_TOUCH:
                isShowIndicator = true;
                break;
            case INDICATOR_ALWAYS_HIDE:
                isShowIndicator = false;
                break;
        }
    }

    public void materialRestore() {
        if (anim != null) anim.cancel();
        anim = ValueAnimator.ofFloat(material, 0);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                material = (float) animation.getAnimatedValue();
                if (rangeSeekBar != null) rangeSeekBar.invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                material = 0;
                if (rangeSeekBar != null) rangeSeekBar.invalidate();
            }
        });
        anim.start();
    }

    public void setIndicatorText(String text) {
        userText2Draw = text;
    }

    public void setIndicatorTextDecimalFormat(String formatPattern) {
        indicatorTextDecimalFormat = new DecimalFormat(formatPattern);
    }

    public DecimalFormat getIndicatorTextDecimalFormat() {
        return indicatorTextDecimalFormat;
    }

    public void setIndicatorTextStringFormat(String formatPattern) {
        indicatorTextStringFormat = formatPattern;
    }

    public Drawable getIndicatorDrawableId() {
        return indicatorDrawableId;
    }

    public void setIndicatorDrawableId(Drawable indicatorDrawableId) {
        if (indicatorDrawableId != null) {
            this.indicatorDrawableId = indicatorDrawableId;
            indicatorBitmap = Utils.drawableToBitmap(getContext(), indicatorWidth, indicatorHeight, indicatorDrawableId);
        }
    }

    public void setIndicatorUrl(IEsTraceable traceable, String indicatorUrl) {
        if (!TextUtils.isEmpty(indicatorUrl)) {
            this.indicatorUrl = indicatorUrl;
            EsMap data = new EsMap();
            data.pushString("url", indicatorUrl);
            data.pushInt("width", indicatorWidth);
            data.pushInt("height", indicatorHeight);
            EsProxy.get().loadImageBitmap(traceable, data, new EsCallback<Bitmap, Throwable>() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    indicatorBitmap = bitmap;
                    if (rangeSeekBar != null) {
                        if (isLoggable) {
                            Log.e(TAG, "----------setIndicatorUrl--success-->>>>>" //
                                    + "indicatorUrl:" + indicatorUrl + "\n"//
                                    + "indicatorBitmap:" + indicatorBitmap + "\n"//
                                    + "indicatorWidth:" + indicatorWidth + "\n"//
                                    + "indicatorHeight:" + indicatorHeight + "\n"//
                            );
                        }
                        rangeSeekBar.invalidateSeekBar();
                    }
                }

                @Override
                public void onFailed(Throwable throwable) {
                    if (isLoggable) {
                        Log.e(TAG, "----------setIndicatorUrl--onFailed-->>>>>" //
                                + "indicatorUrl:" + indicatorUrl + "\n"//
                                + "indicatorWidth:" + indicatorWidth + "\n"//
                                + "indicatorHeight:" + indicatorHeight + "\n"//
                        );
                    }
                }
            });
        }
    }


    public int getIndicatorArrowSize() {
        return indicatorArrowSize;
    }

    public void setIndicatorArrowSize(int indicatorArrowSize) {
        this.indicatorArrowSize = indicatorArrowSize;
    }

    public int getIndicatorPaddingLeft() {
        return indicatorPaddingLeft;
    }

    public void setIndicatorPaddingLeft(int indicatorPaddingLeft) {
        this.indicatorPaddingLeft = indicatorPaddingLeft;
    }

    public int getIndicatorPaddingRight() {
        return indicatorPaddingRight;
    }

    public void setIndicatorPaddingRight(int indicatorPaddingRight) {
        this.indicatorPaddingRight = indicatorPaddingRight;
    }

    public int getIndicatorPaddingTop() {
        return indicatorPaddingTop;
    }

    public void setIndicatorPaddingTop(int indicatorPaddingTop) {
        this.indicatorPaddingTop = indicatorPaddingTop;
    }

    public int getIndicatorPaddingBottom() {
        return indicatorPaddingBottom;
    }

    public void setIndicatorPaddingBottom(int indicatorPaddingBottom) {
        this.indicatorPaddingBottom = indicatorPaddingBottom;
    }

    public int getIndicatorMargin() {
        return indicatorMargin;
    }

    public void setIndicatorMargin(int indicatorMargin) {
        this.indicatorMargin = indicatorMargin;
    }

    public int getIndicatorShowMode() {
        return indicatorShowMode;
    }

    /**
     * the indicator show mode
     * {@link #INDICATOR_SHOW_WHEN_TOUCH}
     * {@link #INDICATOR_ALWAYS_SHOW}
     * {@link #INDICATOR_ALWAYS_SHOW_AFTER_TOUCH}
     * {@link #INDICATOR_ALWAYS_SHOW}
     *
     * @param indicatorShowMode
     */
    public void setIndicatorShowMode(@IndicatorModeDef int indicatorShowMode) {
        this.indicatorShowMode = indicatorShowMode;
    }

    public void showIndicator(boolean isShown) {
        isShowIndicator = isShown;
    }

    public boolean isShowIndicator() {
        return isShowIndicator;
    }

    /**
     * include indicator text Height、padding、margin
     *
     * @return The actual occupation height of indicator
     */
    public int getIndicatorRawHeight() {
        if (indicatorHeight > 0) {
            if (indicatorBitmap != null) {
                return indicatorHeight + indicatorMargin;
            } else {
                return indicatorHeight + indicatorArrowSize + indicatorMargin;
            }
        } else {
            if (indicatorBitmap != null) {
                return Utils.measureText("8", indicatorTextSize).height() + indicatorPaddingTop + indicatorPaddingBottom + indicatorMargin;
            } else {
                return Utils.measureText("8", indicatorTextSize).height() + indicatorPaddingTop + indicatorPaddingBottom + indicatorMargin + indicatorArrowSize;
            }
        }
    }

    public int getIndicatorHeight() {
        return indicatorHeight;
    }

    public void setIndicatorHeight(int indicatorHeight) {
        this.indicatorHeight = indicatorHeight;
    }

    public int getIndicatorWidth() {
        return indicatorWidth;
    }

    public void setIndicatorWidth(int indicatorWidth) {
        this.indicatorWidth = indicatorWidth;
    }

    public int getIndicatorTextSize() {
        return indicatorTextSize;
    }

    public void setIndicatorTextSize(int indicatorTextSize) {
        this.indicatorTextSize = indicatorTextSize;
    }

    public int getIndicatorTextColor() {
        return indicatorTextColor;
    }

    public void setIndicatorTextColor(@ColorInt int indicatorTextColor) {
        this.indicatorTextColor = indicatorTextColor;
    }

    public int getIndicatorBackgroundColor() {
        return indicatorBackgroundColor;
    }

    public void setIndicatorBackgroundColor(@ColorInt int indicatorBackgroundColor) {
        this.indicatorBackgroundColor = indicatorBackgroundColor;
    }

    public Drawable getThumbInactivatedDrawableId() {
        return thumbInactivatedDrawableId;
    }

    public void setThumbInactivatedUrl(String thumbInactivatedUrl) {
        if (!TextUtils.isEmpty(thumbInactivatedUrl)) {
            this.thumbInactivatedDrawableUrl = thumbInactivatedUrl;
            EsMap data = new EsMap();
            data.pushString("url", thumbInactivatedUrl);
            data.pushInt("width", thumbWidth);
            data.pushInt("height", thumbHeight);
            EsProxy.get().loadImageBitmap(data, new EsCallback<Bitmap, Throwable>() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    thumbInactivatedBitmap = bitmap;
                    if (rangeSeekBar != null) {
                        if (isLoggable) {
                            Log.e(TAG, "----------setThumbInactivatedUrl--success-->>>>>" //
                                    + "thumbDrawableId:" + thumbInactivatedUrl + "\n"//
                                    + "thumbBitmap:" + thumbInactivatedBitmap + "\n"//
                                    + "width:" + thumbWidth + "\n"//
                                    + "height:" + thumbHeight + "\n"//
                            );
                        }
                        rangeSeekBar.invalidateSeekBar();
                    }
                }

                @Override
                public void onFailed(Throwable throwable) {
                }
            });
        }
    }

    public void setThumbInactivatedDrawableId(Drawable thumbInactivatedDrawableId) {
        setThumbInactivatedDrawableId(thumbInactivatedDrawableId, thumbWidth, thumbHeight);
    }

    public void setThumbInactivatedDrawableId(Drawable thumbInactivatedDrawableId, int width, int height) {
        if (thumbInactivatedDrawableId != null) {
            this.thumbInactivatedDrawableId = thumbInactivatedDrawableId;
            this.thumbInactivatedBitmap = Utils.drawableToBitmap(getContext(), width, height, thumbInactivatedDrawableId);
        }
    }
    //----------------------------------------------------------------------------
    public Drawable getThumbDrawableId() {
        return thumbDrawableId;
    }

    public void setThumbDrawableId(Drawable thumbDrawableId, int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        loadThumb(thumbDrawableId, width, height);
    }

    public void setThumbDrawableId(Drawable thumbDrawableId) {
        if (thumbWidth <= 0 || thumbHeight <= 0) {
            return;
        }
        loadThumb(thumbDrawableId, thumbWidth, thumbHeight);
    }

    public void setThumbUrl(String thumbUrl) {
        if (thumbWidth <= 0 || thumbHeight <= 0 || TextUtils.isEmpty(thumbUrl)) {
            return;
        }
        loadThumb(thumbUrl, thumbWidth, thumbHeight);
    }

    private void loadThumb(Drawable thumbDrawableId, int width, int height) {
        if (thumbDrawableId != null) {
            this.thumbDrawableId = thumbDrawableId;
            this.thumbBitmap = Utils.drawableToBitmap(width, height, thumbDrawableId);
        }
    }

    private void loadThumb(String thumbDrawableId, int width, int height) {
        if (!TextUtils.isEmpty(thumbDrawableId)) {
            this.thumbDrawableUrl = thumbDrawableId;
            EsMap data = new EsMap();
            data.pushString("url", thumbDrawableId);
            data.pushInt("width", width);
            data.pushInt("height", height);
            EsProxy.get().loadImageBitmap(data, new EsCallback<Bitmap, Throwable>() {
                @Override
                public void onSuccess(Bitmap bitmap) {
                    thumbBitmap = bitmap;
                    if (rangeSeekBar != null) {
                        if (isLoggable) {
                            Log.e(TAG, "----------loadThumb--success-->>>>>" //
                                    + "thumbDrawableId:" + thumbDrawableId + "\n"//
                                    + "thumbBitmap:" + thumbBitmap + "\n"//
                                    + "width:" + width + "\n"//
                                    + "height:" + height + "\n"//
                            );
                        }
                        rangeSeekBar.invalidateSeekBar();
                    }
                }

                @Override
                public void onFailed(Throwable throwable) {
                    if (isLoggable) {
                        Log.e(TAG, "------loadThumb----onFailed---->>>>>" + thumbDrawableId);
                    }
                }
            });
        }
    }

    public int getThumbWidth() {
        return thumbWidth;
    }

    public void setThumbWidth(int thumbWidth) {
        this.thumbWidth = thumbWidth;
    }

    public float getThumbScaleHeight() {
        return thumbHeight * thumbScaleRatio;
    }

    public float getThumbScaleWidth() {
        return thumbWidth * thumbScaleRatio;
    }

    public int getThumbHeight() {
        return thumbHeight;
    }

    public void setThumbHeight(int thumbHeight) {
        this.thumbHeight = thumbHeight;
    }

    public float getIndicatorRadius() {
        return indicatorRadius;
    }

    public void setIndicatorRadius(float indicatorRadius) {
        this.indicatorRadius = indicatorRadius;
    }

    protected boolean getActivate() {
        return isActivate;
    }

    public void setActivate(boolean activate) {
        isActivate = activate;
    }

    public void setTypeface(Typeface typeFace) {
        paint.setTypeface(typeFace);
    }


    /**
     * when you touch or move, the thumb will scale, default not scale
     *
     * @return default 1.0f
     */
    public float getThumbScaleRatio() {
        return thumbScaleRatio;
    }

    public void setThumbScaleRatio(float thumbScaleRatio) {
        this.thumbScaleRatio = thumbScaleRatio;
    }

    public boolean isVisible() {
        return isVisible;
    }

    /**
     * if visble is false, will clear the Canvas
     *
     * @param visible
     */
    public void setVisible(boolean visible) {
        isVisible = visible;
    }

    public float getProgress() {
        float range = rangeSeekBar.getMaxProgress() - rangeSeekBar.getMinProgress();
        return rangeSeekBar.getMinProgress() + range * currPercent;
    }
}
