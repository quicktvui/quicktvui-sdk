package quicktvui.support.lottie.animation.keyframe;

import android.support.annotation.Nullable;

import java.util.Collections;

import quicktvui.support.lottie.value.Keyframe;
import quicktvui.support.lottie.value.LottieValueCallback;

public class ValueCallbackKeyframeAnimation<K, A> extends BaseKeyframeAnimation<K, A> {
  private final A valueCallbackValue;

  public ValueCallbackKeyframeAnimation(LottieValueCallback<A> valueCallback) {
    this(valueCallback, null);
  }

  public ValueCallbackKeyframeAnimation(LottieValueCallback<A> valueCallback, @Nullable A valueCallbackValue) {
    super(Collections.emptyList());
    setValueCallback(valueCallback);
    this.valueCallbackValue = valueCallbackValue;
  }

  @Override public void setProgress(float progress) {
    this.progress = progress;
  }

  /**
   * If this doesn't return 1, then {@link #setProgress(float)} will always clamp the progress
   * to 0.
   */
  @Override float getEndProgress() {
    return 1f;
  }

  @Override public void notifyListeners() {
    if (this.valueCallback != null) {
      super.notifyListeners();
    }
  }

  @Override public A getValue() {
    //noinspection ConstantConditions
    return valueCallback.getValueInternal(0f, 0f, valueCallbackValue, valueCallbackValue, getProgress(), getProgress(), getProgress());
  }

  @Override A getValue(Keyframe<K> keyframe, float keyframeProgress) {
    return getValue();
  }
}
