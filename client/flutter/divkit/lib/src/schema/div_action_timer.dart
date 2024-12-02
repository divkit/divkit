// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Controls the timer.
class DivActionTimer extends Resolvable with EquatableMixin {
  const DivActionTimer({
    required this.action,
    required this.id,
  });

  static const type = "timer";

  /// Timer actions:
  /// • `start` — starts the timer from a stopped state
  /// • `stop`— stops the timer and performs the `onEnd` action
  /// • `pause` — pauses the timer, saves the current time
  /// • `resume` — restarts the timer after a pause
  /// • `cancel` — interrupts the timer, resets the time
  /// • `reset` — cancels the timer, then starts it again
  final Expression<DivActionTimerAction> action;

  /// Timer ID.
  final Expression<String> id;

  @override
  List<Object?> get props => [
        action,
        id,
      ];

  DivActionTimer copyWith({
    Expression<DivActionTimerAction>? action,
    Expression<String>? id,
  }) =>
      DivActionTimer(
        action: action ?? this.action,
        id: id ?? this.id,
      );

  static DivActionTimer? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionTimer(
        action: reqVProp<DivActionTimerAction>(
          safeParseStrEnumExpr(
            json['action'],
            parse: DivActionTimerAction.fromJson,
          ),
          name: 'action',
        ),
        id: reqVProp<String>(
          safeParseStrExpr(
            json['id'],
          ),
          name: 'id',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivActionTimer resolve(DivVariableContext context) {
    action.resolve(context);
    id.resolve(context);
    return this;
  }
}

enum DivActionTimerAction implements Resolvable {
  start('start'),
  stop('stop'),
  pause('pause'),
  resume('resume'),
  cancel('cancel'),
  reset('reset');

  final String value;

  const DivActionTimerAction(this.value);
  bool get isStart => this == start;

  bool get isStop => this == stop;

  bool get isPause => this == pause;

  bool get isResume => this == resume;

  bool get isCancel => this == cancel;

  bool get isReset => this == reset;

  T map<T>({
    required T Function() start,
    required T Function() stop,
    required T Function() pause,
    required T Function() resume,
    required T Function() cancel,
    required T Function() reset,
  }) {
    switch (this) {
      case DivActionTimerAction.start:
        return start();
      case DivActionTimerAction.stop:
        return stop();
      case DivActionTimerAction.pause:
        return pause();
      case DivActionTimerAction.resume:
        return resume();
      case DivActionTimerAction.cancel:
        return cancel();
      case DivActionTimerAction.reset:
        return reset();
    }
  }

  T maybeMap<T>({
    T Function()? start,
    T Function()? stop,
    T Function()? pause,
    T Function()? resume,
    T Function()? cancel,
    T Function()? reset,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivActionTimerAction.start:
        return start?.call() ?? orElse();
      case DivActionTimerAction.stop:
        return stop?.call() ?? orElse();
      case DivActionTimerAction.pause:
        return pause?.call() ?? orElse();
      case DivActionTimerAction.resume:
        return resume?.call() ?? orElse();
      case DivActionTimerAction.cancel:
        return cancel?.call() ?? orElse();
      case DivActionTimerAction.reset:
        return reset?.call() ?? orElse();
    }
  }

  static DivActionTimerAction? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'start':
          return DivActionTimerAction.start;
        case 'stop':
          return DivActionTimerAction.stop;
        case 'pause':
          return DivActionTimerAction.pause;
        case 'resume':
          return DivActionTimerAction.resume;
        case 'cancel':
          return DivActionTimerAction.cancel;
        case 'reset':
          return DivActionTimerAction.reset;
      }
      return null;
    } catch (e, st) {
      logger.warning(
        "Invalid type of DivActionTimerAction: $json",
        error: e,
        stackTrace: st,
      );
      return null;
    }
  }

  @override
  DivActionTimerAction resolve(DivVariableContext context) => this;
}
