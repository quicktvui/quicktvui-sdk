#不进行优化，建议使用此选项，因为根据proguard-android-optimize.txt中的描述，
#优化可能会造成一些潜在风险，不能保证在所有版本的Dalvik上都正常运行。
-dontoptimize
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-optimizationpasses 5
#优化时允许访问并修改有修饰符的类和类的成员
#-allowaccessmodification
#不进行预校验。这个预校验是作用在Java平台上的，Android平台上不需要这项功能，去掉之后还可以加快混淆速度。
-dontpreverify
#混淆时不会产生形形色色的类名
-dontusemixedcaseclassnames
#不跳过library中的非public的类
-dontskipnonpubliclibraryclasses
#打印混淆的详细信息
-verbose
#包名不混淆
#-keeppackagenames
#参数名称不混淆
-keepparameternames

-packageobfuscationdictionary dict5.txt

-keepattributes *Annotation*,Signature,InnerClasses

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

# For enumeration classes, see http://proguard.sourceforge.net/manual/examples.html#enumerations
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}

# region sdk-base
-keep interface com.quicktvui.sdk.base.module.IEsModule{*;}
-keep class com.quicktvui.sdk.base.EsException {*;}
-keep interface com.quicktvui.sdk.base.IEsInfo{*;}
-keep interface com.quicktvui.sdk.base.EsPromise{*;}
-keep interface com.quicktvui.sdk.base.IEsTraceable{*;}
-keep class com.quicktvui.sdk.base.args.**{*;}
-keep class com.quicktvui.sdk.base.core.**{*;}
-keep class com.quicktvui.sdk.base.EsSimplePromise{*;}
-keep class * implements com.quicktvui.sdk.base.module.IEsModule {public <methods>;}
-keep interface com.quicktvui.sdk.base.component.IEsComponent{*;}
-keep class * implements com.quicktvui.sdk.base.component.IEsComponent {public <methods>;}
-keep interface com.quicktvui.sdk.base.component.IEsComponentView{*;}
-keep class * implements com.quicktvui.sdk.base.component.IEsComponentView {public <methods>;}
-keep class com.quicktvui.sdk.base.core.EsProxy {
    public <fields>;
    public <methods>;
}
# endregion

#region sdk-core
-keep class com.quicktvui.sdk.core.IEsManager {
    public <methods>;
}

-keep class com.quicktvui.sdk.core.EsManager{
    public <methods>;
}

-keep class * implements com.quicktvui.sdk.core.EsKitInitCallback{
    public <methods>;
}

-keep class * implements com.quicktvui.sdk.core.callback.EsAppLifeCallback{
    public <methods>;
}

-keep class com.quicktvui.sdk.core.InitConfig{
    public <fields>;
    public <methods>;
}

-keep class com.quicktvui.sdk.core.EsData{*;}
-keep class com.quicktvui.sdk.core.EsStartParam{*;}

-keep class com.quicktvui.sdk.core.entity.**{*;}
-keep class eskit.sdk.core.update.entity.**{*;}

-keep class com.quicktvui.sdk.core.EsKitInitCallback{*;}
-keep class com.quicktvui.sdk.core.callback.EsAppLifeCallback{*;}
-keep class com.quicktvui.sdk.core.internal.IEsAppLoadHandler{*;}
-keep class com.quicktvui.sdk.core.internal.IEsAppLoadCallback{*;}

-keep class com.quicktvui.sdk.core.EsKitStatus {*;}
-keep class com.quicktvui.sdk.core.internal.EsContext {
   *** get();
   boolean isInitSuccess();
   boolean isInitNotOk();
   public *** get*(...);
   *** setCustomServer(...);
   *** setRemoteEventCallback(...);
   *** setImageLoader(...);
   *** setDeviceId(...);
   *** isRelieveImageSize();
   *** setRelieveImageSize(...);
}
-keep class com.quicktvui.sdk.core.ext.loadproxy.IEsRpkLoadProxy{
    public <methods>;
}
-keep class com.quicktvui.sdk.core.internal.EsComponentManager{
    public <methods>;
}
-keep class com.quicktvui.sdk.core.adapter.EsImageLoaderAdapter {
    public <methods>;
}
-keep class com.quicktvui.sdk.core.protocol.EsProtocolDispatcher{
    public <methods>;
}
-keep class com.quicktvui.sdk.core.udp.EsUdpServer{
    public <methods>;
}
-keep class com.quicktvui.sdk.core.utils.MapperUtils {
    *** intent2JsonObject(...);
    *** uri2JsonObject(...);
}

-keep class com.quicktvui.sdk.core.utils.Am {
    public <methods>;
}
-keep class com.quicktvui.sdk.core.internal.EsViewManager{
    *** get();
    *** getEsAppData();
    public <methods>;
    public <fields>;
}
-keep class com.quicktvui.sdk.core.utils.CommonUtils {
    public <methods>;
}

-keep class com.quicktvui.sdk.core.utils.HttpRequestUtils {
    public <methods>;
}

-keep class com.quicktvui.sdk.core.utils.ESExecutors {
    public <methods>;
}

-keep class com.quicktvui.sdk.core.internal.Constants {
    public <fields>;
}
-keep class com.quicktvui.sdk.core.internal.Constants$* {
    public <fields>;
}
-keep class com.quicktvui.sdk.core.protocol.EsProtocolDispatcher{
    public <fields>;
}
-keep class com.quicktvui.sdk.core.protocol.Protocol_2{
    public <methods>;
}
-keep class com.quicktvui.sdk.core.internal.BaseHandlerThread{
    public <methods>;
    protected <methods>;
}

-keep class com.quicktvui.sdk.core.tookit.ToolkitUseCase {public <fields>;}
-keep class com.quicktvui.sdk.core.utils.FullScreenUtils{ public <methods>;}
-keep class com.quicktvui.sdk.core.utils.PluginUtils {
    public <fields>;
}
-keep class com.quicktvui.sdk.core.jsview.JsSlotViewManager {
    public ***get();
    public ***attachToActivity(...);
    public ***createJSView(...);
    public ***deleteJSView(...);
    public ***dispatchKeyEvent(...);
    public ***onBackPressed(...);
    public ***sendEvent(...);
    public ***sendCustomEvent(...);
    public ***detachFromActivity(...);
    public ***setEnableMouse(...);
}

-keep class com.quicktvui.sdk.core.jsview.JsSlotView {
    public <methods>;
}
-keep class com.quicktvui.sdk.core.interceptor.LaunchEsPageInterceptor{*;}
-keep class com.quicktvui.sdk.core.utils.EskitLazyInitHelper{public <methods>;}
#endregion

#region sdk-hippy
-keep class * extends com.tencent.mtt.hippy.modules.nativemodules.HippyNativeModuleBase{
    public <methods>;
}

-keep class * extends com.tencent.mtt.hippy.uimanager.HippyViewController{
    public <methods>;
    protected <methods>;
}
#endregion

#region support
-keep class com.quicktvui.support.**{public <fields>; public <methods>;}
-keep class com.sunrain.toolkit.**{public <fields>; public <methods>;}

-keep class com.quicktvui.support.ijk.base.**{*;}

#endregion