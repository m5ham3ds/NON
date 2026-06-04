# ── WiFi Card Master Pro – ProGuard Rules (Complete) ─────────────

# ── Kotlin ────────────────────────────────────────────────────────
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepattributes AnnotationDefault
-keepclassmembers @kotlin.Metadata class * { *; }
-dontwarn kotlin.**

# ── Domain & Data Models ──────────────────────────────────────────
-keep class com.example.domain.model.** { *; }
-keep class com.example.data.local.entity.** { *; }

# ── Room ──────────────────────────────────────────────────────────
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *
-keep @androidx.room.Dao interface *
-keepclassmembers class * extends androidx.room.RoomDatabase {
    abstract *;
}
-dontwarn androidx.room.paging.**

# ── Koin ──────────────────────────────────────────────────────────
-keep class org.koin.** { *; }
-keepnames class * extends org.koin.core.module.Module
-dontwarn org.koin.**

# ── Coroutines ────────────────────────────────────────────────────
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** { volatile <fields>; }
-dontwarn kotlinx.coroutines.**

# ── Kotlinx Serialization ─────────────────────────────────────────
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }
-keepclasseswithmembers class **$$serializer { *; }
-keep @kotlinx.serialization.Serializable class * { *; }

# ── Coil ──────────────────────────────────────────────────────────
-keep class coil.** { *; }
-dontwarn coil.**

# ── Lottie ────────────────────────────────────────────────────────
-keep class com.airbnb.lottie.** { *; }
-dontwarn com.airbnb.lottie.**

# ── OkHttp ────────────────────────────────────────────────────────
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# ── Timber ────────────────────────────────────────────────────────
-dontwarn org.jetbrains.annotations.**

# ── WebView JavaScript Interface ──────────────────────────────────
-keepclassmembers class com.example.service.TestService$* {
    @android.webkit.JavascriptInterface public *;
}

# ── Security Crypto ───────────────────────────────────────────────
-keep class androidx.security.crypto.** { *; }

# ── Navigation Component ──────────────────────────────────────────
-keep class * extends androidx.navigation.NavArgs { *; }
