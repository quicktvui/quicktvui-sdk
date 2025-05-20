package quicktvui.support.lottie.model.animatable;


import java.util.List;

import quicktvui.support.lottie.animation.keyframe.BaseKeyframeAnimation;
import quicktvui.support.lottie.animation.keyframe.ScaleKeyframeAnimation;
import quicktvui.support.lottie.value.Keyframe;
import quicktvui.support.lottie.value.ScaleXY;

public class AnimatableScaleValue extends BaseAnimatableValue<ScaleXY, ScaleXY> {

  public AnimatableScaleValue(ScaleXY value) {
    super(value);
  }

  public AnimatableScaleValue(List<Keyframe<ScaleXY>> keyframes) {
    super(keyframes);
  }

  @Override public BaseKeyframeAnimation<ScaleXY, ScaleXY> createAnimation() {
    return new ScaleKeyframeAnimation(keyframes);
  }
}
