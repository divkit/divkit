// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'div_image_background.dart';
import 'div_linear_gradient.dart';
import 'div_nine_patch_background.dart';
import 'div_radial_gradient.dart';
import 'div_solid_background.dart';

class DivBackground with EquatableMixin {
  const DivBackground(Object value) : _value = value;

  final Object _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  Object get value {
    final value = _value;
    if (value is DivImageBackground) {
      return value;
    }
    if (value is DivLinearGradient) {
      return value;
    }
    if (value is DivNinePatchBackground) {
      return value;
    }
    if (value is DivRadialGradient) {
      return value;
    }
    if (value is DivSolidBackground) {
      return value;
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivBackground");
  }

  T map<T>({
    required T Function(DivImageBackground) divImageBackground,
    required T Function(DivLinearGradient) divLinearGradient,
    required T Function(DivNinePatchBackground) divNinePatchBackground,
    required T Function(DivRadialGradient) divRadialGradient,
    required T Function(DivSolidBackground) divSolidBackground,
  }) {
    final value = _value;
    if (value is DivImageBackground) {
      return divImageBackground(value);
    }
    if (value is DivLinearGradient) {
      return divLinearGradient(value);
    }
    if (value is DivNinePatchBackground) {
      return divNinePatchBackground(value);
    }
    if (value is DivRadialGradient) {
      return divRadialGradient(value);
    }
    if (value is DivSolidBackground) {
      return divSolidBackground(value);
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivBackground");
  }

  T maybeMap<T>({
    T Function(DivImageBackground)? divImageBackground,
    T Function(DivLinearGradient)? divLinearGradient,
    T Function(DivNinePatchBackground)? divNinePatchBackground,
    T Function(DivRadialGradient)? divRadialGradient,
    T Function(DivSolidBackground)? divSolidBackground,
    required T Function() orElse,
  }) {
    final value = _value;
    if (value is DivImageBackground && divImageBackground != null) {
      return divImageBackground(value);
    }
    if (value is DivLinearGradient && divLinearGradient != null) {
      return divLinearGradient(value);
    }
    if (value is DivNinePatchBackground && divNinePatchBackground != null) {
      return divNinePatchBackground(value);
    }
    if (value is DivRadialGradient && divRadialGradient != null) {
      return divRadialGradient(value);
    }
    if (value is DivSolidBackground && divSolidBackground != null) {
      return divSolidBackground(value);
    }
    return orElse();
  }

  const DivBackground.divImageBackground(
    DivImageBackground value,
  ) : _value = value;

  const DivBackground.divLinearGradient(
    DivLinearGradient value,
  ) : _value = value;

  const DivBackground.divNinePatchBackground(
    DivNinePatchBackground value,
  ) : _value = value;

  const DivBackground.divRadialGradient(
    DivRadialGradient value,
  ) : _value = value;

  const DivBackground.divSolidBackground(
    DivSolidBackground value,
  ) : _value = value;

  static DivBackground? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivLinearGradient.type:
        return DivBackground(DivLinearGradient.fromJson(json)!);
      case DivRadialGradient.type:
        return DivBackground(DivRadialGradient.fromJson(json)!);
      case DivImageBackground.type:
        return DivBackground(DivImageBackground.fromJson(json)!);
      case DivSolidBackground.type:
        return DivBackground(DivSolidBackground.fromJson(json)!);
      case DivNinePatchBackground.type:
        return DivBackground(DivNinePatchBackground.fromJson(json)!);
    }
    return null;
  }
}
