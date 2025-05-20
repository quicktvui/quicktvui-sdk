package com.quicktvui.support.player.manager.player;

import java.util.HashMap;
import java.util.Map;


public class PlayerError {
    /**
     * 播放器类型
     */
    public PlayerType playerType;
    /**
     * 错误的信息
     */
    public String errorMsg;

    /**
     * 错误码
     */
    public int errorCode;

    /**
     * 其他信息
     */
    private Map<String, String> extraMap;

    public PlayerError(PlayerType playerTypeF) {
        this.playerType = playerTypeF;
    }

    public PlayerError(PlayerType playerTypeF, String errorMsg) {
        this(playerTypeF);
        this.errorMsg = errorMsg;
    }

    public PlayerError(PlayerType playerTypeF, String errorMsg, int errorCode) {
        this(playerTypeF, errorMsg);
        this.errorCode = errorCode;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public void setPlayerType(PlayerType playerTypeF) {
        this.playerType = playerTypeF;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public void putExtraInfo(String key, String value) {
        if (extraMap == null) {
            extraMap = new HashMap<>();
        }
        extraMap.put(key, value);
    }

    public Map<String, String> getExtraMap() {
        return extraMap;
    }

    @Override
    public String toString() {
        return "PlayerError{" +
                "playerTypeF=" + playerType +
                ", errorCode='" + errorCode + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                ", extraMap='" + extraMap + '\'' +
                '}';
    }
}
