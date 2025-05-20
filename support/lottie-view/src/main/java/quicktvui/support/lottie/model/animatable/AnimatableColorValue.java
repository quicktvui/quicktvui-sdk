package quicktvui.support.lottie.model.animatable;


import java.util.List;

import quicktvui.support.lottie.animation.keyframe.BaseKeyframeAnimation;
import quicktvui.support.lottie.animation.keyframe.ColorKeyframeAnimation;
import quicktvui.support.lottie.value.Keyframe;

public class AnimatableColorValue extends BaseAnimatableValue<Integer, Integer> {
  public AnimatableColorValue(List<Keyframe<Integer>> keyframes) {
    super(keyframes);
  }

  @Override public BaseKeyframeAnimation<Integer, Integer> createAnimation() {
    return new ColorKeyframeAnimation(keyframes);
  }
}
