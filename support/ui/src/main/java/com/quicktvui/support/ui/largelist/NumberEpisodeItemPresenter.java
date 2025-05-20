package com.quicktvui.support.ui.largelist;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.ViewGroup;

import android.support.v7.widget.RecyclerView;

import com.quicktvui.sdk.base.args.EsMap;

import java.util.List;

import com.quicktvui.support.ui.R;
import com.quicktvui.support.ui.ScreenAdapt;
import com.quicktvui.support.ui.item.presenter.SimpleItemPresenter;
import com.quicktvui.support.ui.item.presenter.StandardItemPlugin;
import com.quicktvui.support.ui.item.widget.LazyWidgetsHolder;
import com.quicktvui.support.ui.leanback.Presenter;

public class NumberEpisodeItemPresenter extends SimpleItemPresenter implements TemplatePresenter {

    Template template = new Template();
    ColorStateList textColorStateList;
    Drawable cornerDrawable;
    int cornerTextColor = -1;
    private List<GradientDrawable> bg;
    int[] playMarkColor = null;
    int lines = 1;

    public NumberEpisodeItemPresenter(float focusSale) {
        super(new Builder().setPlugin((Plugin) new StandardItemPlugin())
                .setHostViewLayout(R.layout.episode_item_host_view)
                .enableCover(false)
                .enableBorder(false)
                .enableShimmer(false)
                .setActiveFocusShadow(false).setActiveFocusShadow(false)
                .setFocusScale(focusSale)
        );
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        final LazyWidgetsHolder h = (LazyWidgetsHolder) super.onCreateViewHolder(parent);
        final TextEpisodeItemHostView flh = h.view.findViewById(R.id.root_view);
        if (template != null) {
            if (this.template.extra != null) {
//                final Drawable  stateListDrawable = TemplateUtil.createGradientDrawableDrawable(this.template.extra,"focusBackground");
                if (template.type.equals("text")) {
                    int lines = this.template.extra.containsKey("titleLines") ? this.template.extra.getInt("titleLines") : -1;
                    this.lines = lines;
                    if (lines > 1) {
                        flh.tx.setMaxLines(lines);
                    } else {
                        flh.tx.setSingleLine(true);
                        flh.tx.setFocusableInTouchMode(true);
                        flh.tx.setMarqueeRepeatLimit(-1);
                    }
                    flh.tx.setEllipsize(TextUtils.TruncateAt.END);
                }
            }
            h.view.setLayoutParams(new RecyclerView.LayoutParams(template.width, template.height));

            if (this.textColorStateList != null) {
                flh.tx.setTextColor(this.textColorStateList);
            }
            if (playMarkColor != null) {
                flh.setMarkPlayColor(playMarkColor);
            }
//            flh.tx.setTextSize(template.textSize);
            flh.tx.setTextSize(TypedValue.COMPLEX_UNIT_PX, template.textSize);
            if (this.cornerDrawable != null && flh.corner != null) {
                flh.corner.setBackgroundDrawable(cornerDrawable);

//                flh.corner.setTextSize(template.cornerTextSize);
                flh.corner.setTextSize(TypedValue.COMPLEX_UNIT_PX, template.cornerTextSize);

                flh.corner.setTextColor(cornerTextColor);

                ViewGroup.LayoutParams layoutParams = flh.corner.getLayoutParams();
                layoutParams.width = ScreenAdapt.getInstance().transform(45);
                layoutParams.height = ScreenAdapt.getInstance().transform(30);
                flh.corner.setLayoutParams(layoutParams);
            }
            if (this.bg != null && bg.size() == 2) {
                flh.setBackgroundDrawable(bg.get(0));
            }
        }

        return h;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Object item) {
        super.onBindViewHolder(viewHolder, item);
        viewHolder.view.setSelected(false);
        if (viewHolder instanceof LazyWidgetsHolder) {
            final TextEpisodeItemHostView flh = (TextEpisodeItemHostView) viewHolder.view;
            flh.setContentData(item);
            if (this.cornerDrawable != null && flh.corner != null) {
                flh.corner.setBackgroundDrawable(cornerDrawable);
                flh.corner.setTextColor(cornerTextColor);
            }
            viewHolder.view.setOnFocusChangeListener((v, hasFocus) -> {
                if (bg != null && bg.size() == 2) {
                    if (hasFocus) {
                        flh.setBackgroundDrawable(bg.get(1));
                        if (this.lines == 1){
                            flh.tx.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                            flh.tx.setSelected(true);
                        }
                    } else {
                        flh.setBackgroundDrawable(bg.get(0));
                        if (this.lines == 1){
                            flh.tx.setSelected(false);
                            flh.tx.setEllipsize(TextUtils.TruncateAt.END);
                        }
                    }
                }

            });
        }
    }

    @Override
    public void unbindViewHolder(ViewHolder viewHolder) {
        super.unbindViewHolder(viewHolder);
        viewHolder.view.setOnClickListener(null);
        viewHolder.view.setOnFocusChangeListener(null);

    }

    @Override
    public void applyProps(EsMap props) {
        this.template.apply(props);
        if (this.template.extra != null) {
            this.textColorStateList = TemplateUtil.createColorStateList(this.template.extra, "textColor");
            this.cornerDrawable = TemplateUtil.createGradientDrawableDrawable(this.template.extra, "cornerBgColor");
            String corColor = this.template.extra.getString("cornerTextColor");
            if (!TextUtils.isEmpty(corColor)) {
                this.cornerTextColor = Color.parseColor(corColor);
            }
            this.playMarkColor = TemplateUtil.createStateColor(this.template.extra, "playMark");
            this.bg = TemplateUtil.createStateListDrawable(this.template.extra, "numberFocusBg");
        }

//        this.backgroundStateList = TemplateUtil.createColorStateList(this.template.templateMap,"backGroundColor");
    }

    @Override
    public Presenter getPresenter() {
        return this;
    }


}
