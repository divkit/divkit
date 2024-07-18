// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

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
