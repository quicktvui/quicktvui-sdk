package quicktvui.support.lottie.model.content;

import quicktvui.support.lottie.model.animatable.AnimatableFloatValue;

public class BlurEffect {

  final AnimatableFloatValue blurriness;

  public BlurEffect(AnimatableFloatValue blurriness) {
    this.blurriness = blurriness;
  }

  public AnimatableFloatValue getBlurriness() {
    return blurriness;
  }
}
