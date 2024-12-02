// Generated code. Do not modify.

import 'package:divkit/src/schema/div_neighbour_page_size.dart';
import 'package:divkit/src/schema/div_page_content_size.dart';
import 'package:divkit/src/schema/div_page_size.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

class DivPagerLayoutMode extends Resolvable with EquatableMixin {
  final Resolvable value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivNeighbourPageSize) divNeighbourPageSize,
    required T Function(DivPageContentSize) divPageContentSize,
    required T Function(DivPageSize) divPageSize,
  }) {
    switch (_index) {
      case 0:
        return divNeighbourPageSize(
          value as DivNeighbourPageSize,
        );
      case 1:
        return divPageContentSize(
          value as DivPageContentSize,
        );
      case 2:
        return divPageSize(
          value as DivPageSize,
        );
    }
    throw Exception(
      "Type ${value.runtimeType.toString()} is not generalized in DivPagerLayoutMode",
    );
  }

  T maybeMap<T>({
    T Function(DivNeighbourPageSize)? divNeighbourPageSize,
    T Function(DivPageContentSize)? divPageContentSize,
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
        if (divPageContentSize != null) {
          return divPageContentSize(
            value as DivPageContentSize,
          );
        }
        break;
      case 2:
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

  const DivPagerLayoutMode.divPageContentSize(
    DivPageContentSize obj,
  )   : value = obj,
        _index = 1;

  const DivPagerLayoutMode.divPageSize(
    DivPageSize obj,
  )   : value = obj,
        _index = 2;

  bool get isDivNeighbourPageSize => _index == 0;

  bool get isDivPageContentSize => _index == 1;

  bool get isDivPageSize => _index == 2;

  static DivPagerLayoutMode? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivNeighbourPageSize.type:
          return DivPagerLayoutMode.divNeighbourPageSize(
            DivNeighbourPageSize.fromJson(json)!,
          );
        case DivPageContentSize.type:
          return DivPagerLayoutMode.divPageContentSize(
            DivPageContentSize.fromJson(json)!,
          );
        case DivPageSize.type:
          return DivPagerLayoutMode.divPageSize(
            DivPageSize.fromJson(json)!,
          );
      }
      return null;
    } catch (_) {
      return null;
    }
  }

  @override
  DivPagerLayoutMode resolve(DivVariableContext context) {
    value.resolve(context);
    return this;
  }
}
