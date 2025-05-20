package quicktvui.support.lottie.model.content;

import quicktvui.support.lottie.LottieComposition;
import quicktvui.support.lottie.LottieDrawable;
import quicktvui.support.lottie.animation.content.Content;
import quicktvui.support.lottie.animation.content.TrimPathContent;
import quicktvui.support.lottie.model.animatable.AnimatableFloatValue;
import quicktvui.support.lottie.model.layer.BaseLayer;

public class ShapeTrimPath implements ContentModel {

  public enum Type {
    SIMULTANEOUSLY,
    INDIVIDUALLY;

    public static Type forId(int id) {
      switch (id) {
        case 1:
          return SIMULTANEOUSLY;
        case 2:
          return INDIVIDUALLY;
        default:
          throw new IllegalArgumentException("Unknown trim path type " + id);
      }
    }
  }

  private final String name;
  private final Type type;
  private final AnimatableFloatValue start;
  private final AnimatableFloatValue end;
  private final AnimatableFloatValue offset;
  private final boolean hidden;

  public ShapeTrimPath(String name, Type type, AnimatableFloatValue start,
      AnimatableFloatValue end, AnimatableFloatValue offset, boolean hidden) {
    this.name = name;
    this.type = type;
    this.start = start;
    this.end = end;
    this.offset = offset;
    this.hidden = hidden;
  }

  public String getName() {
    return name;
  }

  public Type getType() {
    return type;
  }

  public AnimatableFloatValue getEnd() {
    return end;
  }

  public AnimatableFloatValue getStart() {
    return start;
  }

  public AnimatableFloatValue getOffset() {
    return offset;
  }

  public boolean isHidden() {
    return hidden;
  }

  @Override public Content toContent(LottieDrawable drawable, LottieComposition composition, BaseLayer layer) {
    return new TrimPathContent(layer, this);
  }

  @Override public String toString() {
    return "Trim Path: {start: " + start + ", end: " + end + ", offset: " + offset + "}";
  }
}
