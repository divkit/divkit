// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import '../utils/parsing_utils.dart';

class StringValue with EquatableMixin {
  const StringValue({
    required this.value,
  });

  static const type = "string";

  final Expression<String> value;

  @override
  List<Object?> get props => [
        value,
      ];

  static StringValue? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return StringValue(
      value: safeParseStrExpr(
        json['value']?.toString(),
      )!,
    );
  }
}
