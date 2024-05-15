// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'div_fixed_size.dart';
import 'div_radial_gradient_relative_radius.dart';

class DivRadialGradientRadius with EquatableMixin {
  const DivRadialGradientRadius(Object value) : _value = value;

  final Object _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  Object get value {
    final value = _value;
    if (value is DivFixedSize) {
      return value;
    }
    if (value is DivRadialGradientRelativeRadius) {
      return value;
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivRadialGradientRadius");
  }

  T map<T>({
    required T Function(DivFixedSize) divFixedSize,
    required T Function(DivRadialGradientRelativeRadius)
        divRadialGradientRelativeRadius,
  }) {
    final value = _value;
    if (value is DivFixedSize) {
      return divFixedSize(value);
    }
    if (value is DivRadialGradientRelativeRadius) {
      return divRadialGradientRelativeRadius(value);
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivRadialGradientRadius");
  }

  T maybeMap<T>({
    T Function(DivFixedSize)? divFixedSize,
    T Function(DivRadialGradientRelativeRadius)?
        divRadialGradientRelativeRadius,
    required T Function() orElse,
  }) {
    final value = _value;
    if (value is DivFixedSize && divFixedSize != null) {
      return divFixedSize(value);
    }
    if (value is DivRadialGradientRelativeRadius &&
        divRadialGradientRelativeRadius != null) {
      return divRadialGradientRelativeRadius(value);
    }
    return orElse();
  }

  const DivRadialGradientRadius.divFixedSize(
    DivFixedSize value,
  ) : _value = value;

  const DivRadialGradientRadius.divRadialGradientRelativeRadius(
    DivRadialGradientRelativeRadius value,
  ) : _value = value;

  static DivRadialGradientRadius? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivFixedSize.type:
        return DivRadialGradientRadius(DivFixedSize.fromJson(json)!);
      case DivRadialGradientRelativeRadius.type:
        return DivRadialGradientRadius(
            DivRadialGradientRelativeRadius.fromJson(json)!);
    }
    return null;
  }
}
