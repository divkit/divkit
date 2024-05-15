// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

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

  static ContentUrl? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return ContentUrl(
      value: safeParseUriExpr(json['value'])!,
    );
  }
}
