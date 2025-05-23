package com.quicktvui.support.log.printer.file.clean;

import java.io.File;

/**
 * Decide whether the log file should be clean.
 *
 * @since 1.5.0
 */
public interface CleanStrategy {

    /**
     * Whether we should clean a specified log file.
     *
     * @param file the log file
     * @return true is we should clean the log file
     */
    boolean shouldClean(File file);
}
