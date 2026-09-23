package com.yandex.div.gradle.multiplatform

enum class PlatformGroup {
    JVM,
    IOS;
}

enum class PlatformIdentifier(
    val id: String,
    val group: PlatformGroup
) {
    JVM("jvm", PlatformGroup.JVM),
    ANDROID("android", PlatformGroup.JVM),
    IOS_ARM_64("iosarm64", PlatformGroup.IOS),
    IOS_X_64("iosx64", PlatformGroup.IOS),
    IOS_SIMULATOR_ARM_64("iossimulatorarm64", PlatformGroup.IOS);
}
