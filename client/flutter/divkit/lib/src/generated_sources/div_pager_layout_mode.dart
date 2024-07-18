// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/generated_sources/div_neighbour_page_size.dart';
import 'package:divkit/src/generated_sources/div_page_size.dart';

class DivPagerLayoutMode with EquatableMixin {
  final Object value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivNeighbourPageSize) divNeighbourPageSize,
    required T Function(DivPageSize) divPageSize,
  }) {
    switch (_index) {
      case 0:
        return divNeighbourPageSize(
          value as DivNeighbourPageSize,
        );
      case 1:
        return divPageSize(
          value as DivPageSize,
        );
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivPagerLayoutMode");
  }

  T maybeMap<T>({
    T Function(DivNeighbourPageSize)? divNeighbourPageSize,
    T Function(DivPageSize)? divPageSize,
    required T Function() orElse,
  }) {
    switch (_index) {
      case 0:
        if (divNeighbourPageSize != null) {
          return divNeighbourPageSize(
            value as DivNeighbourPageSize,
          );
        }
        break;
      case 1:
        if (divPageSize != null) {
          return divPageSize(
            value as DivPageSize,
          );
        }
        break;
    }
    return orElse();
  }

  const DivPagerLayoutMode.divNeighbourPageSize(
    DivNeighbourPageSize obj,
  )   : value = obj,
        _index = 0;

  const DivPagerLayoutMode.divPageSize(
    DivPageSize obj,
  )   : value = obj,
        _index = 1;

  static DivPagerLayoutMode? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivNeighbourPageSize.type:
        return DivPagerLayoutMode.divNeighbourPageSize(
            DivNeighbourPageSize.fromJson(json)!);
      case DivPageSize.type:
        return DivPagerLayoutMode.divPageSize(DivPageSize.fromJson(json)!);
    }
    return null;
  }
}
