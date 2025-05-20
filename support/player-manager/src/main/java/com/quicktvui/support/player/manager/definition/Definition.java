package com.quicktvui.support.player.manager.definition;

/**
 * 清晰度
 */
public enum Definition {
    UNKNOWN(-1, "未知"),//流畅
    SD(0, "标清"),//标清 720P
    HD(1, "高清"),//高清 1080P
    FULL_HD(2, "超清720"),//超清
    ORIGINAL(3, "原画"),//原始
    BLUERAY(4, "蓝光"),//蓝光
    FOURK(5, "4K");//4K

    private int value;
    private String name;

    Definition(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return this.value;
    }

    public String getName() {
        return name;
    }

    public static Definition getDefinition(int value) {
        Definition[] definitions = values();
        for (Definition d : definitions) {
            if (d.value == value) {
                return d;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Definition{" +
                "value=" + value +
                ", name='" + name + '\'' +
                '}';
    }
}
