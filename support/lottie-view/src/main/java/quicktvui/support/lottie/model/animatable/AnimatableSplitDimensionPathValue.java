package quicktvui.support.lottie.model.animatable;

import android.graphics.PointF;


import java.util.List;

import quicktvui.support.lottie.animation.keyframe.BaseKeyframeAnimation;
import quicktvui.support.lottie.animation.keyframe.SplitDimensionPathKeyframeAnimation;
import quicktvui.support.lottie.value.Keyframe;

public class AnimatableSplitDimensionPathValue implements AnimatableValue<PointF, PointF> {
  private final AnimatableFloatValue animatableXDimension;
  private final AnimatableFloatValue animatableYDimension;

  public AnimatableSplitDimensionPathValue(
      AnimatableFloatValue animatableXDimension,
      AnimatableFloatValue animatableYDimension) {
    this.animatableXDimension = animatableXDimension;
    this.animatableYDimension = animatableYDimension;
  }

  @Override
  public List<Keyframe<PointF>> getKeyframes() {
    throw new UnsupportedOperationException("Cannot call getKeyframes on AnimatableSplitDimensionPathValue.");
  }

  @Override
  public boolean isStatic() {
    return animatableXDimension.isStatic() && animatableYDimension.isStatic();
  }

  @Override public BaseKeyframeAnimation<PointF, PointF> createAnimation() {
    return new SplitDimensionPathKeyframeAnimation(
        animatableXDimension.createAnimation(), animatableYDimension.createAnimation());
  }
}
