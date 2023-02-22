package com.yandex.divkit.perftests

import android.os.Bundle
import androidx.core.os.bundleOf

const val PACKAGE_NAME = "com.yandex.divkit.demo"
const val DIV_BENCHMARK_ACTIVITY = "$PACKAGE_NAME.benchmark.Div2BenchmarkActivity"
const val DIV_STATE_BENCHMARK_ACTIVITY = "$PACKAGE_NAME.benchmark.Div2StateBenchmarkActivity"
const val DIV_FEED_BENCHMARK_ACTIVITY = "$PACKAGE_NAME.benchmark.Div2FeedBenchmarkActivity"
const val DIV_STORAGE_BENCHMARK_ACTIVITY = "$PACKAGE_NAME.benchmark.DivStorageBenchmarkActivity"

private const val EXTRA_ASSET_NAME = "asset_name"
private const val EXTRA_ASSET_NAMES = "asset_names"
private const val EXTRA_STATE_PATHS = "state_paths"

fun divBenchmarkActivityExtras(assetName: String): Bundle {
    return bundleOf(
        EXTRA_ASSET_NAME to assetName,
    )
}

fun divStateBenchmarkActivityExtras(assetName: String, statePaths: Array<String>): Bundle {
    return bundleOf(
        EXTRA_ASSET_NAME to assetName,
        EXTRA_STATE_PATHS to statePaths
    )
}

fun divFeedBenchmarkActivityExtras(assetNames: Array<String>): Bundle {
    return bundleOf(
        EXTRA_ASSET_NAMES to assetNames,
    )
}

fun divStorageBenchmarkActivityExtras(assetNames: Array<String>): Bundle {
    return bundleOf(
        EXTRA_ASSET_NAMES to assetNames,
    )
}
