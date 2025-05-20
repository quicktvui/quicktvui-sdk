package com.quicktvui.support.log.module;

import android.content.Context;
import android.text.TextUtils;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.base.EsPromise;
import com.quicktvui.sdk.base.IEsInfo;
import com.quicktvui.sdk.base.args.EsArray;
import com.quicktvui.sdk.base.args.EsMap;
import com.quicktvui.sdk.base.core.EsProxy;
import com.quicktvui.sdk.base.module.IEsModule;
import com.quicktvui.support.log.LogConfiguration;
import com.quicktvui.support.log.flattener.ClassicFlattener;
import com.quicktvui.support.log.flattener.DefaultFlattener;
import com.quicktvui.support.log.flattener.PatternFlattener;
import com.quicktvui.support.log.interceptor.BlacklistTagsFilterInterceptor;
import com.quicktvui.support.log.printer.AndroidPrinter;
import com.quicktvui.support.log.printer.Printer;
import com.quicktvui.support.log.printer.file.FilePrinter;
import com.quicktvui.support.log.printer.file.backup.FileSizeBackupStrategy2;
import com.quicktvui.support.log.printer.file.backup.NeverBackupStrategy;
import com.quicktvui.support.log.printer.file.clean.FileLastModifiedCleanStrategy;
import com.quicktvui.support.log.printer.file.clean.NeverCleanStrategy;
import com.quicktvui.support.log.printer.file.naming.ChangelessFileNameGenerator;
import com.quicktvui.support.log.printer.file.naming.DateFileNameGenerator;
import com.quicktvui.support.log.printer.file.naming.LevelFileNameGenerator;
import com.quicktvui.support.log.printer.file.writer.SimpleWriter;
import com.quicktvui.support.log.ESLog;
import com.sunrain.toolkit.utils.log.L;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


@ESKitAutoRegister
public class ESLoggerModule implements IEsModule, IEsInfo {

    @Override
    public void init(Context context) {

    }

