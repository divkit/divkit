// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Variable — URL as a string.
class UrlVariable with EquatableMixin {
  const UrlVariable({
    required this.name,
    required this.value,
  });

  static const type = "url";

  /// Variable name.
  final String name;

  /// Value.
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
        name: reqProp<String>(
          safeParseStr(
            json['name'],
          ),
          name: 'name',
        ),
        value: reqProp<Uri>(
          safeParseUri(
            json['value'],
          ),
          name: 'value',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }
}
