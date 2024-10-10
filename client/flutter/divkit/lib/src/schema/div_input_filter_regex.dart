// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Filter based on regular expressions.
class DivInputFilterRegex extends Preloadable with EquatableMixin {
  const DivInputFilterRegex({
    required this.pattern,
  });

  static const type = "regex";

  /// Regular expression (pattern) that the entered value must match.
  final Expression<String> pattern;

  @override
  List<Object?> get props => [
        pattern,
      ];

  DivInputFilterRegex copyWith({
    Expression<String>? pattern,
  }) =>
      DivInputFilterRegex(
        pattern: pattern ?? this.pattern,
      );

  static DivInputFilterRegex? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivInputFilterRegex(
        pattern: safeParseStrExpr(
          json['pattern']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivInputFilterRegex?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivInputFilterRegex(
        pattern: (await safeParseStrExprAsync(
          json['pattern']?.toString(),
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
      await pattern.preload(context);
    } catch (e) {
      return;
    }
  }
}
