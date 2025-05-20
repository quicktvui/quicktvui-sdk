package com.quicktvui.support.core.component.rangeseekbar;

import static com.quicktvui.sdk.base.IEsInfo.ES_OP_GET_ES_INFO;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.EsComponentAttribute;
import com.quicktvui.sdk.base.component.IEsComponent;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.display.IDisplayManager;
import com.quicktvui.support.core.utils.GradientDrawableUtils;

import java.util.ArrayList;
import java.util.List;

@ESKitAutoRegister
public class ESHorizontalSeekBarViewComponent implements IEsComponent<RangeSeekBar> {

    private static final String TAG = "ESHorizontalSeekBar";

    //刷新操作
    protected static final String OP_INVALIDATE_RANGE_SEEK_BAR = "invalidateSeekBar";
    protected static final String OP_REQUEST_LAYOUT = "requestLayout";
    protected static final String OP_INVALIDATE = "invalidate";
    protected static final String OP_SET_VISIBLE = "setVisible";

    protected static final String OP_SET_FOCUSABLE = "setFocusable";
    protected static final String OP_SET_CLICKABLE = "setClickable";
    protected static final String OP_REQUEST_FOCUS = "requestFocus";


    protected static final String OP_SET_LEFT_SEEKBAR_VISIBLE = "setLeftSeekBarVisible";
    protected static final String OP_SET_RIGHT_SEEKBAR_VISIBLE = "setRightSeekBarVisible";

    //基本操作
    protected static final String OP_SET_SEEK_BAR_MODE = "setSeekBarMode";
    protected static final String OP_SET_PROGRESS = "setProgress";
    protected static final String OP_GET_PROGRESS = "getProgress";
    protected static final String OP_GET_LEFT_PROGRESS = "getLeftProgress";
    protected static final String OP_GET_RIGHT_PROGRESS = "getRightProgress";
    protected static final String OP_SET_RANGE = "setRange";
    protected static final String OP_SET_GRAVITY = "setGravity";

    //进度条样式
    protected static final String OP_SET_PROGRESS_WIDTH = "setProgressWidth";
    protected static final String OP_SET_PROGRESS_HEIGHT = "setProgressHeight";

    protected static final String OP_SET_PROGRESS_LEFT = "setProgressLeft";
    protected static final String OP_SET_PROGRESS_RIGHT = "setProgressRight";
    protected static final String OP_SET_PROGRESS_TOP = "setProgressTop";
    protected static final String OP_SET_PROGRESS_BOTTOM = "setProgressBottom";


    protected static final String OP_SET_PROGRESS_RADIUS = "setProgressRadius";
    //color
    protected static final String OP_SET_PROGRESS_COLOR = "setProgressColor";
    protected static final String OP_SET_PROGRESS_DEFAULT_COLOR = "setProgressDefaultColor";
    //drawable
    protected static final String OP_SET_PROGRESS_DRAWABLE = "setProgressDrawable";
    protected static final String OP_SET_PROGRESS_DEFAULT_DRAWABLE = "setProgressDefaultDrawable";
    //url
    protected static final String OP_SET_PROGRESS_URL = "setProgressUrl";
    protected static final String OP_SET_PROGRESS_DEFAULT_URL = "setProgressDefaultUrl";

    //提示框样式
    protected static final String OP_SHOW_INDICATOR = "showIndicator";
    protected static final String OP_SHOW_LEFT_INDICATOR = "showLeftIndicator";
    protected static final String OP_SHOW_RIGHT_INDICATOR = "showRightIndicator";

    protected static final String OP_SET_INDICATOR_SHOW_MODE = "setIndicatorShowMode";
    protected static final String OP_SET_LEFT_INDICATOR_SHOW_MODE = "setLeftIndicatorShowMode";
    protected static final String OP_SET_RIGHT_INDICATOR_SHOW_MODE = "setRightIndicatorShowMode";

    //
    protected static final String OP_SET_INDICATOR_WIDTH = "setIndicatorWidth";
    protected static final String OP_SET_LEFT_INDICATOR_WIDTH = "setLeftIndicatorWidth";
    protected static final String OP_SET_RIGHT_INDICATOR_WIDTH = "setRightIndicatorWidth";

    protected static final String OP_SET_INDICATOR_HEIGHT = "setIndicatorHeight";
    protected static final String OP_SET_LEFT_INDICATOR_HEIGHT = "setLeftIndicatorHeight";
    protected static final String OP_SET_RIGHT_INDICATOR_HEIGHT = "setRightIndicatorHeight";

    //
    protected static final String OP_SET_INDICATOR_TEXT_DECIMAL_FORMAT = "setIndicatorTextDecimalFormat";
    protected static final String OP_SET_LEFT_INDICATOR_TEXT_DECIMAL_FORMAT = "setLeftIndicatorTextDecimalFormat";
    protected static final String OP_SET_RIGHT_INDICATOR_TEXT_DECIMAL_FORMAT = "setRightIndicatorTextDecimalFormat";

    protected static final String OP_SET_INDICATOR_TEXT_STRING_FORMAT = "setIndicatorTextStringFormat";
    protected static final String OP_SET_LEFT_INDICATOR_TEXT_STRING_FORMAT = "setLeftIndicatorTextStringFormat";
    protected static final String OP_SET_RIGHT_INDICATOR_TEXT_STRING_FORMAT = "setRightIndicatorTextStringFormat";

    //
    protected static final String OP_SET_INDICATOR_MARGIN = "setIndicatorMargin";
    protected static final String OP_SET_LEFT_INDICATOR_MARGIN = "setLeftIndicatorMargin";
    protected static final String OP_SET_RIGHT_INDICATOR_MARGIN = "setRightIndicatorMargin";

