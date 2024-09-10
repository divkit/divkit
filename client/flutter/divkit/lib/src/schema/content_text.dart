// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class ContentText extends Preloadable with EquatableMixin {
  const ContentText({
    required this.value,
  });

  static const type = "text";
  final Expression<String> value;

  @override
  List<Object?> get props => [
        value,
      ];

  ContentText copyWith({
    Expression<String>? value,
  }) =>
      ContentText(
        value: value ?? this.value,
      );

  static ContentText? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return ContentText(
        value: safeParseStrExpr(
          json['value']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<ContentText?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return ContentText(
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
