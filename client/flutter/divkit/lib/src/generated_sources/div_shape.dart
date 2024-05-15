// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'div_circle_shape.dart';
import 'div_rounded_rectangle_shape.dart';

class DivShape with EquatableMixin {
  const DivShape(Object value) : _value = value;

  final Object _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  Object get value {
    final value = _value;
    if (value is DivCircleShape) {
      return value;
    }
    if (value is DivRoundedRectangleShape) {
      return value;
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivShape");
  }

  T map<T>({
    required T Function(DivCircleShape) divCircleShape,
    required T Function(DivRoundedRectangleShape) divRoundedRectangleShape,
  }) {
    final value = _value;
    if (value is DivCircleShape) {
      return divCircleShape(value);
    }
    if (value is DivRoundedRectangleShape) {
      return divRoundedRectangleShape(value);
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivShape");
  }

  T maybeMap<T>({
    T Function(DivCircleShape)? divCircleShape,
    T Function(DivRoundedRectangleShape)? divRoundedRectangleShape,
    required T Function() orElse,
  }) {
    final value = _value;
    if (value is DivCircleShape && divCircleShape != null) {
      return divCircleShape(value);
    }
    if (value is DivRoundedRectangleShape && divRoundedRectangleShape != null) {
      return divRoundedRectangleShape(value);
    }
    return orElse();
  }

  const DivShape.divCircleShape(
    DivCircleShape value,
  ) : _value = value;

  const DivShape.divRoundedRectangleShape(
    DivRoundedRectangleShape value,
  ) : _value = value;

  static DivShape? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivRoundedRectangleShape.type:
        return DivShape(DivRoundedRectangleShape.fromJson(json)!);
      case DivCircleShape.type:
        return DivShape(DivCircleShape.fromJson(json)!);
    }
    return null;
  }
}
