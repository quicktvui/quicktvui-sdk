package com.quicktvui.sdk.core.internal.loader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * <br>
 *
 * <br>
 */
public class RecordInfo {

    @Nullable
    public String name = "";
    @NotNull
    public String pkg = "";
    @Nullable
    public String versionName;
    @Nullable
    public String versionCode = "";
    @Nullable
    public String iconNormal;
    @Nullable
    public String iconCircle;//circle_logo

    @Override
    public String toString() {
        return "Info{" +
                "name='" + name + '\'' +
                ", pkg='" + pkg + '\'' +
                ", versionName='" + versionName + '\'' +
                ", versionCode='" + versionCode + '\'' +
                ", iconNormal='" + iconNormal + '\'' +
                ", iconCircle='" + iconCircle + '\'' +
                '}';
    }
}
