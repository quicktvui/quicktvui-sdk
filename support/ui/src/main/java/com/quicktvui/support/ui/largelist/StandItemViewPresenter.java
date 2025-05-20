package com.quicktvui.support.ui.largelist;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.support.ui.item.HomeItemView;

import com.quicktvui.support.ui.ScreenAdapt;

import com.quicktvui.support.ui.item.host.ItemHostView;
import com.quicktvui.support.ui.item.presenter.SimpleItemPresenter;
import com.quicktvui.support.ui.item.presenter.StandardItemPlugin;
import com.quicktvui.support.ui.leanback.Presenter;

public class StandItemViewPresenter extends SimpleItemPresenter implements TemplatePresenter {

    private Template template = new Template();
    private String cornerColor;
    Drawable cornerDrawable;
    private boolean isHideRipple;
    private ScreenAdapt screenAdapt;
    public StandItemViewPresenter(float focusScale) {
        super(new Builder().setPlugin((Plugin) new StandardItemPlugin())
                .enableCover(false)
                .enableBorder(false)
                .setFocusScale(focusScale)
                .enableShimmer(false)
                .setActiveDefaultShadow(false)
                .setActiveFocusShadow(false)
        );
        screenAdapt = ScreenAdapt.getInstance();
    }

    @Override
    protected ItemHostView onCreateHostView(ViewGroup parent) {

        HomeItemView v = null;
        if(template != null){
            EsMap map = template.extra;
            ColorStateList titleColor = null;
            Drawable bg = null;
            if (map != null){
                titleColor = TemplateUtil.createColorStateList(map,"textColor");
                bg = TemplateUtil.createGradientDrawableDrawable(this.template.extra,"topDownFocusBg");
            }
            v = new HomeItemView(parent.getContext(),template.type,titleColor,bg,template.textSize,template.floatTextSize,template.isFree);
            v.setEnableShimmer(false);
            v.setHideRipple(isHideRipple);
            v.setFocusable(true);

            if (map != null){
                if (map.containsKey("imgWidth")&& map.containsKey("imgHeight")
                && map.getInt("imgWidth") != 0 && map.getInt("imgHeight") != 0){
                    v.setImageViewSize(screenAdapt.transform(map.getInt("imgWidth")),screenAdapt.transform(map.getInt("imgHeight")));
                }else{
                    v.setImageViewSize(template.width,template.height);
                }
            }else{
                v.setImageViewSize(template.width,template.height);
            }
            v.setLayoutParams(new RecyclerView.LayoutParams(template.width,template.height));
        }
        return v;
    }


    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        super.onBindViewHolder(viewHolder, item);
        final HomeItemView hv = (HomeItemView) viewHolder.view;
        final TemplateItem ti = (TemplateItem) item;
        hv.setEmpty();
        hv.setCornerColor(cornerColor);
        hv.setCornerTextSize(template.cornerTextSize);
        hv.setCornerBgDrawable(cornerDrawable);
        hv.setContentData(ti);
    }

    @Override
    public void unbindViewHolder(ViewHolder viewHolder) {
        super.unbindViewHolder(viewHolder);
        final HomeItemView hv = (HomeItemView) viewHolder.view;
        hv.setMainTitle("");
        hv.setEmpty();
        viewHolder.view.setOnClickListener(null);
    }

    @Override
    public void applyProps(EsMap props) {
        template.apply(props);
        if (this.template.extra != null){
//            this.textColorStateList = TemplateUtil.createColorStateList(this.template.extra,"textColor");
            this.cornerDrawable = TemplateUtil.createGradientDrawableDrawable(this.template.extra,"cornerBgColor");
            cornerColor = this.template.extra.getString("cornerTextColor");
            isHideRipple = this.template.extra.getBoolean("hideRipper");
        }
    }

    @Override
    public Presenter getPresenter() {
        return this;
    }


}
