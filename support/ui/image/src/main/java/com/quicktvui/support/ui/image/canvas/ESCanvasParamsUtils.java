package com.quicktvui.support.ui.image.canvas;

import android.graphics.Color;

import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;

import java.util.ArrayList;
import java.util.List;


public class ESCanvasParamsUtils {

    public static List<ESPath> pathArrayToList(EsArray array) {
        List<ESPath> pathList = new ArrayList<>(array.size());
        for (int i = 0; i < array.size(); i++) {
            EsMap map = array.getMap(i);
            ESPath path = pathMapToBean(map);
            if (path != null) {
                pathList.add(path);
            }
        }
        return pathList;
    }

    public static ESPath pathMapToBean(EsMap map) {
        if (map == null) {
            return null;
        }
        ESPath path = new ESPath();
        if (map.containsKey("action")) {
            int action = map.getInt("action");
            path.setAction(action);
        }
        if (map.containsKey("x1")) {
            long x1 = map.getLong("x1");
            path.setX1(x1);
        }
        if (map.containsKey("y1")) {
            long y1 = map.getLong("y1");
            path.setY1(y1);
        }
        if (map.containsKey("x2")) {
            long x2 = map.getLong("x2");
            path.setX2(x2);
        }
        if (map.containsKey("y2")) {
            long y2 = map.getLong("y2");
            path.setY2(y2);
        }
        if (map.containsKey("x3")) {
            long x3 = map.getLong("x3");
            path.setX3(x3);
        }
        if (map.containsKey("y3")) {
            long y3 = map.getLong("y3");
            path.setY3(y3);
        }
        if (map.containsKey("startAngle")) {
            long startAngle = map.getLong("startAngle");
            path.setStartAngle(startAngle);
        }
        if (map.containsKey("startAngle")) {
            long startAngle = map.getLong("startAngle");
            path.setStartAngle(startAngle);
        }
        if (map.containsKey("sweepAngle")) {
            long sweepAngle = map.getLong("sweepAngle");
            path.setSweepAngle(sweepAngle);
        }
        if (map.containsKey("forceMoveTo")) {
            boolean forceMoveTo = map.getBoolean("forceMoveTo");
            path.setForceMoveTo(forceMoveTo);
        }
        return path;
    }

    public static List<ESPaint> paintArrayToList(EsArray array) {
        List<ESPaint> paintList = new ArrayList<>(array.size());
        for (int i = 0; i < array.size(); i++) {
            EsMap map = array.getMap(i);
            ESPaint paint = paintMapToBean(map);
            if (paint != null) {
                paintList.add(paint);
            }
        }
        return paintList;
    }

    public static ESPaint paintMapToBean(EsMap map) {
        if (map == null) {
            return null;
        }
        ESPaint paint = new ESPaint();
        if (map.containsKey("action")) {
            int action = map.getInt("action");
            paint.setAction(action);
        }
        if (map.containsKey("color")) {
            String color = map.getString("color");
            paint.setColor(Color.parseColor(color));
        }
        if (map.containsKey("style")) {
            int style = map.getInt("style");
            paint.setStyle(style);
        }
        if (map.containsKey("strokeWidth")) {
            long strokeWidth = map.getLong("strokeWidth");
            paint.setStrokeWidth(strokeWidth);
        }
        if (map.containsKey("mode")) {
            int mode = map.getInt("mode");
            paint.setMode(mode);
        }
        return paint;
    }
}
