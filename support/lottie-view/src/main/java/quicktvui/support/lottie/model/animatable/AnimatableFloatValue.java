package quicktvui.support.lottie.model.animatable;


import java.util.List;

import quicktvui.support.lottie.animation.keyframe.BaseKeyframeAnimation;
import quicktvui.support.lottie.animation.keyframe.FloatKeyframeAnimation;
import quicktvui.support.lottie.value.Keyframe;

public class AnimatableFloatValue extends BaseAnimatableValue<Float, Float> {

  public AnimatableFloatValue(List<Keyframe<Float>> keyframes) {
    super(keyframes);
  }

  @Override public BaseKeyframeAnimation<Float, Float> createAnimation() {
    return new FloatKeyframeAnimation(keyframes);
  }
}
