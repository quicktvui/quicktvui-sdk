package quicktvui.support.lottie.parser;

import android.graphics.Path;

import java.io.IOException;
import java.util.Collections;

import quicktvui.support.lottie.LottieComposition;
import quicktvui.support.lottie.model.animatable.AnimatableGradientColorValue;
import quicktvui.support.lottie.model.animatable.AnimatableIntegerValue;
import quicktvui.support.lottie.model.animatable.AnimatablePointValue;
import quicktvui.support.lottie.model.content.GradientFill;
import quicktvui.support.lottie.model.content.GradientType;
import quicktvui.support.lottie.parser.moshi.JsonReader;
import quicktvui.support.lottie.value.Keyframe;

class GradientFillParser {
  private static final JsonReader.Options NAMES = JsonReader.Options.of(
      "nm",
      "g",
      "o",
      "t",
      "s",
      "e",
      "r",
      "hd"
  );
  private static final JsonReader.Options GRADIENT_NAMES = JsonReader.Options.of(
      "p",
      "k"
  );

  private GradientFillParser() {
  }

  static GradientFill parse(
          JsonReader reader, LottieComposition composition) throws IOException {
    String name = null;
    AnimatableGradientColorValue color = null;
    AnimatableIntegerValue opacity = null;
    GradientType gradientType = null;
    AnimatablePointValue startPoint = null;
    AnimatablePointValue endPoint = null;
    Path.FillType fillType = Path.FillType.WINDING;
    boolean hidden = false;

    while (reader.hasNext()) {
      switch (reader.selectName(NAMES)) {
        case 0:
          name = reader.nextString();
          break;
        case 1:
          int points = -1;
          reader.beginObject();
          while (reader.hasNext()) {
            switch (reader.selectName(GRADIENT_NAMES)) {
              case 0:
                points = reader.nextInt();
                break;
              case 1:
                color = AnimatableValueParser.parseGradientColor(reader, composition, points);
                break;
              default:
                reader.skipName();
                reader.skipValue();
            }
          }
          reader.endObject();
          break;
        case 2:
          opacity = AnimatableValueParser.parseInteger(reader, composition);
          break;
        case 3:
          gradientType = reader.nextInt() == 1 ? GradientType.LINEAR : GradientType.RADIAL;
          break;
        case 4:
          startPoint = AnimatableValueParser.parsePoint(reader, composition);
          break;
        case 5:
          endPoint = AnimatableValueParser.parsePoint(reader, composition);
          break;
        case 6:
          fillType = reader.nextInt() == 1 ? Path.FillType.WINDING : Path.FillType.EVEN_ODD;
          break;
        case 7:
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
    return new GradientFill(
        name, gradientType, fillType, color, opacity, startPoint, endPoint, null, null, hidden);
  }
}
