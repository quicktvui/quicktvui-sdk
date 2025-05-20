package com.quicktvui.support.subtitle.converter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;
import com.quicktvui.support.subtitle.converter.subtitleFile.Caption;
import com.quicktvui.support.subtitle.converter.subtitleFile.FatalParsingException;
import com.quicktvui.support.subtitle.converter.subtitleFile.FormatASS;
import com.quicktvui.support.subtitle.converter.subtitleFile.FormatSRT;
import com.quicktvui.support.subtitle.converter.subtitleFile.FormatSTL;
import com.quicktvui.support.subtitle.converter.subtitleFile.FormatTTML;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

import com.quicktvui.support.subtitle.converter.subtitleFile.FormatVTT;
import com.quicktvui.support.subtitle.converter.subtitleFile.SubtitleResult;
import com.quicktvui.support.subtitle.converter.subtitleFile.SubtitleText;
import com.quicktvui.support.subtitle.converter.subtitleFile.TimedTextFileFormat;
import com.quicktvui.support.subtitle.converter.subtitleFile.TimedTextObject;
import com.quicktvui.support.subtitle.converter.universalchardet.FileCharsetConverter;


@ESKitAutoRegister
public class ESSubtitleModule implements IEsModule, IEsInfo {

    private static final String TAG = "[-ESSubtitleModule-]";

