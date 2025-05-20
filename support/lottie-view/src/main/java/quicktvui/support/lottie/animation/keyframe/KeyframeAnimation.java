package quicktvui.support.lottie.animation.keyframe;

import java.util.List;

import quicktvui.support.lottie.value.Keyframe;

abstract class KeyframeAnimation<T> extends BaseKeyframeAnimation<T, T> {
  KeyframeAnimation(List<? extends Keyframe<T>> keyframes) {
    super(keyframes);
  }
}
