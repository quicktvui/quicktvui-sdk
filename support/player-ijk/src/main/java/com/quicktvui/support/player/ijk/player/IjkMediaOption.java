package com.quicktvui.support.player.ijk.player;

public class IjkMediaOption {

    public static final int IJK_MEDIA_OPTION_TYPE_LONG = 0;
    public static final int IJK_MEDIA_OPTION_TYPE_STRING = 1;

    private int category;
    private String name;
    private long longValue;
    private String stringValue;
    private int type;

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLongValue() {
        return longValue;
    }

    public void setLongValue(long longValue) {
        this.longValue = longValue;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "IjkMediaOption{" +
                "category=" + category +
                ", name='" + name + '\'' +
                ", longValue=" + longValue +
                ", stringValue='" + stringValue + '\'' +
                ", type=" + type +
                '}';
    }
}
