package com.quicktvui.support.record.core.naming;


import com.quicktvui.support.record.core.AudioRecorderType;

public interface FileNameGenerator {

    String generate(String key, AudioRecorderType audioFormat);
}
