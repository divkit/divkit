import 'dart:developer';

const trace = 'TRACE_DIVKIT';

const isTraceEnabled =
    bool.hasEnvironment(trace) && bool.fromEnvironment(trace);

void traceEvent(String name, {Map<dynamic, dynamic>? args}) {
  if (isTraceEnabled) {
    Timeline.instantSync(name, arguments: args);
  }
}

T traceFunc<T>(
  String name,
  T Function() func,
) {
  if (isTraceEnabled) {
    final task = TimelineTask()..start(name);
    try {
      final result = func();
      return result;
    } finally {
      task.finish();
    }
  } else {
    return func();
  }
}

Future<T> traceAsyncFunc<T>(
  String name,
  Future<T> Function() func,
) async {
  if (isTraceEnabled) {
    final task = TimelineTask()..start(name);
    try {
      final result = await func();
      return result;
    } finally {
      task.finish();
    }
  } else {
    return func();
  }
}
