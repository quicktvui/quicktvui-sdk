package quicktvui.support.lottie.parser;

import android.graphics.PointF;


import java.io.IOException;

import quicktvui.support.lottie.LottieComposition;
import quicktvui.support.lottie.animation.keyframe.PathKeyframe;
import quicktvui.support.lottie.parser.moshi.JsonReader;
import quicktvui.support.lottie.utils.Utils;
import quicktvui.support.lottie.value.Keyframe;

class PathKeyframeParser {

  private PathKeyframeParser() {
  }

  static PathKeyframe parse(
          JsonReader reader, LottieComposition composition) throws IOException {
    boolean animated = reader.peek() == JsonReader.Token.BEGIN_OBJECT;
    Keyframe<PointF> keyframe = KeyframeParser.parse(
        reader, composition, Utils.dpScale(), PathParser.INSTANCE, animated, false);

    return new PathKeyframe(composition, keyframe);
  }
}
