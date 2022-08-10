import subprocess
from sys import argv
from os import path, system

script_dir = path.split(path.abspath(__file__))[0]
project_dir = script_dir[:script_dir.index('scripts')]
reports_dir = path.join(project_dir, 'divkit-demo-app/build/reports/')
report_file = lambda run: reports_dir + '/uitests-run-' + str(run) + '.log'
args = '' if len(argv) < 3 else ' '.join(argv[2:])

system('mkdir -p ' + reports_dir)
for run in range(int(argv[1])):
    subprocess.check_output('./gradlew :divkit-demo-app:runUiTests ' + args + ' > ' + report_file(run + 1), shell=True)
