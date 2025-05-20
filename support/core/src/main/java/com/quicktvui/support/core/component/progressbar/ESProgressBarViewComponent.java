package com.quicktvui.support.core.component.progressbar;

import static com.quicktvui.sdk.base.IEsInfo.ES_OP_GET_ES_INFO;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.support.core.R;

import java.lang.reflect.Field;

@ESKitAutoRegister
public class ESProgressBarViewComponent implements IEsComponent<TVProgressBarView> {

    private static final String TAG = "TVProgressBarView";

    protected static final String OP_SET_MAX_PROGRESS = "setMaxProgress";
    protected static final String OP_SET_PROGRESS = "setProgress";
    protected static final String OP_SET_SECOND_PROGRESS = "setSecondProgress";
    protected static final String OP_SHOW = "show";


    @Override
    public TVProgressBarView createView(Context context, EsMap initParams) {
        final TVProgressBarView progressBar = new TVProgressBarView(context, android.R.attr.progressBarStyleHorizontal);
        final LayerDrawable drawable = (LayerDrawable) context.getResources().getDrawable(R.drawable.es_player_progress_bar);
        progressBar.setProgressDrawable(drawable);
        return progressBar;
    }

    @EsComponentAttribute
    public void backgroundColor(TVProgressBarView pb, String backgroundColor) {
        try {
            Drawable progressDrawable = pb.getProgressDrawable();
            if (progressDrawable instanceof LayerDrawable) {
                LayerDrawable drawable = (LayerDrawable) progressDrawable;
                //bg
                GradientDrawable bg = (GradientDrawable) drawable.getDrawable(0);
                int bgColor = Color.parseColor(backgroundColor);
                bg.setColor(bgColor);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @EsComponentAttribute
    public void cornerRadius(TVProgressBarView pb, int cornerRadius) {
        try {
            Drawable progressDrawable = pb.getProgressDrawable();
            if (progressDrawable instanceof LayerDrawable) {
                LayerDrawable drawable = (LayerDrawable) progressDrawable;
                for (int i = 0; i < drawable.getNumberOfLayers(); i++) {

                    final Drawable d = drawable.getDrawable(i);
                    if (d instanceof GradientDrawable) {
                        ((GradientDrawable) d).setCornerRadius(cornerRadius);
                    }
                    //
                    else if (d instanceof ClipDrawable) {
                        GradientDrawable gd = getDrawableFromClipDrawable((ClipDrawable) d);
                        if (gd != null) {
                            gd.setCornerRadius(cornerRadius);
                        }
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @EsComponentAttribute
    public void secondColor(TVProgressBarView pb, String secondColor) {
        try {
            if (TextUtils.isEmpty(secondColor)) {
                return;
            }
            Drawable d = pb.getProgressDrawable();
            if (d instanceof LayerDrawable) {
                LayerDrawable drawable = (LayerDrawable) d;
                ClipDrawable clipDrawable = (ClipDrawable) drawable.getDrawable(1);
                int color = Color.parseColor(secondColor);
                GradientDrawable gd = getDrawableFromClipDrawable(clipDrawable);
                if (gd != null) {
                    gd.setColor(color);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @EsComponentAttribute
    public void color(TVProgressBarView pb, EsMap esMap) {
        try {
            if (esMap == null) {
                return;
            }
            Drawable d = pb.getProgressDrawable();
            if (d instanceof LayerDrawable) {
                LayerDrawable drawable = (LayerDrawable) d;
                ClipDrawable clipDrawable = (ClipDrawable) drawable.getDrawable(2);
                GradientDrawable gd = getDrawableFromClipDrawable(clipDrawable);
                if (gd != null) {
                    int startColor = Color.parseColor((String) esMap.get("startColor"));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        int endColor = Color.parseColor((String) esMap.get("endColor"));
                        int[] colors = new int[]{startColor, endColor};
                        gd.mutate();
                        gd.setColors(colors);
                    } else {
                        gd.setColor(startColor);
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------------------------
    @EsComponentAttribute
    public void show(TVProgressBarView pb, boolean show) {
        if (show) {
            pb.setVisibility(View.VISIBLE);
        } else {
            pb.setVisibility(View.GONE);
        }
    }

    @EsComponentAttribute
    public void progress(TVProgressBarView pb, int progress) {
        pb.setProgress(progress);
    }

    @EsComponentAttribute
    public void secondProgress(TVProgressBarView pb, int progress) {
        pb.setSecondaryProgress(progress);
    }

    @EsComponentAttribute
    public void maxProgress(TVProgressBarView pb, int max) {
        pb.setMax(max);
    }
    //----------------------------------------------------------------------

    @Override
    public void dispatchFunction(TVProgressBarView view, String functionName, EsArray params, EsPromise esPromise) {
        switch (functionName) {
            //getVersion
            case ES_OP_GET_ES_INFO:
                EsMap map = new EsMap();
                try {
                    map.pushInt(IEsInfo.ES_PROP_INFO_VERSION, EsProxy.get().getSdkVersionCode());
                    map.pushDouble(IEsInfo.ES_PROP_INFO_ESKIT_VERSION, EsProxy.get().getEsKitVersionCode());
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(map);
                break;

            case OP_SET_MAX_PROGRESS:
                try {
                    if (view != null) {
                        int maxProgress = params.getInt(0);
                        if (maxProgress >= 0) {
                            view.setMax(maxProgress);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_PROGRESS:
                try {
                    if (view != null) {
                        int progress = params.getInt(0);
                        if (progress >= 0) {
                            view.setProgress(progress);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_SECOND_PROGRESS:
                try {
                    if (view != null) {
                        int progress = params.getInt(0);
                        if (progress >= 0) {
                            view.setSecondaryProgress(progress);
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
            default:
                break;
        }
    }

    private GradientDrawable getDrawableFromClipDrawable(ClipDrawable clipDrawable) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return (GradientDrawable) clipDrawable.getDrawable();
        } else {
            try {
                Field field = clipDrawable.getClass().getSuperclass().getDeclaredField("mDrawable");
                field.setAccessible(true);
                Object o = new Object();
                Object r = field.get(o);
                return null;
            } catch (Throwable t) {
                return null;
            }
        }
    }

    @Override
    public void destroy(TVProgressBarView tvProgressBarView) {

    }
}
