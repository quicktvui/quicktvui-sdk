package com.quicktvui.support.ui.legacy.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import com.quicktvui.support.ui.legacy.FConfig;


public class TVFocusFrame extends View implements IFloatFocus {

    final static String TAG = "FBlueFocusFrame";

    private Paint mPaint;

    /**蓝框与view之间的间隙
     * */
    private int mMargin = 0;

    private int mStrokeWidth = 4;

    static boolean DEBUG = FConfig.DEBUG;

//    Point mTargetPoint = new Point();

    private static final float MILLISECONDS_PER_INCH = 25f;

    private final float MILLISECONDS_PER_PX;

    int mDrawX, mDrawY;
    int mDrawWidth, mDrawHeight = 100;


    float fraction = 1.5f;

    Animator mMoveAnimator;

    public TVFocusFrame(Context context) {
        super(context);
        init();
        MILLISECONDS_PER_PX = calculateSpeedPerPixel(context.getResources().getDisplayMetrics());
    }

    public TVFocusFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        MILLISECONDS_PER_PX = calculateSpeedPerPixel(context.getResources().getDisplayMetrics());
    }

    public TVFocusFrame(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        MILLISECONDS_PER_PX = calculateSpeedPerPixel(context.getResources().getDisplayMetrics());
    }


    void init(){
        mPaint = new Paint();
        mPaint.setColor(0xFF0079FF);
        mPaint.setStrokeWidth(getStrokeWidth());
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawRect(mDrawX,mDrawY,mDrawX + mDrawWidth, mDrawY + mDrawHeight,mPaint);
    }


    Animator moveToAnimator(int x,int y,int duration){
        AnimatorSet set = new AnimatorSet();

        ObjectAnimator tx = ObjectAnimator.ofFloat(this,"translationX",getTranslationX(),x);
        ObjectAnimator ty = ObjectAnimator.ofFloat(this,"translationY",getTranslationY(),y);

        tx.setInterpolator(new DecelerateInterpolator(fraction));
        ty.setInterpolator(new DecelerateInterpolator(fraction));

        tx.setDuration(duration);
        ty.setDuration(duration);

        set.playTogether(tx,ty);

        return set;
    }

    Animator sizeToAnimator(float width, float height,int duration){
        final float originWidth = getWidth();
        final float originHeight = getHeight();

        AnimatorSet set = new AnimatorSet();

        ObjectAnimator sx = ObjectAnimator.ofFloat(this,"scaleX",getScaleX(),width / originWidth);
        ObjectAnimator sy = ObjectAnimator.ofFloat(this,"scaleY",getScaleY(),height / originHeight);

        sx.setDuration(duration);
        sy.setDuration(duration);

        set.playTogether(sx,sy);

        return set;
    }

    public void alphaTo(float alpha, int duration) {
        ObjectAnimator a = ObjectAnimator.ofFloat(this,"alpha",getAlpha(),alpha);
        a.setDuration(duration);
        a.start();
    }

    @Override
    public void transformTo(ITVView ITVView, Point p, float alpha, int duration) {

        int fx,fy,fwidth,fheight;

        if(DEBUG){
            Log.v(TAG,"getFloatFocusMoveRectForFView ITVView is "+ ITVView);
        }

        final ITVView fv = ITVView instanceof ITVViewGroup ? ((ITVViewGroup) ITVView).getFloatFocusFocusableView() : ITVView;

        if(fv  == null) {
            return;
        }

        //得到flaotfocus显示的位置
//        fv.getView().getGlobalVisibleRect(rect);
        final View targetView = fv.getView();

        int[] location = new int[2];

        targetView.getLocationInWindow(location);
        final int realX = location[0];
        final int realY = location[1];

        if(DEBUG){
            Log.v(IFloatFocus.TAG,"getView localtion  in window realX is "+realX+" realY is "+realY);
        }

        final float scaleX = fv.getFocusScaleX();
        final float scaleY = fv.getFocusScaleY();


        final int viewWidth = targetView.getWidth();
        final int viewHeight = targetView.getHeight();

        //      因为fView本身可能会放大，所以floatFocus的大小需要根据view的放大而放大
        final int scaleWidth = (int) (viewWidth * scaleX);
        final int scaleHeight = (int) (viewHeight * scaleY);


        //floatFocus在的最终框的大小（双向的margin + 框本身）
        Rect marginRect = new Rect();
        getFrameRect(marginRect);
        //用户额外设置的间隙
        final Rect extraMargin = fv.getFloatFocusMarginRect();
//        if(extraMargin != null) {
//            marginRect.set(marginRect.left - extraMargin.left,
//                    marginRect.top - extraMargin.top,
//                    marginRect.right - extraMargin.right,
//                    marginRect.bottom - extraMargin.bottom);
//        }
        final int paddingLeft = extraMargin.left;
        final int paddingTop = extraMargin.top;
        final int paddingRight = extraMargin.right;
        final int paddingBottom = extraMargin.bottom;

        //位置偏移offset,因为可能子view自己会移动
        if(DEBUG){
            Log.d(IFloatFocus.TAG,"@@@@FRootView getFloatFocusMoveRectForFView offset point is "+p);
        }

        int dx = (int) ((int) ((scaleX - 1) * viewWidth * 0.5f)  + marginRect.width() * 0.5f);
        int dy = (int) ((int) ((scaleY -1) * viewHeight * 0.5f) + marginRect.height() * 0.5f);

        fx = (realX - dx + p.x) + paddingLeft;
        fy =  (realY - dy + p.y) + paddingTop;

//        fx -= 100;

//        fx = 300;
//        fy = 300;

        fwidth =  (scaleWidth + marginRect.width()) - paddingLeft - paddingRight;
        fheight = (scaleHeight + marginRect.height()) - paddingTop - paddingBottom;

        if(DEBUG){
            Log.d(IFloatFocus.TAG,"$$$$$$FRootView transformFloatFocus fx is "+fx+" fy is "+fy+" fwidth is "+fwidth+" fHeight is "+fheight);
        }

        transform(fx,fy,fwidth,fheight,alpha,duration);

    }



    public void transform(int x, int y, int width,  int height,float alpha ,int duration) {

        if(duration < 0){
            duration = DURATION;
        }

        ValueAnimator move = ValueAnimator.ofObject(new PointEvaluator(),new Point(mDrawX,mDrawY),new Point(x,y));
        move.setInterpolator(new DecelerateInterpolator(fraction));
        move.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Point p = (Point) animation.getAnimatedValue();
                mDrawX = p.x;
                mDrawY = p.y;
                postInvalidateDelayed(16);
            }
        });
        move.setDuration(duration);


        ValueAnimator size = ValueAnimator.ofObject(new PointEvaluator(),new Point(mDrawWidth,mDrawHeight),new Point(width,height));
        size.setInterpolator(new DecelerateInterpolator(fraction));
        size.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Point p = (Point) animation.getAnimatedValue();
                mDrawWidth = p.x;
                mDrawHeight = p.y;
                postInvalidateDelayed(16);
            }
        });
        size.setDuration(duration);

        ObjectAnimator a = ObjectAnimator.ofFloat(this,"alpha",getAlpha(),alpha);
        a.setDuration(0);

        AnimatorSet set = new AnimatorSet();

        set.playTogether(move,size,a);

        mMoveAnimator = move;
        set.start();

    }

    @Override
    public void getFrameRect(Rect rect) {
        final int StrokeWidth = getStrokeWidth();
        rect.inset(-mMargin,-mMargin);
        rect.inset(-StrokeWidth,-StrokeWidth);
    }

    /**
     * Calculates the scroll speed.
     *
     * @param displayMetrics DisplayMetrics to be used for real dimension calculations
     * @return The time (in ms) it should take for each pixel. For instance, if returned value is
     * 2 ms, it means scrolling 1000 pixels with LinearInterpolation should take 2 seconds.
     */
    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
        return MILLISECONDS_PER_INCH / displayMetrics.densityDpi;
    }


