package com.quicktvui.support.record.core.naming;

import com.quicktvui.support.record.core.AudioRecorderType;

/**
 *
 */
public class NoOpFileNameGenerator implements FileNameGenerator {

    @Override
    public String generate(String key, AudioRecorderType audioFormat) {
        return key + audioFormat.getFileNameSuffix();
    }
}
