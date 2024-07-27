// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/generated_sources/div_page_transformation_overlap.dart';
import 'package:divkit/src/generated_sources/div_page_transformation_slide.dart';

class DivPageTransformation with EquatableMixin {
  final Object value;
  final int _index;

  @override
  List<Object?> get props => [value];

  T map<T>({
    required T Function(DivPageTransformationOverlap)
        divPageTransformationOverlap,
    required T Function(DivPageTransformationSlide) divPageTransformationSlide,
  }) {
    switch (_index) {
      case 0:
        return divPageTransformationOverlap(
          value as DivPageTransformationOverlap,
        );
      case 1:
        return divPageTransformationSlide(
          value as DivPageTransformationSlide,
        );
    }
    throw Exception(
      "Type ${value.runtimeType.toString()} is not generalized in DivPageTransformation",
    );
  }

  T maybeMap<T>({
    T Function(DivPageTransformationOverlap)? divPageTransformationOverlap,
    T Function(DivPageTransformationSlide)? divPageTransformationSlide,
    required T Function() orElse,
  }) {
    switch (_index) {
      case 0:
        if (divPageTransformationOverlap != null) {
          return divPageTransformationOverlap(
            value as DivPageTransformationOverlap,
          );
        }
        break;
      case 1:
        if (divPageTransformationSlide != null) {
          return divPageTransformationSlide(
            value as DivPageTransformationSlide,
          );
        }
        break;
    }
    return orElse();
  }

  const DivPageTransformation.divPageTransformationOverlap(
    DivPageTransformationOverlap obj,
  )   : value = obj,
        _index = 0;

  const DivPageTransformation.divPageTransformationSlide(
    DivPageTransformationSlide obj,
  )   : value = obj,
        _index = 1;

  static DivPageTransformation? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      switch (json['type']) {
        case DivPageTransformationOverlap.type:
          return DivPageTransformation.divPageTransformationOverlap(
            DivPageTransformationOverlap.fromJson(json)!,
          );
        case DivPageTransformationSlide.type:
          return DivPageTransformation.divPageTransformationSlide(
            DivPageTransformationSlide.fromJson(json)!,
          );
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}
