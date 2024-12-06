// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

class UrlValue with EquatableMixin {
  const UrlValue({
    required this.value,
  });

  static const type = "url";
  final Expression<Uri> value;

  @override
  List<Object?> get props => [
        value,
      ];

  UrlValue copyWith({
    Expression<Uri>? value,
  }) =>
      UrlValue(
        value: value ?? this.value,
      );

  static UrlValue? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return UrlValue(
        value: reqVProp<Uri>(
          safeParseUriExpr(
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
