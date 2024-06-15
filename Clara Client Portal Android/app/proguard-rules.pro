# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep inherited services.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# R8 full mode strips generic signatures from return types if not kept.
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# With R8 full mode generic signatures are stripped for classes that are not kept.
-keep,allowobfuscation,allowshrinking class retrofit2.Response

# Hilt
-keep class dagger.hilt.android.** { *; }
-keep class javax.annotation.** { *; }
-keep class javax.inject.** { *; }
-keep class javax.inject.** { *; }
-keepclasseswithmembers class * {@dagger.hilt.InstallIn *;}
# Keep class names of Hilt injected ViewModels since their name are used as a multibinding map key.
-keepnames @dagger.hilt.android.lifecycle.HiltViewModel class * extends androidx.lifecycle.ViewModel


# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}
-keep class * implements com.bumptech.glide.load.model.ModelLoaderFactory {
  <init>(...);
}
-keep class * extends com.bumptech.glide.module.AppGlideModule {
  <init>(...);
}
-keep public class * implements com.bumptech.glide.module.GlideModule {
  public void applyOptions(android.content.Context, com.bumptech.glide.GlideBuilder);
  public void registerComponents(android.content.Context, com.bumptech.glide.Glide, com.bumptech.glide.Registry);
}
# Annotation for Glide's annotations
-dontwarn com.bumptech.glide.annotation.**

# Support v4 and v7 Annotations
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.** { *; }
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

# OkHttp3
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-keepattributes Signature

-keep class android.support.design.widget.** { *; }
-keep interface android.support.design.widget.** { *; }
-dontwarn android.support.design.**

-keepattributes *Annotation*,Signature
-keep class com.clara.clientportal.model.** { *; }
-keepclassmembers class com.clara.clientportal.model.** { *; }

-dontwarn android.databinding.**
-keep class android.databinding.** { *; }

-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}

# Gson
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.google.gson.** { *; }
-keepattributes Signature
-keepattributes *Annotation*
-keepnames class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# Android Architecture Lifecycle
-keep public class * extends androidx.lifecycle.LifecycleObserver
-keepclassmembers class * implements androidx.lifecycle.LifecycleObserver {
    <methods>;
}
-keepclassmembers class * {
    @androidx.lifecycle.OnLifecycleEvent *;
}
-keep class androidx.lifecycle.Lifecycle$* { *; }
-keepclassmembers class androidx.lifecycle.Lifecycle$* { *; }
-keep class androidx.lifecycle.LifecycleRegistry { *; }
-keepclassmembers class androidx.lifecycle.LifecycleRegistry { *; }
-keep public class * extends android.app.Activity {
    public <init>(android.content.Context);
}
-keepclassmembers class * {
    public <init>(android.util.AttributeSet);
    public void set*(...);
}
-keepclassmembers class * extends android.app.Activity {
    public <init>();
    void set*(***);
    *** get*();
}