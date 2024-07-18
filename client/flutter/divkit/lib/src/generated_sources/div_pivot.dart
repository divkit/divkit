// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/generated_sources/div_pivot_fixed.dart';
import 'package:divkit/src/generated_sources/div_pivot_percentage.dart';

class DivPivot with EquatableMixin {
  final Object value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivPivotFixed) divPivotFixed,
    required T Function(DivPivotPercentage) divPivotPercentage,
  }) {
    switch (_index) {
      case 0:
        return divPivotFixed(
          value as DivPivotFixed,
        );
      case 1:
        return divPivotPercentage(
          value as DivPivotPercentage,
        );
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivPivot");
  }

  T maybeMap<T>({
    T Function(DivPivotFixed)? divPivotFixed,
    T Function(DivPivotPercentage)? divPivotPercentage,
    required T Function() orElse,
  }) {
    switch (_index) {
      case 0:
        if (divPivotFixed != null) {
          return divPivotFixed(
            value as DivPivotFixed,
          );
        }
        break;
      case 1:
        if (divPivotPercentage != null) {
          return divPivotPercentage(
            value as DivPivotPercentage,
          );
        }
        break;
    }
    return orElse();
  }

  const DivPivot.divPivotFixed(
    DivPivotFixed obj,
  )   : value = obj,
        _index = 0;

  const DivPivot.divPivotPercentage(
    DivPivotPercentage obj,
  )   : value = obj,
        _index = 1;

  static DivPivot? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivPivotFixed.type:
        return DivPivot.divPivotFixed(DivPivotFixed.fromJson(json)!);
      case DivPivotPercentage.type:
        return DivPivot.divPivotPercentage(DivPivotPercentage.fromJson(json)!);
    }
    return null;
  }
}
