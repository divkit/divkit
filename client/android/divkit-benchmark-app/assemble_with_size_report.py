import json
import os
import subprocess
import sys
from datetime import datetime

PROJECT_DIR = os.getcwd()
MODULE = 'divkit-benchmark-app'
APK_DEBUG_DIR = os.path.join(PROJECT_DIR, f'{MODULE}/build/outputs/apk/perf')
OUTPUT_DIR = f'{PROJECT_DIR}/{MODULE}/build/generated/assets/apk-size'


def assemble_apk():
    print(subprocess.check_output(
        f'./gradlew :{MODULE}:assemblePerf -q',
        shell=True,
        stderr=subprocess.STDOUT,
    ))
    apk_path = find_apk()
    apk_dir = os.path.dirname(apk_path)
    apk_basename = os.path.basename(apk_path)
    new_basename = apk_basename[:-4] + '-with-size-report.apk'
    new_path = os.path.join(apk_dir, new_basename)
    os.rename(apk_path, new_path)
    print(f'--> Apk with size report assembled: {new_path}')


def find_apk():
    if not os.path.isdir(APK_DEBUG_DIR):
        raise FileNotFoundError(f'APK output directory not found: {APK_DEBUG_DIR}')
    for name in os.listdir(APK_DEBUG_DIR):
        if name.startswith(MODULE) and name.endswith('.apk'):
            return os.path.join(APK_DEBUG_DIR, name)
    raise FileNotFoundError(f'No APK starting with "{MODULE}" in {APK_DEBUG_DIR}')


def generate_report() -> {}:
    now = datetime.now()
    apk_file = find_apk()
    report = {
        'benchmark-app': {
            'file': apk_file,
            'size': os.path.getsize(apk_file),
        },
        'generation_time': now.strftime("%B %d, %Y %I:%M:%S %p")
    }
    return report


def attach_size_report():
    report = generate_report()
    os.makedirs(OUTPUT_DIR, exist_ok=True)
    report_file = OUTPUT_DIR + '/size-report.json'
    with open(report_file, 'w', encoding='utf-8') as f:
        json.dump(report, f, ensure_ascii=False, indent=4)
    if not os.path.exists(report_file):
        raise Exception(f'Unable to save report file to "{report_file}"')
    print(f'--> Size Report saved to: {report_file}')


def main():
    attach_size_report()
    assemble_apk()


if __name__ == '__main__':
    main()
