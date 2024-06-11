// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_action.dart';

class DivTimer with EquatableMixin {
  const DivTimer({
    this.duration = const ValueExpression(0),
    this.endActions,
    required this.id,
    this.tickActions,
    this.tickInterval,
    this.valueVariable,
  });

  // constraint: number >= 0; default value: 0
  final Expression<int> duration;

  final List<DivAction>? endActions;

  final String id;

  final List<DivAction>? tickActions;
  // constraint: number > 0
  final Expression<int>? tickInterval;

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

  static DivTimer? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
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
                )!),
      ),
      id: safeParseStr(
        json['id']?.toString(),
      )!,
      tickActions: safeParseObj(
        safeListMap(
            json['tick_actions'],
            (v) => safeParseObj(
                  DivAction.fromJson(v),
                )!),
      ),
      tickInterval: safeParseIntExpr(
        json['tick_interval'],
      ),
      valueVariable: safeParseStr(
        json['value_variable']?.toString(),
      ),
    );
  }
}
