# Keep line numbers for crash reports
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Room Database — entities and DAOs use reflection at runtime
-keep class app.krafted.prizewheel.data.** { *; }

# Kotlin coroutines internals
-keepclassmembernames class kotlinx.coroutines.** {
    volatile <fields>;
}

# Enum values() and valueOf() — used by WheelSegment.entries
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
