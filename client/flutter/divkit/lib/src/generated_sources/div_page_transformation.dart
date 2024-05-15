// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'div_page_transformation_overlap.dart';
import 'div_page_transformation_slide.dart';

class DivPageTransformation with EquatableMixin {
  const DivPageTransformation(Object value) : _value = value;

  final Object _value;

  @override
  List<Object?> get props => [_value];

  /// It may not work correctly so use [map] or [maybeMap]!
  Object get value {
    final value = _value;
    if (value is DivPageTransformationOverlap) {
      return value;
    }
    if (value is DivPageTransformationSlide) {
      return value;
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivPageTransformation");
  }

  T map<T>({
    required T Function(DivPageTransformationOverlap)
        divPageTransformationOverlap,
    required T Function(DivPageTransformationSlide) divPageTransformationSlide,
  }) {
    final value = _value;
    if (value is DivPageTransformationOverlap) {
      return divPageTransformationOverlap(value);
    }
    if (value is DivPageTransformationSlide) {
      return divPageTransformationSlide(value);
    }
    throw Exception(
        "Type ${value.runtimeType.toString()} is not generalized in DivPageTransformation");
  }

  T maybeMap<T>({
    T Function(DivPageTransformationOverlap)? divPageTransformationOverlap,
    T Function(DivPageTransformationSlide)? divPageTransformationSlide,
    required T Function() orElse,
  }) {
    final value = _value;
    if (value is DivPageTransformationOverlap &&
        divPageTransformationOverlap != null) {
      return divPageTransformationOverlap(value);
    }
    if (value is DivPageTransformationSlide &&
        divPageTransformationSlide != null) {
      return divPageTransformationSlide(value);
    }
    return orElse();
  }

  const DivPageTransformation.divPageTransformationOverlap(
    DivPageTransformationOverlap value,
  ) : _value = value;

  const DivPageTransformation.divPageTransformationSlide(
    DivPageTransformationSlide value,
  ) : _value = value;

  static DivPageTransformation? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    switch (json['type']) {
      case DivPageTransformationSlide.type:
        return DivPageTransformation(
            DivPageTransformationSlide.fromJson(json)!);
      case DivPageTransformationOverlap.type:
        return DivPageTransformation(
            DivPageTransformationOverlap.fromJson(json)!);
    }
    return null;
  }
}
