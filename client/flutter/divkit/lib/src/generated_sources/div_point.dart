// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:divkit/src/generated_sources/div_dimension.dart';

class DivPoint with EquatableMixin {
  const DivPoint({
    required this.x,
    required this.y,
  });

  final DivDimension x;

  final DivDimension y;

  @override
  List<Object?> get props => [
        x,
        y,
      ];

  DivPoint copyWith({
    DivDimension? x,
    DivDimension? y,
  }) =>
      DivPoint(
        x: x ?? this.x,
        y: y ?? this.y,
      );

  static DivPoint? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    try {
      return DivPoint(
        x: safeParseObj(
          DivDimension.fromJson(json['x']),
        )!,
        y: safeParseObj(
          DivDimension.fromJson(json['y']),
        )!,
      );
    } catch (e) {
      return null;
    }
  }
}
