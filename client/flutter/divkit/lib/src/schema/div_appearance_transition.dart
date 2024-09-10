// Generated code. Do not modify.

import 'package:divkit/src/schema/div_appearance_set_transition.dart';
import 'package:divkit/src/schema/div_fade_transition.dart';
import 'package:divkit/src/schema/div_scale_transition.dart';
import 'package:divkit/src/schema/div_slide_transition.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivAppearanceTransition extends Preloadable with EquatableMixin {
  final Preloadable value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivAppearanceSetTransition) divAppearanceSetTransition,
    required T Function(DivFadeTransition) divFadeTransition,
    required T Function(DivScaleTransition) divScaleTransition,
    required T Function(DivSlideTransition) divSlideTransition,
  }) {
    switch (_index) {
      case 0:
        return divAppearanceSetTransition(
          value as DivAppearanceSetTransition,
        );
      case 1:
        return divFadeTransition(
          value as DivFadeTransition,
        );
      case 2:
        return divScaleTransition(
          value as DivScaleTransition,
        );
      case 3:
        return divSlideTransition(
          value as DivSlideTransition,
        );
    }
    throw Exception(
      "Type ${value.runtimeType.toString()} is not generalized in DivAppearanceTransition",
    );
  }

  T maybeMap<T>({
    T Function(DivAppearanceSetTransition)? divAppearanceSetTransition,
    T Function(DivFadeTransition)? divFadeTransition,
    T Function(DivScaleTransition)? divScaleTransition,
    T Function(DivSlideTransition)? divSlideTransition,
    required T Function() orElse,
  }) {
    switch (_index) {
      case 0:
        if (divAppearanceSetTransition != null) {
          return divAppearanceSetTransition(
            value as DivAppearanceSetTransition,
          );
        }
        break;
      case 1:
        if (divFadeTransition != null) {
          return divFadeTransition(
            value as DivFadeTransition,
          );
        }
        break;
      case 2:
        if (divScaleTransition != null) {
          return divScaleTransition(
            value as DivScaleTransition,
          );
        }
        break;
      case 3:
        if (divSlideTransition != null) {
          return divSlideTransition(
            value as DivSlideTransition,
          );
        }
        break;
    }
    return orElse();
  }

  const DivAppearanceTransition.divAppearanceSetTransition(
    DivAppearanceSetTransition obj,
  )   : value = obj,
        _index = 0;

  const DivAppearanceTransition.divFadeTransition(
    DivFadeTransition obj,
  )   : value = obj,
        _index = 1;

  const DivAppearanceTransition.divScaleTransition(
    DivScaleTransition obj,
  )   : value = obj,
        _index = 2;

  const DivAppearanceTransition.divSlideTransition(
    DivSlideTransition obj,
  )   : value = obj,
        _index = 3;

  bool get isDivAppearanceSetTransition => _index == 0;

  bool get isDivFadeTransition => _index == 1;

  bool get isDivScaleTransition => _index == 2;

  bool get isDivSlideTransition => _index == 3;

  @override
  Future<void> preload(Map<String, dynamic> context) => value.preload(context);

  static DivAppearanceTransition? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivAppearanceSetTransition.type:
          return DivAppearanceTransition.divAppearanceSetTransition(
            DivAppearanceSetTransition.fromJson(json)!,
          );
        case DivFadeTransition.type:
          return DivAppearanceTransition.divFadeTransition(
            DivFadeTransition.fromJson(json)!,
          );
        case DivScaleTransition.type:
          return DivAppearanceTransition.divScaleTransition(
            DivScaleTransition.fromJson(json)!,
          );
        case DivSlideTransition.type:
          return DivAppearanceTransition.divSlideTransition(
            DivSlideTransition.fromJson(json)!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivAppearanceTransition?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivAppearanceSetTransition.type:
          return DivAppearanceTransition.divAppearanceSetTransition(
            (await DivAppearanceSetTransition.parse(json))!,
          );
        case DivFadeTransition.type:
          return DivAppearanceTransition.divFadeTransition(
            (await DivFadeTransition.parse(json))!,
          );
        case DivScaleTransition.type:
          return DivAppearanceTransition.divScaleTransition(
            (await DivScaleTransition.parse(json))!,
          );
        case DivSlideTransition.type:
          return DivAppearanceTransition.divSlideTransition(
            (await DivSlideTransition.parse(json))!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
