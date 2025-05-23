package quicktvui.support.lottie.model.content;

import android.graphics.PointF;

import quicktvui.support.lottie.LottieComposition;
import quicktvui.support.lottie.LottieDrawable;
import quicktvui.support.lottie.animation.content.Content;
import quicktvui.support.lottie.animation.content.PolystarContent;
import quicktvui.support.lottie.model.animatable.AnimatableFloatValue;
import quicktvui.support.lottie.model.animatable.AnimatableValue;
import quicktvui.support.lottie.model.layer.BaseLayer;

public class PolystarShape implements ContentModel {
  public enum Type {
    STAR(1),
    POLYGON(2);

    private final int value;

    Type(int value) {
      this.value = value;
    }

    public static Type forValue(int value) {
      for (Type type : Type.values()) {
        if (type.value == value) {
          return type;
        }
      }
      return null;
    }
  }

  private final String name;
  private final Type type;
  private final AnimatableFloatValue points;
  private final AnimatableValue<PointF, PointF> position;
  private final AnimatableFloatValue rotation;
  private final AnimatableFloatValue innerRadius;
  private final AnimatableFloatValue outerRadius;
  private final AnimatableFloatValue innerRoundedness;
  private final AnimatableFloatValue outerRoundedness;
  private final boolean hidden;
  private final boolean isReversed;

  public PolystarShape(String name, Type type, AnimatableFloatValue points,
      AnimatableValue<PointF, PointF> position,
      AnimatableFloatValue rotation, AnimatableFloatValue innerRadius,
      AnimatableFloatValue outerRadius, AnimatableFloatValue innerRoundedness,
      AnimatableFloatValue outerRoundedness, boolean hidden, boolean isReversed) {
    this.name = name;
    this.type = type;
    this.points = points;
    this.position = position;
    this.rotation = rotation;
    this.innerRadius = innerRadius;
    this.outerRadius = outerRadius;
    this.innerRoundedness = innerRoundedness;
    this.outerRoundedness = outerRoundedness;
    this.hidden = hidden;
    this.isReversed = isReversed;
  }

  public String getName() {
    return name;
  }

  public Type getType() {
    return type;
  }

  public AnimatableFloatValue getPoints() {
    return points;
  }

  public AnimatableValue<PointF, PointF> getPosition() {
    return position;
  }

  public AnimatableFloatValue getRotation() {
    return rotation;
  }

  public AnimatableFloatValue getInnerRadius() {
    return innerRadius;
  }

  public AnimatableFloatValue getOuterRadius() {
    return outerRadius;
  }

  public AnimatableFloatValue getInnerRoundedness() {
    return innerRoundedness;
  }

  public AnimatableFloatValue getOuterRoundedness() {
    return outerRoundedness;
  }

  public boolean isHidden() {
    return hidden;
  }

  public boolean isReversed() {
    return isReversed;
  }

  @Override public Content toContent(LottieDrawable drawable, LottieComposition composition, BaseLayer layer) {
    return new PolystarContent(drawable, layer, this);
  }
}
