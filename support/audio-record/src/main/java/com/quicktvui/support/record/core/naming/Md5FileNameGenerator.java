package com.quicktvui.support.record.core.naming;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.quicktvui.support.record.core.AudioRecorderType;

public class Md5FileNameGenerator implements FileNameGenerator {

    private static final String HASH_ALGORITHM = "MD5";
    private static final int RADIX = 10 + 26; // 10 digits + 26 letters

    @Override
    public String generate(String key, AudioRecorderType audioFormat) {
        byte[] md5 = getMD5(key.getBytes());
        BigInteger bi = new BigInteger(md5).abs();
        return bi.toString(RADIX) + audioFormat.getFileNameSuffix();
    }

    private byte[] getMD5(byte[] data) {
        byte[] hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            digest.update(data);
            hash = digest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash;
    }
}
