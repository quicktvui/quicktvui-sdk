/*
 * Copyright 2016 Elvis Hew
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.quicktvui.support.log.internal;

import com.quicktvui.support.log.formatter.border.BorderFormatter;
import com.quicktvui.support.log.formatter.border.DefaultBorderFormatter;
import com.quicktvui.support.log.formatter.message.json.DefaultJsonFormatter;
import com.quicktvui.support.log.formatter.message.json.JsonFormatter;
import com.quicktvui.support.log.formatter.message.xml.DefaultXmlFormatter;
import com.quicktvui.support.log.formatter.message.xml.XmlFormatter;
import com.quicktvui.support.log.formatter.stacktrace.DefaultStackTraceFormatter;
import com.quicktvui.support.log.formatter.stacktrace.StackTraceFormatter;
import com.quicktvui.support.log.formatter.thread.DefaultThreadFormatter;
import com.quicktvui.support.log.formatter.thread.ThreadFormatter;
import com.quicktvui.support.log.printer.file.FilePrinter;
import com.quicktvui.support.log.printer.file.backup.BackupStrategy2;
import com.quicktvui.support.log.printer.file.backup.FileSizeBackupStrategy;
import com.quicktvui.support.log.printer.file.clean.CleanStrategy;
import com.quicktvui.support.log.printer.file.clean.NeverCleanStrategy;
import com.quicktvui.support.log.printer.file.naming.ChangelessFileNameGenerator;
import com.quicktvui.support.log.printer.file.naming.FileNameGenerator;
import com.quicktvui.support.log.printer.file.writer.SimpleWriter;
import com.quicktvui.support.log.printer.file.writer.Writer;
import com.quicktvui.support.log.flattener.DefaultFlattener;
import com.quicktvui.support.log.flattener.Flattener;
import com.quicktvui.support.log.flattener.Flattener2;
import com.quicktvui.support.log.formatter.message.object.ObjectFormatter;
import com.quicktvui.support.log.formatter.message.throwable.DefaultThrowableFormatter;
import com.quicktvui.support.log.formatter.message.throwable.ThrowableFormatter;
import com.quicktvui.support.log.printer.Printer;
import com.quicktvui.support.log.internal.printer.file.backup.BackupStrategyWrapper;

import java.util.Map;

/**
 * Factory for providing default implementation.
 */
public class DefaultsFactory {

    private static final String DEFAULT_LOG_FILE_NAME = "log";

    private static final long DEFAULT_LOG_FILE_MAX_SIZE = 1024 * 1024; // 1M bytes;

    /**
     * Create the default JSON formatter.
     */
    public static JsonFormatter createJsonFormatter() {
        return new DefaultJsonFormatter();
    }

    /**
     * Create the default XML formatter.
     */
    public static XmlFormatter createXmlFormatter() {
        return new DefaultXmlFormatter();
    }

    /**
     * Create the default throwable formatter.
     */
    public static ThrowableFormatter createThrowableFormatter() {
        return new DefaultThrowableFormatter();
    }

    /**
     * Create the default thread formatter.
     */
    public static ThreadFormatter createThreadFormatter() {
        return new DefaultThreadFormatter();
    }

    /**
     * Create the default stack trace formatter.
     */
    public static StackTraceFormatter createStackTraceFormatter() {
        return new DefaultStackTraceFormatter();
    }

    /**
     * Create the default border formatter.
     */
    public static BorderFormatter createBorderFormatter() {
        return new DefaultBorderFormatter();
    }

    /**
     * Create the default {@link Flattener}.
     */
    public static Flattener createFlattener() {
        return new DefaultFlattener();
    }

    /**
     * Create the default {@link Flattener2}.
     */
    public static Flattener2 createFlattener2() {
        return new DefaultFlattener();
    }

    /**
     * Create the default printer.
     */
    public static Printer createPrinter() {
        return Platform.get().defaultPrinter();
    }

    /**
     * Create the default file name generator for {@link FilePrinter}.
     */
    public static FileNameGenerator createFileNameGenerator() {
        return new ChangelessFileNameGenerator(DEFAULT_LOG_FILE_NAME);
    }

    /**
     * Create the default backup strategy for {@link FilePrinter}.
     */
    public static BackupStrategy2 createBackupStrategy() {
        return new BackupStrategyWrapper(new FileSizeBackupStrategy(DEFAULT_LOG_FILE_MAX_SIZE));
    }

    /**
     * Create the default clean strategy for {@link FilePrinter}.
     */
    public static CleanStrategy createCleanStrategy() {
        return new NeverCleanStrategy();
    }

    /**
     * Create the default writer for {@link FilePrinter}.
     */
    public static Writer createWriter() {
        return new SimpleWriter();
    }

    /**
     * Get the builtin object formatters.
     *
     * @return the builtin object formatters
     */
    public static Map<Class<?>, ObjectFormatter<?>> builtinObjectFormatters() {
        return Platform.get().builtinObjectFormatters();
    }
}