    //
    protected static final String OP_SET_INDICATOR_PADDING_BOTTOM = "setIndicatorPaddingBottom";
    protected static final String OP_SET_LEFT_INDICATOR_PADDING_BOTTOM = "setLeftIndicatorPaddingBottom";
    protected static final String OP_SET_RIGHT_INDICATOR_PADDING_BOTTOM = "setRightIndicatorPaddingBottom";

    protected static final String OP_SET_INDICATOR_PADDING_TOP = "setIndicatorPaddingTop";
    protected static final String OP_SET_LEFT_INDICATOR_PADDING_TOP = "setLeftIndicatorPaddingTop";
    protected static final String OP_SET_RIGHT_INDICATOR_PADDING_TOP = "setRightIndicatorPaddingTop";

    protected static final String OP_SET_INDICATOR_PADDING_LEFT = "setIndicatorPaddingLeft";
    protected static final String OP_SET_LEFT_INDICATOR_PADDING_LEFT = "setLeftIndicatorPaddingLeft";
    protected static final String OP_SET_RIGHT_INDICATOR_PADDING_LEFT = "setRightIndicatorPaddingLeft";

    protected static final String OP_SET_INDICATOR_PADDING_RIGHT = "setIndicatorPaddingRight";
    protected static final String OP_SET_LEFT_INDICATOR_PADDING_RIGHT = "setLeftIndicatorPaddingRight";
    protected static final String OP_SET_RIGHT_INDICATOR_PADDING_RIGHT = "setRightIndicatorPaddingRight";

    protected static final String OP_SET_INDICATOR_BACKGROUND_COLOR = "setIndicatorBackgroundColor";
    protected static final String OP_SET_LEFT_INDICATOR_BACKGROUND_COLOR = "setLeftIndicatorBackgroundColor";
    protected static final String OP_SET_RIGHT_INDICATOR_BACKGROUND_COLOR = "setRightIndicatorBackgroundColor";

    protected static final String OP_SET_INDICATOR_RADIUS = "setIndicatorRadius";
    protected static final String OP_SET_LEFT_INDICATOR_RADIUS = "setLeftIndicatorRadius";
    protected static final String OP_SET_RIGHT_INDICATOR_RADIUS = "setRightIndicatorRadius";

    protected static final String OP_SET_INDICATOR_TEXT_SIZE = "setIndicatorTextSize";
    protected static final String OP_SET_LEFT_INDICATOR_TEXT_SIZE = "setLeftIndicatorTextSize";
    protected static final String OP_SET_RIGHT_INDICATOR_TEXT_SIZE = "setRightIndicatorTextSize";

    protected static final String OP_SET_INDICATOR_TEXT_COLOR = "setIndicatorTextColor";
    protected static final String OP_SET_LEFT_INDICATOR_TEXT_COLOR = "setLeftIndicatorTextColor";
    protected static final String OP_SET_RIGHT_INDICATOR_TEXT_COLOR = "setRightIndicatorTextColor";

    protected static final String OP_SET_INDICATOR_ARROW_SIZE = "setIndicatorArrowSize";
    protected static final String OP_SET_LEFT_INDICATOR_ARROW_SIZE = "setLeftIndicatorArrowSize";
    protected static final String OP_SET_RIGHT_INDICATOR_ARROW_SIZE = "setRightIndicatorArrowSize";

    protected static final String OP_SET_INDICATOR_DRAWABLE = "setIndicatorDrawable";
    protected static final String OP_SET_LEFT_INDICATOR_DRAWABLE = "setLeftIndicatorDrawable";
    protected static final String OP_SET_RIGHT_INDICATOR_DRAWABLE = "setRightIndicatorDrawable";

    protected static final String OP_SET_INDICATOR_URL = "setIndicatorUrl";
    protected static final String OP_SET_LEFT_INDICATOR_URL = "setLeftIndicatorUrl";
    protected static final String OP_SET_RIGHT_INDICATOR_URL = "setRightIndicatorUrl";

    //----------------------------------------按钮样式-------------------------------------
    protected static final String OP_SET_THUMB_WIDTH = "setThumbWidth";
    protected static final String OP_SET_LEFT_THUMB_WIDTH = "setLeftThumbWidth";
    protected static final String OP_SET_RIGHT_THUMB_WIDTH = "setRightThumbWidth";

    protected static final String OP_SET_THUMB_HEIGHT = "setThumbHeight";
    protected static final String OP_SET_LEFT_THUMB_HEIGHT = "setLeftThumbHeight";
    protected static final String OP_SET_RIGHT_THUMB_HEIGHT = "setRightThumbHeight";

    protected static final String OP_SET_THUMB_SCALE_RATIO = "setThumbScaleRatio";
    protected static final String OP_SET_LEFT_THUMB_SCALE_RATIO = "setLeftThumbScaleRatio";
    protected static final String OP_SET_RIGHT_THUMB_SCALE_RATIO = "setRightThumbScaleRatio";

    protected static final String OP_SCALE_THUMB = "scaleThumb";
    protected static final String OP_SCALE_LEFT_THUMB = "scaleLeftThumb";
    protected static final String OP_SCALE_RIGHT_THUMB = "scaleRightThumb";

    protected static final String OP_RESET_THUMB = "resetThumb";
    protected static final String OP_RESET_LEFT_THUMB = "resetLeftThumb";
    protected static final String OP_RESET_RIGHT_THUMB = "resetRightThumb";

    protected static final String OP_SET_THUMB_ACTIVATE = "setThumbActivate";
    protected static final String OP_SET_LEFT_THUMB_ACTIVATE = "setLeftThumbActivate";
    protected static final String OP_SET_RIGHT_THUMB_ACTIVATE = "setRightThumbActivate";

    //
    protected static final String OP_SET_THUMB_DRAWABLE = "setThumbDrawable";
    protected static final String OP_SET_LEFT_THUMB_DRAWABLE = "setLeftThumbDrawable";
    protected static final String OP_SET_RIGHT_THUMB_DRAWABLE = "setRightThumbDrawable";

