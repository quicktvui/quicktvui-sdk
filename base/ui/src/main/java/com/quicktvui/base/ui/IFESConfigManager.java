package com.quicktvui.base.ui;

public interface IFESConfigManager {
  /**
   * 解析packageJson
   * @param packageJson
   */
  void doConfigs(String packageJson);
  /**
   * 是否开启hippyViewGroup的焦点抖动
   */
  boolean IsShakeSelf();
  /**
   * 是否开启list的焦点抖动
   */
  boolean IsListShakeSelf();
  /**
   * 获取选中边框样式
   */
  int getFocusBorderType();
  /**
   * runtime最低版本号
   */
  int getMinRuntime();
}
