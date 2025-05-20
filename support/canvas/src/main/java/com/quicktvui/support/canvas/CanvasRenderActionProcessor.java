/*
 * Copyright (c) 2021, the hapjs-platform Project Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.quicktvui.support.canvas;

import android.text.TextUtils;

import com.quicktvui.support.canvas.canvas2d.Parser;

import java.util.ArrayList;
import java.util.Map;

public class CanvasRenderActionProcessor {

    private static final String TAG = "CanvasRenderActionProcessor";

    private Map<Character, Parser> mParsers;

    public CanvasRenderActionProcessor() {
        mParsers = Parser.create();
    }

    public ArrayList<Action> process(int pageId, int refId, String renderCommand) {
        if (TextUtils.isEmpty(renderCommand)) {
            return null;
        }
        //fillRect?(0, 0, 100, 100);fillStyle?#ff0000
        String[] commands = renderCommand.split(";");
        if (commands.length <= 0) {
            return null;
        }

        ArrayList<Action> actions = new ArrayList<>();

        for (String command : commands) {
            if (TextUtils.isEmpty(command) || command.length() < 2) {
                continue;
            }
            // 标识方法名称，比如fillStyle，lineCap
            char flag = command.charAt(0);//AA!#
            // 标识参数类型，例如fillStyle的参数可以为color，gradient等
            char type = command.charAt(1);
            String parameter = command.substring(2);
            Parser parser = mParsers.get(flag);
            if (parser == null) {
                continue;
            }

            try {
                Action action = parser.parse(pageId, refId, type, parameter);
                if (action != null) {
                    actions.add(action);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return actions;
    }
}
