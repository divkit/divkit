#!python3
import json
import os
import shutil
from sys import exit

SUCCESS_CODE = 0
TERMINATED_CODE = 33280

result_msg = {
    SUCCESS_CODE: "!!!SUCCESS!!!",
    TERMINATED_CODE: "!!!TERMINATED!!!"
}

PASSED_TEST_CASES_FILE_PATH = \
    "divkit-demo-app/build/reports/screenshots/collected/passed_test_cases.json"

TEST_CASES_DIR = "../../test_data"
TEMP_DIR = "divkit-demo-app/build/temp_passed_test_cases"


def run_snapshot_tests(max_reruns=1):
    """
        Used from gradle project root working directory to run snapshot tests.
        @:param max_reruns - max number of test restarts in case of failure (default is 3)
    """

    task_name = ":divkit-demo-app:connectedDebugAndroidTest"
    run_command = f"./gradlew {task_name} -Pscreenshot-tests -Pscreenshot-comparison"
    passed_test_cases = []

    def exclude_passed_test_cases():
        if os.path.exists(PASSED_TEST_CASES_FILE_PATH):
            with (open(PASSED_TEST_CASES_FILE_PATH)) as file:
                test_cases = json.load(file)
                file.close()
                for passed_case in test_cases:
                    passed_test_cases.append(passed_case)
                    # move test case to temp dir
                    test_case_dir = os.path.dirname(os.path.join(TEMP_DIR, passed_case))
                    if not os.path.exists(test_case_dir):
                        os.makedirs(test_case_dir)
                    os.replace(f"{TEST_CASES_DIR}/{passed_case}", f"{TEMP_DIR}/{passed_case}")
                    print(f"PASSED: {passed_case}", flush=True)
        else:
            print(f"\nWARNING: File {PASSED_TEST_CASES_FILE_PATH} does not exist!\n", flush=True)

    result = os.system(run_command)

    rerun = 1
    while result not in [SUCCESS_CODE, TERMINATED_CODE] and rerun <= max_reruns:
        print(f"\nRERUN {rerun}/{max_reruns}\n", flush=True)
        exclude_passed_test_cases()
        result = os.system(run_command)
        rerun += 1

    # Restore test cases from temp dir
    print("\nRestore test cases...")
    if passed_test_cases:
        for test_case in passed_test_cases:
            os.replace(f"{TEMP_DIR}/{test_case}", f"{TEST_CASES_DIR}/{test_case}")
        shutil.rmtree(TEMP_DIR)

    return result


def main():
    project_dir = os.path.normpath(
        os.path.join(os.path.dirname(os.path.normpath(__file__)), "../..")
    )

    os.chdir(project_dir)

    result = run_snapshot_tests()

    print(f"\n{result_msg.get(result, '!!!FAILURE!!!')}")

    exit(result)


if __name__ == '__main__':
    main()
