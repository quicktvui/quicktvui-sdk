package com.quicktvui.support.player.manager.decode;

/**
 * 解码
 */
public enum Decode {
    IJK(0, "硬解IJK"),//
    EXO(1, "软解EXO"),//
    HARDWARE(2, "硬解"),//
    IJK_SOFT(3, "软解IJK");//

    private int value;
    private String name;

    Decode(int value, String name) {
        this.value = value;
        this.name = name;
    }

    public int getValue() {
        return this.value;
    }

    public String getName() {
        return name;
    }

    public static Decode getDecode(int value) {
        Decode[] definitions = values();
        for (Decode d : definitions) {
            if (d.value == value) {
                return d;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return "Decode{" +
                "value=" + value +
                ", name='" + name + '\'' +
                '}';
    }
}
