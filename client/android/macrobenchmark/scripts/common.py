"""Shared helpers for the standalone Macrobenchmark scripts (Python 3.9+)."""

import hashlib
import json
import os
from pathlib import Path
import re
import shlex
import shutil
import subprocess
import sys
from datetime import datetime, timezone


ANDROID = Path(__file__).resolve().parents[2]
MODULE = ANDROID / "macrobenchmark"
APP_PACKAGE = "com.yandex.divkit.benchmark.macrobenchmark"
TEST_PACKAGE = "com.yandex.divkit.macrobenchmark"
VARIANT = "macrobenchmarkRelease"
ASSETS = ("with_templates.json", "services.json")


def now():
    return datetime.now(timezone.utc).isoformat()


def write_json(path, data):
    path = Path(path)
    temporary = path.with_suffix(path.suffix + ".tmp")
    temporary.write_text(json.dumps(data, indent=2, ensure_ascii=False) + "\n")
    temporary.replace(path)


def sha256(path):
    digest = hashlib.sha256()
    with Path(path).open("rb") as stream:
        for chunk in iter(lambda: stream.read(1024 * 1024), b""):
            digest.update(chunk)
    return digest.hexdigest()


def tree_hash(root):
    digest = hashlib.sha256()
    for path in sorted(Path(root).rglob("*")):
        if path.is_file():
            digest.update(path.relative_to(root).as_posix().encode() + b"\0")
            digest.update(path.read_bytes())
    return digest.hexdigest()


def command(args, *, cwd=None, log=None, timeout=120):
    args = [str(arg) for arg in args]
    if log is None:
        result = subprocess.run(args, cwd=cwd, text=True, stdout=subprocess.PIPE,
                                stderr=subprocess.STDOUT, timeout=timeout)
        output = result.stdout
    else:
        with Path(log).open("w") as stream:
            result = subprocess.run(args, cwd=cwd, text=True, stdout=stream,
                                    stderr=subprocess.STDOUT, timeout=timeout)
        output = Path(log).read_text(errors="replace")
    if result.returncode:
        detail = output.strip()[-4000:] or "Command produced no output."
        if log:
            detail += f"\nFull log: {log}"
        raise RuntimeError(f"Command exited with {result.returncode}: {shlex.join(args)}\n{detail}")
    return output


def revision():
    try:
        info = json.loads(command(["arc", "info", "--json"], cwd=ANDROID))
        status = command(["arc", "status", "--short", "."], cwd=ANDROID.parents[2])
        return {"hash": info["hash"], "branch": info.get("branch"),
                "dirty": bool(status.strip()), "status": status}
    except (OSError, RuntimeError, ValueError, subprocess.TimeoutExpired) as error:
        return {"hash": None, "warning": f"Revision unavailable: {error}"}


def gradle(task, log):
    print(f"Building {task}; log: {log}", flush=True)
    command(["bash", ANDROID / "gradlew", task, "--console=plain"],
            cwd=ANDROID, log=log, timeout=3600)


def built_apk(module, expected_package):
    folder = ANDROID / module / "build/outputs/apk" / VARIANT
    metadata = json.loads((folder / "output-metadata.json").read_text())
    if metadata["applicationId"] != expected_package or len(metadata["elements"]) != 1:
        raise ValueError(f"Unexpected APK metadata in {folder}")
    path = folder / metadata["elements"][0]["outputFile"]
    if not path.is_file():
        raise ValueError(f"Missing APK: {path}")
    return path


def adb_path(explicit=None):
    if explicit:
        return str(Path(explicit).resolve())
    found = shutil.which("adb")
    if found:
        return found
    sdk = os.environ.get("ANDROID_HOME") or os.environ.get("ANDROID_SDK_ROOT")
    properties = ANDROID / "local.properties"
    if not sdk and properties.exists():
        match = re.search(r"^sdk\.dir=(.+)$", properties.read_text(), re.MULTILINE)
        if match:
            sdk = match[1].replace("\\:", ":").replace("\\\\", "\\")
    candidate = Path(sdk or "") / "platform-tools/adb"
    if candidate.is_file():
        return str(candidate.resolve())
    raise ValueError("adb not found. Use --adb /path/to/adb or set ANDROID_HOME.")


def main_guard(main):
    try:
        main()
    except KeyboardInterrupt:
        print("Interrupted. Completed runs and logs have been preserved.", file=sys.stderr)
        raise SystemExit(130)
    except (OSError, ValueError, RuntimeError, subprocess.TimeoutExpired) as error:
        print(f"Error: {error}", file=sys.stderr)
        raise SystemExit(1)
