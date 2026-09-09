# DivKit Compose Macrobenchmark

Measures the first render of `with_templates.json` and `services.json`, the same
assets used by `ComposePerformanceTest`. Each iteration starts a new process,
reads the asset and parses the card on `Dispatchers.IO`, creates a `DivContext`
on the main thread, and displays a `DivView`. There are no status messages,
artificial delays, or warm recomposition/reset-content passes.

The image loader and histogram configuration match `DivComposeBenchmarkActivity`.
Completion means the card's first draw, not completion of network image loading.

## Run

From `public/client/android`, with an unlocked physical Android device (API 29+)
connected and selected:

```bash
./gradlew :macrobenchmark:connectedMacrobenchmarkReleaseAndroidTest
```

Run just the templates case:

```bash
./gradlew :macrobenchmark:connectedMacrobenchmarkReleaseAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=com.yandex.divkit.macrobenchmark.ComposeMacrobenchmark#withTemplates
```

In Android Studio, select `macrobenchmarkRelease` for both modules and run the
test class or method. Gradle builds and installs both APKs. The target APK has
application ID `com.yandex.divkit.benchmark.macrobenchmark`, release optimization,
debug signing and `profileable` enabled. The tests use full AOT compilation and
10 cold-process iterations per card; this does not simulate an uncompiled first
install or guarantee a cold filesystem cache.

The cards live in `internal/test_data/perf_test_data` and are included by the
benchmark app's `build.internal.gradle`. These two cases require an Arcadia
checkout containing those assets.

## Results

Android Studio/Gradle output contains metric summaries. JSON results and Perfetto
traces are copied under this module's `build/outputs/connected_android_test_additional_output/`.
The HTML test report is under `build/reports/androidTests/connected/`.

| Trace metric | Measured interval |
| --- | --- |
| `Div.ReadJson` | Asset read, UTF-8 decoding and `JSONObject` construction |
| `Div.ParseDivData` | Parsing environment, templates and `DivData` construction |
| `Div.CreateContext` | Configuration and `DivContext` construction |
| `Div.Draw` | `ComposeView` creation through the traversal containing the card's first draw |
| `Div.Total` | Entire chain, including coroutine dispatch and waiting for rendering |

`Div.CompositionMs` and `Div.RenderEffectsMs` use the first-render durations from
`HistogramBridge`, matching `DivCompose.Render.Composition.Cold` and
`DivCompose.Render.Effects.Cold` in `ComposePerformanceTest`. Composition covers
the measured composable content; render effects cover the interval from its
completion to the reporter's `DisposableEffect`. These are not GPU effect timings.
The bridge writes the durations as Perfetto counters in microseconds, and the test
converts them to milliseconds. Missing counters fail the test. Both metrics are
included in the comparison report alongside trace-section timings.

`StartupTimingMetric` additionally reports startup to the initial display
(`timeToInitialDisplayMs`), including process/activity startup, unlike the custom
total. `FrameTimingMetric` reports frame timings
during the scenario (`frameOverrunMs` requires API 31+).

For comparisons, use the same physical device and compilation mode. Emulator
runs can check that the scenario works, but are not performance measurements.

See the [Android Macrobenchmark guide](https://developer.android.com/topic/performance/benchmarking/macrobenchmark-overview).

## Compare two builds

The scripts in `scripts/` require Python 3.9+, Java/Gradle configured as for the
Android project, and `adb`. No Python packages need to be installed. Run the
following commands from `macrobenchmark/scripts`, or use absolute script paths.

### 1. Build the current revision

```bash
python3 build.py --output before --label before
# Switch to the second revision yourself, then:
python3 build.py --output after --label after
```

Each directory contains `app.apk`, `build.json` and `build.log`. The manifest
records the revision, local change status, APK hash, card hashes and trace-source
hash. Local source changes are included in the build. Both revisions must have
the `macrobenchmarkRelease` target variant and identical trace instrumentation
and input cards. Build scripts never switch revisions or change your checkout.

### 2. Run current tests against both APKs

```bash
python3 run.py --before before --after after --output comparison --serial YOUR_DEVICE_SERIAL
```

This builds the test APK from the **current working tree** once, archives it and
both application APKs, and uses that same test APK for both builds. By default it
runs three `A → B → B → A` cycles: 12 complete test runs, each with the iteration
count defined in `ComposeMacrobenchmark`. Use `--cycles 1` for a shorter check.

The selected device must be a physical Android device (API 29+) with USB debugging
authorized and its screen unlocked. Use `adb devices` to find its serial, or
`--adb /path/to/adb` to select adb explicitly. Keep the phone's brightness,
charging state and network conditions consistent throughout the comparison.

Before each run, the script **uninstalls and reinstalls only the dedicated target
package** `com.yandex.divkit.benchmark.macrobenchmark`, clearing its data and
allowing older version codes to be installed. The test package
`com.yandex.divkit.macrobenchmark` is reinstalled once at the start. It leaves the
last target build installed. Other applications are not changed.

Before measuring, it waits at least 15 seconds and until battery temperature is
at most 38°C; `--cooldown-seconds`, `--max-battery-temperature`, and
`--cooldown-timeout` configure this. Battery temperature is a coarse guard, not
a measurement of CPU thermal throttling. It does not lock CPU clocks or change
device settings.

Output directories must be new to prevent overwriting measurements. After a
failure, use a new `--output` directory. `comparison.json` records progress and
errors; each `runs/NN-A` or `runs/NN-B` directory contains installation and
instrumentation logs, the benchmark JSON and Perfetto traces. Successfully pulled
artifacts remain on the computer; only this run's temporary device directory is
removed. Failed/interrupted comparisons retain completed results.

### 3. Generate the report

```bash
python3 report.py comparison/comparison.json
```

`scripts/report.template.html` is the generator's template, not a report. Run
`report.py` first; the ready-to-open HTML is written next to `comparison.json`.

Open `comparison/report.html` in a browser. It works offline and contains:

- Total-time cards for both scenarios and an A/B table for every scalar timing metric.
- A scenario selector and clickable metrics with per-iteration dots and per-run medians.
- Percentage and absolute changes, confidence intervals when enough runs exist,
  and links to original JSON files. CSV and summary JSON are also generated.

The comparison uses the median of run medians. The 95% percentile bootstrap
resamples entire runs independently for A and B, not individual iterations;
at least five completed runs per build are required for an interval. The default
practical threshold is 3% (`report.py --threshold-percent 3`): an improvement or
regression is marked only when the entire interval exceeds that threshold.
This is exploratory evidence, not a multiple-comparison-adjusted significance
test. More cycles may be needed for small effects. Incomplete comparisons are
shown with a warning and never receive an improvement/regression verdict.

The report rejects different device/OS configurations, missing metrics and
different scenarios or iteration counts. `sampledMetrics` frame distributions
are retained in the original JSON but are not aggregated in this report.

Run script checks without a device:

```bash
python3 -m unittest -v test_scripts.py
```
