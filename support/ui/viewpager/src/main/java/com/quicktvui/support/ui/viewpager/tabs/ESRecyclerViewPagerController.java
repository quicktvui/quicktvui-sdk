package com.quicktvui.support.ui.viewpager.tabs;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.hippyext.views.fastlist.Utils;
import com.tencent.mtt.hippy.annotation.HippyController;
import com.tencent.mtt.hippy.annotation.HippyControllerProps;
import com.tencent.mtt.hippy.common.HippyArray;
import com.tencent.mtt.hippy.common.HippyMap;
import com.tencent.mtt.hippy.uimanager.HippyViewController;
import com.tencent.mtt.hippy.utils.LogUtils;
import com.tencent.mtt.hippy.utils.PixelUtil;

@ESKitAutoRegister
@HippyController(name = ESRecyclerViewPagerController.CLASS_NAME)
public class ESRecyclerViewPagerController extends HippyViewController<RecyclerViewPager> {
    public static final String CLASS_NAME = "RecyclerViewPager";

    private static final String FUNC_SET_PAGE = "setPage";
    private static final String FUNC_SET_PAGE_WITHOUT_ANIM = "setPageWithoutAnimation";

    private static final String FUNC_SET_INDEX = "setIndex";
    private static final String FUNC_NEXT_PAGE = "next";
    private static final String FUNC_PREV_PAGE = "prev";
    private static final String FUNC_PAGE_TRANSLATION_LEFT = "translationLeft";
    private static final String FUNC_PAGE_TRANSLATION_RIGHT = "translationRight";

    @Override
    protected View createViewImpl(Context context) {
        return null;
    }

    @Override
    protected void addView(ViewGroup parentView, View view, int index) {
        //FastList 不将数据添加到view树里
//        super.addView(parentView, view, index);
    }

    @Override
    protected View createViewImpl(Context context, HippyMap iniProps) {
        boolean isVertical = false;
        boolean enableTransform = false;
        boolean isSlidingEnable = false;
        boolean isTouchClickModeEnable = false;
        int firstTranslation = 500;
        int rightTranslation = 1500;
        int leftTranslation = 1000;
        int animationDuration = 1000;
        int interpolatorType = 1;
        boolean requestAutofocusOnPageChange = false;
        if (iniProps != null) {
            if ((iniProps.containsKey("direction") && iniProps.getString("direction").equals("vertical")) || iniProps.containsKey("vertical")) {
                isVertical = true;
            }
            if (iniProps.containsKey("disableTransform")) {
                enableTransform = false;
            } else {
                try {
                    int level = Utils.getRoughPerformance(context);
                    if (level < Utils.ROUGH_PERFORMANCE_MEDIUM) {
                        //低端机直接关闭动画
                        enableTransform = false;
                    }
                } catch (Throwable t) {
                }
            }
//            if(iniProps.containsKey("checkAutofocusOnPageChange")){
//                requestAutofocusOnPageChange = iniProps.getBoolean("checkAutofocusOnPageChange");
//            }
        }
            if ((iniProps.containsKey("slidingEnable") && iniProps.getBoolean("slidingEnable"))) {
                isSlidingEnable = iniProps.getBoolean("slidingEnable");
            }
            if ((iniProps.containsKey("initTranslation") && iniProps.getInt("initTranslation") > 0)) {
                firstTranslation = iniProps.getInt("initTranslation");
                firstTranslation = (int) PixelUtil.dp2px(firstTranslation);
            }
            if ((iniProps.containsKey("rightTranslation") && iniProps.getInt("rightTranslation") > 0)) {
                rightTranslation = iniProps.getInt("rightTranslation");
                rightTranslation = (int) PixelUtil.dp2px(rightTranslation);
            }
            if ((iniProps.containsKey("leftTranslation") && iniProps.getInt("leftTranslation") > 0)) {
                leftTranslation = iniProps.getInt("leftTranslation");
                leftTranslation = (int) PixelUtil.dp2px(leftTranslation);
            }
            if (iniProps.containsKey("duration") && iniProps.getInt("duration") > 0) {
                animationDuration = iniProps.getInt("duration");
            }
            if (iniProps.containsKey("interpolatorType") && iniProps.getInt("interpolatorType") > 0) {
                interpolatorType = iniProps.getInt("interpolatorType");
            }
        if ((iniProps.containsKey("useTouchClickMode") && iniProps.getBoolean("useTouchClickMode"))) {
            isTouchClickModeEnable = iniProps.getBoolean("useTouchClickMode");//todo 是否需要加参数 处理安卓原生touch和click
        }

        RecyclerViewPager recyclerViewPager = null;
//        FastListPageContentAdapter adapter = new FastListPageContentAdapter(context, recyclerViewPager, iniProps);
        PageContentAdapter adapter = null;
        if(iniProps.containsKey("singleContent")){
            recyclerViewPager =  new RecyclerViewPager(context, isVertical, enableTransform, isSlidingEnable, firstTranslation, rightTranslation, leftTranslation, animationDuration, interpolatorType);
            adapter = new SingleTabContentAdapter(context,recyclerViewPager,iniProps);
        }else{
            recyclerViewPager =  new RecyclerViewPager(context, isVertical, enableTransform, isSlidingEnable, firstTranslation, rightTranslation, leftTranslation, animationDuration, interpolatorType);
            adapter =  new FastListPageContentAdapter(context, recyclerViewPager, iniProps);
        }
//        adapter.setRequestAutofocusOnPageChange(requestAutofocusOnPageChange);
        recyclerViewPager.setContentFactory(adapter);
        return recyclerViewPager;
    }

