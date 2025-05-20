package com.quicktvui.support.data.group;

/**
 *
 */
public class ESGroupData {

    private Object data;//数据
    private int type;//类型
    private String secretKey;//秘钥
    private int mode;//读写权限

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String toString() {
        return "ESSharedData{" + "data=" + data + ", type=" + type + ", secretKey='" + secretKey + '\'' + ", mode=" + mode + '}';
    }
}
