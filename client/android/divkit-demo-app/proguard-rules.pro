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
