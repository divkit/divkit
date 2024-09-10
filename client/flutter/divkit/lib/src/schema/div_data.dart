// Generated code. Do not modify.

import 'package:divkit/src/schema/div.dart';
import 'package:divkit/src/schema/div_timer.dart';
import 'package:divkit/src/schema/div_transition_selector.dart';
import 'package:divkit/src/schema/div_trigger.dart';
import 'package:divkit/src/schema/div_variable.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Root structure.
class DivData extends Preloadable with EquatableMixin {
  const DivData({
    required this.logId,
    required this.states,
    this.timers,
    this.transitionAnimationSelector =
        const ValueExpression(DivTransitionSelector.none),
    this.variableTriggers,
    this.variables,
  });

  /// Logging ID.
  final String logId;

  /// A set of visual element states. Each element can have a few states with a different layout. The states are displayed strictly one by one and switched using [action](div-action.md).
  // at least 1 elements
  final List<DivDataState> states;

  /// List of timers.
  final List<DivTimer>? timers;

  /// Events that trigger transition animations.
  // default value: DivTransitionSelector.none
  final Expression<DivTransitionSelector> transitionAnimationSelector;

  /// Triggers for changing variables.
  final List<DivTrigger>? variableTriggers;

  /// Declaration of variables that can be used in an element.
  final List<DivVariable>? variables;

  @override
  List<Object?> get props => [
        logId,
        states,
        timers,
        transitionAnimationSelector,
        variableTriggers,
        variables,
      ];

  DivData copyWith({
    String? logId,
    List<DivDataState>? states,
    List<DivTimer>? Function()? timers,
    Expression<DivTransitionSelector>? transitionAnimationSelector,
    List<DivTrigger>? Function()? variableTriggers,
    List<DivVariable>? Function()? variables,
  }) =>
      DivData(
        logId: logId ?? this.logId,
        states: states ?? this.states,
        timers: timers != null ? timers.call() : this.timers,
        transitionAnimationSelector:
            transitionAnimationSelector ?? this.transitionAnimationSelector,
        variableTriggers: variableTriggers != null
            ? variableTriggers.call()
            : this.variableTriggers,
        variables: variables != null ? variables.call() : this.variables,
      );

  static DivData? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivData(
        logId: safeParseStr(
          json['log_id']?.toString(),
        )!,
        states: safeParseObj(
          safeListMap(
            json['states'],
            (v) => safeParseObj(
              DivDataState.fromJson(v),
            )!,
          ),
        )!,
        timers: safeParseObj(
          safeListMap(
            json['timers'],
            (v) => safeParseObj(
              DivTimer.fromJson(v),
            )!,
          ),
        ),
        transitionAnimationSelector: safeParseStrEnumExpr(
          json['transition_animation_selector'],
          parse: DivTransitionSelector.fromJson,
          fallback: DivTransitionSelector.none,
        )!,
        variableTriggers: safeParseObj(
          safeListMap(
            json['variable_triggers'],
            (v) => safeParseObj(
              DivTrigger.fromJson(v),
            )!,
          ),
        ),
        variables: safeParseObj(
          safeListMap(
            json['variables'],
            (v) => safeParseObj(
              DivVariable.fromJson(v),
            )!,
          ),
        ),
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivData?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivData(
        logId: (await safeParseStrAsync(
          json['log_id']?.toString(),
        ))!,
        states: (await safeParseObjAsync(
          await safeListMapAsync(
            json['states'],
            (v) => safeParseObj(
              DivDataState.fromJson(v),
            )!,
          ),
        ))!,
        timers: await safeParseObjAsync(
          await safeListMapAsync(
            json['timers'],
            (v) => safeParseObj(
              DivTimer.fromJson(v),
            )!,
          ),
        ),
        transitionAnimationSelector: (await safeParseStrEnumExprAsync(
          json['transition_animation_selector'],
          parse: DivTransitionSelector.fromJson,
          fallback: DivTransitionSelector.none,
        ))!,
        variableTriggers: await safeParseObjAsync(
          await safeListMapAsync(
            json['variable_triggers'],
            (v) => safeParseObj(
              DivTrigger.fromJson(v),
            )!,
          ),
        ),
        variables: await safeParseObjAsync(
          await safeListMapAsync(
            json['variables'],
            (v) => safeParseObj(
              DivVariable.fromJson(v),
            )!,
          ),
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
      await safeFuturesWait(states, (v) => v.preload(context));
      await safeFuturesWait(timers, (v) => v.preload(context));
      await transitionAnimationSelector.preload(context);
      await safeFuturesWait(variableTriggers, (v) => v.preload(context));
      await safeFuturesWait(variables, (v) => v.preload(context));
    } catch (e) {
      return;
    }
  }
}

class DivDataState extends Preloadable with EquatableMixin {
  const DivDataState({
    required this.div,
    required this.stateId,
  });

  /// Contents.
  final Div div;

  /// State ID.
  final int stateId;

  @override
  List<Object?> get props => [
        div,
        stateId,
      ];

  DivDataState copyWith({
    Div? div,
    int? stateId,
  }) =>
      DivDataState(
        div: div ?? this.div,
        stateId: stateId ?? this.stateId,
      );

  static DivDataState? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivDataState(
        div: safeParseObj(
          Div.fromJson(json['div']),
        )!,
        stateId: safeParseInt(
          json['state_id'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivDataState?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivDataState(
        div: (await safeParseObjAsync(
          Div.fromJson(json['div']),
        ))!,
        stateId: (await safeParseIntAsync(
          json['state_id'],
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
      await div.preload(context);
    } catch (e) {
      return;
    }
  }
}
