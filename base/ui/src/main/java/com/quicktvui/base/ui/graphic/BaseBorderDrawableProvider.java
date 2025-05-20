package com.quicktvui.base.ui.graphic;

import java.util.concurrent.ConcurrentHashMap;

public interface BaseBorderDrawableProvider<T extends ConcurrentHashMap<Integer, BaseBorderDrawable>> {
  T create();
}
