package quicktvui.support.lottie.parser;


import java.io.IOException;

import quicktvui.support.lottie.parser.moshi.JsonReader;
import quicktvui.support.lottie.value.ScaleXY;

public class ScaleXYParser implements ValueParser<ScaleXY> {
  public static final ScaleXYParser INSTANCE = new ScaleXYParser();

  private ScaleXYParser() {
  }

  @Override public ScaleXY parse(JsonReader reader, float scale) throws IOException {
    boolean isArray = reader.peek() == JsonReader.Token.BEGIN_ARRAY;
    if (isArray) {
      reader.beginArray();
    }
    float sx = (float) reader.nextDouble();
    float sy = (float) reader.nextDouble();
    while (reader.hasNext()) {
      reader.skipValue();
    }
    if (isArray) {
      reader.endArray();
    }
    return new ScaleXY(sx / 100f * scale, sy / 100f * scale);
  }

}
