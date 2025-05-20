package com.quicktvui.support.core.component.animation;

import static com.quicktvui.sdk.base.IEsInfo.ES_OP_GET_ES_INFO;

import android.content.Context;
import android.os.Build;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.sunrain.toolkit.utils.log.L;

import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.component.IEsComponent;
import com.quicktvui.sdk.base.core.EsProxy;

@ESKitAutoRegister
public class AnimationViewComponent implements IEsComponent<AnimationView> {

    private static final String TAG = "AnimationView";

    //
    private static final String OP_OBJECT_ANIMATOR_RESET_ANIMATIONS = "resetAnimators";

    //
    private static final String OP_OBJECT_ANIMATOR_START_ANIMATOR = "startAnimator";
    private static final String OP_OBJECT_ANIMATOR_START_ANIMATOR_DELAY = "startAnimatorDelay";
    private static final String OP_OBJECT_ANIMATOR_PAUSE_ANIMATOR = "pauseAnimator";
    private static final String OP_OBJECT_ANIMATOR_RESUME_ANIMATOR = "resumeAnimator";
    private static final String OP_OBJECT_ANIMATOR_CANCEL_ANIMATOR = "cancelAnimator";
    private static final String OP_OBJECT_ANIMATOR_REVERSE_ANIMATOR = "reverseAnimator";

    //
    private static final String OP_ANIMATOR_SET = "animatorSet";

    //
    private static final String OP_ANIMATOR_SET_PIVOT_X = "setPivotX";
    private static final String OP_ANIMATOR_SET_PIVOT_Y = "setPivotY";
    private static final String OP_ANIMATOR_RESET_PIVOT = "resetPivot";

    private static final String OP_ANIMATOR_SET_PLAY = "play";
    private static final String OP_ANIMATOR_SET_WITH = "with";
    private static final String OP_ANIMATOR_SET_BEFORE = "before";
    private static final String OP_ANIMATOR_SET_AFTER = "after";
    private static final String OP_ANIMATOR_SET_AFTER_DELAY = "afterDelay";

    private static final String OP_ANIMATOR_SET_PLAY_SEQUENTIALLY_1 = "playSequentially1";
    private static final String OP_ANIMATOR_SET_PLAY_SEQUENTIALLY_2 = "playSequentially2";
    private static final String OP_ANIMATOR_SET_PLAY_SEQUENTIALLY_3 = "playSequentially3";
    private static final String OP_ANIMATOR_SET_PLAY_SEQUENTIALLY_4 = "playSequentially4";
    private static final String OP_ANIMATOR_SET_PLAY_SEQUENTIALLY_5 = "playSequentially5";

    private static final String OP_ANIMATOR_SET_PLAY_TOGETHER_1 = "playTogether1";
    private static final String OP_ANIMATOR_SET_PLAY_TOGETHER_2 = "playTogether2";
    private static final String OP_ANIMATOR_SET_PLAY_TOGETHER_3 = "playTogether3";
    private static final String OP_ANIMATOR_SET_PLAY_TOGETHER_4 = "playTogether4";
    private static final String OP_ANIMATOR_SET_PLAY_TOGETHER_5 = "playTogether5";

    //
    private static final String OP_OBJECT_ANIMATOR = "objectAnimator";
    private static final String OP_OBJECT_ANIMATOR_1 = "objectAnimator1";
    private static final String OP_OBJECT_ANIMATOR_2 = "objectAnimator2";
    private static final String OP_OBJECT_ANIMATOR_3 = "objectAnimator3";
    private static final String OP_OBJECT_ANIMATOR_4 = "objectAnimator4";
    private static final String OP_OBJECT_ANIMATOR_5 = "objectAnimator5";
    private static final String OP_OBJECT_ANIMATOR_6 = "objectAnimator6";
    private static final String OP_OBJECT_ANIMATOR_7 = "objectAnimator7";
    private static final String OP_OBJECT_ANIMATOR_8 = "objectAnimator8";
    private static final String OP_OBJECT_ANIMATOR_9 = "objectAnimator9";
    private static final String OP_OBJECT_ANIMATOR_10 = "objectAnimator10";

    @Override
    public AnimationView createView(Context context, EsMap initParams) {
        return new AnimationView(context);
    }

