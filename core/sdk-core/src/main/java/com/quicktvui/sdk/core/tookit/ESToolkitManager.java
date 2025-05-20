package com.quicktvui.sdk.core.tookit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import com.sunrain.toolkit.utils.SPUtils;
import com.sunrain.toolkit.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * <br>
 *
 * <br>
 */
public class ESToolkitManager {

    public static final String TOOLKIT_ACTION_SETTING = "eskit.sdk.core.ACTION_TOOLKIT_SETTING";

//    private Map<String, IESToolkit> mWindows = new HashMap<>();

    public void init(Context context) {
        context.getApplicationContext().registerReceiver(new ToolkitSettingReceiver(), new IntentFilter(TOOLKIT_ACTION_SETTING));
    }

    //region 监听设置

    private final Map<Callback<Object>, ToolkitUseCase> mCallbacks = new HashMap<>();

    public void listen(ToolkitUseCase useCase, Callback<?> callback) {
        mCallbacks.put((Callback<Object>) callback, useCase);
        notifyDefaultValue(useCase, callback);
    }

    public void unListen(Callback<?> callback) {
        mCallbacks.remove(callback);
    }

    private <T> void notifyDefaultValue(ToolkitUseCase useCase, Callback<T> callback) {
        callback.onCallback((T) useCase.defaultValue);
    }

    public interface Callback<T> {
        void onCallback(T data);
    }

    //endregion


    /**
     * 获取一个对应Tag的TooKit， 没有则创建
     **/
//    public IESToolkit getTooKit(Context context, String key) {
//        if (!mWindows.containsKey(key)) {
//            IESToolkit manager = new DefaultWindowManager(context);
//            mWindows.put(key, manager);
//            return manager;
//        }
//        return mWindows.get(key);
//    }

    //region 单例

    private static final class ESToolkitWindowManagerHolder {
        private static final ESToolkitManager INSTANCE = new ESToolkitManager();
    }

    public static ESToolkitManager get() {
        return ESToolkitWindowManagerHolder.INSTANCE;
    }

    private ESToolkitManager() {
    }

    //endregion

    private static class ToolkitSettingReceiver extends BroadcastReceiver {

        private final SPUtils mSp = SPUtils.getInstance("es_toolkit_setting");

        public ToolkitSettingReceiver() {
            for (ToolkitUseCase useCase : ToolkitUseCase.values()) {
                String value = mSp.getString(useCase.name(), null);
                if (value != null) {
                    processValue(useCase, null, value);
                }
            }
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    if (!PermissionUtils.isGrantedDrawOverlays()) {
//                        PermissionUtils.requestDrawOverlays(new PermissionUtils.SimpleCallback() {
//                            @Override
//                            public void onGranted() {
//                                parseIntent(intent);
//                            }
//
//                            @Override
//                            public void onDenied() {
//                                ToastUtils.showLong("调试工具需要弹窗权限");
//                            }
//                        });
//                        return;
//                    }
//                }
                parseIntent(intent);
            } catch (Exception e) {
                ToastUtils.showLong(e.getMessage());
            }
        }

        private void showResultInfo(String msg) {
            setResultData("\n----------------------------------------------\n" + msg + "\n");
        }

        private void parseIntent(Intent intent) {
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String value = extras.getString(key);
                    try {
                        ToolkitUseCase useCase = ToolkitUseCase.valueOf(key);
                        processValue(useCase, value);
                        mSp.put(useCase.name(), value);
                        showResultInfo("成功");
                    } catch (Throwable e) {
                        showResultInfo("失败 " + key + " " + value + "\n" + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        }

        private void processValue(ToolkitUseCase useCase, String value) {
            processValue(useCase, null, value);
        }

        private void processValue(ToolkitUseCase useCase, Callback<Object> callback, String value) {
            if (callback == null) callback = findRegisterCallback(useCase);
            Object parseValue = parseValue(value, useCase.defaultValue.getClass());
            useCase.defaultValue = parseValue;
            if (callback != null) {
                callback.onCallback(parseValue);
            }
        }

        private Object parseValue(String value, Class<?> clazz) {
            if (clazz == String.class) return value;
            if (clazz == int.class || clazz == Integer.class) return Integer.parseInt(value);
            if (clazz == boolean.class || clazz == Boolean.class)
                return Boolean.parseBoolean(value);
            if (clazz == float.class || clazz == Float.class) return Float.parseFloat(value);
            if (clazz == double.class || clazz == Double.class) return Double.parseDouble(value);
            return null;
        }

        private Callback<Object> findRegisterCallback(ToolkitUseCase useCase) {
            Map<Callback<Object>, ToolkitUseCase> callbacks = ESToolkitManager.get().mCallbacks;
            Set<Callback<Object>> keys = callbacks.keySet();
            for (Callback<Object> key : keys) {
                ToolkitUseCase uc = callbacks.get(key);
                if (uc == useCase) {
                    return key;
                }
            }
            return null;
        }

    }

}


