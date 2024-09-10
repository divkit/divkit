// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Controls the timer.
class DivActionTimer extends Preloadable with EquatableMixin {
  const DivActionTimer({
    required this.action,
    required this.id,
  });

  static const type = "timer";

  /// Defines timer action:
  /// • `start`- starts the timer when stopped, does onStart action;
  /// • `stop`- stops timer, resets the time, does onEnd action;
  /// • `pause`- pause timer, preserves current time;
  /// • `resume`- starts timer from paused state, restores saved time;
  /// • `cancel`- stops timer, resets its state, does onInterrupt action;
  /// • `reset`- cancels timer and starts it again.
  final Expression<DivActionTimerAction> action;

  /// Timer identifier.
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
        action: safeParseStrEnumExpr(
          json['action'],
          parse: DivActionTimerAction.fromJson,
        )!,
        id: safeParseStrExpr(
          json['id']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivActionTimer?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivActionTimer(
        action: (await safeParseStrEnumExprAsync(
          json['action'],
          parse: DivActionTimerAction.fromJson,
        ))!,
        id: (await safeParseStrExprAsync(
          json['id']?.toString(),
        ))!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {
    try {
      await action.preload(context);
      await id.preload(context);
    } catch (e) {
      return;
    }
  }
}

enum DivActionTimerAction implements Preloadable {
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

  @override
  Future<void> preload(Map<String, dynamic> context) async {}

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
    } catch (e) {
      return null;
    }
  }

  static Future<DivActionTimerAction?> parse(
    String? json,
  ) async {
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
    } catch (e) {
      return null;
    }
  }
}
