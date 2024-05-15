// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'div_fixed_count.dart';
import 'div_infinity_count.dart';

class DivCount with EquatableMixin {
  const DivCount(Object value) : _value = value;

  final Object _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  Object get value {
    final value = _value;
    if (value is DivFixedCount) {
      return value;
    }
    if (value is DivInfinityCount) {
      return value;
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivCount");
  }

  T map<T>({
    required T Function(DivFixedCount) divFixedCount,
    required T Function(DivInfinityCount) divInfinityCount,
  }) {
    final value = _value;
    if (value is DivFixedCount) {
      return divFixedCount(value);
    }
    if (value is DivInfinityCount) {
      return divInfinityCount(value);
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivCount");
  }

  T maybeMap<T>({
    T Function(DivFixedCount)? divFixedCount,
    T Function(DivInfinityCount)? divInfinityCount,
    required T Function() orElse,
  }) {
    final value = _value;
    if (value is DivFixedCount && divFixedCount != null) {
      return divFixedCount(value);
    }
    if (value is DivInfinityCount && divInfinityCount != null) {
      return divInfinityCount(value);
    }
    return orElse();
  }

  const DivCount.divFixedCount(
    DivFixedCount value,
  ) : _value = value;

  const DivCount.divInfinityCount(
    DivInfinityCount value,
  ) : _value = value;

  static DivCount? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivInfinityCount.type:
        return DivCount(DivInfinityCount.fromJson(json)!);
      case DivFixedCount.type:
        return DivCount(DivFixedCount.fromJson(json)!);
    }
    return null;
  }
}
