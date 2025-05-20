package com.quicktvui.base.ui.waterfall;

import android.view.View;

import com.quicktvui.base.ui.TVViewUtil;


public class WaterfallUtils {

  public static String debugChunk(Chunk chunk) {
    // Log the chunk
    if (chunk == null) {
      return "null";
    }
    View view = chunk.getView();
    return "Chunk: " + TVViewUtil.debugView(view);
  }

  public static String getChunkSID(Chunk chunk) {
    // Get the SID of the chunk
    if (chunk == null) {
      return null;
    }
    View view = chunk.getView();
    return TVViewUtil.getViewSID(view);
  }
}
