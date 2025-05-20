package com.quicktvui.support.core.component.rangeseekbar;

import android.content.Context;
import android.util.Log;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;

@ESKitAutoRegister
public class ESVerticalSeekBarViewComponent extends ESHorizontalSeekBarViewComponent {
    private static final String TAG = "ESVerticalSeekBar";

    protected static final String OP_SET_ORIENTATION = "setOrientation";
    protected static final String OP_SET_TICK_MARK_DIRECTION = "setTickMarkDirection";
    protected static final String OP_SET_INDICATOR_TEXT_ORIENTATION = "setIndicatorTextOrientation";
    protected static final String OP_SET_LEFT_INDICATOR_TEXT_ORIENTATION = "setLeftIndicatorTextOrientation";
    protected static final String OP_SET_RIGHT_INDICATOR_TEXT_ORIENTATION = "setRightIndicatorTextOrientation";

    @Override
    protected RangeSeekBar getRangeSeekBar(Context context) {
        VerticalRangeSeekBar rangeSeekBar = new VerticalRangeSeekBar(context);
        rangeSeekBar.setOnRangeChangedListener(new OnRangeChangedListener() {
            @Override
            public void onRangeChanged(RangeSeekBar view, float leftValue, float rightValue,
                                       boolean isFromUser) {
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
        return rangeSeekBar;
    }

    @Override
    public void dispatchFunction(RangeSeekBar view, String functionName, EsArray params, EsPromise esPromise) {
        switch (functionName) {
            case OP_SET_ORIENTATION:
                try {
                    if (view instanceof VerticalRangeSeekBar) {
                        int orientation = params.getInt(0);
                        ((VerticalRangeSeekBar) view).setOrientation(orientation);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_TICK_MARK_DIRECTION:
                try {
                    if (view instanceof VerticalRangeSeekBar) {
                        int tickMarkDirection = params.getInt(0);
                        ((VerticalRangeSeekBar) view).setTickMarkDirection(tickMarkDirection);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_INDICATOR_TEXT_ORIENTATION:
            case OP_SET_LEFT_INDICATOR_TEXT_ORIENTATION:
                try {
                    if (view instanceof VerticalRangeSeekBar) {
                        int orientation = params.getInt(0);
                        ((VerticalRangeSeekBar) view).getLeftSeekBar().setIndicatorTextOrientation(orientation);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_SET_RIGHT_INDICATOR_TEXT_ORIENTATION:
                try {
                    if (view instanceof VerticalRangeSeekBar) {
                        int orientation = params.getInt(0);
                        ((VerticalRangeSeekBar) view).getRightSeekBar().setIndicatorTextOrientation(orientation);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            default:
                super.dispatchFunction(view, functionName, params, esPromise);
                break;
        }
    }
}
