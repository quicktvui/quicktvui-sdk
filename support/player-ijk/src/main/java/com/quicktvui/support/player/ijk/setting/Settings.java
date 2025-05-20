/*
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.quicktvui.support.player.ijk.setting;

import java.util.HashMap;
import java.util.Map;


public class Settings {
    public static final int PV_PLAYER__AndroidMediaPlayer = 1;
    public static final int PV_PLAYER__IjkMediaPlayer = 2;
    public static final int PV_PLAYER__IjkExoMediaPlayer = 3;
    public static final int PV_PLAYER__ApolloMediaPlayer = 4;
    public static final int IJK_OPTION_TYPE_TRADITION = 0;
    public static final int IJK_OPTION_TYPE_FULL_FAST = 1;
    public static final int IJK_OPTION_TYPE_BUFFER_FAST = 2;
    public static final int IJK_OPTION_TYPE_ANALYZE_FAST = 3;

    private static final Map<String, Integer> esRpkCountMap = new HashMap<>();
    private static final Map<String, Integer> categoryTypeMap = new HashMap<>();

    private int playerType = PV_PLAYER__IjkMediaPlayer;
    private int optionCategory = -1;
    private boolean useMediaCodec = true;
    private boolean useMediaDataSource = false;

    public int getPlayerType() {
        return playerType;
    }
    public int getOptionCategory(String appName) {
        if (optionCategory > -1) return optionCategory;
        Integer type = categoryTypeMap.get(appName);
        if (type == null) type = IJK_OPTION_TYPE_TRADITION;
        return type;
    }
    public boolean getUsingMediaCodec() {
        return useMediaCodec;
    }
    public boolean getUsingMediaDataSource() {
        return useMediaDataSource;
    }

    public void setPlayerType(int type) {
        playerType = type;
    }
    public void setOptionCategory(int type) {
        optionCategory = type;
    }
    // 设置ijk使用软解/硬解，默认硬解
    public void setUsingHardwareDecoder(boolean value) {
        useMediaCodec = value;
    }
    public void setUsingMediaDataSource(boolean use) {
        useMediaDataSource = use;
    }

    public static void setOptionCategory(String appName, int type) {
        categoryTypeMap.put(appName, type);
    }

    // 是否可以后台播放，暂时未使用
    public boolean getEnableBackgroundPlay() {
        return false;
    }

    public boolean getEnableDetachedSurfaceTextureView() {
        return false;
    }

    private int timedType;
    private boolean subChinese;
    private int subIndex = -1;
    private boolean audioChinese;
    private int audioIndex = -1;
    public void setTimedType(int type) {
        timedType = type;
    }

    public int getTimedType() {
        if (timedType < 0 || timedType > 3) {
            timedType = 0;
        }
        return timedType;
    }

    public boolean getSubChinese() {
        return subChinese;
    }

    public void setSubChinese(boolean subChinese) {
        this.subChinese = subChinese;
    }

    public int getSubIndex() {
        return subIndex;
    }

    public void setSubIndex(int subIndex) {
        this.subIndex = subIndex;
    }

    public boolean getAudioChinese() {
        return audioChinese;
    }

    public void setAudioChinese(boolean audioChinese) {
        this.audioChinese = audioChinese;
    }

    public int getAudioIndex() {
        return audioIndex;
    }

    public void setAudioIndex(int audioIndex) {
        this.audioIndex = audioIndex;
    }

    public void resetTrack() {
        timedType = 0;
        subChinese = false;
        subIndex = -1;
        audioChinese = false;
        audioIndex = -1;
    }

    public static void addRpkCountMap(String appName) {
        Integer oldCount = esRpkCountMap.get(appName);
        if (oldCount == null) oldCount = 0;
        esRpkCountMap.put(appName, ++oldCount);
    }

    public static void removeRpkCountMap(String appName) {
        Integer oldCount = esRpkCountMap.get(appName);
        if (oldCount != null && oldCount > 0) {
            if (--oldCount == 0) {
                removeAllMapByAppName(appName);
            } else {
                esRpkCountMap.put(appName, oldCount);
            }
        }
    }

    private static void removeAllMapByAppName(String appName) {
        categoryTypeMap.remove(appName);

        esRpkCountMap.remove(appName);
    }
}