    protected static final String OP_SET_THUMB_URL = "setThumbUrl";
    protected static final String OP_SET_LEFT_THUMB_URL = "setLeftThumbUrl";
    protected static final String OP_SET_RIGHT_THUMB_URL = "setRightThumbUrl";

    protected static final String OP_SET_THUMB_INACTIVATED_DRAWABLE = "setThumbInactivatedDrawable";
    protected static final String OP_SET_LEFT_THUMB_INACTIVATED_DRAWABLE = "setLeftThumbInactivatedDrawable";
    protected static final String OP_SET_RIGHT_THUMB_INACTIVATED_DRAWABLE = "setRightThumbInactivatedDrawable";

    protected static final String OP_SET_THUMB_INACTIVATED_URL = "setThumbInactivatedUrl";
    protected static final String OP_SET_LEFT_THUMB_INACTIVATED_URL = "setLeftThumbInactivatedUrl";
    protected static final String OP_SET_RIGHT_THUMB_INACTIVATED_URL = "setRightThumbInactivatedUrl";

    //------------------------------------刻度文字样式----------------------------------
    protected static final String OP_SET_TICK_MARK_MODE = "setTickMarkMode";
    protected static final String OP_SET_TICK_MARK_GRAVITY = "setTickMarkGravity";
    protected static final String OP_SET_TICK_MARK_LAYOUT_GRAVITY = "setTickMarkLayoutGravity";
    protected static final String OP_SET_TICK_MARK_TEXT_ARRAY = "setTickMarkTextArray";
    protected static final String OP_SET_TICK_MARK_TEXT_MARGIN = "setTickMarkTextMargin";
    protected static final String OP_SET_TICK_MARK_TEXT_SIZE = "setTickMarkTextSize";
    protected static final String OP_SET_TICK_MARK_TEXT_COLOR = "setTickMarkTextColor";
    protected static final String OP_SET_TICK_MARK_IN_RANGE_TEXT_COLOR = "setTickMarkInRangeTextColor";

    //--------------------------------分步RangeSeekBar--------------------------------
    protected static final String OP_SET_STEPS = "setSteps";
    protected static final String OP_SET_STEPS_WIDTH = "setStepsWidth";
    protected static final String OP_SET_STEPS_HEIGHT = "setStepsHeight";
    protected static final String OP_SET_STEPS_RADIUS = "setStepsRadius";
    protected static final String OP_SET_STEPS_COLOR = "setStepsColor";
    protected static final String OP_SET_STEPS_AUTO_BONDING = "setStepsAutoBonding";
    protected static final String OP_SET_STEPS_DRAWABLE = "setStepsDrawable";
    protected static final String OP_SET_STEPS_URL = "setStepsUrl";

