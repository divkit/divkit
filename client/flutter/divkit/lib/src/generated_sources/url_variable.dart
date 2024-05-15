// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

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

  static UrlVariable? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return UrlVariable(
      name: safeParseStr(
        json['name']?.toString(),
      )!,
      value: safeParseUri(json['value'])!,
    );
  }
}
