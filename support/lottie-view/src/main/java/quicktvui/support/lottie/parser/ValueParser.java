package quicktvui.support.lottie.parser;


import java.io.IOException;

import quicktvui.support.lottie.parser.moshi.JsonReader;

interface ValueParser<V> {
  V parse(JsonReader reader, float scale) throws IOException;
}