    public static final String TEXT_SRT = "srt";
    public static final String TEXT_BIG_SRT = "SRT";
    public static final String TEXT_ASS = "ass";
    public static final String TEXT_BIG_ASS = "ASS";
    public static final String TEXT_STL = "stl";
    public static final String TEXT_BIG_STL = "STL";
    public static final String TEXT_TTML = "ttml";
    public static final String TEXT_BIG_TTML = "TTML";
    public static final String TEXT_XML = "xml";
    public static final String TEXT_BIG_XML = "XNL";
    public static final String TEXT_VTT = "vtt";//
    public static final String TEXT_BIG_VTT = "VTT";//
    public static final String EVENT_RESULT = "result";
    public static final String EVENT_DATA = "data";
    public static final String EVENT_CONTENT = "content";
    public static final int SUCCESS = 1; //解析成功
    public static final int FAILED = 2; //解析失败
    public static final int NOT_FOUND = 3; //文件未找到
    public static final int NOT_SUPPORT = 4; //文件格式不支持
    //
    private final int msgSRT = 111;
    private final int msgASS = 222;
    private final int msgVTT = 333;
    private final int msgSTL = 444;
    private final int msgTTML = 555;
    private TimedTextObject tto;
    private TimedTextFileFormat ttff;
    private Charset charset;
    private int currentSubtitleType; //当前字幕解析方式 1：srt 2：ass 3：stl 4：ttml/xml 5：vtt
    private String currentSubtitleName; //当前电影名称
    //保存所有字幕map  key默认为fileName--文件名称，前端可自定义
    private HashMap<String, SubtitleResult> subtitleMap = new HashMap<>();
    private Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            SubtitleText subtitleText = null;
            SubtitleResult subtitleResult = null;
            EsMap eventMap = new EsMap();
            switch (msg.what) {
                case msgSRT:
                    subtitleText = (SubtitleText) msg.obj;
                    break;
                case msgASS:
                    subtitleText = (SubtitleText) msg.obj;
                    break;
                case msgVTT:
                    subtitleText = (SubtitleText) msg.obj;
                    break;
                case msgSTL:
                    subtitleText = (SubtitleText) msg.obj;
                    break;
                case msgTTML:
                    subtitleText = (SubtitleText) msg.obj;
                    break;
            }
            if (subtitleText != null) {
                if (!subtitleMap.containsKey(subtitleText.getFileName())) {
                    subtitleResult = new SubtitleResult();
                    HashMap<String, TimedTextObject> timedTextMap = new HashMap<>();
                    timedTextMap.put(subtitleText.getTag(), subtitleText.getTto());
                    subtitleResult.setTimedTextMap(timedTextMap);
                    subtitleMap.put(subtitleText.getFileName(), subtitleResult);
                } else {
                    subtitleResult = subtitleMap.get(subtitleText.getFileName());
                    if (subtitleResult.getTimedTextMap() != null) {
                        //直接覆盖
                        subtitleResult.getTimedTextMap().put(subtitleText.getTag(), subtitleText.getTto());
                    } else {
                        HashMap<String, TimedTextObject> timedTextMap = new HashMap<>();
                        timedTextMap.put(subtitleText.getTag(), subtitleText.getTto());
                        subtitleResult.setTimedTextMap(timedTextMap);
                        subtitleMap.put(subtitleText.getFileName(), subtitleResult);
                    }
                }
                eventMap.pushMap(EVENT_DATA, toEsMap(subtitleText));
                eventMap.pushInt(EVENT_RESULT, SUCCESS);
            } else {
                eventMap.pushInt(EVENT_RESULT, FAILED);
            }
            sendNativeSubtitleCompleteEvent(eventMap);
        }
    };

    @Override
    public void getEsInfo(EsPromise promise) {

    }

    @Override
    public void init(Context context) {
        charset = Charset.defaultCharset();

        Log.w(TAG, "init");
    }

    /**
     * 解析字幕文件
     *
     * @param title
     * @param filePath
     */
    public void converterSubtitleWithTitle(String title, String filePath) {
        Log.w(TAG, "converterSubtitleWithTitle " + title);
        Log.w(TAG, "converterSubtitleWithTitle " + filePath);
        this.converterSubtitleWithFile(title, FileUtils.getFileByPath(filePath));
    }

    /**
     * 解析字幕文件
     *
     * @param filePath
     */
    public void converterSubtitle(String filePath) {
        this.converterSubtitleWithFile("", FileUtils.getFileByPath(filePath));
    }

    /**
     * 解析字幕文件
     *
     * @param file
     */
    private void converterSubtitleWithFile(String title, File file) {
        EsMap eventMap = new EsMap();
        if (FileUtils.isFileExists(file)) {
            String simpleCharset = FileCharsetConverter.convertFileCharset(file);
            String fileExtension = FileUtils.getFileExtension(file);
            switch (fileExtension) {
                case TEXT_BIG_SRT:
                case TEXT_SRT:
                    Log.w(TAG, "srt");
                    currentSubtitleType = 1;
                    String fileTitle;
                    if (!TextUtils.isEmpty(title)) {
                        fileTitle = title;
                    } else {
                        fileTitle = FileUtils.getFileNameNoExtension(file);
                    }
                    currentSubtitleName = fileTitle;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ttff = new FormatSRT();
                                tto = ttff.parseFile("test", new FileInputStream(file), Charset.forName(simpleCharset));
                                SubtitleText subtitleText = new SubtitleText();
                                subtitleText.setTag(fileTitle + "_SRT");
                                subtitleText.setFileName(fileTitle);
                                subtitleText.setMimeType("SRT");
                                subtitleText.setTto(tto);
                                Message message = new Message();
                                message.what = msgSRT;
                                message.obj = subtitleText;
                                handler.sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                                eventMap.pushInt(EVENT_RESULT, FAILED);
                                sendNativeSubtitleCompleteEvent(eventMap);
                            } catch (FatalParsingException e) {
                                e.printStackTrace();
                                eventMap.pushInt(EVENT_RESULT, FAILED);
                                sendNativeSubtitleCompleteEvent(eventMap);
                            }
                        }
                    }).start();
                    break;
                case TEXT_BIG_ASS:
                case TEXT_ASS:
                    Log.w(TAG, "ass");
                    currentSubtitleType = 2;
                    String fileTitle2;
                    if (!TextUtils.isEmpty(title)) {
                        fileTitle2 = title;
                    } else {
                        fileTitle2 = FileUtils.getFileNameNoExtension(file);
                    }
                    currentSubtitleName = fileTitle2;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ttff = new FormatASS();
                                tto = ttff.parseFile("test", new FileInputStream(file), Charset.forName(simpleCharset));
                                SubtitleText subtitleText = new SubtitleText();
                                subtitleText.setTag(fileTitle2 + "_ASS");
                                subtitleText.setFileName(fileTitle2);
                                subtitleText.setMimeType("ASS");
                                subtitleText.setTto(tto);
                                Message message = new Message();
                                message.what = msgASS;
                                message.obj = subtitleText;
                                handler.sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                                eventMap.pushInt(EVENT_RESULT, FAILED);
                                sendNativeSubtitleCompleteEvent(eventMap);
                            } catch (FatalParsingException e) {
                                e.printStackTrace();
                                eventMap.pushInt(EVENT_RESULT, FAILED);
                                sendNativeSubtitleCompleteEvent(eventMap);
                            }
                        }
                    }).start();
                    break;
                case TEXT_BIG_STL:
                case TEXT_STL:
                    Log.w(TAG, "stl");
                    currentSubtitleType = 3;
                    String fileTitle3;
                    if (!TextUtils.isEmpty(title)) {
                        fileTitle3 = title;
                    } else {
                        fileTitle3 = FileUtils.getFileNameNoExtension(file);
                    }
                    currentSubtitleName = fileTitle3;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ttff = new FormatSTL();
                                tto = ttff.parseFile("test", new FileInputStream(file), Charset.forName(simpleCharset));
                                SubtitleText subtitleText = new SubtitleText();
                                subtitleText.setTag(fileTitle3 + "_STL");
                                subtitleText.setFileName(fileTitle3);
                                subtitleText.setMimeType("STL");
                                subtitleText.setTto(tto);
                                Message message = new Message();
                                message.what = msgSTL;
                                message.obj = subtitleText;
                                handler.sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                                eventMap.pushInt(EVENT_RESULT, FAILED);
                                sendNativeSubtitleCompleteEvent(eventMap);
                            } catch (FatalParsingException e) {
                                e.printStackTrace();
                                eventMap.pushInt(EVENT_RESULT, FAILED);
                                sendNativeSubtitleCompleteEvent(eventMap);
                            }
                        }
                    }).start();
                    break;
                case TEXT_BIG_XML:
                case TEXT_BIG_TTML:
                case TEXT_XML:
                case TEXT_TTML:
                    Log.w(TAG, "xml");
                    currentSubtitleType = 4;
                    String fileTitle4;
                    if (!TextUtils.isEmpty(title)) {
                        fileTitle4 = title;
                    } else {
                        fileTitle4 = FileUtils.getFileNameNoExtension(file);
                    }
                    currentSubtitleName = fileTitle4;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ttff = new FormatTTML();
                                tto = ttff.parseFile("test", new FileInputStream(file), Charset.forName(simpleCharset));
                                SubtitleText subtitleText = new SubtitleText();
                                subtitleText.setTag(fileTitle4 + "_TTML");
                                subtitleText.setFileName(fileTitle4);
                                subtitleText.setMimeType("TTML/XML");
                                subtitleText.setTto(tto);
                                Message message = new Message();
                                message.what = msgTTML;
                                message.obj = subtitleText;
                                handler.sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                                eventMap.pushInt(EVENT_RESULT, FAILED);
                                sendNativeSubtitleCompleteEvent(eventMap);
                            } catch (FatalParsingException e) {
                                e.printStackTrace();
                                eventMap.pushInt(EVENT_RESULT, FAILED);
                                sendNativeSubtitleCompleteEvent(eventMap);
                            }
                        }
                    }).start();
                    break;
                case TEXT_VTT:
                case TEXT_BIG_VTT:
                    Log.w(TAG, "vtt");
                    currentSubtitleType = 5;
                    String fileTitle5;
                    if (!TextUtils.isEmpty(title)) {
                        fileTitle5 = title;
                    } else {
                        fileTitle5 = FileUtils.getFileNameNoExtension(file);
                    }
                    currentSubtitleName = fileTitle5;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                ttff = new FormatVTT();
                                tto = ttff.parseFile("test", new FileInputStream(file));
                                SubtitleText subtitleText = new SubtitleText();
                                subtitleText.setTag(fileTitle5 + "_VTT");
                                subtitleText.setFileName(fileTitle5);
                                subtitleText.setMimeType("VTT");
                                subtitleText.setTto(tto);
                                Message message = new Message();
                                message.what = msgVTT;
                                message.obj = subtitleText;
                                handler.sendMessage(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                                eventMap.pushInt(EVENT_RESULT, FAILED);
                                sendNativeSubtitleCompleteEvent(eventMap);
                            } catch (FatalParsingException e) {
                                e.printStackTrace();
                                eventMap.pushInt(EVENT_RESULT, FAILED);
                                sendNativeSubtitleCompleteEvent(eventMap);
                            }
                        }
                    }).start();
                    break;
                default:
                    Log.w(TAG, "not support " + fileExtension);
                    eventMap.pushInt(EVENT_RESULT, NOT_SUPPORT);
                    sendNativeSubtitleCompleteEvent(eventMap);
            }
        } else {
            Log.w(TAG, "file not exists");
            eventMap.pushInt(EVENT_RESULT, NOT_FOUND);
            sendNativeSubtitleCompleteEvent(eventMap);
        }
    }

    /**
     * 给前端发送当前定位字幕
     *
     * @param position
     */
    public void seekToSubtitle(long position) {
        String content = this.seekTo(position);
        EsMap eventMap = new EsMap();
        eventMap.pushString(EVENT_CONTENT, content);
        sendNativeSeekToSubtitleEvent(eventMap);
    }

    /**
     * 定位到某个时间点
     *
     * @param position
     */
    private String seekTo(long position) {
        if (currentSubtitleType != 0) {
            if (currentSubtitleType == 1 && !TextUtils.isEmpty(currentSubtitleName)) {
                if (subtitleMap != null && subtitleMap.get(currentSubtitleName) != null) {
                    SubtitleResult subtitleResult = subtitleMap.get(currentSubtitleName);
                    if (subtitleResult.getTimedTextMap() != null) {
                        HashMap<String, TimedTextObject> timedTextMap = subtitleResult.getTimedTextMap();
                        if (timedTextMap.containsKey(currentSubtitleName + "_SRT")) {
                            TimedTextObject tto = timedTextMap.get(currentSubtitleName + "_SRT");
                            return seekToPos(position, tto);
                        }
                    }
                }
            } else if (currentSubtitleType == 2 && !TextUtils.isEmpty(currentSubtitleName)) {
                if (subtitleMap != null && subtitleMap.get(currentSubtitleName) != null) {
                    SubtitleResult subtitleResult = subtitleMap.get(currentSubtitleName);
                    if (subtitleResult.getTimedTextMap() != null) {
                        HashMap<String, TimedTextObject> timedTextMap = subtitleResult.getTimedTextMap();
                        if (timedTextMap.containsKey(currentSubtitleName + "_ASS")) {
                            TimedTextObject tto = timedTextMap.get(currentSubtitleName + "_ASS");
                            return seekToPos(position, tto);
                        }
                    }
                }
            } else if (currentSubtitleType == 3 && !TextUtils.isEmpty(currentSubtitleName)) {
                if (subtitleMap != null && subtitleMap.get(currentSubtitleName) != null) {
                    SubtitleResult subtitleResult = subtitleMap.get(currentSubtitleName);
                    if (subtitleResult.getTimedTextMap() != null) {
                        HashMap<String, TimedTextObject> timedTextMap = subtitleResult.getTimedTextMap();
                        if (timedTextMap.containsKey(currentSubtitleName + "_STL")) {
                            TimedTextObject tto = timedTextMap.get(currentSubtitleName + "_STL");
                            return seekToPos(position, tto);
                        }
                    }
                }
            } else if (currentSubtitleType == 4 && !TextUtils.isEmpty(currentSubtitleName)) {
                if (subtitleMap != null && subtitleMap.get(currentSubtitleName) != null) {
                    SubtitleResult subtitleResult = subtitleMap.get(currentSubtitleName);
                    if (subtitleResult.getTimedTextMap() != null) {
                        HashMap<String, TimedTextObject> timedTextMap = subtitleResult.getTimedTextMap();
                        if (timedTextMap.containsKey(currentSubtitleName + "_TTML")) {
                            TimedTextObject tto = timedTextMap.get(currentSubtitleName + "_TTML");
                            return seekToPos(position, tto);
                        }
                    }
                }
            } else if (currentSubtitleType == 5 && !TextUtils.isEmpty(currentSubtitleName)) {
                if (subtitleMap != null && subtitleMap.get(currentSubtitleName) != null) {
                    SubtitleResult subtitleResult = subtitleMap.get(currentSubtitleName);
                    if (subtitleResult.getTimedTextMap() != null) {
                        HashMap<String, TimedTextObject> timedTextMap = subtitleResult.getTimedTextMap();
                        if (timedTextMap.containsKey(currentSubtitleName + "_VTT")) {
                            TimedTextObject tto = timedTextMap.get(currentSubtitleName + "_VTT");
                            return seekToPos(position, tto);
                        }
                    }
                }
            }
        }
        return "";
    }

    private String seekToPos(long position, TimedTextObject tto) {
        if (tto != null && !tto.captions.isEmpty()) {
            List<Caption> captions = searchSub(tto.captions, position);
            if (captions.size() > 0) {
                Caption caption = captions.get(0);
                return formatContent(caption.content);
            }
        }
        return "";
    }

    /**
     * 排除字幕中含有“{}”中的内容
     *
     * @param content
     * @return
     */
    private String formatContent(String content) {
        String resultContent = "";
        if (!TextUtils.isEmpty(content)) {
            resultContent = content;
            boolean tag = true;
            while (tag) {
                if (resultContent.contains("{") && resultContent.contains("}")) {
                    int first = resultContent.indexOf("{");
                    int second = resultContent.indexOf("}");
                    String delete = resultContent.substring(first, second + 1);
                    resultContent = resultContent.replace(delete, "");
                } else {
                    tag = false;
                }
            }
        }
        return resultContent;
    }

    public List<Caption> searchSub(TreeMap<Integer, Caption> list, long key) {
        List<Caption> captions = new ArrayList<>();
        boolean hasMore = true;
        for (Integer key1 : list.keySet()) {
            Caption caption = list.get(key1);
            if (hasMore) {
                if (key >= caption.start.getMseconds() && key <= caption.end.getMseconds()) {
                    hasMore = false;
                    captions.add(caption);
                }
            } else {
                break;
            }
        }
        return captions;
    }

    private EsMap toEsMap(SubtitleText subtitleText) {
        EsMap esMap = new EsMap();
        EsArray captionsArray = new EsArray();
        esMap.pushString("title", subtitleText.getFileName());
        esMap.pushString("mimeType", subtitleText.getMimeType());
        if (subtitleText.getTto() != null && !subtitleText.getTto().captions.isEmpty()) {
            Collection<Caption> captions = subtitleText.getTto().captions.values();
            List<Caption> lists = new ArrayList<>();
            lists.addAll(captions);
            for (int i = 0; i < lists.size(); i++) {
                Caption caption = lists.get(i);
                if (caption != null) {
                    EsMap esMap1 = new EsMap();
                    esMap1.pushString("content", caption.content);
                    esMap1.pushInt("start", caption.start.getMseconds());
                    esMap1.pushInt("end", caption.end.getMseconds());
                    captionsArray.pushMap(esMap1);
                }
            }
        }
        esMap.pushArray("captions", captionsArray);
        return esMap;
    }

    private void sendNativeSubtitleCompleteEvent(EsMap esMap) {
        EsProxy.get().sendNativeEventTraceable(this, Events.EVENT_ON_SUBTITLE_CONVERTER_COMPLETE.toString(), esMap);
    }

    private void sendNativeSeekToSubtitleEvent(EsMap esMap) {
        EsProxy.get().sendNativeEventTraceable(this, Events.EVENT_ON_SEEK_TO_SUBTITLE.toString(), esMap);
    }

    public enum Events {
        //字幕解析完成
        EVENT_ON_SUBTITLE_CONVERTER_COMPLETE("onSubtitleConverterComplete"),
        EVENT_ON_SEEK_TO_SUBTITLE("onSeekToSubtitle");

        private final String mName;

        Events(final String name) {
            mName = name;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    @Override
    public void destroy() {
        if (subtitleMap != null) {
            subtitleMap.clear();
        }
    }
}
