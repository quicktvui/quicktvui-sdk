package com.quicktvui.sdk.core.views;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.quicktvui.base.ui.FocusUtils;
import com.quicktvui.hippyext.RenderUtil;
import com.tencent.mtt.hippy.utils.ExtendUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class DebugFocusRoot extends FrameLayout {
    public static final String TAG = "DebugFocusFloat";
    private DebugFocusView mFloatView;
    private TextView mLeftText;
    private TextView mRightText;
    View focusedView;
    int inset = 10;
    long logTime = 0;
    private Handler mHandler = new Handler();
    public DebugFocusRoot(@NonNull Context context) {
        super(context);
//        setBackgroundColor(Color.argb(60, 0, 0, 255));
        setFocusable(false);
        mFloatView = new DebugFocusView(context);
        addView(mFloatView);
        mLeftText = new TextView(context);
        mRightText = new TextView(context);
        configFloatInfoText(mLeftText);
        configFloatInfoText(mRightText);
        addView(mLeftText);
        addView(mRightText);
        setBackgroundColor(Color.argb(30, 0, 0, 0));
    }

    private void configFloatInfoText(TextView tv){
        tv.setPadding(inset, inset, inset, inset);
        tv.setFocusable(false);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(12);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        RenderUtil.layoutView(mLeftText, 0, 0, w / 4 , h);
        RenderUtil.layoutView(mRightText, 0, 0, w * 2 / 3, h);
        mRightText.setTranslationX(w / 3);
        mRightText.invalidate();
    }

    void layoutFocus(int x, int y, int width, int height) {
        RenderUtil.reLayoutView(mFloatView, x, y, width, height);
    }

    int[] location = new int[2];
    Runnable updateTask;
    void postUpdateInfo() {
        updateTask = () -> {
            if (focusedView != null) {
                focusedView.getLocationOnScreen(location);
                layoutFocus(location[0], location[1], focusedView.getWidth(), focusedView.getHeight());
            }else{
                layoutFocus(0, 0, 0, 0);
            }

            mFloatView.nextTick();
            postUpdateInfo();
        };
        mHandler.postDelayed(updateTask, 16);
    }

    void cancelUpdateInfo() {
        mHandler.removeCallbacksAndMessages(null);
    }

    public void notifyFocusedViewChanged(ViewGroup rootView,View child, View focused) {

        focusedView = focused;
        if (focused != null) {
            mLeftText.setText(debugFocusedViewInfo(rootView,focusedView));
        }
        postUpdateInfo();
    }

    public static String debugFocusedViewInfo(ViewGroup pageView, View view){
        final StringBuilder sb = new StringBuilder();
        if (view == null) {
            return sb.append("focused info => ").append("null").toString();
        }
        FocusUtils.FocusParams lfp = ExtendUtil.
                findUserSpecifiedNextFocusViewIdTraverse(pageView, view, View.FOCUS_LEFT,pageView);
        FocusUtils.FocusParams rfp = ExtendUtil.
                findUserSpecifiedNextFocusViewIdTraverse(pageView, view, View.FOCUS_RIGHT,pageView);
        FocusUtils.FocusParams ufp = ExtendUtil.
                findUserSpecifiedNextFocusViewIdTraverse(pageView, view, View.FOCUS_UP,pageView);
        FocusUtils.FocusParams dfp = ExtendUtil.
                findUserSpecifiedNextFocusViewIdTraverse(pageView, view, View.FOCUS_DOWN,pageView);

        sb.append("focused info => ").append("\n")
                .append("id : ").append(view.getId()).append("\n")
                .append("sid : ").append(ExtendUtil.getViewSID(view)).append("\n")
                .append("name : ").append(ExtendUtil.getViewName(view)).append("\n")
                .append("size : ").append(view.getWidth()+"x"+view.getHeight()).append("\n")
                .append("alpha : ").append(view.getAlpha()).append("\n")
                .append("visibility : ").append(view.getVisibility()).append("\n")
//                append("className : ").append(view.getClass().getSimpleName()).append("\n").
                .append("hashcode : ").append(Integer.toHexString(view.hashCode())).append("\n")
                .append("------------------------------------------------------------\n")
                .append("nextFocusName  : ").append("{").append("\n")
                .append("   left  : ").append(lfp.specifiedTargetViewName).append("\n")
                .append("   right  : ").append(rfp.specifiedTargetViewName).append("\n")
                .append("   up  : ").append(ufp.specifiedTargetViewName).append("\n")
                .append("   down  : ").append(dfp.specifiedTargetViewName).append("\n")
                .append("}").append("\n")
                .append("------------------------------------------------------------\n")
                .append("nextFocusLeftSID  : ").append(lfp.specifiedTargetSID).append("\n")
                .append("nextFocusRightSID  : ").append(rfp.specifiedTargetSID).append("\n")
                .append("nextFocusUpSID  : ").append(ufp.specifiedTargetSID).append("\n")
                .append("nextFocusDownSID  : ").append(dfp.specifiedTargetSID).append("\n")
                .append("------------------------------------------------------------\n")

                ;

        return sb.toString();
    }

    public void onAttachToParent(int parentWidth, int parentHeight) {
        RenderUtil.layoutView(this, inset, inset, parentWidth - inset, parentHeight - inset);
    }

    private List<CharSequence> lines = new ArrayList<>();

    int[] textColorArray = new int[]{Color.RED,Color.GREEN,Color.WHITE};

    int colorIndex = 0;
    String lastLog = "";
    int sameTimes = 1;
    public void printLog(String tag, String msg) {
        boolean sameMsg = lastLog.equals(msg);
        if(sameMsg){
            sameTimes++;
        }else{
            sameTimes = 1;
        }
        lastLog = msg;
        if(lines.size() > 50){
            lines.remove(lines.size() -1);
        }
//        StringBuilder sb = new StringBuilder();
        long now = new Date().getTime();
        SpannableStringBuilder sb = new SpannableStringBuilder();
//        <font color='#FF0000'>红色文本</font>"
        if(sameMsg){
            if(lines.size() > 0){
                SpannableString stringAtTop = new SpannableString(msg);
                final String prefix = "x" + sameTimes;
                int prefixLength = prefix.length();
                stringAtTop = new SpannableString(prefix+" "+stringAtTop);
                stringAtTop.setSpan(new ForegroundColorSpan(textColorArray[colorIndex]), prefixLength, stringAtTop.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                stringAtTop.setSpan(new ForegroundColorSpan(Color.BLUE), 0, prefixLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                lines.set(0,stringAtTop);
            }
        }else{
            SpannableString str = new SpannableString(msg);
            str.setSpan(new ForegroundColorSpan(textColorArray[colorIndex]), 0, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            lines.add(0,str);
            if((now - logTime) > 1000){
//                SpannableString separator = new SpannableString("------------------------------------------------------------------------------------------------------------------------");
//                separator.setSpan(new ForegroundColorSpan(Color.BLUE), 0, separator.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                lines.add(0,
//                        separator);
                colorIndex = (colorIndex + 1) % textColorArray.length;
            }
        }
        logTime = now;
        for(CharSequence line : lines){
            sb.append(line).append("\n");
        }
        mRightText.setText(sb);
    }
}
