package quicktvui.support.lottie.model.layer;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v4.util.LongSparseArray;

import java.util.ArrayList;
import java.util.List;

import quicktvui.support.lottie.L;
import quicktvui.support.lottie.LottieComposition;
import quicktvui.support.lottie.LottieDrawable;
import quicktvui.support.lottie.LottieProperty;
import quicktvui.support.lottie.animation.keyframe.BaseKeyframeAnimation;
import quicktvui.support.lottie.animation.keyframe.ValueCallbackKeyframeAnimation;
import quicktvui.support.lottie.model.KeyPath;
import quicktvui.support.lottie.model.animatable.AnimatableFloatValue;
import quicktvui.support.lottie.utils.Utils;
import quicktvui.support.lottie.value.LottieValueCallback;

public class CompositionLayer extends BaseLayer {
  @Nullable private BaseKeyframeAnimation<Float, Float> timeRemapping;
  private final List<BaseLayer> layers = new ArrayList<>();
  private final RectF rect = new RectF();
  private final RectF newClipRect = new RectF();
  private final Paint layerPaint = new Paint();

  @Nullable private Boolean hasMatte;
  @Nullable private Boolean hasMasks;
  private float progress;

  private boolean clipToCompositionBounds = true;

  public CompositionLayer(LottieDrawable lottieDrawable, Layer layerModel, List<Layer> layerModels,
                          LottieComposition composition) {
    super(lottieDrawable, layerModel);

    AnimatableFloatValue timeRemapping = layerModel.getTimeRemapping();
    if (timeRemapping != null) {
      this.timeRemapping = timeRemapping.createAnimation();
      addAnimation(this.timeRemapping);
      //noinspection ConstantConditions
      this.timeRemapping.addUpdateListener(this);
    } else {
      this.timeRemapping = null;
    }

    LongSparseArray<BaseLayer> layerMap =
        new LongSparseArray<>(composition.getLayers().size());

    BaseLayer mattedLayer = null;
    for (int i = layerModels.size() - 1; i >= 0; i--) {
      Layer lm = layerModels.get(i);
      BaseLayer layer = forModel(this, lm, lottieDrawable, composition);
      if (layer == null) {
        continue;
      }
      layerMap.put(layer.getLayerModel().getId(), layer);
      if (mattedLayer != null) {
        mattedLayer.setMatteLayer(layer);
        mattedLayer = null;
      } else {
        layers.add(0, layer);
        switch (lm.getMatteType()) {
          case ADD:
          case INVERT:
            mattedLayer = layer;
            break;
        }
      }
    }

    for (int i = 0; i < layerMap.size(); i++) {
      long key = layerMap.keyAt(i);
      BaseLayer layerView = layerMap.get(key);
      // This shouldn't happen but it appears as if sometimes on pre-lollipop devices when
      // compiled with d8, layerView is null sometimes.
      // https://github.com/airbnb/lottie-android/issues/524
      if (layerView == null) {
        continue;
      }
      BaseLayer parentLayer = layerMap.get(layerView.getLayerModel().getParentId());
      if (parentLayer != null) {
        layerView.setParentLayer(parentLayer);
      }
    }
  }

  public void setClipToCompositionBounds(boolean clipToCompositionBounds) {
    this.clipToCompositionBounds = clipToCompositionBounds;
  }

  @Override public void setOutlineMasksAndMattes(boolean outline) {
    super.setOutlineMasksAndMattes(outline);
    for (BaseLayer layer : layers) {
      layer.setOutlineMasksAndMattes(outline);
    }
  }

