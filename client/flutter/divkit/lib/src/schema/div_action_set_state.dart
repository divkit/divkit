// Generated code. Do not modify.

import 'package:divkit/src/utils/parsing_utils.dart';
import 'package:equatable/equatable.dart';

/// Switches the appearance of content in `div-state`.
class DivActionSetState extends Preloadable with EquatableMixin {
  const DivActionSetState({
    required this.stateId,
    this.temporary = const ValueExpression(true),
  });

  static const type = "set_state";

  /// The path of the state inside `state` that needs to be activated. Set in `div_data_state_id/id/state_id` format. Can be hierarchical: `div_data_state_id/id_1/state_id_1/../id_n/state_id_n`. Consists of:
  /// • `div_data_state_id` - `state_id` numeric value of the `state` object in data;
  /// • `id` - `id` value of the `state` object;
  /// • `state_id` - `state_id` value of the state object in `state`.
  final Expression<String> stateId;

  /// Indicates a state change:
  /// • `true`: The change is temporary and when the element is re-created, the state will change back to the initial one (default value).
  /// • `false` - The state change is permanent.
  // default value: true
  final Expression<bool> temporary;

  @override
  List<Object?> get props => [
        stateId,
        temporary,
      ];

  DivActionSetState copyWith({
    Expression<String>? stateId,
    Expression<bool>? temporary,
  }) =>
      DivActionSetState(
        stateId: stateId ?? this.stateId,
        temporary: temporary ?? this.temporary,
      );

  static DivActionSetState? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionSetState(
        stateId: safeParseStrExpr(
          json['state_id']?.toString(),
        )!,
        temporary: safeParseBoolExpr(
          json['temporary'],
          fallback: true,
        )!,
      );
    } catch (e) {
      return null;
    }
  }

  static Future<DivActionSetState?> parse(
    Map<String, dynamic>? json,
  ) async {
    if (json == null) {
      return null;
    }
    try {
      return DivActionSetState(
        stateId: (await safeParseStrExprAsync(
          json['state_id']?.toString(),
        ))!,
        temporary: (await safeParseBoolExprAsync(
          json['temporary'],
          fallback: true,
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
      await stateId.preload(context);
      await temporary.preload(context);
    } catch (e) {
      return;
    }
  }
}
