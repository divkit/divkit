// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivMatchParentSize extends Preloadable with EquatableMixin {
  const DivMatchParentSize({
    this.weight,
  });

  static const type = "match_parent";
  // constraint: number > 0
  final Expression<double>? weight;

  @override
  List<Object?> get props => [
        weight,
      ];

  DivMatchParentSize copyWith({
    Expression<double>? Function()? weight,
  }) =>
      DivMatchParentSize(
        weight: weight != null ? weight.call() : this.weight,
      );

  static DivMatchParentSize? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivMatchParentSize(
        weight: safeParseDoubleExpr(
          json['weight'],
        ),
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivMatchParentSize?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivMatchParentSize(
        weight: await safeParseDoubleExprAsync(
          json['weight'],
        ),
      );
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {
    try {
      await weight?.preload(context);
    } catch (e) {
      return;
    }
  }
}
