// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

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

  static UrlValue? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return UrlValue(
      value: safeParseUriExpr(json['value'])!,
    );
  }
}
