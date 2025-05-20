package quicktvui.support.lottie.model.animatable;


import java.util.List;

import quicktvui.support.lottie.animation.keyframe.BaseKeyframeAnimation;
import quicktvui.support.lottie.value.Keyframe;

public interface AnimatableValue<K, A> {
  List<Keyframe<K>> getKeyframes();

  boolean isStatic();

  BaseKeyframeAnimation<K, A> createAnimation();
}
