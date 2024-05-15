// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class DivSolidBackground with EquatableMixin {
  const DivSolidBackground({
    required this.color,
  });

  static const type = "solid";

  final Expression<Color> color;

  @override
  List<Object?> get props => [
        color,
      ];

  static DivSolidBackground? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivSolidBackground(
      color: safeParseColorExpr(
        json['color'],
      )!,
    );
  }
}
