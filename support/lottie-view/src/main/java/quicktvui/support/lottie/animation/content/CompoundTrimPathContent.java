package quicktvui.support.lottie.animation.content;

import android.graphics.Path;

import java.util.ArrayList;
import java.util.List;

import quicktvui.support.lottie.utils.Utils;

public class CompoundTrimPathContent {
    private final List<TrimPathContent> contents = new ArrayList<>();

    void addTrimPath(TrimPathContent trimPath) {
        contents.add(trimPath);
    }

    public void apply(Path path) {
        for (int i = contents.size() - 1; i >= 0; i--) {
            Utils.applyTrimPathIfNeeded(path, contents.get(i));
        }
    }
}
