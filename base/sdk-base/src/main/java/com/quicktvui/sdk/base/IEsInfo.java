package com.quicktvui.sdk.base;

public interface IEsInfo {

    //prop
    public static final String ES_PROP_INFO_PACKAGE_NAME = "packageName";
    public static final String ES_PROP_INFO_VERSION = "version";
    public static final String ES_PROP_INFO_ESKIT_VERSION = Constants.ESKIT_V_CODE;
    public static final String ES_PROP_INFO_CHANNEL = "channel";
    public static final String ES_PROP_INFO_BRANCH = "branch";
    public static final String ES_PROP_INFO_COMMIT_ID = "commitId";
    public static final String ES_PROP_INFO_RELEASE_TIME = "releaseTime";

    //op
    public static final String ES_OP_GET_ES_INFO = "getEsInfo";

    public void getEsInfo(EsPromise promise);
}
