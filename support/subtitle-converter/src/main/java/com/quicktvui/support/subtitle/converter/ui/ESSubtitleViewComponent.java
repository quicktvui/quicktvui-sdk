package com.quicktvui.support.subtitle.converter.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;
import com.tencent.mtt.hippy.utils.PixelUtil;


@ESKitAutoRegister
public class ESSubtitleViewComponent implements IEsComponent<ESSubtitleView> {

    private static final String TAG = "ESSubtitleViewComponent";

    @Override
    public ESSubtitleView createView(Context context, EsMap params) {
        Log.w(TAG, "crateView");
        ESSubtitleView esSubtitleView = new ESSubtitleView(context);
        if (params.containsKey("backgroundColor")) {
            String backgroundColor = params.getString("backgroundColor");
            esSubtitleView.setBackgroundColor(Color.parseColor(backgroundColor));
        }
        return esSubtitleView;
    }

    @Override
    public void dispatchFunction(ESSubtitleView view, String eventName, EsArray params, EsPromise promise) {
        Log.w(TAG, "dispatchFunction " + eventName);
        switch (eventName) {
            case "setText":
                view.setText(params.getString(0));
                break;
            case "setTextSize":
                view.setTextSize(params.getInt(0));
                break;
            case "setTextColor":
                int colorInt = Color.parseColor(params.getString(0));
                view.setTextColor(ColorStateList.valueOf(colorInt));
                break;
        }
    }

    @EsComponentAttribute
    public void text(ESSubtitleView view, String text) {
        if (view != null) {
            view.setText(text);
        }
    }

    @EsComponentAttribute
    public void fontSize(ESSubtitleView view, int size) {
        if (view != null) {
            view.setTextSize(size);
        }
    }

    @EsComponentAttribute
    public void typeface(ESSubtitleView view, String typeFace) {
        if (view != null) {
            view.setTypeStyle(typeFace);
        }
    }

    @EsComponentAttribute
    public void gravity(ESSubtitleView view, String gravity) {
        if (view != null) {
            if (gravity.contains("|")) {
                final String[] gs = gravity.split("\\|");
                final int gravity1 = getGravity(gs[0]);
                final int gravity2 = getGravity(gs[1]);
                view.setGravity(gravity1 | gravity2);
            } else {
                view.setGravity(getGravity(gravity));
            }
        }
    }

    @EsComponentAttribute
    public void textSize(ESSubtitleView view, int size) {
        if (view != null) {
            view.setTextSize(TypedValue.COMPLEX_UNIT_PX, (int) Math.ceil(PixelUtil.dp2px(size)));
        }
    }

    @EsComponentAttribute
    public void paddingRect(ESSubtitleView view, EsArray array) {
        if (view != null) {
            if (array == null) {
                view.setPadding(0, 0, 0, 0);
            } else {
                view.setPadding(0,0,0,0);
                view.setPadding((int) PixelUtil.dp2px(array.getInt(0)),
                        (int) PixelUtil.dp2px(array.getInt(1)),
                        (int) PixelUtil.dp2px(array.getInt(2)),
                        (int) PixelUtil.dp2px(array.getInt(3)));
            }
        }
    }

    @EsComponentAttribute
    public void ellipsizeMode(ESSubtitleView view, int where) {
        if (view != null) {
            switch (where) {
                case 0:
                    view.setEllipsize(TextUtils.TruncateAt.START);
                    break;
                case 1:
                    view.setEllipsize(TextUtils.TruncateAt.MIDDLE);
                    break;
                case 2: {
                    view.setEllipsize(TextUtils.TruncateAt.END);
                    break;
                }
                case 3: {
                    view.setEllipsize(TextUtils.TruncateAt.MARQUEE);
                    view.setSingleLine();
                    view.setMarqueeRepeatLimit(-1);
                    break;
                }
            }
        }
    }

    @EsComponentAttribute
    public void lines(ESSubtitleView view, int lines) {
        if (view != null) {
            view.setLines(lines);
        }
    }

    @EsComponentAttribute
    public void maxLines(ESSubtitleView view, int lines) {
        if (view != null) {
            view.setMaxLines(lines);
        }
    }

    @EsComponentAttribute
    public void textColor(ESSubtitleView view, String color) {
        if (view != null) {
            final int colorInt = Color.parseColor(color);
            if (view != null) {
                view.setTextColor(ColorStateList.valueOf(colorInt));
            }
        }
    }

    @EsComponentAttribute
    public void select(ESSubtitleView view, boolean enable) {
        if (view != null) {
            view.setSelected(enable);
        }
    }

    private int getGravity(String gravity) {
        switch (gravity) {
            case "center":
                return Gravity.CENTER;
            case "top":
                return Gravity.TOP;
            case "bottom":
                return Gravity.BOTTOM;
            case "end":
                return Gravity.END;
            case "centerHorizontal":
                return Gravity.CENTER_HORIZONTAL;
            case "centerVertical":
                return Gravity.CENTER_VERTICAL;
            case "start":
            default:
                return Gravity.START;
        }
    }

    @Override
    public void destroy(ESSubtitleView view) {

    }
}
