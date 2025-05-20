package quicktvui.support.lottie.model.content;


import android.support.annotation.Nullable;

import quicktvui.support.lottie.LottieComposition;
import quicktvui.support.lottie.LottieDrawable;
import quicktvui.support.lottie.animation.content.Content;
import quicktvui.support.lottie.model.layer.BaseLayer;

public interface ContentModel {
  @Nullable
  Content toContent(LottieDrawable drawable, LottieComposition composition, BaseLayer layer);
}
