# Consumer ProGuard rules for RxCommLib
# These rules are applied to projects that consume this library

-keepattributes SourceFile,LineNumberTable
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions

# For native methods
-keepclasseswithmembernames class * {
    native <methods>;
}

# For enumeration classes
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

# Preserve R
-keepclassmembers class **.R$* {
    public static <fields>;
}

-dontwarn android.support.**

# Preserve all fundamental application classes
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# Preserve all View implementations
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public <init>(android.content.Context, android.util.AttributeSet, int, int);
    public void set*(...);
}

# Preserve custom views
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int, int);
}

-keep public class rx.** {*;}

# Preserve data objects for retrofit
-keep class info.mx.comlib.data.** { *; }
-keep class info.mx.tracks.rest.google.** { *; }
-keep class info.mx.tracks.rest.model.** { *; }
-keep class info.mx.comlib.retrofit.service.data.** { *; }

### OkHttp ###
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

### RxJava ###
-dontwarn rx.**

### Retrofit ###
-dontnote retrofit2.Platform
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

### Schema Converter ###
-dontwarn java.io.File
-dontwarn java.nio.file.**
