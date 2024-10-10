// Generated code. Do not modify.

import 'package:divkit/src/schema/div_typed_value.dart';
import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Temporarily saves variable to the persistent storage.
class DivActionSetStoredValue extends Preloadable with EquatableMixin {
  const DivActionSetStoredValue({
    required this.lifetime,
    required this.name,
    required this.value,
  });

  static const type = "set_stored_value";

  /// Storing time in seconds.
  final Expression<int> lifetime;

  /// Nave of stored variable.
  final Expression<String> name;

  /// Value to be stored.
  final DivTypedValue value;

  @override
  List<Object?> get props => [
        lifetime,
        name,
        value,
      ];

  DivActionSetStoredValue copyWith({
    Expression<int>? lifetime,
    Expression<String>? name,
    DivTypedValue? value,
  }) =>
      DivActionSetStoredValue(
        lifetime: lifetime ?? this.lifetime,
        name: name ?? this.name,
        value: value ?? this.value,
      );

  static DivActionSetStoredValue? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionSetStoredValue(
        lifetime: safeParseIntExpr(
          json['lifetime'],
        )!,
        name: safeParseStrExpr(
          json['name']?.toString(),
        )!,
        value: safeParseObj(
          DivTypedValue.fromJson(json['value']),
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivActionSetStoredValue?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivActionSetStoredValue(
        lifetime: (await safeParseIntExprAsync(
          json['lifetime'],
        ))!,
        name: (await safeParseStrExprAsync(
          json['name']?.toString(),
        ))!,
        value: (await safeParseObjAsync(
          DivTypedValue.fromJson(json['value']),
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
      await lifetime.preload(context);
      await name.preload(context);
      await value.preload(context);
    } catch (e) {
      return;
    }
  }
}
