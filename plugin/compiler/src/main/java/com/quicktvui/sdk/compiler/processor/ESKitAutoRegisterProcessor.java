package com.quicktvui.sdk.compiler.processor;

import static com.quicktvui.sdk.annotations.Constants.AUTO_REGISTER_CLASS_METHOD;
import static com.quicktvui.sdk.annotations.Constants.AUTO_REGISTER_CLASS_PKG;
import static com.quicktvui.sdk.annotations.Constants.AUTO_REGISTER_CLASS_SUFFIX;

import com.quicktvui.sdk.annotations.ESKitAutoRegister;
import com.quicktvui.sdk.compiler.AnnotationProcessor;
import com.quicktvui.sdk.compiler.utils.Utils;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

/**
 *
 */
public class ESKitAutoRegisterProcessor extends AbstractProcessor {

    private static final String REG_CLASS_PKG = "com.quicktvui.sdk.base.core";
    private static final String REG_CLASS_NAME = "EsProxy";

    private static final String TYPE_MODULE = "com.quicktvui.sdk.base.module.IEsModule";
    private static final String TYPE_COMPONENT = "com.quicktvui.sdk.base.component.IEsComponent";

    private static final String TYPE_HP_CONTROLLER = "com.tencent.mtt.hippy.uimanager.HippyViewController";
    private static final String TYPE_HP_MODULE = "com.tencent.mtt.hippy.modules.nativemodules.HippyNativeModuleBase";

    private ClassName mKeepClassName;

    public ESKitAutoRegisterProcessor(AnnotationProcessor processor) {
        super(processor);
    }

    @Override
    public void process(RoundEnvironment roundEnv, AnnotationProcessor processor) {
        Class<? extends Annotation> findAnnotationClass = ESKitAutoRegister.class;
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(findAnnotationClass);
        if (elements == null || elements.size() == 0) return;

        String modulePath = getModulePath((Element) elements.toArray()[0]);
        String classPrefix = Utils.generateMethodName(modulePath, 6);
        String moduleName = getSafeModuleName(modulePath);

        List<String> moduleList = new ArrayList<>();
        List<String> componentList = new ArrayList<>();
        List<String> hpControllerList = new ArrayList<>();
        List<String> hpModuleList = new ArrayList<>();

        for (Element element : elements) {
            logD("@fire " + element);
            if (isType(element, TYPE_MODULE)) {
                moduleList.add(element.toString());
            } else if (isType(element, TYPE_COMPONENT)) {
                componentList.add(element.toString());
            } else if (isType(element, TYPE_HP_CONTROLLER)) {
                hpControllerList.add(element.toString());
            } else if (isType(element, TYPE_HP_MODULE)) {
                hpModuleList.add(element.toString());
            } else {
                logE("@" + (findAnnotationClass.getSimpleName()) + "修饰了不支持的类型：" + element);
            }
        }

        if (mKeepClassName == null) {
            mKeepClassName = getKeepClassName();
        }

        String className = moduleName + "_" + classPrefix + AUTO_REGISTER_CLASS_SUFFIX;

        TypeSpec.Builder classBuilder = TypeSpec.classBuilder(className).addModifiers(Modifier.FINAL)
                .addAnnotation(mKeepClassName);

        MethodSpec.Builder methodBuild = MethodSpec.methodBuilder(AUTO_REGISTER_CLASS_METHOD)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL);
        methodBuild.addStatement("$T.logIF($S)", ClassName.bestGuess("com.sunrain.toolkit.utils.log.L"), className + " register");

        generateRegisterMethodCode(methodBuild, moduleList, "registerModule");
        generateRegisterMethodCode(methodBuild, componentList, "registerComponent");

        generateHpRegisterMethodCode(methodBuild, hpControllerList, hpModuleList);

        classBuilder.addMethod(methodBuild.build());

