#!/usr/bin/env python3
"""Generate an offline HTML report from run.py's comparison.json."""

import argparse
import csv
import json
import math
from pathlib import Path
import random
from statistics import median

from common import main_guard, now, sha256, write_json


def confidence_interval(a, b):
    """Resample whole runs, not correlated iterations within each run."""
    if min(len(a), len(b)) < 5 or min(a) <= 0:
        return None
    rng = random.Random(0)
    changes = sorted((median(rng.choices(b, k=len(b))) / median(rng.choices(a, k=len(a))) - 1) * 100
                     for _ in range(3000))
    return [changes[74], changes[2924]]


def read_run(path):
    data = json.loads(path.read_text())
    context = data.get("context", {})
    if not context.get("build", {}).get("fingerprint"):
        raise ValueError(f"Missing device fingerprint: {path}")
    measurements = {}
    for benchmark in data.get("benchmarks", []):
        case = benchmark["name"]
        identity = json.dumps([benchmark.get("className"), case, benchmark.get("params", {})], sort_keys=True)
        if identity in measurements:
            raise ValueError(f"Duplicate benchmark {case}: {path}")
        metrics = {}
        for name, metric in benchmark.get("metrics", {}).items():
            if not name.endswith("Ms"):
                continue
            values = metric.get("runs", [])
            if (not values or len(values) != benchmark.get("repeatIterations") or
                    any(isinstance(value, bool) or not isinstance(value, (int, float)) or
                        not math.isfinite(value) or value < 0 for value in values)):
                raise ValueError(f"Invalid/incomplete measurements for {case}/{name}: {path}")
            metrics[name] = values
        if not metrics or "Div.TotalFirstMs" not in metrics:
            raise ValueError(f"Missing Div.TotalFirstMs for {case}: {path}")
        measurements[identity] = {"case": case, "metrics": metrics,
                                  "iterations": benchmark["repeatIterations"]}
    if not measurements:
        raise ValueError(f"No benchmark measurements in {path}")
    return context, measurements


def summarize(manifest_path, threshold):
    manifest = json.loads(manifest_path.read_text())
    if manifest.get("schemaVersion") != 1:
        raise ValueError("Unsupported comparison manifest version")
    root = manifest_path.parent.resolve()
    rows = {}
    expected_signature = None
    expected_context = None
    hashes = {"A": set(), "B": set()}
    warnings = []
    for run in manifest.get("runs", []):
        if run.get("status") != "complete":
            continue
        group = run["group"]
        if group not in hashes:
            raise ValueError(f"Unknown comparison group: {group}")
        path = (root / run["json"]).resolve()
        if root not in path.parents:
            raise ValueError("Run JSON must be inside the comparison directory")
        context, measurements = read_run(path)
        comparable_context = {
            "build": context["build"],
            **{key: context.get(key) for key in ("artMainlineVersion", "cpuLocked", "cpuMaxFreqHz",
                                                "sustainedPerformanceModeEnabled", "compilationMode")},
        }
        signature = {key: (sorted(value["metrics"]), value["iterations"]) for key, value in measurements.items()}
        if expected_signature is None:
            expected_signature, expected_context = signature, comparable_context
        if signature != expected_signature:
            raise ValueError(f"Scenarios, metrics or iteration counts differ: {path}")
        if comparable_context != expected_context:
            raise ValueError(f"Device/OS/ART or performance configuration differs: {path}")
        digest = sha256(path)
        if digest in hashes[group]:
            raise ValueError(f"The same JSON was counted twice in group {group}: {path}")
        hashes[group].add(digest)
        for identity, benchmark in measurements.items():
            for name, values in benchmark["metrics"].items():
                key = (identity, name)
                row = rows.setdefault(key, {"case": benchmark["case"], "metric": name, "A": [], "B": []})
                row[group].append({"order": run["order"], "median": median(values), "values": values,
                                   "json": run["json"], "battery": run.get("batteryBefore")})
    if not all(hashes.values()):
        raise ValueError("At least one completed run is required for both A and B")
    same_input = bool(hashes["A"] & hashes["B"])
    if same_input:
        warnings.append("A and B contain identical JSON: this is a report preview, not an independent build comparison.")
    complete = manifest.get("status") == "complete"
    if not complete:
        warnings.append("The comparison is incomplete. Only successfully saved runs are shown; no performance verdict is given.")
    if min(map(len, hashes.values())) < 5:
        warnings.append("Fewer than five runs per build: differences are descriptive only; no confidence interval is calculated.")
    for row in rows.values():
        a, b = ([run["median"] for run in row[group]] for group in ("A", "B"))
        row["before"], row["after"] = median(a), median(b)
        row["deltaMs"] = row["after"] - row["before"]
        row["deltaPercent"] = (row["after"] / row["before"] - 1) * 100 if row["before"] > 0 else None
        row["interval"] = confidence_interval(a, b) if complete and not same_input else None
        row["verdict"] = "uncertain"
        if row["interval"]:
            if row["interval"][1] < -threshold:
                row["verdict"] = "faster"
            elif row["interval"][0] > threshold:
                row["verdict"] = "slower"
    return {"generated": now(), "status": manifest.get("status"), "warnings": warnings,
            "builds": manifest.get("builds", {}), "device": manifest.get("device", {}),
            "testRevision": manifest.get("testRevision"), "threshold": threshold,
            "counts": {group: len(values) for group, values in hashes.items()},
            "rows": sorted(rows.values(), key=lambda row: (row["case"], row["metric"] != "Div.TotalFirstMs", row["metric"]))}


def generate(manifest_path, threshold=3):
    summary = summarize(manifest_path.resolve(), threshold)
    root = manifest_path.resolve().parent
    write_json(root / "summary.json", summary)
    with (root / "summary.csv").open("w", newline="") as stream:
        writer = csv.writer(stream)
        writer.writerow(["scenario", "metric", "A_ms", "B_ms", "delta_ms", "delta_percent", "CI95_low", "CI95_high", "verdict"])
        for row in summary["rows"]:
            writer.writerow([row["case"], row["metric"], row["before"], row["after"], row["deltaMs"],
                             row["deltaPercent"], *(row["interval"] or [None, None]), row["verdict"]])
    # Prevent user-controlled labels from ending the script element.
    payload = json.dumps(summary, ensure_ascii=False).replace("<", "\\u003c").replace("&", "\\u0026")
    template = Path(__file__).with_name("report.template.html").read_text()
    result = root / "report.html"
    result.write_text(template.replace("__COMPARISON_DATA__", payload))
    return result


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("comparison", type=Path, help="Path to comparison.json")
    parser.add_argument("--threshold-percent", type=float, default=3, help="Practical change threshold; default: 3%%")
    args = parser.parse_args()
    if not math.isfinite(args.threshold_percent) or args.threshold_percent < 0:
        parser.error("threshold-percent must be finite and non-negative")
    print(f"Report: {generate(args.comparison, args.threshold_percent)}")


if __name__ == "__main__":
    main_guard(main)
