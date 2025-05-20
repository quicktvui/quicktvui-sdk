package com.quicktvui.support.player.manager.setting;


import android.content.Context;
import android.content.SharedPreferences;

import com.quicktvui.support.player.manager.log.PLog;
import com.quicktvui.support.player.manager.manager.PlayerConfiguration;
import com.quicktvui.support.player.manager.utils.Preconditions;

public class PlayerSetting {

    private static final String PLAYER_SETTING = "player_setting";

    private static final String SETTING_ASPECT_RATIO_KEY = "aspect_ratio";

    private static final String SETTING_DEFINITION_KEY = "definition";

    private static PlayerSetting defaultInstance;
    private SharedPreferences sharedSetting;

    private int aspectRatio;
    private int definition;
    private Context context;

    private PlayerConfiguration configuration;

    private PlayerSetting() {
    }

    public static PlayerSetting getInstance() {
        if (defaultInstance == null) {
            synchronized (PlayerSetting.class) {
                if (defaultInstance == null) {
                    defaultInstance = new PlayerSetting();
                }
            }
        }
        return defaultInstance;
    }

    public synchronized void init(PlayerConfiguration configuration) {
        this.configuration = Preconditions.checkNotNull(configuration);
        this.context = configuration.getContext().getApplicationContext();
        readSharedSetting();
    }

    /**
     * 读取设置数据
     */
    private void readSharedSetting() {
        if (context == null) {
            throw new IllegalStateException("PlayerSetting must be init first!");
        }
        sharedSetting = context.getSharedPreferences(PLAYER_SETTING, Context.MODE_PRIVATE);
        aspectRatio = sharedSetting.getInt(SETTING_ASPECT_RATIO_KEY, -1);
        definition = sharedSetting.getInt(SETTING_DEFINITION_KEY, -1);
    }


    /**
     * 设置画面比例
     *
     * @param aspectRatio
     */
    public void setAspectRatioSetting(int aspectRatio) {
        if (context == null) {
            throw new IllegalStateException("PlayerSetting must be init first!");
        }
        if (saveShareDataInteger(SETTING_ASPECT_RATIO_KEY, aspectRatio)) {
            this.aspectRatio = aspectRatio;
        }
    }

    /**
     * 设置清晰度
     *
     * @param definition
     */
    public void setDefinitionSetting(int definition) {
        if (context == null) {
            throw new IllegalStateException("PlayerSetting must be init first!");
        }
        if (saveShareDataInteger(SETTING_DEFINITION_KEY, definition)) {
            this.definition = definition;
        }
    }

    /**
     * 获取屏幕的比例设置
     *
     * @return
     */
    public int getAspectRatio() {
        return aspectRatio;
    }

    /**
     * 获取清晰度设置
     *
     * @return
     */
    public int getDefinition() {
        return definition;
    }

    /**
     * 保存设置数据
     */
    private boolean saveShareDataBoolean(String key, boolean value) {
        if (context == null) {
            throw new IllegalStateException("PlayerSetting must be init first!");
        }

        if (sharedSetting == null) {
            sharedSetting = context.getSharedPreferences(PLAYER_SETTING, Context.MODE_PRIVATE);
        }
        SharedPreferences.Editor edit = sharedSetting.edit();
        edit.putBoolean(key, value);
        return edit.commit();
    }

    /**
     * 保存设置数据
     */
    private boolean saveShareDataInteger(String key, int value) {
        if (context == null) {
            throw new IllegalStateException("PlayerSetting must be init first!");
        }

        if (sharedSetting == null) {
            sharedSetting = context.getSharedPreferences(PLAYER_SETTING, Context.MODE_PRIVATE);
        }
        PLog.d("new_player", "saveShareDataInteger key：" + key + " value:" + value);
        SharedPreferences.Editor edit = sharedSetting.edit();
        edit.putInt(key, value);
        return edit.commit();
    }

    /**
     * 回收资源
     */
    public void release() {
        defaultInstance = null;
    }
}