        try {
            JavaFile.builder(AUTO_REGISTER_CLASS_PKG, classBuilder.build()).build().writeTo(getFiler());
        } catch (Exception e) {
            logE(Utils.getStackTraceString(e));
        }
    }

    private void generateHpRegisterMethodCode(MethodSpec.Builder methodBuild, List<String> hpControllerList, List<String> hpModuleList) {
        if (hpControllerList.size() == 0 && hpModuleList.size() == 0) return;
        ClassName hippyAPIProvider = ClassName.get("com.tencent.mtt.hippy", "HippyAPIProvider");
        ClassName hippyNativeModuleBase = ClassName.get("com.tencent.mtt.hippy.modules.nativemodules", "HippyNativeModuleBase");
        ClassName hippyViewController = ClassName.get("com.tencent.mtt.hippy.uimanager", "HippyViewController");
        ClassName hippyEngineContext = ClassName.get("com.tencent.mtt.hippy", "HippyEngineContext");
        ClassName hippyJavaScriptModule = ClassName.get("com.tencent.mtt.hippy.modules.javascriptmodules", "HippyJavaScriptModule");
        ClassName hippyProvider = ClassName.get("com.tencent.mtt.hippy.common", "Provider");

        methodBuild.addCode("$T apiProvider = new $T(){",
                hippyAPIProvider, hippyAPIProvider);

        methodBuild.addCode("@Override\n"
                        + "public $T getControllers(){",
                ParameterizedTypeName.get(ClassName.get(List.class),
                        ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(hippyViewController)))
        );

        if(hpControllerList.size() == 0) {
            methodBuild.addStatement("return null;}");
        }else {
            methodBuild.addCode("$T controllerList = new $T();",
                    ParameterizedTypeName.get(ClassName.get(List.class),
                            ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(hippyViewController))),
                    ClassName.get(ArrayList.class));

            for (String controller : hpControllerList) {
                methodBuild.addStatement("controllerList.add($T.class)", ClassName.bestGuess(controller));
            }

            methodBuild.addStatement("return controllerList;}");
        }

        methodBuild.addCode("@Override\n"
                        + "public $T getNativeModules($T context){",
                ParameterizedTypeName.get(ClassName.get(Map.class),
                        ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(hippyNativeModuleBase)),
                        ParameterizedTypeName.get(hippyProvider, WildcardTypeName.subtypeOf(hippyNativeModuleBase))
                ),
                hippyEngineContext
        );

        if(hpModuleList.size() == 0) {
            methodBuild.addStatement("return null;}");
        }else {
            methodBuild.addCode("$T moduleMap = new $T();",
                    ParameterizedTypeName.get(ClassName.get(Map.class),
                            ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(hippyNativeModuleBase)),
                            ParameterizedTypeName.get(hippyProvider, WildcardTypeName.subtypeOf(hippyNativeModuleBase))
                    ),
                    ClassName.get(HashMap.class));
            for (String module : hpModuleList) {
                methodBuild.addStatement("moduleMap.put($T.class, ($T)()-> new $T(context))",
                        ClassName.bestGuess(module),
                        hippyProvider,
                        ClassName.bestGuess(module));
            }
            methodBuild.addStatement("return moduleMap;}");
        }

        methodBuild.addCode("@Override\n"
                        + "public $T getJavaScriptModules(){return null;}",
                ParameterizedTypeName.get(ClassName.get(List.class),
                        ParameterizedTypeName.get(ClassName.get(Class.class), WildcardTypeName.subtypeOf(hippyJavaScriptModule)))
        );

        methodBuild.addStatement("}");

        methodBuild.addCode("$T.get().$L(apiProvider);", ClassName.get(REG_CLASS_PKG, REG_CLASS_NAME), "registerApiProvider");

    }

    private boolean isType(Element element, String checkName) {
        TypeMirror currentType = element.asType();
        if (currentType != null && currentType.getKind() != TypeKind.NONE) {
            // 检测类本身
            if (TypeName.get(currentType).toString().contains(checkName)) {
                return true;
            }
            // 检测父类
            Types typeUtils = getTypeUtils();
            List<? extends TypeMirror> supertypes = typeUtils.directSupertypes(currentType);
            if (!supertypes.isEmpty()) {
                for (TypeMirror supertype : supertypes) {
                    if (isType(typeUtils.asElement(supertype), checkName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void generateRegisterMethodCode(MethodSpec.Builder methodBuild, List<String> classList, String callMethodName) {
        if (classList.size() > 0) {
            methodBuild.addCode("$T.get().$L(", ClassName.get(REG_CLASS_PKG, REG_CLASS_NAME), callMethodName);
            int index = 0;
            for (String className : classList) {
                if (index != classList.size() - 1) {
                    methodBuild.addCode("$L.class,", className);
                } else {
                    methodBuild.addCode("$L.class", className);
                }
                index++;
            }
            methodBuild.addStatement(")");
        }
    }
}
