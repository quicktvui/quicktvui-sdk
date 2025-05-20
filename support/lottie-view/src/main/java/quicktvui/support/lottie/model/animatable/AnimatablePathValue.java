package quicktvui.support.lottie.model.animatable;

import android.graphics.PointF;

import java.util.List;

import quicktvui.support.lottie.animation.keyframe.BaseKeyframeAnimation;
import quicktvui.support.lottie.animation.keyframe.PathKeyframeAnimation;
import quicktvui.support.lottie.animation.keyframe.PointKeyframeAnimation;
import quicktvui.support.lottie.value.Keyframe;

public class AnimatablePathValue implements AnimatableValue<PointF, PointF> {
  private final List<Keyframe<PointF>> keyframes;

  public AnimatablePathValue(List<Keyframe<PointF>> keyframes) {
    this.keyframes = keyframes;
  }

  @Override
  public List<Keyframe<PointF>> getKeyframes() {
    return keyframes;
  }

  @Override
  public boolean isStatic() {
    return keyframes.size() == 1 && keyframes.get(0).isStatic();
  }

  @Override
  public BaseKeyframeAnimation<PointF, PointF> createAnimation() {
    if (keyframes.get(0).isStatic()) {
      return new PointKeyframeAnimation(keyframes);
    }
    return new PathKeyframeAnimation(keyframes);
  }
}
