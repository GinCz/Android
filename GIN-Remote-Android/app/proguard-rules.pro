# =============================================================================
# GIN-Remote R8 ProGuard Obfuscation Rules
# =============================================================================

-keep class com.gincz.remote.MainActivity { *; }
-keep class com.gincz.remote.RemoteForegroundService { *; }
-keep class com.gincz.remote.ScreenCaptureService { *; }
-keep class com.gincz.remote.RemoteAccessibilityService { *; }
-keep class com.gincz.remote.ScreenCaptureManager { *; }
-keep class com.gincz.remote.UpdateManager { *; }

# Keep Android Support and ViewBinding
-keep class androidx.** { *; }
-keep interface androidx.** { *; }
-dontwarn androidx.**

# Keep Kotlin Coroutines
-keepnames class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Obfuscation optimizations
-allowaccessmodification
-repackageclasses 'com.gincz.remote.s'
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
