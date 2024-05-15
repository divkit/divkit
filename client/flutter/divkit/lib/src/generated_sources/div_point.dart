// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';
import 'div_dimension.dart';

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

  static DivPoint? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivPoint(
      x: safeParseObj(
        DivDimension.fromJson(json['x']),
      )!,
      y: safeParseObj(
        DivDimension.fromJson(json['y']),
      )!,
    );
  }
}
