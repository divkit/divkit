// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'div_pivot_fixed.dart';
import 'div_pivot_percentage.dart';

class DivPivot with EquatableMixin {
  const DivPivot(Object value) : _value = value;

  final Object _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  Object get value {
    final value = _value;
    if (value is DivPivotFixed) {
      return value;
    }
    if (value is DivPivotPercentage) {
      return value;
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivPivot");
  }

  T map<T>({
    required T Function(DivPivotFixed) divPivotFixed,
    required T Function(DivPivotPercentage) divPivotPercentage,
  }) {
    final value = _value;
    if (value is DivPivotFixed) {
      return divPivotFixed(value);
    }
    if (value is DivPivotPercentage) {
      return divPivotPercentage(value);
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivPivot");
  }

  T maybeMap<T>({
    T Function(DivPivotFixed)? divPivotFixed,
    T Function(DivPivotPercentage)? divPivotPercentage,
    required T Function() orElse,
  }) {
    final value = _value;
    if (value is DivPivotFixed && divPivotFixed != null) {
      return divPivotFixed(value);
    }
    if (value is DivPivotPercentage && divPivotPercentage != null) {
      return divPivotPercentage(value);
    }
    return orElse();
  }

  const DivPivot.divPivotFixed(
    DivPivotFixed value,
  ) : _value = value;

  const DivPivot.divPivotPercentage(
    DivPivotPercentage value,
  ) : _value = value;

  static DivPivot? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivPivotFixed.type:
        return DivPivot(DivPivotFixed.fromJson(json)!);
      case DivPivotPercentage.type:
        return DivPivot(DivPivotPercentage.fromJson(json)!);
    }
    return null;
  }
}
