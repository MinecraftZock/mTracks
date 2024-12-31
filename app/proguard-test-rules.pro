# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/hannes/Development/adt-bundle-mac-x86_64-20130522/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
# proguard-test.pro:
#for easier debug if proguard fails
-keepattributes SourceFile,LineNumberTable

# removes Warning: library class android.graphics.drawable.Drawable depends on program class org.xmlpull.v1.XmlPullParser
-dontwarn org.xmlpull.v1.**
# Warning: androidx.test.espresso.core.internal.deps.guava.cache.Striped64$Cell: can't find referenced class sun.misc.Unsafe
-keep class sun.misc.** { *; }
-dontwarn sun.misc.**xmlpull.v1.**

-dontwarn com.google.common.**
-dontwarn com.google.errorprone.annotations.**
