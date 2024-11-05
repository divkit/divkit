// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Extension that affects an element.
class DivExtension extends Resolvable with EquatableMixin {
  const DivExtension({
    required this.id,
    this.params,
  });

  /// Extension ID.
  final String id;

  /// Additional extension parameters.
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

  static DivExtension? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivExtension(
        id: safeParseStr(
          json['id']?.toString(),
        )!,
        params: safeParseMap(
          json['params'],
        ),
      );
    } catch (e) {
      return null;
    }
  }

  @override
  DivExtension resolve(DivVariableContext context) {
    return this;
  }
}
