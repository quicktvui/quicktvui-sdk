package quicktvui.support.lottie.parser;

import android.graphics.Path;

import java.io.IOException;
import java.util.Collections;

import quicktvui.support.lottie.LottieComposition;
import quicktvui.support.lottie.model.animatable.AnimatableColorValue;
import quicktvui.support.lottie.model.animatable.AnimatableIntegerValue;
import quicktvui.support.lottie.model.content.ShapeFill;
import quicktvui.support.lottie.parser.moshi.JsonReader;
import quicktvui.support.lottie.value.Keyframe;

class ShapeFillParser {
  private static final JsonReader.Options NAMES = JsonReader.Options.of(
      "nm",
      "c",
      "o",
      "fillEnabled",
      "r",
      "hd"
  );

  private ShapeFillParser() {
  }

  static ShapeFill parse(
          JsonReader reader, LottieComposition composition) throws IOException {
    AnimatableColorValue color = null;
    boolean fillEnabled = false;
    AnimatableIntegerValue opacity = null;
    String name = null;
    int fillTypeInt = 1;
    boolean hidden = false;

    while (reader.hasNext()) {
      switch (reader.selectName(NAMES)) {
        case 0:
          name = reader.nextString();
          break;
        case 1:
          color = AnimatableValueParser.parseColor(reader, composition);
          break;
        case 2:
          opacity = AnimatableValueParser.parseInteger(reader, composition);
          break;
        case 3:
          fillEnabled = reader.nextBoolean();
          break;
        case 4:
          fillTypeInt = reader.nextInt();
          break;
        case 5:
          hidden = reader.nextBoolean();
          break;
        default:
          reader.skipName();
          reader.skipValue();
      }
    }

    // Telegram sometimes omits opacity.
    // https://github.com/airbnb/lottie-android/issues/1600
    opacity = opacity == null ? new AnimatableIntegerValue(Collections.singletonList(new Keyframe<>(100))) : opacity;
    Path.FillType fillType = fillTypeInt == 1 ? Path.FillType.WINDING : Path.FillType.EVEN_ODD;
    return new ShapeFill(name, fillEnabled, fillType, color, opacity, hidden);
  }
}
