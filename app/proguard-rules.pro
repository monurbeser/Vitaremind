# ── VitaRemind ProGuard Rules ──────────────────────────────────────────────────

# Room — keep all entities and DAOs
-keep class com.vitaremind.app.data.local.entity.** { *; }
-keep interface com.vitaremind.app.data.local.dao.** { *; }
-keep class * extends androidx.room.RoomDatabase { *; }
-keepclassmembers @androidx.room.Entity class * { *; }
-keepclassmembers @androidx.room.Dao interface * { *; }

# Hilt generated classes
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }
-dontwarn dagger.hilt.**
-keepclasseswithmembernames class * {
    @dagger.hilt.android.lifecycle.HiltViewModel <init>(...);
}

# WorkManager workers
-keep class com.vitaremind.app.worker.** { *; }
-keep class * extends androidx.work.Worker { *; }
-keep class * extends androidx.work.CoroutineWorker { *; }
-keepclassmembers class * extends androidx.work.CoroutineWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}

# AdMob / Google Play Services Ads
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.ads.** { *; }
-dontwarn com.google.android.gms.ads.**

# Kotlin coroutines
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

# DataStore
-keep class androidx.datastore.** { *; }
-keepclassmembers class * implements java.io.Serializable { *; }

# Kotlin serialization (if added later)
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.**
-keepclassmembers class kotlinx.serialization.json.** { *** Companion; }

# Crash reporting — preserve line numbers
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# AndroidX Navigation
-keep class androidx.navigation.** { *; }

# Accompanist
-keep class com.google.accompanist.** { *; }

# General Android
-keepattributes *Annotation*
-dontwarn sun.misc.**
-dontwarn java.lang.invoke.**
