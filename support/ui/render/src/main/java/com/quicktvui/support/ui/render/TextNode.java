package com.quicktvui.support.ui.render;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

import android.support.annotation.NonNull;

public class TextNode extends RenderNode {

    String mText;
    String mTempText;

    /**2个文本之间的间隔*/
    private float offset = 90;

    /**控制的x坐标*/
    private int tempX1,tempX2;

    private int textWidth ;
    /**文本的字体大小*/
    private float textSize = 20;

    private boolean isFirstFocused = true;

    private boolean isMarqueAble;// 给外界提供的一个开关
    int textColor;

    boolean layoutWrapContent = false;

    public static final String TAG = "TextNode";


    StaticLayout mStaticLayout;

    int linesCount = 1;

    private OnTextContentListener mOnTextContentListener;

    public String getText() {
        return mText;
    }

    public enum Gravity{
        LEFT(-1),RIGHT(1),CENTER(0);

        private int flag;

        public int getFlag(){
            return flag;
        }

        Gravity(int i) {
            flag = i;
        }
    }

    private Gravity mGravity = Gravity.CENTER;


    private TextPaint tPaint;

    private int paddingLeft = 0;
    private int paddingRight = 0;

    private float justfyOffSet = 10.f;// 调节偏移量

    private long delaySeconds = 1000;//ms----渲染一帧需要60ms，根据2者商，做跳过次数

    int tmpCount = 0;

    boolean isNeedDelay = true;

    boolean forceMeasureText = true;


    @Deprecated
    public static final int WIDTH_WRAP_CONTENT = RenderNode.WRAP_CONTENT;
    public static final int WRAP_CONTENT = RenderNode.WRAP_CONTENT;


    public void setOnTextContentListener(OnTextContentListener mOnTextContentListener) {
        this.mOnTextContentListener = mOnTextContentListener;
        Log.d(TAG,"setOnTextContentListener ："+mOnTextContentListener);
    }

    public int getTextWidth() {
        return textWidth;
    }


    @Override
    protected void init() {
        super.init();
        tPaint = new TextPaint();
        tPaint.setAntiAlias(true);
        tPaint.setTextSize(textSize);
    }

    public void setText(String text) {
        mText = text;
        mTempText = null;
        isFirstFocused = true;
        forceMeasureText();
        invalidateSelf();
    }


    public float getTextSize() {
        return textSize;
    }

    int drawWidth = 0;
    int drawHeight = 0;
    int drawTextY = 0;
    int drawTextX = 0;


    int generateMultiLine(){

        final int width = width();

        final int validTextWidth  = width - paddingRight - paddingLeft;
        final  float  dw = StaticLayout.getDesiredWidth(mText,tPaint);

        final int textMeasureWidth = (int) StaticLayout.getDesiredWidth(mText,0,mText.length(),tPaint);

        if(RenderLab.DEBUG) {
            Log.v(TAG, "doTextLayout textMeasureWidth:" + textMeasureWidth + " validTextWidth is :" + validTextWidth + " lines = " + (textMeasureWidth / (float) validTextWidth) + " getDesiredWidth :" + dw);
        }

        int height ;
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1){
//                if(false){
            mStaticLayout = StaticLayout.Builder.obtain(mText,0,mText.length(),tPaint,validTextWidth).setAlignment(getAlignment()).setMaxLines(linesCount).setEllipsize(TextUtils.TruncateAt.END).setIncludePad(false).build();

            final int displayLineCount = Math.min(linesCount,mStaticLayout.getLineCount());
            height =  Math.min(mStaticLayout.getHeight(),mStaticLayout.getLineBottom(displayLineCount - 1));

            if(RenderLab.DEBUG){
                Log.d(TAG,"generateMultiLine  height :"+height +" text:"+mText+",getLineCount "+mStaticLayout.getLineCount());
            }
        }else {
            final float rate = (validTextWidth * linesCount) / (float)textMeasureWidth;
            final int index = Math.min((int) Math.ceil(mText.length() * rate),mText.length() - 1);

            mStaticLayout = new StaticLayout(mText, 0, index, tPaint, width - paddingLeft - paddingRight, getAlignment(), 0, 0, false);
            final int displayLineCount = Math.min(linesCount,mStaticLayout.getLineCount());
            int contentHeight = mStaticLayout.getLineTop(displayLineCount);
           // height =  Math.min(height(),contentHeight);
            height = Math.min(contentHeight,mStaticLayout.getHeight());
            if(RenderLab.DEBUG){
                Log.d(TAG,"generateMultiLine  validTextWidth :"+validTextWidth +" index:"+index+",getLineCount "+mStaticLayout.getLineCount()+",displayLineCount:"+displayLineCount+",height:"+height+" contentHeight："+contentHeight);
            }
        }

