package com.quicktvui.sdk.core.card;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quicktvui.base.ui.IRecyclerItemView;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.core.R;
import com.tencent.mtt.hippy.HippyInstanceContext;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.uimanager.HippyViewEvent;


public class WebFrameView extends ESCardView implements IRecyclerItemView {
    private HippyViewEvent cardViewEvent;
    private boolean tag = false;

    public WebFrameView(@NonNull Context context, EsMap params) {
        super(context);
        this.init(((HippyInstanceContext) context).getBaseContext());
        //设置默认状态ui
        setDefaultUI(context);
        //
        if (params != null && params.containsKey("focusable")) {
            Object focusable = params.get("focusable");
            if (focusable instanceof Boolean) {
                defaultFocusable = params.getBoolean("focusable");
            }
        } else {
            defaultFocusable = false;
        }
        setClipChildren(false);
        setClipToPadding(false);
    }

    @Override
    public void setFocusable(boolean focusable) {
        super.setFocusable(focusable);
        if (!tag) {
            defaultFocusable = focusable;
            tag = true;
        }
    }

    @Override
    public void onCreate(View view) {
        getCardViewEvent().send(JSEventViewID, ((HippyInstanceContext) getContext()).getEngineContext(), getItemEventMap("create", -1, null));
    }

    @Override
    public void onBind(View view, int position, Object item) {
        getCardViewEvent().send(JSEventViewID, ((HippyInstanceContext) getContext()).getEngineContext(), getItemEventMap("bind", position, item));
    }

    @Override
    public void onAttachToWindow(View view, int position, Object item) {
        getCardViewEvent().send(JSEventViewID, ((HippyInstanceContext) getContext()).getEngineContext(), getItemEventMap("attach", position, item));
    }

    @Override
    public void onDetachFromWindow(View view, int position, Object item) {
        getCardViewEvent().send(JSEventViewID, ((HippyInstanceContext) getContext()).getEngineContext(), getItemEventMap("detach", position, item));
    }

    @Override
    public void onUnBind(View view, int position, Object item) {
        getCardViewEvent().send(JSEventViewID, ((HippyInstanceContext) getContext()).getEngineContext(), getItemEventMap("unBind", position, item));
    }

    @Override
    public void setJSEventViewID(int JSEventViewID) {
        this.JSEventViewID = JSEventViewID;
    }

    private HippyViewEvent getCardViewEvent() {
        if (cardViewEvent == null) {
            cardViewEvent = new HippyViewEvent("onCardBind");
        }
        return cardViewEvent;
    }

    private HippyMap getItemEventMap(String eventName, int position, Object item) {
        HippyMap hm = new HippyMap();
        hm.pushString("eventName", eventName);
        hm.pushInt("position", position);
        hm.pushObject("itemData", item);
        return hm;
    }

    private void setDefaultUI(Context context) {
        ImageView imageView = new ImageView(context);
        imageView.setBackgroundResource(R.drawable.eskit_card_loading_bg);
        setLoadingView(imageView);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setBackgroundResource(R.drawable.eskit_card_loading_bg);
        TextView textView = new TextView(context);
        textView.setText("加载失败，请重试");
        textView.setTextColor(Color.WHITE);
        textView.setTextSize(18);
        Button button = new Button(context);
        button.setBackgroundResource(R.drawable.eskit_card_load_fail_btn_bg);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadByClick();
            }
        });
        linearLayout.addView(textView, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER_HORIZONTAL));
        FrameLayout.LayoutParams lp = new LayoutParams(130, 40, Gravity.CENTER_HORIZONTAL);
        lp.topMargin = 40;
        linearLayout.addView(button, lp);
        setLoadFailView(linearLayout);
    }
}
