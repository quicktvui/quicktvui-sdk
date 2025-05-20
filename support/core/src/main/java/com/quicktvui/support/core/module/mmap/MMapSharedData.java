package com.quicktvui.support.core.module.mmap;

/**
 *
 */
public class MMapSharedData {

    private Object data;
    private int mode;
    private int type;

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
