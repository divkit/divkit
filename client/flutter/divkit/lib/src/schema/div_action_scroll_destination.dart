// Generated code. Do not modify.

import 'package:divkit/src/schema/end_destination.dart';
import 'package:divkit/src/schema/index_destination.dart';
import 'package:divkit/src/schema/offset_destination.dart';
import 'package:divkit/src/schema/start_destination.dart';
import 'package:equatable/equatable.dart';

class DivActionScrollDestination with EquatableMixin {
  final Object value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(EndDestination) endDestination,
    required T Function(IndexDestination) indexDestination,
    required T Function(OffsetDestination) offsetDestination,
    required T Function(StartDestination) startDestination,
  }) {
    switch (_index) {
      case 0:
        return endDestination(
          value as EndDestination,
        );
      case 1:
        return indexDestination(
          value as IndexDestination,
        );
      case 2:
        return offsetDestination(
          value as OffsetDestination,
        );
      case 3:
        return startDestination(
          value as StartDestination,
        );
    }
    throw Exception(
      "Type ${value.runtimeType.toString()} is not generalized in DivActionScrollDestination",
    );
  }

  T maybeMap<T>({
    T Function(EndDestination)? endDestination,
    T Function(IndexDestination)? indexDestination,
    T Function(OffsetDestination)? offsetDestination,
    T Function(StartDestination)? startDestination,
    required T Function() orElse,
  }) {
    switch (_index) {
      case 0:
        if (endDestination != null) {
          return endDestination(
            value as EndDestination,
          );
        }
        break;
      case 1:
        if (indexDestination != null) {
          return indexDestination(
            value as IndexDestination,
          );
        }
        break;
      case 2:
        if (offsetDestination != null) {
          return offsetDestination(
            value as OffsetDestination,
          );
        }
        break;
      case 3:
        if (startDestination != null) {
          return startDestination(
            value as StartDestination,
          );
        }
        break;
    }
    return orElse();
  }

  const DivActionScrollDestination.endDestination(
    EndDestination obj,
  )   : value = obj,
        _index = 0;

  const DivActionScrollDestination.indexDestination(
    IndexDestination obj,
  )   : value = obj,
        _index = 1;

  const DivActionScrollDestination.offsetDestination(
    OffsetDestination obj,
  )   : value = obj,
        _index = 2;

  const DivActionScrollDestination.startDestination(
    StartDestination obj,
  )   : value = obj,
        _index = 3;

  bool get isEndDestination => _index == 0;

  bool get isIndexDestination => _index == 1;

  bool get isOffsetDestination => _index == 2;

  bool get isStartDestination => _index == 3;

  static DivActionScrollDestination? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case EndDestination.type:
          return DivActionScrollDestination.endDestination(
            EndDestination.fromJson(json)!,
          );
        case IndexDestination.type:
          return DivActionScrollDestination.indexDestination(
            IndexDestination.fromJson(json)!,
          );
        case OffsetDestination.type:
          return DivActionScrollDestination.offsetDestination(
            OffsetDestination.fromJson(json)!,
          );
        case StartDestination.type:
          return DivActionScrollDestination.startDestination(
            StartDestination.fromJson(json)!,
          );
      }
      return null;
    } catch (_) {
      return null;
    }
  }
}
