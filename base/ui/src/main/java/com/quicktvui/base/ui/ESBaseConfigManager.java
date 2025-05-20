package com.quicktvui.base.ui;

/**
 * 全局配置
 * created by wanglei on 2023/8/3
 */
public abstract class ESBaseConfigManager implements IFESConfigManager{
  //焦点抖动
  //默认关闭焦点抖动
//  private boolean isShakeSelf = false;
//  private boolean isListShakeSelf = true;
//  private int focusBorderType = 0;

  public ESBaseConfigManager() {
  }

  //  public void doConfigs(String packageJson) {
//    if (TextUtils.isEmpty(packageJson)) {
//      Log.e("ESConfigManager", "doConfigs: ------解析packageJson失败-----错误原因：packageJson = null");
//      return;
//    }
//    try {
//      JSONObject packageObj = new JSONObject(packageJson);
//      if (packageJson.contains(SHAKESELF)) {
//        this.isShakeSelf = packageObj.optBoolean(SHAKESELF);
//      }
//      if (packageJson.contains(LIST_SHAKESELF)) {
//        this.isListShakeSelf = packageObj.optBoolean(LIST_SHAKESELF);
//      }
//      if (packageJson.contains(FOCUS_BORDER_TYPE)) {
//        this.focusBorderType = packageObj.optInt(FOCUS_BORDER_TYPE);
//      }
//    } catch (JSONException e) {
//      e.printStackTrace();
//      Log.e("ESConfigManager", "doConfigs: ------解析packageJson失败-----");
//    }
//  }

//  public boolean isShakeSelf() {
//    return isShakeSelf;
//  }
//
//  public void setShakeSelf(boolean shakeSelf) {
//    isShakeSelf = shakeSelf;
//  }

//  @Override
//  public boolean IsShakeSelf() {
//    return false;
//  }
//
//  @Override
//  public boolean IsListShakeSelf() {
//    return false;
//  }
//
//  @Override
//  public int getFocusBorderType() {
//    return 0;
//  }

//  public int getFocusBorderType() {
//    return focusBorderType;
//  }
//
//  public void setFocusBorderType(int focusBorderType) {
//    this.focusBorderType = focusBorderType;
//  }
//
//  public boolean isListShakeSelf() {
//    return isListShakeSelf;
//  }
//
//  public void setListShakeSelf(boolean listShakeSelf) {
//    isListShakeSelf = listShakeSelf;
//  }
}
