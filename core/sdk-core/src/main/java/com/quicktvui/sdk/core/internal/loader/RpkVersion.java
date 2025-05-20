package com.quicktvui.sdk.core.internal.loader;

public final class RpkVersion {

        private final String[] versions;

        public static RpkVersion parse(String version) {
            return new RpkVersion(version);
        }

        private RpkVersion(String version) {
            versions = version.split("\\.");
        }

        private int compareTo(RpkVersion version) {
            // 1.0      1.0.1   -1
            // 1.0.1    1.0      1
            // 1.0.1    1.1     -1
            // 1.1      1.2     -1
            // 1.2      1.1      1
            // 1.0      1.0      0
            int minLen = Math.min(versions.length, version.versions.length);
            for (int i = 0; i < minLen; i++) {
                int v1 = Integer.parseInt(versions[i]);
                int v2 = Integer.parseInt(version.versions[i]);
                if (v1 > v2) {
                    return 1;
                } else if (v1 < v2) {
                    return -1;
                }
            }
            return Integer.compare(versions.length, version.versions.length);
        }

        public boolean isGreaterThan(RpkVersion version) {
            return compareTo(version) == 1;
        }

        public boolean isLessThan(RpkVersion version) {
            return compareTo(version) == -1;
        }

        public boolean isGreaterOrEqualsThan(RpkVersion version) {
            return compareTo(version) >= 0;
        }

        public boolean isLessOrEqualsThan(RpkVersion version) {
            return compareTo(version) <= 0;
        }

        public boolean isEqualsThan(RpkVersion version) {
            return compareTo(version) == 0;
        }
    }