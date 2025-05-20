package quicktvui.support.lottie.animation.keyframe;

import java.util.List;

import quicktvui.support.lottie.model.content.GradientColor;
import quicktvui.support.lottie.value.Keyframe;

public class GradientColorKeyframeAnimation extends KeyframeAnimation<GradientColor> {
  private final GradientColor gradientColor;

  public GradientColorKeyframeAnimation(List<Keyframe<GradientColor>> keyframes) {
    super(keyframes);
    GradientColor startValue = keyframes.get(0).startValue;
    int size = startValue == null ? 0 : startValue.getSize();
    gradientColor = new GradientColor(new float[size], new int[size]);
  }

  @Override GradientColor getValue(Keyframe<GradientColor> keyframe, float keyframeProgress) {
    gradientColor.lerp(keyframe.startValue, keyframe.endValue, keyframeProgress);
    return gradientColor;
  }
}
