package quicktvui.support.lottie.animation.keyframe;

import java.util.List;

import quicktvui.support.lottie.utils.MiscUtils;
import quicktvui.support.lottie.value.Keyframe;
import quicktvui.support.lottie.value.ScaleXY;

public class ScaleKeyframeAnimation extends KeyframeAnimation<ScaleXY> {

  private final ScaleXY scaleXY = new ScaleXY();

  public ScaleKeyframeAnimation(List<Keyframe<ScaleXY>> keyframes) {
    super(keyframes);
  }

  @Override public ScaleXY getValue(Keyframe<ScaleXY> keyframe, float keyframeProgress) {
    if (keyframe.startValue == null || keyframe.endValue == null) {
      throw new IllegalStateException("Missing values for keyframe.");
    }
    ScaleXY startTransform = keyframe.startValue;
    ScaleXY endTransform = keyframe.endValue;

    if (valueCallback != null) {
      //noinspection ConstantConditions
      ScaleXY value = valueCallback.getValueInternal(keyframe.startFrame, keyframe.endFrame,
          startTransform, endTransform,
          keyframeProgress, getLinearCurrentKeyframeProgress(), getProgress());
      if (value != null) {
        return value;
      }
    }

    scaleXY.set(
        MiscUtils.lerp(startTransform.getScaleX(), endTransform.getScaleX(), keyframeProgress),
        MiscUtils.lerp(startTransform.getScaleY(), endTransform.getScaleY(), keyframeProgress)
    );
    return scaleXY;
  }
}
