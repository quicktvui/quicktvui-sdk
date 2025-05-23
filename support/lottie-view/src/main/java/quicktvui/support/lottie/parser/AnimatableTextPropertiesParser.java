package quicktvui.support.lottie.parser;

import java.io.IOException;

import quicktvui.support.lottie.LottieComposition;
import quicktvui.support.lottie.model.animatable.AnimatableColorValue;
import quicktvui.support.lottie.model.animatable.AnimatableFloatValue;
import quicktvui.support.lottie.model.animatable.AnimatableTextProperties;
import quicktvui.support.lottie.parser.moshi.JsonReader;

public class AnimatableTextPropertiesParser {

  private static final JsonReader.Options PROPERTIES_NAMES = JsonReader.Options.of("a");
  private static final JsonReader.Options ANIMATABLE_PROPERTIES_NAMES = JsonReader.Options.of(
      "fc",
      "sc",
      "sw",
      "t"
  );

  private AnimatableTextPropertiesParser() {
  }

  public static AnimatableTextProperties parse(
      JsonReader reader, LottieComposition composition) throws IOException {
    AnimatableTextProperties anim = null;

    reader.beginObject();
    while (reader.hasNext()) {
      switch (reader.selectName(PROPERTIES_NAMES)) {
        case 0:
          anim = parseAnimatableTextProperties(reader, composition);
          break;
        default:
          reader.skipName();
          reader.skipValue();
      }
    }
    reader.endObject();
    if (anim == null) {
      // Not sure if this is possible.
      return new AnimatableTextProperties(null, null, null, null);
    }
    return anim;
  }

  private static AnimatableTextProperties parseAnimatableTextProperties(
      JsonReader reader, LottieComposition composition) throws IOException {
    AnimatableColorValue color = null;
    AnimatableColorValue stroke = null;
    AnimatableFloatValue strokeWidth = null;
    AnimatableFloatValue tracking = null;

    reader.beginObject();
    while (reader.hasNext()) {
      switch (reader.selectName(ANIMATABLE_PROPERTIES_NAMES)) {
        case 0:
          color = AnimatableValueParser.parseColor(reader, composition);
          break;
        case 1:
          stroke = AnimatableValueParser.parseColor(reader, composition);
          break;
        case 2:
          strokeWidth = AnimatableValueParser.parseFloat(reader, composition);
          break;
        case 3:
          tracking = AnimatableValueParser.parseFloat(reader, composition);
          break;
        default:
          reader.skipName();
          reader.skipValue();
      }
    }
    reader.endObject();

    return new AnimatableTextProperties(color, stroke, strokeWidth, tracking);
  }
}
