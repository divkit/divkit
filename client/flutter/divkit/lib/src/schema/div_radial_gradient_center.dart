// Generated code. Do not modify.

import 'package:divkit/src/schema/div_radial_gradient_fixed_center.dart';
import 'package:divkit/src/schema/div_radial_gradient_relative_center.dart';
import 'package:equatable/equatable.dart';

class DivRadialGradientCenter with EquatableMixin {
  final Object value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivRadialGradientFixedCenter)
        divRadialGradientFixedCenter,
    required T Function(DivRadialGradientRelativeCenter)
        divRadialGradientRelativeCenter,
  }) {
    switch (_index) {
      case 0:
        return divRadialGradientFixedCenter(
          value as DivRadialGradientFixedCenter,
        );
      case 1:
        return divRadialGradientRelativeCenter(
          value as DivRadialGradientRelativeCenter,
        );
    }
    throw Exception(
      "Type ${value.runtimeType.toString()} is not generalized in DivRadialGradientCenter",
    );
  }

  T maybeMap<T>({
    T Function(DivRadialGradientFixedCenter)? divRadialGradientFixedCenter,
    T Function(DivRadialGradientRelativeCenter)?
        divRadialGradientRelativeCenter,
    required T Function() orElse,
  }) {
    switch (_index) {
      case 0:
        if (divRadialGradientFixedCenter != null) {
          return divRadialGradientFixedCenter(
            value as DivRadialGradientFixedCenter,
          );
        }
        break;
      case 1:
        if (divRadialGradientRelativeCenter != null) {
          return divRadialGradientRelativeCenter(
            value as DivRadialGradientRelativeCenter,
          );
        }
        break;
    }
    return orElse();
  }

  const DivRadialGradientCenter.divRadialGradientFixedCenter(
    DivRadialGradientFixedCenter obj,
  )   : value = obj,
        _index = 0;

  const DivRadialGradientCenter.divRadialGradientRelativeCenter(
    DivRadialGradientRelativeCenter obj,
  )   : value = obj,
        _index = 1;

  bool get isDivRadialGradientFixedCenter => _index == 0;

  bool get isDivRadialGradientRelativeCenter => _index == 1;

  static DivRadialGradientCenter? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivRadialGradientFixedCenter.type:
          return DivRadialGradientCenter.divRadialGradientFixedCenter(
            DivRadialGradientFixedCenter.fromJson(json)!,
          );
        case DivRadialGradientRelativeCenter.type:
          return DivRadialGradientCenter.divRadialGradientRelativeCenter(
            DivRadialGradientRelativeCenter.fromJson(json)!,
          );
      }
      return null;
    } catch (_) {
      return null;
    }
  }
}
