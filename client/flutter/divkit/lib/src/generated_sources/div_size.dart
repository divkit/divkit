// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/generated_sources/div_fixed_size.dart';
import 'package:divkit/src/generated_sources/div_match_parent_size.dart';
import 'package:divkit/src/generated_sources/div_wrap_content_size.dart';

class DivSize with EquatableMixin {
  final Object value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivFixedSize) divFixedSize,
    required T Function(DivMatchParentSize) divMatchParentSize,
    required T Function(DivWrapContentSize) divWrapContentSize,
  }) {
    switch (_index) {
      case 0:
        return divFixedSize(
          value as DivFixedSize,
        );
      case 1:
        return divMatchParentSize(
          value as DivMatchParentSize,
        );
      case 2:
        return divWrapContentSize(
          value as DivWrapContentSize,
        );
    }
    throw Exception(
      "Type ${value.runtimeType.toString()} is not generalized in DivSize",
    );
  }

  T maybeMap<T>({
    T Function(DivFixedSize)? divFixedSize,
    T Function(DivMatchParentSize)? divMatchParentSize,
    T Function(DivWrapContentSize)? divWrapContentSize,
    required T Function() orElse,
  }) {
    switch (_index) {
      case 0:
        if (divFixedSize != null) {
          return divFixedSize(
            value as DivFixedSize,
          );
        }
        break;
      case 1:
        if (divMatchParentSize != null) {
          return divMatchParentSize(
            value as DivMatchParentSize,
          );
        }
        break;
      case 2:
        if (divWrapContentSize != null) {
          return divWrapContentSize(
            value as DivWrapContentSize,
          );
        }
        break;
    }
    return orElse();
  }

  const DivSize.divFixedSize(
    DivFixedSize obj,
  )   : value = obj,
        _index = 0;

  const DivSize.divMatchParentSize(
    DivMatchParentSize obj,
  )   : value = obj,
        _index = 1;

  const DivSize.divWrapContentSize(
    DivWrapContentSize obj,
  )   : value = obj,
        _index = 2;

  static DivSize? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivFixedSize.type:
          return DivSize.divFixedSize(DivFixedSize.fromJson(json)!);
        case DivMatchParentSize.type:
          return DivSize.divMatchParentSize(DivMatchParentSize.fromJson(json)!);
        case DivWrapContentSize.type:
          return DivSize.divWrapContentSize(DivWrapContentSize.fromJson(json)!);
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
