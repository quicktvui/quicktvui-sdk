
#region sdk-base
-keep interface com.quicktvui.sdk.base.module.IEsModule{*;}
-keep interface com.quicktvui.sdk.base.component.IEsComponentView{*;}
-keep interface com.quicktvui.sdk.base.component.IEsComponent{*;}
-keep interface com.quicktvui.sdk.base.IEsInfo{*;}
-keep interface com.quicktvui.sdk.base.EsPromise{*;}
-keep interface com.quicktvui.sdk.base.IEsTraceable{*;}
-keep class * implements com.quicktvui.sdk.base.core.IEsProxy {public <fields>; public <methods>;}
-keep class * implements com.quicktvui.sdk.base.component.IEsComponent {public <fields>; public <methods>;}
-keep class * implements com.quicktvui.sdk.base.component.IEsComponentView {public <fields>; public <methods>;}
-keep class * implements com.quicktvui.sdk.base.module.IEsModule {public <fields>; public <methods>;}
-keep class com.quicktvui.sdk.base.component.EsComponentAttribute{*;}
-keep class com.quicktvui.sdk.base.args.**{*;}
-keep class com.quicktvui.sdk.base.core.**{*;}
-keep class com.quicktvui.sdk.base.EsException {*;}
-keep class org.slf4j.** {*;}
-keep class com.quicktvui.sdk.base.PromiseHolder {
    public <methods>;
}
#endregion

#region sdk-core
-keepclasseswithmembers class * {
    @com.quicktvui.sdk.core.EsStartParam <methods>;
}
-keep class com.quicktvui.sdk.core.tookit.ToolkitUseCase {public <fields>;}
-keep class com.sunrain.toolkit.**{public <fields>; public <methods>;}
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
-keep class com.quicktvui.support.ijk.base.**{*;}

#region ui


-keep class * extends com.quicktvui.support.ui.item.widget.BuilderWidget {
    <init>(...);
}

-keep class * extends com.quicktvui.support.ui.item.widget.BuilderWidget$Builder {
    <init>(...);
}

-keep class com.quicktvui.support.ui.selectseries.ESSelectSeriesController {*;}

-keep class androidx.viewpager2.widget.ViewPager2{
    *** mRecyclerView;
    *** mCurrentItem;
    *** mAccessibilityProvider;
    *** mScrollEventAdapter;
}

-keep class com.extscreen.support.viewpager2.widget.ViewPager2{
    *** mRecyclerView;
    *** mCurrentItem;
    *** mAccessibilityProvider;
    *** mScrollEventAdapter;
}

-keep class android.support.v7.widget.LinearSmoothScroller {
    *** mDecelerateInterpolator;
}

-keep class androidx.recyclerview.widget.LinearSmoothScroller{
    *** mDecelerateInterpolator;
}

-keep class androidx.viewpager2.widget.ViewPager2$PageAwareAccessibilityProvider {
    *** onSetNewCurrentItem(...);
}

-keep class com.extscreen.support.viewpager2.widget.ViewPager2$AccessibilityProvider{
    *** onSetNewCurrentItem(...);
}

-keep class com.extscreen.support.viewpager2.widget.ScrollEventAdapter{
    *** getRelativeScrollPosition(...);
    *** notifyProgrammaticScroll(...);
}

-keep public class * extends com.quicktvui.support.ui.v7.widget.RecyclerView$LayoutManager {
    public <init>(...);
}
#endregion

#endregion

#region third
-keep class com.bumptech.glide.load.model.ModelLoader$LoadData {
        com.bumptech.glide.load.data.DataFetcher fetcher;
}

-keep class com.bumptech.glide.load.data.HttpUrlFetcher {
    com.bumptech.glide.load.data.HttpUrlFetcher$HttpUrlConnectionFactory connectionFactory;
}

#region x5
-dontwarn dalvik.**
-dontwarn com.tencent.smtt.**

-keep class com.tencent.smtt.** {
    *;
}

-keep class com.tencent.tbs.** {
    *;
}

-keepattributes *JavascriptInterface*
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
#endregion

#endregion