  @Override void drawLayer(Canvas canvas, Matrix parentMatrix, int parentAlpha) {
    L.beginSection("CompositionLayer#draw");
    newClipRect.set(0, 0, layerModel.getPreCompWidth(), layerModel.getPreCompHeight());
    parentMatrix.mapRect(newClipRect);

    // Apply off-screen rendering only when needed in order to improve rendering performance.
    boolean isDrawingWithOffScreen = lottieDrawable.isApplyingOpacityToLayersEnabled() && layers.size() > 1 && parentAlpha != 255;
    if (isDrawingWithOffScreen) {
      layerPaint.setAlpha(parentAlpha);
      Utils.saveLayerCompat(canvas, newClipRect, layerPaint);
    } else {
      canvas.save();
    }

    int childAlpha = isDrawingWithOffScreen ? 255 : parentAlpha;
    for (int i = layers.size() - 1; i >= 0; i--) {
      boolean nonEmptyClip = true;
      // Only clip precomps. This mimics the way After Effects renders animations.
      boolean ignoreClipOnThisLayer = !clipToCompositionBounds && "__container".equals(layerModel.getName());
      if (!ignoreClipOnThisLayer && !newClipRect.isEmpty()) {
        nonEmptyClip = canvas.clipRect(newClipRect);
      }
      if (nonEmptyClip) {
        BaseLayer layer = layers.get(i);
        layer.draw(canvas, parentMatrix, childAlpha);
      }
    }
    canvas.restore();
    L.endSection("CompositionLayer#draw");
  }

  @Override public void getBounds(RectF outBounds, Matrix parentMatrix, boolean applyParents) {
    super.getBounds(outBounds, parentMatrix, applyParents);
    for (int i = layers.size() - 1; i >= 0; i--) {
      rect.set(0, 0, 0, 0);
      layers.get(i).getBounds(rect, boundsMatrix, true);
      outBounds.union(rect);
    }
  }

  @Override public void setProgress(@FloatRange(from = 0f, to = 1f) float progress) {
    L.beginSection("CompositionLayer#setProgress");
    this.progress = progress;
    super.setProgress(progress);
    if (timeRemapping != null) {
      // The duration has 0.01 frame offset to show end of animation properly.
      // https://github.com/airbnb/lottie-android/pull/766
      // Ignore this offset for calculating time-remapping because time-remapping value is based on original duration.
      float durationFrames = lottieDrawable.getComposition().getDurationFrames() + 0.01f;
      float compositionDelayFrames = layerModel.getComposition().getStartFrame();
      float remappedFrames = timeRemapping.getValue() * layerModel.getComposition().getFrameRate() - compositionDelayFrames;
      progress = remappedFrames / durationFrames;
    }
    if (timeRemapping == null) {
      progress -= layerModel.getStartProgress();
    }
    //Time stretch needs to be divided if is not "__container"
    if (layerModel.getTimeStretch() != 0 && !"__container".equals(layerModel.getName())) {
      progress /= layerModel.getTimeStretch();
    }
    for (int i = layers.size() - 1; i >= 0; i--) {
      layers.get(i).setProgress(progress);
    }
    L.endSection("CompositionLayer#setProgress");
  }

  public float getProgress() {
    return progress;
  }

  public boolean hasMasks() {
    if (hasMasks == null) {
      for (int i = layers.size() - 1; i >= 0; i--) {
        BaseLayer layer = layers.get(i);
        if (layer instanceof ShapeLayer) {
          if (layer.hasMasksOnThisLayer()) {
            hasMasks = true;
            return true;
          }
        } else if (layer instanceof CompositionLayer && ((CompositionLayer) layer).hasMasks()) {
          hasMasks = true;
          return true;
        }
      }
      hasMasks = false;
    }
    return hasMasks;
  }

  public boolean hasMatte() {
    if (hasMatte == null) {
      if (hasMatteOnThisLayer()) {
        hasMatte = true;
        return true;
      }

      for (int i = layers.size() - 1; i >= 0; i--) {
        if (layers.get(i).hasMatteOnThisLayer()) {
          hasMatte = true;
          return true;
        }
      }
      hasMatte = false;
    }
    return hasMatte;
  }

  @Override
  protected void resolveChildKeyPath(KeyPath keyPath, int depth, List<KeyPath> accumulator,
                                     KeyPath currentPartialKeyPath) {
    for (int i = 0; i < layers.size(); i++) {
      layers.get(i).resolveKeyPath(keyPath, depth, accumulator, currentPartialKeyPath);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> void addValueCallback(T property, @Nullable LottieValueCallback<T> callback) {
    super.addValueCallback(property, callback);

    if (property == LottieProperty.TIME_REMAP) {
      if (callback == null) {
        if (timeRemapping != null) {
          timeRemapping.setValueCallback(null);
        }
      } else {
        timeRemapping = new ValueCallbackKeyframeAnimation<>((LottieValueCallback<Float>) callback);
        timeRemapping.addUpdateListener(this);
        addAnimation(timeRemapping);
      }
    }
  }
}
