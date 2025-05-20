package com.quicktvui.base.ui;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Create by weipeng on 2022/06/10 15:12
 * Describe 用于Debug状态下的bundle缓存。
 *          debug状态下会加载本地的index.bundle，有的动辄10几M，造成debug状态特别慢
 *          因此，做一些缓存，提高加载速度
 */
public class DebugCache {

  private final Map<String, ByteBuffer> CACHE = new ConcurrentHashMap<>();

  public void cache(String uri, ByteBuffer buffer) {
    CACHE.put(uri, buffer);
  }

  public ByteBuffer restore(String uri) {
    return CACHE.get(uri);
  }

  public void release() {
    CACHE.clear();
  }

  //region 单例

  private static final class DebugCacheHolder{
      private static final DebugCache INSTANCE = new DebugCache();
  }

  public static DebugCache get(){
      return DebugCacheHolder.INSTANCE;
  }

  private DebugCache(){}

  //endregion
}
