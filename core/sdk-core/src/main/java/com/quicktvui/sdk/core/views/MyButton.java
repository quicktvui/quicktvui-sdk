package com.quicktvui.sdk.core.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

import com.quicktvui.sdk.core.R;

/**
 * <br>
 *
 * <br>
 */
@SuppressLint("AppCompatCustomView")
public class MyButton extends TextView {

    public MyButton(Context context) {
        this(context, null);
    }

    public MyButton(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyButton(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context){
        setGravity(Gravity.CENTER);
        setTextColor(Color.WHITE);
        setFocusable(true);
        setBackground(context.getResources().getDrawable(R.drawable.eskit_button_background));
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
        setTextColor(focused ? Color.BLACK : Color.WHITE);
    }
}
