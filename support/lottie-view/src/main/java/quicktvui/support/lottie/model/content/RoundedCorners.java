package quicktvui.support.lottie.model.content;

import android.support.annotation.Nullable;

import quicktvui.support.lottie.LottieComposition;
import quicktvui.support.lottie.LottieDrawable;
import quicktvui.support.lottie.animation.content.Content;
import quicktvui.support.lottie.animation.content.RoundedCornersContent;
import quicktvui.support.lottie.model.animatable.AnimatableValue;
import quicktvui.support.lottie.model.layer.BaseLayer;

public class RoundedCorners implements ContentModel {
  private final String name;
  private final AnimatableValue<Float, Float> cornerRadius;

  public RoundedCorners(String name, AnimatableValue<Float, Float> cornerRadius) {
    this.name = name;
    this.cornerRadius = cornerRadius;
  }

  public String getName() {
    return name;
  }

  public AnimatableValue<Float, Float> getCornerRadius() {
    return cornerRadius;
  }

  @Nullable @Override public Content toContent(LottieDrawable drawable, LottieComposition composition, BaseLayer layer) {
    return new RoundedCornersContent(drawable, layer, this);
  }
}
