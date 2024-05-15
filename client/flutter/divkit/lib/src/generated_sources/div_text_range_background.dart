// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'div_solid_background.dart';

class DivTextRangeBackground with EquatableMixin {
  const DivTextRangeBackground(Object value) : _value = value;

  final Object _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  Object get value {
    final value = _value;
    if (value is DivSolidBackground) {
      return value;
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivTextRangeBackground");
  }

  T map<T>({
    required T Function(DivSolidBackground) divSolidBackground,
  }) {
    final value = _value;
    if (value is DivSolidBackground) {
      return divSolidBackground(value);
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivTextRangeBackground");
  }

  T maybeMap<T>({
    T Function(DivSolidBackground)? divSolidBackground,
    required T Function() orElse,
  }) {
    final value = _value;
    if (value is DivSolidBackground && divSolidBackground != null) {
      return divSolidBackground(value);
    }
    return orElse();
  }

  const DivTextRangeBackground.divSolidBackground(
    DivSolidBackground value,
  ) : _value = value;

  static DivTextRangeBackground? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivSolidBackground.type:
        return DivTextRangeBackground(DivSolidBackground.fromJson(json)!);
    }
    return null;
  }
}
