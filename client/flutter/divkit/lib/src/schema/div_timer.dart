// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Timer.
class DivTimer extends Preloadable with EquatableMixin {
  const DivTimer({
    this.duration = const ValueExpression(0),
    this.endActions,
    required this.id,
    this.tickActions,
    this.tickInterval,
    this.valueVariable,
  });

  /// Timer duration in milliseconds. If the parameter is `0` or not specified, the timer runs indefinitely (until the timer stop event occurs).
  // constraint: number >= 0; default value: 0
  final Expression<int> duration;

  /// Actions performed when the timer ends: when the timer has counted to the `duration` value or the `div-action://timer?action=stop&id=<id>` command has been received.
  final List<DivAction>? endActions;

  /// Timer ID. Must be unique. Used when calling actions for the selected timer, for example: start, stop.
  final String id;

  /// Actions that are performed on each count of the timer.
  final List<DivAction>? tickActions;

  /// Duration of time intervals in milliseconds between counts. If the parameter is not specified, the timer counts down from `0` to `duration` without calling `tick_actions`.
  // constraint: number > 0
  final Expression<int>? tickInterval;

  /// Name of the variable where the current timer value is stored. Updated on each count or when the timer commands are called (start, stop, and so on), except the cancel command.
  final String? valueVariable;

  @override
  List<Object?> get props => [
        duration,
        endActions,
        id,
        tickActions,
        tickInterval,
        valueVariable,
      ];

  DivTimer copyWith({
    Expression<int>? duration,
    List<DivAction>? Function()? endActions,
    String? id,
    List<DivAction>? Function()? tickActions,
    Expression<int>? Function()? tickInterval,
    String? Function()? valueVariable,
  }) =>
      DivTimer(
        duration: duration ?? this.duration,
        endActions: endActions != null ? endActions.call() : this.endActions,
        id: id ?? this.id,
        tickActions:
            tickActions != null ? tickActions.call() : this.tickActions,
        tickInterval:
            tickInterval != null ? tickInterval.call() : this.tickInterval,
        valueVariable:
            valueVariable != null ? valueVariable.call() : this.valueVariable,
      );

  static DivTimer? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivTimer(
        duration: safeParseIntExpr(
          json['duration'],
          fallback: 0,
        )!,
        endActions: safeParseObj(
          safeListMap(
            json['end_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        id: safeParseStr(
          json['id']?.toString(),
        )!,
        tickActions: safeParseObj(
          safeListMap(
            json['tick_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        tickInterval: safeParseIntExpr(
          json['tick_interval'],
        ),
        valueVariable: safeParseStr(
          json['value_variable']?.toString(),
        ),
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivTimer?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivTimer(
        duration: (await safeParseIntExprAsync(
          json['duration'],
          fallback: 0,
        ))!,
        endActions: await safeParseObjAsync(
          await safeListMapAsync(
            json['end_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        id: (await safeParseStrAsync(
          json['id']?.toString(),
        ))!,
        tickActions: await safeParseObjAsync(
          await safeListMapAsync(
            json['tick_actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
        ),
        tickInterval: await safeParseIntExprAsync(
          json['tick_interval'],
        ),
        valueVariable: await safeParseStrAsync(
          json['value_variable']?.toString(),
        ),
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
      await duration.preload(context);
      await safeFuturesWait(endActions, (v) => v.preload(context));
      await safeFuturesWait(tickActions, (v) => v.preload(context));
      await tickInterval?.preload(context);
    } catch (e) {
      return;
    }
  }
}
