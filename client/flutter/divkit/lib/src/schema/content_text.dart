// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class ContentText extends Resolvable with EquatableMixin {
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
        value: safeParseStrExpr(
          json['value']?.toString(),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  ContentText resolve(DivVariableContext context) {
    value.resolve(context);
    return this;
  }
}
