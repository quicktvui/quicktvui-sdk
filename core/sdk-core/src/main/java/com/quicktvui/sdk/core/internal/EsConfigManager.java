package com.quicktvui.sdk.core.internal;

import android.text.TextUtils;

import com.sunrain.toolkit.utils.log.L;
import com.quicktvui.base.ui.ESBaseConfigManager;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * packageJson解析，前端配置文件：configs.json
 */
public class EsConfigManager extends ESBaseConfigManager {

    public static String SHAKESELF = "shakeSelf"; //焦点抖动
    public static String LIST_SHAKESELF = "listShakeSelf"; //fastList焦点抖动
    public static String FOCUS_BORDER_TYPE = "focusBorderType"; //默认边框类型 默认：0

    private boolean isShakeSelf = false;
    private boolean isListShakeSelf = false;
    private int focusBorderType = 0;

    public EsConfigManager() {

    }

    @Override
    public void doConfigs(String packageJson) {
        if (TextUtils.isEmpty(packageJson)) {
            L.logI("package.json is null");
            return;
        }
        try {
            JSONObject packageObj = new JSONObject(packageJson);
            if (packageJson.contains(SHAKESELF)) {
                this.isShakeSelf = packageObj.optBoolean(SHAKESELF);
            }
            if (packageJson.contains(LIST_SHAKESELF)) {
                this.isListShakeSelf = packageObj.optBoolean(LIST_SHAKESELF);
            }
            if (packageJson.contains(FOCUS_BORDER_TYPE)) {
                this.focusBorderType = packageObj.optInt(FOCUS_BORDER_TYPE);
            }
        } catch (JSONException e) {
            L.logW("parse package.json", e);
        }
    }

    @Override
    public boolean IsShakeSelf() {
        return isShakeSelf;
    }

    @Override
    public boolean IsListShakeSelf() {
        return isListShakeSelf;
    }

    @Override
    public int getFocusBorderType() {
        return focusBorderType;
    }

    @Override
    public int getMinRuntime() {
        return 0;
    }

    public void setShakeSelf(boolean shakeSelf) {
        isShakeSelf = shakeSelf;
    }

    public void setListShakeSelf(boolean listShakeSelf) {
        isListShakeSelf = listShakeSelf;
    }

    public void setFocusBorderType(int focusBorderType) {
        this.focusBorderType = focusBorderType;
    }
}
