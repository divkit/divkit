// Generated code. Do not modify.

import 'package:divkit/src/schema/div_animator_base.dart';
import 'package:divkit/src/schema/div_color_animator.dart';
import 'package:divkit/src/schema/div_number_animator.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivAnimator extends Resolvable with EquatableMixin {
  final DivAnimatorBase value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivColorAnimator) divColorAnimator,
    required T Function(DivNumberAnimator) divNumberAnimator,
  }) {
    switch (_index) {
      case 0:
        return divColorAnimator(
          value as DivColorAnimator,
        );
      case 1:
        return divNumberAnimator(
          value as DivNumberAnimator,
        );
    }
    throw Exception(
      "Type ${value.runtimeType.toString()} is not generalized in DivAnimator",
    );
  }

  T maybeMap<T>({
    T Function(DivColorAnimator)? divColorAnimator,
    T Function(DivNumberAnimator)? divNumberAnimator,
    required T Function() orElse,
  }) {
    switch (_index) {
      case 0:
        if (divColorAnimator != null) {
          return divColorAnimator(
            value as DivColorAnimator,
          );
        }
        break;
      case 1:
        if (divNumberAnimator != null) {
          return divNumberAnimator(
            value as DivNumberAnimator,
          );
        }
        break;
    }
    return orElse();
  }

  const DivAnimator.divColorAnimator(
    DivColorAnimator obj,
  )   : value = obj,
        _index = 0;

  const DivAnimator.divNumberAnimator(
    DivNumberAnimator obj,
  )   : value = obj,
        _index = 1;

  bool get isDivColorAnimator => _index == 0;

  bool get isDivNumberAnimator => _index == 1;

  static DivAnimator? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivColorAnimator.type:
          return DivAnimator.divColorAnimator(
            DivColorAnimator.fromJson(json)!,
          );
        case DivNumberAnimator.type:
          return DivAnimator.divNumberAnimator(
            DivNumberAnimator.fromJson(json)!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  @override
  DivAnimator resolve(DivVariableContext context) {
    value.resolve(context);
    return this;
  }
}
