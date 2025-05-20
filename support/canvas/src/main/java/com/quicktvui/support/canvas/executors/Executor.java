/*
 * Copyright (c) 2021, the hapjs-platform Project Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package com.quicktvui.support.canvas.executors;

import java.util.concurrent.Callable;

public interface Executor {
    void execute(Runnable runnable);

    <T> Future<T> submit(Callable<T> task);
}
