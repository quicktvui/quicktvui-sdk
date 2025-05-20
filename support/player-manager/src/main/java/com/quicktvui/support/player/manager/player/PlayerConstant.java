package com.quicktvui.support.player.manager.player;


public class PlayerConstant {

    public static final class Preview {
        public static final int UNKNOWN = 0;
    }

    public static final class Error {
        public static final int COMMON_ERROR_NET_DISCONNECTED = -9000;
        public static final int COMMON_ERROR_PARSER_ERROR = -9001;
        public static final int COMMON_ERROR_NO_URL_ERROR = -9002;
        public static final int COMMON_ERROR_AUTH_ERROR = -9003;
    }

    public static final class ErrorCode {
        public static final int UNKNOWN = -1;
        public static final int ILLEGAL_ARGUMENT = 90000;
        public static final int UNINITIALIZED = 90001;
        public static final int UN_SUPPORT_OPERATION = 90002;
    }

    public static final class ErrorMessage {
        public static final String UNKNOWN = "未知错误";
        public static final String ILLEGAL_ARGUMENT = "参数错误";
        public static final String UNINITIALIZED = "播放器没有初始化";
        public static final String UN_SUPPORT_OPERATION = "不支持的操作";
    }
}
