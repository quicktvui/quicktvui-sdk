package quicktvui.support.lottie.parser;

import java.io.IOException;

import quicktvui.support.lottie.parser.moshi.JsonReader;

public class IntegerParser implements ValueParser<Integer> {
  public static final IntegerParser INSTANCE = new IntegerParser();

  private IntegerParser() {
  }

  @Override public Integer parse(JsonReader reader, float scale) throws IOException {
    return Math.round(JsonUtils.valueFromObject(reader) * scale);
  }
}
