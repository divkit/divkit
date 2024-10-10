// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Specifies element with provided index as scroll destination.
class IndexDestination extends Preloadable with EquatableMixin {
  const IndexDestination({
    required this.value,
  });

  static const type = "index";

  /// Index of contaner's item.
  // constraint: number >= 0
  final Expression<int> value;

  @override
  List<Object?> get props => [
        value,
      ];

  IndexDestination copyWith({
    Expression<int>? value,
  }) =>
      IndexDestination(
        value: value ?? this.value,
      );

  static IndexDestination? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return IndexDestination(
        value: safeParseIntExpr(
          json['value'],
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<IndexDestination?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return IndexDestination(
        value: (await safeParseIntExprAsync(
          json['value'],
        ))!,
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
