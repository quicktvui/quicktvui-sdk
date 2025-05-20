package quicktvui.support.lottie.model.animatable;

import android.graphics.Path;


import java.util.List;

import quicktvui.support.lottie.animation.keyframe.ShapeKeyframeAnimation;
import quicktvui.support.lottie.model.content.ShapeData;
import quicktvui.support.lottie.value.Keyframe;

public class AnimatableShapeValue extends BaseAnimatableValue<ShapeData, Path> {

  public AnimatableShapeValue(List<Keyframe<ShapeData>> keyframes) {
    super(keyframes);
  }

  @Override public ShapeKeyframeAnimation createAnimation() {
    return new ShapeKeyframeAnimation(keyframes);
  }
}
