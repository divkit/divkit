import argparse
import collections
import contextlib
import functools
import json
import logging
import os
import sys
import time
import urllib

import yaml


BenchmarkMeta = collections.namedtuple('BenchmarkMeta', (
    'description', 'owners', 'timeout', 'enabled', 'disable_reason',
))


ALL_BENCHMARKS = {}


def benchmark(name, description, owners, timeout,
              enabled=True, disable_reason=None):
    def outer_wrapper(fn):
        @functools.wraps(fn)
        def inner_wrapper(args):
            return fn(args.device, args.repeat, args.output)

        assert name not in ALL_BENCHMARKS, \
            '{} is already defined as a benchmark'.format(name)

        meta = BenchmarkMeta(description, owners, timeout,
                             enabled, disable_reason)
        ALL_BENCHMARKS[name] = (meta, inner_wrapper)
        return inner_wrapper

    return outer_wrapper


def print_metadata(args):
    all_meta = [
        {'name': name, 'description': meta.description, 'owners': meta.owners,
         'timeout': meta.timeout, 'enabled': meta.enabled,
         'disable_reason': meta.disable_reason}
        for name, (meta, fn) in ALL_BENCHMARKS.iteritems()
    ]
    yaml.dump(all_meta, sys.stdout)


def run_benchmark(args):
    meta, fn = ALL_BENCHMARKS[args.NAME]
    fn(args)


@contextlib.contextmanager
def modified_path(path):
    try:
        sys.path.insert(0, path)
        yield path
    finally:
        if path == sys.path[0]:
            sys.path.pop(0)
        else:
            logging.warning('Expected {} in path, found {}'.format(
                path, sys.path[0]))


@contextlib.contextmanager
def modified_env(key, value):
    try:
        old_value = os.environ.pop(key, None)
        os.environ[key] = value
        yield
    finally:
        final_value = os.environ.pop(key)
        assert value == final_value, \
            'Expected {} in env for {}, found {}'.format(
                value, key, final_value)
        if old_value is not None:
            os.environ[key] = old_value


@contextlib.contextmanager
def initialized_libs():
    third_party = os.path.join(os.path.dirname(__file__), 'third_party')
    devil_dir = os.path.join(third_party, 'catapult', 'devil')
    with modified_path(devil_dir):
        try:
            from devil.android import device_utils  # noqa
        except ImportError:
            logging.exception(
                'Failed to import some third_party perf dependencies. '
                'You may need to run scripts/devperf/download_deps.py before'
                'running a benchmark.')
            raise
        yield


@contextlib.contextmanager
def high_perf_mode(device):
    from devil.android.perf import perf_control
    pc = perf_control.PerfControl(device)
    try:
        pc.SetHighPerfMode()
        yield
    finally:
        pc.SetDefaultPerfMode()


@contextlib.contextmanager
def started_app(device, package, activity):
    from devil.android.sdk import intent
    try:
        device.StartActivity(
            intent.Intent(action=None, package=package, activity=activity),
            force_stop=True)
        yield
    finally:
        device.ForceStop(package)


def extract_histograms(monitor, names, timeout_in_seconds=10):
    for name in names:
        regex = (r'AliceKitHistograms:\s+{}:\s+(?P<time>\d+)\s+ms'.format(
            name.replace('.', r'\.')))
        match = monitor.WaitFor(regex, timeout=timeout_in_seconds)
        yield (name, int(match.group('time')))


def format_items(test, metrics):
    for metric, values in metrics.iteritems():
        escaped_metric = urllib.quote_plus(metric.encode('utf-8'))
        yield {
            'description': None,
            'selector': '{}/{}/summary'.format(test, escaped_metric),
            'type': 'list_of_scalar_values',
            'units': 'ms',
            'value': values,
        }


def dump_metrics(test, metrics, output):
    if not os.path.exists(output):
        os.makedirs(output)

    with open(os.path.join(output, 'results.ya.json'), 'w') as out_file:
        json.dump({'timestamp': int(time.time()),
                   'items': list(format_items(test, metrics))},
                  out_file, indent=4, sort_keys=True)


@benchmark('startup.warm', 'Warm startup', ['gulevsky'], 10)
def warm_startup(serial, repeat, output):
    default_repeat = 31
    package = 'com.yandex.alicekit.demo'
    activity = '.alice.DemoAliceActivity'

    histograms = (
        'AliceLib.Init',
        'AliceActivity.Create.First',
        'AliceActivity.Start.First',
        'AliceActivity.Draw.First',
    )

    metrics = collections.defaultdict(list)

    with initialized_libs():
        from devil.android import device_utils
        device = device_utils.DeviceUtils(serial)

        device.ForceStop(package)
        with high_perf_mode(device):
            for i in range(0, repeat or default_repeat):
                with device.GetLogcatMonitor(clear=True) as monitor, \
                        started_app(device, package, activity):
                    if i != 0:
                        for name, value in extract_histograms(
                                monitor, histograms):
                            metrics[name].append(value)

    if output:
        dump_metrics('startup.warm', metrics, output)


def parse_args():
    p = argparse.ArgumentParser(
        description='Performance benchmark runner entry point')
    p.add_argument('--verbose', '-v', help='Increase logging verbosity')
    sp = p.add_subparsers(help='sub-command help')

    mp = sp.add_parser('print_metadata',
                       help='Print benchmark metadata in YAML to stdout')
    mp.set_defaults(func=print_metadata)

    rp = sp.add_parser('run', help='Run a benchmark')
    rp.add_argument('--device', required=True, help='Android device to use')
    rp.add_argument('--repeat', type=int,
                    help='Number of repetitions instead of default')
    rp.add_argument('--output', help='Directory to write results to')
    rp.add_argument('NAME', help='Benchmark to run',
                    choices=[name for name in ALL_BENCHMARKS])
    rp.set_defaults(func=run_benchmark)

    return p.parse_args()


def main():
    args = parse_args()
    logging.basicConfig(level=logging.DEBUG if args.verbose else logging.INFO)
    args.func(args)


if __name__ == '__main__':
    main()
