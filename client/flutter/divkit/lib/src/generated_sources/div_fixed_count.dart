// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class DivFixedCount with EquatableMixin {
  const DivFixedCount({
    required this.value,
  });

  static const type = "fixed";
  // constraint: number >= 0
  final Expression<int> value;

  @override
  List<Object?> get props => [
        value,
      ];

  static DivFixedCount? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivFixedCount(
      value: safeParseIntExpr(
        json['value'],
      )!,
    );
  }
}
