// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'div_shape_drawable.dart';

class DivDrawable with EquatableMixin {
  const DivDrawable(Object value) : _value = value;

  final Object _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  Object get value {
    final value = _value;
    if (value is DivShapeDrawable) {
      return value;
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivDrawable");
  }

  T map<T>({
    required T Function(DivShapeDrawable) divShapeDrawable,
  }) {
    final value = _value;
    if (value is DivShapeDrawable) {
      return divShapeDrawable(value);
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivDrawable");
  }

  T maybeMap<T>({
    T Function(DivShapeDrawable)? divShapeDrawable,
    required T Function() orElse,
  }) {
    final value = _value;
    if (value is DivShapeDrawable && divShapeDrawable != null) {
      return divShapeDrawable(value);
    }
    return orElse();
  }

  const DivDrawable.divShapeDrawable(
    DivShapeDrawable value,
  ) : _value = value;

  static DivDrawable? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivShapeDrawable.type:
        return DivDrawable(DivShapeDrawable.fromJson(json)!);
    }
    return null;
  }
}