    //zhaopeng add
    @HippyControllerProps(name = "focusSearchEnabled", defaultBoolean = false, defaultType = HippyControllerProps.BOOLEAN)
    public void setFocusSearchEnabled(RecyclerViewPager viewPager, boolean value) {
        viewPager.setFocusSearchEnabled(value);
    }

    @HippyControllerProps(name = "useAdvancedFocusSearch", defaultType = HippyControllerProps.BOOLEAN)
    public void setUseAdvancedFocusSearch(RecyclerViewPager view, boolean flag) {
        view.setUseAdvancedFocusSearch(flag);
    }


    //zhaopeng add
    @HippyControllerProps(name = "listenFocusSearchOnFail", defaultType = HippyControllerProps.BOOLEAN, defaultBoolean = false)
    public void setListenFocusSearchOnFail(RecyclerViewPager view, boolean listen) {
        //view.setListenFocusSearchOnFail(listen);
    }

    //zhaopeng add
    @HippyControllerProps(name = "enableTransform", defaultType = HippyControllerProps.BOOLEAN)
    public void setEnableTransform(RecyclerViewPager view, boolean enable) {
        view.setPageTransformer(enable);
    }

    //zhaopeng add
    @HippyControllerProps(name = "offscreenPageLimit", defaultType = HippyControllerProps.NUMBER)
    public void setOffscreenPageLimit(RecyclerViewPager view, int limit) {
        view.setOffscreenPageLimit(limit);
    }

    //xd add
    @HippyControllerProps(name = "slidingMode", defaultType = HippyControllerProps.STRING, defaultString = "")
    public void setSlidingMode(RecyclerViewPager view, String mode) {
        view.setSlidingMode(mode);
    }

    @Override
    public void dispatchFunction(RecyclerViewPager view, String functionName, HippyArray var) {
        super.dispatchFunction(view, functionName, var);
        if(LogUtils.isDebug()) {
            Log.i(RecyclerViewPager.TAG, "--->dispatchFunction funcName :" + functionName + ",var:" + var);
        }
        int curr = view.vp2.getCurrentItem();
        switch (functionName) {
//      case "setPageList":
//        view.setPageList(var);
//        break;
            case "setInitialInfo":
//                view.setInitInfo(var.getInt(0),var.getInt(1));
                break;
            case "setPageData":
                view.setPageData(var.getInt(0), var.getMap(1), var.getArray(2));
                break;
            case FUNC_SET_PAGE:
                if (var != null) {
                    Object selected = var.get(0);
                    if (selected instanceof Integer) {
                        view.requestSwitchToPage((int) selected, true);
                    }
                }
                break;
            case FUNC_SET_PAGE_WITHOUT_ANIM:
                if (var != null) {
                    Object selected = var.get(0);
                    if (selected instanceof Integer) {
                        view.requestSwitchToPage((int) selected, false);
                    }
                }
                break;
            case FUNC_SET_INDEX:
                if (var != null && var.size() > 0) {
                    HippyMap paramsMap = var.getMap(0);
                    if (paramsMap != null && paramsMap.size() > 0 && paramsMap
                            .containsKey("index")) {
                        int index = paramsMap.getInt("index");
                        boolean animated = !paramsMap.containsKey("animated") || paramsMap
                                .getBoolean("animated");
                        view.requestSwitchToPage(index, animated);
                    }
                }
                break;
            case FUNC_NEXT_PAGE:
                int total = view.vp2.getAdapter().getItemCount();
                if (curr < total - 1) {
                    view.requestSwitchToPage(curr + 1, true);
                }
                break;
            case FUNC_PREV_PAGE:
                if (curr > 0) {
                    view.requestSwitchToPage(curr - 1, true);
                }
                break;
            case FUNC_PAGE_TRANSLATION_LEFT:
                if (var != null && var.size() > 0) {
                    view.translationLeft();
                }
                break;
            case FUNC_PAGE_TRANSLATION_RIGHT:
                if (var != null && var.size() > 0) {
                    view.translationRight();
                }
                break;
            default:
                break;
        }
    }

    //TODO 实现promise
//  @SuppressWarnings("SwitchStatementWithTooFewBranches")
//  @Override
//  public void dispatchFunction(ListViewPager view, String functionName, HippyArray params,
//                               Promise promise) {
//    if (view == null) {
//      return;
//    }
//
//    switch (functionName) {
//      case FUNC_SET_INDEX:
//        if (params != null && params.size() > 0) {
//          HippyMap paramsMap = params.getMap(0);
//          if (paramsMap != null && paramsMap.size() > 0 && paramsMap
//            .containsKey("index")) {
//            int index = paramsMap.getInt("index");
//            boolean animated = !paramsMap.containsKey("animated") || paramsMap
//              .getBoolean("animated");
//            //view.setCallBackPromise(promise);
//           // view.switchToPage(index, animated);
//            return;
//          }
//        }
//
//        if (promise != null) {
//          String msg = "invalid parameter!";
//          HippyMap resultMap = new HippyMap();
//          resultMap.pushString("msg", msg);
//          promise.resolve(resultMap);
//        }
//        break;
//      default:
//        break;
//    }
//  }
}
