package com.quicktvui.support.record.core.naming;


import com.quicktvui.support.record.core.AudioRecorderType;

public class HashCodeFileNameGenerator implements FileNameGenerator {
    @Override
    public String generate(String key, AudioRecorderType audioFormat) {
        return String.valueOf(key.hashCode()) + audioFormat.getFileNameSuffix();
    }
}
