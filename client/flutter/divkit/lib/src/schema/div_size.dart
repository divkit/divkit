// Generated code. Do not modify.

import 'package:divkit/src/schema/div_fixed_size.dart';
import 'package:divkit/src/schema/div_match_parent_size.dart';
import 'package:divkit/src/schema/div_wrap_content_size.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivSize extends Preloadable with EquatableMixin {
  final Preloadable value;
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

  bool get isDivFixedSize => _index == 0;

  bool get isDivMatchParentSize => _index == 1;

  bool get isDivWrapContentSize => _index == 2;

  @override
  Future<void> preload(Map<String, dynamic> context) => value.preload(context);

  static DivSize? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivFixedSize.type:
          return DivSize.divFixedSize(
            DivFixedSize.fromJson(json)!,
          );
        case DivMatchParentSize.type:
          return DivSize.divMatchParentSize(
            DivMatchParentSize.fromJson(json)!,
          );
        case DivWrapContentSize.type:
          return DivSize.divWrapContentSize(
            DivWrapContentSize.fromJson(json)!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivSize?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivFixedSize.type:
          return DivSize.divFixedSize(
            (await DivFixedSize.parse(json))!,
          );
        case DivMatchParentSize.type:
          return DivSize.divMatchParentSize(
            (await DivMatchParentSize.parse(json))!,
          );
        case DivWrapContentSize.type:
          return DivSize.divWrapContentSize(
            (await DivWrapContentSize.parse(json))!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
