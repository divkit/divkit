// Generated code. Do not modify.

import 'package:divkit/src/schema/div_radial_gradient_fixed_center.dart';
import 'package:divkit/src/schema/div_radial_gradient_relative_center.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivRadialGradientCenter extends Preloadable with EquatableMixin {
  final Preloadable value;
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

  @override
  Future<void> preload(Map<String, dynamic> context) => value.preload(context);

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
    } catch (e) {
      return null;
    }
  }

  static Future<DivRadialGradientCenter?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivRadialGradientFixedCenter.type:
          return DivRadialGradientCenter.divRadialGradientFixedCenter(
            (await DivRadialGradientFixedCenter.parse(json))!,
          );
        case DivRadialGradientRelativeCenter.type:
          return DivRadialGradientCenter.divRadialGradientRelativeCenter(
            (await DivRadialGradientRelativeCenter.parse(json))!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
