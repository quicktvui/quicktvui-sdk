/*
 * Copyright 2015 Elvis Hew
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

package com.quicktvui.support.log.printer.file.backup;

import java.io.File;

/**
 * Limit the file size of a max length.
 *
 * @deprecated use {@link FileSizeBackupStrategy2} instead, since 1.9.0.
 * A {@link FileSizeBackupStrategy2} allows you to define the max number of backup files
 */
@Deprecated
public class FileSizeBackupStrategy implements BackupStrategy {

    private long maxSize;

    /**
     * Constructor.
     *
     * @param maxSize the max size the file can reach
     */
    public FileSizeBackupStrategy(long maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    public boolean shouldBackup(File file) {
        return file.length() > maxSize;
    }
}
