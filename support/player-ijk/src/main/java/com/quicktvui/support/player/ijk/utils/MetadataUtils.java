package com.quicktvui.support.player.ijk.utils;

import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaPlayer;
import android.os.Build;

import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.support.ijk.base.misc.AndroidTrackInfo;
import com.quicktvui.support.ijk.base.misc.ITrackInfo;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MetadataUtils {

    public static final String STREAM_KEY_INDEX = "index";
    public static final String STREAM_KEY_SEEK_FLAG = "seekFlag";
    public static final String STREAM_KEY_TRACK_TYPE = "trackType";
    public static final String STREAM_KEY_LANGUAGE = "language";
    public static final String STREAM_KEY_TITLE = "title";
    public static final String STREAM_KEY_SUB_TYPE = "subType";
    public static final String STREAM_KEY_CODEC = "codec";
    public static final String STREAM_KEY_CHANNELS = "channels";
    public static final String STREAM_KEY_CHANNEL_NAME = "channelName";
    public static final String STREAM_KEY_VIDEO_WIDTH = "videoWidth";
    public static final String STREAM_KEY_VIDEO_HEIGHT = "videoHeight";
    public static final String IJK_STREAM_KEY_CODE_NAME = "codec_name";
    public static final String IJK_STREAM_KEY_CHANNELS = "channels";
    public static final String IJK_STREAM_KEY_WIDTH = "width";
    public static final String IJK_STREAM_KEY_HEIGHT = "height";
    public static final String APOLLO_STREAM_KEY_PLUGIN = "isPlugin";
    public static final String APOLLO_STREAM_KEY_DETECTED_LANG = "detectedLanguage";
    public static final String APOLLO_STREAM_KEY_ID = "resourceID";

    private static final Map<String, Field> fieldMap = new HashMap<>();

    public static void getAndroidTrackInfo(AndroidTrackInfo trackInfo, EsMap map) {
        try {
//            Field mTrackInfoField = trackInfo.getClass().getDeclaredField("mTrackInfo");
            Field mTrackInfoField = getFieldByName("mTrackInfo", trackInfo.getClass());
            mTrackInfoField.setAccessible(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                MediaPlayer.TrackInfo mTrackInfo = (MediaPlayer.TrackInfo) mTrackInfoField.get(trackInfo);

                if (mTrackInfo != null) {
//                    @SuppressLint("PrivateApi") Field mFormatField = mTrackInfo.getClass().getDeclaredField("mFormat");
                    Field mFormatField = getFieldByName("mFormat", mTrackInfo.getClass());
                    mFormatField.setAccessible(true);
                    MediaFormat mFormat = (MediaFormat) mFormatField.get(mTrackInfo);

                    if (mFormat != null) {
                        map.pushString(STREAM_KEY_CODEC, mFormat.getString(MediaFormat.KEY_MIME));

                    }

                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void getAndroidTracksInfo(String url, EsPromise promise) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            MediaExtractor extractor = new MediaExtractor();
            EsArray array = new EsArray();
            try {
                extractor.setDataSource(url);
                for (int i = 0; i < extractor.getTrackCount(); i++) {
                    MediaFormat trackFormat = extractor.getTrackFormat(i);
                    if (trackFormat != null) {
                        EsMap map = new EsMap();

                        String mime = trackFormat.getString(MediaFormat.KEY_MIME);
                        int trackType = getTrackType(mime);
                        map.pushInt(STREAM_KEY_INDEX, i);

                        map.pushBoolean(STREAM_KEY_SEEK_FLAG, true);
                        map.pushInt(STREAM_KEY_TRACK_TYPE, trackType);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            map.pushString(STREAM_KEY_LANGUAGE, trackFormat.getString(MediaFormat.KEY_LANGUAGE));
                        }
                        map.pushString(STREAM_KEY_CODEC, mime);

                        if (trackType == ITrackInfo.MEDIA_TRACK_TYPE_AUDIO) {
                            int channels;
                            try {
                                channels = trackFormat.getInteger(MediaFormat.KEY_CHANNEL_COUNT);
                            } catch (NullPointerException e) {
                                channels = 0;
                            }
                            String channelName = AudioChannelType.getChannelName(channels);
                            map.pushInt(STREAM_KEY_CHANNELS, channels);
                            map.pushString(STREAM_KEY_CHANNEL_NAME, channelName);
                        } else if (trackType == ITrackInfo.MEDIA_TRACK_TYPE_VIDEO) {
                            try {
                                map.pushInt(STREAM_KEY_VIDEO_WIDTH, trackFormat.getInteger(MediaFormat.KEY_WIDTH));
                                map.pushInt(STREAM_KEY_VIDEO_HEIGHT, trackFormat.getInteger(MediaFormat.KEY_HEIGHT));
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }
                        }

                        array.pushMap(map);
                    }
                    extractor.unselectTrack(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                promise.resolve(array);
                extractor.release();
            }
        }
    }

    public static int getTrackType(String mime) {
        if (mime == null) return -1;
        if (mime.startsWith("video/")) {
            return 1;
        }
        if (mime.startsWith("audio/")) {
            return 2;
        }
        if (mime.startsWith("subtitle/")) {
            return 3;
        }

        return -1;
    }

    private static Field getFieldByName(String fieldName, Class<?> clazz) {
        Field field = fieldMap.get(fieldName);
        if (field != null) return field;
        try {
            field = clazz.getDeclaredField(fieldName);
            fieldMap.put(fieldName, field);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return field;
    }
}
