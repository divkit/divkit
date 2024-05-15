// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_action.dart';

class DivTrigger with EquatableMixin {
  const DivTrigger({
    required this.actions,
    required this.condition,
    this.mode = const ValueExpression(DivTriggerMode.onCondition),
  });

  // at least 1 elements
  final List<DivAction> actions;

  final Expression<bool> condition;
  // default value: DivTriggerMode.onCondition
  final Expression<DivTriggerMode> mode;

  @override
  List<Object?> get props => [
        actions,
        condition,
        mode,
      ];

  static DivTrigger? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivTrigger(
      actions: safeParseObj(
        (json['actions'] as List<dynamic>)
            .map(
              (v) => safeParseObj(
                DivAction.fromJson(v),
              )!,
            )
            .toList(),
      )!,
      condition: safeParseBoolExpr(
        json['condition'],
      )!,
      mode: safeParseStrEnumExpr(
        json['mode'],
        parse: DivTriggerMode.fromJson,
        fallback: DivTriggerMode.onCondition,
      )!,
    );
  }
}

enum DivTriggerMode {
  onCondition('on_condition'),
  onVariable('on_variable');

  final String value;

  const DivTriggerMode(this.value);

  T map<T>({
    required T Function() onCondition,
    required T Function() onVariable,
  }) {
    switch (this) {
      case DivTriggerMode.onCondition:
        return onCondition();
      case DivTriggerMode.onVariable:
        return onVariable();
    }
  }

  T maybeMap<T>({
    T Function()? onCondition,
    T Function()? onVariable,
    required T Function() orElse,
  }) {
    switch (this) {
      case DivTriggerMode.onCondition:
        return onCondition?.call() ?? orElse();
      case DivTriggerMode.onVariable:
        return onVariable?.call() ?? orElse();
    }
  }

  static DivTriggerMode? fromJson(String? json) {
    if (json == null) {
      return null;
    }
    switch (json) {
      case 'on_condition':
        return DivTriggerMode.onCondition;
      case 'on_variable':
        return DivTriggerMode.onVariable;
    }
    return null;
  }
}
