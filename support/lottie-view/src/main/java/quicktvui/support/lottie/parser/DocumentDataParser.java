package quicktvui.support.lottie.parser;


import android.graphics.PointF;


import java.io.IOException;

import quicktvui.support.lottie.model.DocumentData;
import quicktvui.support.lottie.parser.moshi.JsonReader;

public class DocumentDataParser implements ValueParser<DocumentData> {
  public static final DocumentDataParser INSTANCE = new DocumentDataParser();
  private static final JsonReader.Options NAMES = JsonReader.Options.of(
      "t",  // 0
      "f",  // 1
      "s",  // 2
      "j",  // 3
      "tr", // 4
      "lh", // 5
      "ls", // 6
      "fc", // 7
      "sc", // 8
      "sw", // 9
      "of", // 10
      "ps", // 11
      "sz" // 12
  );

  private DocumentDataParser() {
  }

  @Override
  public DocumentData parse(JsonReader reader, float scale) throws IOException {
    String text = null;
    String fontName = null;
    float size = 0f;
    DocumentData.Justification justification = DocumentData.Justification.CENTER;
    int tracking = 0;
    float lineHeight = 0f;
    float baselineShift = 0f;
    int fillColor = 0;
    int strokeColor = 0;
    float strokeWidth = 0f;
    boolean strokeOverFill = true;
    PointF boxPosition = null;
    PointF boxSize = null;

    reader.beginObject();
    while (reader.hasNext()) {
      switch (reader.selectName(NAMES)) {
        case 0:
          text = reader.nextString();
          break;
        case 1:
          fontName = reader.nextString();
          break;
        case 2:
          size = (float) reader.nextDouble();
          break;
        case 3:
          int justificationInt = reader.nextInt();
          if (justificationInt > DocumentData.Justification.CENTER.ordinal() || justificationInt < 0) {
            justification = DocumentData.Justification.CENTER;
          } else {
            justification = DocumentData.Justification.values()[justificationInt];
          }
          break;
        case 4:
          tracking = reader.nextInt();
          break;
        case 5:
          lineHeight = (float) reader.nextDouble();
          break;
        case 6:
          baselineShift = (float) reader.nextDouble();
          break;
        case 7:
          fillColor = JsonUtils.jsonToColor(reader);
          break;
        case 8:
          strokeColor = JsonUtils.jsonToColor(reader);
          break;
        case 9:
          strokeWidth = (float) reader.nextDouble();
          break;
        case 10:
          strokeOverFill = reader.nextBoolean();
          break;
        case 11:
          reader.beginArray();
          boxPosition = new PointF((float) reader.nextDouble() * scale, (float) reader.nextDouble() * scale);
          reader.endArray();
          break;
        case 12:
          reader.beginArray();
          boxSize = new PointF((float) reader.nextDouble() * scale, (float) reader.nextDouble() * scale);
          reader.endArray();
          break;
        default:
          reader.skipName();
          reader.skipValue();
      }
    }
    reader.endObject();

    return new DocumentData(text, fontName, size, justification, tracking, lineHeight,
        baselineShift, fillColor, strokeColor, strokeWidth, strokeOverFill, boxPosition, boxSize);
  }
}
