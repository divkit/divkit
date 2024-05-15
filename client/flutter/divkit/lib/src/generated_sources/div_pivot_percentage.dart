// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class DivPivotPercentage with EquatableMixin {
  const DivPivotPercentage({
    required this.value,
  });

  static const type = "pivot-percentage";

  final Expression<double> value;

  @override
  List<Object?> get props => [
        value,
      ];

  static DivPivotPercentage? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivPivotPercentage(
      value: safeParseDoubleExpr(
        json['value'],
      )!,
    );
  }
}
