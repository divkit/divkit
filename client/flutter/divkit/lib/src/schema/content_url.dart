// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

class ContentUrl with EquatableMixin {
  const ContentUrl({
    required this.value,
  });

  static const type = "url";
  final Expression<Uri> value;

  @override
  List<Object?> get props => [
        value,
      ];

  ContentUrl copyWith({
    Expression<Uri>? value,
  }) =>
      ContentUrl(
        value: value ?? this.value,
      );

  static ContentUrl? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return ContentUrl(
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
