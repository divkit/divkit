// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'div_change_bounds_transition.dart';
import 'div_change_set_transition.dart';

class DivChangeTransition with EquatableMixin {
  const DivChangeTransition(Object value) : _value = value;

  final Object _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  Object get value {
    final value = _value;
    if (value is DivChangeBoundsTransition) {
      return value;
    }
    if (value is DivChangeSetTransition) {
      return value;
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivChangeTransition");
  }

  T map<T>({
    required T Function(DivChangeBoundsTransition) divChangeBoundsTransition,
    required T Function(DivChangeSetTransition) divChangeSetTransition,
  }) {
    final value = _value;
    if (value is DivChangeBoundsTransition) {
      return divChangeBoundsTransition(value);
    }
    if (value is DivChangeSetTransition) {
      return divChangeSetTransition(value);
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivChangeTransition");
  }

  T maybeMap<T>({
    T Function(DivChangeBoundsTransition)? divChangeBoundsTransition,
    T Function(DivChangeSetTransition)? divChangeSetTransition,
    required T Function() orElse,
  }) {
    final value = _value;
    if (value is DivChangeBoundsTransition &&
        divChangeBoundsTransition != null) {
      return divChangeBoundsTransition(value);
    }
    if (value is DivChangeSetTransition && divChangeSetTransition != null) {
      return divChangeSetTransition(value);
    }
    return orElse();
  }

  const DivChangeTransition.divChangeBoundsTransition(
    DivChangeBoundsTransition value,
  ) : _value = value;

  const DivChangeTransition.divChangeSetTransition(
    DivChangeSetTransition value,
  ) : _value = value;

  static DivChangeTransition? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivChangeSetTransition.type:
        return DivChangeTransition(DivChangeSetTransition.fromJson(json)!);
      case DivChangeBoundsTransition.type:
        return DivChangeTransition(DivChangeBoundsTransition.fromJson(json)!);
    }
    return null;
  }
}
