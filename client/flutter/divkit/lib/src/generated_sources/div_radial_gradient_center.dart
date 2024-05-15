// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'div_radial_gradient_fixed_center.dart';
import 'div_radial_gradient_relative_center.dart';

class DivRadialGradientCenter with EquatableMixin {
  const DivRadialGradientCenter(Object value) : _value = value;

  final Object _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  Object get value {
    final value = _value;
    if (value is DivRadialGradientFixedCenter) {
      return value;
    }
    if (value is DivRadialGradientRelativeCenter) {
      return value;
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivRadialGradientCenter");
  }

  T map<T>({
    required T Function(DivRadialGradientFixedCenter)
        divRadialGradientFixedCenter,
    required T Function(DivRadialGradientRelativeCenter)
        divRadialGradientRelativeCenter,
  }) {
    final value = _value;
    if (value is DivRadialGradientFixedCenter) {
      return divRadialGradientFixedCenter(value);
    }
    if (value is DivRadialGradientRelativeCenter) {
      return divRadialGradientRelativeCenter(value);
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivRadialGradientCenter");
  }

  T maybeMap<T>({
    T Function(DivRadialGradientFixedCenter)? divRadialGradientFixedCenter,
    T Function(DivRadialGradientRelativeCenter)?
        divRadialGradientRelativeCenter,
    required T Function() orElse,
  }) {
    final value = _value;
    if (value is DivRadialGradientFixedCenter &&
        divRadialGradientFixedCenter != null) {
      return divRadialGradientFixedCenter(value);
    }
    if (value is DivRadialGradientRelativeCenter &&
        divRadialGradientRelativeCenter != null) {
      return divRadialGradientRelativeCenter(value);
    }
    return orElse();
  }

  const DivRadialGradientCenter.divRadialGradientFixedCenter(
    DivRadialGradientFixedCenter value,
  ) : _value = value;

  const DivRadialGradientCenter.divRadialGradientRelativeCenter(
    DivRadialGradientRelativeCenter value,
  ) : _value = value;

  static DivRadialGradientCenter? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivRadialGradientFixedCenter.type:
        return DivRadialGradientCenter(
            DivRadialGradientFixedCenter.fromJson(json)!);
      case DivRadialGradientRelativeCenter.type:
        return DivRadialGradientCenter(
            DivRadialGradientRelativeCenter.fromJson(json)!);
    }
    return null;
  }
}
