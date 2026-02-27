-allowaccessmodification
-dontskipnonpubliclibraryclasses
-dontusemixedcaseclassnames
-keepattributes *Annotation*,EnclosingMethod
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-printseeds
-renamesourcefileattribute SourceFile
-repackageclasses ''
-verbose

-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn kotlin.**

-keep class kotlin.reflect.**
-keep class kotlin.Metadata { *; }
-keep class kotlinx.**

-keep class com.yandex.div.lottie.**

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void checkExpressionValueIsNotNull(...);
    public static void checkFieldIsNotNull(...);
    public static void checkNotNull(...);
    public static void checkNotNullExpressionValue(...);
    public static void checkNotNullParameter(...);
    public static void checkParameterIsNotNull(...);
    public static void checkReturnedValueIsNotNull(...);
    public static void throwUninitializedPropertyAccessException(...);
}
