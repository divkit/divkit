// Generated code. Do not modify.

import 'package:divkit/src/schema/div_action_scroll_destination.dart';
import 'package:divkit/src/utils/parsing.dart';
import 'package:equatable/equatable.dart';

/// Scrolls to a position or switches to the container element specified by the `destination` parameter.
class DivActionScrollTo extends Resolvable with EquatableMixin {
  const DivActionScrollTo({
    this.animated = const ValueExpression(true),
    required this.destination,
    required this.id,
  });

  static const type = "scroll_to";

  /// Enables scrolling animation.
  // default value: true
  final Expression<bool> animated;

  /// Defines the scrolling end position:
  /// • `index`: Scroll to the element with the index provided in `value`
  /// • `offset`: Scroll to the position specified in `value` and measured in `dp` from the start of the container. Applies only in `gallery`;
  /// • `start`: Scroll to the container start;
  /// • `end`: Scroll to the container end.
  final DivActionScrollDestination destination;

  /// ID of the element where the action should be performed.
  final Expression<String> id;

  @override
  List<Object?> get props => [
        animated,
        destination,
        id,
      ];

  DivActionScrollTo copyWith({
    Expression<bool>? animated,
    DivActionScrollDestination? destination,
    Expression<String>? id,
  }) =>
      DivActionScrollTo(
        animated: animated ?? this.animated,
        destination: destination ?? this.destination,
        id: id ?? this.id,
      );

  static DivActionScrollTo? fromJson(
    Map<String, dynamic>? json,
  ) {
    if (json == null) {
      return null;
    }
    try {
      return DivActionScrollTo(
        animated: reqVProp<bool>(
          safeParseBoolExpr(
            json['animated'],
            fallback: true,
          ),
          name: 'animated',
        ),
        destination: reqProp<DivActionScrollDestination>(
          safeParseObject(
            json['destination'],
            parse: DivActionScrollDestination.fromJson,
          ),
          name: 'destination',
        ),
        id: reqVProp<String>(
          safeParseStrExpr(
            json['id'],
          ),
          name: 'id',
        ),
      );
    } catch (e, st) {
      logger.warning("Parsing error", error: e, stackTrace: st);
      return null;
    }
  }

  @override
  DivActionScrollTo resolve(DivVariableContext context) {
    animated.resolve(context);
    destination.resolve(context);
    id.resolve(context);
    return this;
  }
}
