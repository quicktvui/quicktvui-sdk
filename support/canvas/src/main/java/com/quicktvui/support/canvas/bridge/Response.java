/*
 * Copyright (c) 2021, the hapjs-platform Project Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.quicktvui.support.canvas.bridge;

import com.quicktvui.support.canvas.annotation.Extension;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Hybrid invocation response. Hold a status code and detail content.
 *
 * @ FeatureExtension
 */
public class Response implements java.io.Serializable {
    /**
     * Invocation is success
     */
    public static final int CODE_SUCCESS = 0;

    /**
     * {@link Extension.Mode#SYNC} invocation.
     */
    public static final int CODE_SYNC = 1;

    /**
     * {@link Extension.Mode#ASYNC} invocation.
     */
    public static final int CODE_ASYNC = 2;

    /**
     * {@link Extension.Mode#CALLBACK} invocation.
     */
    public static final int CODE_CALLBACK = 3;

    /**
     * 自定义callback方法
     */
    public static final int CODE_CUSTOM_CALLBACK = 4;

    /**
     * Invocation is cancelled.
     */
    public static final int CODE_CANCEL = 100;
    /**
     * IO error
     */
    public static final int CODE_IO_ERROR = 300;
    /**
     * file not found error
     */
    public static final int CODE_FILE_NOT_FOUND = 301;
    /**
     * OOM error
     */
    public static final int CODE_OOM_ERROR = 400;
    /**
     * Base feature error code, feature can define error code >= CODE_FEATURE_ERROR
     */
    public static final int CODE_FEATURE_ERROR = 1000;
    public static final Response SUCCESS = new Response(CODE_SUCCESS, "success");
    public static final Response CANCEL = new Response(CODE_CANCEL, "cancel");
    /**
     * Base error code, other system code should >= CODE_ERROR
     */
    private static final int CODE_ERROR = 200;
    /**
     * Invocation occurs generic error.
     */
    public static final int CODE_GENERIC_ERROR = CODE_ERROR;
    public static final Response ERROR = new Response(CODE_GENERIC_ERROR, "generic error");
    /**
     * User deny permission request
     */
    public static final int CODE_USER_DENIED = CODE_ERROR + 1;
    public static final Response USER_DENIED = new Response(CODE_USER_DENIED, "user denied");
    /**
     * Found illegal argument in request parameters
     */
    public static final int CODE_ILLEGAL_ARGUMENT = CODE_ERROR + 2;
    /**
     * Service is unavailable
     */
    public static final int CODE_SERVICE_UNAVAILABLE = CODE_ERROR + 3;
    /**
     * Invocation timeout
     */
    public static final int CODE_TIMEOUT = CODE_ERROR + 4;
    /**
     * Too many requests
     */
    public static final int CODE_TOO_MANY_REQUEST = CODE_ERROR + 5;
    public static final Response TOO_MANY_REQUEST =
            new Response(CODE_TOO_MANY_REQUEST, "too many requests");
    /**
     * Illegal request
     */
    public static final int CODE_ILLEGAL_REQUEST = CODE_ERROR + 6;
    /**
     * Base framework error code
     */
    private static final int CODE_FRAMEWORK_ERROR = 800;
    /**
     * ModuleExtension not found
     */
    public static final int CODE_NO_MODULE = CODE_FRAMEWORK_ERROR + 1;
    public static final Response NO_MODULE = new Response(CODE_NO_MODULE, "no module");
    /**
     * Action of module not found
     */
    public static final int CODE_NO_ACTION = CODE_FRAMEWORK_ERROR + 2;
    public static final Response NO_ACTION = new Response(CODE_NO_ACTION, "no action");
    /**
     * Invocation occurs configuration error.
     */
    public static final int CODE_CONFIG_ERROR = CODE_FRAMEWORK_ERROR + 3;
    /**
     * Invocation occurs permission error.
     */
    public static final int CODE_PERMISSION_ERROR = CODE_FRAMEWORK_ERROR + 4;
    private static final String CODE = "code";
    private static final String CONTENT = "content";

    private final int mCode;
    private final Object mContent;
    private JSONObject mJSONResult;

    /**
     * Construct a new instance with code {@link #CODE_SUCCESS} and specified content.
     *
     * @param content detail content.
     */
    public Response(Object content) {
        this(CODE_SUCCESS, content);
    }

    /**
     * Construct a new instance with specified code and content.
     *
     * @param code    status code.
     * @param content detail content.
     */
    public Response(int code, Object content) {
        mCode = code;
        mContent = content;
    }

    public static Response fromJSON(JSONObject jsonObject) throws JSONException {
        int code = jsonObject.getInt(CODE);
        Object content = jsonObject.get(CONTENT);
        return new Response(code, content);
    }

    /**
     * Get response code.
     *
     * @return response code.
     */
    public int getCode() {
        return mCode;
    }

    /**
     * Get response content.
     *
     * @return response content.
     */
    public Object getContent() {
        return mContent;
    }


    @Override
    public String toString() {
        return "Response { code=" + mCode + " content=" + mContent + " }";
    }

    private JSONObject buildJSON(Object content) {
        JSONObject result = new JSONObject();
        try {
            result.put(CODE, mCode);
            result.put(CONTENT, content);
        } catch (JSONException e) {
            throw new IllegalStateException("Fail to build json response", e);
        }
        return result;
    }
}
