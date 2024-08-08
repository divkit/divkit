// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

class UrlVariable extends Preloadable with EquatableMixin {
  const UrlVariable({
    required this.name,
    required this.value,
  });

  static const type = "url";

  final String name;

  final Uri value;

  @override
  List<Object?> get props => [
        name,
        value,
      ];

  UrlVariable copyWith({
    String? name,
    Uri? value,
  }) =>
      UrlVariable(
        name: name ?? this.name,
        value: value ?? this.value,
      );

  static UrlVariable? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return UrlVariable(
        name: safeParseStr(
          json['name']?.toString(),
        )!,
        value: safeParseUri(json['value'])!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<UrlVariable?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return UrlVariable(
        name: (await safeParseStrAsync(
          json['name']?.toString(),
        ))!,
        value: (await safeParseUriAsync(json['value']))!,
      );
    } catch (e) {
      return null;
    }
  }

  @override
  Future<void> preload(
    Map<String, dynamic> context,
  ) async {
    try {} catch (e) {
      return;
    }
  }
}
