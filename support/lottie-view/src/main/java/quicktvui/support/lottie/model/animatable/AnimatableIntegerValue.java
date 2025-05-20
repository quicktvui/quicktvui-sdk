package quicktvui.support.lottie.model.animatable;

import java.util.List;

import quicktvui.support.lottie.animation.keyframe.BaseKeyframeAnimation;
import quicktvui.support.lottie.animation.keyframe.IntegerKeyframeAnimation;
import quicktvui.support.lottie.value.Keyframe;

public class AnimatableIntegerValue extends BaseAnimatableValue<Integer, Integer> {

  public AnimatableIntegerValue(List<Keyframe<Integer>> keyframes) {
    super(keyframes);
  }

  @Override public BaseKeyframeAnimation<Integer, Integer> createAnimation() {
    return new IntegerKeyframeAnimation(keyframes);
  }
}
