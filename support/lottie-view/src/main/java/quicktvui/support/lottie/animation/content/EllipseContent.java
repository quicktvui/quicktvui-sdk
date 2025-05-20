package quicktvui.support.lottie.animation.content;

import android.graphics.Path;
import android.graphics.PointF;

import android.support.annotation.Nullable;


import java.util.List;

import quicktvui.support.lottie.LottieDrawable;
import quicktvui.support.lottie.LottieProperty;
import quicktvui.support.lottie.animation.keyframe.BaseKeyframeAnimation;
import quicktvui.support.lottie.model.KeyPath;
import quicktvui.support.lottie.model.content.CircleShape;
import quicktvui.support.lottie.model.content.ShapeTrimPath;
import quicktvui.support.lottie.model.layer.BaseLayer;
import quicktvui.support.lottie.utils.MiscUtils;
import quicktvui.support.lottie.value.LottieValueCallback;

public class EllipseContent
    implements PathContent, BaseKeyframeAnimation.AnimationListener, KeyPathElementContent {
  private static final float ELLIPSE_CONTROL_POINT_PERCENTAGE = 0.55228f;

  private final Path path = new Path();

  private final String name;
  private final LottieDrawable lottieDrawable;
  private final BaseKeyframeAnimation<?, PointF> sizeAnimation;
  private final BaseKeyframeAnimation<?, PointF> positionAnimation;
  private final CircleShape circleShape;

  private final CompoundTrimPathContent trimPaths = new CompoundTrimPathContent();
  private boolean isPathValid;

  public EllipseContent(LottieDrawable lottieDrawable, BaseLayer layer, CircleShape circleShape) {
    name = circleShape.getName();
    this.lottieDrawable = lottieDrawable;
    sizeAnimation = circleShape.getSize().createAnimation();
    positionAnimation = circleShape.getPosition().createAnimation();
    this.circleShape = circleShape;

    layer.addAnimation(sizeAnimation);
    layer.addAnimation(positionAnimation);

    sizeAnimation.addUpdateListener(this);
    positionAnimation.addUpdateListener(this);
  }

  @Override public void onValueChanged() {
    invalidate();
  }

  private void invalidate() {
    isPathValid = false;
    lottieDrawable.invalidateSelf();
  }

  @Override public void setContents(List<Content> contentsBefore, List<Content> contentsAfter) {
    for (int i = 0; i < contentsBefore.size(); i++) {
      Content content = contentsBefore.get(i);
      if (content instanceof TrimPathContent && ((TrimPathContent) content).getType() == ShapeTrimPath.Type.SIMULTANEOUSLY) {
        TrimPathContent trimPath = (TrimPathContent) content;
        trimPaths.addTrimPath(trimPath);
        trimPath.addListener(this);
      }
    }
  }

  @Override public String getName() {
    return name;
  }

  @Override public Path getPath() {
    if (isPathValid) {
      return path;
    }

    path.reset();

    if (circleShape.isHidden()) {
      isPathValid = true;
      return path;
    }

    PointF size = sizeAnimation.getValue();
    float halfWidth = size.x / 2f;
    float halfHeight = size.y / 2f;
    // TODO: handle bounds

    float cpW = halfWidth * ELLIPSE_CONTROL_POINT_PERCENTAGE;
    float cpH = halfHeight * ELLIPSE_CONTROL_POINT_PERCENTAGE;

    path.reset();
    if (circleShape.isReversed()) {
      path.moveTo(0, -halfHeight);
      path.cubicTo(0 - cpW, -halfHeight, -halfWidth, 0 - cpH, -halfWidth, 0);
      path.cubicTo(-halfWidth, 0 + cpH, 0 - cpW, halfHeight, 0, halfHeight);
      path.cubicTo(0 + cpW, halfHeight, halfWidth, 0 + cpH, halfWidth, 0);
      path.cubicTo(halfWidth, 0 - cpH, 0 + cpW, -halfHeight, 0, -halfHeight);
    } else {
      path.moveTo(0, -halfHeight);
      path.cubicTo(0 + cpW, -halfHeight, halfWidth, 0 - cpH, halfWidth, 0);
      path.cubicTo(halfWidth, 0 + cpH, 0 + cpW, halfHeight, 0, halfHeight);
      path.cubicTo(0 - cpW, halfHeight, -halfWidth, 0 + cpH, -halfWidth, 0);
      path.cubicTo(-halfWidth, 0 - cpH, 0 - cpW, -halfHeight, 0, -halfHeight);
    }

    PointF position = positionAnimation.getValue();
    path.offset(position.x, position.y);

    path.close();

    trimPaths.apply(path);

    isPathValid = true;
    return path;
  }

  @Override public void resolveKeyPath(
          KeyPath keyPath, int depth, List<KeyPath> accumulator, KeyPath currentPartialKeyPath) {
    MiscUtils.resolveKeyPath(keyPath, depth, accumulator, currentPartialKeyPath, this);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T> void addValueCallback(T property, @Nullable LottieValueCallback<T> callback) {
    if (property == LottieProperty.ELLIPSE_SIZE) {
      sizeAnimation.setValueCallback((LottieValueCallback<PointF>) callback);
    } else if (property == LottieProperty.POSITION) {
      positionAnimation.setValueCallback((LottieValueCallback<PointF>) callback);
    }
  }
}
