package com.quicktvui.sdk.base;

import android.support.annotation.Nullable;

import com.quicktvui.sdk.base.args.EsMap;

/**
 * Create by weipeng on 2022/03/21 20:12
 */
public class EsException extends RuntimeException {
    private final int code;
    private int reasonCode;
    private final EsMap data;

    public EsException(String message) {
        this(-1, message);
    }

    public EsException(int code, String message) {
        this(code, message, null);
    }

    public EsException(int code, String message, EsMap data) {
        super(message);
        this.code = code;
        this.data = data;
    }

    public EsException setReasonCode(int secondCode) {
        this.reasonCode = secondCode;
        return this;
    }

    public int getCode() {
        return code;
    }

    public int getReasonCode() {
        return reasonCode;
    }

    public EsMap getData() {
        return data;
    }

    @Nullable
    @Override
    public String getMessage() {
        return "code: " + code + ", reasonCode: " + reasonCode + ", message: " + super.getMessage();
    }

    public String getErrorMessage() {
        return super.getMessage();
    }
}
