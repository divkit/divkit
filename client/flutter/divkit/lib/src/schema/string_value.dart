// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

class StringValue with EquatableMixin {
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
        value: reqVProp<String>(
          safeParseStrExpr(
            json['value'],
          ),
          name: 'value',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
