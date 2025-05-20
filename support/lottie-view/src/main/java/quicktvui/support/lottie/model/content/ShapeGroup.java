package quicktvui.support.lottie.model.content;

import java.util.Arrays;
import java.util.List;

import quicktvui.support.lottie.LottieComposition;
import quicktvui.support.lottie.LottieDrawable;
import quicktvui.support.lottie.animation.content.Content;
import quicktvui.support.lottie.animation.content.ContentGroup;
import quicktvui.support.lottie.model.layer.BaseLayer;

public class ShapeGroup implements ContentModel {
  private final String name;
  private final List<ContentModel> items;
  private final boolean hidden;

  public ShapeGroup(String name, List<ContentModel> items, boolean hidden) {
    this.name = name;
    this.items = items;
    this.hidden = hidden;
  }

  public String getName() {
    return name;
  }

  public List<ContentModel> getItems() {
    return items;
  }

  public boolean isHidden() {
    return hidden;
  }

  @Override public Content toContent(LottieDrawable drawable, LottieComposition composition, BaseLayer layer) {
    return new ContentGroup(drawable, layer, this, composition);
  }

  @Override public String toString() {
    return "ShapeGroup{" + "name='" + name + "\' Shapes: " + Arrays.toString(items.toArray()) + '}';
  }
}
