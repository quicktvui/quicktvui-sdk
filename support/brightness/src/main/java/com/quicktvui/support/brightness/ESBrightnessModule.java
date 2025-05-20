package com.quicktvui.support.brightness;

import android.content.Context;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;

@ESKitAutoRegister
public class ESBrightnessModule implements IEsModule, IEsInfo {
    private Context context;

    @Override
    public void init(Context context) {
        this.context = context;
    }

    //获取系统亮度
    public void getScreenBrightness(EsPromise promise) {
        EsMap esMap = new EsMap();
        try {
            int brightness = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            esMap.pushInt("result", brightness);
        } catch (Settings.SettingNotFoundException e) {
            esMap.pushInt("result", -1);
        }
        promise.resolve(esMap);
    }

    //获取当前window亮度
    public void getWindowBrightness(EsPromise promise) {
        EsMap esMap = new EsMap();
        Window window = EsProxy.get().getCurrentActivity(this).getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (lp.screenBrightness == -1) {
            esMap.pushInt("result", -1);
        } else {
            esMap.pushInt("result", (int) (getMaxSystemBrightness() * lp.screenBrightness));
        }
        promise.resolve(esMap);
    }

    // 获取系统亮度模式
    public void getScreenBrightnessMode(EsPromise promise) {
        EsMap esMap = new EsMap();
        try {
            int brightnessmode = Settings.System.getInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE);
            esMap.pushInt("result", brightnessmode);
        } catch (Settings.SettingNotFoundException e) {
            esMap.pushInt("result", -1);
        }
        promise.resolve(esMap);
    }

    //设置当前window亮度
    public void changeWindowBrightness(int brightness) {
        Window window = EsProxy.get().getCurrentActivity(this).getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        if (brightness == -1f) {
            lp.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE;
        } else {
            lp.screenBrightness = (float) (brightness <= 0 ? 1 : brightness) / (float) getMaxSystemBrightness();
        }
        window.setAttributes(lp);
    }

    //设置系统亮度
    public void changeScreenBrightness(int systemBrightness) {
        Settings.System.putInt(context.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, systemBrightness);
    }

    //获取系统最大亮度值
    public void getMaxBrightness(EsPromise promise) {
        EsMap esMap = new EsMap();
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            Field[] fields = powerManager.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals("BRIGHTNESS_ON")) {
                    field.setAccessible(true);
                    try {
                        esMap.pushInt("result", (int) field.get(powerManager));
                    } catch (IllegalAccessException e) {
                        esMap.pushInt("result", 255);
                    }
                }
            }
        } else {
            esMap.pushInt("result", 255);
        }
        promise.resolve(esMap);
    }

    //获取系统最大亮度值
    public int getMaxSystemBrightness() {
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (powerManager != null) {
            Field[] fields = powerManager.getClass().getDeclaredFields();
            for (Field field : fields) {
                if (field.getName().equals("BRIGHTNESS_ON")) {
                    field.setAccessible(true);
                    try {
                        return (int) field.get(powerManager);
                    } catch (IllegalAccessException e) {

                    }
                }
            }
        } else {
            return 255;
        }
        return 255;
    }

    @Override
    public void getEsInfo(EsPromise promise) {

    }

    @Override
    public void destroy() {

    }
}
