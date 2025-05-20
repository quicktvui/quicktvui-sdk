package quicktvui.support.lottie.parser;

import android.graphics.PointF;

import java.io.IOException;

import quicktvui.support.lottie.LottieComposition;
import quicktvui.support.lottie.model.animatable.AnimatableFloatValue;
import quicktvui.support.lottie.model.animatable.AnimatableValue;
import quicktvui.support.lottie.model.content.RectangleShape;
import quicktvui.support.lottie.parser.moshi.JsonReader;

class RectangleShapeParser {

  private static final JsonReader.Options NAMES = JsonReader.Options.of(
      "nm",
      "p",
      "s",
      "r",
      "hd"
  );

  private RectangleShapeParser() {
  }

  static RectangleShape parse(
      JsonReader reader, LottieComposition composition) throws IOException {
    String name = null;
    AnimatableValue<PointF, PointF> position = null;
    AnimatableValue<PointF, PointF> size = null;
    AnimatableFloatValue roundedness = null;
    boolean hidden = false;

    while (reader.hasNext()) {
      switch (reader.selectName(NAMES)) {
        case 0:
          name = reader.nextString();
          break;
        case 1:
          position =
              AnimatablePathValueParser.parseSplitPath(reader, composition);
          break;
        case 2:
          size = AnimatableValueParser.parsePoint(reader, composition);
          break;
        case 3:
          roundedness = AnimatableValueParser.parseFloat(reader, composition);
          break;
        case 4:
          hidden = reader.nextBoolean();
          break;
        default:
          reader.skipValue();
      }
    }

    return new RectangleShape(name, position, size, roundedness, hidden);
  }
}
