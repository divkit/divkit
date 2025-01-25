// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// A trigger that causes an action when activated.
class DivTrigger extends Resolvable with EquatableMixin {
  const DivTrigger({
    required this.actions,
    required this.condition,
    this.mode = const ValueExpression(DivTriggerMode.onCondition),
  });

  /// Action when a trigger is activated.
  // at least 1 elements
  final List<DivAction> actions;

  /// Condition for activating a trigger. For example, `liked && subscribed`.
  final Expression<bool> condition;

  /// Trigger activation mode:
  /// • `on_condition` — a trigger is activated when the condition changes from `false` to `true`;
  /// • `on_variable` — a trigger is activated when the condition is met and the variable value changes.
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

  static DivTrigger? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivTrigger(
        actions: safeParseObj(
          safeListMap(
            json['actions'],
            (v) => safeParseObj(
              DivAction.fromJson(v),
            )!,
          ),
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
    } catch (e) {
      return null;
    }
  }

  @override
  DivTrigger resolve(DivVariableContext context) {
    safeListResolve(actions, (v) => v.resolve(context));
    condition.resolve(context);
    mode.resolve(context);
    return this;
  }
}

enum DivTriggerMode implements Resolvable {
  onCondition('on_condition'),
  onVariable('on_variable');

  final String value;

  const DivTriggerMode(this.value);
  bool get isOnCondition => this == onCondition;

  bool get isOnVariable => this == onVariable;

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

  static DivTriggerMode? fromJson(
    String? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json) {
        case 'on_condition':
          return DivTriggerMode.onCondition;
        case 'on_variable':
          return DivTriggerMode.onVariable;
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  @override
  DivTriggerMode resolve(DivVariableContext context) => this;
}
