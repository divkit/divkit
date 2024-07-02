// Generated code. Do not modify.

import 'package:equatable/equatable.dart';

import 'package:divkit/src/utils/parsing_utils.dart';

class DivExtension with EquatableMixin {
  const DivExtension({
    required this.id,
    this.params,
  });

  final String id;

  final Map<String, dynamic>? params;

  @override
  List<Object?> get props => [
        id,
        params,
      ];

  DivExtension copyWith({
    String? id,
    Map<String, dynamic>? Function()? params,
  }) =>
      DivExtension(
        id: id ?? this.id,
        params: params != null ? params.call() : this.params,
      );

  static DivExtension? fromJson(Map<String, dynamic>? json) {
    if (json == null) {
      return null;
    }
    return DivExtension(
      id: safeParseStr(
        json['id']?.toString(),
      )!,
      params: safeParseMap(
        json,
      ),
    );
  }
}
