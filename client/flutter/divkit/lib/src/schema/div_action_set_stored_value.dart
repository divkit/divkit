// Generated code. Do not modify.

import 'package:divkit/src/schema/div_typed_value.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Temporarily saves the variable in storage.
class DivActionSetStoredValue extends Resolvable with EquatableMixin {
  const DivActionSetStoredValue({
    required this.lifetime,
    required this.name,
    required this.value,
  });

  static const type = "set_stored_value";

  /// Duration of storage in seconds.
  final Expression<int> lifetime;

  /// Name of the saved variable.
  final Expression<String> name;

  /// Saved value.
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
        lifetime: reqVProp<int>(
          safeParseIntExpr(
            json['lifetime'],
          ),
          name: 'lifetime',
        ),
        name: reqVProp<String>(
          safeParseStrExpr(
            json['name'],
          ),
          name: 'name',
        ),
        value: reqProp<DivTypedValue>(
          safeParseObject(
            json['value'],
            parse: DivTypedValue.fromJson,
          ),
          name: 'value',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivActionSetStoredValue resolve(DivVariableContext context) {
    lifetime.resolve(context);
    name.resolve(context);
    value.resolve(context);
    return this;
  }
}
