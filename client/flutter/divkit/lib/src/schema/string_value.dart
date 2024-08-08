// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class StringValue extends Preloadable with EquatableMixin {
  const StringValue({
    required this.value,
  });

  static const type = "string";

  final Expression<String> value;

  @override
  List<Object?> get props => [
        value,
      ];

  StringValue copyWith({
    Expression<String>? value,
  }) =>
      StringValue(
        value: value ?? this.value,
      );

  static StringValue? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return StringValue(
        value: safeParseStrExpr(
          json['value']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<StringValue?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return StringValue(
        value: (await safeParseStrExprAsync(
          json['value']?.toString(),
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
