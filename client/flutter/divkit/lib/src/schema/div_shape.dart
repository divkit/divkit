// Generated code. Do not modify.

import 'package:divkit/src/schema/div_circle_shape.dart';
import 'package:divkit/src/schema/div_rounded_rectangle_shape.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivShape extends Preloadable with EquatableMixin {
  final Preloadable value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivCircleShape) divCircleShape,
    required T Function(DivRoundedRectangleShape) divRoundedRectangleShape,
  }) {
    switch (_index) {
      case 0:
        return divCircleShape(
          value as DivCircleShape,
        );
      case 1:
        return divRoundedRectangleShape(
          value as DivRoundedRectangleShape,
        );
    }
    throw Exception(
      "Type ${value.runtimeType.toString()} is not generalized in DivShape",
    );
  }

  T maybeMap<T>({
    T Function(DivCircleShape)? divCircleShape,
    T Function(DivRoundedRectangleShape)? divRoundedRectangleShape,
    required T Function() orElse,
  }) {
    switch (_index) {
      case 0:
        if (divCircleShape != null) {
          return divCircleShape(
            value as DivCircleShape,
          );
        }
        break;
      case 1:
        if (divRoundedRectangleShape != null) {
          return divRoundedRectangleShape(
            value as DivRoundedRectangleShape,
          );
        }
        break;
    }
    return orElse();
  }

  const DivShape.divCircleShape(
    DivCircleShape obj,
  )   : value = obj,
        _index = 0;

  const DivShape.divRoundedRectangleShape(
    DivRoundedRectangleShape obj,
  )   : value = obj,
        _index = 1;

  bool get isDivCircleShape => _index == 0;

  bool get isDivRoundedRectangleShape => _index == 1;

  @override
  Future<void> preload(Map<String, dynamic> context) => value.preload(context);

  static DivShape? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivCircleShape.type:
          return DivShape.divCircleShape(
            DivCircleShape.fromJson(json)!,
          );
        case DivRoundedRectangleShape.type:
          return DivShape.divRoundedRectangleShape(
            DivRoundedRectangleShape.fromJson(json)!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivShape?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivCircleShape.type:
          return DivShape.divCircleShape(
            (await DivCircleShape.parse(json))!,
          );
        case DivRoundedRectangleShape.type:
          return DivShape.divRoundedRectangleShape(
            (await DivRoundedRectangleShape.parse(json))!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
