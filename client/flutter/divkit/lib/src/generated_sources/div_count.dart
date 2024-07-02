// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/generated_sources/div_fixed_count.dart';
import 'package:divkit/src/generated_sources/div_infinity_count.dart';

class DivCount with EquatableMixin {
  final Object value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivFixedCount) divFixedCount,
    required T Function(DivInfinityCount) divInfinityCount,
  }) {
    switch (_index) {
      case 0:
        return divFixedCount(
          value as DivFixedCount,
        );
      case 1:
        return divInfinityCount(
          value as DivInfinityCount,
        );
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivCount");
  }

  T maybeMap<T>({
    T Function(DivFixedCount)? divFixedCount,
    T Function(DivInfinityCount)? divInfinityCount,
    required T Function() orElse,
  }) {
    switch (_index) {
      case 0:
        if (divFixedCount != null) {
          return divFixedCount(
            value as DivFixedCount,
          );
        }
        break;
      case 1:
        if (divInfinityCount != null) {
          return divInfinityCount(
            value as DivInfinityCount,
          );
        }
        break;
    }
    return orElse();
  }

  const DivCount.divFixedCount(
    DivFixedCount obj,
  )   : value = obj,
        _index = 0;

  const DivCount.divInfinityCount(
    DivInfinityCount obj,
  )   : value = obj,
        _index = 1;

  static DivCount? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivFixedCount.type:
        return DivCount.divFixedCount(DivFixedCount.fromJson(json)!);
      case DivInfinityCount.type:
        return DivCount.divInfinityCount(DivInfinityCount.fromJson(json)!);
    }
    return null;
  }
}
