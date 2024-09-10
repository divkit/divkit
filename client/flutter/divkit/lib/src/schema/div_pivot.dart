// Generated code. Do not modify.

import 'package:divkit/src/schema/div_pivot_fixed.dart';
import 'package:divkit/src/schema/div_pivot_percentage.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivPivot extends Preloadable with EquatableMixin {
  final Preloadable value;
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
      "Type ${value.runtimeType.toString()} is not generalized in DivPivot",
    );
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

  bool get isDivPivotFixed => _index == 0;

  bool get isDivPivotPercentage => _index == 1;

  @override
  Future<void> preload(Map<String, dynamic> context) => value.preload(context);

  static DivPivot? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivPivotFixed.type:
          return DivPivot.divPivotFixed(
            DivPivotFixed.fromJson(json)!,
          );
        case DivPivotPercentage.type:
          return DivPivot.divPivotPercentage(
            DivPivotPercentage.fromJson(json)!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivPivot?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivPivotFixed.type:
          return DivPivot.divPivotFixed(
            (await DivPivotFixed.parse(json))!,
          );
        case DivPivotPercentage.type:
          return DivPivot.divPivotPercentage(
            (await DivPivotPercentage.parse(json))!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
