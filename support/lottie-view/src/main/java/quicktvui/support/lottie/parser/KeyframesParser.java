package quicktvui.support.lottie.parser;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import quicktvui.support.lottie.LottieComposition;
import quicktvui.support.lottie.animation.keyframe.PathKeyframe;
import quicktvui.support.lottie.parser.moshi.JsonReader;
import quicktvui.support.lottie.value.Keyframe;

class KeyframesParser {

  static JsonReader.Options NAMES = JsonReader.Options.of("k");

  private KeyframesParser() {
  }

  static <T> List<Keyframe<T>> parse(JsonReader reader, LottieComposition composition,
                                     float scale, ValueParser<T> valueParser, boolean multiDimensional) throws IOException {
    List<Keyframe<T>> keyframes = new ArrayList<>();

    if (reader.peek() == JsonReader.Token.STRING) {
      composition.addWarning("Lottie doesn't support expressions.");
      return keyframes;
    }

    reader.beginObject();
    while (reader.hasNext()) {
      switch (reader.selectName(NAMES)) {
        case 0:
          if (reader.peek() == JsonReader.Token.BEGIN_ARRAY) {
            reader.beginArray();

            if (reader.peek() == JsonReader.Token.NUMBER) {
              // For properties in which the static value is an array of numbers.
              keyframes.add(KeyframeParser.parse(reader, composition, scale, valueParser, false, multiDimensional));
            } else {
              while (reader.hasNext()) {
                keyframes.add(KeyframeParser.parse(reader, composition, scale, valueParser, true, multiDimensional));
              }
            }
            reader.endArray();
          } else {
            keyframes.add(KeyframeParser.parse(reader, composition, scale, valueParser, false, multiDimensional));
          }
          break;
        default:
          reader.skipValue();
      }
    }
    reader.endObject();

    setEndFrames(keyframes);
    return keyframes;
  }

  /**
   * The json doesn't include end frames. The data can be taken from the start frame of the next
   * keyframe though.
   */
  public static <T> void setEndFrames(List<? extends Keyframe<T>> keyframes) {
    int size = keyframes.size();
    for (int i = 0; i < size - 1; i++) {
      // In the json, the keyframes only contain their starting frame.
      Keyframe<T> keyframe = keyframes.get(i);
      Keyframe<T> nextKeyframe = keyframes.get(i + 1);
      keyframe.endFrame = nextKeyframe.startFrame;
      if (keyframe.endValue == null && nextKeyframe.startValue != null) {
        keyframe.endValue = nextKeyframe.startValue;
        if (keyframe instanceof PathKeyframe) {
          ((PathKeyframe) keyframe).createPath();
        }
      }
    }
    Keyframe<?> lastKeyframe = keyframes.get(size - 1);
    if ((lastKeyframe.startValue == null || lastKeyframe.endValue == null) && keyframes.size() > 1) {
      // The only purpose the last keyframe has is to provide the end frame of the previous
      // keyframe.
      keyframes.remove(lastKeyframe);
    }
  }
}