    @Override
    public void dispatchFunction(AnimationView view, String functionName, EsArray params, EsPromise promise) {
        if (L.DEBUG) {
            L.logD("#---dispatchFunction------>>>" +
                    "functionName:" + functionName + "----->>>" +
                    "params:" + params.toString()
            );
        }
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
                promise.resolve(map);
                break;

            case OP_ANIMATOR_SET_PIVOT_X:
                try {
                    double value = params.getDouble(0);
                    view.setPivotX((float) value);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_ANIMATOR_SET_PIVOT_Y:
                try {
                    double value = params.getDouble(0);
                    view.setPivotY((float) value);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_ANIMATOR_RESET_PIVOT:
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        view.resetPivot();
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //
            case OP_ANIMATOR_SET:
                try {
                    String id = params.getString(0);
                    int value = params.getInt(1);
                    boolean listenAnimator = params.getBoolean(2);
                    view.animatorSet(id, value, listenAnimator);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;

            case OP_OBJECT_ANIMATOR_RESET_ANIMATIONS:
                try {
                    view.resetAnimators();
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------------------------------
            case OP_OBJECT_ANIMATOR_START_ANIMATOR:
                try {
                    String id = params.getString(0);
                    view.startAnimator(id);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;

            case OP_OBJECT_ANIMATOR_START_ANIMATOR_DELAY:
                try {
                    String id = params.getString(0);
                    long value = params.getLong(1);
                    view.startAnimatorDelay(id, value);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_OBJECT_ANIMATOR_REVERSE_ANIMATOR:
                try {
                    String id = params.getString(0);
                    view.reverseAnimator(id);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;

            case OP_OBJECT_ANIMATOR_PAUSE_ANIMATOR:
                try {
                    String id = params.getString(0);
                    view.pauseAnimator(id);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_OBJECT_ANIMATOR_RESUME_ANIMATOR:
                try {
                    String id = params.getString(0);
                    view.resumeAnimator(id);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_OBJECT_ANIMATOR_CANCEL_ANIMATOR:
                try {
                    String id = params.getString(0);
                    view.cancelAnimator(id);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //----------------------ObjectAnimator------------------------
            case OP_OBJECT_ANIMATOR:
                try {
                    String id = params.getString(0);
                    String valueType = params.getString(1);
                    String propertyName = params.getString(2);

                    int duration = params.getInt(3);
                    int repeatMode = params.getInt(4);
                    int repeatCount = params.getInt(5);
                    boolean listenAnimator = params.getBoolean(6);
                    boolean listenAnimatorValue = params.getBoolean(7);
                    EsMap interpolatorMap = params.getMap(8);
                    //
                    if (AnimationValueType.OF_INT.equals(valueType)) {
                        view.ofInt(id, propertyName, duration,
                                repeatMode, repeatCount,
                                listenAnimator, listenAnimatorValue, interpolatorMap
                        );
                    }
                    //
                    else if (AnimationValueType.OF_FLOAT.equals(valueType)) {
                        view.ofFloat(id, propertyName, duration,
                                repeatMode, repeatCount,
                                listenAnimator, listenAnimatorValue, interpolatorMap
                        );
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_OBJECT_ANIMATOR_1:
                try {
                    String id = params.getString(0);
                    String valueType = params.getString(1);
                    String propertyName = params.getString(2);
                    int duration = params.getInt(4);
                    int repeatMode = params.getInt(5);
                    int repeatCount = params.getInt(6);
                    boolean listenAnimator = params.getBoolean(7);
                    boolean listenAnimatorValue = params.getBoolean(8);
                    EsMap interpolatorMap = params.getMap(9);
                    //int
                    if (AnimationValueType.OF_INT.equals(valueType)) {
                        int value1 = params.getInt(3);
                        view.ofInt1(id, propertyName,
                                value1,
                                duration, repeatMode, repeatCount,
                                listenAnimator, listenAnimatorValue,
                                interpolatorMap
                        );
                    }
                    //float
                    else if (AnimationValueType.OF_FLOAT.equals(valueType)) {
                        String string = params.getString(3);
                        view.ofFloat1(id, propertyName,
                                Float.parseFloat(string),
                                duration, repeatMode, repeatCount,
                                listenAnimator, listenAnimatorValue,
                                interpolatorMap
                        );
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_OBJECT_ANIMATOR_2:
                try {

                    String id = params.getString(0);
                    String valueType = params.getString(1);
                    String propertyName = params.getString(2);

                    int duration = params.getInt(5);
                    int repeatMode = params.getInt(6);
                    int repeatCount = params.getInt(7);
                    boolean listenAnimator = params.getBoolean(8);
                    boolean listenAnimatorValue = params.getBoolean(9);
                    EsMap interpolatorMap = params.getMap(10);
                    //
                    if (AnimationValueType.OF_INT.equals(valueType)) {
                        int value1 = params.getInt(3);
                        int value2 = params.getInt(4);
                        view.ofInt2(id, propertyName,
                                value1,
                                value2,
                                duration, repeatMode, repeatCount,
                                listenAnimator, listenAnimatorValue,
                                interpolatorMap
                        );
                    }
                    //
                    else if (AnimationValueType.OF_FLOAT.equals(valueType)) {
                        String string1 = params.getString(3);
                        String string2 = params.getString(4);
                        view.ofFloat2(id, propertyName,
                                Float.parseFloat(string1),
                                Float.parseFloat(string2),
                                duration, repeatMode, repeatCount,
                                listenAnimator, listenAnimatorValue,
                                interpolatorMap
                        );
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;

            case OP_OBJECT_ANIMATOR_3:
                try {

                    String id = params.getString(0);
                    String valueType = params.getString(1);
                    String propertyName = params.getString(2);

                    int duration = params.getInt(6);
                    int repeatMode = params.getInt(7);
                    int repeatCount = params.getInt(8);
                    boolean listenAnimator = params.getBoolean(9);
                    boolean listenAnimatorValue = params.getBoolean(10);
                    EsMap interpolatorMap = params.getMap(11);
                    //
                    if (AnimationValueType.OF_INT.equals(valueType)) {
                        int value1 = params.getInt(3);
                        int value2 = params.getInt(4);
                        int value3 = params.getInt(5);
                        view.ofInt3(id, propertyName,
                                value1,
                                value2,
                                value3,
                                duration, repeatMode, repeatCount,
                                listenAnimator, listenAnimatorValue,
                                interpolatorMap
                        );
                    }
                    //
                    else if (AnimationValueType.OF_FLOAT.equals(valueType)) {
                        String string1 = params.getString(3);
                        String string2 = params.getString(4);
                        String string3 = params.getString(5);
                        view.ofFloat3(id, propertyName,
                                Float.parseFloat(string1),
                                Float.parseFloat(string2),
                                Float.parseFloat(string3),
                                duration, repeatMode, repeatCount,
                                listenAnimator, listenAnimatorValue,
                                interpolatorMap
                        );
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;

            case OP_OBJECT_ANIMATOR_4:
                try {

                    String id = params.getString(0);
                    String valueType = params.getString(1);
                    String propertyName = params.getString(2);

                    int duration = params.getInt(7);
                    int repeatMode = params.getInt(8);
                    int repeatCount = params.getInt(9);
                    boolean listenAnimator = params.getBoolean(10);
                    boolean listenAnimatorValue = params.getBoolean(11);
                    EsMap interpolatorMap = params.getMap(12);
                    //
                    if (AnimationValueType.OF_INT.equals(valueType)) {
                        int value1 = params.getInt(3);
                        int value2 = params.getInt(4);
                        int value3 = params.getInt(5);
                        int value4 = params.getInt(6);
                        view.ofInt4(id, propertyName,
                                value1,
                                value2,
                                value3,
                                value4,
                                duration, repeatMode, repeatCount,
                                listenAnimator, listenAnimatorValue,
                                interpolatorMap
                        );
                    }
                    //
                    else if (AnimationValueType.OF_FLOAT.equals(valueType)) {
                        String string1 = params.getString(3);
                        String string2 = params.getString(4);
                        String string3 = params.getString(5);
                        String string4 = params.getString(6);
                        view.ofFloat4(id, propertyName,
                                Float.parseFloat(string1),
                                Float.parseFloat(string2),
                                Float.parseFloat(string3),
                                Float.parseFloat(string4),
                                duration, repeatMode, repeatCount,
                                listenAnimator, listenAnimatorValue,
                                interpolatorMap
                        );
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;


            case OP_OBJECT_ANIMATOR_5:
                try {

                    String id = params.getString(0);
                    String valueType = params.getString(1);
                    String propertyName = params.getString(2);

                    int duration = params.getInt(8);
                    int repeatMode = params.getInt(9);
                    int repeatCount = params.getInt(10);
                    boolean listenAnimator = params.getBoolean(11);
                    boolean listenAnimatorValue = params.getBoolean(12);
                    EsMap interpolatorMap = params.getMap(13);
                    //
                    if (AnimationValueType.OF_INT.equals(valueType)) {
                        int value1 = params.getInt(3);
                        int value2 = params.getInt(4);
                        int value3 = params.getInt(5);
                        int value4 = params.getInt(6);
                        int value5 = params.getInt(7);
                        view.ofInt5(id, propertyName,
                                value1,
                                value2,
                                value3,
                                value4,
                                value5,
                                duration, repeatMode, repeatCount,
                                listenAnimator, listenAnimatorValue,
                                interpolatorMap
                        );
                    }
                    //
                    else if (AnimationValueType.OF_FLOAT.equals(valueType)) {
                        String string1 = params.getString(3);
                        String string2 = params.getString(4);
                        String string3 = params.getString(5);
                        String string4 = params.getString(6);
                        String string5 = params.getString(7);
                        view.ofFloat5(id, propertyName,
                                Float.parseFloat(string1),
                                Float.parseFloat(string2),
                                Float.parseFloat(string3),
                                Float.parseFloat(string4),
                                Float.parseFloat(string5),
                                duration, repeatMode, repeatCount,
                                listenAnimator, listenAnimatorValue,
                                interpolatorMap
                        );
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;


            case OP_OBJECT_ANIMATOR_6:
                try {

                    String id = params.getString(0);
                    String valueType = params.getString(1);
                    String propertyName = params.getString(2);

                    int duration = params.getInt(9);
                    int repeatMode = params.getInt(10);
                    int repeatCount = params.getInt(11);
                    boolean listenAnimator = params.getBoolean(12);
                    boolean listenAnimatorValue = params.getBoolean(13);
                    EsMap interpolatorMap = params.getMap(14);
                    //
                    if (AnimationValueType.OF_INT.equals(valueType)) {
                        int value1 = params.getInt(3);
                        int value2 = params.getInt(4);
                        int value3 = params.getInt(5);
                        int value4 = params.getInt(6);
                        int value5 = params.getInt(7);
                        int value6 = params.getInt(8);
                        view.ofInt6(id, propertyName,
                                value1,
                                value2,
                                value3,
                                value4,
                                value5,
                                value6,
                                duration, repeatMode, repeatCount,
                                listenAnimator, listenAnimatorValue,
                                interpolatorMap
                        );
                    }
                    //
                    else if (AnimationValueType.OF_FLOAT.equals(valueType)) {
                        String string1 = params.getString(3);
                        String string2 = params.getString(4);
                        String string3 = params.getString(5);
                        String string4 = params.getString(6);
                        String string5 = params.getString(7);
                        String string6 = params.getString(8);
                        view.ofFloat6(id, propertyName,
                                Float.parseFloat(string1),
                                Float.parseFloat(string2),
                                Float.parseFloat(string3),
                                Float.parseFloat(string4),
                                Float.parseFloat(string5),
                                Float.parseFloat(string6),
                                duration, repeatMode, repeatCount,
                                listenAnimator, listenAnimatorValue,
                                interpolatorMap
                        );
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;


            case OP_OBJECT_ANIMATOR_7:
                try {

                    String id = params.getString(0);
                    String valueType = params.getString(1);
                    String propertyName = params.getString(2);

                    int duration = params.getInt(10);
                    int repeatMode = params.getInt(11);
                    int repeatCount = params.getInt(12);
                    boolean listenAnimator = params.getBoolean(13);
                    boolean listenAnimatorValue = params.getBoolean(14);
                    EsMap interpolatorMap = params.getMap(15);
                    //
                    if (AnimationValueType.OF_INT.equals(valueType)) {
                        int value1 = params.getInt(3);
                        int value2 = params.getInt(4);
                        int value3 = params.getInt(5);
                        int value4 = params.getInt(6);
                        int value5 = params.getInt(7);
                        int value6 = params.getInt(8);
                        int value7 = params.getInt(9);
                        view.ofInt7(id, propertyName,
                                value1,
                                value2,
                                value3,
                                value4,
                                value5,
                                value6,
                                value7,
                                duration, repeatMode, repeatCount,
                                listenAnimator, listenAnimatorValue,
                                interpolatorMap
                        );
                    }
                    //
                    else if (AnimationValueType.OF_FLOAT.equals(valueType)) {
                        String string1 = params.getString(3);
                        String string2 = params.getString(4);
                        String string3 = params.getString(5);
                        String string4 = params.getString(6);
                        String string5 = params.getString(7);
                        String string6 = params.getString(8);
                        String string7 = params.getString(9);
                        view.ofFloat7(id, propertyName,
                                Float.parseFloat(string1),
                                Float.parseFloat(string2),
                                Float.parseFloat(string3),
                                Float.parseFloat(string4),
                                Float.parseFloat(string5),
                                Float.parseFloat(string6),
                                Float.parseFloat(string7),
                                duration, repeatMode, repeatCount,
                                listenAnimator, listenAnimatorValue,
                                interpolatorMap
                        );
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;


            case OP_OBJECT_ANIMATOR_8:
                try {

                    String id = params.getString(0);
                    String valueType = params.getString(1);
                    String propertyName = params.getString(2);

                    int duration = params.getInt(11);
                    int repeatMode = params.getInt(12);
                    int repeatCount = params.getInt(13);
                    boolean listenAnimator = params.getBoolean(14);
                    boolean listenAnimatorValue = params.getBoolean(15);
                    EsMap interpolatorMap = params.getMap(16);
                    //
                    if (AnimationValueType.OF_INT.equals(valueType)) {
                        int value1 = params.getInt(3);
                        int value2 = params.getInt(4);
                        int value3 = params.getInt(5);
                        int value4 = params.getInt(6);
                        int value5 = params.getInt(7);
                        int value6 = params.getInt(8);
                        int value7 = params.getInt(9);
                        int value8 = params.getInt(10);
                        view.ofInt8(id, propertyName,
                                value1,
                                value2,
                                value3,
                                value4,
                                value5,
                                value6,
                                value7,
                                value8,
                                duration, repeatMode, repeatCount,
                                listenAnimator, listenAnimatorValue,
                                interpolatorMap
                        );
                    }
                    //
                    else if (AnimationValueType.OF_FLOAT.equals(valueType)) {
                        String string1 = params.getString(3);
                        String string2 = params.getString(4);
                        String string3 = params.getString(5);
                        String string4 = params.getString(6);
                        String string5 = params.getString(7);
                        String string6 = params.getString(8);
                        String string7 = params.getString(9);
                        String string8 = params.getString(10);
                        view.ofFloat8(id, propertyName,
                                Float.parseFloat(string1),
                                Float.parseFloat(string2),
                                Float.parseFloat(string3),
                                Float.parseFloat(string4),
                                Float.parseFloat(string5),
                                Float.parseFloat(string6),
                                Float.parseFloat(string7),
                                Float.parseFloat(string8),
                                duration, repeatMode, repeatCount,
                                listenAnimator, listenAnimatorValue,
                                interpolatorMap
                        );
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;

            case OP_OBJECT_ANIMATOR_9:
                try {

                    String id = params.getString(0);

                    String valueType = params.getString(1);
                    String propertyName = params.getString(2);

                    int duration = params.getInt(12);
                    int repeatMode = params.getInt(13);
                    int repeatCount = params.getInt(14);
                    boolean listenAnimator = params.getBoolean(15);
                    boolean listenAnimatorValue = params.getBoolean(16);
                    EsMap interpolatorMap = params.getMap(17);
                    //
                    if (AnimationValueType.OF_INT.equals(valueType)) {
                        int value1 = params.getInt(3);
                        int value2 = params.getInt(4);
                        int value3 = params.getInt(5);
                        int value4 = params.getInt(6);
                        int value5 = params.getInt(7);
                        int value6 = params.getInt(8);
                        int value7 = params.getInt(9);
                        int value8 = params.getInt(10);
                        int value9 = params.getInt(11);
                        view.ofInt9(id, propertyName,
                                value1,
                                value2,
                                value3,
                                value4,
                                value5,
                                value6,
                                value7,
                                value8,
                                value9,
                                duration, repeatMode, repeatCount,
                                listenAnimator, listenAnimatorValue,
                                interpolatorMap
                        );
                    }
                    //
                    else if (AnimationValueType.OF_FLOAT.equals(valueType)) {
                        String string1 = params.getString(3);
                        String string2 = params.getString(4);
                        String string3 = params.getString(5);
                        String string4 = params.getString(6);
                        String string5 = params.getString(7);
                        String string6 = params.getString(8);
                        String string7 = params.getString(9);
                        String string8 = params.getString(10);
                        String string9 = params.getString(11);
                        view.ofFloat9(id, propertyName,
                                Float.parseFloat(string1),
                                Float.parseFloat(string2),
                                Float.parseFloat(string3),
                                Float.parseFloat(string4),
                                Float.parseFloat(string5),
                                Float.parseFloat(string6),
                                Float.parseFloat(string7),
                                Float.parseFloat(string8),
                                Float.parseFloat(string9),
                                duration, repeatMode, repeatCount,
                                listenAnimator, listenAnimatorValue,
                                interpolatorMap
                        );
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;

            case OP_OBJECT_ANIMATOR_10:
                try {
                    String id = params.getString(0);

                    String valueType = params.getString(1);
                    String propertyName = params.getString(2);

                    int duration = params.getInt(13);
                    int repeatMode = params.getInt(14);
                    int repeatCount = params.getInt(15);
                    boolean listenAnimator = params.getBoolean(16);
                    boolean listenAnimatorValue = params.getBoolean(17);
                    EsMap interpolatorMap = params.getMap(18);
                    //
                    if (AnimationValueType.OF_INT.equals(valueType)) {
                        int value1 = params.getInt(3);
                        int value2 = params.getInt(4);
                        int value3 = params.getInt(5);
                        int value4 = params.getInt(6);
                        int value5 = params.getInt(7);
                        int value6 = params.getInt(8);
                        int value7 = params.getInt(9);
                        int value8 = params.getInt(10);
                        int value9 = params.getInt(11);
                        int value10 = params.getInt(12);
                        view.ofInt10(id, propertyName,
                                value1,
                                value2,
                                value3,
                                value4,
                                value5,
                                value6,
                                value7,
                                value8,
                                value9,
                                value10,
                                duration, repeatMode, repeatCount,
                                listenAnimator, listenAnimatorValue,
                                interpolatorMap
                        );
                    }
                    //
                    else if (AnimationValueType.OF_FLOAT.equals(valueType)) {
                        String string1 = params.getString(3);
                        String string2 = params.getString(4);
                        String string3 = params.getString(5);
                        String string4 = params.getString(6);
                        String string5 = params.getString(7);
                        String string6 = params.getString(8);
                        String string7 = params.getString(9);
                        String string8 = params.getString(10);
                        String string9 = params.getString(11);
                        String string10 = params.getString(12);
                        view.ofFloat10(id, propertyName,
                                Float.parseFloat(string1),
                                Float.parseFloat(string2),
                                Float.parseFloat(string3),
                                Float.parseFloat(string4),
                                Float.parseFloat(string5),
                                Float.parseFloat(string6),
                                Float.parseFloat(string7),
                                Float.parseFloat(string8),
                                Float.parseFloat(string9),
                                Float.parseFloat(string10),
                                duration, repeatMode, repeatCount,
                                listenAnimator, listenAnimatorValue,
                                interpolatorMap
                        );
                    }

                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_ANIMATOR_SET_PLAY:
                try {
                    String animatorSetId = params.getString(0);
                    String animatorId = params.getString(1);
                    view.play(animatorSetId, animatorId);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_ANIMATOR_SET_WITH:
                try {
                    String animatorSetId = params.getString(0);
                    String animatorId = params.getString(1);
                    view.with(animatorSetId, animatorId);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_ANIMATOR_SET_BEFORE:
                try {
                    String animatorSetId = params.getString(0);
                    String animatorId = params.getString(1);
                    view.before(animatorSetId, animatorId);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_ANIMATOR_SET_AFTER:
                try {
                    String animatorSetId = params.getString(0);
                    String animatorId = params.getString(1);
                    view.after(animatorSetId, animatorId);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_ANIMATOR_SET_AFTER_DELAY:
                try {
                    String animatorSetId = params.getString(0);
                    long delay = params.getLong(1);
                    view.afterDelay(animatorSetId, delay);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //--------------------------------------------------
            case OP_ANIMATOR_SET_PLAY_SEQUENTIALLY_1:
                try {
                    String animatorSetId = params.getString(0);
                    String animatorId1 = params.getString(1);
                    view.playSequentially(animatorSetId, animatorId1);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_ANIMATOR_SET_PLAY_SEQUENTIALLY_2:
                try {
                    String animatorSetId = params.getString(0);
                    String animatorId1 = params.getString(1);
                    String animatorId2 = params.getString(2);
                    view.playSequentially(animatorSetId,
                            animatorId1,
                            animatorId2
                    );
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_ANIMATOR_SET_PLAY_SEQUENTIALLY_3:
                try {
                    String animatorSetId = params.getString(0);
                    String animatorId1 = params.getString(1);
                    String animatorId2 = params.getString(2);
                    String animatorId3 = params.getString(3);
                    view.playSequentially(animatorSetId,
                            animatorId1,
                            animatorId2,
                            animatorId3
                    );
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_ANIMATOR_SET_PLAY_SEQUENTIALLY_4:
                try {
                    String animatorSetId = params.getString(0);
                    String animatorId1 = params.getString(1);
                    String animatorId2 = params.getString(2);
                    String animatorId3 = params.getString(3);
                    String animatorId4 = params.getString(4);
                    view.playSequentially(animatorSetId,
                            animatorId1,
                            animatorId2,
                            animatorId3,
                            animatorId4
                    );
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_ANIMATOR_SET_PLAY_SEQUENTIALLY_5:
                try {
                    String animatorSetId = params.getString(0);
                    String animatorId1 = params.getString(1);
                    String animatorId2 = params.getString(2);
                    String animatorId3 = params.getString(3);
                    String animatorId4 = params.getString(4);
                    String animatorId5 = params.getString(5);
                    view.playSequentially(animatorSetId,
                            animatorId1,
                            animatorId2,
                            animatorId3,
                            animatorId4,
                            animatorId5
                    );
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            //---------------------------------------------------
            case OP_ANIMATOR_SET_PLAY_TOGETHER_1:
                try {
                    String animatorSetId = params.getString(0);
                    String animatorId1 = params.getString(1);
                    view.playTogether(animatorSetId, animatorId1);
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_ANIMATOR_SET_PLAY_TOGETHER_2:
                try {
                    String animatorSetId = params.getString(0);
                    String animatorId1 = params.getString(1);
                    String animatorId2 = params.getString(2);
                    view.playTogether(animatorSetId,
                            animatorId1,
                            animatorId2
                    );
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_ANIMATOR_SET_PLAY_TOGETHER_3:
                try {
                    String animatorSetId = params.getString(0);
                    String animatorId1 = params.getString(1);
                    String animatorId2 = params.getString(2);
                    String animatorId3 = params.getString(3);
                    view.playTogether(animatorSetId,
                            animatorId1,
                            animatorId2,
                            animatorId3
                    );
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_ANIMATOR_SET_PLAY_TOGETHER_4:
                try {
                    String animatorSetId = params.getString(0);
                    String animatorId1 = params.getString(1);
                    String animatorId2 = params.getString(2);
                    String animatorId3 = params.getString(3);
                    String animatorId4 = params.getString(4);
                    view.playTogether(animatorSetId,
                            animatorId1,
                            animatorId2,
                            animatorId3,
                            animatorId4
                    );
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            case OP_ANIMATOR_SET_PLAY_TOGETHER_5:
                try {
                    String animatorSetId = params.getString(0);
                    String animatorId1 = params.getString(1);
                    String animatorId2 = params.getString(2);
                    String animatorId3 = params.getString(3);
                    String animatorId4 = params.getString(4);
                    String animatorId5 = params.getString(5);
                    view.playTogether(animatorSetId,
                            animatorId1,
                            animatorId2,
                            animatorId3,
                            animatorId4,
                            animatorId5
                    );
                } catch (Throwable e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void destroy(AnimationView animationView) {
        if (animationView != null) {
            animationView.resetAnimators();
        }
    }
}
