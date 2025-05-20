package com.quicktvui.support.ui.image.canvas;

public class ESPaint {
    private int action;
    private int color;
    private int style;
    private float strokeWidth;
    private int mode;

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public String toString() {
        return "ESPaint{" +
                "action=" + action +
                "color=" + color +
                ", style=" + style +
                ", strokeWidth=" + strokeWidth +
                ", mode=" + mode +
                '}';
    }
}
