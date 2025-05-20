/*
 * Copyright (c) 2021, the hapjs-platform Project Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.quicktvui.support.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.support.canvas.canvas2d.CanvasView2D;
import com.tencent.mtt.hippy.HippyInstanceContext;
import com.tencent.mtt.hippy.HippyInstanceLifecycleEventListener;
import com.tencent.mtt.hippy.HippyRootView;
import com.tencent.mtt.hippy.annotation.HippyController;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.uimanager.ControllerManager;
import com.tencent.mtt.hippy.uimanager.HippyViewController;
import com.tencent.mtt.hippy.uimanager.RenderNode;
import com.tencent.mtt.hippy.utils.LogUtils;

import com.quicktvui.sdk.base.EsCallback;
import com.quicktvui.sdk.base.args.EsMap;

import com.quicktvui.sdk.base.core.EsProxy;

//CanvasComponent 组件
@ESKitAutoRegister
@HippyController(name = ESCanvasController.CLASS_NAME)
public class ESCanvasController extends HippyViewController<CanvasView2D> {
    public static final String CLASS_NAME = "CanvasView2D";

    // methods
    private static final String TAG = "ESCanvasController";
    private CanvasManager mCanvasManager;

    private CanvasView mCanvasView;
    private final StringBuilder builder = new StringBuilder();

    private void dealWithAction(String eventName, HippyArray params, View view) {
        //fillStyle!#ff0000;fillRect!0,0,100,100;fillStyle!#FF018786;fillRect!50,50,200,200
        if (params == null) {
            return;
        }
        switch (eventName) {
            case "fillStyle"://绘制填充样式
                if (isColorAction(params)) {
                    builder.append("AA");
                }
                break;
            case "fillRect"://绘制矩形
                builder.append("Z?");
                break;
            case "rect"://绘制矩形
                builder.append("f?");
                break;
            case "strokeStyle"://描边填充样式
                if (isColorAction(params)) {
                    builder.append("NA");
                }
                break;
            case "strokeRect"://描边绘制矩形
                builder.append("n?");
                break;
            case "clearRect"://擦除矩形区域
                builder.append("U?");
                break;
            case "setLineDash"://设置虚线线段和间隙长度k
                builder.append("k?");
                break;
            case "lineDashOffset"://设置虚线的起始偏移量F
                builder.append("F?");
                break;
            case "lineWidth"://设置线宽度H
                builder.append("H?");
                break;
            case "drawImage"://设置图片X
                builder.append("X?");
                String url = params.getString(0);
                EsMap data = new EsMap();
                data.pushString("url", fixImageUrl(url));
                if (LogUtils.isDebug()) {
                    Log.d(TAG, "设置drawImage图片");
                }
                EsProxy.get().loadImageBitmap(data, new EsCallback<Bitmap, Throwable>() {
                    @Override
                    public void onSuccess(Bitmap bitmap) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            CanvasManager.getInstance().setBitmap(view.getId(), bitmap);
                            if (LogUtils.isDebug()) {
                                view.invalidate();
                                Log.d(Constants.TAG, "拿到并 保存Bitmap--->" + view.getId());
                            }
                        }
                    }

                    @Override
                    public void onFailed(Throwable throwable) {
                    }
                });
                break;
            case "fillText"://设置文字a
                builder.append("a?");
                break;
            case "strokeText"://设置文字o
                builder.append("o?");
                break;
            case "textAlign"://设置文字对齐方式O
                builder.append("O?");
                break;
            case "textBaseline"://设置文字基线
                builder.append("P?");
                break;
            case "font"://设置文字大小B
                builder.append("B?");
                break;
            case "beginPath"://重置路径S
                builder.append("S?");
                break;
            case "moveTo"://路径
                builder.append("c?");
                break;
            case "arc"://圆弧Q
                builder.append("Q?");
                break;
            case "arcTo"://圆弧R
                builder.append("R?");
                break;
            case "lineTo"://线b
                builder.append("b?");
                break;
            case "closePath"://关闭路径
                builder.append("W?");
                break;
            case "stroke"://描边
                builder.append("m?");
                break;
            case "fill"://填充
                builder.append("Y?");
                break;
            case "clip"://裁切
                builder.append("V?");
                break;
            case "save"://保存i
                builder.append("i?");
                break;
            case "restore"://恢复g
                builder.append("g?");
                break;
            case "quadraticCurveTo"://贝塞尔曲线
                builder.append("e?");
                break;
            case "bezierCurveTo"://贝塞尔曲线
                builder.append("T?");
                break;
            case "globalAlpha"://绘图的当前透明值
                builder.append("C?");
                break;
            case "globalCompositeOperation"://合成
                builder.append("D?");
                break;
            case "miterLimit"://设定外延交点与连接点的最大距离
                builder.append("I?");
                break;
            case "lineCap"://线帽
                builder.append("E?");
                break;
            case "lineJoin"://设置两条线相交的拐点的样式。属性值为“bevel”、“round”、“miter”。
                builder.append("G?");
                break;
            case "shadowBlur"://设置阴影
                builder.append("J?");
                break;
            case "shadowColor"://设置阴影颜色
                builder.append("K?");
                break;
            case "shadowOffsetX"://设置x轴阴影
                builder.append("L?");
                break;
            case "shadowOffsetY"://设置y轴阴影
                builder.append("M?");
                break;
            case "translate"://平移
                builder.append("q?");
                break;
            case "rotate"://旋转
                builder.append("h?");
                break;
            case "scale"://缩放
                builder.append("j?");
                break;
            case "transform"://变形
                builder.append("p?");
                break;
            case "setTransform"://变形
                builder.append("l?");
                break;
        }
        if (params.size() > 0) {
            builder.append(list2String(params));
        }
        builder.append(";");
    }

    public String list2String(HippyArray arrayList) {
        StringBuilder result = new StringBuilder();
        if (arrayList != null && arrayList.size() > 0) {
            for (int i = 0; i < arrayList.size(); i++) {
                if (i < arrayList.size() - 1) {
                    result.append(arrayList.getString(i)).append(",");
                } else {
                    result.append(arrayList.getString(i));
                }
            }
        }
        return result.toString();
    }

    public CanvasViewContainer getHostView() {
        return null;//todo 这里重新new了个 viewgroup可能有问题
    }

    @Deprecated
    public CanvasView getCanvasView() {
        return mCanvasView;
    }

    @Deprecated
    public void setCanvasView(CanvasView canvasView) {
        this.mCanvasView = canvasView;
    }

    private boolean isColorAction(HippyArray params) {
        if (params != null) {
            if (params.getString(0).substring(0, 1).startsWith("#") || params.getString(0).substring(0, 3).startsWith("rgb") || params.getString(0).substring(0, 4).startsWith("rgba")) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected View createViewImpl(Context context) {
        CanvasView2D currentView = new CanvasView2D(context);
        ((HippyInstanceContext) currentView.getContext()).getEngineContext().addInstanceLifecycleEventListener(new HippyInstanceLifecycleEventListener() {
            @Override
            public void onInstanceLoad(int i) {
                if (LogUtils.isDebug()) {
                    Log.d(TAG, "onInstanceLoad: -----------" + i);
                }
            }

            @Override
            public void onInstanceResume(int i) {
                if (LogUtils.isDebug()) {
                    Log.d(TAG, "onInstanceResume: -----------" + i);
                }
            }

            @Override
            public void onInstancePause(int i) {
                if (LogUtils.isDebug()) {
                    Log.d(TAG, "onInstancePause: -----------" + i);
                }
            }

            @Override
            public void onInstanceDestroy(int i) {
                if (LogUtils.isDebug()) {
                    Log.d(TAG, "onInstanceDestroy: -----------" + i);
                }
                if (mCanvasManager != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        mCanvasManager.destroyData();
                        mCanvasManager.removeCanvas(mCanvasManager.getCanvas(i, i));
                    }
                }
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mCanvasManager = CanvasManager.getInstance();
            //mCanvasManager.setCanvas(this);
            mCanvasView = currentView;
        }
        return currentView;
    }


    @Override
    public RenderNode createRenderNode(int id, HippyMap props, String className, HippyRootView hippyRootView, ControllerManager controllerManager, boolean lazy) {
        //将业务逻辑集中在TabsNode中处理
        return new CanvasNode(id, props, className, hippyRootView, controllerManager, lazy);
    }

    @Override
    public void dispatchFunction(CanvasView2D view, String functionName, HippyArray params) {
        super.dispatchFunction(view, functionName, params);
        if (functionName.equals("destoryView")) {
            if (mCanvasManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mCanvasManager.destroyData();
                    mCanvasManager.removeCanvas(view);
                    mCanvasManager.recycleBitmap();
                    mCanvasManager.clearAction();
                }
            }
            return;
        }
        if (functionName.equals("drawFinish")) {
            if (mCanvasManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    mCanvasManager.clearAction();
                    mCanvasManager.clearDrawnBitmaps();
                }
            }
            return;
        }
        if (!functionName.equals("drawAction")) { //执行绘制指令
            dealWithAction(functionName, params, view);
            //调用一次命令，刷新一次页面
            //view.postInvalidate();
        } else {
            try {
                if (params != null) {
                    String actionStr = builder.toString();
                    if (!actionStr.isEmpty()) {
                        if (LogUtils.isDebug()) {
                            Log.d(TAG, "dispatchFunction: ---------->" + actionStr);
                        }
                        actionStr = actionStr.substring(0, actionStr.length() - 1);
                        if (LogUtils.isDebug()) {
                            Log.d(TAG, "processAsyncActions: ---------->" + actionStr);
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            CanvasActionHandler.getInstance().processAsyncActions(view.getId(), view.getId(), actionStr);
                        }
                        if (LogUtils.isDebug()) {
                            Log.d(Constants.TAG, "processAsyncActions: ---------->" + "pageId:" + view.getId() + "ref:" + view.getId() + actionStr);
                        }
                        builder.delete(0, builder.length());
                        if (LogUtils.isDebug()) {
                            Log.d(TAG, "dispatchFunction: ---------->" + actionStr);
                        }
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    private String fixImageUrl(String url) {
        if (url.startsWith("http:") || url.startsWith("https")) {
            return url.replaceAll(" ", "%20");
        }
        if (EsProxy.get().isDebugModel()) {
            if (url.startsWith("file://") || url.startsWith("http://127.0.0.1")) {
                int index = url.indexOf("assets");
                if (index >= 0)
                    return "http://" + EsProxy.get().getDebugServer() + "/" + url.substring(index);
            }
        }
        return url.replaceAll(" ", "%20");
    }
}
