// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

class ContentText with EquatableMixin {
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
