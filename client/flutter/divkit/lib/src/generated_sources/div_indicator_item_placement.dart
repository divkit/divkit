// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'div_default_indicator_item_placement.dart';
import 'div_stretch_indicator_item_placement.dart';

class DivIndicatorItemPlacement with EquatableMixin {
  final Object value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivDefaultIndicatorItemPlacement)
        divDefaultIndicatorItemPlacement,
    required T Function(DivStretchIndicatorItemPlacement)
        divStretchIndicatorItemPlacement,
  }) {
    switch (_index!) {
      case 0:
        return divDefaultIndicatorItemPlacement(
          value as DivDefaultIndicatorItemPlacement,
        );
      case 1:
        return divStretchIndicatorItemPlacement(
          value as DivStretchIndicatorItemPlacement,
        );
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
    switch (_index!) {
      case 0:
        if (divDefaultIndicatorItemPlacement != null) {
          return divDefaultIndicatorItemPlacement(
            value as DivDefaultIndicatorItemPlacement,
          );
        }
        break;
      case 1:
        if (divStretchIndicatorItemPlacement != null) {
          return divStretchIndicatorItemPlacement(
            value as DivStretchIndicatorItemPlacement,
          );
        }
        break;
    }
    return orElse();
  }

  const DivIndicatorItemPlacement.divDefaultIndicatorItemPlacement(
    DivDefaultIndicatorItemPlacement obj,
  )   : value = obj,
        _index = 0;

  const DivIndicatorItemPlacement.divStretchIndicatorItemPlacement(
    DivStretchIndicatorItemPlacement obj,
  )   : value = obj,
        _index = 1;

  static DivIndicatorItemPlacement? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivDefaultIndicatorItemPlacement.type:
        return DivIndicatorItemPlacement.divDefaultIndicatorItemPlacement(
            DivDefaultIndicatorItemPlacement.fromJson(json)!);
      case DivStretchIndicatorItemPlacement.type:
        return DivIndicatorItemPlacement.divStretchIndicatorItemPlacement(
            DivStretchIndicatorItemPlacement.fromJson(json)!);
    }
    return null;
  }
}