        return height;

    }



    void doWrapContent(){
        if(preferWidth == WRAP_CONTENT && mText != null && height() > 0){

            if(linesCount > 1){
                throw new IllegalStateException("多行文本暂不支持宽度设置为WRAP_CONTENT");
            }else {
                textWidth = (int) tPaint.measureText(mText);

            }

            int width = textWidth + paddingLeft + paddingRight;
            if(width > mParent.width()){
                width = mParent.width();
            }

            changeSizeInternal(width,height());

//            invalidateSelfDelayed(16);
//            invalidateSelf();
        }

        if(preferHeight == WRAP_CONTENT && mText != null && linesCount < 2){
            Rect rect = new Rect();
            tPaint.getTextBounds(mText, 0, mText.length(), rect);
//            final Paint.FontMetrics  fontMetrics = tPaint.getFontMetrics();
//            float height = fontMetrics.bottom - fontMetrics.top + fontMetrics.leading;
            changeSizeInternal(width(), (int) Math.ceil(rect.height()));
        }



    }



    public void setMaxLines(int lineCount){

        if(this.linesCount != lineCount){
            this.linesCount = lineCount;
            forceMeasureText();
        }
    }



    Layout.Alignment getAlignment(){
        if (mGravity == Gravity.CENTER) {
            return Layout.Alignment.ALIGN_CENTER;
        }
        return Layout.Alignment.ALIGN_NORMAL;
    }



    protected void doTextLayout(int textMeasureWidth){
        final int width = width();
        if(width > 0 && height() > 0 && mText != null){

            if(linesCount > 1){

                //CharSequence source, int bufstart, int bufend,
                //                        TextPaint paint, int outerwidth,
                //                        Alignment align, TextDirectionHeuristic textDir,
                //                        float spacingmult, float spacingadd,
                //                        boolean includepad,
                //                        TextUtils.TruncateAt ellipsize, int ellipsizedWidth, int maxLines

//                Class c = StaticLayout.class;
//
//                try {
//                    Constructor constructor =  c.getDeclaredConstructor(String.class,Integer.class,Integer.class,TextPaint.class,Integer.class, Layout.Alignment.class, TextDirectionHeuristic.class,
//                            Float.class,float.class,Boolean.class, TextUtils.TruncateAt.class,Integer.class,Integer.class);
//
//                    constructor.newInstance(mText,0,mText.length(),tPaint,width - paddingLeft - paddingRight,
//                            getAlignment(), TextDirectionHeuristics.,
//                            1.02f,0,
//                            false,
//                            TextUtils.TruncateAt.END,30,linesCount);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                //TODO 多行设置 mGravity暂未实现

            }else {

                final int lastWidth = textWidth;

                final int textWidth = textMeasureWidth;


                if (lastWidth != textWidth) {
                    if (mOnTextContentListener != null) {
                        mOnTextContentListener.onTextWidthChanged(this, textWidth);
                    }
                }

                this.drawWidth = getDrawBounds().width();

                this.drawHeight = getDrawBounds().height();

                tempX1 = paddingLeft;
                tempX2 = tempX1 + textWidth + (int) offset;

                final int vHeight = drawHeight;
                if (mGravity == Gravity.LEFT) {
                    Rect rect = new Rect();
                    tPaint.getTextBounds(mText, 0, mText.length(), rect);
                    drawTextY = (int) ((vHeight * 0.5f) + (tPaint.descent() - tPaint.ascent()) * 0.5f - tPaint.descent());
                }
                if (mGravity == Gravity.CENTER) {
                    Rect rect = new Rect();
                    tPaint.getTextBounds(mText, 0, mText.length(), rect);
                    drawTextY = (int) ((vHeight * 0.5f) + (tPaint.descent() - tPaint.ascent()) * 0.5f - tPaint.descent());
                    if (textWidth + paddingLeft + paddingRight > drawWidth + justfyOffSet) {
                    } else {
                        float tempX = (drawWidth - textWidth) * 0.5f;
                        drawTextX = (int) Math.max(paddingLeft, tempX);
                    }
                }
                if (mGravity == Gravity.RIGHT) {
                    Rect rect = new Rect();
                    tPaint.getTextBounds(mText, 0, mText.length(), rect);
                    drawTextY = (int) ((vHeight * 0.5f) + (tPaint.descent() - tPaint.ascent()) * 0.5f - tPaint.descent());
                    drawTextX = (drawWidth - textWidth - paddingRight);
                }
                if (RenderLab.DEBUG) {
                    Log.d("TextNode", "do measure text");
                }

                if((textMeasureWidth - drawWidth)  > (textSize * - 1) && mText.length() > 2){
                    final int validTextWidth  = width - paddingRight - paddingLeft;
                    if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1){
                        final StaticLayout sl  = StaticLayout.Builder.obtain(mText,0,mText.length(),tPaint,validTextWidth).setAlignment(getAlignment()).setMaxLines(1).setEllipsize(TextUtils.TruncateAt.END).setIncludePad(false).build();
//                        final StaticLayout sl  = new StaticLayout(mText, 0, mText.length(), tPaint, width - paddingLeft - paddingRight, getAlignment(), 0, 0, false);
//                        final int start = sl.getEllipsisStart(0);
                        final String result = sl.getText().toString();
                        mTempText = TextUtils.isEmpty(result) ? null : result;
                    }

                }else{
                    mTempText = null;
                }
            }


        }


    }

    void forceMeasureText(){
        this.forceMeasureText = true;
        invalidateSelf();
    }


    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);
        forceMeasureText();
    }


    @Override
    public void draw(@NonNull Canvas canvas) {

        if(textWidth < 0 || forceMeasureText && mText != null && !mText.isEmpty()){
            final int lastTextWidth = textWidth;
            textWidth = (int) tPaint.measureText(mText);
            if(linesCount > 1) {
                if(width() > 0 && mText != null){
                    if(preferHeight == WRAP_CONTENT ) {
                        changeSizeInternal(width(), generateMultiLine());
                    }else{
                        generateMultiLine();
                    }
                }else{
                    mStaticLayout = null;
                }
            }else {
                doWrapContent();
            }
            doTextLayout(textWidth);
            forceMeasureText = false;
            if(lastTextWidth != textWidth && mOnTextContentListener != null){
                mOnTextContentListener.onTextWidthChanged(this,textWidth);
            }
        }

        super.draw(canvas);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(mText != null && width() > 0 && textWidth > 0  ) {
            canvas.save();
            if(isMarqueAble && textWidth + paddingRight + paddingLeft > drawWidth){
                dealCustomMarque(canvas);
            }else {
                if(linesCount > 1){
                    if(mStaticLayout != null) {
                        if (paddingLeft != 0) {
                            canvas.translate(paddingLeft, 0);
                        }
                        if(RenderLab.DEBUG){
                            Log.d(TAG,"mStaticLayout draw height :"+height()+" mStaticLayout height:"+mStaticLayout.getHeight());
                        }
                        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
//                        if(true){
                            //int contentHeight = mStaticLayout.getLineBottom(Math.min(linesCount,mStaticLayout.getLineCount())-1);
                            canvas.clipRect(0, 0, width() - paddingRight - paddingLeft,  height());
                        }else {
                            canvas.clipRect(0, 0, width() - paddingRight - paddingLeft, height());
                        }
                        mStaticLayout.draw(canvas);
                    }
                }else {
                    tempX1 = paddingLeft;
                    tempX2 = tempX1 + textWidth + (int) offset;
                    if (mGravity == Gravity.LEFT) {
                        canvas.clipRect(0, 0, drawWidth - paddingRight, drawHeight);
                        canvas.drawText(mTempText != null ? mTempText : mText, paddingLeft, drawTextY, tPaint);
                    }
                    if (mGravity == Gravity.CENTER) {
                        if (textWidth + paddingLeft + paddingRight > drawWidth + justfyOffSet) {
                            canvas.clipRect(paddingLeft, 0, drawWidth - paddingRight, drawHeight);
                            canvas.drawText(mTempText != null ? mTempText : mText, paddingLeft, drawTextY, tPaint);
                        } else {
                            canvas.clipRect(drawTextX, 0, drawWidth - drawTextX, drawHeight);
                            canvas.drawText(mTempText != null ? mTempText : mText, drawTextX, drawTextY, tPaint);
                        }
                    }
                    if (mGravity == Gravity.RIGHT) {
                        canvas.clipRect(0, 0, drawWidth, drawHeight);
                        canvas.drawText(mTempText != null ? mTempText : mText, drawTextX, drawTextY, tPaint);
                    }
                }
            }
            canvas.restore();
        }
    }


    public void setTextSize(float textSize) {
        this.textSize = textSize;
        isFirstFocused = true;
        tPaint.setTextSize(textSize);
        invalidateSelf();
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        tPaint.setColor(textColor);
        invalidateSelf();
    }

    public void setGravity(Gravity gravity){
        mGravity = gravity;
        isFirstFocused = true;
        forceMeasureText();
        invalidateSelf();
    }

    /**
     * 设置是否可以滚动
     * @param marqueAble
     */
    public void setMarqueAble(boolean marqueAble) {
        isMarqueAble = marqueAble;
        invalidateSelf();
    }

    /**
     * 设置左padding
     * @param paddingLeft
     */
    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
        forceMeasureText();
        invalidateSelf();
    }

    /**
     * 设置右padding
     * @param paddingRight
     */
    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
        forceMeasureText();
        invalidateSelf();
    }

    /**
     * 设置左右padding
     * @param paddingLR
     */
    public void setPaddingLR(int paddingLR){
        this.paddingRight = paddingLR;
        this.paddingLeft = paddingLR;
        forceMeasureText();
        invalidateSelf();
    }

    /**
     * 调节敏感度阈值
     * @param justfyOffSet
     */
    public void setJustfyOffSet(int justfyOffSet) {
        this.justfyOffSet = justfyOffSet;
        forceMeasureText();
        invalidateSelf();
    }

    /**
     * z做自定义的滚动
     * @param canvas
     */
    private void dealCustomMarque(Canvas canvas) {

//        Paint tmpPaint = new Paint();
//        tmpPaint.setColor(Color.RED);
//        Rect rect = getDrawBounds();
//        canvas.drawRect(rect,tmpPaint);


        canvas.clipRect(paddingLeft,0,getDrawBounds().width() - paddingRight,getDrawBounds().height());

        int vWidth = getDrawBounds().width();
        int vHeight = getDrawBounds().height();

        float tempY = (vHeight / 2.f) +(tPaint.descent() - tPaint.ascent())/ 2.f -tPaint.descent();


        if(tempX1 == paddingLeft || tempX2 == paddingLeft){
            isNeedDelay = true;
        }


        int jumpOverTimes = (int) (delaySeconds / 60);
        if(isNeedDelay){
            tempX1 = (int) paddingLeft;

            tempX2 = (int) (tempX1 + textWidth +offset );
            canvas.drawText(mText,tempX1, tempY,tPaint);
            delayInvalidate();
            while (tmpCount++ <= jumpOverTimes){
                return;
            }
            tmpCount = 0;
            isNeedDelay = false;
        }else {
            if(isFirstFocused){

                tempX2 = (int) (tempX1 + textWidth +offset );
                isFirstFocused = false;
            }


            if(isMarqueAble){
                // 滚动 2个同时滚动
                if(tempX1 <= -textWidth){
                    // 还原,即重置坐标
                    tempX1 = onReachLimitPointAndReSetValue(0);
                }
                canvas.drawText(mText,tempX1, tempY,tPaint);
                if(tempX2 <= -textWidth){
                    tempX2 = onReachLimitPointAndReSetValue(1);
                }
                canvas.drawText(mText,tempX2, tempY,tPaint);
                delayInvalidate();
            }
        }

        tempX1 -= 1;
        tempX2 -= 1;


    }

    private void delayInvalidate(){
        invalidateSelfDelayed(16);
    }


    private int onReachLimitPointAndReSetValue(int flag){
        int tempValue = getDrawBounds().width();
        if(flag == 0){
            tempValue = tempX2 + textWidth + (int)offset;
        }else if(flag ==1){
            tempValue = tempX1 + textWidth + (int)offset;
        }
        return tempValue;
    }


    public interface OnTextContentListener{
        void onTextWidthChanged(TextNode textNode, int textWidth);
    }




}
