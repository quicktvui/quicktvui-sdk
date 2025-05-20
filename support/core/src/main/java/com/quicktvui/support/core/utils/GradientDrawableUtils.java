package com.quicktvui.support.core.utils;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;

public class GradientDrawableUtils {

    public static GradientDrawable createGradientDrawable(EsMap map) {
        try {
            int type = GradientDrawable.LINEAR_GRADIENT;
            if (map.containsKey("type")) {
                type = map.getInt("type");
            }
            int shape = GradientDrawable.RECTANGLE;
            if (map.containsKey("shape")) {
                shape = map.getInt("shape");
            }
            GradientDrawable.Orientation orientation = GradientDrawable.Orientation.TOP_BOTTOM;
            if (map.containsKey("orientation")) {
                int orientationValue = map.getInt("orientation");
                switch (orientationValue) {
                    case 1:
                        orientation = GradientDrawable.Orientation.TR_BL;
                        break;
                    case 2:
                        orientation = GradientDrawable.Orientation.RIGHT_LEFT;
                        break;
                    case 3:
                        orientation = GradientDrawable.Orientation.BR_TL;
                        break;
                    case 4:
                        orientation = GradientDrawable.Orientation.BOTTOM_TOP;
                        break;
                    case 5:
                        orientation = GradientDrawable.Orientation.BL_TR;
                        break;
                    case 6:
                        orientation = GradientDrawable.Orientation.LEFT_RIGHT;
                        break;
                    case 7:
                        orientation = GradientDrawable.Orientation.TL_BR;
                        break;
                }
            }

            EsArray colorArray = map.getArray("colors");
            if (colorArray == null) {
                return null;
            }
            int[] colors = new int[colorArray.size()];
            try {
                for (int i = 0; i < colors.length; i++) {
                    final String colorStr = colorArray.getString(i);
                    colors[i] = Color.parseColor(colorStr);
                }
            } catch (Exception e) {
                return null;
            }

            final GradientDrawable g = new GradientDrawable(orientation, colors);
            g.setShape(shape);
            g.setGradientType(type);
            if (map.containsKey("gradientRadius")) {
                g.setGradientRadius(map.getInt("gradientRadius"));
            }
            if (map.containsKey("cornerRadius")) {
                g.setCornerRadius((float) map.getDouble("cornerRadius"));
            }
            if (map.containsKey("cornerRadii4")) {
                EsArray array = map.getArray("cornerRadii4");
                if (array.size() != 4) {
                    throw new IllegalArgumentException("cornerRadii4 size need 8");
                }
                g.setCornerRadii(new float[]{(float) array.getDouble(0),//
                        (float) array.getDouble(0),//
                        (float) array.getDouble(1),//
                        (float) array.getDouble(1),//
                        (float) array.getDouble(2),//
                        (float) array.getDouble(2),//
                        (float) array.getDouble(3),//
                        (float) array.getDouble(3),//
                });
            }
            if (map.containsKey("cornerRadii8")) {
                EsArray array = map.getArray("cornerRadii8");
                if (array.size() != 8) {
                    throw new IllegalArgumentException("cornerRadii8 size need 8");
                }
                float[] radii = new float[array.size()];
                for (int i = 0; i < radii.length; i++) {
                    radii[i] = (float) array.getDouble(i);
                }
                g.setCornerRadii(radii);
            }
            return g;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }

}
