package com.quicktvui.support.player.ijk.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.display.DisplayManager;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.os.Build;
import android.util.Log;
import android.view.Display;

import java.util.Arrays;

public class DisplayAndDecodeTools {

    private static final String TAG = "DisplayAndDecodeTools";

    /**
     * 检测屏幕显示对HDR的支持情况
     */
    public static void getDisplayLevel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            DisplayManager displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
            Display display = displayManager.getDisplay(0);
            Display.HdrCapabilities hdrCapabilities = display.getHdrCapabilities();
            int[] supportedHdrTypes = hdrCapabilities.getSupportedHdrTypes();
//            int HDR_TYPE_HDR10 = Display.HdrCapabilities.HDR_TYPE_HDR10;  2
//            int HDR_TYPE_HLG = Display.HdrCapabilities.HDR_TYPE_HLG;  3
//            int HDR_TYPE_HDR10_PLUS = Display.HdrCapabilities.HDR_TYPE_HDR10_PLUS;    4
//            int HDR_TYPE_DOLBY_VISION = Display.HdrCapabilities.HDR_TYPE_DOLBY_VISION;    1
            Log.d(TAG, "支持的HDR类型数量1：" + supportedHdrTypes.length);
            for (int i : supportedHdrTypes) {
                Log.d(TAG, "支持的HDR类型1：" + i);
            }
        }

    }

    public static void getDisplayLevel2(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            Display display = context.getDisplay();
            assert display != null;
            Display.HdrCapabilities hdrCapabilities = display.getHdrCapabilities();
            int[] supportedHdrTypes = hdrCapabilities.getSupportedHdrTypes();
            Log.d(TAG, "支持的HDR类型数量2：" + supportedHdrTypes.length);
            for (int i : supportedHdrTypes) {
                Log.d(TAG, "支持的HDR类型2：" + i);
            }
        }

    }

    public static void getDecodeLevel() {
        int codecCount = MediaCodecList.getCodecCount();
        Log.d(TAG, "编解码器列表1个数：" + codecCount);
        for (int i = 0; i < codecCount; i++) {
            MediaCodecInfo mediaCodecInfo = MediaCodecList.getCodecInfoAt(i);
            String[] supportedTypes = mediaCodecInfo.getSupportedTypes();
            Log.d(TAG, "编解码器列表1：" + mediaCodecInfo.getName()
                    + "\n支持类型：" + Arrays.toString(supportedTypes)
                    + "\n是否是编码器：" + mediaCodecInfo.isEncoder()
            );
//                for (String type: supportedTypes) {
//                    MediaCodecInfo.CodecCapabilities capabilitiesForType = mediaCodecInfo.getCapabilitiesForType(type);
//                    Log.d(TAG, "profileLevels：" + Arrays.toString(capabilitiesForType.profileLevels)
//                            + "\ncolorFormats：" + Arrays.toString(capabilitiesForType.colorFormats)
//                    );
//

        }
    }

    public static void getDecodeLevel2() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            MediaCodecList mediaCodecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
            MediaCodecInfo[] codecInfos = mediaCodecList.getCodecInfos();
            Log.d(TAG, "编解码器列表2个数：" + codecInfos.length);
            Log.d(TAG, "编解码器列表2个数：" + new MediaCodecList(MediaCodecList.REGULAR_CODECS).getCodecInfos().length);
            for (MediaCodecInfo mediaCodecInfo : codecInfos) {
                String[] supportedTypes = mediaCodecInfo.getSupportedTypes();
                Log.d(TAG, "编解码器列表2：" + mediaCodecInfo.getName()
                        + "\n支持类型：" + Arrays.toString(supportedTypes)
                        + "\n是否是编码器：" + mediaCodecInfo.isEncoder()
                );
//                for (String type: supportedTypes) {
//                    MediaCodecInfo.CodecCapabilities capabilitiesForType = mediaCodecInfo.getCapabilitiesForType(type);
//                    Log.d(TAG, "profileLevels：" + Arrays.toString(capabilitiesForType.profileLevels)
//                            + "\ncolorFormats：" + Arrays.toString(capabilitiesForType.colorFormats)
//                    );
//                }
            }
        }


    }

    @SuppressLint({"InlinedApi"})
    public static boolean isHlgHdrDecoderSupported(String codecName, String pixelFormat) {
        if ("h264".equals(codecName)) {
            return !"yuv420p10le".equalsIgnoreCase(pixelFormat);
        } else {
            int[] profiles = new int[]{MediaCodecInfo.CodecProfileLevel.HEVCProfileMain10};
            return checkDecoderAndProfile("video/hevc", profiles);
        }
    }

    @SuppressLint({"InlinedApi"})
    public static boolean isPqHdrDecoderSupported(String codecName, String pixelFormat) {
        if ("h264".equals(codecName)) {
            return !"yuv420p10le".equalsIgnoreCase(pixelFormat);
        } else {
            int[] profiles =
                    new int[]{MediaCodecInfo.CodecProfileLevel.HEVCProfileMain10HDR10,
                            MediaCodecInfo.CodecProfileLevel.HEVCProfileMain10HDR10Plus};
            return checkDecoderAndProfile("video/hevc", profiles);
        }
    }

    @SuppressLint({"InlinedApi"})
    public static boolean isDolbyVisionDecoderSupported() {
        return checkDecoderAndProfile("video/dolby-vision", null);
    }

    private static boolean checkDecoderAndProfile(String codecMimeType, int[] profiles) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            MediaCodecList codecList = new MediaCodecList(MediaCodecList.ALL_CODECS);
            MediaCodecInfo[] codecInfos = codecList.getCodecInfos();
            if (codecInfos != null) {
                for (MediaCodecInfo codecInfo : codecInfos) {
                    if (!codecInfo.isEncoder()) {
                        String[] supportedTypes = codecInfo.getSupportedTypes();
                        if (supportedTypes != null) {
                            for (String type : supportedTypes) {
                                if (type.equals(codecMimeType) && !isSoftwareCodec(codecInfo.getName())) {
                                    if (profiles == null || profiles.length == 0) {
                                        return true;
                                    }
                                    MediaCodecInfo.CodecCapabilities capabilities = codecInfo.getCapabilitiesForType(type);
                                    if (capabilities != null) {
                                        MediaCodecInfo.CodecProfileLevel[] var14 = capabilities.profileLevels;
                                        for (MediaCodecInfo.CodecProfileLevel cap : var14) {
                                            for (int profile : profiles) {
                                                if (cap.profile == profile) {
                                                    return true;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
        return false;
    }

    private static boolean isSoftwareCodec(String codecName) {
        return codecName.startsWith("c2.android.") || codecName.startsWith("OMX.google.") || codecName.startsWith("OMX.k3.ffmpeg") || codecName.startsWith("OMX.ffmpeg.") || codecName.startsWith("OMX.avcodec.") || codecName.startsWith("OMX.sprd.soft.") || codecName.startsWith("OMX.pv.") || !codecName.startsWith("OMX.") && !codecName.startsWith("c2.");
    }
}
