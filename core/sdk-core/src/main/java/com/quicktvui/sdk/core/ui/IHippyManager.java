package com.quicktvui.sdk.core.ui;

import com.tencent.mtt.hippy.HippyEngine;
import com.tencent.mtt.hippy.HippyEngineContext;
import com.tencent.mtt.hippy.HippyRootView;
import com.tencent.mtt.hippy.common.HippyMap;

/**
 * Create by WeiPeng on 2020/11/11 17:56
 */
public interface IHippyManager {

  // ------------- Hippy初始化 ------------- //
  void initEngine(HippyEngine.EngineListener listener);
  HippyEngineContext getContext();
  boolean isEngineInit();
  boolean isEngineError();
  HippyEngine.EngineState getEngineState();
  void prepareLoadModule();
  HippyRootView makeHippyView(HippyMap args, HippyEngine.ModuleListener listener);

  void destroyHippyView(HippyRootView view);

  // ------------- 与Hippy交互 ------------- //
  void sendNativeEvent(String eventName, Object params) throws Exception;

  void sendUIEvent(int tagId, String eventName, Object params) throws Exception;

  boolean onBackPressed(HippyEngine.BackPressHandler handler);

  void onResume();

  void onPause();

  void onDestroy();
}