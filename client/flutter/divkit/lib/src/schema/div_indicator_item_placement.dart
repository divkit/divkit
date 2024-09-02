// Generated code. Do not modify.

import 'package:divkit/src/schema/div_default_indicator_item_placement.dart';
import 'package:divkit/src/schema/div_stretch_indicator_item_placement.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivIndicatorItemPlacement extends Preloadable with EquatableMixin {
  final Preloadable value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivDefaultIndicatorItemPlacement)
        divDefaultIndicatorItemPlacement,
    required T Function(DivStretchIndicatorItemPlacement)
        divStretchIndicatorItemPlacement,
  }) {
    switch (_index) {
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
      "Type ${value.runtimeType.toString()} is not generalized in DivIndicatorItemPlacement",
    );
  }

  T maybeMap<T>({
    T Function(DivDefaultIndicatorItemPlacement)?
        divDefaultIndicatorItemPlacement,
    T Function(DivStretchIndicatorItemPlacement)?
        divStretchIndicatorItemPlacement,
    required T Function() orElse,
  }) {
    switch (_index) {
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

  @override
  Future<void> preload(Map<String, dynamic> context) => value.preload(context);

  static DivIndicatorItemPlacement? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivDefaultIndicatorItemPlacement.type:
          return DivIndicatorItemPlacement.divDefaultIndicatorItemPlacement(
            DivDefaultIndicatorItemPlacement.fromJson(json)!,
          );
        case DivStretchIndicatorItemPlacement.type:
          return DivIndicatorItemPlacement.divStretchIndicatorItemPlacement(
            DivStretchIndicatorItemPlacement.fromJson(json)!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivIndicatorItemPlacement?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivDefaultIndicatorItemPlacement.type:
          return DivIndicatorItemPlacement.divDefaultIndicatorItemPlacement(
            (await DivDefaultIndicatorItemPlacement.parse(json))!,
          );
        case DivStretchIndicatorItemPlacement.type:
          return DivIndicatorItemPlacement.divStretchIndicatorItemPlacement(
            (await DivStretchIndicatorItemPlacement.parse(json))!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
