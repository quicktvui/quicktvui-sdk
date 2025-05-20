package com.quicktvui.support.ui.largelist;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quicktvui.support.ui.ViewUtil;

import com.quicktvui.support.ui.playmarkview.PlayMarkView;
import com.quicktvui.support.ui.R;
import com.quicktvui.support.ui.item.host.FrameLayoutHostView;

public class TextEpisodeItemHostView extends FrameLayoutHostView implements PendingItemView {
    TextView tx;
    TextView corner;
    View gifImg;
    ViewGroup linearGroup;

    public TextEpisodeItemHostView(Context context) {
        super(context);
        setSelected(false);
    }

    public TextEpisodeItemHostView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setSelected(false);
    }

    public TextEpisodeItemHostView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setSelected(false);
    }

    @Override
    public void setContentData(Object itemData) {
        if (itemData instanceof TemplateItem) {
            String title = ((TemplateItem) itemData).obtainNormalTitle();
            String befTitle = tx.getText().toString();
            if (!TextUtils.isEmpty(title) && !title.equals(befTitle)) {
                tx.setText(title);
            }

            final String flagText = ((TemplateItem) itemData).obtainFlagText();
            Log.d("NumberLog", "setContentData flagText:" + flagText + ",text:" + tx.getText());
            if (TextUtils.isEmpty(flagText)) {
                corner.setText("");
                corner.setVisibility(View.INVISIBLE);
            } else {
                String cornerT = corner.getText().toString();
                if (flagText.equals(cornerT)) {
                    return;
                }
                corner.setText(flagText);
                corner.setVisibility(View.VISIBLE);
            }
            if (isSelected()) {
                gifImg.setVisibility(View.VISIBLE);
            } else {
                gifImg.setVisibility(View.GONE);
            }
            requestLayout();
        } else {
            Log.d("NumberLog", "setContentData null itemData:" + itemData + ",text:" + tx.getText());
        }
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);

    }

    public void setMarkPlayColor(int[] colors) {
        if (gifImg instanceof PlayMarkView) {
            if (colors.length == 1) {
                ((PlayMarkView) gifImg).setPlayColor(colors[0]);
            }
            if (colors.length == 2) {
                ((PlayMarkView) gifImg).setPlayColorState(colors);
            }
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        tx = findViewById(R.id.focus_text);
        corner = findViewById(R.id.corner);
        gifImg = findViewById(R.id.gif_img);
        linearGroup = findViewById(R.id.linearGroup);
        if (gifImg != null) {
            gifImg.setDuplicateParentStateEnabled(true);
        }
    }

    @Override
    public void setSingleSelect(boolean selected) {
        if (tx != null) {
            Log.d("NumberLog", "setSelected:" + selected + ",text:" + tx.getText());
        }
        if (selected != isSelected()) {
            setSelected(selected);
        }
        if (gifImg != null && getWidth() > 0) {
            int visi = gifImg.getVisibility();
            if (selected) {
                if (visi != View.VISIBLE) {
                    gifImg.setVisibility(View.VISIBLE);
                    if (linearGroup != null) {
                        ViewUtil.reLayoutView(linearGroup, 0, 0, getWidth(), getHeight());
                    }
                }
            } else {
                if (visi != View.GONE) {
                    gifImg.setVisibility(View.GONE);
                    if (linearGroup != null) {
                        ViewUtil.reLayoutView(linearGroup, 0, 0, getWidth(), getHeight());
                    }
                }
            }

        }
    }
}
