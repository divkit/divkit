// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class BooleanValue extends Preloadable with EquatableMixin {
  const BooleanValue({
    required this.value,
  });

  static const type = "boolean";

  final Expression<bool> value;

  @override
  List<Object?> get props => [
        value,
      ];

  BooleanValue copyWith({
    Expression<bool>? value,
  }) =>
      BooleanValue(
        value: value ?? this.value,
      );

  static BooleanValue? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return BooleanValue(
        value: safeParseBoolExpr(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<BooleanValue?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return BooleanValue(
        value: (await safeParseBoolExprAsync(
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
