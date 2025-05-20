package com.quicktvui.support.core.component.seekbar;

import static com.quicktvui.sdk.base.IEsInfo.ES_OP_GET_ES_INFO;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.support.core.R;
import com.sunrain.toolkit.utils.log.L;

import java.lang.reflect.Field;

@ESKitAutoRegister
public class ESSeekBarViewComponent implements IEsComponent<TVSeekBarView> {

    protected static final String OP_SET_MAX_PROGRESS = "setMaxProgress";
    protected static final String OP_SET_PROGRESS = "setProgress";
    protected static final String OP_SET_SECOND_PROGRESS = "setSecondProgress";
    protected static final String OP_SHOW = "show";

    private static final String TAG = "TVSeekBarView";

    private LayerDrawable drawable;

    @Override
    public TVSeekBarView createView(Context context, EsMap initParams) {
        final TVSeekBarView seekBar = new TVSeekBarView(context);
        drawable = crateProgressDrawable(context);
        seekBar.setProgressDrawable(drawable);
        Drawable thumb = crateThumbDrawable(context);
        seekBar.setThumb(thumb);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (seekBar instanceof TVSeekBarView && ((TVSeekBarView) seekBar).isListenProgressEvent()) {
                    if (L.DEBUG) {
                        L.logD("#------onProgressChanged-------->>>progress:" + progress + "  fromUser:" + fromUser);
                    }
                    EsMap hippyMap = new EsMap();
                    hippyMap.pushInt("progress", progress);
                    hippyMap.pushBoolean("fromUser", fromUser);
                    EsProxy.get().sendUIEvent(seekBar.getId(), "onSeekBarChange", hippyMap);
                } else {
                    if (L.DEBUG) {
                        L.logD("#------onProgressChanged-----Not  ListenProgressEvent--->>>progress:" + progress + "  fromUser:" + fromUser);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (L.DEBUG) {
                    L.logD("#------onStartTrackingTouch------>>>progress:");
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (L.DEBUG) {
                    L.logD("#------onStopTrackingTouch------>>>progress:");
                }
            }
        });
        return seekBar;
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

    protected Drawable crateThumbDrawable(Context context) {
        return (StateListDrawable) context.getResources().getDrawable(R.drawable.es_seek_thumb);
    }

    protected LayerDrawable crateProgressDrawable(Context context) {
        return (LayerDrawable) context.getResources().getDrawable(R.drawable.es_player_seekbar);
    }

    //-------------------------------------------------------------------------
    @EsComponentAttribute
    public void thumbSize(TVSeekBarView pb, int thumbSize) {
        try {
            Drawable thumbDrawable = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                thumbDrawable = pb.getThumb();
            }
            thumbDrawable.setBounds(0, 0, thumbSize, thumbSize);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @EsComponentAttribute
    public void thumbColor(TVSeekBarView pb, String thumbColor) {
        try {
            if (TextUtils.isEmpty(thumbColor)) {
                return;
            }
            Drawable d = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                d = pb.getThumb();
            }
            if (d instanceof LayerDrawable) {
                LayerDrawable drawable = (LayerDrawable) d;
                ClipDrawable clipDrawable = (ClipDrawable) drawable.getDrawable(1);
                int color = Color.parseColor(thumbColor);
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
    public void backgroundColor(TVSeekBarView pb, String backgroundColor) {
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
    public void cornerRadius(TVSeekBarView pb, int cornerRadius) {
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
    public void secondColor(TVSeekBarView pb, String secondColor) {
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
    public void color(TVSeekBarView pb, EsMap esMap) {
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

    @EsComponentAttribute
    public void listenProgress(TVSeekBarView pb, boolean listen) {
        pb.setListenProgressEvent(listen);
    }

    @EsComponentAttribute
    public void interceptKeyEvent(TVSeekBarView pb, boolean listen) {
        pb.setInterceptKeyEvent(listen);
    }

    @EsComponentAttribute
    public void show(TVSeekBarView pb, boolean show) {
        if (show) {
            pb.setVisibility(View.VISIBLE);
        } else {
            pb.setVisibility(View.GONE);
        }
    }

    @EsComponentAttribute
    public void progress(ProgressBar pb, int progress) {
        pb.setProgress(progress);
    }

    @EsComponentAttribute
    public void secondProgress(ProgressBar pb, int progress) {
        pb.setSecondaryProgress(progress);
    }

    @EsComponentAttribute
    public void maxProgress(ProgressBar pb, int max) {
        pb.setMax(max);
    }

    @EsComponentAttribute
    public void keyProgressIncrement(ProgressBar pb, int number) {
        if (pb instanceof TVSeekBarView) {
            ((TVSeekBarView) pb).setKeyProgressIncrement(number);
        }
    }

    @EsComponentAttribute
    public void cornerRadius(ProgressBar pb, int cornerRadius) {
        try {
            for (int i = 0; i < drawable.getNumberOfLayers(); i++) {
                final Drawable d = drawable.getDrawable(i);
                if (d instanceof GradientDrawable) {
                    ((GradientDrawable) d).setCornerRadius(cornerRadius);
                } else if (d instanceof ClipDrawable) {
                    GradientDrawable gd = getDrawableFromClipDrawable((ClipDrawable) d);
                    if (gd != null) {
                        gd.setCornerRadius(cornerRadius);
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    //----------------------------------------------------------------------

    @Override
    public void dispatchFunction(TVSeekBarView view, String functionName, EsArray params, EsPromise esPromise) {
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

    @Override
    public void destroy(TVSeekBarView tvSeekBarView) {

    }
}
