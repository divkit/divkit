// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Timer.
class DivTimer extends Resolvable with EquatableMixin {
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
  final Arr<DivAction>? endActions;

  /// Timer ID. Must be unique. Used when calling actions for the selected timer, for example: start, stop.
  final String id;

  /// Actions that are performed on each count of the timer.
  final Arr<DivAction>? tickActions;

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
    Arr<DivAction>? Function()? endActions,
    String? id,
    Arr<DivAction>? Function()? tickActions,
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
        duration: reqVProp<int>(
          safeParseIntExpr(
            json['duration'],
            fallback: 0,
          ),
          name: 'duration',
        ),
        endActions: safeParseObjects(
          json['end_actions'],
          (v) => reqProp<DivAction>(
            safeParseObject(
              v,
              parse: DivAction.fromJson,
            ),
          ),
        ),
        id: reqProp<String>(
          safeParseStr(
            json['id'],
          ),
          name: 'id',
        ),
        tickActions: safeParseObjects(
          json['tick_actions'],
          (v) => reqProp<DivAction>(
            safeParseObject(
              v,
              parse: DivAction.fromJson,
            ),
          ),
        ),
        tickInterval: safeParseIntExpr(
          json['tick_interval'],
        ),
        valueVariable: safeParseStr(
          json['value_variable'],
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivTimer resolve(DivVariableContext context) {
    duration.resolve(context);
    tryResolveList(endActions, (v) => v.resolve(context));
    tryResolveList(tickActions, (v) => v.resolve(context));
    tickInterval?.resolve(context);
    return this;
  }
}
