package com.quicktvui.sdk.core.utils;

import static com.quicktvui.sdk.core.internal.Constants.ERR_DECRYPT;

import com.sunrain.toolkit.utils.FileIOUtils;
import com.sunrain.toolkit.utils.FileUtils;

import java.io.File;

import com.quicktvui.sdk.base.EsException;

/**
 *
 */
public class EsDecode {

    /** 默认解密 **/
    public static void decryptRpk(File originFile, File decryptFile) {
        try {
            byte[] bytes = FileIOUtils.readFile2BytesByStream(originFile);
            for (int i = 0; i < 100; i++) {
                byte tmp = bytes[i];
                bytes[i] = bytes[bytes.length - i - 1];
                bytes[bytes.length - i - 1] = tmp;
            }
            FileIOUtils.writeFileFromBytesByChannel(decryptFile, bytes, false, false);
        } catch (Exception e) {
            try {
                FileUtils.delete(originFile);
                FileUtils.delete(decryptFile);
            } catch (Exception ignore) {
            }
            throw new EsException(ERR_DECRYPT, "" + e.getMessage());
        }
    }

}
