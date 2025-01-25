// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Filter based on regular expressions.
class DivInputFilterRegex with EquatableMixin {
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
        pattern: reqVProp<String>(
          safeParseStrExpr(
            json['pattern'],
          ),
          name: 'pattern',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
