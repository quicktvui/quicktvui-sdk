package quicktvui.support.lottie.animation.keyframe;

import android.graphics.Path;

import android.support.annotation.Nullable;

import java.util.List;

import quicktvui.support.lottie.animation.content.ShapeModifierContent;
import quicktvui.support.lottie.model.content.ShapeData;
import quicktvui.support.lottie.utils.MiscUtils;
import quicktvui.support.lottie.value.Keyframe;

public class ShapeKeyframeAnimation extends BaseKeyframeAnimation<ShapeData, Path> {
  private final ShapeData tempShapeData = new ShapeData();
  private final Path tempPath = new Path();

  private List<ShapeModifierContent> shapeModifiers;

  public ShapeKeyframeAnimation(List<Keyframe<ShapeData>> keyframes) {
    super(keyframes);
  }

  @Override public Path getValue(Keyframe<ShapeData> keyframe, float keyframeProgress) {
    ShapeData startShapeData = keyframe.startValue;
    ShapeData endShapeData = keyframe.endValue;

    tempShapeData.interpolateBetween(startShapeData, endShapeData, keyframeProgress);
    ShapeData modifiedShapeData = tempShapeData;
    if (shapeModifiers != null) {
      for (int i = shapeModifiers.size() - 1; i >= 0; i--) {
        modifiedShapeData = shapeModifiers.get(i).modifyShape(modifiedShapeData);
      }
    }
    MiscUtils.getPathFromData(modifiedShapeData, tempPath);
    return tempPath;
  }

  public void setShapeModifiers(@Nullable List<ShapeModifierContent> shapeModifiers) {
    this.shapeModifiers = shapeModifiers;
  }
}
