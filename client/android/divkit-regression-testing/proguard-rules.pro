# snakeyaml
-keep class org.yaml.snakeyaml.** { public protected private *; }
-dontwarn org.yaml.snakeyaml.**

# Scenario
-keep class com.yandex.divkit.regression.data.Scenario { *; }
-keep class com.yandex.divkit.regression.data.Priority { *; }
-keep class com.yandex.divkit.regression.data.Platforms { *; }
