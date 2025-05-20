package com.quicktvui.support.core.component.progress;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.IEsComponent;
import com.tencent.mtt.hippy.utils.PixelUtil;

@ESKitAutoRegister
public class ESProgressWheelComponent implements IEsComponent<ProgressWheel> {

    private static final String TAG = "ESProgressWheel";

    protected boolean debuggable = true;

    protected static final String OP_INCREMENT_PROGRESS = "incrementProgress";
    protected static final String OP_IS_SPINNING = "isSpinning";
    protected static final String OP_RESET_COUNT = "resetCount";
    protected static final String OP_SET_BAR_COLOR = "setBarColor";
    protected static final String OP_SET_BAR_LENGTH = "setBarLength";
    protected static final String OP_SET_BAR_WIDTH = "setBarWidth";
    protected static final String OP_SET_BAR_STYLE = "setBarStyle";
    protected static final String OP_SET_CIRCLE_COLOR = "setCircleColor";
    protected static final String OP_SET_CIRCLE_RADIUS = "setCircleRadius";
    protected static final String OP_SET_CONTOUR_COLOR = "setContourColor";
    protected static final String OP_SET_CONTOUR_ENABLED = "setContourEnabled";
    protected static final String OP_SET_CONTOUR_SIZE = "setContourSize";
    protected static final String OP_SET_DELAY_MILLIS = "setDelayMillis";
    protected static final String OP_SET_PADDING_TOP = "setPaddingTop";
    protected static final String OP_SET_PADDING_BOTTOM = "setPaddingBottom";
    protected static final String OP_SET_PADDING_LEFT = "setPaddingLeft";
    protected static final String OP_SET_PADDING_RIGHT = "setPaddingRight";
    protected static final String OP_SET_PROGRESS = "setProgress";
    protected static final String OP_SET_RIM_COLOR = "setRimColor";
    protected static final String OP_SET_RIM_WIDTH = "setRimWidth";
    protected static final String OP_SET_RIM_ENABLED = "setRimEnabled";
    protected static final String OP_SET_SPIN_SPEED = "setSpinSpeed";
    protected static final String OP_SET_TEXT = "setText";
    protected static final String OP_SET_TEXT_COLOR = "setTextColor";
    protected static final String OP_SET_TEXT_SIZE = "setTextSize";
    protected static final String OP_STOP_SPINNING = "stopSpinning";
    protected static final String OP_START_SPINNING = "startSpinning";
    protected static final String OP_SHOW = "show";

    @Override
    public ProgressWheel createView(Context context, EsMap initParams) {
        ProgressWheel progressWheel = new ProgressWheel(context);
        return progressWheel;
    }

