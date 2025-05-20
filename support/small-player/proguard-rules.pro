-dontoptimize
-dontusemixedcaseclassnames
-verbose
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-keeppackagenames

-keep public class * extends android.app.Activity {
	public <fields>;
	public <methods>;
}
-keep public class * extends android.app.Application {
	public <fields>;
	public <methods>;
}

-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclasseswithmembers class * {
	public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
	public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepattributes *Annotation*,Signature,InnerClasses

# <!--改成这样,因为发现部分SDK中没有被java代码显示调用过,SO文件中调用的native方法会被混淆掉-->

-keepclasseswithmembers class * {
	native <methods>;
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# utils
-keep class com.sunrain.toolkit.utils**{public <methods>; public <fields>;}

# support
-keep class eskit.sdk.support**{*;}
-keep class * implements eskit.sdk.support.module.IEsModule {public <methods>;}
-keep class * implements eskit.sdk.support.component.IEsComponent {public <methods>;}
-keep class * implements eskit.sdk.support.component.IEsComponentView {public <methods>;}

#core
-keep class eskit.sdk.core.entity.**{*;}

-keep class eskit.sdk.core.IEsManager {*;}
-keep class eskit.sdk.core.EsData {*;}
-keep class eskit.sdk.core.internal.EsContext {public <methods>;}
-keep class * implements eskit.sdk.core.IEsManager {public <methods>;}

-keep class eskit.sdk.support.small.player.view.**{*;}
################################################################################################################