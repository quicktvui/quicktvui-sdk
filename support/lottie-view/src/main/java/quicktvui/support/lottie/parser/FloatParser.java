package quicktvui.support.lottie.parser;


import java.io.IOException;

import quicktvui.support.lottie.parser.moshi.JsonReader;

public class FloatParser implements ValueParser<Float> {
  public static final FloatParser INSTANCE = new FloatParser();

  private FloatParser() {
  }

  @Override public Float parse(JsonReader reader, float scale) throws IOException {
    return JsonUtils.valueFromObject(reader) * scale;
  }
}