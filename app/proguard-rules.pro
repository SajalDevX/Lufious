# 📍 Jetpack Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# 📍 Kotlin metadata
-keep class kotlin.Metadata { *; }

# 📍 Annotations & attributes
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

# 📍 AndroidX lifecycle & ViewModel
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.lifecycle.**

# 📍 Your ViewModels
-keep class ai.lufious.app.**ViewModel { *; }

# 📍 Model classes (if using Gson/Moshi/serialization)
-keep class ai.lufious.app.model.** { *; }

# 📍 OkHttp & Retrofit
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn retrofit2.**

# 📍 javax.lang.model and javax.annotation (fixes R8 missing classes)
-keep class javax.annotation.** { *; }
-dontwarn javax.annotation.**

-keep class javax.lang.model.** { *; }
-dontwarn javax.lang.model.**

-keep class javax.lang.model.util.** { *; }
-dontwarn javax.lang.model.util.**

-keep class javax.lang.model.element.** { *; }
-dontwarn javax.lang.model.element.**

-keep class javax.lang.model.type.** { *; }
-dontwarn javax.lang.model.type.**

-keep class javax.annotation.processing.** { *; }
-dontwarn javax.annotation.processing.**

# 📍 java.sql (sometimes Room drivers use this)
-keep class java.sql.** { *; }
-dontwarn java.sql.**

# 📍 Room (safe fallback)
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# 📍 AutoValue & JavaPoet (used by Room and others internally)
-keep class com.google.auto.** { *; }
-dontwarn com.google.auto.**

-keep class com.squareup.javapoet.** { *; }
-dontwarn com.squareup.javapoet.**

# 📍 Optional: Keep JS interface for WebView if you use it
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# 📍 Optional: Hide original file names
#-renamesourcefileattribute SourceFile
# Fix Guava missing annotations
-keep class com.google.j2objc.annotations.** { *; }
-dontwarn com.google.j2objc.annotations.**

# Fix javax.tools used by kotlinpoet
-keep class javax.tools.** { *; }
-dontwarn javax.tools.**
