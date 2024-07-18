// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/generated_sources/div_change_bounds_transition.dart';
import 'package:divkit/src/generated_sources/div_change_set_transition.dart';

class DivChangeTransition with EquatableMixin {
  final Object value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivChangeBoundsTransition) divChangeBoundsTransition,
    required T Function(DivChangeSetTransition) divChangeSetTransition,
  }) {
    switch (_index) {
      case 0:
        return divChangeBoundsTransition(
          value as DivChangeBoundsTransition,
        );
      case 1:
        return divChangeSetTransition(
          value as DivChangeSetTransition,
        );
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivChangeTransition");
  }

  T maybeMap<T>({
    T Function(DivChangeBoundsTransition)? divChangeBoundsTransition,
    T Function(DivChangeSetTransition)? divChangeSetTransition,
    required T Function() orElse,
  }) {
    switch (_index) {
      case 0:
        if (divChangeBoundsTransition != null) {
          return divChangeBoundsTransition(
            value as DivChangeBoundsTransition,
          );
        }
        break;
      case 1:
        if (divChangeSetTransition != null) {
          return divChangeSetTransition(
            value as DivChangeSetTransition,
          );
        }
        break;
    }
    return orElse();
  }

  const DivChangeTransition.divChangeBoundsTransition(
    DivChangeBoundsTransition obj,
  )   : value = obj,
        _index = 0;

  const DivChangeTransition.divChangeSetTransition(
    DivChangeSetTransition obj,
  )   : value = obj,
        _index = 1;

  static DivChangeTransition? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivChangeBoundsTransition.type:
        return DivChangeTransition.divChangeBoundsTransition(
            DivChangeBoundsTransition.fromJson(json)!);
      case DivChangeSetTransition.type:
        return DivChangeTransition.divChangeSetTransition(
            DivChangeSetTransition.fromJson(json)!);
    }
    return null;
  }
}
