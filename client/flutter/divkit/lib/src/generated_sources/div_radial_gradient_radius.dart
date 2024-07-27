// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/generated_sources/div_fixed_size.dart';
import 'package:divkit/src/generated_sources/div_radial_gradient_relative_radius.dart';

class DivRadialGradientRadius with EquatableMixin {
  final Object value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivFixedSize) divFixedSize,
    required T Function(DivRadialGradientRelativeRadius)
        divRadialGradientRelativeRadius,
  }) {
    switch (_index) {
      case 0:
        return divFixedSize(
          value as DivFixedSize,
        );
      case 1:
        return divRadialGradientRelativeRadius(
          value as DivRadialGradientRelativeRadius,
        );
    }
    throw Exception(
      "Type ${value.runtimeType.toString()} is not generalized in DivRadialGradientRadius",
    );
  }

  T maybeMap<T>({
    T Function(DivFixedSize)? divFixedSize,
    T Function(DivRadialGradientRelativeRadius)?
        divRadialGradientRelativeRadius,
    required T Function() orElse,
  }) {
    switch (_index) {
      case 0:
        if (divFixedSize != null) {
          return divFixedSize(
            value as DivFixedSize,
          );
        }
        break;
      case 1:
        if (divRadialGradientRelativeRadius != null) {
          return divRadialGradientRelativeRadius(
            value as DivRadialGradientRelativeRadius,
          );
        }
        break;
    }
    return orElse();
  }

  const DivRadialGradientRadius.divFixedSize(
    DivFixedSize obj,
  )   : value = obj,
        _index = 0;

  const DivRadialGradientRadius.divRadialGradientRelativeRadius(
    DivRadialGradientRelativeRadius obj,
  )   : value = obj,
        _index = 1;

  static DivRadialGradientRadius? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivFixedSize.type:
          return DivRadialGradientRadius.divFixedSize(
            DivFixedSize.fromJson(json)!,
          );
        case DivRadialGradientRelativeRadius.type:
          return DivRadialGradientRadius.divRadialGradientRelativeRadius(
            DivRadialGradientRelativeRadius.fromJson(json)!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
