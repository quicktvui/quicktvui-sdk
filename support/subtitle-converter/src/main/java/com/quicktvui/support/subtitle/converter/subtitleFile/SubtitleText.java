package com.quicktvui.support.subtitle.converter.subtitleFile;

import java.io.Serializable;

public class SubtitleText implements Serializable {
    private String tag; //文件名+“_SRT”等
    private String fileName; //文件名
    private String mimeType; //SRT ASS STL TTML/XML
    private TimedTextObject tto;

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public TimedTextObject getTto() {
        return tto;
    }

    public void setTto(TimedTextObject tto) {
        this.tto = tto;
    }
}
