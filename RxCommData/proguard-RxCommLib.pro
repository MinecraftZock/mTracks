# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/bpflug/android-sdk-macosx/tools/proguard/proguard-android.txt
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

# We have to preserve our data objects, otherwise they are not properly instanciated by retrofit
-keep class info.mx.comlib.retrofit.service.data.** { *; }