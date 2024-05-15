// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'div_fixed_size.dart';
import 'div_match_parent_size.dart';
import 'div_wrap_content_size.dart';

class DivSize with EquatableMixin {
  const DivSize(Object value) : _value = value;

  final Object _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  Object get value {
    final value = _value;
    if (value is DivFixedSize) {
      return value;
    }
    if (value is DivMatchParentSize) {
      return value;
    }
    if (value is DivWrapContentSize) {
      return value;
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivSize");
  }

  T map<T>({
    required T Function(DivFixedSize) divFixedSize,
    required T Function(DivMatchParentSize) divMatchParentSize,
    required T Function(DivWrapContentSize) divWrapContentSize,
  }) {
    final value = _value;
    if (value is DivFixedSize) {
      return divFixedSize(value);
    }
    if (value is DivMatchParentSize) {
      return divMatchParentSize(value);
    }
    if (value is DivWrapContentSize) {
      return divWrapContentSize(value);
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivSize");
  }

  T maybeMap<T>({
    T Function(DivFixedSize)? divFixedSize,
    T Function(DivMatchParentSize)? divMatchParentSize,
    T Function(DivWrapContentSize)? divWrapContentSize,
    required T Function() orElse,
  }) {
    final value = _value;
    if (value is DivFixedSize && divFixedSize != null) {
      return divFixedSize(value);
    }
    if (value is DivMatchParentSize && divMatchParentSize != null) {
      return divMatchParentSize(value);
    }
    if (value is DivWrapContentSize && divWrapContentSize != null) {
      return divWrapContentSize(value);
    }
    return orElse();
  }

  const DivSize.divFixedSize(
    DivFixedSize value,
  ) : _value = value;

  const DivSize.divMatchParentSize(
    DivMatchParentSize value,
  ) : _value = value;

  const DivSize.divWrapContentSize(
    DivWrapContentSize value,
  ) : _value = value;

  static DivSize? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivFixedSize.type:
        return DivSize(DivFixedSize.fromJson(json)!);
      case DivMatchParentSize.type:
        return DivSize(DivMatchParentSize.fromJson(json)!);
      case DivWrapContentSize.type:
        return DivSize(DivWrapContentSize.fromJson(json)!);
    }
    return null;
  }
}
