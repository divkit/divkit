// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'div_default_indicator_item_placement.dart';
import 'div_stretch_indicator_item_placement.dart';

class DivIndicatorItemPlacement with EquatableMixin {
  const DivIndicatorItemPlacement(Object value) : _value = value;

  final Object _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  Object get value {
    final value = _value;
    if (value is DivDefaultIndicatorItemPlacement) {
      return value;
    }
    if (value is DivStretchIndicatorItemPlacement) {
      return value;
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivIndicatorItemPlacement");
  }

  T map<T>({
    required T Function(DivDefaultIndicatorItemPlacement)
        divDefaultIndicatorItemPlacement,
    required T Function(DivStretchIndicatorItemPlacement)
        divStretchIndicatorItemPlacement,
  }) {
    final value = _value;
    if (value is DivDefaultIndicatorItemPlacement) {
      return divDefaultIndicatorItemPlacement(value);
    }
    if (value is DivStretchIndicatorItemPlacement) {
      return divStretchIndicatorItemPlacement(value);
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivIndicatorItemPlacement");
  }

  T maybeMap<T>({
    T Function(DivDefaultIndicatorItemPlacement)?
        divDefaultIndicatorItemPlacement,
    T Function(DivStretchIndicatorItemPlacement)?
        divStretchIndicatorItemPlacement,
    required T Function() orElse,
  }) {
    final value = _value;
    if (value is DivDefaultIndicatorItemPlacement &&
        divDefaultIndicatorItemPlacement != null) {
      return divDefaultIndicatorItemPlacement(value);
    }
    if (value is DivStretchIndicatorItemPlacement &&
        divStretchIndicatorItemPlacement != null) {
      return divStretchIndicatorItemPlacement(value);
    }
    return orElse();
  }

  const DivIndicatorItemPlacement.divDefaultIndicatorItemPlacement(
    DivDefaultIndicatorItemPlacement value,
  ) : _value = value;

  const DivIndicatorItemPlacement.divStretchIndicatorItemPlacement(
    DivStretchIndicatorItemPlacement value,
  ) : _value = value;

  static DivIndicatorItemPlacement? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivDefaultIndicatorItemPlacement.type:
        return DivIndicatorItemPlacement(
            DivDefaultIndicatorItemPlacement.fromJson(json)!);
      case DivStretchIndicatorItemPlacement.type:
        return DivIndicatorItemPlacement(
            DivStretchIndicatorItemPlacement.fromJson(json)!);
    }
    return null;
  }
}
