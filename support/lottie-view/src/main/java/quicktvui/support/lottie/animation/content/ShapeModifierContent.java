package quicktvui.support.lottie.animation.content;

import quicktvui.support.lottie.model.content.ShapeData;

public interface ShapeModifierContent extends Content {
  ShapeData modifyShape(ShapeData shapeData);
}
