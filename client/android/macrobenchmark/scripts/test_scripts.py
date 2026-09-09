import sys
import json
import tempfile
import unittest
from pathlib import Path
from unittest.mock import patch

from common import APP_PACKAGE, command
from run import Device, instrumentation_succeeded, schedule
from report import confidence_interval, generate, summarize


class DeviceTests(unittest.TestCase):
    def test_absent_package_is_installed_without_uninstall(self):
        device = Device("adb", "serial")
        with patch.object(device, "shell", return_value="") as shell, \
                patch.object(device, "run", return_value="Success\n") as run:
            device.fresh_install(Path("app.apk"), APP_PACKAGE, Path("install.log"))
        shell.assert_called_once_with("pm", "list", "packages", "--user", "current", APP_PACKAGE)
        run.assert_called_once_with("install", "app.apk", log=Path("install.log"), timeout=180)

    def test_existing_package_is_uninstalled(self):
        device = Device("adb", "serial")
        with patch.object(device, "shell", return_value=f"package:{APP_PACKAGE}\n"), \
                patch.object(device, "run", return_value="Success\n") as run:
            device.fresh_install(Path("app.apk"), APP_PACKAGE, Path("install.log"))
        self.assertEqual(run.call_args_list[0].args, ("uninstall", APP_PACKAGE))
        self.assertEqual(run.call_args_list[1].args, ("install", "app.apk"))

    def test_similar_package_name_does_not_trigger_uninstall(self):
        device = Device("adb", "serial")
        with patch.object(device, "shell", return_value=f"package:{APP_PACKAGE}.other\n"), \
                patch.object(device, "run", return_value="Success\n") as run:
            device.fresh_install(Path("app.apk"), APP_PACKAGE, Path("install.log"))
        self.assertEqual(run.call_count, 1)
        self.assertEqual(run.call_args.args[0], "install")

    def test_adb_failure_is_not_treated_as_missing_package(self):
        device = Device("adb", "serial")
        with patch.object(device, "shell", side_effect=RuntimeError("device offline")), \
                patch.object(device, "run") as run:
            with self.assertRaisesRegex(RuntimeError, "device offline"):
                device.fresh_install(Path("app.apk"), APP_PACKAGE, Path("install.log"))
        run.assert_not_called()

    def test_command_error_contains_command_even_without_output(self):
        with self.assertRaisesRegex(RuntimeError, "Command produced no output") as error:
            command([sys.executable, "-c", "raise SystemExit(1)"])
        self.assertIn("raise SystemExit(1)", str(error.exception))

    def test_command_error_includes_stderr_and_log(self):
        with tempfile.TemporaryDirectory() as folder:
            log = Path(folder) / "failure.log"
            with self.assertRaises(RuntimeError) as error:
                command([sys.executable, "-c", "import sys; sys.exit('offline')"], log=log)
            self.assertIn("offline", str(error.exception))
            self.assertIn(str(log), str(error.exception))

    def test_instrumentation_failures_are_not_hidden_by_exit_zero(self):
        self.assertTrue(instrumentation_succeeded("OK (2 tests)\n"))
        for output in ("OK (0 tests)", "INSTRUMENTATION_FAILED", "FAILURES!!!\nOK (2 tests)"):
            self.assertFalse(instrumentation_succeeded(output))

    def test_abba_order(self):
        self.assertEqual(schedule(2), ["A", "B", "B", "A", "A", "B", "B", "A"])


