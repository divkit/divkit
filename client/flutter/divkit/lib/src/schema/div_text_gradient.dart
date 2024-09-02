// Generated code. Do not modify.

import 'package:divkit/src/schema/div_linear_gradient.dart';
import 'package:divkit/src/schema/div_radial_gradient.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivTextGradient extends Preloadable with EquatableMixin {
  final Preloadable value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivLinearGradient) divLinearGradient,
    required T Function(DivRadialGradient) divRadialGradient,
  }) {
    switch (_index) {
      case 0:
        return divLinearGradient(
          value as DivLinearGradient,
        );
      case 1:
        return divRadialGradient(
          value as DivRadialGradient,
        );
    }
    throw Exception(
      "Type ${value.runtimeType.toString()} is not generalized in DivTextGradient",
    );
  }

  T maybeMap<T>({
    T Function(DivLinearGradient)? divLinearGradient,
    T Function(DivRadialGradient)? divRadialGradient,
    required T Function() orElse,
  }) {
    switch (_index) {
      case 0:
        if (divLinearGradient != null) {
          return divLinearGradient(
            value as DivLinearGradient,
          );
        }
        break;
      case 1:
        if (divRadialGradient != null) {
          return divRadialGradient(
            value as DivRadialGradient,
          );
        }
        break;
    }
    return orElse();
  }

  const DivTextGradient.divLinearGradient(
    DivLinearGradient obj,
  )   : value = obj,
        _index = 0;

  const DivTextGradient.divRadialGradient(
    DivRadialGradient obj,
  )   : value = obj,
        _index = 1;

  @override
  Future<void> preload(Map<String, dynamic> context) => value.preload(context);

  static DivTextGradient? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivLinearGradient.type:
          return DivTextGradient.divLinearGradient(
            DivLinearGradient.fromJson(json)!,
          );
        case DivRadialGradient.type:
          return DivTextGradient.divRadialGradient(
            DivRadialGradient.fromJson(json)!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivTextGradient?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivLinearGradient.type:
          return DivTextGradient.divLinearGradient(
            (await DivLinearGradient.parse(json))!,
          );
        case DivRadialGradient.type:
          return DivTextGradient.divRadialGradient(
            (await DivRadialGradient.parse(json))!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
