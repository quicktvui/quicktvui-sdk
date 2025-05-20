package com.quicktvui.support.subtitle.converter.universalchardet;

import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileCharsetConverter {

    public static String convertFileCharset(File file) {
        try {
            InputStream inputStream = new FileInputStream(file);
            String charset = UniversalDetector.detectCharset(inputStream);
            if (TextUtils.isEmpty(charset)) {
                charset = "UTF-8";
            } else if (charset.equals(Constants.CHARSET_GB18030)) {
                charset = "GBK";
            }
            return charset;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
