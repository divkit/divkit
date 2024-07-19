// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/generated_sources/div_linear_gradient.dart';
import 'package:divkit/src/generated_sources/div_radial_gradient.dart';

class DivTextGradient with EquatableMixin {
  final Object value;
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

  static DivTextGradient? fromJson(Map<String, dynamic>? json) {
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
}