    @Override
    public void dispatchFunction(ProgressWheel progressWheel, String functionName, EsArray esArray,
                                 EsPromise esPromise) {
        switch (functionName) {
            case OP_INCREMENT_PROGRESS:
                try {
                    int amount = esArray.getInt(0);
                    if (debuggable) {
                        Log.d(TAG, "=========incrementProgress===========>>>>" + amount);
                    }
                    progressWheel.incrementProgress(amount);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;

            case OP_IS_SPINNING:
                try {
                    boolean ret = progressWheel.isSpinning();
                    esPromise.resolve(ret);
                    if (debuggable) {
                        Log.d(TAG, "=========isSpinning===========>>>>" + ret);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                    esPromise.resolve(false);
                }
                break;
            case OP_RESET_COUNT:
                try {
                    progressWheel.resetCount();
                    if (debuggable) {
                        Log.d(TAG, "=========resetCount===========>>>>");
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;
            case OP_SET_BAR_COLOR:
                try {
                    String barColor = esArray.getString(0);
                    progressWheel.setBarColor(Color.parseColor(barColor));
                    if (debuggable) {
                        Log.d(TAG, "=========setBarColor===========>>>>" + barColor);
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;
            case OP_SET_BAR_LENGTH:
                try {
                    int barLength = esArray.getInt(0);
                    progressWheel.setBarLength((int) PixelUtil.dp2px(barLength));
                    if (debuggable) {
                        Log.d(TAG, "=========setBarLength===========>>>>" + (int) PixelUtil.dp2px(barLength));
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;
            case OP_SET_BAR_WIDTH:
                try {
                    int barWidth = esArray.getInt(0);
                    progressWheel.setBarWidth((int) PixelUtil.dp2px(barWidth));
                    if (debuggable) {
                        Log.d(TAG, "=========setBarWidth===========>>>>" + (int) PixelUtil.dp2px(barWidth));
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;

            case OP_SET_BAR_STYLE:
                try {
                    int barStyle = esArray.getInt(0);
                    switch (barStyle) {
                        case 0:
                            progressWheel.setBarStyle(Paint.Style.FILL);
                            break;
                        case 1:
                            progressWheel.setBarStyle(Paint.Style.STROKE);
                            break;
                        case 2:
                            progressWheel.setBarStyle(Paint.Style.FILL_AND_STROKE);
                            break;
                    }
                    if (debuggable) {
                        Log.d(TAG, "=========setBarStyle===========>>>>" + barStyle);
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;

            case OP_SET_CIRCLE_COLOR:
                try {
                    String circleColor = esArray.getString(0);
                    progressWheel.setCircleColor(Color.parseColor(circleColor));
                    if (debuggable) {
                        Log.d(TAG, "=========setCircleColor===========>>>>" + circleColor);
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;
            case OP_SET_CIRCLE_RADIUS:
                try {
                    int circleRadius = esArray.getInt(0);
                    progressWheel.setCircleRadius((int) PixelUtil.dp2px(circleRadius));
                    if (debuggable) {
                        Log.d(TAG,
                                "=========setCircleRadius===========>>>>" + (int) PixelUtil.dp2px(circleRadius));
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;
            case OP_SET_CONTOUR_COLOR:
                try {
                    String contourColor = esArray.getString(0);
                    progressWheel.setContourColor(Color.parseColor(contourColor));
                    if (debuggable) {
                        Log.d(TAG, "=========setContourColor===========>>>>" + contourColor);
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;
            case OP_SET_CONTOUR_SIZE:
                try {
                    int contourSize = esArray.getInt(0);
                    progressWheel.setContourSize((int) PixelUtil.dp2px(contourSize));
                    if (debuggable) {
                        Log.d(TAG,
                                "=========setContourSize===========>>>>" + (int) PixelUtil.dp2px(contourSize));
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;
            case OP_SET_DELAY_MILLIS:
                try {
                    int delayMillis = esArray.getInt(0);
                    progressWheel.setDelayMillis(delayMillis);
                    if (debuggable) {
                        Log.d(TAG, "=========setDelayMillis===========>>>>" + delayMillis);
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;


            case OP_SET_PADDING_TOP:
                try {
                    int paddingTop = esArray.getInt(0);
                    progressWheel.setPaddingTop((int) PixelUtil.dp2px(paddingTop));
                    if (debuggable) {
                        Log.d(TAG,
                                "=========setPaddingTop===========>>>>" + (int) PixelUtil.dp2px(paddingTop));
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;
            case OP_SET_PADDING_BOTTOM:
                try {
                    int paddingBottom = esArray.getInt(0);
                    progressWheel.setPaddingBottom((int) PixelUtil.dp2px(paddingBottom));
                    if (debuggable) {
                        Log.d(TAG,
                                "=========setPaddingBottom===========>>>>" + (int) PixelUtil.dp2px(paddingBottom));
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;

            case OP_SET_PADDING_LEFT:
                try {
                    int paddingLeft = esArray.getInt(0);
                    progressWheel.setPaddingLeft((int) PixelUtil.dp2px(paddingLeft));
                    if (debuggable) {
                        Log.d(TAG,
                                "=========setPaddingLeft===========>>>>" + (int) PixelUtil.dp2px(paddingLeft));
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;

            case OP_SET_PADDING_RIGHT:
                try {
                    int paddingRight = esArray.getInt(0);
                    progressWheel.setPaddingRight((int) PixelUtil.dp2px(paddingRight));
                    if (debuggable) {
                        Log.d(TAG,
                                "=========setPaddingRight===========>>>>" + (int) PixelUtil.dp2px(paddingRight));
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;

            case OP_SET_PROGRESS:
                try {
                    int progress = esArray.getInt(0);
                    progressWheel.setProgress(progress);
                    if (debuggable) {
                        Log.d(TAG, "=========setProgress===========>>>>" + progress);
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;
            case OP_SET_RIM_COLOR:
                try {
                    String rimColor = esArray.getString(0);
                    progressWheel.setRimColor(Color.parseColor(rimColor));
                    if (debuggable) {
                        Log.d(TAG, "=========setRimColor===========>>>>" + rimColor);
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;
            case OP_SET_RIM_WIDTH:
                try {
                    int rimWidth = esArray.getInt(0);
                    progressWheel.setRimWidth((int) PixelUtil.dp2px(rimWidth));
                    if (debuggable) {
                        Log.d(TAG, "=========setRimColor===========>>>>" + (int) PixelUtil.dp2px(rimWidth));
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;
            case OP_SET_SPIN_SPEED:
                try {
                    int spinSpeed = esArray.getInt(0);
                    progressWheel.setSpinSpeed(spinSpeed);
                    if (debuggable) {
                        Log.d(TAG, "=========setSpinSpeed===========>>>>" + spinSpeed);
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;
            case OP_SET_TEXT:
                try {
                    String text = esArray.getString(0);
                    progressWheel.setText(text);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;

            case OP_SET_TEXT_COLOR:
                try {
                    String textColor = esArray.getString(0);
                    progressWheel.setTextColor(Color.parseColor(textColor));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;
            case OP_SET_TEXT_SIZE:
                try {
                    int textSize = esArray.getInt(0);
                    progressWheel.setTextSize((int) PixelUtil.dp2px(textSize));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;
            case OP_STOP_SPINNING:
                try {
                    progressWheel.stopSpinning();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;
            case OP_START_SPINNING:
                try {
                    progressWheel.startSpinning();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;
            case OP_SET_CONTOUR_ENABLED:
                try {
                    boolean enabled = esArray.getBoolean(0);
                    progressWheel.setContourEnabled(enabled);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;
            case OP_SET_RIM_ENABLED:
                try {
                    boolean enabled = esArray.getBoolean(0);
                    progressWheel.setRimEnabled(enabled);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;
            case OP_SHOW:
                try {
                    boolean show = esArray.getBoolean(0);
                    progressWheel.setVisibility(show ? View.VISIBLE : View.GONE);
                    if (debuggable) {
                        Log.d(TAG, "=========setVisibility===========>>>>" + show);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                esPromise.resolve(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void destroy(ProgressWheel esEventView) {

    }
}
