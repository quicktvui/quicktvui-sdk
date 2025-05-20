package com.quicktvui.support.ui.item.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.quicktvui.support.ui.legacy.FConfig;
import com.quicktvui.support.ui.item.utils.DimensUtil;

import com.quicktvui.support.ui.item.R;

import java.util.HashMap;
import java.util.Map;


public class ActorTagWidget extends BuilderWidget<ActorTagWidget.Builder> implements IActorTagWidget {

    String text;
    TextPaint mPaint;
    private Context mContext;

    int mLeft = 92;
    int mTop = 108;

    int height = 39;    //默认高26dp
    int width = 74;     //默认宽49.3dp
    Paint mBgPaint;
    RectF mBgRect;

    float textSize = 16f;   //16sp
    float horizontal_spac = 8.7f;
    int vertical_spac = 5;

    boolean backGroundVisible = false;

    /**
     * 设置TAG比例缩放
     */
    private float scaleOffset = 1.0f;

    static Map<Integer, StaticLayout> layoutMapCache = new HashMap<>();
    StaticLayout staticLayout;
    private final int baseLineY;

    public static class Builder extends BuilderWidget.Builder<ActorTagWidget> {
        public Builder(Context context) {
            super(context);
        }

        @Override
        public ActorTagWidget build() {
            return new ActorTagWidget(this);
        }
    }

    protected ActorTagWidget(Builder builder) {
        super(builder);
        mContext = builder.context;
        mPaint = new TextPaint();
        mLeft = DimensUtil.dp2Px(builder.context, mLeft);
        mTop = DimensUtil.dp2Px(builder.context, mTop);
        width = DimensUtil.dp2Px(builder.context,49.3f);
        height = DimensUtil.dp2Px(builder.context,26f);
        setBounds(mLeft, mTop, mLeft + width, mTop + height);
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setColor(builder.context.getResources().getColor(R.color.color_actor_tag_back));
        mBgRect = new RectF(0, 0, width, height);
        textSize = builder.context.getResources().getDimension(R.dimen.actor_tag_text_size);
        setActorTagSize(0, textSize);

        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        //为基线到字体上边框的距离,即上图中的top
        float top = fontMetrics.top;
        //为基线到字体下边框的距离,即上图中的bottom         int baseLineY = (int) (rect.centerY() - top/2 - bottom/2);//基线中间点的y轴计算公式
        float bottom = fontMetrics.bottom;
        //基线中间点的y轴计算公式
        baseLineY = (int) (mBgRect.centerY() - top / 2 - bottom / 2);
    }

    public void setBgColor(int color) {
        if (mBgPaint != null) {
            mBgPaint.setColor(color);
            invalidateSelf();
        }
    }

    void setTextColor(int color) {
        mPaint.setColor(color);
    }

    StaticLayout obtainStaticLayout(String text) {
        StaticLayout cached = layoutMapCache.get(text.length());
        if (cached == null) {
            cached = new StaticLayout(text + "", mPaint, width, Layout.Alignment.ALIGN_CENTER,
                    0, 0, true);
            layoutMapCache.put(text.length(), cached);
        }
        return cached;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (isVisible()) {
            final int count = canvas.save();
            canvas.translate(mLeft, mTop);

            canvas.scale(scaleOffset, scaleOffset);
            if (backGroundVisible) {
                canvas.drawRoundRect(mBgRect,DimensUtil.dp2Px(mContext, 3f), DimensUtil.dp2Px(mContext, 3f),mBgPaint);
            }
            if (text != null) {
                canvas.drawText(text, DimensUtil.dp2Px(getBuilder().context,horizontal_spac), baseLineY, mPaint);
            }
            if (staticLayout != null) {
                staticLayout.draw(canvas);
            }
            canvas.restoreToCount(count);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    @Override
    public void setActorTag(String actorTag) {
        this.backGroundVisible = !TextUtils.isEmpty(actorTag);
        if (TextUtils.isEmpty(actorTag)) {
            this.setVisible(false);
            invalidateSelf();
        }
        this.text = actorTag;
        if (FConfig.DEBUG) {
            Log.d("ActorTagWidget", "setActorTag tag is " + text + " this is " + this);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            staticLayout = obtainStaticLayout(text);
        }
        invalidateSelf();
    }

    @Override
    public void setActorTagSize(int unit, float size) {
        mPaint.setTextSize(size);
        invalidateSelf();
    }

    @Override
    public void setVisibility(int visible) {
        if (FConfig.DEBUG) {
            Log.d("ActorTagWidget", "setVisibility visible is " + isVisible() + " this is " + this);
        }
        this.setVisible(visible == View.VISIBLE);
    }

    @Override
    public void setVisible(boolean isShow) {
        setVisible(isShow, false);
        invalidateSelf();
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * 设置TAG比例缩放
     */
    public void setNumberWidgetScaleOffset(float scaleOffset) {
        if (scaleOffset <= 0) {
            this.scaleOffset = 1.0f;
        } else {
            this.scaleOffset = scaleOffset;
        }
    }
}
