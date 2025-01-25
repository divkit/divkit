// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class UrlValue extends Resolvable with EquatableMixin {
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
        value: safeParseUriExpr(json['value'])!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  UrlValue resolve(DivVariableContext context) {
    value.resolve(context);
    return this;
  }
}
