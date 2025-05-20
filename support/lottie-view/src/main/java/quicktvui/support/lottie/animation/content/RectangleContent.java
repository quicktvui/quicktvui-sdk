package quicktvui.support.lottie.animation.content;

import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;

import android.support.annotation.Nullable;

import java.util.List;

import quicktvui.support.lottie.LottieDrawable;
import quicktvui.support.lottie.LottieProperty;
import quicktvui.support.lottie.animation.keyframe.BaseKeyframeAnimation;
import quicktvui.support.lottie.animation.keyframe.FloatKeyframeAnimation;
import quicktvui.support.lottie.model.KeyPath;
import quicktvui.support.lottie.model.content.RectangleShape;
import quicktvui.support.lottie.model.content.ShapeTrimPath;
import quicktvui.support.lottie.model.layer.BaseLayer;
import quicktvui.support.lottie.utils.MiscUtils;
import quicktvui.support.lottie.value.LottieValueCallback;

public class RectangleContent
    implements BaseKeyframeAnimation.AnimationListener, KeyPathElementContent, PathContent {
  private final Path path = new Path();
  private final RectF rect = new RectF();

  private final String name;
  private final boolean hidden;
  private final LottieDrawable lottieDrawable;
  private final BaseKeyframeAnimation<?, PointF> positionAnimation;
  private final BaseKeyframeAnimation<?, PointF> sizeAnimation;
  private final BaseKeyframeAnimation<?, Float> cornerRadiusAnimation;

  private final CompoundTrimPathContent trimPaths = new CompoundTrimPathContent();
  /** This corner radius is from a layer item. The first one is from the roundedness on this specific rect. */
  @Nullable private BaseKeyframeAnimation<Float, Float> roundedCornersAnimation = null;
  private boolean isPathValid;

  public RectangleContent(LottieDrawable lottieDrawable, BaseLayer layer, RectangleShape rectShape) {
    name = rectShape.getName();
    hidden = rectShape.isHidden();
    this.lottieDrawable = lottieDrawable;
    positionAnimation = rectShape.getPosition().createAnimation();
    sizeAnimation = rectShape.getSize().createAnimation();
    cornerRadiusAnimation = rectShape.getCornerRadius().createAnimation();

    layer.addAnimation(positionAnimation);
    layer.addAnimation(sizeAnimation);
    layer.addAnimation(cornerRadiusAnimation);

    positionAnimation.addUpdateListener(this);
    sizeAnimation.addUpdateListener(this);
    cornerRadiusAnimation.addUpdateListener(this);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void onValueChanged() {
    invalidate();
  }

  private void invalidate() {
    isPathValid = false;
    lottieDrawable.invalidateSelf();
  }

  @Override
  public void setContents(List<Content> contentsBefore, List<Content> contentsAfter) {
    for (int i = 0; i < contentsBefore.size(); i++) {
      Content content = contentsBefore.get(i);
      if (content instanceof TrimPathContent &&
          ((TrimPathContent) content).getType() == ShapeTrimPath.Type.SIMULTANEOUSLY) {
        TrimPathContent trimPath = (TrimPathContent) content;
        trimPaths.addTrimPath(trimPath);
        trimPath.addListener(this);
      } else if (content instanceof RoundedCornersContent) {
        roundedCornersAnimation = ((RoundedCornersContent) content).getRoundedCorners();
      }
    }
  }

  @Override
  public Path getPath() {
    if (isPathValid) {
      return path;
    }

    path.reset();

    if (hidden) {
      isPathValid = true;
      return path;
    }

    PointF size = sizeAnimation.getValue();
    float halfWidth = size.x / 2f;
    float halfHeight = size.y / 2f;
    float radius = cornerRadiusAnimation == null ?
        0f : ((FloatKeyframeAnimation) cornerRadiusAnimation).getFloatValue();
    if (radius == 0f && this.roundedCornersAnimation != null) {
      radius = Math.min(roundedCornersAnimation.getValue(), Math.min(halfWidth, halfHeight));
    }
    float maxRadius = Math.min(halfWidth, halfHeight);
    if (radius > maxRadius) {
      radius = maxRadius;
    }

    // Draw the rectangle top right to bottom left.
    PointF position = positionAnimation.getValue();

    path.moveTo(position.x + halfWidth, position.y - halfHeight + radius);

    path.lineTo(position.x + halfWidth, position.y + halfHeight - radius);

    if (radius > 0) {
      rect.set(position.x + halfWidth - 2 * radius,
          position.y + halfHeight - 2 * radius,
          position.x + halfWidth,
          position.y + halfHeight);
      path.arcTo(rect, 0, 90, false);
    }

    path.lineTo(position.x - halfWidth + radius, position.y + halfHeight);

    if (radius > 0) {
      rect.set(position.x - halfWidth,
          position.y + halfHeight - 2 * radius,
          position.x - halfWidth + 2 * radius,
          position.y + halfHeight);
      path.arcTo(rect, 90, 90, false);
    }

    path.lineTo(position.x - halfWidth, position.y - halfHeight + radius);

    if (radius > 0) {
      rect.set(position.x - halfWidth,
          position.y - halfHeight,
          position.x - halfWidth + 2 * radius,
          position.y - halfHeight + 2 * radius);
      path.arcTo(rect, 180, 90, false);
    }

    path.lineTo(position.x + halfWidth - radius, position.y - halfHeight);

    if (radius > 0) {
      rect.set(position.x + halfWidth - 2 * radius,
          position.y - halfHeight,
          position.x + halfWidth,
          position.y - halfHeight + 2 * radius);
      path.arcTo(rect, 270, 90, false);
    }
    path.close();

    trimPaths.apply(path);

    isPathValid = true;
    return path;
  }

  @Override
  public void resolveKeyPath(KeyPath keyPath, int depth, List<KeyPath> accumulator,
                             KeyPath currentPartialKeyPath) {
    MiscUtils.resolveKeyPath(keyPath, depth, accumulator, currentPartialKeyPath, this);
  }

  @Override
  public <T> void addValueCallback(T property, @Nullable LottieValueCallback<T> callback) {
    if (property == LottieProperty.RECTANGLE_SIZE) {
      sizeAnimation.setValueCallback((LottieValueCallback<PointF>) callback);
    } else if (property == LottieProperty.POSITION) {
      positionAnimation.setValueCallback((LottieValueCallback<PointF>) callback);
    } else if (property == LottieProperty.CORNER_RADIUS) {
      cornerRadiusAnimation.setValueCallback((LottieValueCallback<Float>) callback);
    }
  }
}
