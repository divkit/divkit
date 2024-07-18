// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_action.dart';

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

  DivTrigger copyWith({
    List<DivAction>? actions,
    Expression<bool>? condition,
    Expression<DivTriggerMode>? mode,
  }) =>
      DivTrigger(
        actions: actions ?? this.actions,
        condition: condition ?? this.condition,
        mode: mode ?? this.mode,
      );

  static DivTrigger? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivTrigger(
      actions: safeParseObj(
        safeListMap(
            json['actions'],
            (v) => safeParseObj(
                  DivAction.fromJson(v),
                )!),
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
