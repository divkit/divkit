#!/usr/bin/env python3
"""Build and archive the target app from the current working tree."""

import argparse
import hashlib
import re
import shutil
import zipfile
from pathlib import Path

from common import (ANDROID, APP_PACKAGE, ASSETS, VARIANT, built_apk, gradle,
                    main_guard, now, revision, sha256, tree_hash, write_json)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--output", type=Path, required=True, help="New directory for app.apk and build.json")
    parser.add_argument("--label", help="Readable build name, e.g. before or after")
    args = parser.parse_args()
    output = args.output.resolve()
    output.mkdir(parents=True, exist_ok=False)
    source = ANDROID / "divkit-benchmark-app/src" / VARIANT
    source_hash = tree_hash(source)
    state = revision()
    gradle(":divkit-benchmark-app:assembleMacrobenchmarkRelease", output / "build.log")
    if source_hash != tree_hash(source) or state != revision():
        raise ValueError("Working tree/revision changed during the build; repeat in a new output directory.")
    shutil.copy2(built_apk("divkit-benchmark-app", APP_PACKAGE), output / "app.apk")
    with zipfile.ZipFile(output / "app.apk") as archive:
        assets = {name: hashlib.sha256(archive.read("assets/" + name)).hexdigest() for name in ASSETS}
    markers = sorted(set(re.findall(r'"(Div\.[A-Za-z]+)"', "\n".join(
        path.read_text() for path in source.rglob("*.kt")))))
    write_json(output / "build.json", {
        "schemaVersion": 1, "created": now(), "label": args.label or output.name,
        "revision": state, "applicationId": APP_PACKAGE, "variant": VARIANT,
        "apkSha256": sha256(output / "app.apk"), "assets": assets,
        "traceSourceSha256": source_hash, "traceMetrics": markers,
    })
    print(f"Build saved: {output / 'app.apk'}")


if __name__ == "__main__":
    main_guard(main)
