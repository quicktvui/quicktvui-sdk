package quicktvui.support.lottie.animation.keyframe;

import java.util.List;

import quicktvui.support.lottie.utils.GammaEvaluator;
import quicktvui.support.lottie.utils.MiscUtils;
import quicktvui.support.lottie.value.Keyframe;

public class ColorKeyframeAnimation extends KeyframeAnimation<Integer> {

  public ColorKeyframeAnimation(List<Keyframe<Integer>> keyframes) {
    super(keyframes);
  }

  @Override
  Integer getValue(Keyframe<Integer> keyframe, float keyframeProgress) {
    return getIntValue(keyframe, keyframeProgress);
  }

  /**
   * Optimization to avoid autoboxing.
   */
  public int getIntValue(Keyframe<Integer> keyframe, float keyframeProgress) {
    if (keyframe.startValue == null || keyframe.endValue == null) {
      throw new IllegalStateException("Missing values for keyframe.");
    }

    if (valueCallback != null) {
      //noinspection ConstantConditions
      Integer value = valueCallback.getValueInternal(keyframe.startFrame, keyframe.endFrame, keyframe.startValue,
          keyframe.endValue, keyframeProgress, getLinearCurrentKeyframeProgress(), getProgress());
      if (value != null) {
        return value;
      }
    }

    return GammaEvaluator.evaluate(MiscUtils.clamp(keyframeProgress, 0f, 1f), keyframe.startValue, keyframe.endValue);
  }

  /**
   * Optimization to avoid autoboxing.
   */
  public int getIntValue() {
    return getIntValue(getCurrentKeyframe(), getInterpolatedCurrentKeyframeProgress());
  }
}
