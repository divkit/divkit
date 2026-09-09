#!/usr/bin/env python3
"""Run current Macrobenchmark tests against two archived builds in ABBA order."""

import argparse
import json
from pathlib import Path
import re
import shlex
import shutil
import time
import uuid

from common import (APP_PACKAGE, MODULE, TEST_PACKAGE, VARIANT, adb_path, built_apk,
                    command, gradle, main_guard, now, revision, sha256, tree_hash, write_json)


def schedule(cycles):
    return [group for _ in range(cycles) for group in ("A", "B", "B", "A")]


def load_build(folder):
    data = json.loads((folder / "build.json").read_text())
    if data.get("schemaVersion") != 1 or data.get("applicationId") != APP_PACKAGE or data.get("variant") != VARIANT:
        raise ValueError(f"Not a supported Macrobenchmark build: {folder}")
    if data.get("apkSha256") != sha256(folder / "app.apk"):
        raise ValueError(f"APK hash mismatch: {folder}")
    return data


def compatible_builds(a, b):
    for key in ("assets", "traceSourceSha256", "traceMetrics"):
        if not a.get(key) or a[key] != b.get(key):
            raise ValueError(f"Builds have different {key}; use identical cards and trace instrumentation.")


def instrumentation_succeeded(output):
    result = re.search(r"^OK \((\d+) tests?\)", output, re.MULTILINE)
    return bool(result and int(result[1]) > 0 and not any(
        marker in output for marker in ("FAILURES!!!", "INSTRUMENTATION_FAILED", "shortMsg=")))


