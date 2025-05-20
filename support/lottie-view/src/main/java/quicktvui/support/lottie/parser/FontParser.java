package quicktvui.support.lottie.parser;

import java.io.IOException;

import quicktvui.support.lottie.model.Font;
import quicktvui.support.lottie.parser.moshi.JsonReader;

class FontParser {
  private static final JsonReader.Options NAMES = JsonReader.Options.of(
      "fFamily",
      "fName",
      "fStyle",
      "ascent"
  );

  private FontParser() {
  }

  static Font parse(JsonReader reader) throws IOException {
    String family = null;
    String name = null;
    String style = null;
    float ascent = 0;

    reader.beginObject();
    while (reader.hasNext()) {
      switch (reader.selectName(NAMES)) {
        case 0:
          family = reader.nextString();
          break;
        case 1:
          name = reader.nextString();
          break;
        case 2:
          style = reader.nextString();
          break;
        case 3:
          ascent = (float) reader.nextDouble();
          break;
        default:
          reader.skipName();
          reader.skipValue();
      }
    }
    reader.endObject();

    return new Font(family, name, style, ascent);
  }
}
