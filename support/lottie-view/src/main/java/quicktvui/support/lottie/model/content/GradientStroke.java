package quicktvui.support.lottie.model.content;

import android.support.annotation.Nullable;

import java.util.List;

import quicktvui.support.lottie.LottieComposition;
import quicktvui.support.lottie.LottieDrawable;
import quicktvui.support.lottie.animation.content.Content;
import quicktvui.support.lottie.animation.content.GradientStrokeContent;
import quicktvui.support.lottie.model.animatable.AnimatableFloatValue;
import quicktvui.support.lottie.model.animatable.AnimatableGradientColorValue;
import quicktvui.support.lottie.model.animatable.AnimatableIntegerValue;
import quicktvui.support.lottie.model.animatable.AnimatablePointValue;
import quicktvui.support.lottie.model.layer.BaseLayer;

public class GradientStroke implements ContentModel {

  private final String name;
  private final GradientType gradientType;
  private final AnimatableGradientColorValue gradientColor;
  private final AnimatableIntegerValue opacity;
  private final AnimatablePointValue startPoint;
  private final AnimatablePointValue endPoint;
  private final AnimatableFloatValue width;
  private final ShapeStroke.LineCapType capType;
  private final ShapeStroke.LineJoinType joinType;
  private final float miterLimit;
  private final List<AnimatableFloatValue> lineDashPattern;
  @Nullable private final AnimatableFloatValue dashOffset;
  private final boolean hidden;

  public GradientStroke(String name, GradientType gradientType,
      AnimatableGradientColorValue gradientColor,
      AnimatableIntegerValue opacity, AnimatablePointValue startPoint,
      AnimatablePointValue endPoint, AnimatableFloatValue width, ShapeStroke.LineCapType capType,
      ShapeStroke.LineJoinType joinType, float miterLimit,
      List<AnimatableFloatValue> lineDashPattern,
      @Nullable AnimatableFloatValue dashOffset, boolean hidden) {
    this.name = name;
    this.gradientType = gradientType;
    this.gradientColor = gradientColor;
    this.opacity = opacity;
    this.startPoint = startPoint;
    this.endPoint = endPoint;
    this.width = width;
    this.capType = capType;
    this.joinType = joinType;
    this.miterLimit = miterLimit;
    this.lineDashPattern = lineDashPattern;
    this.dashOffset = dashOffset;
    this.hidden = hidden;
  }

  public String getName() {
    return name;
  }

  public GradientType getGradientType() {
    return gradientType;
  }

  public AnimatableGradientColorValue getGradientColor() {
    return gradientColor;
  }

  public AnimatableIntegerValue getOpacity() {
    return opacity;
  }

  public AnimatablePointValue getStartPoint() {
    return startPoint;
  }

  public AnimatablePointValue getEndPoint() {
    return endPoint;
  }

  public AnimatableFloatValue getWidth() {
    return width;
  }

  public ShapeStroke.LineCapType getCapType() {
    return capType;
  }

  public ShapeStroke.LineJoinType getJoinType() {
    return joinType;
  }

  public List<AnimatableFloatValue> getLineDashPattern() {
    return lineDashPattern;
  }

  @Nullable public AnimatableFloatValue getDashOffset() {
    return dashOffset;
  }

  public float getMiterLimit() {
    return miterLimit;
  }

  public boolean isHidden() {
    return hidden;
  }

  @Override public Content toContent(LottieDrawable drawable, LottieComposition composition, BaseLayer layer) {
    return new GradientStrokeContent(drawable, layer, this);
  }
}
