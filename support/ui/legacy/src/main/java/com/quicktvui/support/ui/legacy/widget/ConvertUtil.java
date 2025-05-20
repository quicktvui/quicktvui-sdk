package com.quicktvui.support.ui.legacy.widget;

import android.content.res.TypedArray;


class ConvertUtil {

    public static int convertPixel(TypedArray array, int index, int defValue) {
        if (array == null) {
            return defValue;
        }

        int value = array.getDimensionPixelSize(index, defValue);


        return value;
    }


}
