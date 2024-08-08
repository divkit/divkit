// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class DivActionArrayRemoveValue extends Preloadable with EquatableMixin {
  const DivActionArrayRemoveValue({
    required this.index,
    required this.variableName,
  });

  static const type = "array_remove_value";

  final Expression<int> index;

  final Expression<String> variableName;

  @override
  List<Object?> get props => [
        index,
        variableName,
      ];

  DivActionArrayRemoveValue copyWith({
    Expression<int>? index,
    Expression<String>? variableName,
  }) =>
      DivActionArrayRemoveValue(
        index: index ?? this.index,
        variableName: variableName ?? this.variableName,
      );

  static DivActionArrayRemoveValue? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionArrayRemoveValue(
        index: safeParseIntExpr(
          json['index'],
        )!,
        variableName: safeParseStrExpr(
          json['variable_name']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivActionArrayRemoveValue?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivActionArrayRemoveValue(
        index: (await safeParseIntExprAsync(
          json['index'],
        ))!,
        variableName: (await safeParseStrExprAsync(
          json['variable_name']?.toString(),
        ))!,
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
      await index.preload(context);
      await variableName.preload(context);
    } catch (e) {
      return;
    }
  }
}
