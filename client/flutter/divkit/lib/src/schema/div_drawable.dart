// Generated code. Do not modify.

import 'package:divkit/src/schema/div_shape_drawable.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivDrawable extends Preloadable with EquatableMixin {
  final Preloadable value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivShapeDrawable) divShapeDrawable,
  }) {
    switch (_index) {
      case 0:
        return divShapeDrawable(
          value as DivShapeDrawable,
        );
    }
    throw Exception(
      "Type ${value.runtimeType.toString()} is not generalized in DivDrawable",
    );
  }

  T maybeMap<T>({
    T Function(DivShapeDrawable)? divShapeDrawable,
    required T Function() orElse,
  }) {
    switch (_index) {
      case 0:
        if (divShapeDrawable != null) {
          return divShapeDrawable(
            value as DivShapeDrawable,
          );
        }
        break;
    }
    return orElse();
  }

  const DivDrawable.divShapeDrawable(
    DivShapeDrawable obj,
  )   : value = obj,
        _index = 0;

  @override
  Future<void> preload(Map<String, dynamic> context) => value.preload(context);

  static DivDrawable? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivShapeDrawable.type:
          return DivDrawable.divShapeDrawable(
            DivShapeDrawable.fromJson(json)!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  static Future<DivDrawable?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivShapeDrawable.type:
          return DivDrawable.divShapeDrawable(
            (await DivShapeDrawable.parse(json))!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
