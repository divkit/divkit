package com.yandex.divkit.perftests

import com.yandex.perftests.runner.PerfTestUtils

fun PerfTestUtils.resetAndCompile(packageName: String) {
    reset(packageName)
    device.executeShellCommand("cmd package compile -f -m speed $packageName")
}

fun PerfTestUtils.reset(packageName: String) {
    device.executeShellCommand("cmd package compile --reset $packageName")
}
