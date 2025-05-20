package com.quicktvui.support.player.manager.player;

import java.util.HashMap;
import java.util.Map;

public class PlayerStatus {

    public static final String CODE_KEY = "code";
    public static final String CODE_MESSAGE = "message";

    /**
     * 播放器状态信息
     */
    public PlayerStatusEnum status;
    /**
     * 播放器类型
     */
    public PlayerType playerType;

    private Map<String, Object> data;

    public PlayerStatus(PlayerStatusEnum status) {
        this.status = status;
    }

    public PlayerStatus(PlayerType playerType) {
        this.playerType = playerType;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void putData(String key, Object value) {
        if (data == null) {
            data = new HashMap<>();
        }
        data.put(key, value);
    }

    public Object getData(String key) {
        if (data == null) {
            return null;
        }
        return data.get(key);
    }

    @Override
    public String toString() {
        return "PlayerStatus{" +
                "status=" + status +
                ", playerType=" + playerType +
                ", data=" + data +
                '}';
    }
}
