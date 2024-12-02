// Generated code. Do not modify.

import 'package:divkit/src/schema/div_shape_drawable.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

class DivDrawable extends Resolvable with EquatableMixin {
  final Resolvable value;
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

  bool get isDivShapeDrawable => _index == 0;

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
    } catch (_) {
      return null;
    }
  }

  @override
  DivDrawable resolve(DivVariableContext context) {
    value.resolve(context);
    return this;
  }
}
