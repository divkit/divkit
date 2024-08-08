// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class UrlValue extends Preloadable with EquatableMixin {
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

  static Future<UrlValue?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return UrlValue(
        value: (await safeParseUriExprAsync(json['value']))!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {
    try {
      await value.preload(context);
    } catch (e) {
      return;
    }
  }
}
