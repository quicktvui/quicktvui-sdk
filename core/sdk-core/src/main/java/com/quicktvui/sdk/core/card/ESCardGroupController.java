package com.quicktvui.sdk.core.card;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.hippyext.pm.WindowNode;
import com.tencent.mtt.hippy.HippyInstanceContext;
import com.tencent.mtt.hippy.HippyRootView;
import com.tencent.mtt.hippy.annotation.HippyController;
import com.tencent.mtt.hippy.annotation.HippyControllerProps;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.uimanager.ControllerManager;
import com.tencent.mtt.hippy.uimanager.HippyViewEvent;
import com.tencent.mtt.hippy.uimanager.RenderNode;
import com.tencent.mtt.hippy.views.view.HippyViewGroup;
import com.tencent.mtt.hippy.views.view.HippyViewGroupController;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 卡片根布局
 */
@ESKitAutoRegister
@HippyController(
        name = ESCardGroupController.CLASS_NAME
)
public class ESCardGroupController extends HippyViewGroupController {
    public static final String CLASS_NAME = "ESCardGroupView";

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        super.onFocusChange(v, hasFocus);
        if (v instanceof ESCardGroupView) {
            ESCardView esCardView = findCardView((ESCardGroupView) v);
            if (esCardView != null) {
                HippyViewEvent cardViewEvent = new HippyViewEvent("onFocus");
                HippyMap hm = new HippyMap();
                hm.pushBoolean("isFocused", hasFocus);
                cardViewEvent.send(esCardView.JSEventViewID, ((HippyInstanceContext) esCardView.getContext()).getEngineContext(), hm);
            }
        }
    }

    @Override
    protected View createViewImpl(Context context) {
        return new ESCardGroupView(context);
    }

    @Override
    protected View createViewImpl(Context context, HippyMap iniProps) {
        return super.createViewImpl(context, iniProps);
    }

    @HippyControllerProps(name = "cardId", defaultType = HippyControllerProps.STRING, defaultString = "")
    public void setCardId(View view, String cardId) {
        if (view instanceof ESCardGroupView) {
            ((ESCardGroupView) view).setCardId(cardId);
        }
    }

    @HippyControllerProps(name = "needFocus", defaultType = HippyControllerProps.BOOLEAN, defaultBoolean = true)
    public void setNeedFocus(View view, boolean isNeedFocus) {
        if (view instanceof ESCardGroupView) {
            ((ESCardGroupView) view).setNeedFocus(isNeedFocus);
        }
    }

    @HippyControllerProps(name = "showDefaultBg", defaultType = HippyControllerProps.BOOLEAN, defaultBoolean = true)
    public void setShowDefaultBg(View view, boolean showDefaultBg) {
        if (view instanceof ESCardGroupView) {
            ((ESCardGroupView) view).setShowDefaultBg(showDefaultBg);
        }
    }

    @Override
    public void onAfterCreateView(View view, HippyMap initialProps) {
        super.onAfterCreateView(view, initialProps);
        if (view instanceof ESCardGroupView) {
            if (initialProps.containsKey("needFocus")) {
                ((ESCardGroupView) view).setNeedFocus(initialProps.getBoolean("needFocus"));
            }
            if (initialProps.containsKey("cardId")) {
                ((ESCardGroupView) view).setCardId(initialProps.getString("cardId"));
            }
        }
    }

    @Override
    public void dispatchFunction(HippyViewGroup view, String functionName, HippyArray var) {
        super.dispatchFunction(view, functionName, var);
        switch (functionName) {
            case "load":
                String cardId = var.getString(0);
                boolean useCache = var.getBoolean(1);
                if (cardId != null) {
                    ESCardView esCardView = findCardView(view);
                    if (esCardView != null) {
                        esCardView.load(cardId, useCache);
                    }
                }
                break;
            case "reload":
                ESCardView esCardView = findCardView(view);
                if (esCardView != null) {
                    esCardView.reload();
                }
                break;
            case "showDefaultBg":
                boolean showDefaultBg = var.getBoolean(0);
                if (view instanceof ESCardGroupView) {
                    ((ESCardGroupView) view).setShowDefaultBg(showDefaultBg);
                }
                break;
            case "requestCardFocus":
                if (view instanceof ESCardGroupView) {
                    if (((ESCardGroupView) view).isNeedFocus()) {
                        ESCardView esCardView2 = findCardView(view);
                        if (esCardView2 != null) {
                            esCardView2.makeFocusToCard();
//                            esCardView2.showPlaceHolder(false);
                        }
                    }
                }
                break;
            case "sendCardEvent":
                String eventJson = var.getString(0);
                if (view instanceof ESCardGroupView) {
                    ESCardView esCardView2 = findCardView(view);
                    if (esCardView2 != null) {
                        if (!TextUtils.isEmpty(eventJson)) {
                            try {
                                //TODO
                                JSONObject jsonObject = new JSONObject(eventJson);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
                break;
        }
    }

    private ESCardView findCardView(ViewGroup view) {
        if (view.getParent() != null && view.getParent() instanceof ViewGroup) {
            if (view.getParent() instanceof ESCardView) {
                return (ESCardView) view.getParent();
            } else {
                return findCardView((ViewGroup) view.getParent());
            }
        } else {
            return null;
        }
    }

    @Override
    public void onBatchComplete(HippyViewGroup view) {
        super.onBatchComplete(view);
        if (view instanceof ESCardGroupView) {
            if (((ESCardGroupView) view).isNeedFocus()) {
                ESCardView esCardView = findCardView(view);
                if (esCardView != null) {
                    esCardView.removeLoading();
                    esCardView.makeFocusToCard();
//                    esCardView.showPlaceHolder(false);
                }
            } else {
                ESCardView esCardView = findCardView(view);
                if (esCardView != null) {
                    esCardView.removeLoading();
                    esCardView.showPlaceHolder(false);
                }
            }
        }
    }

    @Override
    public RenderNode createRenderNode(int i, HippyMap hippyMap, String s, HippyRootView hippyRootView, ControllerManager controllerManager, boolean b) {
        //return super.createRenderNode(i, hippyMap, s, hippyRootView, controllerManager, b);
        return new WindowNode(i, hippyMap, s, hippyRootView, controllerManager, b, WindowNode.WindowType.CARD);
    }
}
