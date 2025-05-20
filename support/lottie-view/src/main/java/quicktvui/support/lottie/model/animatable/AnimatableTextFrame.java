package quicktvui.support.lottie.model.animatable;

import java.util.List;

import quicktvui.support.lottie.animation.keyframe.TextKeyframeAnimation;
import quicktvui.support.lottie.model.DocumentData;
import quicktvui.support.lottie.value.Keyframe;

public class AnimatableTextFrame extends BaseAnimatableValue<DocumentData, DocumentData> {

  public AnimatableTextFrame(List<Keyframe<DocumentData>> keyframes) {
    super(keyframes);
  }

  @Override public TextKeyframeAnimation createAnimation() {
    return new TextKeyframeAnimation(keyframes);
  }
}