class Device:
    def __init__(self, adb, serial):
        self.prefix = [adb, "-s", serial]

    def run(self, *args, **kwargs):
        return command(self.prefix + list(args), **kwargs)

    def shell(self, *args, **kwargs):
        # adb shell joins its arguments; quote them for the remote shell too.
        return self.run("shell", shlex.join([str(arg) for arg in args]), **kwargs)

    def properties(self):
        return {name: self.shell("getprop", name).strip() for name in (
            "ro.product.model", "ro.build.fingerprint", "ro.build.version.sdk", "ro.kernel.qemu")}

    def battery(self):
        data = self.shell("dumpsys", "battery")
        result = {}
        for name in ("level", "temperature", "status"):
            match = re.search(rf"^\s*{name}: (\d+)", data, re.MULTILINE)
            if match:
                result[name] = int(match[1]) / (10 if name == "temperature" else 1)
        if "temperature" not in result:
            raise ValueError("Cannot read battery temperature; refusing an uncontrolled comparison.")
        return result

    def cool_down(self, seconds, max_temperature, timeout):
        start = time.monotonic()
        while True:
            battery = self.battery()
            elapsed = time.monotonic() - start
            if elapsed >= seconds and battery["temperature"] <= max_temperature:
                return battery
            if elapsed >= timeout:
                raise RuntimeError(f"Phone did not cool to {max_temperature}°C in {timeout}s: {battery}")
            time.sleep(min(5, timeout - elapsed))

    def fresh_install(self, apk, package, log):
        # `pm path` returns exit code 1 with empty output for a missing package.
        # Listing packages succeeds for both the installed and absent cases.
        packages = self.shell("pm", "list", "packages", "--user", "current", package).splitlines()
        if f"package:{package}" in packages:
            result = self.run("uninstall", package)
            if "Success" not in result:
                raise RuntimeError(f"Could not uninstall benchmark package {package}: {result}")
        result = self.run("install", str(apk), log=log, timeout=180)
        if "Success" not in result:
            raise RuntimeError(f"APK installation failed; see {log}")


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--before", type=Path, required=True, help="Directory produced by build.py")
    parser.add_argument("--after", type=Path, required=True, help="Directory produced by build.py")
    parser.add_argument("--serial", required=True, help="Physical device serial from adb devices")
    parser.add_argument("--adb", help="Path to adb")
    parser.add_argument("--output", type=Path, required=True, help="New directory for the comparison")
    parser.add_argument("--cycles", type=int, default=3, help="ABBA cycles; default: 3 (12 test runs)")
    parser.add_argument("--cooldown-seconds", type=float, default=15)
    parser.add_argument("--max-battery-temperature", type=float, default=38)
    parser.add_argument("--cooldown-timeout", type=float, default=600)
    args = parser.parse_args()
    if args.cycles < 1 or not 0 <= args.cooldown_seconds < args.cooldown_timeout:
        parser.error("cycles must be positive and 0 <= cooldown-seconds < cooldown-timeout")
    if not 15 <= args.max_battery_temperature <= 45:
        parser.error("max-battery-temperature must be between 15 and 45°C")
    inputs = {"A": args.before.resolve(), "B": args.after.resolve()}
    builds = {group: load_build(folder) for group, folder in inputs.items()}
    compatible_builds(builds["A"], builds["B"])
    test_sources = "\n".join(path.read_text() for path in (MODULE / "src").rglob("*.kt"))
    expected_metrics = set(re.findall(r'(?:TraceSectionMetric|HistogramMetric)\("([^"]+)"', test_sources))
    missing_metrics = expected_metrics - set(builds["A"]["traceMetrics"])
    if missing_metrics:
        raise ValueError(f"Current tests require trace metrics absent from the APKs: {sorted(missing_metrics)}")
    device = Device(adb_path(args.adb), args.serial)
    properties = device.properties()
    if properties["ro.kernel.qemu"] == "1" or int(properties["ro.build.version.sdk"]) < 29:
        raise ValueError("Use a physical Android device with API 29 or newer.")
    output = args.output.resolve()
    output.mkdir(parents=True, exist_ok=False)
    manifest = {"schemaVersion": 1, "created": now(), "status": "running",
                "device": {"serial": args.serial, **properties}, "builds": builds,
                "testRevision": revision(), "schedule": schedule(args.cycles), "runs": [],
                "cooldownSeconds": args.cooldown_seconds, "maxBatteryTemperature": args.max_battery_temperature}
    manifest_path = output / "comparison.json"
    write_json(manifest_path, manifest)
    try:
        for group, folder in inputs.items():
            destination = output / "builds" / group
            destination.mkdir(parents=True)
            shutil.copy2(folder / "app.apk", destination / "app.apk")
            shutil.copy2(folder / "build.json", destination / "build.json")
            if sha256(destination / "app.apk") != builds[group]["apkSha256"]:
                raise ValueError(f"Build {group} changed while it was being copied")
        source_hash = tree_hash(MODULE / "src")
        gradle(":macrobenchmark:assembleMacrobenchmarkRelease", output / "test-build.log")
        if source_hash != tree_hash(MODULE / "src"):
            raise ValueError("Test sources changed during compilation; restart the comparison.")
        shutil.copytree(MODULE / "src", output / "test-source")
        shutil.copy2(built_apk("macrobenchmark", TEST_PACKAGE), output / "test.apk")
        manifest["testApkSha256"] = sha256(output / "test.apk")
        manifest["testSourceSha256"] = source_hash
        write_json(manifest_path, manifest)
        device.fresh_install(output / "test.apk", TEST_PACKAGE, output / "test-install.log")
        for index, group in enumerate(manifest["schedule"]):
            name = f"{index + 1:02d}-{group}"
            folder = output / "runs" / name
            folder.mkdir(parents=True)
            print(f"[{index + 1}/{len(manifest['schedule'])}] {group}: installing and cooling down", flush=True)
            device.fresh_install(output / "builds" / group / "app.apk", APP_PACKAGE, folder / "install.log")
            battery = device.cool_down(args.cooldown_seconds, args.max_battery_temperature, args.cooldown_timeout)
            if device.properties() != properties:
                raise ValueError("Device configuration changed during the comparison.")
            remote = f"/sdcard/Android/media/{TEST_PACKAGE}/comparison-{uuid.uuid4().hex}"
            run = {"group": group, "order": index + 1, "started": now(), "status": "running",
                   "batteryBefore": battery, "remoteOutput": remote}
            manifest["runs"].append(run)
            write_json(manifest_path, manifest)
            print(f"[{index + 1}/{len(manifest['schedule'])}] {group}: running tests", flush=True)
            result = device.shell("am", "instrument", "-w", "-e", "additionalTestOutputDir", remote,
                                  f"{TEST_PACKAGE}/androidx.test.runner.AndroidJUnitRunner",
                                  log=folder / "instrumentation.log", timeout=1800)
            if not instrumentation_succeeded(result):
                raise RuntimeError(f"Instrumentation did not pass; see {folder / 'instrumentation.log'}")
            device.run("pull", remote, str(folder / "artifacts"), log=folder / "pull.log", timeout=300)
            files = list((folder / "artifacts").rglob("*-benchmarkData.json"))
            if len(files) != 1:
                raise ValueError(f"Expected one benchmark JSON in {folder}, found {len(files)}")
            run.update({"status": "complete", "finished": now(), "batteryAfter": device.battery(),
                        "json": files[0].relative_to(output).as_posix()})
            write_json(manifest_path, manifest)
            # Only remove this invocation's unique directory, after successful pull.
            device.shell("rm", "-r", remote)
        manifest["status"] = "complete"
        manifest["finished"] = now()
        write_json(manifest_path, manifest)
        print(f"Results saved: {manifest_path}\nGenerate report: python3 {Path(__file__).with_name('report.py')} {manifest_path}")
    except BaseException as error:
        manifest["status"] = "interrupted" if isinstance(error, KeyboardInterrupt) else "failed"
        manifest["error"] = str(error)
        write_json(manifest_path, manifest)
        raise


if __name__ == "__main__":
    main_guard(main)
