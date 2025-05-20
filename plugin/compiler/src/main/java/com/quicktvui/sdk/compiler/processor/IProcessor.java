package com.quicktvui.sdk.compiler.processor;

import javax.annotation.processing.RoundEnvironment;

import com.quicktvui.sdk.compiler.AnnotationProcessor;

/**
 *
 */
public interface IProcessor {
    void process(RoundEnvironment roundEnv, AnnotationProcessor processor);
}
