package com.quicktvui.base.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import java.util.HashMap;

public class ExtendTag {


  public String nodeClassName;
  public String sid;
  public String nextFocusUpSID;
  public String nextFocusDownSID;
  public String nextFocusLeftSID;
  public String nextFocusRightSID;
  public HashMap<String,String> nextFocusFocusName;
  public String name;
  public int pendingBackGroundColor = -1;

  public HashMap<String,Object> extra;

  public static void putExtraValue(@NonNull View view, @NonNull String key, @NonNull Object value){
    ExtendTag tag = obtainExtendTag(view);
    if(tag.extra == null){
      tag.extra = new HashMap<>();
    }
    tag.extra.put(key,value);
  }

  public Object getExtraValue(@NonNull String key){
    if(extra == null){
      return null;
    }
    return extra.get(key);
  }

  public boolean getBooleanExtraValue(@NonNull String key){
    Object value = getExtraValue(key);
    if(value instanceof Boolean){
      return (Boolean) value;
    }
    return false;
  }


  public static void putTag(View view, @Nullable ExtendTag tag){
    view.setTag(R.id.tag_view_extend_4tv,tag);
  }


  public static @NonNull
  ExtendTag obtainExtendTag(@NonNull View view){
    Object tag = view.getTag(R.id.tag_view_extend_4tv);
    if(tag instanceof ExtendTag){
      return (ExtendTag) tag;
    }else{
      tag = new ExtendTag();
      putTag(view, (ExtendTag) tag);
    }
    return (ExtendTag) tag;
  }

  public static @Nullable
  ExtendTag getExtendTag(@Nullable View view){
    if (view == null) {
      return null;
    }
    Object tag = view.getTag(R.id.tag_view_extend_4tv);
    if(tag instanceof ExtendTag){
      return (ExtendTag) tag;
    }else{
      return null;
    }
  }


}
