package com.quicktvui.support.player.manager.player;

import java.util.HashMap;
import java.util.Map;

public class PlayerInfo {
    public PlayerType playerType;
    public String message;
    public int code;
    private Map<String, String> extraMap;

    public PlayerInfo(PlayerType playerTypeF) {
        this.playerType = playerTypeF;
    }

    public PlayerInfo(PlayerType playerTypeF, String message) {
        this(playerTypeF);
        this.message = message;
    }

    public PlayerInfo(PlayerType playerTypeF, String message, int code) {
        this(playerTypeF, message);
        this.code = code;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public void setPlayerType(PlayerType playerTypeF) {
        this.playerType = playerTypeF;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
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
        return "PlayerInfo{" +
                "playerType=" + playerType +
                ", message='" + message + '\'' +
                ", code=" + code +
                ", extraMap=" + extraMap +
                '}';
    }
}
