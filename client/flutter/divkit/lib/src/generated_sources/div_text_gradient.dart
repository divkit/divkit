// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'div_linear_gradient.dart';
import 'div_radial_gradient.dart';

class DivTextGradient with EquatableMixin {
  const DivTextGradient(Object value) : _value = value;

  final Object _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  Object get value {
    final value = _value;
    if (value is DivLinearGradient) {
      return value;
    }
    if (value is DivRadialGradient) {
      return value;
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivTextGradient");
  }

  T map<T>({
    required T Function(DivLinearGradient) divLinearGradient,
    required T Function(DivRadialGradient) divRadialGradient,
  }) {
    final value = _value;
    if (value is DivLinearGradient) {
      return divLinearGradient(value);
    }
    if (value is DivRadialGradient) {
      return divRadialGradient(value);
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivTextGradient");
  }

  T maybeMap<T>({
    T Function(DivLinearGradient)? divLinearGradient,
    T Function(DivRadialGradient)? divRadialGradient,
    required T Function() orElse,
  }) {
    final value = _value;
    if (value is DivLinearGradient && divLinearGradient != null) {
      return divLinearGradient(value);
    }
    if (value is DivRadialGradient && divRadialGradient != null) {
      return divRadialGradient(value);
    }
    return orElse();
  }

  const DivTextGradient.divLinearGradient(
    DivLinearGradient value,
  ) : _value = value;

  const DivTextGradient.divRadialGradient(
    DivRadialGradient value,
  ) : _value = value;

  static DivTextGradient? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivLinearGradient.type:
        return DivTextGradient(DivLinearGradient.fromJson(json)!);
      case DivRadialGradient.type:
        return DivTextGradient(DivRadialGradient.fromJson(json)!);
    }
    return null;
  }
}
