// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class DivBlur with EquatableMixin {
  const DivBlur({
    required this.radius,
  });

  static const type = "blur";
  // constraint: number >= 0
  final Expression<int> radius;

  @override
  List<Object?> get props => [
        radius,
      ];

  DivBlur copyWith({
    Expression<int>? radius,
  }) =>
      DivBlur(
        radius: radius ?? this.radius,
      );

  static DivBlur? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivBlur(
      radius: safeParseIntExpr(
        json['radius'],
      )!,
    );
  }
}
