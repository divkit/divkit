import 'package:divkit/src/core/protocol/div_logger.dart';
import 'package:divkit/src/core/timer/timer.dart';

/// Empty storage, important for initialization.
final _empty = <String, DivTimerModel>{};

class DivTimerScheduler {
  final Map<String, DivTimerModel> _timers;

  Iterable<DivTimerModel> get timers => _timers.values;

  DivTimerScheduler({
    required Iterable<DivTimerModel>? timers,
  }) : _timers = timers != null
            ? Map.fromIterable(timers, key: (v) => v.id)
            : _empty;

  void start(String id) {
    if (_timers.containsKey(id)) {
      _timers[id]!.clockwork.start();
    } else {
      logger.error("Try `start` indefinite timer: $id");
    }
  }

  void stop(String id) {
    if (_timers.containsKey(id)) {
      _timers[id]!.clockwork.stop();
    } else {
      logger.error("Try `stop` indefinite timer: $id");
    }
  }

  void resume(String id) {
    if (_timers.containsKey(id)) {
      _timers[id]!.clockwork.resume();
    } else {
      logger.error("Try `resume` indefinite timer: $id");
    }
  }

  void pause(String id) {
    if (_timers.containsKey(id)) {
      _timers[id]!.clockwork.pause();
    } else {
      logger.error("Try `pause` indefinite timer: $id");
    }
  }

  void cancel(String id) {
    if (_timers.containsKey(id)) {
      _timers[id]!.clockwork.cancel();
    } else {
      logger.error("Try `cancel` indefinite timer: $id");
    }
  }

  void reset(String id) {
    if (_timers.containsKey(id)) {
      _timers[id]!.clockwork.reset();
    } else {
      logger.error("Try `reset` indefinite timer: $id");
    }
  }
}
