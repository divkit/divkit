// Generated code. Do not modify.

import 'package:divkit/src/schema/div.dart';
import 'package:divkit/src/schema/div_timer.dart';
import 'package:divkit/src/schema/div_transition_selector.dart';
import 'package:divkit/src/schema/div_trigger.dart';
import 'package:divkit/src/schema/div_variable.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Root structure.
class DivData with EquatableMixin {
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
  final Arr<DivDataState> states;

  /// List of timers.
  final Arr<DivTimer>? timers;

  /// Events that trigger transition animations.
  // default value: DivTransitionSelector.none
  final Expression<DivTransitionSelector> transitionAnimationSelector;

  /// Triggers for changing variables.
  final Arr<DivTrigger>? variableTriggers;

  /// Declaration of variables that can be used in an element.
  final Arr<DivVariable>? variables;

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
    Arr<DivDataState>? states,
    Arr<DivTimer>? Function()? timers,
    Expression<DivTransitionSelector>? transitionAnimationSelector,
    Arr<DivTrigger>? Function()? variableTriggers,
    Arr<DivVariable>? Function()? variables,
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
        logId: reqProp<String>(
          safeParseStr(
            json['log_id'],
          ),
          name: 'log_id',
        ),
        states: reqProp<Arr<DivDataState>>(
          safeParseObjects(
            json['states'],
            (v) => reqProp<DivDataState>(
              safeParseObject(
                v,
                parse: DivDataState.fromJson,
              ),
            ),
          ),
          name: 'states',
        ),
        timers: safeParseObjects(
          json['timers'],
          (v) => reqProp<DivTimer>(
            safeParseObject(
              v,
              parse: DivTimer.fromJson,
            ),
          ),
        ),
        transitionAnimationSelector: reqVProp<DivTransitionSelector>(
          safeParseStrEnumExpr(
            json['transition_animation_selector'],
            parse: DivTransitionSelector.fromJson,
            fallback: DivTransitionSelector.none,
          ),
          name: 'transition_animation_selector',
        ),
        variableTriggers: safeParseObjects(
          json['variable_triggers'],
          (v) => reqProp<DivTrigger>(
            safeParseObject(
              v,
              parse: DivTrigger.fromJson,
            ),
          ),
        ),
        variables: safeParseObjects(
          json['variables'],
          (v) => reqProp<DivVariable>(
            safeParseObject(
              v,
              parse: DivVariable.fromJson,
            ),
          ),
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}

class DivDataState with EquatableMixin {
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
        div: reqProp<Div>(
          safeParseObject(
            json['div'],
            parse: Div.fromJson,
          ),
          name: 'div',
        ),
        stateId: reqProp<int>(
          safeParseInt(
            json['state_id'],
          ),
          name: 'state_id',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
