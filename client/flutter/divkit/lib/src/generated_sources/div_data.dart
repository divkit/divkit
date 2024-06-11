// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div.dart';
import 'div_timer.dart';
import 'div_transition_selector.dart';
import 'div_trigger.dart';
import 'div_variable.dart';

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

  final String logId;
  // at least 1 elements
  final List<DivDataState> states;

  final List<DivTimer>? timers;
  // default value: DivTransitionSelector.none
  final Expression<DivTransitionSelector> transitionAnimationSelector;

  final List<DivTrigger>? variableTriggers;

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

  static DivData? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivData(
      logId: safeParseStr(
        json['log_id']?.toString(),
      )!,
      states: safeParseObj(
        safeListMap(
            json['states'],
            (v) => safeParseObj(
                  DivDataState.fromJson(v),
                )!),
      )!,
      timers: safeParseObj(
        safeListMap(
            json['timers'],
            (v) => safeParseObj(
                  DivTimer.fromJson(v),
                )!),
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
                )!),
      ),
      variables: safeParseObj(
        safeListMap(
            json['variables'],
            (v) => safeParseObj(
                  DivVariable.fromJson(v),
                )!),
      ),
    );
  }
}

class DivDataState with EquatableMixin {
  const DivDataState({
    required this.div,
    required this.stateId,
  });

  final Div div;

  final int stateId;

  @override
  List<Object?> get props => [
        div,
        stateId,
      ];

  static DivDataState? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivDataState(
      div: safeParseObj(
        Div.fromJson(json['div']),
      )!,
      stateId: safeParseInt(
        json['state_id'],
      )!,
    );
  }
}
