package com.quicktvui.sdk.core.utils;

import android.text.format.DateUtils;

import com.quicktvui.sdk.core.internal.Constants;

/**
 * <br>
 * 注释
 * <br>
 * <br>
 *
 * @Created by WeiPeng on 2024-03-29 17:35
 */
public class NexusRepo {

    private final String mBaseUrl;
    private String mRpkPackageUrl;


    private NexusRepo(String repo) {
        if (!repo.endsWith("/")) repo += "/";
        // http://repo.hsrc.tv/repository/rpk/
//        mBaseUrl = repo + Constants.REPO_RPK;
        mBaseUrl = repo;
    }

    public static NexusRepo from(String repo) {
        return new NexusRepo(repo);
    }

    public NexusRepo withPackage(String pkg) {
        // http://repo.hsrc.tv/repository/huantv/es.hello.world
        String fixPkg = fixPackage(pkg);
        mRpkPackageUrl = mBaseUrl + fixPkg.replaceAll("\\.", "/");
        return this;
    }

    private String fixPackage(String pkg) {
        if (pkg.startsWith("@")) {
            return pkg.substring(1);
        }
        return pkg;
    }

    public String getPackageUrl() {
        return mRpkPackageUrl;
    }

    public String getOuterMetaUrl() {
        return mRpkPackageUrl + Constants.Nexus.REPO_META + "?time=" + System.currentTimeMillis() / DateUtils.MINUTE_IN_MILLIS;
    }

    public String getOuterUrl(String fileName) {
        return String.format("%s/%s",
                mRpkPackageUrl, fileName);
    }

    // http://repo.hsrc.tv/repository/es.hello.world/1.0.0/meta.json
    public String getInnerMetaUrl(String ver) {
        return String.format("%s/%s%s",
                mRpkPackageUrl, ver, Constants.Nexus.REPO_META);
    }

    // http://repo.hsrc.tv/repository/es.hello.world/1.0.0/xxx.rpk
    public String getInnerUrl(String ver, String file) {
        return String.format("%s/%s/%s",
                mRpkPackageUrl, ver, file);
    }
}
