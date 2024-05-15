// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

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

  static ContentText? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return ContentText(
      value: safeParseStrExpr(
        json['value']?.toString(),
      )!,
    );
  }
}
