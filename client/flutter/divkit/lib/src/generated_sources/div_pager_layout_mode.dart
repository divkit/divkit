// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'div_neighbour_page_size.dart';
import 'div_page_size.dart';

class DivPagerLayoutMode with EquatableMixin {
  const DivPagerLayoutMode(Object value) : _value = value;

  final Object _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  Object get value {
    final value = _value;
    if (value is DivNeighbourPageSize) {
      return value;
    }
    if (value is DivPageSize) {
      return value;
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivPagerLayoutMode");
  }

  T map<T>({
    required T Function(DivNeighbourPageSize) divNeighbourPageSize,
    required T Function(DivPageSize) divPageSize,
  }) {
    final value = _value;
    if (value is DivNeighbourPageSize) {
      return divNeighbourPageSize(value);
    }
    if (value is DivPageSize) {
      return divPageSize(value);
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivPagerLayoutMode");
  }

  T maybeMap<T>({
    T Function(DivNeighbourPageSize)? divNeighbourPageSize,
    T Function(DivPageSize)? divPageSize,
    required T Function() orElse,
  }) {
    final value = _value;
    if (value is DivNeighbourPageSize && divNeighbourPageSize != null) {
      return divNeighbourPageSize(value);
    }
    if (value is DivPageSize && divPageSize != null) {
      return divPageSize(value);
    }
    return orElse();
  }

  const DivPagerLayoutMode.divNeighbourPageSize(
    DivNeighbourPageSize value,
  ) : _value = value;

  const DivPagerLayoutMode.divPageSize(
    DivPageSize value,
  ) : _value = value;

  static DivPagerLayoutMode? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivPageSize.type:
        return DivPagerLayoutMode(DivPageSize.fromJson(json)!);
      case DivNeighbourPageSize.type:
        return DivPagerLayoutMode(DivNeighbourPageSize.fromJson(json)!);
    }
    return null;
  }
}
