package quicktvui.support.lottie.model.animatable;

import java.util.Arrays;
import java.util.List;

import quicktvui.support.lottie.animation.keyframe.BaseKeyframeAnimation;
import quicktvui.support.lottie.animation.keyframe.GradientColorKeyframeAnimation;
import quicktvui.support.lottie.model.content.GradientColor;
import quicktvui.support.lottie.value.Keyframe;

public class AnimatableGradientColorValue extends BaseAnimatableValue<GradientColor,
    GradientColor> {
  public AnimatableGradientColorValue(List<Keyframe<GradientColor>> keyframes) {
    super(ensureInterpolatableKeyframes(keyframes));
  }

  private static List<Keyframe<GradientColor>> ensureInterpolatableKeyframes(List<Keyframe<GradientColor>> keyframes) {
    for (int i = 0; i < keyframes.size(); i++) {
      keyframes.set(i, ensureInterpolatableKeyframe(keyframes.get(i)));
    }
    return keyframes;
  }

  private static Keyframe<GradientColor> ensureInterpolatableKeyframe(Keyframe<GradientColor> keyframe) {
    GradientColor startValue = keyframe.startValue;
    GradientColor endValue = keyframe.endValue;
    if (startValue == null || endValue == null || startValue.getPositions().length == endValue.getPositions().length) {
      return keyframe;
    }
    float[] mergedPositions = mergePositions(startValue.getPositions(), endValue.getPositions());
    // The start/end has opacity stops which required adding extra positions in between the existing colors.
    return keyframe.copyWith(startValue.copyWithPositions(mergedPositions), endValue.copyWithPositions(mergedPositions));
  }

  static float[] mergePositions(float[] startPositions, float[] endPositions) {
    float[] mergedArray = new float[startPositions.length + endPositions.length];
    System.arraycopy(startPositions, 0, mergedArray, 0, startPositions.length);
    System.arraycopy(endPositions, 0, mergedArray, startPositions.length, endPositions.length);
    Arrays.sort(mergedArray);
    int uniqueValues = 0;
    float lastValue = Float.NaN;
    for (int i = 0; i < mergedArray.length; i++) {
      if (mergedArray[i] != lastValue) {
        mergedArray[uniqueValues] = mergedArray[i];
        uniqueValues++;
        lastValue = mergedArray[i];
      }
    }
    return Arrays.copyOfRange(mergedArray, 0, uniqueValues);
  }

  @Override public BaseKeyframeAnimation<GradientColor, GradientColor> createAnimation() {
    return new GradientColorKeyframeAnimation(keyframes);
  }
}
