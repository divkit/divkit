// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'div_appearance_set_transition.dart';
import 'div_fade_transition.dart';
import 'div_scale_transition.dart';
import 'div_slide_transition.dart';

class DivAppearanceTransition with EquatableMixin {
  const DivAppearanceTransition(Object value) : _value = value;

  final Object _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  Object get value {
    final value = _value;
    if (value is DivAppearanceSetTransition) {
      return value;
    }
    if (value is DivFadeTransition) {
      return value;
    }
    if (value is DivScaleTransition) {
      return value;
    }
    if (value is DivSlideTransition) {
      return value;
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivAppearanceTransition");
  }

  T map<T>({
    required T Function(DivAppearanceSetTransition) divAppearanceSetTransition,
    required T Function(DivFadeTransition) divFadeTransition,
    required T Function(DivScaleTransition) divScaleTransition,
    required T Function(DivSlideTransition) divSlideTransition,
  }) {
    final value = _value;
    if (value is DivAppearanceSetTransition) {
      return divAppearanceSetTransition(value);
    }
    if (value is DivFadeTransition) {
      return divFadeTransition(value);
    }
    if (value is DivScaleTransition) {
      return divScaleTransition(value);
    }
    if (value is DivSlideTransition) {
      return divSlideTransition(value);
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivAppearanceTransition");
  }

  T maybeMap<T>({
    T Function(DivAppearanceSetTransition)? divAppearanceSetTransition,
    T Function(DivFadeTransition)? divFadeTransition,
    T Function(DivScaleTransition)? divScaleTransition,
    T Function(DivSlideTransition)? divSlideTransition,
    required T Function() orElse,
  }) {
    final value = _value;
    if (value is DivAppearanceSetTransition &&
        divAppearanceSetTransition != null) {
      return divAppearanceSetTransition(value);
    }
    if (value is DivFadeTransition && divFadeTransition != null) {
      return divFadeTransition(value);
    }
    if (value is DivScaleTransition && divScaleTransition != null) {
      return divScaleTransition(value);
    }
    if (value is DivSlideTransition && divSlideTransition != null) {
      return divSlideTransition(value);
    }
    return orElse();
  }

  const DivAppearanceTransition.divAppearanceSetTransition(
    DivAppearanceSetTransition value,
  ) : _value = value;

  const DivAppearanceTransition.divFadeTransition(
    DivFadeTransition value,
  ) : _value = value;

  const DivAppearanceTransition.divScaleTransition(
    DivScaleTransition value,
  ) : _value = value;

  const DivAppearanceTransition.divSlideTransition(
    DivSlideTransition value,
  ) : _value = value;

  static DivAppearanceTransition? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivAppearanceSetTransition.type:
        return DivAppearanceTransition(
            DivAppearanceSetTransition.fromJson(json)!);
      case DivFadeTransition.type:
        return DivAppearanceTransition(DivFadeTransition.fromJson(json)!);
      case DivScaleTransition.type:
        return DivAppearanceTransition(DivScaleTransition.fromJson(json)!);
      case DivSlideTransition.type:
        return DivAppearanceTransition(DivSlideTransition.fromJson(json)!);
    }
    return null;
  }
}
