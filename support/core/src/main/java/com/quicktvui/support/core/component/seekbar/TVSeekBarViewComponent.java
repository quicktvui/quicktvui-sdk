package com.quicktvui.support.core.component.seekbar;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.support.core.R;
import com.quicktvui.support.core.component.rangeseekbar.ESHorizontalSeekBarViewComponent;
import com.quicktvui.support.core.component.rangeseekbar.OnRangeFocusListener;
import com.quicktvui.support.core.component.rangeseekbar.RangeSeekBar;
import com.quicktvui.support.core.utils.GradientDrawableUtils;

@ESKitAutoRegister
public class TVSeekBarViewComponent extends ESHorizontalSeekBarViewComponent {

    protected static final String OP_SET_MAX_PROGRESS = "setMaxProgress";
    protected static final String OP_SHOW = "show";

    @Override
    public RangeSeekBar createView(Context context, EsMap initParams) {

        Log.e("TVSeekBar", "-----RangeSeekBar----createView--------->>>>" + initParams);

        RangeSeekBar rangeSeekBar = super.createView(context, initParams);
        rangeSeekBar.setSeekBarMode(RangeSeekBar.SEEKBAR_MODE_SINGLE);
        rangeSeekBar.setGravity(RangeSeekBar.Gravity.CENTER);
        rangeSeekBar.setFocusable(true);

        //progress
        rangeSeekBar.setProgressRadius(EsProxy.get().getDisplayManager().dp2px(8));
        rangeSeekBar.setProgressHeight((int) EsProxy.get().getDisplayManager().dp2px(16));
        rangeSeekBar.setProgressDefaultColor(Color.parseColor("#80ffffff"));
        rangeSeekBar.setProgressColor(Color.parseColor("#FFFF3823"));

        //
        float thumbSize = EsProxy.get().getDisplayManager().dp2px(40);
        rangeSeekBar.getLeftSeekBar().setThumbWidth((int) thumbSize);
        rangeSeekBar.getLeftSeekBar().setThumbHeight((int) thumbSize);

        //thumb
        Drawable thumbDrawable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            thumbDrawable = context.getResources().getDrawable(R.drawable.es_seek_bar_thumb_sel, null);
        } else {
            thumbDrawable = context.getResources().getDrawable(R.drawable.es_seek_bar_thumb_sel);
        }
        rangeSeekBar.getLeftSeekBar().setThumbDrawableId(thumbDrawable);

        Drawable thumbInactivatedDrawable = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            thumbInactivatedDrawable = context.getResources().getDrawable(R.drawable.es_seek_bar_thumb_nor, null);
        } else {
            thumbInactivatedDrawable = context.getResources().getDrawable(R.drawable.es_seek_bar_thumb_nor);
        }
        rangeSeekBar.getLeftSeekBar().setThumbInactivatedDrawableId(thumbInactivatedDrawable);

        rangeSeekBar.setOnRangeFocusListener(new OnRangeFocusListener() {
            @Override
            public void onRangeFocusChange(RangeSeekBar view, boolean hasFocus) {
                try {
                    Log.d("TVSeekBar", "---------onFocusChange--------->>>>" + hasFocus);
                    rangeSeekBar.getLeftSeekBar().setActivate(hasFocus);
                    rangeSeekBar.invalidateSeekBar();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        return rangeSeekBar;
    }

    @EsComponentAttribute
    public void backgroundColor(RangeSeekBar rangeSeekBar, String backgroundColor) {
        try {
            if (rangeSeekBar != null) {
                rangeSeekBar.setProgressDefaultColor(Color.parseColor(backgroundColor));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @EsComponentAttribute
    public void color(RangeSeekBar seekBar, EsMap params) {
        try {
            if (params == null) {
                return;
            }

            EsMap progressDrawableId = new EsMap();

            //type
            if (params.containsKey("type")) {
                int type = params.getInt("type");
                progressDrawableId.pushInt("type", type);
            }

            //shape
            if (params.containsKey("shape")) {
                int shape = params.getInt("shape");
                progressDrawableId.pushInt("shape", shape);
            }

            //orientation
            if (params.containsKey("orientation")) {
                int orientation = params.getInt("orientation");
                progressDrawableId.pushInt("orientation", orientation);
            } else {
                progressDrawableId.pushInt("orientation", 6);
            }

            //colors
            String startColor = params.getString("startColor");
            String endColor = params.getString("endColor");
            EsArray array = new EsArray();
            array.pushString(startColor);
            array.pushString(endColor);
            progressDrawableId.pushArray("colors", array);

            //gradientRadius
            if (params.containsKey("gradientRadius")) {
                int cornerRadius = params.getInt("gradientRadius");
                progressDrawableId.pushInt("gradientRadius", cornerRadius);
            }

            //cornerRadius
            if (params.containsKey("cornerRadius")) {
                double cornerRadius = params.getDouble("cornerRadius");
                progressDrawableId.pushDouble("cornerRadius", cornerRadius);
            } else {
                progressDrawableId.pushDouble("cornerRadius", EsProxy.get().getDisplayManager().dp2px(100));
            }

            //cornerRadii4
            if (params.containsKey("cornerRadii4")) {
                EsArray cornerRadii4 = params.getArray("cornerRadii4");
                progressDrawableId.pushArray("cornerRadii4", cornerRadii4);
            }

            //cornerRadii8
            if (params.containsKey("cornerRadii8")) {
                EsArray cornerRadii8 = params.getArray("cornerRadii8");
                progressDrawableId.pushArray("cornerRadii8", cornerRadii8);
            }

            GradientDrawable gradientDrawable =//
                    GradientDrawableUtils.createGradientDrawable(progressDrawableId);
            if (gradientDrawable != null) {
                seekBar.setProgressDrawableId(gradientDrawable);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @EsComponentAttribute
    public void show(RangeSeekBar pb, boolean show) {
        if (show) {
            pb.setVisibility(View.VISIBLE);
        } else {
            pb.setVisibility(View.GONE);
        }
    }

    @EsComponentAttribute
    public void progress(RangeSeekBar seekBar, int progress) {
        seekBar.setProgress(progress);
    }

    @EsComponentAttribute
    public void maxProgress(RangeSeekBar seekBar, int max) {
        seekBar.setRange(0, max);
    }

    @EsComponentAttribute
    public void cornerRadius(RangeSeekBar seekBar, int cornerRadius) {
        try {
            seekBar.setProgressRadius(EsProxy.get().getDisplayManager().dp2px(cornerRadius));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispatchFunction(RangeSeekBar view, String functionName, EsArray params, EsPromise esPromise) {
        switch (functionName) {
            case OP_SET_MAX_PROGRESS:
                try {
                    if (view != null) {
                        int maxProgress = params.getInt(0);
                        if (maxProgress >= 0) {
                            view.setRange(0, maxProgress);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SHOW:
                try {
                    if (view != null) {
                        boolean show = params.getBoolean(0);
                        if (show) {
                            view.setVisibility(View.VISIBLE);
                        } else {
                            view.setVisibility(View.GONE);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //不能删
            default:
                super.dispatchFunction(view, functionName, params, esPromise);
                break;
        }
    }
}
