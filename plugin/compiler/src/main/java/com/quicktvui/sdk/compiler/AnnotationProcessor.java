package com.quicktvui.sdk.compiler;

import com.google.auto.service.AutoService;

import com.quicktvui.sdk.compiler.processor.ESKitAutoInitProcessor;
import com.quicktvui.sdk.compiler.processor.ESKitAutoRegisterProcessor;

import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

/**
 *
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({
        "com.quicktvui.sdk.annotations.ESKitAutoRegister",
        "com.quicktvui.sdk.annotations.ESKitAutoInit",
})
public class AnnotationProcessor extends AbstractProcessor {

    public ProcessingEnvironment env(){
        return processingEnv;
    }

    @Override
    public Set<String> getSupportedOptions() {
        return processingEnv.getOptions().keySet();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        new ESKitAutoRegisterProcessor(this).process(roundEnv, this);
        new ESKitAutoInitProcessor(this).process(roundEnv, this);
        return true;
    }

}
