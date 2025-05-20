package com.quicktvui.support.player.manager.aspect;

import com.quicktvui.support.player.manager.manager.PlayerConfiguration;
import com.quicktvui.support.player.manager.setting.PlayerSetting;
import com.quicktvui.support.player.manager.utils.Preconditions;

import java.util.List;

/**
 * 画面比例管理器
 */
public class AspectRatioManager {

    private static AspectRatioManager defaultInstance;
    private PlayerConfiguration configuration;

    private List<AspectRatio> aspectRatioList;
    private AspectRatio currentAspectRatio;

    private AspectRatioManager() {
    }

    public static AspectRatioManager getInstance() {
        if (defaultInstance == null) {
            synchronized (AspectRatioManager.class) {
                if (defaultInstance == null) {
                    defaultInstance = new AspectRatioManager();
                }
            }
        }
        return defaultInstance;
    }


    /**
     * 初始化
     *
     * @param configuration
     */
    public synchronized void init(PlayerConfiguration configuration) {
        this.configuration = Preconditions.checkNotNull(configuration);
        //读取默认的屏幕比例
        int userSettingAR = PlayerSetting.getInstance().getAspectRatio();
        if (userSettingAR != -1) {
            currentAspectRatio = AspectRatioSettingMapper.getAspectRatio(userSettingAR);
        } else {
            currentAspectRatio = configuration.getDefaultAspectRatio();
        }
    }

    /**
     * 获取所有的屏幕比例
     *
     * @return
     */
    public List<AspectRatio> getAllAspectRatio() {
        Preconditions.checkNotNull(configuration);
        return null;
    }

    /**
     * 的画面比例
     *
     * @param aspectRatioList
     */
    public void setAllAspectRatio(List<AspectRatio> aspectRatioList) {
        this.aspectRatioList = aspectRatioList;
    }

    /**
     * 设置屏幕比例
     *
     * @param aspectRatio
     */
    public void setCurrentAspectRatio(AspectRatio aspectRatio) {
        if (aspectRatio == null) {
            return;
        }
        //保存
        if (configuration != null && configuration.isAutoSaveAspectRatio()) {
            //保存xml
            PlayerSetting.getInstance().setAspectRatioSetting(
                    AspectRatioSettingMapper.getAspectRatioValue(aspectRatio));
        }
        //设置当前的屏幕比例
        currentAspectRatio = aspectRatio;
    }

    /**
     * 获取当前的屏幕比例
     *
     * @return
     */
    public AspectRatio getCurrentAspectRatio() {
        return currentAspectRatio;
    }

    /**
     * 回收资源
     */
    public void release() {
        configuration = null;
        defaultInstance = null;
    }
}
