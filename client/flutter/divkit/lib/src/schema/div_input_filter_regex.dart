// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Filter based on regular expressions.
class DivInputFilterRegex extends Resolvable with EquatableMixin {
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

  @override
  DivInputFilterRegex resolve(DivVariableContext context) {
    pattern.resolve(context);
    return this;
  }
}
