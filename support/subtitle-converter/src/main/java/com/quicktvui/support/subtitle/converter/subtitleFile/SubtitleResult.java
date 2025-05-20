package com.quicktvui.support.subtitle.converter.subtitleFile;

import java.io.Serializable;
import java.util.HashMap;

public class SubtitleResult implements Serializable {
    private HashMap<String, TimedTextObject> timedTextMap;

    public HashMap<String, TimedTextObject> getTimedTextMap() {
        return timedTextMap;
    }

    public void setTimedTextMap(HashMap<String, TimedTextObject> timedTextMap) {
        this.timedTextMap = timedTextMap;
    }
}
