package com.quicktvui.base.ui;

public class ESBaseConfigManagerImp extends ESBaseConfigManager{
  @Override
  public void doConfigs(String packageJson) {
    //由外部默认实现
  }

  @Override
  public boolean IsShakeSelf() {
    //默认关闭HippyViewGroup的焦点抖动
    return false;
  }

  @Override
  public boolean IsListShakeSelf() {
    //默认关闭List的焦点抖动
    return false;
  }

  @Override
  public int getFocusBorderType() {
    //默认类型
    return 0;
  }

  @Override
  public int getMinRuntime() {
    return 0;
  }
}
