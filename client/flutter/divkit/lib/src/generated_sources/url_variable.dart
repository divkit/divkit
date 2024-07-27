// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class UrlVariable with EquatableMixin {
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

  static UrlVariable? fromJson(Map<String, dynamic>? json) {
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
}
