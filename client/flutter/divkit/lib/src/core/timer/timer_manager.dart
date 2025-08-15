import 'package:divkit/divkit.dart';
import 'package:divkit/src/core/timer/timer_scheduler.dart';

class DefaultDivTimerManager extends DivTimerManager {
  final DivContext divContext;
  DivTimerScheduler? scheduler;

  DefaultDivTimerManager({
    required this.divContext,
  });

  DivTimerManager init({
    Iterable<DivTimerModel>? timers,
  }) {
    if (timers != null) {
      for (final timer in timers) {
        timer.init(divContext);
      }
    }

    scheduler = DivTimerScheduler(timers: timers);
    return this;
  }

  @override
  void start(String id) => scheduler?.start(id);

  @override
  void stop(String id) => scheduler?.stop(id);

  @override
  void resume(String id) => scheduler?.resume(id);

  @override
  void pause(String id) => scheduler?.pause(id);

  @override
  void cancel(String id) => scheduler?.cancel(id);

  @override
  void reset(String id) => scheduler?.reset(id);

  @override
  Future<void> dispose() async {
    final timers = scheduler?.timers;
    if (timers != null) {
      for (var timer in timers) {
        timer.dispose();
      }
    }
  }
}