    /**
     * 初始化
     */
    public void initLog(EsMap configuration) {
        if (L.DEBUG) {
            L.logD("#------initLog--------->>>" + configuration);
        }
        if (configuration != null) {
            LogConfiguration.Builder builder = new LogConfiguration.Builder();
            // 0.
            try {
                if (configuration.containsKey("tag")) {
                    String tag = configuration.getString("tag");
                    builder.tag(tag);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            // 1.指定日志级别，低于该级别的日志将不会被打印，默认为 LogLevel.ALL
            try {
                if (configuration.containsKey("logLevel")) {
                    int logLevel = configuration.getInt("logLevel");
                    builder.logLevel(logLevel);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            // 2.允许打印线程信息，默认禁止
            try {
                if (configuration.containsKey("enableThreadInfo")) {
                    boolean enableThreadInfo = configuration.getBoolean("enableThreadInfo");
                    if (enableThreadInfo) {
                        builder.enableThreadInfo();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            // 3.允许打印深度为 2 的调用栈信息，默认禁止
            try {
                if (configuration.containsKey("enableStackTrace")) {
                    int depth = configuration.getInt("enableStackTrace");
                    builder.enableStackTrace(depth);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            // 3.允许打印日志边框，默认禁止
            try {
                if (configuration.containsKey("enableBorder")) {
                    boolean enableBorder = configuration.getBoolean("enableBorder");
                    if (enableBorder) {
                        builder.enableBorder();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            // 4.日志黑名单
            try {
                if (configuration.containsKey("blacklistTags")) {
                    EsArray blacklistTags = configuration.getArray("blacklistTags");
                    if (blacklistTags != null && blacklistTags.size() > 0) {
                        List<String> tagsList = new ArrayList<>(blacklistTags.size());
                        for (int i = 0; i < blacklistTags.size(); i++) {
                            try {
                                String tag = blacklistTags.getString(i);
                                tagsList.add(tag);
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                        builder.addInterceptor(new BlacklistTagsFilterInterceptor(tagsList));
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
            LogConfiguration config = builder.build();

            //AndroidPrinter
            Printer androidPrinter = null;
            try {
                if (configuration.containsKey("androidPrinter")) {
                    EsMap androidPrinterMap = configuration.getMap("androidPrinter");
                    boolean autoSeparate = androidPrinterMap.getBoolean("autoSeparate");
                    int maxChunkSize = androidPrinterMap.getInt("maxChunkSize");
                    androidPrinter = new AndroidPrinter(autoSeparate, maxChunkSize);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            //filePrinter
            Printer filePrinter = null;
            try {
                if (configuration.containsKey("filePrinter")) {
                    EsMap filePrinterMap = configuration.getMap("filePrinter");
                    if (filePrinterMap != null) {
                        // 1.指定保存日志文件的路径
                        String folderPath = filePrinterMap.getString("folderPath");
                        if (TextUtils.isEmpty(folderPath)) {
                            return;
                        }
                        //小程序的根目录
                        String rootPath = EsProxy.get().getEsAppPath(this);
                        if (L.DEBUG) {
                            L.logD("#------程序的根路径--------->>>rootPath:" + rootPath);
                        }
                        FilePrinter.Builder b = new FilePrinter.Builder(
                                rootPath + File.separator + folderPath);

                        // 2.指定日志文件名生成器，默认为 ChangelessFileNameGenerator("log")
                        try {
                            if (filePrinterMap.containsKey("fileNameGenerator")) {
                                EsMap fileNameGenerator = filePrinterMap.getMap("fileNameGenerator");
                                int type = fileNameGenerator.getInt("type");
                                switch (type) {
                                    case 0:
                                        String fileName = fileNameGenerator.getString("fileName");
                                        b.fileNameGenerator(new ChangelessFileNameGenerator(fileName));
                                        break;
                                    case 1:
                                        b.fileNameGenerator(new DateFileNameGenerator());
                                        break;
                                    case 2:
                                        b.fileNameGenerator(new LevelFileNameGenerator());
                                        break;
                                }
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        //3.指定日志文件备份策略，默认为 FileSizeBackupStrategy(1024 * 1024)
                        try {
                            if (filePrinterMap.containsKey("backupStrategy")) {
                                EsMap backupStrategy = filePrinterMap.getMap("backupStrategy");
                                int type = backupStrategy.getInt("type");
                                switch (type) {
                                    case 0:
                                        b.backupStrategy(new NeverBackupStrategy());
                                        break;
                                    case 1:
                                        long maxSize = backupStrategy.getInt("maxSize");
                                        int maxBackupIndex = backupStrategy.getInt("maxBackupIndex");
                                        b.backupStrategy(new FileSizeBackupStrategy2(maxSize, maxBackupIndex));
                                        break;
                                }
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                        // 4.指定日志文件清除策略，默认为 NeverCleanStrategy()
                        try {
                            if (filePrinterMap.containsKey("cleanStrategy")) {
                                EsMap cleanStrategy = filePrinterMap.getMap("cleanStrategy");
                                int type = cleanStrategy.getInt("type");
                                switch (type) {
                                    case 0:
                                        long maxTimeMillis = cleanStrategy.getLong("maxTimeMillis");
                                        b.cleanStrategy(new FileLastModifiedCleanStrategy(maxTimeMillis));
                                        break;
                                    case 1:
                                        b.cleanStrategy(new NeverCleanStrategy());
                                        break;
                                }
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                        //5.指定日志平铺器，默认为 DefaultFlattener
                        try {
                            if (filePrinterMap.containsKey("flattener")) {
                                EsMap flattener = filePrinterMap.getMap("flattener");
                                int type = flattener.getInt("type");
                                switch (type) {
                                    case 0:
                                        b.flattener(new DefaultFlattener());
                                        break;
                                    case 1:
                                        b.flattener(new ClassicFlattener());
                                        break;
                                    case 2:
                                        String pattern = flattener.getString("pattern");
                                        b.flattener(new PatternFlattener(pattern));
                                        break;
                                }
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }

                        //6.指定日志写入器，默认为 SimpleWriter
                        try {
                            if (filePrinterMap.containsKey("writer")) {
                                EsMap writer = filePrinterMap.getMap("writer");
                                int type = writer.getInt("type");
                                switch (type) {
                                    case 0:
                                        b.writer(new SimpleWriter());
                                        break;
                                }
                            }
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                        filePrinter = b.build();
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            // 初始化 XLog
            // 指定日志配置，如果不指定，会默认使用 new LogConfiguration.Builder().build()
            // 添加任意多的打印器。如果没有添加任何打印器，会默认使用 AndroidPrinter(Android)/ConsolePrinter(java)
            if (filePrinter != null && androidPrinter != null) {
                ESLog.init(config, androidPrinter, filePrinter);
            } else if (filePrinter != null) {
                ESLog.init(config, filePrinter);
            } else if (androidPrinter != null) {
                ESLog.init(config, androidPrinter);
            } else {
                ESLog.init(config);
            }
        } else {
            ESLog.init();
        }
    }

    public void initLogLevel(int logLevel) {
        if (L.DEBUG) {
            L.logD("#------initLogLevel--------->>>" + logLevel);
        }
        ESLog.init(logLevel);
    }

    public void v(String tag, String log) {
        ESLog.tag(tag).v(log);
    }

    public void d(String tag, String log) {
        ESLog.tag(tag).d(log);
    }

    public void i(String tag, String log) {
        ESLog.tag(tag).i(log);
    }

    public void w(String tag, String log) {
        ESLog.tag(tag).w(log);
    }

    public void e(String tag, String log) {
        ESLog.tag(tag).e(log);
    }

    public void json(String tag, String log) {
        ESLog.tag(tag).json(log);
    }

    public void xml(String tag, String log) {
        ESLog.tag(tag).xml(log);
    }

    @Override
    public void getEsInfo(EsPromise promise) {
        EsMap map = new EsMap();
        try {
            map.pushInt(IEsInfo.ES_PROP_INFO_VERSION, EsProxy.get().getSdkVersionCode());
            map.pushDouble(IEsInfo.ES_PROP_INFO_ESKIT_VERSION, EsProxy.get().getEsKitVersionCode());
        } catch (Throwable e) {
            e.printStackTrace();
        }
        promise.resolve(map);
    }

    @Override
    public void destroy() {

    }
}