    @Override
    public RangeSeekBar createView(Context context, EsMap initParams) {
        RangeSeekBar rangeSeekBar = getRangeSeekBar(context);
        rangeSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue, boolean isFromUser) {
                //leftValue is left seekbar value, rightValue is right seekbar value
                try {
                    Log.d(TAG, "###########-------onRangeChanged------>>>>>" + (int) leftValue);

                    EsMap hippyMap = new EsMap();
                    hippyMap.pushInt("progress", (int) leftValue);
                    hippyMap.pushInt("leftProgress", (int) leftValue);
                    hippyMap.pushInt("rightProgress", (int) rightValue);
                    hippyMap.pushBoolean("fromUser", isFromUser);
                    EsProxy.get().sendUIEvent(view.getId(), "onSeekBarChange", hippyMap);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStartTrackingTouch(RangeSeekBar view, boolean isLeft) {
                //start tracking touch
                try {
                    Log.d(TAG, "###########-------onStartTrackingTouch------>>>>>" + isLeft);
                    EsMap hippyMap = new EsMap();
                    hippyMap.pushBoolean("isLeftSeekBar", isLeft);
                    EsProxy.get().sendUIEvent(view.getId(), "onStartTrackingTouch", hippyMap);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onStopTrackingTouch(RangeSeekBar view, boolean isLeft) {
                //stop tracking touch
                try {
                    Log.d(TAG, "###########-------onStopTrackingTouch------>>>>>" + isLeft);
                    EsMap hippyMap = new EsMap();
                    hippyMap.pushBoolean("isLeftSeekBar", isLeft);
                    EsProxy.get().sendUIEvent(view.getId(), "onStopTrackingTouch", hippyMap);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        });
        //
        return rangeSeekBar;
    }

    protected RangeSeekBar getRangeSeekBar(Context context) {
        RangeSeekBar rangeSeekBar = new RangeSeekBar(context);
        return rangeSeekBar;
    }

    /**
     * 可焦点
     *
     * @param seekBar
     * @param focusable
     */
    @EsComponentAttribute
    public void focusable(RangeSeekBar seekBar, boolean focusable) {
        try {
            Log.d(TAG, "#-------focusable------>>>>>" + focusable);
            seekBar.setFocusable(focusable);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 可点击
     *
     * @param seekBar
     * @param clickable
     */
    @EsComponentAttribute
    public void clickable(RangeSeekBar seekBar, boolean clickable) {
        try {
            Log.d(TAG, "#-------clickable------>>>>>" + clickable);
            seekBar.setClickable(clickable);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求焦点
     *
     * @param seekBar
     */
    @EsComponentAttribute
    public void thumbActivate(RangeSeekBar seekBar, boolean active) {
        Log.i(TAG,"thumbActivate active :"+active );
        try {
            try {
                seekBar.getLeftSeekBar().setActivate(active);
                //seekBar.setEnableThumbOverlap(active);
                seekBar.getLeftSeekBar().setActivate(active);
                seekBar.invalidateSeekBar();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求焦点
     *
     * @param seekBar
     */
    @EsComponentAttribute
    public void progressHeight(RangeSeekBar seekBar, int progressHeight) {
        Log.i(TAG,"progressHeight progressHeight :"+progressHeight );
        try {
            try {
                IDisplayManager displayManager = EsProxy.get().getDisplayManager();
                seekBar.setProgressHeight((int) displayManager.dp2px(progressHeight));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求焦点
     *
     * @param seekBar
     */
    @EsComponentAttribute
    public void progressRadius(RangeSeekBar seekBar, double progressRadius) {
        Log.i(TAG,"progressRadius progressRadius :"+progressRadius );
        try {
            try {
                IDisplayManager displayManager = EsProxy.get().getDisplayManager();
                seekBar.setProgressRadius(displayManager.dp2px(progressRadius));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 请求焦点
     *
     * @param seekBar
     */
    @EsComponentAttribute
    public void progressColor(RangeSeekBar seekBar, String progressColor) {
        Log.i(TAG,"progressColor progressColor :"+progressColor );
        try {
            try {
                seekBar.setProgressColor(Color.parseColor(progressColor));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }



    /**
     * 请求焦点
     *
     * @param seekBar
     * @param focusable
     */
    @EsComponentAttribute
    public void requestFocus(RangeSeekBar seekBar, boolean focusable) {
        try {
            if (focusable) {
                Log.d(TAG, "#-------requestFocus------>>>>>");
                seekBar.requestFocus();
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispatchFunction(RangeSeekBar rangeSeekBar, String functionName, EsArray params, EsPromise esPromise) {
        Log.d(TAG, "#-------dispatchFunction------>>>>>functionName:" + functionName + "------>params:" + params);
        IDisplayManager displayManager = EsProxy.get().getDisplayManager();
        switch (functionName) {
            case ES_OP_GET_ES_INFO:
                try {
                    EsMap map = new EsMap();
                    try {
                        map.pushInt(IEsInfo.ES_PROP_INFO_VERSION, EsProxy.get().getSdkVersionCode());
                        map.pushDouble(IEsInfo.ES_PROP_INFO_ESKIT_VERSION, EsProxy.get().getEsKitVersionCode());
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                    esPromise.resolve(map);
                } catch (Throwable e) {
                    e.printStackTrace();
                    esPromise.reject(e.getMessage());
                }
                break;
            case OP_SET_FOCUSABLE:
                try {
                    boolean focusable = params.getBoolean(0);
                    Log.d(TAG, "#-------setFocusable------>>>>>" + focusable);
                    rangeSeekBar.setFocusable(focusable);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_CLICKABLE:
                try {
                    boolean clickable = params.getBoolean(0);
                    Log.d(TAG, "#-------setClickable------>>>>>" + clickable);
                    rangeSeekBar.setClickable(clickable);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_REQUEST_FOCUS:
                try {
                    boolean focused = rangeSeekBar.requestFocus();
                    Log.d(TAG, "#-------requestFocus------>>>>>" + focused);
                    if (esPromise != null) {
                        esPromise.resolve(focused);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    if (esPromise != null) {
                        esPromise.reject(e.getMessage());
                    }
                }
                break;
            case OP_INVALIDATE:
                try {
                    rangeSeekBar.invalidate();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;

            case OP_REQUEST_LAYOUT:
                try {
                    rangeSeekBar.requestLayout();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_VISIBLE:
                try {
                    boolean visible = params.getBoolean(0);
                    rangeSeekBar.setVisibility(visible ? View.VISIBLE : View.GONE);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_LEFT_SEEKBAR_VISIBLE:
                try {
                    boolean visible = params.getBoolean(0);
                    rangeSeekBar.getLeftSeekBar().setVisible(visible);
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                break;
            case OP_SET_RIGHT_SEEKBAR_VISIBLE:
                try {
                    boolean visible = params.getBoolean(0);
                    rangeSeekBar.getRightSeekBar().setVisible(visible);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_INVALIDATE_RANGE_SEEK_BAR:
                try {
                    rangeSeekBar.invalidateSeekBar();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_SEEK_BAR_MODE:
                try {
                    int mode = params.getInt(0);
                    if (mode > 0) {
                        rangeSeekBar.setSeekBarMode(mode);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_PROGRESS:
                try {
                    long leftValue = params.getLong(0);
                    if (params.size() >= 2) {
                        long rightValue = params.getLong(1);
                        rangeSeekBar.setProgress(leftValue, rightValue);
                    } else {
                        rangeSeekBar.setProgress(leftValue);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_GET_PROGRESS:
            case OP_GET_LEFT_PROGRESS:
                try {
                    float progress = rangeSeekBar.getLeftSeekBar().getProgress();
                    esPromise.resolve(progress);
                } catch (Throwable e) {
                    e.printStackTrace();
                    esPromise.reject(-1);
                }
                break;
            case OP_GET_RIGHT_PROGRESS:
                try {
                    float progress = rangeSeekBar.getRightSeekBar().getProgress();
                    esPromise.resolve(progress);
                } catch (Throwable e) {
                    e.printStackTrace();
                    esPromise.reject(-1);
                }
                break;
            case OP_SET_RANGE:
                try {
                    long min = params.getLong(0);
                    long max = params.getLong(1);
                    if (params.size() >= 3) {
                        long minInterval = params.getLong(2);
                        rangeSeekBar.setRange(min, max, minInterval);
                    } else {
                        rangeSeekBar.setRange(min, max);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_GRAVITY:
                try {
                    int gravity = params.getInt(0);
                    rangeSeekBar.setGravity(gravity);
                    rangeSeekBar.invalidateSeekBar();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //---------------------------------------------------
            case OP_SET_PROGRESS_WIDTH:
                try {
                    int progressWidth = params.getInt(0);
                    rangeSeekBar.setProgressWidth((int) displayManager.dp2px(progressWidth));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_PROGRESS_HEIGHT:
                try {
                    int progressHeight = params.getInt(0);
                    rangeSeekBar.setProgressHeight((int) displayManager.dp2px(progressHeight));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //---------------------------------------------------
            case OP_SET_PROGRESS_LEFT:
                try {
                    int progressLeft = params.getInt(0);
                    rangeSeekBar.setProgressLeft((int) displayManager.dp2px(progressLeft));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_PROGRESS_RIGHT:
                try {
                    int progressRight = params.getInt(0);
                    rangeSeekBar.setProgressRight((int) displayManager.dp2px(progressRight));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_PROGRESS_TOP:
                try {
                    int progressTop = params.getInt(0);
                    rangeSeekBar.setProgressTop((int) displayManager.dp2px(progressTop));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_PROGRESS_BOTTOM:
                try {
                    int progressBottom = params.getInt(0);
                    rangeSeekBar.setProgressBottom((int) displayManager.dp2px(progressBottom));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //---------------------------------------------------
            case OP_SET_PROGRESS_RADIUS:
                try {
                    double progressRadius = params.getDouble(0);
                    rangeSeekBar.setProgressRadius(displayManager.dp2px(progressRadius));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_PROGRESS_COLOR:
                try {
                    int progressColor = params.getInt(0);
                    rangeSeekBar.setProgressColor(progressColor);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_PROGRESS_DEFAULT_COLOR:
                try {
                    int progressDefaultColor = params.getInt(0);
                    rangeSeekBar.setProgressDefaultColor(progressDefaultColor);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------

            case OP_SET_PROGRESS_DRAWABLE:
                try {
                    EsMap progressDrawableId = params.getMap(0);
                    GradientDrawable gradientDrawable =//
                            GradientDrawableUtils.createGradientDrawable(progressDrawableId);
                    if (gradientDrawable != null) {
                        rangeSeekBar.setProgressDrawableId(gradientDrawable);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;

            case OP_SET_PROGRESS_DEFAULT_DRAWABLE:
                try {
                    EsMap progressDefaultDrawableId = params.getMap(0);
                    GradientDrawable gradientDrawable =//
                            GradientDrawableUtils.createGradientDrawable(progressDefaultDrawableId);
                    if (gradientDrawable != null) {
                        rangeSeekBar.setProgressDefaultDrawableId(gradientDrawable);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_PROGRESS_URL:
                try {
                    String progressUrl = params.getString(0);
                    rangeSeekBar.setProgressUrl(this, progressUrl);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_PROGRESS_DEFAULT_URL:
                try {
                    String progressDefaultUrl = params.getString(0);
                    rangeSeekBar.setProgressDefaultUrl(this, progressDefaultUrl);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;


            //----------------------------------------------------
            case OP_SHOW_INDICATOR:
            case OP_SHOW_LEFT_INDICATOR:
                try {
                    boolean isShown = params.getBoolean(0);
                    rangeSeekBar.getLeftSeekBar().showIndicator(isShown);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SHOW_RIGHT_INDICATOR:
                try {
                    boolean isShown = params.getBoolean(0);
                    rangeSeekBar.getRightSeekBar().showIndicator(isShown);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_INDICATOR_SHOW_MODE:
            case OP_SET_LEFT_INDICATOR_SHOW_MODE:
                try {
                    int indicatorShowMode = params.getInt(0);
                    rangeSeekBar.getLeftSeekBar().setIndicatorShowMode(indicatorShowMode);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_INDICATOR_SHOW_MODE:
                try {
                    int indicatorShowMode = params.getInt(0);
                    rangeSeekBar.getRightSeekBar().setIndicatorShowMode(indicatorShowMode);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
//----------------------------------------------------
            case OP_SET_INDICATOR_WIDTH:
            case OP_SET_LEFT_INDICATOR_WIDTH:
                try {
                    int indicatorWidth = params.getInt(0);
                    rangeSeekBar.getLeftSeekBar().setIndicatorWidth((int) displayManager.dp2px(indicatorWidth));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_INDICATOR_WIDTH:
                try {
                    int indicatorHeight = params.getInt(0);
                    rangeSeekBar.getRightSeekBar().setIndicatorWidth((int) displayManager.dp2px(indicatorHeight));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_INDICATOR_HEIGHT:
            case OP_SET_LEFT_INDICATOR_HEIGHT:
                try {
                    int indicatorHeight = params.getInt(0);
                    rangeSeekBar.getLeftSeekBar().setIndicatorHeight((int) displayManager.dp2px(indicatorHeight));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_INDICATOR_HEIGHT:
                try {
                    int indicatorHeight = params.getInt(0);
                    rangeSeekBar.getRightSeekBar().setIndicatorHeight((int) displayManager.dp2px(indicatorHeight));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
//----------------------------------------------------
            case OP_SET_INDICATOR_TEXT_DECIMAL_FORMAT:
                try {
                    String formatPattern = params.getString(0);
                    rangeSeekBar.setIndicatorTextDecimalFormat(formatPattern);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_LEFT_INDICATOR_TEXT_DECIMAL_FORMAT:
                try {
                    String formatPattern = params.getString(0);
                    rangeSeekBar.getLeftSeekBar().setIndicatorTextDecimalFormat(formatPattern);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_INDICATOR_TEXT_DECIMAL_FORMAT:
                try {
                    String formatPattern = params.getString(0);
                    rangeSeekBar.getRightSeekBar().setIndicatorTextDecimalFormat(formatPattern);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_INDICATOR_TEXT_STRING_FORMAT:
                try {
                    String formatPattern = params.getString(0);
                    rangeSeekBar.setIndicatorTextStringFormat(formatPattern);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_LEFT_INDICATOR_TEXT_STRING_FORMAT:
                try {
                    String formatPattern = params.getString(0);
                    rangeSeekBar.getLeftSeekBar().setIndicatorTextStringFormat(formatPattern);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_INDICATOR_TEXT_STRING_FORMAT:
                try {
                    String formatPattern = params.getString(0);
                    rangeSeekBar.getRightSeekBar().setIndicatorTextStringFormat(formatPattern);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_INDICATOR_MARGIN:
            case OP_SET_LEFT_INDICATOR_MARGIN:
                try {
                    int indicatorMargin = params.getInt(0);
                    rangeSeekBar.getLeftSeekBar().setIndicatorMargin((int) displayManager.dp2px(indicatorMargin));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_INDICATOR_MARGIN:
                try {
                    int indicatorMargin = params.getInt(0);
                    rangeSeekBar.getRightSeekBar().setIndicatorMargin((int) displayManager.dp2px(indicatorMargin));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_INDICATOR_PADDING_BOTTOM:
            case OP_SET_LEFT_INDICATOR_PADDING_BOTTOM:
                try {
                    int indicatorPaddingBottom = params.getInt(0);
                    rangeSeekBar.getLeftSeekBar().setIndicatorPaddingBottom((int) displayManager.dp2px(indicatorPaddingBottom));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_INDICATOR_PADDING_BOTTOM:
                try {
                    int indicatorPaddingBottom = params.getInt(0);
                    rangeSeekBar.getRightSeekBar().setIndicatorPaddingBottom((int) displayManager.dp2px(indicatorPaddingBottom));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_INDICATOR_PADDING_TOP:
            case OP_SET_LEFT_INDICATOR_PADDING_TOP:
                try {
                    int indicatorPaddingTop = params.getInt(0);
                    rangeSeekBar.getLeftSeekBar().setIndicatorPaddingTop((int) displayManager.dp2px(indicatorPaddingTop));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_INDICATOR_PADDING_TOP:
                try {
                    int indicatorPaddingTop = params.getInt(0);
                    rangeSeekBar.getRightSeekBar().setIndicatorPaddingTop((int) displayManager.dp2px(indicatorPaddingTop));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_INDICATOR_PADDING_LEFT:
            case OP_SET_LEFT_INDICATOR_PADDING_LEFT:
                try {
                    int indicatorPaddingLeft = params.getInt(0);
                    rangeSeekBar.getLeftSeekBar().setIndicatorPaddingLeft((int) displayManager.dp2px(indicatorPaddingLeft));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_INDICATOR_PADDING_LEFT:
                try {
                    int indicatorPaddingLeft = params.getInt(0);
                    rangeSeekBar.getRightSeekBar().setIndicatorPaddingLeft((int) displayManager.dp2px(indicatorPaddingLeft));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_INDICATOR_PADDING_RIGHT:
            case OP_SET_LEFT_INDICATOR_PADDING_RIGHT:
                try {
                    int indicatorPaddingRight = params.getInt(0);
                    rangeSeekBar.getLeftSeekBar().setIndicatorPaddingRight((int) displayManager.dp2px(indicatorPaddingRight));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_INDICATOR_PADDING_RIGHT:
                try {
                    int indicatorPaddingRight = params.getInt(0);
                    rangeSeekBar.getRightSeekBar().setIndicatorPaddingRight((int) displayManager.dp2px(indicatorPaddingRight));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_INDICATOR_BACKGROUND_COLOR:
            case OP_SET_LEFT_INDICATOR_BACKGROUND_COLOR:
                try {
                    int indicatorBackgroundColor = params.getInt(0);
                    rangeSeekBar.getLeftSeekBar().setIndicatorBackgroundColor(indicatorBackgroundColor);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_INDICATOR_BACKGROUND_COLOR:
                try {
                    int indicatorBackgroundColor = params.getInt(0);
                    rangeSeekBar.getRightSeekBar().setIndicatorBackgroundColor(indicatorBackgroundColor);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_INDICATOR_RADIUS:
            case OP_SET_LEFT_INDICATOR_RADIUS:
                try {
                    double indicatorRadius = params.getDouble(0);
                    rangeSeekBar.getLeftSeekBar().setIndicatorRadius(displayManager.dp2px(indicatorRadius));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_INDICATOR_RADIUS:
                try {
                    double indicatorRadius = params.getDouble(0);
                    rangeSeekBar.getRightSeekBar().setIndicatorRadius(displayManager.dp2px(indicatorRadius));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;

            //----------------------------------------------------
            case OP_SET_INDICATOR_TEXT_SIZE:
            case OP_SET_LEFT_INDICATOR_TEXT_SIZE:
                try {
                    int indicatorTextSize = params.getInt(0);
                    rangeSeekBar.getLeftSeekBar().setIndicatorTextSize((int) displayManager.sp2px(indicatorTextSize));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_INDICATOR_TEXT_SIZE:
                try {
                    int indicatorTextSize = params.getInt(0);
                    rangeSeekBar.getRightSeekBar().setIndicatorTextSize((int) displayManager.sp2px(indicatorTextSize));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_INDICATOR_TEXT_COLOR:
            case OP_SET_LEFT_INDICATOR_TEXT_COLOR:
                try {
                    int indicatorTextColor = params.getInt(0);
                    rangeSeekBar.getLeftSeekBar().setIndicatorTextColor(indicatorTextColor);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_INDICATOR_TEXT_COLOR:
                try {
                    int indicatorTextColor = params.getInt(0);
                    rangeSeekBar.getRightSeekBar().setIndicatorTextColor(indicatorTextColor);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_INDICATOR_ARROW_SIZE:
            case OP_SET_LEFT_INDICATOR_ARROW_SIZE:
                try {
                    int indicatorArrowSize = params.getInt(0);
                    rangeSeekBar.getLeftSeekBar().setIndicatorArrowSize((int) displayManager.dp2px(indicatorArrowSize));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_INDICATOR_ARROW_SIZE:
                try {
                    int indicatorArrowSize = params.getInt(0);
                    rangeSeekBar.getRightSeekBar().setIndicatorArrowSize((int) displayManager.dp2px(indicatorArrowSize));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_INDICATOR_DRAWABLE:
            case OP_SET_LEFT_INDICATOR_DRAWABLE:
                try {
                    EsMap progressDrawableId = params.getMap(0);
                    GradientDrawable gradientDrawable =//
                            GradientDrawableUtils.createGradientDrawable(progressDrawableId);
                    if (gradientDrawable != null) {
                        rangeSeekBar.getLeftSeekBar().setIndicatorDrawableId(gradientDrawable);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_INDICATOR_DRAWABLE:
                try {
                    EsMap progressDrawableId = params.getMap(0);
                    GradientDrawable gradientDrawable =//
                            GradientDrawableUtils.createGradientDrawable(progressDrawableId);
                    if (gradientDrawable != null) {
                        rangeSeekBar.getRightSeekBar().setIndicatorDrawableId(gradientDrawable);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_INDICATOR_URL:
            case OP_SET_LEFT_INDICATOR_URL:
                try {
                    String url = params.getString(0);
                    rangeSeekBar.getLeftSeekBar().setIndicatorUrl(this, url);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_INDICATOR_URL:
                try {
                    String url = params.getString(0);
                    rangeSeekBar.getRightSeekBar().setIndicatorUrl(this, url);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_THUMB_WIDTH:
            case OP_SET_LEFT_THUMB_WIDTH:
                try {
                    int thumbWidth = params.getInt(0);
                    rangeSeekBar.getLeftSeekBar().setThumbWidth((int) displayManager.dp2px(thumbWidth));
                    rangeSeekBar.invalidateSeekBar();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_THUMB_WIDTH:
                try {
                    int thumbWidth = params.getInt(0);
                    rangeSeekBar.getRightSeekBar().setThumbWidth((int) displayManager.dp2px(thumbWidth));
                    rangeSeekBar.invalidateSeekBar();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_THUMB_HEIGHT:
            case OP_SET_LEFT_THUMB_HEIGHT:
                try {
                    int thumbHeight = params.getInt(0);
                    rangeSeekBar.getLeftSeekBar().setThumbHeight((int) displayManager.dp2px(thumbHeight));
                    rangeSeekBar.invalidateSeekBar();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_THUMB_HEIGHT:
                try {
                    int thumbHeight = params.getInt(0);
                    rangeSeekBar.getRightSeekBar().setThumbHeight((int) displayManager.dp2px(thumbHeight));
                    rangeSeekBar.invalidateSeekBar();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_THUMB_SCALE_RATIO:
            case OP_SET_LEFT_THUMB_SCALE_RATIO:
                try {
                    double thumbScaleRatio = params.getDouble(0);
                    rangeSeekBar.getLeftSeekBar().setThumbScaleRatio(displayManager.dp2px(thumbScaleRatio));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_THUMB_SCALE_RATIO:
                try {
                    double thumbScaleRatio = params.getDouble(0);
                    rangeSeekBar.getRightSeekBar().setThumbScaleRatio(displayManager.dp2px(thumbScaleRatio));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //------------------------------------------------------------
            case OP_SCALE_THUMB:
            case OP_SCALE_LEFT_THUMB:
                try {
                    rangeSeekBar.getLeftSeekBar().scaleThumb();
                    rangeSeekBar.invalidateSeekBar();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SCALE_RIGHT_THUMB:
                try {
                    rangeSeekBar.getRightSeekBar().scaleThumb();
                    rangeSeekBar.invalidateSeekBar();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //------------------------------------------------------------
            case OP_RESET_THUMB:
            case OP_RESET_LEFT_THUMB:
                try {
                    rangeSeekBar.getLeftSeekBar().resetThumb();
                    rangeSeekBar.invalidateSeekBar();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_RESET_RIGHT_THUMB:
                try {
                    rangeSeekBar.getRightSeekBar().resetThumb();
                    rangeSeekBar.invalidateSeekBar();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //------------------------------------------------------------
            case OP_SET_THUMB_ACTIVATE:
            case OP_SET_LEFT_THUMB_ACTIVATE:
                try {
                    boolean activate = params.getBoolean(0);
                    rangeSeekBar.getLeftSeekBar().setActivate(activate);
                    rangeSeekBar.invalidateSeekBar();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_THUMB_ACTIVATE:
                try {
                    boolean activate = params.getBoolean(0);
                    rangeSeekBar.getRightSeekBar().setActivate(activate);
                    rangeSeekBar.invalidateSeekBar();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_THUMB_DRAWABLE:
            case OP_SET_LEFT_THUMB_DRAWABLE:
                try {
                    EsMap drawable = params.getMap(0);
                    GradientDrawable gradientDrawable =//
                            GradientDrawableUtils.createGradientDrawable(drawable);
                    if (gradientDrawable != null) {
                        rangeSeekBar.getLeftSeekBar().setThumbDrawableId(gradientDrawable);
                        rangeSeekBar.invalidateSeekBar();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_THUMB_DRAWABLE:
                try {
                    EsMap drawable = params.getMap(0);
                    GradientDrawable gradientDrawable =//
                            GradientDrawableUtils.createGradientDrawable(drawable);
                    if (gradientDrawable != null) {
                        rangeSeekBar.getRightSeekBar().setThumbDrawableId(gradientDrawable);
                        rangeSeekBar.invalidateSeekBar();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;

            //----------------------------------------------------
            case OP_SET_THUMB_URL:
            case OP_SET_LEFT_THUMB_URL:
                try {
                    String url = params.getString(0);
                    rangeSeekBar.getLeftSeekBar().setThumbUrl(url);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_THUMB_URL:
                try {
                    String url = params.getString(0);
                    rangeSeekBar.getRightSeekBar().setThumbUrl(url);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;

            //----------------------------------------------------
            case OP_SET_THUMB_INACTIVATED_DRAWABLE:
            case OP_SET_LEFT_THUMB_INACTIVATED_DRAWABLE:
                try {
                    EsMap drawableId = params.getMap(0);
                    GradientDrawable gradientDrawable =//
                            GradientDrawableUtils.createGradientDrawable(drawableId);
                    if (gradientDrawable != null) {
                        rangeSeekBar.getLeftSeekBar().setThumbInactivatedDrawableId(gradientDrawable);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_THUMB_INACTIVATED_DRAWABLE:
                try {
                    EsMap drawableId = params.getMap(0);
                    GradientDrawable gradientDrawable =//
                            GradientDrawableUtils.createGradientDrawable(drawableId);
                    if (gradientDrawable != null) {
                        rangeSeekBar.getRightSeekBar().setThumbInactivatedDrawableId(gradientDrawable);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_THUMB_INACTIVATED_URL:
            case OP_SET_LEFT_THUMB_INACTIVATED_URL:
                try {
                    String url = params.getString(0);
                    rangeSeekBar.getLeftSeekBar().setThumbInactivatedUrl(url);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_THUMB_INACTIVATED_URL:
                try {
                    String url = params.getString(0);
                    rangeSeekBar.getRightSeekBar().setThumbInactivatedUrl(url);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------TICK------------------------------
            case OP_SET_TICK_MARK_MODE:
                try {
                    int tickMarkMode = params.getInt(0);
                    rangeSeekBar.setTickMarkMode(tickMarkMode);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_TICK_MARK_GRAVITY:
                try {
                    int tickMarkGravity = params.getInt(0);
                    rangeSeekBar.setTickMarkGravity(tickMarkGravity);
                    rangeSeekBar.invalidateSeekBar();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_TICK_MARK_LAYOUT_GRAVITY:
                try {
                    int tickMarkLayoutGravity = params.getInt(0);
                    rangeSeekBar.setTickMarkLayoutGravity(tickMarkLayoutGravity);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_TICK_MARK_TEXT_ARRAY:
                try {
                    EsArray tickMarkTextArray = params.getArray(0);
                    if (tickMarkTextArray != null && tickMarkTextArray.size() > 0) {
                        CharSequence[] textArray = new CharSequence[tickMarkTextArray.size()];
                        for (int i = 0; i < tickMarkTextArray.size(); i++) {
                            String text = tickMarkTextArray.getString(i);
                            textArray[i] = text;
                        }
                        rangeSeekBar.setTickMarkTextArray(textArray);
                        rangeSeekBar.invalidateSeekBar();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_TICK_MARK_TEXT_MARGIN:
                try {
                    int tickMarkTextMargin = params.getInt(0);
                    rangeSeekBar.setTickMarkTextMargin((int) displayManager.sp2px(tickMarkTextMargin));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_TICK_MARK_TEXT_SIZE:
                try {
                    int tickMarkTextSize = params.getInt(0);
                    rangeSeekBar.setTickMarkTextSize((int) displayManager.sp2px(tickMarkTextSize));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_TICK_MARK_TEXT_COLOR:
                try {
                    int tickMarkTextColor = params.getInt(0);
                    rangeSeekBar.setTickMarkTextColor(tickMarkTextColor);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_TICK_MARK_IN_RANGE_TEXT_COLOR:
                try {
                    int tickMarkInRangeTextColor = params.getInt(0);
                    rangeSeekBar.setTickMarkInRangeTextColor(tickMarkInRangeTextColor);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_STEPS:
                try {
                    int steps = params.getInt(0);
                    rangeSeekBar.setSteps(steps);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_STEPS_WIDTH:
                try {
                    int stepsWidth = params.getInt(0);
                    rangeSeekBar.setStepsWidth((int) displayManager.dp2px(stepsWidth));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_STEPS_HEIGHT:
                try {
                    int stepsHeight = params.getInt(0);
                    rangeSeekBar.setStepsHeight((int) displayManager.dp2px(stepsHeight));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_STEPS_RADIUS:
                try {
                    int stepsRadius = params.getInt(0);
                    rangeSeekBar.setStepsRadius((int) displayManager.dp2px(stepsRadius));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_STEPS_COLOR:
                try {
                    int stepsColor = params.getInt(0);
                    rangeSeekBar.setStepsColor(stepsColor);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_STEPS_AUTO_BONDING:
                try {
                    boolean stepsAutoBonding = params.getBoolean(0);
                    rangeSeekBar.setStepsAutoBonding(stepsAutoBonding);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------------
            case OP_SET_STEPS_DRAWABLE:
                try {
                    EsArray drawableIdList = params.getArray(0);
                    if (drawableIdList != null && drawableIdList.size() > 0) {
                        List<Drawable> drawableList = new ArrayList<>(drawableIdList.size());
                        for (int i = 0; i < drawableIdList.size(); i++) {
                            EsMap drawableMap = drawableIdList.getMap(i);
                            if (drawableMap != null) {
                                GradientDrawable gradientDrawable =//
                                        GradientDrawableUtils.createGradientDrawable(drawableMap);
                                if (gradientDrawable != null) {
                                    drawableList.add(gradientDrawable);
                                }
                            }
                        }
                        rangeSeekBar.setStepsDrawable(drawableList);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_STEPS_URL:
                try {
                    EsArray urlList = params.getArray(0);
                    if (urlList != null && urlList.size() > 0) {
                        List<String> drawableList = new ArrayList<>(urlList.size());
                        for (int i = 0; i < urlList.size(); i++) {
                            String url = urlList.getString(i);
                            if (!TextUtils.isEmpty(url)) {
                                drawableList.add(url);
                            }
                        }
                        rangeSeekBar.setStepsUrl(drawableList);
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
    public void destroy(RangeSeekBar tvSeekBarView) {

    }
}
