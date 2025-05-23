package quicktvui.support.lottie.animation.content;

import android.graphics.Path;

import android.support.annotation.Nullable;


import java.util.ArrayList;
import java.util.List;

import quicktvui.support.lottie.LottieDrawable;
import quicktvui.support.lottie.animation.keyframe.BaseKeyframeAnimation;
import quicktvui.support.lottie.animation.keyframe.ShapeKeyframeAnimation;
import quicktvui.support.lottie.model.content.ShapePath;
import quicktvui.support.lottie.model.content.ShapeTrimPath;
import quicktvui.support.lottie.model.layer.BaseLayer;

public class ShapeContent implements PathContent, BaseKeyframeAnimation.AnimationListener {
  private final Path path = new Path();

  private final String name;
  private final boolean hidden;
  private final LottieDrawable lottieDrawable;
  private final ShapeKeyframeAnimation shapeAnimation;
  @Nullable private List<ShapeModifierContent> shapeModifierContents;

  private boolean isPathValid;
  private final CompoundTrimPathContent trimPaths = new CompoundTrimPathContent();

  public ShapeContent(LottieDrawable lottieDrawable, BaseLayer layer, ShapePath shape) {
    name = shape.getName();
    hidden = shape.isHidden();
    this.lottieDrawable = lottieDrawable;
    shapeAnimation = shape.getShapePath().createAnimation();
    layer.addAnimation(shapeAnimation);
    shapeAnimation.addUpdateListener(this);
  }

  @Override public void onValueChanged() {
    invalidate();
  }

  private void invalidate() {
    isPathValid = false;
    lottieDrawable.invalidateSelf();
  }

  @Override public void setContents(List<Content> contentsBefore, List<Content> contentsAfter) {
    @Nullable List<ShapeModifierContent> shapeModifierContents = null;
    for (int i = 0; i < contentsBefore.size(); i++) {
      Content content = contentsBefore.get(i);
      if (content instanceof TrimPathContent &&
          ((TrimPathContent) content).getType() == ShapeTrimPath.Type.SIMULTANEOUSLY) {
        // Trim path individually will be handled by the stroke where paths are combined.
        TrimPathContent trimPath = (TrimPathContent) content;
        trimPaths.addTrimPath(trimPath);
        trimPath.addListener(this);
      } else if (content instanceof ShapeModifierContent) {
        if (shapeModifierContents == null) {
          shapeModifierContents = new ArrayList<>();
        }
        shapeModifierContents.add((ShapeModifierContent) content);
      }
    }
    shapeAnimation.setShapeModifiers(shapeModifierContents);
  }

  @Override public Path getPath() {
    if (isPathValid) {
      return path;
    }

    path.reset();

    if (hidden) {
      isPathValid = true;
      return path;
    }

    Path shapeAnimationPath = shapeAnimation.getValue();
    if (shapeAnimationPath == null) {
      // It is unclear why this ever returns null but it seems to in rare cases.
      // https://github.com/airbnb/lottie-android/issues/1632
      return path;
    }
    path.set(shapeAnimationPath);
    path.setFillType(Path.FillType.EVEN_ODD);

    trimPaths.apply(path);

    isPathValid = true;
    return path;
  }

  @Override public String getName() {
    return name;
  }
}
