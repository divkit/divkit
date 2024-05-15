import 'dart:async' show Timer, Zone;

import 'package:clock/clock.dart';

/// Describes all the states in which [Clockwork] can be located.
enum ClockworkState {
  started,
  stopped,
  paused;

  bool get isStarted => this == started;

  bool get isStopped => this == stopped;

  bool get isPaused => this == paused;
}

/// [Timer] extension that adds the ability to [start], [stop], [resume], [pause],
/// [cancel], [reset] its execution.
///
/// There is main [duration] after which triggers [onEnd] and [interval] which triggers
/// [onTick] every time on end until main [duration] has expired.
///
/// If the last [interval] matches main [duration], then [onTick] will be called before [onEnd].
class Clockwork implements Timer {
  var _state = ClockworkState.stopped;

  /// The main _state of clockwork.
  ClockworkState get state => _state;

  /// The [Zone] where the callbacks will be run.
  final Zone _zone;

  /// The [Stopwatch] used to keep track of the elapsed time.
  Stopwatch _stopwatch = clock.stopwatch();

  /// The main timer.
  Timer? _timer;

  /// The interval ticker.
  Timer? _ticker;

  /// The callback to call when this clockwork starts.
  void Function()? _onStart;

  /// The callback to call when this clockwork paused.
  void Function(Duration elapsed)? _onPause;

  /// The callback to call when this clockwork reset.
  void Function()? _onReset;

  /// The callback to call when this clockwork's [duration] expires.
  void Function(Duration elapsed)? _onEnd;

  /// The callback to call when this clockwork's [interval] expires
  /// before the [duration] has expired.
  void Function(Duration elapsed)? _onTick;

  /// The number of intervals passed.
  int _intervals = 0;

  /// The number of planned ticks.
  int _targetIntervals = 0;

  /// The time this clockwork have been active.
  ///
  /// If the clockwork is paused, the elapsed time is also not computed anymore, so
  /// [elapsed] is always less than or equals to the [duration].
  Duration get elapsed => _stopwatch.elapsed;

  /// True if this [Timer] run.
  @override
  bool get isActive => _state.isStarted;

  @override
  int get tick => _intervals;

  Duration? _duration;

  /// The original duration this [Timer].
  Duration? get duration => _duration;

  Duration? _interval;

  /// The duration of one inner loop.
  Duration? get interval => _interval;

  /// Creates a new Clockwork.
  ///
  /// There is main [duration] after which triggers [onEnd] and [interval] which triggers
  /// [onTick] every time on end until main [duration] has expired.
  ///
  /// The clockwork is [stop]ped when created, and must be [start]ed manually.
  Clockwork({
    Duration? duration,
    Duration? interval,
    void Function()? onStart,
    void Function(Duration elapsed)? onPause,
    void Function()? onReset,
    void Function(Duration elapsed)? onTick,
    void Function(Duration elapsed)? onEnd,
  }) : _zone = Zone.current {
    update(duration: duration, interval: interval);

    if (onStart != null) {
      _onStart = _zone.bindCallback(onStart);
    }
    if (onPause != null) {
      _onPause = _zone.bindUnaryCallback(onPause);
    }
    if (onReset != null) {
      _onReset = _zone.bindCallback(onReset);
    }
    if (onTick != null) {
      _onTick = _zone.bindUnaryCallback(onTick);
    }
    if (onEnd != null) {
      _onEnd = _zone.bindUnaryCallback(onEnd);
    }
  }

  /// Full timer recreation!
  void _start() {
    if (_interval != null) {
      // Since the previous [interval] could be interrupted before the beginning of
      // the next one, then needs to make a current through [waitAfterPause],
      // and then each [interval].
      final waitAfterPause = _interval!.inMilliseconds != 0
          ? Duration(
              milliseconds: _interval!.inMilliseconds -
                  elapsed.inMilliseconds % _interval!.inMilliseconds,
            )
          : Duration.zero;

      _ticker?.cancel();
      _ticker = _zone.createTimer(waitAfterPause, () {
        _tick();
        _ticker = _zone.createPeriodicTimer(
          _interval!,
          (_) => _tick(),
        );
      });
    }

    if (_duration != null) {
      _timer?.cancel();
      _timer = _zone.createTimer(
        _duration! - _stopwatch.elapsed,
        () {
          _stopwatch.stop();

          if (_ticker != null) {
            // The duration coincided with the last interval.
            // So needs call [onTick] and increment [tick].
            if (_targetIntervals == _intervals + 1) _tick();
          }

          // Needs to stop the clockwork.
          stop();
        },
      );
    }

    _stopwatch.start();
  }

  void _tick() {
    _intervals++;
    _onTick?.call(elapsed);
  }

  /// Updates the clockwork triggers durations.
  ///
  /// Changes applied in [stopped] _state.
  void update({
    Duration? duration,
    Duration? interval,
  }) {
    _duration =
        (duration?.inMilliseconds ?? 0) <= 0 ? null : duration ?? _duration;
    _interval =
        (interval?.inMilliseconds ?? 0) <= 0 ? null : interval ?? _interval;
  }

  /// Starts the [stopped]clockwork.
  void start() {
    if (_state.isStopped) {
      _onStart?.call();
      _start();
      _state = ClockworkState.started;
    }

    if (_state.isStarted) {
      _intervals = 0;
      if (_duration != null && _interval != null) {
        _targetIntervals =
            _duration!.inMicroseconds ~/ _interval!.inMicroseconds;
      }
    }
  }

  /// Stops the [started] clockwork.
  void stop() {
    if (!_state.isStopped) {
      _onEnd?.call(elapsed);
      cancel();
    }
  }

  /// Resume the [stopped] clockwork.
  void resume() {
    if (_state.isPaused) {
      _start();

      _state = ClockworkState.started;
    }
  }

  /// Pauses the [started] clockwork.
  void pause() {
    if (_state.isStarted) {
      _stopwatch.stop();
      _timer?.cancel();
      _ticker?.cancel();
      _onPause?.call(elapsed);

      _state = ClockworkState.paused;
    }
  }

  /// Resets the clockwork.
  void reset() {
    _stopwatch = clock.stopwatch();
    _timer?.cancel();
    _ticker?.cancel();
    _intervals = 0;
    _onReset?.call();
    if (_duration != null && _interval != null) {
      _targetIntervals =
          _intervals + _duration!.inMicroseconds ~/ _interval!.inMicroseconds;
    }
    _start();

    _state = ClockworkState.started;
  }

  /// Cancels the clockwork.
  @override
  void cancel() {
    _stopwatch = clock.stopwatch();
    _ticker?.cancel();
    _ticker = null;
    _timer?.cancel();
    _timer = null;

    _state = ClockworkState.stopped;
  }

  /// Destroy clockwork.
  void dispose() => cancel();
}