//    protected int calculateTimeForDeceleration(){
//        final float x1 = getTranslationX();
//        final float y1 = getTranslationY();
//        final float x2 = mTargetPoint.x;
//        final float y2 = mTargetPoint.y;
//        final float dx2 = (x2 - x1) * (x2 -x1);
//        final float dy2 = (y2 - y1) * (y2 - y1);
//        return calculateTimeForDeceleration((int) Math.abs(Math.sqrt(dx2 + dy2)));
//    }

    /**
     * <p>Calculates the time for deceleration so that transition from LinearInterpolator to
     * DecelerateInterpolator looks smooth.</p>
     *
     * @param dx Distance to scroll
     * @return Time for DecelerateInterpolator to smoothly traverse the distance when transitioning
     * from LinearInterpolation
     */
    protected int calculateTimeForDeceleration(int dx) {
        // we want to cover same area with the linear interpolator for the first 10% of the
        // interpolation. After that, deceleration will take control.
        // area under curve (1-(1-x)^2) can be calculated as (1 - x/3) * x * x
        // which gives 0.100028 when x = .3356
        // this is why we divide linear scrolling time with .3356
        return  (int) Math.ceil(calculateTimeForScrolling(dx) / .3356);
    }

    /**
     * Calculates the time it should take to scroll the given distance (in pixels)
     *
     * @param dx Distance in pixels that we want to scroll
     * @return Time in milliseconds
     * @see #calculateSpeedPerPixel(android.util.DisplayMetrics)
     */
    protected int calculateTimeForScrolling(int dx) {
        // In a case where dx is very small, rounding may return 0 although dx > 0.
        // To avoid that issue, ceil the result so that if dx > 0, we'll always return positive
        // time.
        return (int) Math.ceil(Math.abs(dx) * MILLISECONDS_PER_PX);
    }

    @Override
    public int getStrokeWidth() {
        return mStrokeWidth;
    }

    @Override
    public void offset(int dx,int dy) {
        mDrawX += dx;
        mDrawY += dy;
        invalidate();
    }

    @Override
    public void remove(TVRootView rootView) {
        rootView.removeView(this);
    }


    @Override
    public void show(int duration) {
        if(duration == 0){
            setAlpha(1);
        }else {
            alphaTo(1, duration);
        }
    }

    @Override
    public void dismiss(int duration) {
        if(duration == 0) {
            setAlpha(0);
        } else {
            alphaTo(0, duration);
        }
    }


    @Override
    public void bringToFront() {
        super.bringToFront();
    }

    @Override
    public void frozen() {
        //TODO
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public void addToContainer(TVRootView rootView) {
        rootView.addView(this,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }

    @Override
    public void setVisible(boolean visible) {

        setVisibility(visible? View.VISIBLE : View.INVISIBLE);

    }


    class PointEvaluator implements TypeEvaluator{
        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            Point startPoint = (Point) startValue;
            Point endPoint = (Point) endValue;
            float x = startPoint.x + fraction * (endPoint.x - startPoint.x);
            float y = startPoint.y + fraction * (endPoint.y - startPoint.y);
            Point point = new Point((int)x,(int) y);
            return point;
        }

    }
}