class ReportTests(unittest.TestCase):
    def fixture(self, root, counts=1):
        runs = []
        for group, scale in (("A", 1), ("B", 0.8)):
            for index in range(counts):
                path = root / f"{group}-{index}.json"
                data = {"context": {"build": {"fingerprint": "test-device"}},
                        "benchmarks": [{"className": "Benchmark", "name": "card", "repeatIterations": 3,
                                        "metrics": {"Div.TotalFirstMs": {
                                            "runs": [(100 + index) * scale, (101 + index) * scale, (102 + index) * scale]}}}]}
                path.write_text(json.dumps(data))
                runs.append({"group": group, "order": len(runs) + 1, "status": "complete", "json": path.name})
        manifest = {"schemaVersion": 1, "status": "complete", "runs": runs,
                    "builds": {"A": {"label": "</script><script>alert(1)</script>"}}}
        path = root / "comparison.json"
        path.write_text(json.dumps(manifest))
        return path

    def test_single_run_has_no_confidence_claim(self):
        with tempfile.TemporaryDirectory() as folder:
            result = summarize(self.fixture(Path(folder)), 3)
        row = result["rows"][0]
        self.assertAlmostEqual(row["deltaPercent"], -20)
        self.assertIsNone(row["interval"])
        self.assertEqual(row["verdict"], "uncertain")

    def test_consistent_change_across_runs(self):
        with tempfile.TemporaryDirectory() as folder:
            result = summarize(self.fixture(Path(folder), counts=6), 3)
        row = result["rows"][0]
        self.assertEqual(row["verdict"], "faster")
        self.assertLess(row["interval"][1], -3)

    def test_histogram_metrics_are_included_in_report(self):
        with tempfile.TemporaryDirectory() as folder:
            root = Path(folder)
            manifest = self.fixture(root)
            for group, scale in (("A", 1), ("B", 0.8)):
                path = root / f"{group}-0.json"
                data = json.loads(path.read_text())
                for name in ("Div.CompositionMs", "Div.RenderEffectsMs"):
                    data["benchmarks"][0]["metrics"][name] = {
                        "runs": [10 * scale, 20 * scale, 30 * scale]}
                path.write_text(json.dumps(data))
            html = generate(manifest).read_text()
            rows = {row["metric"]: row for row in summarize(manifest, 3)["rows"]}
            for name in ("Div.CompositionMs", "Div.RenderEffectsMs"):
                self.assertEqual(rows[name]["before"], 20)
                self.assertEqual(rows[name]["after"], 16)
                self.assertAlmostEqual(rows[name]["deltaPercent"], -20)
                self.assertIn(name, html)

    def test_missing_metric_or_iterations_is_rejected(self):
        with tempfile.TemporaryDirectory() as folder:
            root = Path(folder)
            manifest = self.fixture(root)
            path = root / "B-0.json"
            data = json.loads(path.read_text())
            data["benchmarks"][0]["metrics"]["Div.TotalFirstMs"]["runs"] = []
            path.write_text(json.dumps(data))
            with self.assertRaisesRegex(ValueError, "Invalid/incomplete"):
                summarize(manifest, 3)

    def test_device_mismatch_is_rejected(self):
        with tempfile.TemporaryDirectory() as folder:
            root = Path(folder)
            manifest = self.fixture(root)
            path = root / "B-0.json"
            data = json.loads(path.read_text())
            data["context"]["build"]["fingerprint"] = "other-device"
            path.write_text(json.dumps(data))
            with self.assertRaisesRegex(ValueError, "Device/OS/ART"):
                summarize(manifest, 3)

    def test_failed_comparison_has_no_verdict(self):
        with tempfile.TemporaryDirectory() as folder:
            path = self.fixture(Path(folder), counts=6)
            data = json.loads(path.read_text())
            data["status"] = "failed"
            path.write_text(json.dumps(data))
            result = summarize(path, 3)
        self.assertEqual(result["rows"][0]["verdict"], "uncertain")
        self.assertIsNone(result["rows"][0]["interval"])

    def test_html_escapes_script_tags(self):
        with tempfile.TemporaryDirectory() as folder:
            path = generate(self.fixture(Path(folder)))
            html = path.read_text()
            self.assertNotIn("</script><script>alert(1)</script>", html)
            self.assertNotIn("__COMPARISON_DATA__", html)
            self.assertTrue((path.parent / "summary.csv").exists())

    def test_zero_baseline_has_no_percentage_interval(self):
        self.assertIsNone(confidence_interval([0] * 6, [1] * 6))


if __name__ == "__main__":
    unittest.main()
