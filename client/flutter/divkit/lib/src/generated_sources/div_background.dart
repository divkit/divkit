// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/generated_sources/div_image_background.dart';
import 'package:divkit/src/generated_sources/div_linear_gradient.dart';
import 'package:divkit/src/generated_sources/div_nine_patch_background.dart';
import 'package:divkit/src/generated_sources/div_radial_gradient.dart';
import 'package:divkit/src/generated_sources/div_solid_background.dart';

class DivBackground with EquatableMixin {
  final Object value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivImageBackground) divImageBackground,
    required T Function(DivLinearGradient) divLinearGradient,
    required T Function(DivNinePatchBackground) divNinePatchBackground,
    required T Function(DivRadialGradient) divRadialGradient,
    required T Function(DivSolidBackground) divSolidBackground,
  }) {
    switch (_index) {
      case 0:
        return divImageBackground(
          value as DivImageBackground,
        );
      case 1:
        return divLinearGradient(
          value as DivLinearGradient,
        );
      case 2:
        return divNinePatchBackground(
          value as DivNinePatchBackground,
        );
      case 3:
        return divRadialGradient(
          value as DivRadialGradient,
        );
      case 4:
        return divSolidBackground(
          value as DivSolidBackground,
        );
    }
    throw Exception(
      "Type ${value.runtimeType.toString()} is not generalized in DivBackground",
    );
  }

  T maybeMap<T>({
    T Function(DivImageBackground)? divImageBackground,
    T Function(DivLinearGradient)? divLinearGradient,
    T Function(DivNinePatchBackground)? divNinePatchBackground,
    T Function(DivRadialGradient)? divRadialGradient,
    T Function(DivSolidBackground)? divSolidBackground,
    required T Function() orElse,
  }) {
    switch (_index) {
      case 0:
        if (divImageBackground != null) {
          return divImageBackground(
            value as DivImageBackground,
          );
        }
        break;
      case 1:
        if (divLinearGradient != null) {
          return divLinearGradient(
            value as DivLinearGradient,
          );
        }
        break;
      case 2:
        if (divNinePatchBackground != null) {
          return divNinePatchBackground(
            value as DivNinePatchBackground,
          );
        }
        break;
      case 3:
        if (divRadialGradient != null) {
          return divRadialGradient(
            value as DivRadialGradient,
          );
        }
        break;
      case 4:
        if (divSolidBackground != null) {
          return divSolidBackground(
            value as DivSolidBackground,
          );
        }
        break;
    }
    return orElse();
  }

  const DivBackground.divImageBackground(
    DivImageBackground obj,
  )   : value = obj,
        _index = 0;

  const DivBackground.divLinearGradient(
    DivLinearGradient obj,
  )   : value = obj,
        _index = 1;

  const DivBackground.divNinePatchBackground(
    DivNinePatchBackground obj,
  )   : value = obj,
        _index = 2;

  const DivBackground.divRadialGradient(
    DivRadialGradient obj,
  )   : value = obj,
        _index = 3;

  const DivBackground.divSolidBackground(
    DivSolidBackground obj,
  )   : value = obj,
        _index = 4;

  static DivBackground? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivImageBackground.type:
          return DivBackground.divImageBackground(
            DivImageBackground.fromJson(json)!,
          );
        case DivLinearGradient.type:
          return DivBackground.divLinearGradient(
            DivLinearGradient.fromJson(json)!,
          );
        case DivNinePatchBackground.type:
          return DivBackground.divNinePatchBackground(
            DivNinePatchBackground.fromJson(json)!,
          );
        case DivRadialGradient.type:
          return DivBackground.divRadialGradient(
            DivRadialGradient.fromJson(json)!,
          );
        case DivSolidBackground.type:
          return DivBackground.divSolidBackground(
            DivSolidBackground.fromJson(json)!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
