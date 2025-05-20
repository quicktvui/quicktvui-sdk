package com.quicktvui.base.ui.graphic;

import java.util.concurrent.ConcurrentHashMap;

public class BaseBorderDrawableFactory implements BaseBorderDrawableProvider<ConcurrentHashMap<Integer, BaseBorderDrawable>> {

  @Override
  public ConcurrentHashMap<Integer, BaseBorderDrawable> create() {
    ConcurrentHashMap<Integer, BaseBorderDrawable> hashMap = new ConcurrentHashMap<>();
    hashMap.put(0, new BorderFrontDrawable());
    return hashMap;
  }
}
