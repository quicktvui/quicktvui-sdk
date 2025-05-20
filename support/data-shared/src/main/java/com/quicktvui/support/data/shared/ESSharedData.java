package com.quicktvui.support.data.shared;

/**
 *
 */
public class ESSharedData {

    private Object data;//数据
    private int mode;//读写权限
    private int type;//类型

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

    @Override
    public String toString() {
        return "MMapSharedData{" +
                "data=" + data +
                ", mode=" + mode +
                ", type=" + type +
                '}';
    }
}
