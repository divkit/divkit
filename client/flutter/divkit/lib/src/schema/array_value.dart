// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class ArrayValue extends Preloadable with EquatableMixin {
  const ArrayValue({
    required this.value,
  });

  static const type = "array";

  final Expression<List<dynamic>> value;

  @override
  List<Object?> get props => [
        value,
      ];

  ArrayValue copyWith({
    Expression<List<dynamic>>? value,
  }) =>
      ArrayValue(
        value: value ?? this.value,
      );

  static ArrayValue? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return ArrayValue(
        value: safeParseListExpr(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<ArrayValue?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return ArrayValue(
        value: (await safeParseListExprAsync(
          json['value'],
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
      await value.preload(context);
    } catch (e) {
      return;
    }
  }
}
