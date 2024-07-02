// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/generated_sources/div_solid_background.dart';

class DivTextRangeBackground with EquatableMixin {
  final Object value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivSolidBackground) divSolidBackground,
  }) {
    switch (_index) {
      case 0:
        return divSolidBackground(
          value as DivSolidBackground,
        );
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivTextRangeBackground");
  }

  T maybeMap<T>({
    T Function(DivSolidBackground)? divSolidBackground,
    required T Function() orElse,
  }) {
    switch (_index) {
      case 0:
        if (divSolidBackground != null) {
          return divSolidBackground(
            value as DivSolidBackground,
          );
        }
        break;
    }
    return orElse();
  }

  const DivTextRangeBackground.divSolidBackground(
    DivSolidBackground obj,
  )   : value = obj,
        _index = 0;

  static DivTextRangeBackground? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivSolidBackground.type:
        return DivTextRangeBackground.divSolidBackground(
            DivSolidBackground.fromJson(json)!);
    }
    return null;
  }
}
