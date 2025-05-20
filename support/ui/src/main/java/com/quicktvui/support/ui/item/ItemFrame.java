package com.quicktvui.support.ui.item;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.component.IEsComponentView;
import com.quicktvui.hippyext.views.fastlist.PostHandlerView;
import com.quicktvui.hippyext.views.fastlist.TVListView;
import com.tencent.mtt.hippy.utils.ExtendUtil;
import com.tencent.mtt.hippy.views.list.HippyRecycler;
import com.tencent.mtt.hippy.views.view.HippyViewGroup;

import com.quicktvui.support.ui.R;
import com.quicktvui.support.ui.ScreenUtils;
import com.quicktvui.support.ui.item.widget.ShimmerWidget;


public class ItemFrame extends HippyViewGroup implements HippyRecycler, TVListView.PostContentHolder, IEsComponentView {

    //规则阴影背景view
    protected ImageView imgShadow;
    //流光
    private ShimmerWidget shimmerWidget;
    //是否展示阴影
    private boolean isHideShadow = false;
    //是否展示流光
    private boolean enableShimmer = true;
    private int shimmerWidth = 0;
    private int shimmerHeight = 0;
    private float density;


    public ItemFrame(Context context) {
        super(context);
        density = ScreenUtils.getDensity(context.getApplicationContext());
        init(context);
    }
    private void init(Context context){
        this.setClipChildren(false);
        removeAllViews();
        imgShadow = new ImageView(context);
        imgShadow.setTag("shadow");
        LayoutParams param = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        addView(imgShadow,param);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i < getChildCount(); i++) {
            View view = getChildAt(i);
            if (view.getTag()!=null && (view.getTag()+"").equals("shadow")){
                int left = (int)(-24 * density), right = (int)(24 * density);
                int top = (int)(-24 * density),bottom = (int)(30 * density);
                if (r-l==this.shimmerWidth){
                    if (!isHideShadow){
                        imgShadow.layout(left,top,shimmerWidth+right,shimmerHeight+bottom);
                    }
                }
                return;
            }
        }
        super.onLayout(changed, l, t, r, b);
    }
    /**
     * 设置是否展示阴影
     * @param hideShadow
     */
    public void setHideShadow(boolean hideShadow){
        isHideShadow = hideShadow;
        imgShadow.setVisibility(INVISIBLE);
    }

    /**
     * 设置阴影背景
     */
    private void setImgShadow(boolean isFocus){
        if (imgShadow != null && !isHideShadow){
            if (isFocus)
                imgShadow.setBackgroundResource(R.drawable.shadow_focus_home_item_v2);
            else
                imgShadow.setBackgroundColor(Color.parseColor("#00000000"));
        }
    }
    /**
     * 焦点状态阴影显示
     */
    private void focusImgShadow(boolean isFocus){
        if (imgShadow == null){
            return;
        }
        if (isHideShadow){
            if (imgShadow.getVisibility() == View.VISIBLE){
                imgShadow.setVisibility(View.INVISIBLE);
            }
            return;
        }

        setImgShadow(isFocus);
        imgShadow.setVisibility(View.VISIBLE);

    }

    /**
     * 设置是否展示流光
     * @param enableShimmer
     */
    public void setEnableShimmer(boolean enableShimmer) {
        this.enableShimmer = enableShimmer;
    }

    private void focusShimmer(boolean isFocus){
        if (!enableShimmer){
            return;
        }
        if (isFocus){
            getShimmerWidget().onFocusChange(true);
        }else{
            if (shimmerWidget != null) {
                getShimmerWidget().onFocusChange(false);
            }
        }
        postInvalidateDelayed(16);
    }

    public void setShimmerSize(EsArray array){
        if (array != null){
            if (array.size() > 0)
                this.shimmerWidth = array.getInt(0);
            if (array.size() > 1)
                this.shimmerHeight = array.getInt(1);
        }
    }

    /**
     * zhaopeng add
     * @return
     */
    private ShimmerWidget getShimmerWidget(){
        if (shimmerWidget == null) {
            shimmerWidget = new ShimmerWidget.Builder(getContext(), this)
                    .build();
        }
        shimmerWidget.setSize(shimmerWidth, shimmerHeight);
        return shimmerWidget;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if( shimmerWidget != null){
            shimmerWidget.draw(canvas);
        }
    }

    private void invokeFocusChange(boolean isFocus){
        focusImgShadow(isFocus);
        focusShimmer(isFocus);
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        invokeFocusChange(gainFocus);
    }
    private boolean mFocused = false;
    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        if(isDuplicateParentStateEnabled()){
            final boolean focused = ExtendUtil.stateContainsAttribute(getDrawableState(), android.R.attr.state_focused);
            if (mFocused == focused){
                return;
            }
            mFocused = focused;
            invokeFocusChange(focused);
        }
    }
    @Override
    public void setRootPostHandlerView(PostHandlerView pv) {

    }
}
